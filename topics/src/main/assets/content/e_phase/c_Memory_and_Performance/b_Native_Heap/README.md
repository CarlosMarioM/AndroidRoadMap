# Native Heap (JNI / C++ / Skia)

This document explains the **Android native heap**: what it is, how it differs from the Java/Kotlin heap, why most real OOMs come from here, and how senior engineers debug and control it.

---

## 1. What the native heap actually is

The **native heap** is unmanaged memory allocated outside ART using native allocators:
- `malloc` / `free`
- `new` / `delete`
- `mmap`

Used by:
- C / C++ code (NDK)
- JNI libraries
- Skia (graphics)
- Bitmap pixel storage
- Media codecs

Key properties:
- ❌ Not garbage-collected
- ❌ Not automatically bounded
- ✅ Counted toward **total process memory**

> ART does not manage native memory. If you allocate it, **you own it**.

---

## 2. Native heap vs Java/Kotlin heap

| Aspect | Java/Kotlin heap | Native heap |
|------|------------------|-------------|
| Managed by | ART | libc / kernel |
| GC | Yes | No |
| Auto cleanup | Yes | No |
| OOM source | Sometimes | Very often |
| Debug difficulty | Medium | High |

Apps usually crash from **native heap exhaustion**, not Java heap overflow.

---

## 3. What actually lives in the native heap

### Bitmaps (most important)
On modern Android:
- Bitmap **pixels** live in native memory
- Java `Bitmap` object is just a handle

```kotlin
val bitmap = BitmapFactory.decodeFile(path)
```

Large images = large native allocations.

---

### Skia & rendering
UI rendering uses native memory for:
- Display lists
- Text glyph caches
- GPU upload buffers

Excessive overdraw and large canvases increase native pressure.

---

### JNI allocations
JNI code frequently allocates:
- Buffers
- Structs
- Caches

```cpp
char* buffer = (char*)malloc(1024);
// MUST be freed
free(buffer);
```

Forgetting to free = permanent leak.

---

## 4. Why native leaks are deadly

Native leaks:
- Are invisible to GC
- Accumulate silently
- Kill the process suddenly

No `OutOfMemoryError`. Just **process death**.

This is why "it worked for 10 minutes" bugs exist.

---

## 5. Common native heap leak patterns

### Bitmap leaks
- Bitmaps cached without eviction
- Bitmaps tied to long-lived objects
- Forgetting to recycle on older APIs

```kotlin
bitmap.recycle() // only relevant pre-API 28
```

---

### JNI reference leaks
JNI global references prevent GC:

```cpp
jobject global = (*env)->NewGlobalRef(env, obj);
// MUST call DeleteGlobalRef
```

If not deleted, Java objects leak *through native code*.

---

### Native caches without bounds
Common in:
- Image loaders
- Audio buffers
- ML models

Unbounded native caches = guaranteed crash.

---

## 6. Allocation patterns that hurt

Worst offenders:
- Large allocations in loops
- Repeated allocate/free cycles
- Fragmented allocations

```cpp
for (...) {
    auto* buf = new uint8_t[1_000_000];
    delete[] buf;
}
```

This causes fragmentation and allocator stress.

---

## 7. How Android counts native memory

Android tracks:
- Java heap
- Native heap
- Graphics
- Stack

Low Memory Killer (LMK) kills based on **total footprint**.

Your Java heap can look fine and still get killed.

---

## 8. Debugging native heap issues

### Tools you must know

- Android Studio **Native Memory Profiler**
- `adb shell dumpsys meminfo <package>`
- `malloc_debug`
- `heapprofd`

```bash
adb shell dumpsys meminfo your.package.name
```

Look for **Native Heap** growth.

---

### Typical debugging workflow

1. Reproduce crash or memory growth
2. Capture native heap snapshot
3. Identify large allocations
4. Track ownership
5. Add bounds / free logic

No snapshot = guessing.

---

## 9. Compose & native heap

Compose itself allocates Java objects, but:
- Rendering still uses native Skia memory
- Large surfaces increase native usage

Big Compose UIs + images = native pressure.

---

## 10. Rules for controlling native heap

- Always define ownership for native memory
- Prefer reuse over reallocation
- Bound all caches
- Free deterministically
- Treat JNI code as hostile until proven safe

---

## 11. Senior-level red flags

- Native heap grows while Java heap is stable
- Crashes without Java stacktrace
- Memory issues only on certain devices
- Long sessions cause crashes

These are **native heap bugs**.

---

## 12. Final summary

- Native heap is unmanaged
- Most real OOMs come from native memory
- Bitmaps are the #1 offender
- JNI leaks are silent killers
- If you ignore native heap, you don’t control memory

This knowledge is mandatory for graphics-heavy, media, ML, or performance-critical Android apps.

