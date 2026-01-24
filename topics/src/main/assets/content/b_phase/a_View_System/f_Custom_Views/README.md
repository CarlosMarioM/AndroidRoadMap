# Custom Views in Android

Custom views exist for **one reason**: the existing widgets cannot express what you need *efficiently*.

If you create a custom view for convenience or aesthetics alone, you’re probably doing it wrong.

This document explains the **only three methods that actually matter**:
- `onMeasure`
- `onLayout`
- `onDraw`

Everything else is secondary.

---

## The real contract of a View

A View participates in a **three-phase pipeline** every frame (or invalidation):

1. **Measure** → How big do you want to be?
2. **Layout** → Where do you go?
3. **Draw** → What do you look like?

These phases are **top-down, then bottom-up**, and tightly controlled by the parent.

If you break the contract in any phase, your view becomes a performance liability.

---

## `onMeasure()` — Size negotiation, not guessing

### What `onMeasure` really does

`onMeasure` answers **one question only**:

> Given these constraints, what size do you need?

It does **not**:
- Decide position
- Draw anything
- Ignore parent constraints

Signature:
```kotlin
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
```

Each `MeasureSpec` packs:
- **Mode**: `EXACTLY`, `AT_MOST`, `UNSPECIFIED`
- **Size**: pixels

You must respect them.

---

### Correct mental model

- Parent proposes constraints
- Child chooses a size **within those constraints**
- Parent may reject and re-measure

This is a *negotiation*, not a dictatorship.

---

### Basic correct implementation

```kotlin
override fun onMeasure(widthSpec: Int, heightSpec: Int) {
    val desiredWidth = 200
    val desiredHeight = 100

    val width = resolveSize(desiredWidth, widthSpec)
    val height = resolveSize(desiredHeight, heightSpec)

    setMeasuredDimension(width, height)
}
```

Use `resolveSize` or `resolveSizeAndState`. Do **not** reinvent this logic.

---

### Common mistakes (real-world)

- Ignoring `MeasureSpec` modes
- Calling `setMeasuredDimension` with raw desired size
- Measuring children multiple times unnecessarily
- Allocating objects during measure

If `onMeasure` allocates, you already failed.

---

## `onLayout()` — Positioning children (ViewGroups only)

### When `onLayout` exists

Only **ViewGroups** implement `onLayout`.

If your custom view has no children, you **do not override this**.

Signature:
```kotlin
override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int)
```

---

### What `onLayout` actually does

It assigns **final positions** to already-measured children.

Important:
- Sizes are final
- Coordinates are relative to parent
- No measuring here

---

### Example: simple horizontal layout

```kotlin
override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    var left = paddingLeft

    for (i in 0 until childCount) {
        val child = getChildAt(i)

        if (child.visibility == View.GONE) continue

        val top = paddingTop
        val right = left + child.measuredWidth
        val bottom = top + child.measuredHeight

        child.layout(left, top, right, bottom)
        left = right
    }
}
```

`layout()` uses **measured sizes**, never raw specs.

---

### Mistakes that kill performance

- Measuring children inside `onLayout`
- Allocating Rects
- Calling `requestLayout()` from here

That causes layout loops.

---

## `onDraw()` — Pure rendering, nothing else

### What `onDraw` is allowed to do

`onDraw` must:
- Read state
- Draw pixels

That’s it.

Signature:
```kotlin
override fun onDraw(canvas: Canvas)
```

---

### Golden rules

Never:
- Allocate objects
- Modify state
- Trigger layout
- Start animations

Every frame counts.

---

### Simple drawing example

```kotlin
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.RED
}

override fun onDraw(canvas: Canvas) {
    canvas.drawCircle(width / 2f, height / 2f, 40f, paint)
}
```

All heavy setup happens **outside** `onDraw`.

---

### Invalidations

- `invalidate()` → redraw only
- `requestLayout()` → remeasure + relayout + redraw

If you call `requestLayout()` when only pixels changed, you’re wasting CPU.

---

## Measure → Layout → Draw (full flow)

1. Parent calls `measure()`
2. Child runs `onMeasure()`
3. Parent calls `layout()`
4. ViewGroup runs `onLayout()`
5. System calls `draw()`
6. View runs `onDraw()`

Breaking this order causes undefined behavior.

---

## Custom View vs Custom ViewGroup

Use a **custom View** when:
- Single drawable entity
- No children
- Performance matters

Use a **custom ViewGroup** when:
- You need layout behavior not expressible by XML
- ConstraintLayout is not enough

Never create a ViewGroup when a View is sufficient.

---

## Testing & debugging tips

- Enable `Debug GPU overdraw`
- Enable `Profile HWUI rendering`
- Log `onMeasure` calls (temporarily)

If measure/layout fires every frame, you have a bug.

---

## Senior-level takeaway

Custom views are **low-level UI code**.

You are:
- Closer to the rendering pipeline
- Responsible for performance
- One mistake away from jank

If you don’t fully understand `onMeasure`, don’t override it.

Android already has enough slow apps.

