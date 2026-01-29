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

## CoroutineScope and launch vs async

- `launch` returns `Job`, used for fire-and-forget work.
- `async` returns `Deferred<T>`, used for concurrent work that produces a result.
- Always prefer structured concurrency (`coroutineScope`) over `GlobalScope`.

```kotlin
coroutineScope {
    val job = launch { doWork() }       // fire-and-forget
    val deferred = async { fetchData() } // returns a result
    val result = deferred.await()       // wait for completion
}

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

### **2. Dispatchers and context nuance**

```markdown
## Dispatchers and context nuances

- `suspend` does **not** select a thread; the scope/dispatcher does.
- Use Dispatcher according to work type:
  - CPU → `Dispatchers.Default`
  - Blocking IO → `Dispatchers.IO`
  - UI → `Dispatchers.Main`
- Avoid `Dispatchers.Unconfined` unless fully understood.
- Context switching has overhead; avoid excessive tiny suspends.

```kotlin
withContext(Dispatchers.IO) { readFromDisk() }

---


### **3. Exceptions in suspend functions**

```markdown
## Exceptions in suspend functions

- `CancellationException` must propagate to allow cooperative cancellation.
- `try/catch` works for handling errors, but swallowing all exceptions breaks cancellation.
- Exceptions in `async` propagate only when `await()` is called.

```kotlin
try {
    val data = withContext(Dispatchers.IO) { fetchData() }
} catch (e: CancellationException) {
    throw e  // always propagate
} catch (e: Exception) {
    logError(e)
}


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

### **4. Composing suspend functions**

```markdown
## Composing suspend functions

- Combine multiple suspend functions using `async` + `await` or `awaitAll()`.
- Allows sequential-looking code to run concurrently.

```kotlin
coroutineScope {
    val results = listOf(
        async { fetchA() },
        async { fetchB() },
        async { fetchC() }
    ).awaitAll()
}


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

### **5. Testing suspend functions**

```markdown
## Testing suspend functions

- Use `kotlinx.coroutines.test.runTest` for coroutine testing.
- Control virtual time to test delays, timeouts, and retries.

```kotlin
@Test
fun testFetchUser() = runTest {
    val user = fetchUser()
    assertEquals("Alice", user.name)
}


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


---

### **6. Android-specific best practices**

```markdown
## Android-specific suspend guidelines

- Launch coroutines in lifecycle-aware scopes:
  - `viewModelScope` in ViewModel
  - `lifecycleScope` in Activity/Fragment
- Avoid leaking coroutines outside the scope.
- Combine `suspend` functions with Flow for reactive streams.
- Always choose the proper dispatcher:
  - UI work → `Dispatchers.Main`
  - Network/disk → `Dispatchers.IO`

```kotlin
viewModelScope.launch {
    val data = withContext(Dispatchers.IO) { repository.loadData() }
    updateUI(data)
}


---

### **7. When NOT to use `suspend`**

```markdown
## When NOT to use suspend

Do not mark a function `suspend` if:
- It is purely CPU-bound and synchronous.
- It never calls another suspend function.
- Low-level utility code where overhead is unnecessary.

`suspend` is a contract; do not lie.

## Android Suspend Functions Cheat Sheet
     ┌───────────────────────┐
     │   suspend function    │
     │ describes work, not   │
     │ lifetimes             │
     └─────────┬─────────────┘
               │
               ▼
     ┌───────────────────────┐
     │ Suspension points     │
     │ delay(), await(),     │
     │ withContext(), etc.   │
     └─────────┬─────────────┘
               │
               ▼
     ┌───────────────────────┐
     │ Dispatcher chosen by  │
     │ scope / context       │
     │ IO / Default / Main   │
     └─────────┬─────────────┘
               │
               ▼
     ┌───────────────────────┐
     │ Structured Concurrency│
     │ coroutineScope()      │
     │ supervisorScope()     │
     │ lifecycleScope()      │
     └─────────┬─────────────┘
               │
               ▼
     ┌───────────────────────┐
     │ Cancellation & Errors │
     │ isActive checks       │
     │ propagate exceptions  │
     └───────────────────────┘

**Notes:**
- `suspend` ≠ threads; it’s cooperative and lightweight.
- Always respect **scope, cancellation, and dispatcher**.
- Combine with Flow or async/await for concurrency.
- Lifecycle-aware scopes prevent leaks in Android.


## References & Further Reading

### Kotlin Coroutines
- Official Docs: [https://kotlinlang.org/docs/coroutines-overview.html](https://kotlinlang.org/docs/coroutines-overview.html)
- Coroutine Basics: [https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)
- Structured Concurrency: [https://kotlinlang.org/docs/structured-concurrency.html](https://kotlinlang.org/docs/structured-concurrency.html)
- Async & await: [https://kotlinlang.org/docs/async.html](https://kotlinlang.org/docs/async.html)

### kotlinx.coroutines GitHub
- Repository & samples: [https://github.com/Kotlin/kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)

### KotlinConf Talks
- Deep Dive into Coroutines: [https://www.youtube.com/watch?v=_hfR3NLv1n0](https://www.youtube.com/watch?v=_hfR3NLv1n0)
- Structured Concurrency: [https://www.youtube.com/watch?v=0mTJZ0Dd5iY](https://www.youtube.com/watch?v=0mTJZ0Dd5iY)

### Testing
- Coroutine Testing: [https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)

### Android-specific
- Lifecycle-aware coroutines: [https://developer.android.com/kotlin/coroutines#lifecycles](https://developer.android.com/kotlin/coroutines#lifecycles)
- ViewModel & LiveData with coroutines: [https://developer.android.com/topic/libraries/architecture/coroutines](https://developer.android.com/topic/libraries/architecture/coroutines)



