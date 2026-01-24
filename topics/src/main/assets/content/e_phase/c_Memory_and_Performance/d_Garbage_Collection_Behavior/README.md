# Garbage Collection Behavior (ART)

This document explains **how garbage collection actually works in Android (ART)**, why it causes jank, when it blocks the UI thread, and how senior engineers reason about GC instead of blaming "performance" or "Compose".

---

## 1. What garbage collection really is

Garbage Collection (GC) is the process by which **ART finds and frees heap objects that are no longer reachable**.

Key truths:
- GC is about **reachability**, not usage
- GC trades **CPU time** for **memory safety**
- GC is not free, not instant, and not magic

If an object is reachable, it will **never** be collected.

---

## 2. ART’s GC goals

ART’s GC is designed to:
- Minimize pause times
- Favor UI responsiveness
- Handle mobile memory constraints

But:
> ART optimizes for the *average case*, not pathological allocation patterns.

---

## 3. Generational GC model

ART uses a **generational heap**:

- **Young generation**
  - Short-lived objects
  - Collected frequently

- **Old generation**
  - Long-lived objects
  - Collected rarely

Most objects die young. GC exploits this.

---

## 4. GC phases (simplified)

A GC cycle includes:

1. **Mark** – find reachable objects
2. **Sweep** – reclaim unreachable memory
3. **Compact** – reduce fragmentation (sometimes)

Some phases:
- Run concurrently
- Some **stop the world (STW)**

Even "concurrent" GC has brief STW points.

---

## 5. Stop-the-world pauses (the jank source)

During STW:
- All app threads pause
- UI thread stops
- Frames are dropped

Typical STW durations:
- Few milliseconds (fine)
- 16ms+ → visible jank
- 100ms+ → obvious freeze

This is why GC shows up as animation stutter.

---

## 6. GC triggers (what causes collections)

Common triggers:
- High allocation rate
- Promotion pressure (young → old)
- Large object allocation
- Old generation nearing capacity

```text
GC_FOR_ALLOC
GC_CONCURRENT
GC_EXPLICIT
```

Frequent `GC_FOR_ALLOC` is a red flag.

---

## 7. Allocation vs retention (critical distinction)

- Allocation is cheap (TLAB)
- Retention is expensive

Bad pattern:
```kotlin
items.map { it.copy() } // every frame
```

Problem is **lifetime**, not allocation itself.

---

## 8. GC and UI rendering

GC competes with rendering for:
- CPU time
- Cache locality

When GC runs during:
- Animations
- Scrolling
- Transitions

You see dropped frames.

This is not a GPU issue.

---

## 9. Compose and GC behavior

Compose allocates more frequently by design:
- Immutable state
- Snapshot system

Compose is GC-friendly **if used correctly**.

Problems arise when:
- State is recreated on recomposition
- Unstable parameters cause churn
- Lists are rebuilt repeatedly

These create GC pressure, not Compose bugs.

---

## 10. Explicit GC (don’t do this)

```kotlin
System.gc()
```

Problems:
- Not guaranteed
- Often ignored
- Can cause **worse jank**

Calling GC manually is almost always wrong.

---

## 11. GC logs: how to read them

Example:
```text
GC_FOR_ALLOC freed 1024K, 12% free
Paused 4ms total 10ms
```

Interpretation:
- Allocation-triggered GC
- Pause time matters more than freed size

Look for **pause duration trends**.

---

## 12. Debugging GC issues (real workflow)

1. Observe jank or freezes
2. Check GC logs
3. Measure allocation rate
4. Capture heap dump
5. Reduce object lifetime

Never start by tuning GC.

---

## 13. Senior rules of thumb

- GC pauses cause UI jank
- Allocation rate matters less than retention
- Old-gen pressure is expensive
- Fix object lifetime, not GC settings

---

## 14. Final summary

- GC is inevitable
- STW pauses are the real enemy
- Heap design determines GC cost
- Compose magnifies bad heap patterns
- Master GC behavior to master performance

Understanding GC is **mandatory** for smooth UIs and reliable apps.

