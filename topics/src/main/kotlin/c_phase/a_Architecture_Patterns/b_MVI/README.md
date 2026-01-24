# MVI in Jetpack Compose — Real Implementation Guide

This document explains **how to correctly implement MVI in Jetpack Compose**, why it exists, how it differs from MVVM, and when you should (and should not) use it. This is **execution-focused**, with code you can copy into a real project.

---

## 1. What MVI Actually Is (No Marketing)

MVI is **unidirectional data flow with a single source of truth and pure state reduction**.

Core ideas:
- State is immutable
- User actions are modeled as intents
- State changes only via reducers
- UI renders state, nothing else

If your state mutates outside a reducer, **you are not doing MVI**.

---

## 2. When MVI Makes Sense

Use MVI when:
- Screens have complex interaction logic
- Many events can affect the same state
- You want deterministic behavior
- You want time‑travel / replayable logic

Avoid MVI when:
- Screens are trivial
- State is simple CRUD
- You don’t need strict guarantees

MVI adds structure — structure has a cost.

---

## 3. Core Components

MVI consists of **four explicit pieces**:

1. **Intent** — what the user (or system) wants
2. **State** — immutable snapshot of the UI
3. **Reducer** — pure function transforming state
4. **Effect** — one‑off side effects (navigation, snackbars)

---

## 4. State (Single Source of Truth)

### UI State
```kotlin
data class UserListState(
    val isLoading: Boolean = false,
    val users: List<UserItemUi> = emptyList(),
    val error: String? = null
)
```

Rules:
- Immutable
- Fully describes the screen
- No hidden flags

---

## 5. Intent (All Inputs)

```kotlin
sealed interface UserListIntent {
    data object Load : UserListIntent
    data class UserClicked(val id: String) : UserListIntent
    data object Retry : UserListIntent
}
```

Everything that can change state **must be an intent**.

---

## 6. Effects (One‑Shot Actions)

Effects are not state.

```kotlin
sealed interface UserListEffect {
    data class NavigateToDetail(val id: String) : UserListEffect
    data class ShowError(val message: String) : UserListEffect
}
```

Effects are:
- Not replayable
- Not persistent
- Emitted explicitly

---

## 7. Reducer (Pure Logic)

The reducer is a **pure function**.

```kotlin
fun reduce(
    state: UserListState,
    intent: UserListIntent
): UserListState {
    return when (intent) {
        UserListIntent.Load -> state.copy(isLoading = true)
        UserListIntent.Retry -> state.copy(isLoading = true, error = null)
        is UserListIntent.UserClicked -> state
    }
}
```

Rules:
- No coroutines
- No I/O
- No side effects
- No randomness

If the reducer is not pure, MVI collapses.

---

## 8. ViewModel (Intent Processor)

The ViewModel **connects intents, reducers, and side effects**.

```kotlin
class UserListMviViewModel(
    private val getActiveUsers: GetActiveUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state

    private val _effects = Channel<UserListEffect>()
    val effects = _effects.receiveAsFlow()

    fun process(intent: UserListIntent) {
        when (intent) {
            UserListIntent.Load,
            UserListIntent.Retry -> loadUsers(intent)
            is UserListIntent.UserClicked -> emitNavigation(intent.id)
        }

        _state.update { reduce(it, intent) }
    }

    private fun loadUsers(intent: UserListIntent) {
        viewModelScope.launch {
            runCatching { getActiveUsers() }
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            users = users.map { user ->
                                UserItemUi(user.id, user.name)
                            }
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false, error = "Failed")
                    }
                    _effects.send(UserListEffect.ShowError("Load failed"))
                }
        }
    }

    private fun emitNavigation(id: String) {
        viewModelScope.launch {
            _effects.send(UserListEffect.NavigateToDetail(id))
        }
    }
}
```

Important:
- Reducer updates **structural state**
- Side effects are emitted separately

---

## 9. Compose UI (Pure Renderer)

```kotlin
@Composable
fun UserListScreen(viewModel: UserListMviViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.process(UserListIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is UserListEffect.NavigateToDetail -> { /* navigate */ }
                is UserListEffect.ShowError -> { /* snackbar */ }
            }
        }
    }

    UserListContent(
        state = state,
        onUserClick = { id ->
            viewModel.process(UserListIntent.UserClicked(id))
        }
    )
}
```

The UI:
- Never mutates state
- Never runs business logic
- Only sends intents

---

## 10. MVVM vs MVI (Reality Check)

| Aspect | MVVM | MVI |
|-----|-----|-----|
| State ownership | ViewModel | Single state store |
| Mutability | Often incremental | Strictly immutable |
| Flow | Semi‑unidirectional | Fully unidirectional |
| Reducers | Optional | Mandatory |
| Debugging | Harder | Deterministic |

---

## 11. Common MVI Mistakes

### ❌ Reducer with side effects
### ❌ Multiple state sources
### ❌ Boolean flags for events
### ❌ Skipping intents for convenience

Each shortcut destroys the guarantees MVI gives.

---

## Final Verdict

MVI is **stricter than MVVM**.

It costs more upfront, but:
- Predictability increases
- Bugs decrease
- Testing becomes trivial

Use it **when complexity demands discipline**, not because it’s trendy.

