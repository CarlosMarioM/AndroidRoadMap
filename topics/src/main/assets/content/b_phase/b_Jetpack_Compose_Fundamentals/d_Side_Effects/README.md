# Side Effects — LaunchedEffect & DisposableEffect (Jetpack Compose)

## What it is

In Jetpack Compose, **side effects** are operations that interact with the outside world or cause observable changes beyond returning UI.

Examples:
- Launching coroutines
- Performing I/O
- Updating system state
- Registering listeners
- Calling imperative APIs

Composable functions themselves **must be side‑effect free during composition**.

---

## Why it exists

Composable functions:
- Can run multiple times
- Can be skipped
- Can be reordered

Running side effects directly inside composables would cause:
- Duplicate work
- Memory leaks
- Crashes
- Nondeterministic behavior

Compose provides **controlled side‑effect APIs** tied to the composition lifecycle.

---

## Composition vs side effects

Key rule:

> **Composition describes UI. Side effects perform work.**

Side effects:
- Run *after* successful composition
- Are lifecycle-aware
- Are cancelled or disposed automatically

---

## LaunchedEffect

### What it is

`LaunchedEffect` launches a coroutine that is:
- Scoped to the composition
- Cancelled when keys change or composable leaves composition

```kotlin
LaunchedEffect(key) {
    // suspend work
}
```

---

### When it runs

`LaunchedEffect`:
- Runs after the first composition
- Restarts when any key changes
- Cancels the previous coroutine on restart

Keys define **identity**, not conditions.

---

### Correct usage

```kotlin
LaunchedEffect(userId) {
    viewModel.loadUser(userId)
}
```

Use for:
- One‑off or key‑based suspend work
- Collecting flows
- Triggering business logic

---

### Incorrect usage

```kotlin
LaunchedEffect(Unit) {
    while (true) {
        doWork()
    }
}
```

Unbounded loops and long‑running jobs belong elsewhere (ViewModel).

---

## DisposableEffect

### What it is

`DisposableEffect` is used when you need:
- Setup + cleanup
- Access to imperative APIs

```kotlin
DisposableEffect(key) {
    // setup
    onDispose {
        // cleanup
    }
}
```

---

### When it runs

- Runs after composition
- Re‑runs when keys change
- Calls `onDispose` before restarting
- Calls `onDispose` when leaving composition

---

### Correct usage

```kotlin
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event -> }
    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
    }
}
```

Use for:
- Listeners
- Callbacks
- Resource management

---

## LaunchedEffect vs DisposableEffect

| LaunchedEffect | DisposableEffect |
|---------------|------------------|
| Coroutine-based | Non-suspending |
| Auto-cancelled | Manual cleanup |
| For async work | For imperative APIs |

Use the right tool. Mixing them is a design smell.

---

## rememberCoroutineScope

### What it is

Provides a coroutine scope tied to composition:

```kotlin
val scope = rememberCoroutineScope()
```

Use when:
- Launching coroutines from event callbacks
- You don’t want automatic restart

Scope is cancelled when composable leaves composition.

---

## SideEffect

### What it is

`SideEffect` runs after every successful recomposition:

```kotlin
SideEffect {
    updateSystemUi()
}
```

Rarely needed. Easy to misuse.

---

## Effect keys

Keys define **identity**, not control flow.

Bad:
```kotlin
LaunchedEffect(isVisible) {
    if (isVisible) load()
}
```

Good:
```kotlin
if (isVisible) {
    LaunchedEffect(Unit) { load() }
}
```

Conditional composition is clearer.

---

## Effect lifecycle summary

| Event | a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result |
|-----|-------|
| First composition | Effect runs |
| Key change | Effect disposed and restarted |
| Leaving composition | Effect disposed |

Compose enforces this strictly.

---

## Common mistakes

- Performing I/O directly in composables
- Using wrong effect type
- Misusing keys
- Long‑running work in `LaunchedEffect`
- Forgetting cleanup in `DisposableEffect`

Compose will not warn you — bugs will.

---

## Mental model

Think of effects as:

```
Attach → React → Detach
```

They are **resources**, not logic containers.

---

## Official documentation

- https://developer.android.com/jetpack/compose/side-effects
- https://developer.android.com/jetpack/compose/mental-model
- https://developer.android.com/jetpack/compose/state

