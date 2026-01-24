# Layout Overdraw

This document explains **what layout overdraw is**, why it kills performance, and how senior Android developers detect and eliminate it.

Overdraw is one of the **main hidden causes of jank**, especially on devices with limited GPU performance.

---

## What overdraw is

Overdraw happens when **the same pixel is drawn multiple times within a single frame**.

Example:

- Background color drawn
- Another view drawn on top
- A semi-transparent overlay drawn again

The GPU wastes cycles rendering pixels the user ultimately never sees.

---

## How Android counts overdraw

- Each draw operation is a layer on the frame buffer
- Overdraw count = number of times a pixel is drawn per frame
- System recommends **≤1–2 overdraws per pixel**

Too much overdraw → GPU starvation → dropped frames → jank.

---

## Common sources of overdraw

### 1. Deep view hierarchies

```xml
<FrameLayout>
    <LinearLayout>
        <LinearLayout>
            <TextView />
        </LinearLayout>
    </LinearLayout>
</FrameLayout>
```
- Nested backgrounds are drawn multiple times
- Heavy inflation + overdraw → jank

### 2. Multiple backgrounds

```xml
<LinearLayout android:background="@color/bg">
    <TextView android:background="@color/bg" />
</LinearLayout>
```
- Background drawn twice per pixel

### 3. Transparent layers

- Semi-transparent drawables
- Cards with shadows (elevation) in Compose / Views
- Layered modals / overlays

### 4. Scrolling containers

- RecyclerView items with backgrounds and dividers
- Nested scrolling layouts

---

## Tools to detect overdraw

### 1. GPU Overdraw Debugging

- **Developer Options → Debug GPU Overdraw**
- Colors:
  - Blue = 1x (good)
  - Green = 2x
  - Light red = 3x
  - Dark red = 4x+ (bad)

### 2. Profile GPU Rendering

- Shows frame-by-frame rendering time
- Detects expensive layers

### 3. Layout Inspector / Compose Tooling

- View hierarchy visualization
- Compose: **Layout Inspector shows recomposition + drawing**

---

## How to fix overdraw

### 1. Flatten view hierarchies

- Replace nested layouts with ConstraintLayout or Compose layouts
- Use `merge` tags in XML
- Avoid unnecessary wrappers

### 2. Remove redundant backgrounds

- Only draw background once at root
- Use `android:windowBackground` for static backgrounds

### 3. Use foreground/background wisely

- Elevation/shadows should be used only when needed
- Avoid multiple overlapping semi-transparent layers

### 4. Optimize RecyclerView items

- Use single root background
- Minimize nested containers

### 5. Compose-specific strategies

- Reduce nested `Box` / `Column` / `Row` with backgrounds
- Use `drawBehind` instead of wrapping layouts
- Avoid multiple overlapping modifiers that paint the same area

```kotlin
Box(modifier = Modifier.drawBehind {
    drawRect(color = Color.Gray)
})
```

---

## Senior rules

- Each pixel should ideally be **drawn once**
- Flatten and reuse surfaces
- Overdraw → GPU starvation → dropped frames → jank
- Measure before optimizing

---

## Mental model

> GPU time is finite. Every extra pixel drawn is a wasted deadline.

Design layout to **paint what the user actually sees, once per frame**.

---

## Interview takeaway

**Overdraw is an invisible killer of smooth UI.**

If you see jank on otherwise trivial UI, **check overdraw before blaming coroutines or threads**.

