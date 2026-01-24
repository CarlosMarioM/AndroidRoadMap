## API Versioning — Strategies, Tradeoffs, And Android Client Implementation

API versioning is not a backend-only concern. On Android, **bad versioning decisions leak directly into crashes, forced updates, broken offline flows, and ugly feature flags**. This document explains *why versioning exists*, *how APIs are versioned in practice*, and *how to implement versioning safely in an Android client*.

---

## Why API versioning exists (and why ignoring it hurts)

APIs evolve. Requirements change. Fields get added, removed, or reinterpreted. Without versioning:

- Old clients crash on new responses
- Backend teams fear shipping changes
- Mobile teams are forced into "update the app" loops
- Offline caches become invalid silently

**Versioning is a contract mechanism**, not a technical detail.

---

## What versioning actually protects

| Change | Safe without versioning? |
|------|---------------------------|
| Add optional field | Yes (if nullable) |
| Add enum value | No (breaks parsing) |
| Remove field | No |
| Change field meaning | No |
| Change auth rules | No |
| Change pagination logic | No |

If the answer is "No", you need a version boundary.

---

## Common API versioning strategies

### 1. URL versioning (most common)

```
GET /api/v1/users
GET /api/v2/users
```

**Pros**
- Explicit and visible
- Easy to route on backend
- Easy to debug

**Cons**
- Endpoint explosion
- Harder to evolve gradually

**Android impact:**
- Simple Retrofit separation
- Multiple service interfaces

```kotlin
interface UsersApiV1 {
    @GET("api/v1/users")
    suspend fun users(): List<UserV1>
}

interface UsersApiV2 {
    @GET("api/v2/users")
    suspend fun users(): List<UserV2>
}
```

---

### 2. Header-based versioning (recommended for mature APIs)

```
GET /users
Accept: application/vnd.myapi.v2+json
```

**Pros**
- Clean URLs
- Better long-term evolution
- No endpoint duplication

**Cons**
- Less obvious in logs
- Requires discipline

**Android implementation (OkHttp Interceptor):**

```kotlin
class ApiVersionInterceptor(
    private val version: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/vnd.myapi.$version+json")
            .build()
        return chain.proceed(request)
    }
}
```

---

### 3. Query parameter versioning (generally discouraged)

```
GET /users?version=2
```

**Why it’s bad:**
- Easy to forget
- Easy to misuse
- Breaks caching semantics

Use only if forced.

---

### 4. Field-level evolution ("soft versioning")

No explicit version. Instead:
- Fields are optional
- Defaults are stable
- Old behavior remains valid

```json
{
  "id": "123",
  "name": "Mario",
  "nickname": "M" // added later
}
```

**Rules for Android safety:**
- Always use nullable fields
- Never rely on enum exhaustiveness
- Always ignore unknown fields

```kotlin
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val nickname: String? = null
)
```

---

## Versioning vs backward compatibility

**Backward compatible change** = old clients still work
**Versioned change** = old clients must not receive new behavior

Rule of thumb:
- If you need `if (appVersion < X)` on backend → you failed
- If client behavior depends on server interpretation → version it

---

## Where versioning logic belongs (Android)

**Never in UI**
**Never in ViewModel**

Correct placement:

```
UI
↓
ViewModel
↓
UseCase
↓
Repository  ← version awareness allowed
↓
Retrofit / OkHttp
```

---

## Handling multiple API versions in one app

### Option 1: Parallel repositories (clean, explicit)

```kotlin
interface UsersRepository {
    suspend fun users(): List<User>
}

class UsersRepositoryV1(...) : UsersRepository
class UsersRepositoryV2(...) : UsersRepository
```

Injected via DI based on feature flag or rollout.

---

### Option 2: Version adapter (preferred)

```kotlin
class UsersRepository(
    private val api: UsersApi
) {
    suspend fun users(): List<User> {
        return api.users().map { it.toDomain() }
    }
}
```

Backend switches version → domain remains stable.

---

## API versioning and error modeling

New versions often introduce:
- New error codes
- New validation rules
- New auth requirements

**Rule:**
> Version changes MUST include explicit error contracts

```json
{
  "error": "OUTDATED_CLIENT",
  "min_version": 42
}
```

Android mapping:

```kotlin
sealed interface AppError {
    object OutdatedClient : AppError
}
```

---

## Forced update vs graceful degradation

**Forced update**
- Security issues
- Legal compliance

**Graceful degradation**
- Feature removal
- UX changes

Never mix the two.

---

## Versioning anti-patterns (real-world failures)

❌ Relying on app version codes instead of API versions
❌ Silent response shape changes
❌ Breaking enum exhaustiveness
❌ Feature flags replacing versioning
❌ "We’ll just update the app"

---

## When NOT to version

- Early internal APIs
- Prototypes
- One-off tools

If you don’t have users yet, don’t overengineer.

---

## Senior takeaway

- API versioning is **contract management**
- Android clients must assume APIs will change
- Version boundaries belong below the domain layer
- If versioning leaks into UI, architecture is broken

If you want, next strong follow-ups:
- API deprecation strategies
- Feature flags vs versioning
- Schema evolution & migrations
- Contract testing between backend & Android

