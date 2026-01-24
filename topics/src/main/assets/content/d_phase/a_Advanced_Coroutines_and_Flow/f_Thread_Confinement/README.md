# Thread Confinement in Kotlin Coroutines & Flow

> This section explains **what thread confinement actually means**, why it exists, how Kotlin Coroutines enforce (and break) it, and how to apply it correctly in Android architectures using Flow, ViewModel, and repositories.

Senior-level, no fluff.

---

## 1. What Thread Confinement Really Is

**Thread confinement** means:
> A piece of mutable state is accessed **only from a single thread or dispatcher**.

It is a **design choice**, not a language feature.

Why it exists:
- Avoid locks
- Avoid race conditions
- Keep mental models simple

Compose, coroutines, and Flow all **rely heavily** on thread confinement.

---

## 2. The Lie: "Coroutines Are Thread-Safe"

Coroutines are **NOT thread-safe**.

They are:
- Sequential by default
- Concurrent only when you make them so

```kotlin
var counter = 0

launch {
    counter++
}
launch {
    counter++
}
```

This is a **data race**.

Coroutines do not save you from bad shared state.

---

## 3. Dispatchers = Confinement Boundaries

Thread confinement in coroutines is achieved using **dispatchers**.

| Dispatcher | Confinement Meaning |
|----------|-------------------|
| Dispatchers.Main | UI thread only |
| Dispatchers.IO | Shared IO pool (NO confinement) |
| Dispatchers.Default | Shared CPU pool |
| SingleThreadContext | Hard confinement |

Only **Main** and **single-thread contexts** provide real confinement.

---

## 4. `withContext` Breaks Confinement

```kotlin
withContext(Dispatchers.IO) {
    // You just left your confinement
}
```

- Execution hops threads
- Mutable state outside must NOT be touched

```kotlin
var state = UiState()

withContext(Dispatchers.IO) {
    state = state.copy(...) // ❌ race
}
```

---

## 5. Flow and Thread Confinement

### 5.1 Flow Is Context-Preserving

```kotlin
flow {
    emit(1)
}
.map { it * 2 }
.collect { }
```

All operators run in the **collector’s context** by default.

---

### 5.2 `flowOn` Changes Upstream Context

```kotlin
flow {
    emit(loadFromDisk())
}
.flowOn(Dispatchers.IO)
.map { it.toUi() }
.collect()
```

Rules:
- `flowOn` affects **upstream only**
- Downstream stays where it is

This is the **primary confinement tool in Flow**.

---

## 6. Repository Layer: Correct Confinement

Repositories should:
- Own IO dispatchers
- Never leak threading decisions

```kotlin
class UserRepository(
    private val api: Api,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun userFlow(): Flow<User> = flow {
        emit(api.loadUser())
    }.flowOn(dispatcher)
}
```

UI never sees threads.

---

## 7. ViewModel Layer: Main Confinement

```kotlin
viewModelScope.launch {
    repository.userFlow()
        .collect { state = it }
}
```

- State is mutated on Main
- Safe for Compose
- Safe for LiveData/StateFlow

This is **intentional confinement**.

---

## 8. StateFlow and Confinement

`StateFlow` is **NOT thread-confined**.

It is:
- Thread-safe
- Lock-free

But:
- Your **state object is not**

```kotlin
_state.value = _state.value.copy(...) // must be confined
```

Best practice:
- Update `StateFlow` from **one dispatcher only** (usually Main)

---

## 9. Actors: Explicit Confinement Model

Actors process messages **sequentially**.

```kotlin
val actor = scope.actor<Int> {
    var total = 0
    for (msg in channel) {
        total += msg
    }
}
```

Actors are:
- Safe
- Predictable
- Underused

---

## 10. Compose Relies on Thread Confinement

Compose assumptions:
- State changes happen on Main
- Snapshot system expects confinement

Breaking this causes:
- Random crashes
- Recomposition bugs
- Snapshot violations

Never update Compose state off Main.

---

## 11. Common Production Mistakes

- Mutating state inside `withContext(IO)`
- Using `Dispatchers.IO` everywhere
- Updating `StateFlow` from multiple threads
- Assuming Flow handles synchronization
- Treating `flowOn` like `withContext`

---

## 12. Rules of Thumb

- Confinement beats synchronization
- Own dispatchers in lower layers
- UI state lives on Main
- `flowOn` for IO, not `withContext`
- Avoid shared mutable state

---

## 13. When You Need Synchronization Instead

Use synchronization only when:
- True shared mutable state is unavoidable
- Performance justifies complexity

Options:
- Mutex
- Atomic types
- Actors

Prefer redesign first.

---

## 14. How This Connects

Thread confinement underpins:
- Compose correctness
- StateFlow usage
- Backpressure safety
- Error handling
- MVI and UDF

If you get this wrong, everything above it is unstable.

---

> Thread confinement is boring — and that’s why it works.

