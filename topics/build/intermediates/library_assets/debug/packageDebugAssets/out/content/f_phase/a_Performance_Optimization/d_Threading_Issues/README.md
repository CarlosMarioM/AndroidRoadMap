# Threading Issues in Android

This document explains **common threading problems in Android**, why they cause jank or ANRs, and how senior developers structure concurrency properly.

Threading is **not just multithreading** — it is **task confinement, proper scheduling, and respecting the main thread**.

---

## Android threads overview

- **Main (UI) thread**
  - Handles input, drawing, lifecycle callbacks
  - Any long/blocking work here → jank or ANR

- **Background threads**
  - For CPU, disk, network work
  - Can be created via `Thread`, `Executor`, `Coroutine`, `HandlerThread`

- **RenderThread**
  - GPU command submission (Android views)
  - Compose has its own **UI + render threads**

---

## Common threading issues

### 1. Blocking the main thread

- Synchronous network/disk IO
- Heavy computations
- Locks or waits

```kotlin
fun onClick() {
    val data = File("bigfile").readText() // ❌ blocks main thread
}
```

Impact: missed frames, input lag, ANRs

---

### 2. Race conditions

- Two threads updating the same mutable state without locks
- Leads to inconsistent UI or crashes

```kotlin
var counter = 0
Thread { counter++ }.start()
Thread { counter++ }.start() // ❌ race
```

---

### 3. Incorrect UI access from background thread

- Views / Compose state must be updated on main thread

```kotlin
Thread { textView.text = "Hello" }.start() // ❌
```

Correct:
```kotlin
runOnUiThread { textView.text = "Hello" }
// or with coroutines
lifecycleScope.launch { textView.text = "Hello" }
```

---

### 4. Excessive thread creation

- Creating new threads per task is expensive
- Leads to thread thrashing, GC pressure

Better: **thread pools, coroutines, or WorkManager**

---

### 5. Deadlocks

- Circular waits for locks
- Main thread deadlocks are especially fatal

```kotlin
synchronized(lockA) {
    synchronized(lockB) { ... } // potential deadlock
}
```

---

### 6. Misuse of AsyncTask / legacy APIs

- AsyncTask now deprecated
- Can post results after Activity destroyed → leaks

Use: coroutines + structured concurrency

---

## Senior-level concurrency patterns

### 1. Thread confinement

- Each thread owns its data
- Avoid sharing mutable state

```kotlin
val cache = ThreadLocal<String>()
```

### 2. Coroutines

- `Dispatchers.Main` for UI
- `Dispatchers.IO` for disk/network
- `Dispatchers.Default` for CPU work

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    val data = repository.load()
    withContext(Dispatchers.Main) {
        updateUI(data)
    }
}
```

### 3. Structured concurrency

- Cancel child jobs when parent is cancelled
- Avoid orphaned background work

```kotlin
val job = CoroutineScope(Dispatchers.Main).launch {
    val result = async(Dispatchers.IO) { fetch() }.await()
}
job.cancel() // cancels child too
```

### 4. Thread pools / Executors

- Use for repeated background tasks
- Avoid uncontrolled thread creation

```kotlin
val executor = Executors.newFixedThreadPool(4)
executor.execute { heavyTask() }
```

---

## Compose-specific threading issues

- Avoid updating `State` from background threads directly
- Use `MutableState` only on main thread or `LaunchedEffect` / `snapshotFlow`

```kotlin
val state = remember { mutableStateOf(0) }
LaunchedEffect(Unit) {
    val data = fetchData() // IO thread inside coroutine
    state.value = data      // safe
}
```

---

## Key senior rules

- **UI thread must never block**
- **Background work must not touch UI directly**
- **Use coroutines or structured concurrency**
- **Thread creation must be controlled**
- **Always clean up background work on lifecycle end**

---

## Mental model

> Threads are pipelines, not playgrounds.

Treat the main thread as sacred; all other threads exist to feed it safely.

---

## Interview takeaway

**Threading problems = jank, ANR, race conditions.**

A senior Android dev handles them with **structured concurrency, confinement, and careful scheduling**.

