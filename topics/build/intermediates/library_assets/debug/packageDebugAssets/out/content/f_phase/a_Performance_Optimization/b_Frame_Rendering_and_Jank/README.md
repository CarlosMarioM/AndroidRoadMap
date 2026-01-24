# Frame Rendering and Jank

This document explains **how Android renders frames**, what **jank actually is**, where frames get dropped, and how senior Android developers diagnose and fix rendering problems without guesswork.

Jank is not a vague UX issue — it is a **pipeline failure**.

---

## What is a frame

A **frame** is a single visual update drawn to the screen.

On most devices:
- **60Hz** → 16.67 ms per frame
- **90Hz** → 11.11 ms per frame
- **120Hz** → 8.33 ms per frame

If a frame misses its deadline → **jank**.

---

## What jank is (no myths)

Jank is:
- Missed or delayed frames
- Visual stutter
- Non-smooth animations or scrolling

Jank is **not**:
- ANR
- Crash
- Slow network

You can have severe jank with zero ANRs.

---

## The Android rendering pipeline

Simplified pipeline:

```
App (UI thread)
 ├─ Input handling
 ├─ Measure / Layout
 ├─ Draw
 ↓
RenderThread
 ├─ DisplayList recording
 ├─ GPU commands
 ↓
SurfaceFlinger
 ├─ Buffer composition
 ↓
Display
```

Each stage has a **time budget**.

---

## The Choreographer

`Choreographer` coordinates frame rendering:

- Ticks once per vsync
- Schedules callbacks in this order:
  1. Input
  2. Animation
  3. Traversal (measure/layout/draw)
  4. Commit

If traversal misses the vsync → dropped frame.

---

## Main-thread causes of jank

### 1. Heavy layout passes

- Deep view hierarchies
- Nested `ConstraintLayout`
- Repeated measure/layout

```kotlin
requestLayout() // ❌ repeated calls
```

---

### 2. Expensive `onDraw()`

```kotlin
override fun onDraw(canvas: Canvas) {
    paintBitmapEveryFrame() // ❌
}
```

Drawing must be **cheap and predictable**.

---

### 3. Object allocation during draw

Allocations trigger GC → GC pauses cause jank.

```kotlin
val paint = Paint() // ❌ per frame
```

---

### 4. Blocking work on UI thread

Any work that blocks the Looper delays frames:

- Disk IO
- Locks
- JSON parsing

---

## RenderThread & GPU causes

- Overdraw
- Large textures
- Shader compilation
- Complex clipping

Even if UI thread is fast, GPU can miss deadlines.

---

## RecyclerView-specific jank

Common causes:

- Heavy `onBindViewHolder()`
- Image decoding on main thread
- Synchronous DiffUtil

Fixes:

- Async image loading
- `ListAdapter`
- Stable IDs

---

## Compose-specific jank

Compose introduces different failure modes:

- Excessive recomposition
- Unstable parameters
- Large composition trees

```kotlin
@Composable
fun Bad(item: a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item) { /* recomposes constantly */ }
```

Rules:
- Keep composables small
- Stabilize parameters
- Avoid recomposition storms

---

## Measuring jank

### Android Studio

- **System Trace** (most important)
- **Frame Timeline**
- **Layout Inspector**

### Metrics

- Frame duration
- Janky frames percentage
- Slow vs frozen frames

---

## Jank vs ANR (critical distinction)

| Jank | ANR |
|----|----|
| Missed frames | Blocked main thread |
| Visual issue | System watchdog |
| User annoyance | App freeze |

Fixing jank does **not** automatically fix ANRs.

---

## Prevention rules (senior-level)

- UI thread must be boring
- Allocate once, reuse forever
- Measure, don’t guess
- Fix layout before code
- Optimize what the user sees

---

## Mental model

> Every frame is a deadline.

Miss enough deadlines and the user loses trust.

---

## Interview takeaway

**Jank is a rendering pipeline problem, not a threading myth.**

If you know where frames die, you know how to save them.

