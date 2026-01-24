# MVVM in Jetpack Compose — Proper Responsibilities (Fully Explained)

This document explains **how to actually implement MVVM in Jetpack Compose**, with **concrete rules, real code examples, and anti-patterns**. This is not conceptual theory — this is how you build Compose apps that scale and remain testable.

---

## 1. Core Rule (Non‑Negotiable)

> **Composable renders state and emits intents.**  
> **ViewModel owns UI state and business coordination.**  
> **Domain layer owns business rules.**

If logic flows in any other direction, the architecture is broken.

---

## 2. Model Layer (Domain + Data)

### What the Model Layer Contains
- Domain entities (pure Kotlin)
- Use cases / interactors
- Repositories (interfaces)
- Data sources (network, database, cache)

### Example — Domain Entity
```kotlin
class User(
    val id: String,
    val name: String,
    val isActive: Boolean
)
```

### Example — Repository Contract
```kotlin
interface UserRepository {
    suspend fun getUsers(): List<User>
}
```

### Example — Use Case
```kotlin
class GetActiveUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        return repository.getUsers().filter { it.isActive }
    }
}
```

### Hard Rules
- No Android imports
- No Compose
- No UI state
- No `Flow<UiState>`

If the domain depends on UI, your architecture is upside down.

---

## 3. UI State (Presentation Models)

### Why UI State Exists
UI state represents **what the screen needs to render**, not business behavior.

### Example — UI State
```kotlin
data class UserListUiState(
    val isLoading: Boolean = false,
    val users: List<UserItemUi> = emptyList(),
    val error: String? = null
)
```

### UI a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item Model
```kotlin
data class UserItemUi(
    val id: String,
    val displayName: String
)
```

### Rule
**Never expose domain entities directly to the UI.**

---

## 4. ViewModel (State Owner & Coordinator)

### Responsibilities
- Own screen state
- Handle user intents
- Coordinate use cases
- Reduce results into UI state

### ViewModel Implementation
```kotlin
class UserListViewModel(
    private val getActiveUsers: GetActiveUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserListUiState())
    val state: StateFlow<UserListUiState> = _state

    fun onIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.Load -> loadUsers()
            is UserListIntent.UserClicked -> onUserClicked(intent.id)
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            runCatching { getActiveUsers() }
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            users = users.map { user ->
                                UserItemUi(
                                    id = user.id,
                                    displayName = user.name
                                )
                            }
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false, error = "Failed to load")
                    }
                }
        }
    }

    private fun onUserClicked(id: String) {
        // Emit navigation event (see Events section)
    }
}
```

### Forbidden in ViewModel
- `Context`
- `Modifier`
- Navigation execution
- Toasts / Snackbars
- String resources

ViewModel **describes state**, it does not touch the UI.

---

## 5. Intents (User Actions)

### Why Intents
They make user actions explicit and testable.

```kotlin
sealed interface UserListIntent {
    data object Load : UserListIntent
    data class UserClicked(val id: String) : UserListIntent
}
```

UI never calls random ViewModel methods. It sends intents.

---

## 6. Events vs State

### Events Are One‑Shot
Navigation, snackbars, dialogs.

```kotlin
sealed interface UserListEvent {
    data class NavigateToDetail(val id: String) : UserListEvent
}
```

### ViewModel Event Channel
```kotlin
private val _events = Channel<UserListEvent>()
val events = _events.receiveAsFlow()
```

Never model events as booleans in state.

---

## 7. Compose UI (View)

### Responsibilities
- Collect state
- Render UI
- Emit intents
- React to events

### Screen Implementation
```kotlin
@Composable
fun UserListScreen(
    viewModel: UserListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(UserListIntent.Load)
    }

    UserListContent(
        state = state,
        onUserClick = { id ->
            viewModel.onIntent(UserListIntent.UserClicked(id))
        }
    )
}
```

### Stateless UI
```kotlin
@Composable
fun UserListContent(
    state: UserListUiState,
    onUserClick: (String) -> Unit
) {
    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(state.error)
        else -> LazyColumn {
            items(state.users) { user ->
                Text(
                    text = user.displayName,
                    modifier = Modifier.clickable {
                        onUserClick(user.id)
                    }
                )
            }
        }
    }
}
```

### Rule
Composable functions **do not decide business rules**.

---

## 8. Side Effects Rule (Critical)

### ❌ Wrong
```kotlin
if (state.users.isEmpty()) {
    viewModel.onIntent(UserListIntent.Load)
}
```

### ✅ Correct
```kotlin
LaunchedEffect(Unit) {
    viewModel.onIntent(UserListIntent.Load)
}
```

All side effects must be explicit.

---

## 9. Data Flow Summary

```
User Action
   ↓
Composable
   ↓
Intent
   ↓
ViewModel
   ↓
Use Case
   ↓
Repository
   ↓
ViewModel (reduce)
   ↓
UI State
   ↓
Composable (render)
```

---

## 10. Testing Implications

### ViewModel Tests
- No Android
- Fake repositories
- Assert state transitions

### UI Tests
- No business mocks
- Only state rendering

If this separation is respected, testing becomes trivial.

---

## Final Reality Check

MVVM in Compose is **not optional discipline** — Compose amplifies architectural mistakes.

If your UI recomposes unexpectedly, logic is misplaced.
If your ViewModel knows UI details, it is polluted.

This is the correct baseline.

