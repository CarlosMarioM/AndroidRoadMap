## Cold vs Hot Flows — Mental Model, Rules, and Android Usage

Cold vs hot flows is **not a syntax topic**. It is about **when work starts, who owns state, and how many consumers exist**. Misunderstanding this leads to duplicated network calls, memory leaks, lost events, and broken UI state.

This document explains *what cold and hot flows really are*, *how Kotlin Flow behaves*, *how StateFlow and SharedFlow differ*, and *how to use each correctly in Android architecture*.

---

## The core question

> **Who starts the work, and who owns the data?**

Everything about cold vs hot flows reduces to this.

---

## Cold flows — definition

A **cold flow**:
- Does nothing until collected
- Starts from scratch for each collector
- Has no memory of past emissions

Each collector gets **its own execution**.

---

## Cold flow example

```kotlin
fun fetchUsers(): Flow<List<User>> = flow {
    emit(api.users())
}
```

Behavior:
- No network call until collected
- Two collectors = two API calls
- Cancelling collector cancels work

This is **lazy and repeatable**.

---

## When cold flows are correct

Use cold flows for:
- One-shot operations
- Data transformations
- Repository fetches
- PagingSource flows

Rule:
> If work should restart per observer → cold

---

## Hot flows — definition

A **hot flow**:
- Exists independently of collectors
- Emits even when nobody is collecting
- Can have multiple collectors
- May retain state

Work is **owned externally**.

---

## Hot flow example

```kotlin
val ticker = MutableSharedFlow<Int>()

launch {
    while (true) {
        ticker.emit(1)
        delay(1000)
    }
}
```

Collectors:
- Join late
- Miss previous emissions
- Do not restart producer

---

## StateFlow — hot + state

`StateFlow` is a **hot flow with memory**.

Properties:
- Always has a value
- Replays last value
- Conflated (only latest matters)

```kotlin
val state = MutableStateFlow(UiState())
```

Collectors:
- Immediately receive current state
- Never miss latest value

---

## SharedFlow — hot + events

`SharedFlow` is a **hot broadcast channel**.

```kotlin
val events = MutableSharedFlow<UiEvent>()
```

Configurable:
- `replay`
- `extraBufferCapacity`

Used for **events**, not state.

---

## Cold vs hot — side-by-side

| Aspect | Cold Flow | Hot Flow |
|------|---------|---------|
| Starts work | On collect | Independently |
| Multiple collectors | Restart work | Share emissions |
| Holds state | No | Maybe |
| UI-friendly | Not directly | Yes |

---

## The Android architecture rule

```
Data source → Cold
Repository → Cold
Use case → Cold
ViewModel → Hot
UI → Collects hot
```

ViewModel is the **hot boundary**.

---

## Converting cold → hot (shareIn / stateIn)

```kotlin
val usersState = fetchUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

Rules:
- Convert once
- Share intentionally

---

## shareIn vs stateIn

| Operator | Use for |
|-------|--------|
| stateIn | UI state |
| shareIn | Events / shared work |

Never expose raw cold flows to UI.

---

## Paging is cold by design

```kotlin
val pagingFlow: Flow<PagingData<a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item>>
```

Paging flows:
- Restart on new collectors
- Must be cached with `cachedIn(viewModelScope)`

```kotlin
pager.flow.cachedIn(viewModelScope)
```

---

## Common failure modes

❌ Cold flow in UI causing duplicate calls
❌ SharedFlow used for state
❌ StateFlow used for one-time events
❌ Multiple `stateIn` conversions
❌ Hot flows leaking outside ViewModel

---

## Cold vs hot and MVI

- **Intent stream** → Hot (SharedFlow)
- **Reducer output** → StateFlow
- **Side effects** → Cold flows

---

## Senior mental model

- Cold = function
- Hot = process
- StateFlow = state holder
- SharedFlow = event bus

If you don’t know who owns the work, you chose the wrong one.

---

## Strong follow-ups

- Backpressure & buffering
- Flow cancellation semantics
- combine / flatMapLatest pitfalls
- Testing hot vs cold flows

