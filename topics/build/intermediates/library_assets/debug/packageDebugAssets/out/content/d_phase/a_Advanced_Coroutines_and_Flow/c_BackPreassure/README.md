## Backpressure — Flow, Channels, and Real Android Strategies

Backpressure is **not an edge case**. It is what happens when **producers are faster than consumers**. If you don’t handle it explicitly, Android apps drop events, freeze UIs, leak memory, or silently corrupt state.

This document explains *what backpressure really is*, *how Kotlin Flow handles it*, *where it breaks*, and *how to design Android pipelines that survive real load*.

---

## What backpressure actually means

> **Backpressure = what happens when data arrives faster than it can be processed**

Examples:
- Network emits faster than UI renders
- Sensor events overwhelm reducers
- User actions spam intents
- Paging loads faster than DB writes

Ignoring this is not an option.

---

## Push vs pull (the core model)

| Model | Who controls speed |
|----|------------------|
| Push | Producer |
| Pull | Consumer |

Kotlin `Flow` is **pull-based by default**.

---

## Why Flow usually feels safe

Flow suspends the producer when the consumer is slow:

```kotlin
flow {
    emit(1) // suspends if collector is slow
}
```

This is built-in backpressure.

But this safety **disappears** once buffering enters the picture.

---

## Where backpressure breaks

Backpressure problems appear when:
- `buffer()` is used incorrectly
- `SharedFlow` buffers events
- `Channel` is misconfigured
- Multiple operators change context

These are *opt-in footguns*.

---

## Buffering — power tool, not default

```kotlin
flow
    .buffer(64)
    .collect { render(it) }
```

Effects:
- Producer keeps running
- Consumer may lag
- Memory grows

Use buffering only when:
- Dropping data is acceptable
- Latency matters more than accuracy

---

## Conflation — skipping intermediate values

```kotlin
flow
    .conflate()
    .collect { render(it) }
```

Behavior:
- Only latest value delivered
- Intermediate values dropped

Perfect for:
- UI state updates
- Progress indicators

Never for:
- Events
- Auditing
- Financial data

---

## StateFlow = conflation by design

```kotlin
val state = MutableStateFlow(UiState())
```

StateFlow:
- Always conflated
- Backpressure-safe for UI
- Loses intermediate states

This is intentional and correct.

---

## SharedFlow and backpressure

```kotlin
MutableSharedFlow<Event>(
    replay = 0,
    extraBufferCapacity = 1
)
```

Key rules:
- No buffer → producer suspends
- Buffer → events may queue
- Overflow → drop oldest or latest

Misconfiguration = lost events.

---

## Channels — explicit backpressure control

```kotlin
Channel<Event>(capacity = Channel.RENDEZVOUS)
```

Channel capacities:
- `RENDEZVOUS` → strict backpressure
- `BUFFERED` → bounded buffer
- `UNLIMITED` → memory leak

Channels are sharp tools. Use sparingly.

---

## Backpressure strategies (decision table)

| Strategy | When to use |
|------|-----------|
| Suspend producer | Correct by default |
| Buffer | Burst tolerance |
| Conflate | State updates |
| Drop oldest | UI events |
| Drop latest | Sampling |

Choose intentionally.

---

## UI-specific rules

- UI rendering is slow
- Never buffer UI state
- Always conflate UI state
- Events must not replay

Compose + StateFlow works because of conflation.

---

## Backpressure and MVI

- Intent stream → SharedFlow (no replay)
- Reducer → StateFlow (conflated)
- Side effects → Cold flows

Reducers must be **pure and fast**.

---

## Paging and backpressure

Paging already handles backpressure:
- Load hints are demand-driven
- UI scrolling controls fetch rate

Do not buffer Paging flows.

---

## Common production failures

❌ `UNLIMITED` channels
❌ Buffering state flows
❌ Using SharedFlow for UI state
❌ Over-buffering network responses
❌ Ignoring dispatcher changes

---

## Debugging backpressure

Symptoms:
- Increasing memory usage
- UI lag without ANRs
- Missing or duplicated events

Tools:
- Logging emission rate
- Removing buffers
- Testing slow collectors

---

## Senior mental model

- Flow is safe until you make it unsafe
- Buffering trades correctness for throughput
- Conflation trades history for freshness
- Backpressure decisions are architecture decisions

---

## Strong follow-ups

- Buffering vs conflation deep dive
- Channel vs SharedFlow
- Flow cancellation semantics
- Stress testing flows

