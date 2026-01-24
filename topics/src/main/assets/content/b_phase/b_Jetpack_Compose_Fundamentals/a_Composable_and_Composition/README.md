# Composables and Composition (Jetpack Compose)

## What it is

**Composable functions** are Kotlin functions annotated with `@Composable` that describe **UI as a function of state**. They declare *what* the UI should look like, not *how* to mutate it.

**Composition** is the process by which Jetpack Compose:
- Executes composable functions
- Builds and maintains the UI tree
- Tracks state reads
- Schedules recomposition when state changes

Compose follows a **declarative UI model**.

---

## Why it exists

Traditional Android Views:
- Imperative UI mutations
- Complex lifecycle management
- Manual invalidation
- Fragile state handling

Jetpack Compose:
- UI is derived from state
- Automatic updates
- Deterministic rendering
- Simplified lifecycle

The goal is **predictability, performance, and scalability**.

---

## Core rules of composables

1. **Composable functions can only be called from other composables**
2. **Composable functions must be side-effect free** (during composition)
3. **Composable functions may run many times**
4. **Composable functions must be fast**

Violating these rules leads to bugs, wasted recompositions, or crashes.

---

## Composition

### What composition does

Composition:
- Executes composables top-down
- Emits nodes into a UI tree
- Assigns each node a position
- Records state reads

This tree is *not* a View hierarchy. It is a **slot table–backed structure** used internally by Compose.

---

### Initial composition

When a composable is first displayed:
1. Compose runs the composable function
2. UI nodes are created
3. State reads are registered
4. Layout, draw, and input phases follow

---

## Recomposition

### What triggers recomposition

Recomposition occurs when:
- A `State<T>` read by a composable changes
- A `MutableState` value is updated
- A `Flow`, `LiveData`, or `StateFlow` emits (via adapters)

Only **affected composables** are recomposed.

---

### How recomposition works

- Compose re-executes the composable
- Compares emitted nodes with previous ones
- Skips unchanged parts
- Updates only what is necessary

This process is **structural diffing**, not full re-rendering.

---

## Stability and recomposition skipping

Compose relies on **stability** to skip recompositions.

### Stable types

A type is stable if:
- Its public properties never change
- Or changes are observable by Compose

Examples:
- Primitive types
- `String`
- `Immutable` data classes
- `State<T>`

Unstable types force recomposition.

---

## remember

### What `remember` does

`remember` stores a value across recompositions:

```kotlin
val counter = remember { mutableStateOf(0) }
```

- Initialized once per composition
- Survives recomposition
- Cleared when composable leaves the composition

---

### remember vs rememberSaveable

| remember | rememberSaveable |
|--------|------------------|
| Survives recomposition | Survives recomposition |
| Lost on process death | Restored after process death |
| Not saved | Saved via Bundle |

---

## Side effects

Composable functions must not perform side effects directly.

### Correct side-effect APIs

- `LaunchedEffect`
- `SideEffect`
- `DisposableEffect`
- `rememberCoroutineScope`

Example:

```kotlin
LaunchedEffect(userId) {
    viewModel.loadUser(userId)
}
```

---

## Composition lifecycle

### Entering composition
- `remember` values are created
- Effects start

### Leaving composition
- `DisposableEffect` cleanup runs
- Remembered values are discarded

Compose handles lifecycle automatically.

---

## CompositionLocal

`CompositionLocal` allows implicit value propagation:

```kotlin
val LocalSpacing = compositionLocalOf { 8.dp }
```

Use sparingly. Overuse leads to hidden dependencies.

---

## Common pitfalls

- Performing I/O in composables
- Mutating state during composition
- Passing unstable objects
- Forgetting `remember`
- Creating new objects on every recomposition

Compose punishes careless code.

---

## Mental model

Think of Compose as:

```
UI = f(state)
```

Whenever `state` changes, `f(state)` is re-evaluated **only where needed**.

---

## Official documentation

- https://developer.android.com/jetpack/compose
- https://developer.android.com/jetpack/compose/composition
- https://developer.android.com/jetpack/compose/mental-model
- https://developer.android.com/jetpack/compose/state
- https://kotlinlang.org/docs/composition-overview.html

