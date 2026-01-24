# Java / Kotlin Heap (ART Managed Heap)

This document explains **exactly** how the Java/Kotlin heap works on Android, without fragmentation, repetition, or hand‑waving. This is the level expected from a **senior Android engineer**.

---

## 1. What the Java/Kotlin heap actually is

The Java/Kotlin heap is the **managed memory region owned by ART (Android Runtime)** where all *managed objects* live:
- Java objects
- Kotlin objects (Kotlin has no separate heap)
- Arrays, collections
- Lambdas, closures
- Most UI and state objects

Key properties:
- **Garbage collected**
- **Process‑local** (each app process has its own heap)
- **Shared by all threads**

> Kotlin compiles to JVM bytecode. At runtime, Kotlin objects *are Java objects*.

---

## 2. Heap vs total process memory

The Java heap is **not your app’s total memory**.

| Memory region | Managed by ART | Typical usage |
|--------------|---------------|---------------|
| Java/Kotlin heap | ✅ | Objects, state, models |
| Native heap | ❌ | Bitmaps, JNI, Skia, C/C++ |
| Stack | ❌ | Function calls, locals |
| Code/Dex | ❌ | Bytecode, compiled code |

Android kills apps based on **total process memory**, not heap size alone.

---

## 3. Object layout and hidden cost

Every object has overhead:
- Object header (class pointer, GC metadata)
- Alignment padding (usually 8 bytes)

This means:
- `Boolean` is **not** 1 byte
- Many small objects are expensive

Rule:
> Fewer medium objects > many tiny objects

---

## 4. Heap generations (ART model)

ART uses a **generational heap**:

- **Young generation** – short‑lived objects
- **Old generation** – long‑lived objects

Most objects die young.

Examples:
- Temporary lists, Compose recomposition data → young gen
- ViewModels, caches, singletons → old gen

Promotion to old gen increases **GC cost**.

---

## 5. Allocation: why it feels cheap

ART uses **Thread‑Local Allocation Buffers (TLABs)**:
- Each thread allocates from its own buffer
- Allocation is a pointer bump

a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result:
- Allocation is fast
- High allocation rate is fine **until GC runs**

The real cost is **object lifetime**, not creation.

---

## 6. Large Object Space (LOS)

Large objects (large arrays, buffers):
- Skip young generation
- Allocated in special regions
- Expensive to move and collect

```kotlin
val buffer = ByteArray(5_000_000) // dangerous if repeated
```

Repeated large allocations = guaranteed GC pressure.

---

## 7. Garbage Collection realities

ART uses mostly concurrent GC, but:
- Some phases **stop the world**
- UI thread can stall → dropped frames

Common GC triggers:
- Allocation pressure
- Large object promotion
- Old‑gen growth

Debug:
```bash
adb logcat | grep GC
```

If you see frequent `GC_FOR_ALLOC`, you have a design problem.

---

## 8. Heap limits

Each app has a device‑dependent heap limit:

```kotlin
val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
val heapMb = am.memoryClass
```

Typical values:
- Low‑end: ~128 MB
- Mid‑range: ~256 MB
- High‑end: 512 MB+

Exceed → `OutOfMemoryError` → process death.

---

## 9. Memory leaks (what they really are)

Leaks are **reference ownership bugs**, not GC failures.

Common causes:
- Static references to `Context`
- ViewModel holding `View` / `Activity`
- Long‑lived coroutines
- Callbacks not cleared

```kotlin
// BAD
object Holder { lateinit var context: Context }

// GOOD
object Holder { lateinit var appContext: Context }
```

If an object is reachable, it is *not* garbage.

---

## 10. Compose‑specific heap pitfalls

Compose allocates aggressively by design.

Watch for:
- Unstable parameters
- Recreating lambdas
- State created during recomposition

```kotlin
// BAD
@Composable
fun Screen() {
    val state = mutableStateOf(0)
}

// GOOD
@Composable
fun Screen() {
    val state = remember { mutableStateOf(0) }
}
```

Most Compose performance issues are **heap issues**.

---

## 11. How seniors debug heap problems

Actual workflow:
1. Reproduce jank / OOM
2. Capture heap dump
3. Inspect retained objects
4. Follow reference chains
5. Fix ownership, not GC settings

Tools:
- Android Studio Memory Profiler
- Heap dumps (.hprof)
- LeakCanary (debug only)

Skipping heap dumps = guessing.

---

## 12. Practical rules

- Allocation is cheap, retention is expensive
- Hoist allocations out of hot paths
- Reuse long‑lived objects
- Scope ownership explicitly
- Never rely on GC to fix design mistakes

---

## 13. Final summary

- Kotlin uses the Java heap
- Heap is managed, not free
- GC pauses cause UI jank
- Most memory bugs are ownership bugs
- Mastering the heap is mandatory for performance

This document is **complete**. Anything beyond this belongs to **native heap**, **stack**, or **GC internals**.

