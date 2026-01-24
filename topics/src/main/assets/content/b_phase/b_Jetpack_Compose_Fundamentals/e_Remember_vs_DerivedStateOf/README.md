# remember vs derivedStateOf (Jetpack Compose)

## What it is

`remember` and `derivedStateOf` are **state-related primitives** in Jetpack Compose, but they solve **different problems**.

- `remember` caches a value across recompositions
- `derivedStateOf` creates **computed state** that automatically tracks dependencies

Confusing them leads to unnecessary recompositions or stale UI.

---

## remember

### What it does

`remember` stores a value in the composition so it survives recomposition:

```kotlin
val value = remember { expensiveCalculation() }
```

- Executed only once per composition
- Re-executed only if keys change
- Does **not** observe dependencies

---

### remember with keys

```kotlin
val user = remember(userId) {
    loadUser(userId)
}
```

- Value is recreated when any key changes
- Keys define identity, not logic

---

### What remember is NOT

- It does not prevent recomposition
- It does not automatically update when inputs change
- It is not reactive by itself

Using `remember` for derived values is a common mistake.

---

## derivedStateOf

### What it does

`derivedStateOf` creates a `State<T>` whose value is **derived from other state**:

```kotlin
val enabled by remember {
    derivedStateOf { count > 0 }
}
```

- Automatically tracks state reads
- Recomputes only when dependencies change
- Triggers recomposition only when derived value changes

---

### Why it exists

Without `derivedStateOf`:
- Derived values recompute on every recomposition
- Downstream composables recompose unnecessarily

`derivedStateOf` optimizes **both computation and recomposition**.

---

## Key difference

| Aspect | remember | derivedStateOf |
|-----|---------|---------------|
| Purpose | Cache a value | Compute reactive state |
| Observes dependencies | No | Yes |
| Returns | T | State<T> |
| Triggers recomposition | No | Yes (if value changes) |

---

## Correct usage examples

### Incorrect: derived value with remember

```kotlin
val enabled = remember { count > 0 }
```

- `enabled` never updates
- Bug waiting to happen

---

### Correct: derivedStateOf

```kotlin
val enabled by remember {
    derivedStateOf { count > 0 }
}
```

Updates correctly and efficiently.

---

## Combining remember and derivedStateOf

`derivedStateOf` should almost always be wrapped in `remember`:

```kotlin
val visibleItems by remember {
    derivedStateOf {
        list.filter { it.visible }
    }
}
```

Without `remember`, a new `State` object would be created every recomposition.

---

## Performance implications

Use `derivedStateOf` when:
- Computation is non-trivial
- Derived value affects recomposition scope
- Multiple recompositions occur frequently

Do NOT use it for trivial expressions.

---

## Common mistakes

- Using `remember` instead of `derivedStateOf`
- Forgetting to wrap `derivedStateOf` in `remember`
- Using `derivedStateOf` for side effects
- Over-optimizing trivial calculations

Compose performance problems often start here.

---

## Mental model

Think in terms of **reactivity**:

```
remember → cache

derivedStateOf → reactive computation
```

If it depends on state and must update, `remember` alone is wrong.

---

## Official documentation

- https://developer.android.com/jetpack/compose/state
- https://developer.android.com/jetpack/compose/performance
- https://developer.android.com/jetpack/com