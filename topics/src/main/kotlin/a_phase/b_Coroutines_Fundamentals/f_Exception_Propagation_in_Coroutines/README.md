# Exception propagation in coroutines

See runnable example: [`ExceptionPropagationExample.kt`](examples/ExceptionPropagationExample.kt)

Exception handling in coroutines is **not intuitive if you come from threads or Rx**.
Most bugs here are not crashes — they are **missing crashes**.

If errors don’t propagate, systems rot silently.

---

## The core rule (memorize this)

> **Exceptions propagate up the coroutine hierarchy, not sideways.**

Everything else follows from this.

---

## Launch vs async (this matters)

### `launch`

- Fire-and-forget
- Exceptions are **immediately propagated to the parent**
- If unhandled, they crash the scope

```kotlin
scope.launch {
    error("boom")
}
```

If this crashes your app, that’s correct behavior.

---

### `async`

- Produces a value
- Exceptions are **deferred** until `await()`

```kotlin
val d = async { error("boom") }
// no crash yet

d.await() // exception thrown here
```

If you forget to `await`, the error is lost.

That is not a feature.

---

## Why `async` is dangerous

Common anti-pattern:
```kotlin
async { loadData() }
```

No `await`, no error, no cancellation — just silence.

Rule:
- Use `launch` for side effects
- Use `async` only when you **need a value**

If you don’t await, don’t async.

---

## Parent–child exception flow

Default behavior:
- Child throws exception
- Parent is cancelled
- Siblings are cancelled
- Exception bubbles up

This is **intentional fail-fast** behavior.

---

## `SupervisorJob` and `supervisorScope`

Supervision changes exception propagation.

### With supervision:
- Child failure does **not** cancel siblings
- Parent stays alive
- Exception must be handled manually

```kotlin
supervisorScope {
    launch { error("A") }
    launch { workB() }
}
```

Use only when:
- Children are independent
- Partial failure is acceptable

Using supervisors to “avoid crashes” is malpractice.

---

## `CoroutineExceptionHandler`

This is **not** try/catch.

Key facts:
- Only catches **uncaught exceptions**
- Works with `launch`
- Does NOT work with `async`

```kotlin
val handler = CoroutineExceptionHandler { _, e -> log(e) }

scope.launch(handler) {
    error("boom")
}
```

If you rely on this for business logic, your design is broken.

---

## Why try/catch sometimes doesn’t work

This fails:
```kotlin
try {
    scope.launch {
        error("boom")
    }
} catch (e: Exception) { }
```

Because:
- The exception happens in another coroutine
- Not in the calling stack

Correct handling must be **inside the coroutine**.

---

## Exception handling inside coroutines

Correct pattern:
```kotlin
scope.launch {
    try {
        work()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handle(e)
    }
}
```

Never swallow cancellation.

---

## Exception aggregation

When multiple children fail:
- One exception is primary
- Others are **suppressed**

```kotlin
e.suppressed
```

Ignoring suppressed exceptions hides real failures.

---

## Android-specific behavior

- Unhandled exception in `viewModelScope` → crashes app
- This is **correct**

Silencing crashes hides data corruption and inconsistent UI.

Fail fast beats silent failure.

---

## Common production bugs

- Using `async` without `await`
- Blanket `catch (Exception)`
- Overusing `SupervisorJob`
- Treating `CoroutineExceptionHandler` as try/catch

All of these delay crashes until users find them.

---

## Senior-level rules

- Crashes are signals, not inconveniences
- Propagation is a feature, not a bug
- Silence is the worst failure mode

If your coroutine errors don’t surface, your system is lying.

Correct exception propagation is not optional.
It’s integrity.

