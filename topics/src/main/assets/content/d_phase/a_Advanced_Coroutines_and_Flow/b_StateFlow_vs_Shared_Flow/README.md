## StateFlow vs SharedFlow — Correct Usage, Rules, and Android Architecture

`StateFlow` vs `SharedFlow` is **not a preference debate**. Each exists to solve a *different class of problems*. Using the wrong one leads to duplicated UI events, lost signals, broken recomposition, and impossible-to-debug bugs.

This document explains **what each really is**, **their guarantees**, **how they behave under configuration changes**, and **how to use them correctly in Android apps**.

---

## The fundamental difference

> **StateFlow models state. SharedFlow models events.**

Everything else follows from this.

---

## StateFlow — definition

`StateFlow` is:
- A **hot flow**
- With **exactly one current value**
- Always replaying the **latest state**
- Conflated by design

```kotlin
val uiState = MutableStateFlow(UiState())
```

Properties:
- Always has a value
- Never completes
- Emits only when value changes

---

## StateFlow guarantees

- New collectors **immediately receive the current value**
- Collectors never miss the latest state
- Intermediate values may be skipped (conflation)

This is intentional.

---

## When StateFlow is the correct choice

Use `StateFlow` for:
- UI state
- Screen models
- Reducer outputs (MVI)
- Anything that can be **re-rendered from scratch**

Rule:
> If the UI can redraw from the latest value → StateFlow

---

## SharedFlow — definition

`SharedFlow` is:
- A **hot broadcast flow**
- Without an inherent state
- Configurable replay and buffer

```kotlin
val events = MutableSharedFlow<UiEvent>()
```

By default:
- No replay
- No buffer
- Events are lost if not collected

---

## SharedFlow guarantees

- Events are delivered **only to active collectors**
- No automatic state retention
- Replay is explicit and limited

SharedFlow models **time**, not state.

---

## When SharedFlow is the correct choice

Use `SharedFlow` for:
- One-time UI events
- Navigation commands
- Snackbars / toasts
- Side-effect triggers

Rule:
> If repeating the value is wrong → SharedFlow

---

## Side-by-side comparison

| Aspect | StateFlow | SharedFlow |
|-----|----------|------------|
| Holds state | Yes | No |
| Always has value | Yes | No |
| Replay by default | 1 | 0 |
| Conflated | Yes | Optional |
| UI recomposition | Safe | Dangerous |
| Event delivery | Wrong | Correct |

---

## Configuration change behavior

### StateFlow
- New collector after rotation receives latest state
- UI restores correctly

### SharedFlow
- No replay → event is lost
- Replay > 0 → event may re-fire (dangerous)

This is why **navigation must not be StateFlow**.

---

## Common anti-patterns

❌ Using StateFlow for navigation
❌ Using SharedFlow for UI state
❌ Setting `replay = 1` to "fix" lost events
❌ Exposing Mutable flows publicly
❌ Mixing state and events in one flow

---

## Correct ViewModel pattern

```kotlin
class a_phase.c_Android_Core_Components.a_Activities.examples.MyViewModel : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events
}
```

State and events are **separate channels**.

---

## SharedFlow configuration (advanced)

```kotlin
MutableSharedFlow<UiEvent>(
    replay = 0,
    extraBufferCapacity = 1
)
```

Use buffer only to avoid suspension, not to cache events.

---

## Why Channel is usually wrong

Channels:
- Are point-to-point
- Have lifecycle issues
- Are easier to misuse

`SharedFlow` replaces most Channel use cases safely.

---

## MVI mapping

| Concept | Flow |
|------|------|
| State | StateFlow |
| Intent | SharedFlow |
| Effect | SharedFlow |

Reducers always emit state, never events.

---

## Testing implications

- StateFlow → assert latest value
- SharedFlow → collect and assert emissions

Testing event timing matters.

---

## Senior mental model

- StateFlow = "what the screen looks like"
- SharedFlow = "something happened"

If replaying it causes bugs, it is **not state**.

---

## Strong follow-ups

- Buffering vs conflation
- Event loss handling strategies
- Flow testing patterns
- Compose recomposition + StateFlow

