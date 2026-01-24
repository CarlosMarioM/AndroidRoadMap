# Stable vs Unstable Parameters (Jetpack Compose)

## What it is

In Jetpack Compose, **parameter stability** determines whether a composable can **skip recomposition** when its inputs appear unchanged.

Compose does not blindly recompose on every call. It relies on **stability analysis** to decide whether re-executing a composable is necessary.

This is a **core performance mechanism**.

---

## Why it exists

Without stability rules:
- Every parameter change would trigger recomposition
- Compose would behave like a naive UI system

Stability allows Compose to:
- Compare parameters safely
- Skip recomposition when values are equivalent
- Minimize work without developer intervention

---

## The recomposition decision rule

A composable **may be skipped** if:
1. All parameters are **stable**
2. All parameters are **equal** to their previous values

If any parameter is **unstable**, Compose must assume it may have changed.

---

## Stable parameters

A parameter is considered **stable** if Compose can safely determine when it changes.

### Automatically stable types

- Primitive types (`Int`, `Boolean`, etc.)
- `String`
- Enums
- `State<T>`
- Lambdas with stable captures

---

### Immutable data classes

```kotlin
@Immutable
data class User(
    val id: String,
    val name: String
)
```

- No mutable public state
- Equality is meaningful
- Safe for recomposition skipping

---

### @Stable

```kotlin
@Stable
class CounterState(var count: Int)
```

Use when:
- Internal mutation exists
- Changes are observable by Compose

You are promising correctness. Lying here causes bugs.

---

## Unstable parameters

A parameter is **unstable** when Compose cannot reliably track changes.

### Common unstable types

- Mutable classes
- Mutable collections (`MutableList`, `ArrayList`)
- Classes with mutable public properties
- Java types without immutability guarantees

---

### Example

```kotlin
class User(var name: String)
```

Passing this forces recomposition on every call.

---

## Collections and stability

### Problem

```kotlin
val items: List<a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item>
```

`List` is not guaranteed immutable.

---

### Solutions

- Use immutable collections
- Use `kotlinx.collections.immutable`
- Wrap collections in stable state holders

---

## Lambdas and stability

### Stable lambdas

Lambdas are stable if:
- They do not capture unstable state
- Their identity is preserved

---

### Unstable lambdas

```kotlin
Button(onClick = { submit(user) })
```

Recreated every recomposition.

---

### Fix

```kotlin
val onSubmit = remember(user) { { submit(user) } }
```

---

## Compiler inference

The Compose compiler:
- Infers stability automatically
- Marks parameters as stable or unstable
- Injects runtime checks

Incorrect assumptions force recomposition.

---

## When stability matters most

- Deep composable trees
- Lazy lists
- Frequently updating state
- Performance-critical screens

Ignoring stability here costs frames.

---

## Anti-patterns

- Annotating everything with `@Stable`
- Passing mutable models directly
- Relying on reference equality
- Ignoring compiler warnings

Stability annotations are not decorations.

---

## Mental model

Think in terms of trust:

```
Can Compose trust this value?
Yes → Stable
No  → Unstable
```

If Compose can’t trust it, it recomposes.

---

## Official documentation

- https://developer.android.com/jetpack/compose/performance
- https://developer.android.com/jetpack/compose/recomposition
- https://developer.android.com/jetpack/compose/mental-model
- https://developer.android.com/jetpack/compose/co