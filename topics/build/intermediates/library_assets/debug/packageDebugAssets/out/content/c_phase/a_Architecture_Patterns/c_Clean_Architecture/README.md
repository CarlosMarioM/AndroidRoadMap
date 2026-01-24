# Clean Architecture — When It Helps, When It Doesn’t

This document explains **Clean Architecture without dogma**. It focuses on **practical value**, **real trade‑offs**, and **how it actually integrates with MVVM/MVI in Android + Compose**.

Clean Architecture is a tool. Used correctly, it scales teams and codebases. Used blindly, it becomes ceremony that slows everything down.

---

## 1. What Clean Architecture Actually Is

Clean Architecture is about **dependency direction**, not folders.

Core rule:
> **High‑level policy must not depend on low‑level details.**

Translated:
- Business rules must not depend on frameworks
- UI must depend on business logic, not the other way around
- Details (DB, network, Android) are replaceable

If your architecture is defined by package names instead of dependency flow, you missed the point.

---

## 2. The Real Layers (No UML Noise)

### Domain Layer (Core)
Contains:
- Entities
- Use cases
- Business rules

Properties:
- Pure Kotlin
- No Android
- No frameworks
- No async primitives tied to UI

This is the **only mandatory layer**.

---

### Data Layer (Details)
Contains:
- Repository implementations
- Network / database code
- Mappers

Properties:
- Implements domain interfaces
- Knows about Retrofit, Room, APIs
- Disposable and replaceable

---

### Presentation Layer
Contains:
- ViewModels
- UI state
- Intents / reducers
- Compose UI

Properties:
- Depends on Domain
- Coordinates use cases
- Never contains business rules

---

## 3. Dependency Direction (This Is the a_phase.a_Kotlin_Language_Mastery.h_Operator_Overloading.examples.Point)

```
Presentation → Domain ← Data
```

- Domain depends on nothing
- Presentation depends on Domain
- Data depends on Domain

If Domain depends on anything else, Clean Architecture is broken.

---

## 4. Minimal Clean Architecture (What You Actually Need)

You do **not** need 15 modules to be “clean”.

A realistic structure:
```
- domain/
  - model
  - usecase
  - repository

- data/
  - repository
  - datasource
  - mapper

- presentation/
  - ui
  - state
  - viewmodel
```

This scales surprisingly far.

---

## 5. Example — Domain Layer

```kotlin
class GetUserProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.UserProfile {
        return repository.getProfile(userId)
    }
}
```

Rules enforced:
- No Android imports
- No UI models
- No network code

---

## 6. Example — Repository Interface

```kotlin
interface UserRepository {
    suspend fun getProfile(userId: String): a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.UserProfile
}
```

Interfaces live in **domain**, implementations live in **data**.

---

## 7. Example — Data Layer Implementation

```kotlin
class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {

    override suspend fun getProfile(userId: String): a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.UserProfile {
        return api.fetchUser(userId).toDomain()
    }
}
```

Data layer knows details. Domain never does.

---

## 8. Example — Presentation Layer

```kotlin
class UserProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileUiState())
    val state: StateFlow<UserProfileUiState> = _state

    fun load(userId: String) {
        viewModelScope.launch {
            val profile = getUserProfile(userId)
            _state.value = UserProfileUiState.from(profile)
        }
    }
}
```

Presentation depends on Domain. Clean.

---

## 9. When Clean Architecture HELPS

Clean Architecture is worth it when:
- The app is large or long‑lived
- Business rules are non‑trivial
- Multiple platforms share logic
- You expect team growth
- You need strong test isolation

In these cases, Clean Architecture **reduces entropy**.

---

## 10. When Clean Architecture HURTS

It actively hurts when:
- The app is small
- Logic is mostly CRUD
- Requirements change daily
- Team is junior or rotating
- Deadlines are tight

In these cases, it becomes **ceremony without payoff**.

---

## 11. Common Cargo‑Cult Mistakes

### ❌ One use case per function
### ❌ Mapping data → domain → UI with no logic
### ❌ 10 modules before any complexity exists
### ❌ Domain models duplicated with no reason

If your app has more mappers than logic, stop.

---

## 12. Clean Architecture + MVVM / MVI

### With MVVM
- ViewModel = presentation logic
- Use cases = domain logic
- Repositories = abstraction boundary

### With MVI
- Reducer stays in presentation
- Use cases remain pure
- Effects coordinate domain calls

Clean Architecture does not replace MVVM or MVI — it **supports them**.

---

## 13. Testability Reality

Clean Architecture works if:
- Domain tests don’t touch Android
- Data tests mock APIs
- ViewModel tests mock use cases

If everything is mocked everywhere, you over‑architected.

---

## Final Verdict

Clean Architecture is **not a goal**.

It is a **scaling strategy**.

Use it when complexity demands isolation.
Skip it when simplicity wins.

Knowing **when not to use it** is the senior skill.

