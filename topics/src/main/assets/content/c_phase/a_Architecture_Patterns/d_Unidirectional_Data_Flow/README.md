# Unidirectional Data Flow (UDF)

This document explains **what Unidirectional Data Flow actually is**, why it exists, how it applies to **Compose, MVVM, and MVI**, and how to implement it **correctly**. This is not theory — it’s the discipline that prevents state bugs, recomposition chaos, and unreadable logic.

---

## 1. What Unidirectional Data Flow Really Means

Unidirectional Data Flow means:

> **Data flows in one direction. Always.**

There is:
1. A single source of truth (state)
2. A single direction for state updates
3. No backchannels, shortcuts, or hidden mutations

If state can change from multiple places, you don’t have UDF.

---

## 2. Why UDF Exists (The Real Problem It Solves)

Without UDF:
- UI mutates state directly
- Side effects run implicitly
- Bugs depend on timing
- Recomposition becomes unpredictable

With UDF:
- State transitions are explicit
- Bugs are reproducible
- UI is deterministic
- Debugging becomes linear

UDF trades **discipline for predictability**.

---

## 3. The Canonical UDF Loop

```
User Action
   ↓
Intent / Event
   ↓
State Owner (ViewModel / Store)
   ↓
Business Logic (Use Case)
   ↓
New State
   ↓
UI renders state
```

There are no side exits from this loop.

---

## 4. Single Source of Truth

### Rule
There must be **exactly one owner of state** for a given screen or feature.

Examples:
- `StateFlow<UiState>` in MVVM
- Store state in MVI

### Anti-Patterns
- UI + ViewModel both holding the same state
- Multiple ViewModels mutating shared state
- Mutable singletons

If two places can mutate state, UDF is broken.

---

## 5. UDF in MVVM

MVVM can support UDF **if done correctly**.

### Flow
```
UI → ViewModel → Domain → ViewModel → UI
```

### Example
```kotlin
// UI
viewModel.onIntent(UserIntent.Refresh)
```

```kotlin
// ViewModel
fun onIntent(intent: UserIntent) {
    when (intent) {
        UserIntent.Refresh -> refresh()
    }
}
```

```kotlin
private fun refresh() {
    viewModelScope.launch {
        val data = loadData()
        _state.value = state.copy(data = data)
    }
}
```

UI never mutates state. It only emits intents.

---

## 6. UDF in MVI (Strict Form)

MVI is **UDF enforced by design**.

### Flow
```
UI → Intent → Reducer → State → UI
```

### Reducer Example
```kotlin
fun reduce(state: ScreenState, intent: ScreenIntent): ScreenState {
    return when (intent) {
        ScreenIntent.Load -> state.copy(isLoading = true)
        ScreenIntent.Success -> state.copy(isLoading = false)
    }
}
```

No state mutation is allowed outside the reducer.

---

## 7. Events vs State (Critical for UDF)

### State
- Persistent
- Replayable
- Represents *what is*

### Events
- One-shot
- Not replayable
- Represents *what happened*

### Correct Handling
- State → `StateFlow`
- Events → `Channel` / `SharedFlow`

Encoding events as state breaks UDF guarantees.

---

## 8. Compose and UDF

Compose **assumes UDF**.

### Correct Compose Role
- Render state
- Emit callbacks
- Never mutate state directly

### Example
```kotlin
@Composable
fun Screen(state: ScreenState, onAction: (Action) -> Unit) {
    Button(onClick = { onAction(Action.Submit) }) {
        Text("Submit")
    }
}
```

Compose is a function of state. Nothing more.

---

## 9. Side Effects in UDF

Side effects must be:
- Explicit
- Isolated
- Lifecycle-aware

### Correct
```kotlin
LaunchedEffect(Unit) {
    viewModel.load()
}
```

### Wrong
```kotlin
if (state.shouldLoad) {
    viewModel.load()
}
```

Implicit side effects break determinism.

---

## 10. Common UDF Violations

### ❌ UI mutating ViewModel state
### ❌ Multiple state holders
### ❌ Logic hidden in composables
### ❌ Reducers with side effects
### ❌ Two-way data binding

Every one of these reintroduces implicit state.

---

## 11. Testing Benefits

With UDF:
- State transitions are testable
- Reducers are pure
- UI tests are snapshot-based

Without UDF:
- Tests rely on timing
- Bugs are flaky
- Fixes are fragile

---

## Final Verdict

Unidirectional Data Flow is **not optional** in Compose-scale apps.

MVVM *can* implement it.
MVI *enforces* it.
Compose *expects* it.

If your app feels unpredictable, UDF is what you’re missing.

