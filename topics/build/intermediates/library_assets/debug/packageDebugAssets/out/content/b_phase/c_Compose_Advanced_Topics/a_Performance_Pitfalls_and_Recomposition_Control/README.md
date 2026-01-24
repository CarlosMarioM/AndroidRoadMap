# Performance Pitfalls and Recomposition Control (Jetpack Compose)

## What it is

This document covers **real performance pitfalls** in Jetpack Compose and the **mechanisms you use to control recomposition**.

Compose is fast by default — **bad patterns make it slow**.

---

## Core truth

> **Recomposition is cheap. Unnecessary recomposition is not.**

Most performance issues come from:
- Excessive recomposition scopes
- Unstable parameters
- Work done during composition

---

## Pitfall 1 — Unstable parameters

### Problem

Passing unstable objects forces recomposition:

```kotlin
@Composable
fun a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item(user: User) { }
```

If `User` is mutable, Compose must assume it changed.

---

### Fix

- Use immutable data classes
- Annotate with `@Immutable` when applicable
- Pass primitives or stable wrappers

---

## Pitfall 2 — Creating objects during recomposition

### Problem

```kotlin
val formatter = DateFormatter()
```

Runs on every recomposition.

---

### Fix

```kotlin
val formatter = remember { DateFormatter() }
```

Cache expensive objects.

---

## Pitfall 3 — Lambdas recreated every recomposition

### Problem

```kotlin
Button(onClick = { viewModel.submit(id) })
```

Lambda identity changes each time.

---

### Fix

```kotlin
val onSubmit = remember(id) { { viewModel.submit(id) } }
```

Use stable lambdas when passing deeply.

---

## Pitfall 4 — Large recomposition scopes

### Problem

```kotlin
Column {
    Header()
    Content(state)
    Footer()
}
```

State change in `Content` may recompose the whole column.

---

### Fix

Split scopes:

```kotlin
Header()
Content(state)
Footer()
```

Smaller scopes = less work.

---

## Pitfall 5 — Missing derivedStateOf

### Problem

Derived values recompute constantly:

```kotlin
val enabled = items.isNotEmpty()
```

---

### Fix

```kotlin
val enabled by remember {
    derivedStateOf { items.isNotEmpty() }
}
```

Prevents cascading recomposition.

---

## Pitfall 6 — Overusing CompositionLocal

### Problem

Implicit dependencies increase recomposition fan-out.

---

### Fix

- Prefer explicit parameters
- Use `CompositionLocal` only for configuration

---

## Recomposition control tools

### remember

- Caches values
- Does not block recomposition

---

### key

```kotlin
key(item.id) {
    a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item(item)
}
```

Controls identity in lazy lists and dynamic UI.

---

### derivedStateOf

- Reactive computed state
- Limits recomposition propagation

---

### Stable types

- `@Immutable`
- `@Stable`

Enable recomposition skipping.

---

## Lazy layouts performance

### Common mistake

Doing work inside item composables:

```kotlin
items(list) { item ->
    expensiveCalculation(item)
}
```

---

### Correct approach

- Precompute outside
- Hoist logic to ViewModel

---

## Measuring performance

### Tools

- Layout Inspector
- Compose Recomposition Counts
- Android Studio Profiler

If you’re guessing, you’re doing it wrong.

---

## When NOT to optimize

- Before correctness
- Before profiling
- For trivial recompositions

Premature optimization wastes time.

---

## Mental model

Think in terms of:

```
Small scopes
Stable inputs
Cached work
```

Compose rewards discipline.

---

## Official documentation

- https://developer.android.com/jetpack/compose/performance
- https://developer.android.com/jetpack/compose/recomposition
- https://developer.android.com/jetpack/compose/state
- https://developer.android.com/jetpack/compose/mental-model

