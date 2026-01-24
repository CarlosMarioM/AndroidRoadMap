# Measure / Layout / Draw Phases

This is where Android UI knowledge stops being academic and becomes architectural. If you misunderstand Measure/Layout/Draw, **your layouts will be slow, janky, or memory-heavy**.

---

## What Measure/Layout/Draw really means

Every View participates in three critical phases:
1. **Measure** – determine how big it wants to be
2. **Layout** – decide where it sits inside the parent
3. **Draw** – render pixels on the screen

Think of it as a **pipeline**. Every View adds cost at each stage. If you can’t answer *“what gets measured, laid out, and drawn?”*, your code is already fragile.

---

## Measure phase (why it matters)

- Method: `measure(int widthMeasureSpec, int heightMeasureSpec)`
- Purpose: child reports size to parent under constraints
- Custom View must call `setMeasuredDimension(width, height)`

Example:
```kotlin
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val desiredWidth = 100
    val desiredHeight = 50
    val width = resolveSize(desiredWidth, widthMeasureSpec)
    val height = resolveSize(desiredHeight, heightMeasureSpec)
    setMeasuredDimension(width, height)
}
```

Key traps:
- Heavy computation in `onMeasure()` → jank
- Not calling `setMeasuredDimension()` → crash
- Nested layouts → multiple measure passes

---

## Layout phase (why it matters)

- Method: `layout(int left, int top, int right, int bottom)`
- Purpose: parent positions children
- Called after measure recursively

Example:
```kotlin
override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val child = getChildAt(0)
    child.layout(0, 0, child.measuredWidth, child.measuredHeight)
}
```

Key traps:
- Misplacing children → broken UI
- Heavy calculations → delayed rendering
- Ignoring padding/margin → inconsistent layout

---

## Draw phase (why it matters)

- Method: `draw(Canvas)`
- Purpose: render content on screen
- Calls `onDraw()` for custom drawing

Example:
```kotlin
override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val paint = Paint().apply { color = Color.RED }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
}
```

Key traps:
- Allocating objects inside `onDraw()` → GC pressure
- Complex drawing logic → frame drops
- Overlapping Views → overdraw → GPU waste

---

## Senior-level mental model

- Measure/Layout/Draw = **performance-critical pipeline**
- Every View adds cost in all phases
- Depth of hierarchy multiplies cost

Rules of thumb:
- Keep `onMeasure()` light
- Keep `onLayout()` simple
- Keep `onDraw()` minimal and reuse objects
- Flatten hierarchy where possible
- Profile layout passes and GPU usage

---

## Real-world failure patterns

- Nested LinearLayouts → multiple redundant measure/layout passes
- Allocation in `onDraw()` → dropped frames
- Infinite `requestLayout()` loops
- Heavy custom ViewGroups mismanaging layout → memory overhead

---

## Takeaway

> Measure/Layout/Draw is **the pipeline every View participates in**. Hierarchy depth and inefficient views amplify costs. Understanding this is what separates junior UI from senior, maintainable Android UIs.

