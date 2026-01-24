# Custom Layouts — Measure & Layout (Jetpack Compose)

## What it is

Custom layouts in Jetpack Compose allow you to **control how composables are measured and positioned** by defining your own layout logic.

This is the Compose equivalent of:
- `onMeasure`
- `onLayout`

…but with a **declarative, functional API**.

---

## Why it exists

Built-in layouts (`Row`, `Column`, `Box`, `Lazy*`) cover most use cases.

Custom layouts exist for when you need:
- Non-standard positioning
- Precise measurement control
- Performance optimizations
- Layout algorithms that don’t fit existing primitives

If you reach for custom layouts too early, you’re doing it wrong.

---

## Core layout phases in Compose

Compose layout happens in **three distinct phases**:

1. **Measure** – children decide their size within constraints
2. **Layout** – children are positioned
3. **Draw** – pixels are rendered

Custom layouts only control **measure and layout**.

---

## Constraints

### What constraints are

Constraints define the allowed size range:

```kotlin
Constraints(
    minWidth,
    maxWidth,
    minHeight,
    maxHeight
)
```

Rules:
- Children must respect constraints
- Parents choose constraints
- Children choose size *within* them

Violating constraints crashes at runtime.

---

## Layout composable

### Basic structure

```kotlin
Layout(
    content = { /* children */ }
) { measurables, constraints ->
    // measure
    // layout
}
```

This is the lowest-level layout API.

---

## Measuring children

### Measurable → Placeable

```kotlin
val placeable = measurable.measure(constraints)
```

- `Measurable` represents a child before measurement
- `Placeable` represents a measured child

Each child can be measured **once per layout pass**.

---

## Layout result

### layout(width, height)

```kotlin
layout(width, height) {
    placeable.place(x, y)
}
```

- Parent defines its own size
- Children are positioned inside

---

## Complete example

```kotlin
@Composable
fun CenterLayout(content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints)
        }

        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            placeables.forEach { placeable ->
                val x = (width - placeable.width) / 2
                val y = (height - placeable.height) / 2
                placeable.place(x, y)
            }
        }
    }
}
```

---

## Multiple measurements

### Rule

> **A measurable can only be measured once per layout pass.**

If you need different constraints:
- Use intrinsic measurements
- Or restructure the layout

---

## Intrinsic measurements

Compose supports intrinsic sizing:
- `minIntrinsicWidth`
- `maxIntrinsicWidth`
- `minIntrinsicHeight`
- `maxIntrinsicHeight`

Use sparingly. Intrinsics are expensive.

---

## Modifier.layout

### What it is

`Modifier.layout` allows custom measurement and placement **for a single composable**:

```kotlin
Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.place(0, 0)
    }
}
```

Prefer this over full custom layouts when possible.

---

## Performance considerations

- Avoid complex math in measure
- Avoid intrinsic measurements
- Keep layouts stateless
- Prefer built-in layouts when possible

Layout is on the UI thread. Be disciplined.

---

## Common mistakes

- Ignoring constraints
- Measuring children multiple times
- Hardcoding sizes
- Using custom layouts for simple spacing

Custom layouts are powerful — misuse them and you pay.

---

## Mental model

Think in terms of:

```
Parent → constraints
Child → size
Parent → position
```

That’s it. No magic.

---

## Official documentation

- https://developer.android.com/jetpack/compose/layout
- https://developer.android.com/jetpack/compose/custom-layout
- https://developer.android.com/jetpack/compose/mental-model

