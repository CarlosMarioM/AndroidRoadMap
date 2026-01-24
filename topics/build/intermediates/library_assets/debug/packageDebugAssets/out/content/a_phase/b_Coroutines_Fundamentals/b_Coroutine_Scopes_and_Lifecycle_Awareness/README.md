# Coroutine scopes and lifecycle awareness

See runnable example: [`CoroutineScopesExample.kt`](examples/CoroutineScopesExample.kt)

This is where coroutine knowledge stops being academic and starts being architectural.
If scopes are wrong, **everything built on top is wrong** — cancellation, memory, errors, leaks.

---

## What a CoroutineScope actually is

A `CoroutineScope` is **not** a container for coroutines.
It is a **lifetime boundary**.

A scope defines:
- How long coroutines are allowed to run
- When they are cancelled
- Who owns their failures

Every coroutine must have **exactly one meaningful owner**.

If you can’t answer *“who cancels this?”*, the code is already broken.

---

## Scope = Job + Context

Under the hood:
```kotlin
CoroutineScope = CoroutineContext + Job
```

Key pieces:
- `Job` → lifecycle & cancellation
- `Dispatcher` → threading
- Other context elements → logging, tracing, etc.

Scopes without a clear `Job` hierarchy are a red flag.

---

## Structured concurrency recap (non-negotiable)

Rules:
- Child coroutines die with their parent
- Errors propagate up
- Cancellation propagates down

This is **why** scopes exist.

Violations:
- `GlobalScope`
- Detached jobs
- Manual lifecycle tracking

Once you break structure, coroutines become worse than threads.

---

## Common scopes and what they really mean

### `viewModelScope`

- Lifetime = ViewModel
- Cancelled in `onCleared()`
- Correct place for:
  - Business logic
  - Data loading
  - Long-running UI-related work

What **does not** belong here:
- Infinite background work
- App-wide processes

If it outlives the screen, it doesn’t belong.

---

### `lifecycleScope`

- Lifetime = Android lifecycle owner
- Cancelled at `DESTROYED`

Use for:
- UI-bound work
- One-off UI effects
- Observing flows tied to visibility

Bad use:
- Business logic
- Repositories

UI scopes should stay dumb.

---

### `applicationScope` (custom)

There is no official one — you create it.

Typical pattern:
```kotlin
val applicationScope = CoroutineScope(
    SupervisorJob() + Dispatchers.Default
)
```

Use for:
- App-wide background work
- Analytics
- Sync processes

This scope **must** be explicitly cancelled on app shutdown if applicable.

---

## Why `GlobalScope` is almost always wrong

`GlobalScope` means:
- No owner
- No cancellation
- No error propagation

It is equivalent to:
```kotlin
Thread { }.start()
```

Acceptable uses:
- Truly process-wide fire-and-forget work
- Very low-level framework code

99% of the time, it’s laziness.

---

## `SupervisorJob` vs `Job`

### `Job`
- Failure cancels siblings
- Strict hierarchy

### `SupervisorJob`
- Failure is isolated
- Siblings survive

Use `SupervisorJob` when:
- Children are independent
- Partial failure is acceptable

Example:
```kotlin
CoroutineScope(SupervisorJob() + Dispatchers.IO)
```

Blindly using `SupervisorJob` everywhere hides bugs.

---

## Scope creation rules (hard rules)

1. **Libraries must not create their own scopes casually**
2. **Scopes should be injected, not instantiated**
3. **UI layers own scopes, lower layers don’t**

Bad:
```kotlin
class Repository {
    private val scope = CoroutineScope(Dispatchers.IO)
}
```

Good:
```kotlin
class Repository(private val scope: CoroutineScope)
```

Lifetimes must be explicit.

---

## 9. CoroutineExceptionHandler

`CoroutineExceptionHandler` is an optional element in `CoroutineContext` that allows you to handle uncaught exceptions. It's typically used for "root" coroutines (those launched directly in a `CoroutineScope`) where you want to log errors, show a general error message, or perform cleanup without crashing the application.

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    println("Caught exception: $exception")
}

fun main() = runBlocking {
    val scope = CoroutineScope(Job() + handler)
    scope.launch {
        throw IllegalStateException("Something went wrong!")
    }.join()
    println("Execution continues after exception handled by handler.")
    scope.cancel()
}
```

**Rule:** `CoroutineExceptionHandler` only catches exceptions from coroutines that *fail without a parent* (i.e., root coroutines in a custom scope or `GlobalScope`). Exceptions in child coroutines are usually propagated up to the parent.

---

## 10. Lifecycle-aware collection (Android)

Never do this in UI:
```kotlin
scope.launch {
    flow.collect { }
}
```

Correct:
```kotlin
repeatOnLifecycle(Lifecycle.State.STARTED) {
    flow.collect { }
}
```

Why:
- Automatic cancellation
- Automatic restart
- No leaks

If you ignore lifecycle, the UI will.

---

## 11. Flow + scope ownership

Rules:
- Flow does not manage lifetime
- Collector scope does

This means:
- Repositories expose flows
- UI decides *when* to collect

Never collect flows inside repositories unless explicitly designed to.

---

## 12. Cancellation is not optional

Cancellation must:
- Be respected
- Be propagated
- Be observable

If your coroutine:
- Swallows `CancellationException`
- Runs infinite loops
- Blocks threads

It violates scope contracts.

---

## 13. Real-world failure patterns

- ViewModel survives, coroutine leaks context
- Screen rotates, old collector keeps running
- Background job continues after logout
- Errors disappear due to `SupervisorJob` misuse

All of these are **scope bugs**, not coroutine bugs.

---

## 14. Senior-level mental model

Think in lifetimes first, concurrency second.

Ask:
- Who owns this work?
- When should it stop?
- What happens if it fails?

If those answers aren’t obvious from the scope, the design is wrong.

Coroutines don’t save bad architecture.
They expose it.

