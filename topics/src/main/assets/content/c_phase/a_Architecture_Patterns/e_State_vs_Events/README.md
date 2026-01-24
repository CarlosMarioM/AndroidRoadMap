# State vs Events — Correct Modeling in Compose

This document explains **the single most common source of bugs in Compose apps**: confusing *state* with *events*. This is not a stylistic choice. Getting this wrong causes duplicated navigation, repeated snackbars, broken recomposition, and flaky tests.

---

## 1. The Core Distinction (Non‑Negotiable)

### State
Represents **what the UI currently is**.

### Events
Represent **something that happened once**.

If something should not be replayed on recomposition, configuration change, or process death — **it is not state**.

---

## 2. State — What It Is

### Properties of State
- Persistent
- Replayable
- Idempotent
- Describes the screen at any moment

### Examples of State
- Screen content
- Loading flags
- Form values
- Selected items
- Error *presence* (not actions)

### Example
```kotlin
data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null
)
```

This can be replayed safely at any time.

---

## 3. Events — What They Are

### Properties of Events
- One‑shot
- Not replayable
- Represent actions, not conditions

### Examples of Events
- Navigation
- Toast / Snackbar
- Dialog triggers
- Analytics tracking

### Example
```kotlin
sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}
```

---

## 4. The Classic Bug (Why This Matters)

### ❌ Wrong — Modeling Events as State
```kotlin
val shouldNavigate: Boolean
```

What happens:
- Screen recomposes
- Navigation runs again
- User is navigated twice

This is the root of **ghost navigation bugs**.

---

## 5. Correct Separation Pattern

### ViewModel
```kotlin
private val _state = MutableStateFlow(LoginUiState())
val state: StateFlow<LoginUiState> = _state

private val _events = Channel<LoginEvent>()
val events = _events.receiveAsFlow()
```

State and events are **different streams**.

---

## 6. Emitting State vs Emitting Events

### Updating State
```kotlin
_state.update { it.copy(isLoading = true) }
```

### Emitting Event
```kotlin
_events.send(LoginEvent.NavigateToHome)
```

They must never be mixed.

---

## 7. Consuming State vs Consuming Events in Compose

### State
```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()
```

### Events
```kotlin
LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
        when (event) {
            LoginEvent.NavigateToHome -> navigate()
            is LoginEvent.ShowError -> showSnackbar(event.message)
        }
    }
}
```

Events are collected **once**, inside a side effect.

---

## 8. Error Handling — State vs Event

### Error as State
Used when error affects layout.
```kotlin
errorMessage: String?
```

### Error as Event
Used when error triggers UI behavior.
```kotlin
ShowError("Login failed")
```

Often, **both are needed**.

---

## 9. MVI Perspective

In MVI:
- State = store snapshot
- Event (Effect) = one‑shot side effect

Reducers **never emit events** directly. Effects are explicit.

---

## 10. Common Anti‑Patterns

### ❌ Boolean flags for navigation
### ❌ Resetting state after navigation
### ❌ Single stream for state + events
### ❌ Encoding side effects in reducers

Each of these breaks determinism.

---

## 11. Testing Implications

### State Tests
- Assert state transitions
- Deterministic

### Event Tests
- Assert emissions
- One‑shot

If events are modeled as state, tests become flaky.

---

## Final Verdict

**State describes reality.**  
**Events describe history.**

Compose replays state aggressively.
Events must survive **exactly once**.

If you confuse them, bugs are guaranteed.

This separation is not optional — it is foundational.

