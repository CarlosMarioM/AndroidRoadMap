# OkHttp Internals — Interceptors, Caching, Retry Strategies (Senior Guide)

OkHttp is the **actual networking engine** in most Android apps.

Retrofit only prepares requests.
OkHttp:
- Opens sockets
- Manages threads
- Handles TLS
- Pools connections
- Applies interceptors
- Caches responses
- Retries requests

If you don’t understand OkHttp, you don’t really understand networking on Android.

---

## 1. OkHttp request lifecycle (mental model)

Every request follows this pipeline:

```
Request → Interceptors → Network → Response → Interceptors → Caller
```

Important:
- Interceptors can **modify, short‑circuit, or retry** requests
- Caching happens **inside this pipeline**

---

## 2. Interceptors (the real power)

Interceptors are **middleware**.

They can:
- Modify requests
- Inspect responses
- Retry or fail requests
- Serve cached responses

### Types of interceptors

| Type | Runs | Purpose |
|----|-----|--------|
| Application | Before cache & network | App logic |
| Network | Just before network | Low-level control |

```kotlin
OkHttpClient.Builder()
    .addInterceptor(appInterceptor)
    .addNetworkInterceptor(networkInterceptor)
```

---

## 3. Application interceptors

### Characteristics
- Run once per request
- Can short-circuit network
- See **cached and network responses**

### Typical use cases
- Auth headers
- Logging
- Request rewriting
- Retry logic

### Example: Authorization header

```kotlin
class AuthInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenProvider.token()}")
            .build()

        return chain.proceed(request)
    }
}
```

Use application interceptors for **business‑level concerns**.

---

## 4. Network interceptors

### Characteristics
- Run only when hitting network
- Can observe raw bytes
- See redirects & retries

### Use cases
- Network debugging
- Response rewriting
- Custom caching headers

```kotlin
class NetworkLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        // Inspect raw response
        return response
    }
}
```

Do **not** put auth or retries here.

---

## 5. Interceptor ordering (very important)

Order is execution order:

```kotlin
.addInterceptor(A)
.addInterceptor(B)
```

Execution:
- Request: A → B
- Response: B → A

Wrong order = subtle bugs.

---

## 6. Caching internals (HTTP‑correct caching)

OkHttp cache is:
- Disk‑based
- HTTP spec compliant
- Transparent

### Setup

```kotlin
val cache = Cache(
    directory = File(context.cacheDir, "http_cache"),
    maxSize = 50L * 1024 * 1024
)

val client = OkHttpClient.Builder()
    .cache(cache)
    .build()
```

---

## 7. How OkHttp caching actually works

OkHttp respects:
- `Cache-Control`
- `ETag`
- `Last-Modified`

### Example headers

```
Cache-Control: max-age=600
ETag: "abc123"
```

Flow:
1. Cache hit → return cached
2. Stale → conditional request (If-None-Match)
3. 304 → reuse cache

If your backend disables caching → OkHttp can’t fix it.

---

## 8. Offline caching strategy

Serve cache when offline:

```kotlin
class OfflineCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (!isNetworkAvailable()) {
            request = request.newBuilder()
                .cacheControl(
                    CacheControl.Builder()
                        .onlyIfCached()
                        .maxStale(7, TimeUnit.DAYS)
                        .build()
                )
                .build()
        }
        return chain.proceed(request)
    }
}
```

This works **only if server allows caching**.

---

## 9. Retry strategies (what OkHttp does by default)

OkHttp automatically retries:
- Connection failures
- Some timeouts
- Idempotent requests

Controlled by:

```kotlin
.retryOnConnectionFailure(true)
```

This is **low‑level retry**, not business retry.

---

## 10. Custom retry strategies (advanced)

Use interceptor-based retry for:
- Auth refresh
- Temporary server errors

### Example: token refresh retry

```kotlin
class AuthRetryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            // refresh token synchronously
            val newRequest = chain.request().newBuilder()
                .header("Authorization", "Bearer newToken")
                .build()
            return chain.proceed(newRequest)
        }
        return response
    }
}
```

### Hard rule
- Retry **idempotent requests only**
- Never blindly retry POST/PUT

---

## 11. Backoff strategies

Never retry immediately in a loop.

Use:
- Exponential backoff
- Limited attempts

```kotlin
Thread.sleep(2.0.pow(attempt).toLong() * 1000)
```

Better: handle retries in repository layer, not interceptor.

---

## 12. Where retry logic belongs

| Layer | Retry responsibility |
|-----|---------------------|
| OkHttp | Connection issues |
| Interceptor | Auth refresh |
| Repository | Business retry |
| UI | Manual retry |

Mixing these causes chaos.

---

## 13. Performance & safety rules

- One OkHttpClient per app
- Reuse connection pool
- Avoid heavy logic in interceptors
- Never block interceptors

---

## 14. Common mistakes (very common)

- Retrying non-idempotent calls
- Business logic in network interceptors
- Expecting cache to work without headers
- Multiple OkHttpClient instances

---

## Final mental model

OkHttp is:
- A deterministic pipeline
- HTTP‑spec compliant
- Extremely low‑level

Interceptors are scalpels, not hammers.
Caching depends on servers.
Retry is dangerous if abused.

Understand this, and networking bugs stop being mysterious.

