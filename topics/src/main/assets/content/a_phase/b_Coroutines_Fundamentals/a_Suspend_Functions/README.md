# `suspend` functions in Kotlin

See runnable example: [`SuspendFunctionsExample.kt`](examples/SuspendFunctionsExample.kt)

## What `suspend` really means (no myths)
A `suspend` function is **not** a background thread, **not** async by default, and **not** magic.

`suspend` means:
- The function **can pause without blocking a thread**
- The function **must be called from a coroutine or another suspend function**
- The compiler transforms it into a **state machine**

That’s it.

If you think `suspend` == multithreading, you already lost.

---

## How suspend works under the hood (important)

At compile time, a `suspend` function:
- Receives an extra hidden parameter: `Continuation<T>`
- Can return either:
  - A value
  - Or a marker meaning “I’m suspended, resume me later”

Conceptually:
```kotlin
suspend fun fetchUser(): User
```
becomes (simplified):
```kotlin
fun fetchUser(continuation: Continuation<User>): Any
```

This is why:
- Suspension is **cheap**
- Thousands of coroutines are fine
- Blocking inside `suspend` is a serious bug

---

## Suspension points

A suspend function only pauses at **suspension points**:
- `delay()`
- `await()`
- `withContext()`
- Any other suspend function

This **does not suspend**:
```kotlin
suspend fun bad() {
    Thread.sleep(1000) // blocks the thread
}
```

This **does**:
```kotlin
suspend fun good() {
    delay(1000)
}
```

Blocking inside `suspend` is worse than blocking normally — it lies to the caller.

---

## Structured concurrency (why suspend exists)

Suspend functions only make sense with **structured concurrency**:
- A coroutine has a parent
- Cancellation flows downward
- Errors propagate predictably

This is why Kotlin pushes:
```kotlin
coroutineScope { }
supervisorScope { }
```

And discourages:
- Global coroutines
- Fire-and-forget jobs
- Detached lifetimes

If a suspend function launches work that outlives its caller, it’s broken by design.

---

## `suspend` vs callbacks

Callbacks:
- Inversion of control
- Error handling is manual
- Cancellation is bolted on

Suspend:
- Sequential code
- `try/catch` works
- Cancellation is automatic

Example mental model:
```kotlin
val user = fetchUser()
val posts = fetchPosts(user.id)
```
|
This reads synchronously but executes asynchronously.

---

## `suspend` ≠ concurrency

This is a common mistake.

```kotlin
suspend fun load() {
    fetchA()
    fetchB()
}
```

This is **sequential**.

Concurrency requires:
```kotlin
coroutineScope {
    val a = async { fetchA() }
    val b = async { fetchB() }
    a.await()
    b.await()
}
```

`suspend` enables concurrency — it does not create it.

---

## Dispatchers and context switching

`suspend` does not choose a thread.

Threading comes from:
- Coroutine scope
- Dispatcher

```kotlin
withContext(Dispatchers.IO) {
    readFromDisk()
}
```

Rules:
- CPU work → `Dispatchers.Default`
- Blocking IO → `Dispatchers.IO`
- UI work → `Dispatchers.Main`

Never guess. Never rely on defaults in libraries.

---

## Cancellation behavior (critical)

Suspend functions are **cooperative**.

They cancel when:
- They hit a suspension point
- They check `isActive`

Bad suspend function:
```kotlin
suspend fun busyLoop() {
    while (true) {
        // no suspension, no cancellation
    }
}
```

Good suspend function:
```kotlin
suspend fun busyLoop() = coroutineScope {
    while (isActive) {
        delay(16)
    }
}
```

If your suspend function ignores cancellation, it’s hostile.

---

## Designing good suspend APIs

Good suspend functions:
- Do **one thing**
- Fully respect cancellation
- Don’t leak scopes
- Don’t launch hidden coroutines
- Are **main-safe by default** (handle thread switching internally, so callers don't need to worry about dispatchers)

Bad signs:
- `suspend fun` that starts its own `CoroutineScope`
- `GlobalScope.launch` inside suspend
- Hidden parallelism

A suspend function should **describe work**, not manage lifetimes.

---

## Common mistakes (real-world)

- Marking functions `suspend` “just in case”
- Blocking with `runBlocking` in production code
- Mixing callbacks and suspend inconsistently
- Swallowing `CancellationException`

Especially this:
```kotlin
catch (e: Exception) { }
```

You just broke cancellation.

---

## When NOT to use `suspend`

Do NOT use suspend when:
- The function is purely CPU and synchronous
- The function does not suspend
- You are inside low-level utilities

`suspend` is a contract. Don’t lie.

---

## Senior-level takeaway

`suspend` is about:
- **Correctness**
- **Cancellation safety**
- **Clear lifetimes**

Not speed.
Not threads.
Not magic.

If you misuse `suspend`, your code still compiles — but your architecture rots silently.

That’s why this topic separates seniors from everyone else.

