# Custom Scopes — Dagger & Hilt

This document explains **custom scopes**: when they are justified, how to implement them correctly, and when they are a design smell. Custom scopes are powerful — and dangerous if you don’t understand component ownership.

---

## What a custom scope really is

A custom scope:
- Is **just an annotation**
- Has **no behavior by itself**
- Only works when tied to a **custom component lifetime**

Creating a scope **does not** magically create a lifecycle.

If you don’t control the component, the scope is useless.

---

## When you actually need a custom scope

Valid reasons:
- You need a lifetime **not provided by Hilt**
- You are modeling a **domain lifecycle** (session, flow, feature)
- You want multiple instances of the same graph at once

Invalid reasons:
- You want a shorter `@Singleton`
- You want to avoid recreating objects
- You don’t understand existing scopes

Most apps **do not need custom scopes**.

---

## Common real-world use cases

### 1. User session scope

Lifetime:
- From login → logout
- Longer than a screen
- Shorter than the app

### 2. Feature flow scope

Lifetime:
- Onboarding flow
- Checkout flow
- Multi-step wizard

### 3. External lifecycle ownership

- SDK-driven flows
- Background workers
- Long-running tasks

---

## Defining a custom scope

```kotlin
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class UserSessionScope
```

This does **nothing by itself**.

---

## Why Hilt makes custom scopes hard (on purpose)

Hilt:
- Generates **fixed component hierarchies**
- Does not allow arbitrary components
- Enforces Android lifetimes

This is intentional. It prevents abuse.

To use custom scopes, you must:
- Use **Dagger components directly**, or
- Model the scope **inside an existing Hilt component**

---

## Pattern 1 — Custom scope inside an existing Hilt component

Use when:
- You want session-like behavior
- You can manually control creation/destruction

### Example: User session container

```kotlin
@UserSessionScope
class SessionRepository @Inject constructor()
```

```kotlin
@Singleton
class SessionManager @Inject constructor(
    private val factory: SessionComponent.Factory
) {
    private var sessionComponent: SessionComponent? = null

    fun startSession() {
        sessionComponent = factory.create()
    }

    fun endSession() {
        sessionComponent = null
    }
}
```

Here, **you control the lifetime explicitly**.

---

## Pattern 2 — Dagger subcomponent (full control)

Use when:
- You need a real custom lifecycle
- You need multiple scoped graphs

### Define component

```kotlin
@UserSessionScope
@Subcomponent
interface SessionComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): SessionComponent
    }

    fun inject(target: SessionEntryPoint)
}
```

### Install into parent

```kotlin
@Module(subcomponents = [SessionComponent::class])
@InstallIn(SingletonComponent::class)
object SessionModule
```

Now the scope **actually works**.

---

## Injecting from a custom scope

```kotlin
class SessionEntryPoint {
    @Inject lateinit var repo: SessionRepository
}
```

This injection is only valid **while the component exists**.

---

## Compose-specific reality

Important:
- Compose does **not** create or destroy scopes
- `remember` is **not a scope**
- `rememberSaveable` is **not a DI lifetime**

Correct mental model:
- Compose = rendering
- DI = lifetime

Never confuse them.

---

## What NOT to do

These are architectural failures:
- Custom scope without owning component
- Custom scope to avoid proper state modeling
- Mixing UI state with DI scope
- Session logic in `@Singleton`

If you need many custom scopes, your boundaries are wrong.

---

## Rule of thumb

- If Hilt scopes fit → **use them**
- If you need custom scope → **prove the lifecycle**
- If you can’t explain destruction → **don’t create it**

Custom scopes are an advanced tool. Treat them like one.

