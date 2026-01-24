# State and State Hoisting (Jetpack Compose)

## What it is

**State** represents any value that can change over time and affects what the UI displays.

In Jetpack Compose:
- UI is a pure function of state
- When state changes, Compose schedules recomposition

**State hoisting** is the pattern of **moving state ownership up** to make composables:
- Stateless
- Reusable
- Testable

---

## Why it exists

Without proper state handling:
- UI logic becomes tangled
- Components are hard to reuse
- Bugs appear due to duplicated or conflicting state

State hoisting enforces:
- Single source of truth
- Clear ownership
- Unidirectional data flow

This is not optional for scalable Compose apps.

---

## State in Compose

### MutableState

The fundamental state holder in Compose is `State<T>`:

```kotlin
var count by remember { mutableStateOf(0) }
```

- Reading `count` subscribes the composable
- Writing `count` triggers recomposition

---

### State read tracking

Compose tracks:
- Which composables read which state
- Recompose only affected scopes

This is why **state reads must happen during composition**.

---

## remember and state lifetime

### remember

```kotlin
val text = remember { mutableStateOf("") }
```

- Survives recomposition
- Cleared when composable leaves composition

### rememberSaveable

```kotlin
val text = rememberSaveable { mutableStateOf("") }
```

- Survives recomposition
- Survives configuration change and process death

Uses `Bundle` under the hood.

---

## Stateless vs stateful composables

### Stateful composable (local state)

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("$count")
    }
}
```

Harder to reuse.

---

### Stateless composable

```kotlin
@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit
) {
    Button(onClick = onIncrement) {
        Text("$count")
    }
}
```

No internal state.

---

## State hoisting

### Definition

State hoisting means:
- State is owned by a parent
- Passed down as immutable values
- Events flow upward via callbacks

---

### Hoisted version

```kotlin
@Composable
fun CounterHost() {
    var count by remember { mutableStateOf(0) }

    Counter(
        count = count,
        onIncrement = { count++ }
    )
}
```

This is the **recommended pattern**.

---

## Single source of truth

There must be **one authoritative owner** of a piece of state.

Violations:
- Duplicated `mutableStateOf`
- Local copies of ViewModel state
- Syncing multiple states manually

Compose will not save you from bad architecture.

---

## State holders

### ViewModel as state owner

```kotlin
val uiState by viewModel.uiState.collectAsState()
```

ViewModel:
- Owns business state
- Survives configuration changes
- Keeps composables simple

---

## Unidirectional Data Flow (UDF)

Compose enforces UDF:

```
State ↓
UI → Events
```

Flow:
1. State flows down
2. UI emits events
3. State owner updates state
4. UI recomposes

Breaking UDF causes unpredictable behavior.

---

## Derived state

### derivedStateOf

```kotlin
val isEnabled by remember {
    derivedStateOf { count > 0 }
}
```

- Recomputed only when dependencies change
- Prevents unnecessary recomposition

---

## Snapshot system

Compose state uses a **snapshot system**:
- Thread-safe
- Transactional
- Supports concurrent reads

Direct mutation outside snapshots is forbidden.

---

## Common pitfalls

- Keeping state too low in the tree
- Duplicating ViewModel state in composables
- Mutating state during composition
- Forgetting `remember`
- Overusing `rememberSaveable`

These lead to recomposition storms or bugs.

---

## Mental model

Think in terms of ownership:

```
Who owns this state?
Who can change it?
Who observes it?
```

If you can’t answer, the design is wrong.

---

## Official documentation

- https://developer.android.com/jetpack/compose/state
- https://developer.android.com/jetpack/compose/state-hoisting
- https://developer.android.com/jetpack/compose/mental-model
- https://developer.android.com/topic/architecture/ui-layer

