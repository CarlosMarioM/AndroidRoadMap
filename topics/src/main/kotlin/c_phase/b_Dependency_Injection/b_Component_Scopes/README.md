# Component Scopes — Hilt & Dagger

This document explains **component scopes** from first principles. No shortcuts, no magic. If you misuse scopes, you get memory leaks, duplicated objects, or broken state. Period.

---

## What a scope really is

A scope:
- Binds an object’s lifetime to a **DI component**
- Guarantees **one instance per component instance**
- Does **not** mean global
- Does **not** mean cache
- Does **not** mean survives recomposition

If you don’t know *which component owns an object*, you don’t know how long it lives.

---

## Hilt component hierarchy (Android)

From longest-lived to shortest-lived:

```
SingletonComponent
 └── ActivityRetainedComponent
     └── ActivityComponent
         └── FragmentComponent
             └── ViewComponent
```

Rules:
- Child components can depend on parent bindings
- Parents **cannot** see child bindings
- Each component has its **own instance graph**

---

## @Singleton — Application lifetime

```kotlin
@Singleton
class AppConfig @Inject constructor()
```

### Lifetime
- One instance per **application process**
- Created lazily
- Destroyed when process dies

### Correct usage
- Retrofit / OkHttp
- Room database
- Global configuration
- Logging / analytics

### Wrong usage
- User session state
- UI logic
- Anything tied to navigation

Most misuse of `@Singleton` is just laziness.

---

## @ActivityRetainedScoped — survives configuration changes

```kotlin
@ActivityRetainedScoped
class UserSession @Inject constructor()
```

### Lifetime
- One instance per *logical activity*
- Survives rotation
- Destroyed when activity is actually finished

### Use cases
- ViewModel dependencies
- Screen-level state holders

### Common bug
Putting this logic in `@Singleton` and leaking it across screens.

---

## @ViewModelScoped — ViewModel lifetime

```kotlin
@ViewModelScoped
class LoadProfileUseCase @Inject constructor()
```

### Lifetime
- One instance per ViewModel instance
- Cleared when ViewModel is cleared

### Use cases
- Use cases
- Reducers
- Coordinators

### Rules
- No `Context`
- No UI references

This scope maps cleanly to MVVM and MVI.

---

## @ActivityScoped — Activity instance lifetime

```kotlin
@ActivityScoped
class PermissionCoordinator @Inject constructor()
```

### Lifetime
- One instance per Activity instance
- Recreated on rotation

### Use cases
- UI-only helpers
- Permission flows

Avoid business logic here.

---

## @FragmentScoped — Fragment instance lifetime

```kotlin
@FragmentScoped
class FragmentNavigator @Inject constructor()
```

### Lifetime
- One instance per Fragment instance
- Destroyed when fragment is removed

Rarely useful in Compose-first apps.

---

## Unscoped bindings — recreated every injection

```kotlin
class DateFormatter @Inject constructor()
```

### Behavior
- New instance every time
- No lifetime guarantees

### Rule
Only acceptable for **cheap, stateless objects**.

---

## Compose-specific reality (important)

Facts:
- Recomposition **does not** affect Hilt scopes
- Scopes are tied to **Android components**, not Composables

Correct pattern:

```kotlin
@Composable
fun Screen(
    viewModel: a_phase.c_Android_Core_Components.a_Activities.examples.MyViewModel = hiltViewModel()
) {
    // Safe: ViewModel + scoped deps are retained
}
```

Wrong pattern:

```kotlin
@Composable
fun Screen() {
    val repo = Repo() // recreated every recomposition
}
```

Compose does not manage lifetimes. DI does.

---

## Illegal scope combinations

These are architectural errors:
- `@ActivityScoped` injected into `@ViewModelScoped`
- `@Singleton` holding screen state
- Long-lived scopes holding `Context`

If this compiles but behaves badly, the problem is your design.

---

## Rule of thumb (memorize this)

- App-wide → `@Singleton`
- Screen logic → `@ViewModelScoped`
- Survive rotation → `@ActivityRetainedScoped`
- UI helpers → Activity / Fragment scoped
- Cheap helpers → unscoped

If you hesitate, your boundaries are unclear.

