# Recomposition Rules (Jetpack Compose)

## What it is

**Recomposition** is the process where Jetpack Compose re-executes composable functions whose **observed state has changed**, in order to update the UI.

Recomposition is:
- Incremental
- Scoped
- Non-destructive

It does **not** mean rebuilding the entire UI.

---

## Why it exists

In imperative UI systems:
- Developers manually invalidate views
- UI updates are coarse and error-prone

Compose instead:
- Tracks state reads automatically
- Re-executes only affected composables
- Skips unchanged UI

This enables high performance with simple mental models.

---

## Fundamental recomposition rule

> **A composable is recomposed if and only if state it read during composition changes.**

If a composable did not read a piece of state, it will not recompose when that state changes.

---

## What triggers recomposition

Recomposition is triggered when:
- A `MutableState<T>` value changes
- A `StateFlow`, `Flow`, or `LiveData` emits (via adapters)
- A parameter value changes and is considered unstable

---

## State read tracking

Compose records:
- Which composables read which state
- At what position in the composition tree

This creates a **dependency graph** between state and UI.

State reads must occur during composition to be tracked.

---

## Parameter-based recomposition

A composable is eligible for recomposition when:
- Any of its parameters change

Whether recomposition actually happens depends on **stability**.

---

## Stability rules

Compose uses stability to decide if recomposition can be skipped.

### Stable types

A type is stable if:
- It is immutable
- Or Compose can observe all changes

Examples:
- Primitive types
- `String`
- `State<T>`
- Immutable data classes

---

### Unstable types

Unstable parameters always trigger recomposition:
- Mutable objects
- Collections like `List` without immutability guarantees
- Objects with mutable public fields

---

## Recomposition skipping

If:
- Parameters are stable
- Values are equal

Then Compose **skips recomposition** of that composable.

Skipping is a performance optimization, not a guarantee.

---

## remember and recomposition

### remember

```kotlin
val value = remember { expensiveCalculation() }
```

- Prevents recalculation on recomposition
- Does not prevent recomposition itself

---

### remember keys

```kotlin
remember(userId) { loadUser(userId) }
```

- Value is recreated when keys change
- Otherwise retained

---

## Derived state and recomposition

### derivedStateOf

```kotlin
val enabled by remember {
    derivedStateOf { count > 0 }
}
```

- Recomputes only when dependencies change
- Prevents cascading recompositions

---

## Recomposition scope

Recomposition happens at the **smallest possible scope**:
- Child composables may recompose without parents
- Parents may recompose without children

Compose uses **slot table positions** to achieve this.

---

## Recomposition is not redraw

Recomposition:
- Re-executes composables

Rendering:
- Layout
- Measure
- Draw

These phases are separate and independently optimized.

---

## Side effects and recomposition

Composable functions may run multiple times.

Therefore:
- Side effects must not run directly
- Use effect APIs (`LaunchedEffect`, `SideEffect`, etc.)

Running side effects during recomposition causes bugs.

---

## Common recomposition traps

- Creating new objects on every recomposition
- Passing lambdas without `remember`
- Using unstable collections
- Performing work in composables
- Misunderstanding recomposition vs redraw

Compose will recompose correctly — your code might not.

---

## Mental model

Think in terms of **subscriptions**:

```
Composable reads state → subscribes
State changes → recomposition scheduled
```

No read, no recompose.

---

## Official documentation

- https://developer.android.com/jetpack/compose/recomposition
- https://developer.android.com/jetpack/compose/mental-model
- https://developer.android.com/jetpack/compose/state
- https://developer.android.com/jetpack/compose/performance

