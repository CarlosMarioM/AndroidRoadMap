## Authentication and Token Refresh — Architecture, Pitfalls, and Android Implementation

Authentication on mobile is **not about login screens**. It is about **long‑lived sessions, failure recovery, concurrency, and security boundaries**. Token refresh is where most Android apps quietly break.

This document explains *how authentication really works*, *where token refresh belongs*, *how to implement it safely with Retrofit + OkHttp*, and *how it interacts with error modeling, offline‑first, and API versioning*.

---

## What authentication actually is

Authentication answers two questions:
1. Who is the user?
2. Is this request allowed *right now*?

On mobile, this usually means:
- Short‑lived **access token**
- Long‑lived **refresh token**

Never confuse identity with authorization.

---

## Token types and responsibilities

| Token | Purpose | Lifetime | Storage |
|-----|--------|---------|--------|
| Access token | Authorize requests | Minutes | Memory / encrypted |
| Refresh token | Obtain new access token | Days / weeks | Encrypted only |

**Rule:**
> If a token can create a new session, treat it like a password.

---

## Where authentication logic belongs (Android)

Correct layering:

```
UI
↓
ViewModel
↓
UseCase
↓
Repository
↓
AuthManager  ← owns tokens
↓
OkHttp / Retrofit
```

UI **never** knows how tokens work.

---

## Basic authenticated request flow

1. User logs in
2. Backend returns access + refresh tokens
3. Access token attached to requests
4. Access token expires
5. Refresh token used
6. New access token stored

This must be transparent to callers.

---

## Attaching tokens to requests (Interceptor)

```kotlin
class AuthInterceptor(
    private val authManager: AuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authManager.accessToken()

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
```

Interceptors are **stateless**. Token ownership lives elsewhere.

---

## Token refresh — the real problem

### Why naive refresh fails

❌ Refresh on every 401
❌ Multiple refresh calls in parallel
❌ Infinite retry loops
❌ Refresh inside ViewModel
❌ Refresh using Retrofit interceptor

Token refresh is a **global, synchronized operation**.

---

## Correct place for token refresh: OkHttp Authenticator

OkHttp provides `Authenticator` **specifically** for this use case.

```kotlin
class TokenAuthenticator(
    private val authManager: AuthManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val newToken = authManager.refreshTokenBlocking()
            ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
```

If `authenticate()` returns null → user is logged out.

---

## AuthManager — single source of truth

```kotlin
class AuthManager(
    private val api: AuthApi,
    private val storage: TokenStorage
) {

    @Synchronized
    fun refreshTokenBlocking(): String? {
        val refresh = storage.refreshToken() ?: return null
        val response = api.refresh(refresh)
        storage.save(response.accessToken, response.refreshToken)
        return response.accessToken
    }
}
```

**Rules:**
- One refresh at a time
- Blocking is OK (Authenticator thread)
- Never expose refresh logic upward

---

## Handling refresh failures

Refresh can fail due to:
- Expired refresh token
- Revoked session
- User disabled

Backend should respond with a **terminal error**.

Android reaction:
- Clear tokens
- Emit logout event
- Navigate to login

---

## Authentication and error modeling

Map auth failures explicitly:

```kotlin
sealed interface AuthError : AppError {
    object Unauthorized : AuthError
    object SessionExpired : AuthError
}
```

Never treat auth failure as generic network error.

---

## Offline-first and authentication

Rules:
- Cached data must still be readable
- Mutations must fail fast
- Refresh attempts require network

Never block offline reads due to auth.

---

## Multiple accounts / user switching

Requirements:
- Token storage scoped per user
- Clear cache on logout
- Restart paging flows

Token = identity boundary.

---

## Security rules (non-negotiable)

- Use HTTPS only
- Store tokens encrypted (EncryptedSharedPreferences / Keystore)
- Never log tokens
- Never persist access token unencrypted
- Rotate refresh tokens

---

## Common anti-patterns

❌ Refresh inside Interceptor
❌ Refresh inside ViewModel
❌ UI retrying on 401
❌ Using app version to invalidate auth
❌ Silent logout without state reset

---

## When token refresh becomes harmful

- Very short access tokens with unstable networks
- Aggressive retry logic
- Poor backend error signaling

Sometimes shorter sessions are worse UX.

---

## Senior takeaway

- Authentication is a cross-cutting concern
- Token refresh belongs in OkHttp Authenticator
- One refresh, globally synchronized
- Auth failures are **state changes**, not errors

---

## Strong follow-up topics

- Global logout propagation
- Secure storage internals
- Certificate pinning
- Auth testing with MockWebServer
- OAuth vs custom auth flows

