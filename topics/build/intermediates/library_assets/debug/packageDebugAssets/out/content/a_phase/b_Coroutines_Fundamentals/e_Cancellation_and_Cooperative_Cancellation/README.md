# Cancellation and cooperative cancellation

See runnable example: [`CancellationExample.kt`](examples/CancellationExample.kt)

Cancellation is **not an edge case** in coroutines. It is a first‑class control mechanism.
If cancellation is wrong, your app leaks work, leaks memory, and lies about state.

---

## What cancellation really is

Cancellation in coroutines is:
- **Cooperative**
- **Hierarchical**
- **Exception‑based**

It is *not*:
- Forced termination
- Thread interruption
- Automatic everywhere

A coroutine must *agree* to be cancelled.

---

## Cancellation is cooperative by design

A coroutine is cancelled only when:
- It reaches a **suspension point**, or
- It **checks its cancellation status**

Example (cancellable):
```kotlin
suspend fun work() {
    delay(1000)
}
```

Example (not cancellable):
```kotlin
suspend fun work() {
    while (true) {
        doCpuWork()
    }
}
```

If you don’t cooperate, cancellation doesn’t happen.

---

## How cancellation propagates

Cancellation flows **top‑down**:

- Parent scope is cancelled
- All children receive cancellation
- Suspension points throw `CancellationException`

This is automatic — unless you break structure. Note that a `SupervisorJob` prevents `CancellationException` from cancelling its parent or siblings, allowing for isolated failure.

---

## `CancellationException`

Key facts:
- It is a **normal control signal**
- It extends `Exception`
- It should almost never be caught

This is the most common bug:
```kotlin
catch (e: Exception) {
    // swallowed
}
```

You just broke cancellation.

Correct handling:
```kotlin
catch (e: CancellationException) {
    throw e
}
```

Or don’t catch it at all.

---

## Making CPU‑bound code cancellable

Long‑running loops must cooperate manually:

```kotlin
suspend fun process() = coroutineScope {
    while (isActive) {
        step()
    }
}
```

Alternative checks:
- `yield()`
- `ensureActive()`

If your loop never suspends, cancellation never happens.

---

## Blocking calls and cancellation

Blocking APIs **ignore cancellation**:

```kotlin
withContext(Dispatchers.IO) {
    Thread.sleep(5000)
}
```

Cancellation will wait until the call returns.

Rules:
- Prefer suspend APIs
- Wrap blocking work carefully
- Never block on `Default` or `Main`

Blocking defeats cooperative cancellation.

---

## `withContext` and cancellation

`withContext`:
- Is cancellable
- Cancels its block if the parent is cancelled

But:
- The code inside must still cooperate

Switching context does not magically fix blocking.

---

## Timeouts are cancellation

Timeouts work by cancellation:

```kotlin
withTimeout(1_000) {
    doWork()
}
```

What actually happens:
- Scope is cancelled
- `CancellationException` is thrown

Catching and swallowing it breaks timeouts.

---

## `NonCancellable`

`NonCancellable` disables cancellation *temporarily*.

Use only for:
- Critical cleanup
- Resource release

Example:
```kotlin
withContext(NonCancellable) {
    closeResource()
}
```

Using this for business logic is a design failure.

---

## Cleanup with `finally`

Cancellation always triggers `finally`:

```kotlin
try {
    doWork()
} finally {
    cleanup()
}
```

This is where:
- Locks are released
- Files are closed
- State is reset

Never skip cleanup because of cancellation.

---

## Cancellation and structured concurrency

Structured concurrency guarantees:
- No orphan work
- Predictable shutdown
- Consistent cleanup

Breaking structure breaks cancellation.

They are inseparable concepts.

---

## Real‑world cancellation bugs

- Infinite loops that ignore `isActive`
- Swallowed `CancellationException`
- Blocking calls on wrong dispatcher
- Misuse of `NonCancellable`

These bugs don’t crash immediately.
They rot the system.

---

## Senior‑level rules

- Cancellation is part of the API contract
- Every suspend function must respect it
- Ignoring cancellation is a bug

If your coroutine cannot be cancelled, it is hostile.

Correct cancellation is not optional.
It’s the difference between controlled concurrency and chaos.

