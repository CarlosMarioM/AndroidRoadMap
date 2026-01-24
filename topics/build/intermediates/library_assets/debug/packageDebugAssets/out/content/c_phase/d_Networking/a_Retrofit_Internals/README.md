# Retrofit Internals — How It Actually Works (Senior Level)

Retrofit is **not** a magic HTTP client. It is a **type‑safe API layer generator** built on top of OkHttp.

If you treat Retrofit as “just annotations”, you will misuse it.
If you understand its internals, you will:
- Debug networking issues faster
- Design better APIs
- Avoid performance and threading mistakes

This document explains **what happens from an interface call to a network response**, step by step.

---

## 1. Retrofit’s real role in the stack

Retrofit sits **between your app code and OkHttp**.

```
Your code → Retrofit → OkHttp → Network
```

Retrofit responsibilities:
- Parse annotations
- Generate implementation at runtime
- Convert data (JSON ↔ objects)
- Adapt calls (Call, suspend, Flow, Rx)

OkHttp responsibilities:
- Connection pooling
- TLS
- Caching
- Interceptors
- Actual I/O

If you blame Retrofit for network performance → you’re wrong.

---

## 2. What happens when you call an API method

Example:

```kotlin
interface UsersApi {
    @GET("users")
    suspend fun getUsers(): List<User>
}
```

### Step‑by‑step execution

1. Retrofit parses annotations **once** at startup
2. A dynamic proxy implementation is generated
3. Method call is intercepted
4. Request metadata is built
5. OkHttp Call is created
6. CallAdapter adapts execution model
7. Converter parses response
8. a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result is returned

Nothing happens at compile time.
Everything is runtime.

---

## 3. Dynamic proxy (the core trick)

Retrofit uses Java dynamic proxies.

```kotlin
Proxy.newProxyInstance(...)
```

Each interface method becomes:
- A Method object
- Mapped to a ServiceMethod

### Important implication
- Reflection cost exists **only once**
- Calls afterward are cached

This is why Retrofit startup cost exists, but per‑call cost is low.

---

## 4. ServiceMethod & RequestFactory

For each API method, Retrofit builds:

- `RequestFactory` → HTTP method, URL, headers, body
- `ServiceMethod` → how to execute and adapt

This is where annotations are processed:

```kotlin
@GET
@POST
@Path
@Query
@Body
```

Misusing annotations = malformed requests.

---

## 5. OkHttp integration

Retrofit delegates **everything** network‑related to OkHttp.

```kotlin
val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .build()
```

OkHttp handles:
- Threads
- Timeouts
- DNS
- HTTP/2
- Caching

Retrofit does **zero** I/O.

---

## 6. CallAdapters (execution model)

CallAdapters decide **how** a request is executed and returned.

Built‑in adapters:
- `Call<T>`
- `suspend fun`

Optional adapters:
- RxJava
- Flow (via custom adapter)

### Suspend functions

```kotlin
suspend fun getUsers(): List<User>
```

Internally:
- Executed on OkHttp thread pool
- Suspends coroutine
- Resumes on caller context

Retrofit does **not** switch dispatchers.

---

## 7. ConverterFactories (data transformation)

Converters transform:

```
ResponseBody → Kotlin object
Kotlin object → RequestBody
```

Common converters:
- Moshi
- Gson
- Kotlinx Serialization

Order matters:

```kotlin
Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
```

First matching converter wins.

---

## 8. Error handling internals

Retrofit distinguishes:

- Network errors → IOException
- HTTP errors → non‑2xx
- Parsing errors → JsonDataException

### Common mistake

```kotlin
suspend fun getUsers(): List<User>
```

Non‑2xx responses still throw.

Correct approach:

```kotlin
suspend fun getUsers(): Response<List<User>>
```

Then map manually.

---

## 9. Interceptors (where power lives)

Interceptors run **inside OkHttp**, not Retrofit.

Types:
- Application interceptors
- Network interceptors

Use cases:
- Auth headers
- Logging
- Retry
- Caching

```kotlin
okHttpClient.addInterceptor { chain ->
    chain.proceed(
        chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()
    )
}
```

Retrofit just passes the client.

---

## 10. Threading model (critical)

Retrofit:
- Does NOT manage threads
- Does NOT know about Dispatchers

Suspend calls:
- Execute on OkHttp threads
- Resume on caller context

If you block → it’s your fault.

---

## 11. Performance considerations

- Retrofit reflection cost → startup only
- OkHttp connection pooling → critical
- Converter efficiency → very important

Use:
- One Retrofit instance
- One OkHttpClient

Never create them per request.

---

## 12. Common mistakes (senior‑level red flags)

- Creating Retrofit per call
- Blaming Retrofit for slow APIs
- Mixing business logic into interceptors
- Ignoring HTTP error bodies
- Returning DTOs directly to UI

---

## Final mental model

Retrofit is:
- A runtime API generator
- A request/response orchestrator

It is NOT:
- A networking engine
- A threading manager
- A caching solution

Understand this, and Retrofit stops being “magic”.

