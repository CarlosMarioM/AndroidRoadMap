# Coroutine Dispatchers (`Main`, `IO`, `Default`)

See runnable example: [`DispatchersExample.kt`](examples/DispatchersExample.kt)

Dispatchers are **not performance knobs**. They are **contracts**.
Use the wrong one and your code may work today and collapse under load tomorrow.

---

## What a dispatcher actually does

A dispatcher decides:
- **Which threads** execute the coroutine
- **How work is scheduled**
- **How much parallelism is allowed**

It does *not*:
- Make code asynchronous
- Make blocking safe
- Automatically optimize anything

Dispatchers enforce intent. Nothing more.

---

## `Dispatchers.Main`

### What it is
- Single-threaded
- Backed by the UI thread
- Serialized execution

### What belongs here
- UI state updates
- View rendering
- Event handling

Example:
```kotlin
withContext(Dispatchers.Main) {
    textView.text = state.title
}
```

### What does NOT belong here
- Disk IO
- Network calls
- JSON parsing
- Loops

Blocking `Main` freezes the app. No excuses.

---

## `Dispatchers.IO`

### What it is
- Optimized for **blocking IO**
- Large thread pool (elastic)
- Threads can exceed CPU count

### Correct use cases
- Disk access
- Database queries (Room, JDBC)
- Legacy blocking APIs
- File and socket IO

Example:
```kotlin
withContext(Dispatchers.IO) {
    dao.loadUsers()
}
```

### Common mistake
Using IO for CPU work:
```kotlin
withContext(Dispatchers.IO) {
    heavyJsonParsing() // wrong
}
```

This just wastes threads.

---

## `Dispatchers.Default`

### What it is
- CPU-bound thread pool
- Size ≈ number of CPU cores
- Designed for computation

### Correct use cases
- Data transformation
- JSON parsing
- Sorting, filtering
- Diffing lists
- Business logic

Example:
```kotlin
withContext(Dispatchers.Default) {
    processData()
}
```

### Critical rule
Never block in `Default`.
Blocking starves CPU-bound work.

---

## Quick decision table

| Work type | Dispatcher |
|---------|------------|
| UI updates | `Main` |
| Blocking IO | `IO` |
| CPU-bound | `Default` |
| Unknown | Stop and find out |

Guessing is not acceptable.

---

## Dispatcher inheritance

Coroutines inherit the dispatcher from their scope:
```kotlin
viewModelScope.launch {
    // Main by default
}
```

Explicit context switch:
```kotlin
withContext(Dispatchers.IO) { }
```

Rules:
- Switch **only when needed**
- Switch **as low as possible**
- Switch **back explicitly** if required

---

## Libraries must not hardcode dispatchers

Bad library code:
```kotlin
suspend fun load() = withContext(Dispatchers.IO) { }
```

Why it’s bad:
- Removes control from caller
- Breaks tests
- Hides threading assumptions

Correct approach:
- Let the caller decide
- Or inject a dispatcher

```kotlin
class Repo(private val io: CoroutineDispatcher)
```

---

## Testing and dispatchers

Dispatchers are **not deterministic** in tests.

Correct approach:
- Inject dispatchers
- Replace with test dispatcher

If your code hardcodes `Dispatchers.IO`, it’s harder to test than it needs to be.

---

## 9. `Dispatchers.Unconfined`

### What it is
- Runs the coroutine directly in the current thread until the first suspension point.
- After suspension, it resumes in the thread that was used by the suspending function.
- It does not confine the coroutine to a specific thread pool.

### Use cases
- Primarily for **testing** where precise thread switching is not critical.
- For very specific, low-overhead tasks that should execute immediately on the calling thread and don't involve long-running computations or blocking I/O before the first suspension.

### Warning
- **Generally NOT recommended for application logic.** Its "unconfined" nature can lead to unpredictable thread execution, making debugging and reasoning about concurrency harder.
- Avoid for UI-related coroutines as it offers no guarantee of resuming on the Main thread after suspension.

---

## 10. `Main.immediate`

Advanced but important.

`Main.immediate`:
- Executes immediately if already on Main
- Avoids extra dispatch

Useful for:
- UI state machines
- Reducing frame drops

Danger:
- Reentrancy bugs

Use only when you fully understand call order.

---

## 11. Real-world dispatcher bugs

- Network on `Main` → ANRs
- CPU work on `IO` → thread explosion
- Blocking on `Default` → app-wide slowdown
- Hardcoded dispatchers → untestable code

These bugs scale with users.

---

## 12. Senior-level takeaway

Dispatchers communicate **intent**.

Choosing the right one:
- Makes code readable
- Makes performance predictable
- Makes testing possible

Choosing the wrong one:
- Doesn’t fail fast
- Degrades silently
- Blows up in production

Dispatchers don’t save bad code.
They expose it.


