# CompositionLocal (Jetpack Compose)

## What it is

`CompositionLocal` is a mechanism in Jetpack Compose for **implicitly passing values down the composition tree** without explicitly threading them through parameters.

It provides **ambient-style dependencies** scoped to a part of the UI tree.

```kotlin
val LocalSpacing = compositionLocalOf { 8.dp }
```

---

## Why it exists

Passing common dependencies via parameters can be:
- Verbose
- Noisy
- Hard to refactor

Examples:
- Theme values
- Density
- Layout direction
- Configuration

`CompositionLocal` solves **cross-cutting concerns**, not business state.

---

## Core rule

> **CompositionLocal is for implicit, read-only dependencies — not mutable state ownership.**

If you use it for app logic, you are designing yourself into a corner.

---

## How it works

- A `CompositionLocal` defines a key
- A provider supplies a value for a subtree
- Any composable in that subtree can read it

```kotlin
CompositionLocalProvider(
    LocalSpacing provides 16.dp
) {
    Screen()
}
```

Reads:

```kotlin
val spacing = LocalSpacing.current
```

---

## compositionLocalOf vs staticCompositionLocalOf

### compositionLocalOf

```kotlin
val LocalColor = compositionLocalOf { Color.Black }
```

- Tracked by the snapshot system
- Changes trigger recomposition
- Safer default

---

### staticCompositionLocalOf

```kotlin
val LocalAnalytics = staticCompositionLocalOf<Analytics> {
    error("No Analytics provided")
}
```

- Not tracked
- No automatic recomposition
- Faster, but dangerous if misused

Use only for values that **never change**.

---

## Providing values

### Scoped provision

```kotlin
CompositionLocalProvider(LocalSpacing provides 24.dp) {
    Content()
}
```

- Only affects this subtree
- Nested providers override parent values

---

### Default values

Default values are used when no provider is present.

Avoid defaults that hide configuration errors.

---

## Reading values

```kotlin
val spacing = LocalSpacing.current
```

Reading a `CompositionLocal`:
- Subscribes the composable
- Triggers recomposition if the value changes

---

## Real-world examples

### Built-in locals

Compose uses `CompositionLocal` extensively:
- `LocalContext`
- `LocalDensity`
- `LocalLayoutDirection`
- `LocalLifecycleOwner`

These are framework-level dependencies.

---

## What NOT to put in CompositionLocal

Do not store:
- Screen UI state
- Business logic
- ViewModels
- Mutable app state

These break unidirectional data flow.

---

## Anti-pattern example

```kotlin
val LocalUser = compositionLocalOf<User?> { null }
```

This hides state ownership and causes implicit coupling.

---

## Correct pattern

Use `CompositionLocal` for **configuration**, not **control**.

Good:
- Theme tokens
- Spacing systems
- Feature flags (read-only)

Bad:
- Authentication state
- Navigation state
- Mutable models

---

## Performance considerations

- Reading locals participates in recomposition
- Overuse increases implicit dependencies
- Harder to reason about recomposition triggers

Explicit parameters are always easier to optimize.

---

## Mental model

Think of `CompositionLocal` as:

```
Implicit parameters scoped to a UI subtree
```

If you wouldn’t hide it in a global variable, don’t hide it here.

---

## Official documentation

- https://developer.android.com/jetpack/compose/compositionlocal
- https://developer.android.com/jetpack/compose/mental-model
- https://developer.android.com/jetpack/compose/state

