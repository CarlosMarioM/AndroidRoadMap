## Kotlin Flow Operators — Rules, Pitfalls, and Real Usage

Flow operators are not syntax sugar. They define **lifecycle, cancellation, backpressure, and correctness**. Misusing them causes lost events, wasted work, and UI bugs.

This document explains **what operators actually do**, **when to use each**, and **which ones are dangerous** in real Android apps.

---

## Operator categories (mental model)

Flow operators fall into **five real categories**:

1. Transformation
2. Combination
3. Flattening (flow-of-flows)
4. Terminal
5. Lifecycle / context

If you don’t know which category you’re using, you’re guessing.

---

## Transformation operators

### `map`

```kotlin
flow.map { it * 2 }
```

Rules:
- Pure transformation only
- No side effects
- Cheap operations

❌ Don’t do IO here

---

### `mapLatest`

```kotlin
flow.mapLatest { value ->
    heavyWork(value)
}
```

Behavior:
- Cancels previous block when new value arrives

Use when:
- Only latest result matters (search, filtering)

Danger:
- Cancellation must be safe

---

### `filter` / `filterNotNull`

```kotlin
flow.filter { it.isValid }
```

Cheap and safe. Prefer filtering early.

---

## Side-effect operators

### `onEach`

```kotlin
flow.onEach { log(it) }
```

Rules:
- Side effects only
- No state mutation
- No long work

Think "tap", not "transform".

---

### `onStart` / `onCompletion`

```kotlin
flow.onStart { emit(Loading) }
```

Used for:
- Loading states
- Cleanup

Not a replacement for try/finally.

---

## Combination operators

### `combine`

```kotlin
combine(a, b) { x, y -> x to y }
```

Rules:
- Emits when **any** source emits
- Requires initial value from all sources

Use for:
- UI state composition

---

### `zip`

```kotlin
flowA.zip(flowB) { a, b -> a + b }
```

Rules:
- Strict pairing
- Suspends if one is slower

Rarely correct for UI.

---

## Flattening operators (MOST MISUSED)

### `flatMapLatest` (default choice)

```kotlin
query.flatMapLatest { search(it) }
```

Rules:
- Cancels previous inner flow
- Only latest active

Use for:
- User-driven requests

---

### `flatMapMerge`

```kotlin
flow.flatMapMerge(concurrency = 2) { work(it) }
```

Rules:
- Runs concurrently
- Order not guaranteed

Use for:
- Parallel independent work

Danger:
- Can overload system

---

### `flatMapConcat`

```kotlin
flow.flatMapConcat { work(it) }
```

Rules:
- Sequential
- Preserves order

Use when order matters.

---

## Terminal operators

### `collect`

```kotlin
flow.collect { render(it) }
```

Rules:
- Starts the flow
- Suspends forever for hot flows

Only collect in lifecycle-aware scopes.

---

### `first`, `single`, `toList`

Dangerous if:
- Flow never completes

Never use on hot flows.

---

## Context & lifecycle operators

### `flowOn`

```kotlin
flowOn(Dispatchers.IO)
```

Rules:
- Affects upstream only
- Multiple `flowOn` allowed

Misunderstanding this causes threading bugs.

---

### `buffer` / `conflate`

See Backpressure document.

Never add blindly.

---

## Error handling operators

### `catch`

```kotlin
flow.catch { emit(Error(it)) }
```

Rules:
- Catches upstream only
- Downstream exceptions crash

---

### `retry` / `retryWhen`

```kotlin
.retryWhen { cause, attempt -> attempt < 3 }
```

Use only for:
- Transient failures

Never retry blindly.

---

## State operators

### `stateIn`

```kotlin
flow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initial)
```

Creates hot state.

Only in ViewModel or higher.

---

### `shareIn`

```kotlin
flow.shareIn(scope, SharingStarted.Lazily)
```

For events or shared streams.

---

## Common production bugs

❌ `flatMapMerge` for search
❌ `zip` for UI state
❌ Heavy work in `map`
❌ Collecting hot flows with `first()`
❌ Misplaced `flowOn`

---

## Operator selection cheat sheet

| Problem | Operator |
|------|--------|
| Search | `flatMapLatest` |
| Combine UI state | `combine` |
| Logging | `onEach` |
| Parallel work | `flatMapMerge` |
| Sequential tasks | `flatMapConcat` |
| State | `stateIn` |

---

## Senior rules

- Default to `flatMapLatest`
- Combine state, not events
- Hot flows belong in ViewModel
- Operators define cancellation
- If unsure, simplify

---

## Strong follow-ups

- Testing flow operators
- Cancellation propagation
- Performance profiling flows
- Operator misuse case studies

