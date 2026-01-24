# Input Dispatch Timeout (IDT)

## What it is (no myths)

**Input Dispatch Timeout** happens when the Android system cannot deliver or process a user input event (touch, key, motion) within a fixed time window.

When this happens, the system triggers an **ANR (Application Not Responding)**.

This is not about rendering speed or frame drops. It is about **the main thread being unable to handle input in time**.

---

## The hard limits (important)

Android enforces strict timeouts:

- **5 seconds** → Foreground app input timeout
- **10 seconds** → BroadcastReceiver `onReceive()` timeout
- **20 seconds** → Service lifecycle timeout

For input dispatch specifically:

- The system sends an input event to your app’s **main (UI) thread**
- If the event is not processed within **~5 seconds**, Android assumes the app is frozen

This is enforced by the **InputDispatcher** in system_server.

---

## Where input dispatch lives in the system

Simplified pipeline:

```
InputReader (kernel / drivers)
   ↓
InputDispatcher (system_server)
   ↓
WindowManager
   ↓
ViewRootImpl
   ↓
Main Thread (Looper)
   ↓
View.dispatchTouchEvent()
```

If **anything blocks the main thread**, the event cannot be handled → timeout.

---

## The real root cause

> Input Dispatch Timeout is always a **main-thread blockage problem**.

Not rendering.
Not GPU.
Not animations.

It’s one of these:

- Long synchronous work on the UI thread
- Deadlocks involving the main thread
- Waiting on I/O (disk, network, DB)
- Heavy object allocation / GC pressure
- Blocking locks (`synchronized`, `Mutex`, `ReentrantLock`)

---

## Common real-world causes (Android apps)

### 1. Blocking I/O on the main thread

```kotlin
// ❌ Wrong
override fun onClick(v: View) {
    val data = file.readText() // disk I/O on UI thread
}
```

```kotlin
// ✅ Correct
override fun onClick(v: View) {
    lifecycleScope.launch {
        val data = withContext(Dispatchers.IO) {
            file.readText()
        }
    }
}
```

---

### 2. Database queries on UI thread

```kotlin
// ❌ Room query executed synchronously
val users = userDao.getUsers()
```

```kotlin
// ✅ Suspend / Flow-based access
val users = userDao.getUsersFlow()
```

---

### 3. Heavy work inside input handlers

```kotlin
// ❌ onTouch = too much logic
view.setOnTouchListener { _, event ->
    processLargeBitmap()
    true
}
```

Input handlers must be **near-instant**.

---

### 4. Synchronized locks on main thread

```kotlin
// ❌ UI thread waiting for a lock
synchronized(lock) {
    doWork()
}
```

If another thread holds the lock → input freezes.

---

### 5. Deadlocks involving main thread

Classic pattern:

- Background thread waits for UI result
- UI thread waits for background result

a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result: **no one moves** → timeout.

---

## How it looks in ANR traces

Typical ANR log:

```
Reason: Input dispatching timed out
Waiting because the touched window has not finished processing the input events
```

Main thread stack often shows:

- `ViewRootImpl.performTraversals`
- `Choreographer#doFrame`
- Blocked call (I/O, lock, DB, network)

---

## Why rendering issues are not the cause

Dropped frames ≠ input timeout.

- 60 dropped frames → jank
- Blocked Looper → ANR

Even if rendering is slow, as long as the main thread returns to the Looper quickly, **no timeout occurs**.

---

## Coroutines pitfalls that cause IDT

### Blocking instead of suspending

```kotlin
// ❌ Blocks UI thread
runBlocking {
    delay(3000)
}
```

```kotlin
// ✅ Non-blocking
lifecycleScope.launch {
    delay(3000)
}
```

---

### Using Dispatchers.Main incorrectly

```kotlin
// ❌ Heavy work on Main
withContext(Dispatchers.Main) {
    parseJson()
}
```

Main is for **UI updates only**.

---

## RecyclerView-related timeouts

Causes:

- Heavy work in `onBindViewHolder`
- Image decoding on UI thread
- DiffUtil calculated synchronously for huge lists

Fixes:

- Precompute data
- Use async diffing (`ListAdapter`)
- Offload decoding

---

## System-level view: why Android kills you fast

Input is considered **sacred**.

If the user taps and nothing happens:

- The OS assumes your app is broken
- The system protects UX by killing responsiveness

This is why IDT thresholds are **non-negotiable**.

---

## How to debug Input Dispatch Timeout

1. Pull ANR traces:
   ```
   adb pull /data/anr/traces.txt
   ```

2. Inspect **main thread stack**
3. Look for:
   - I/O
   - Locks
   - Long loops
   - GC pauses

4. Use Android Studio:
   - CPU profiler
   - System Trace
   - Main thread timeline

---

## Prevention rules (print these)

- Never block the main thread
- Input handlers must return immediately
- All I/O off UI thread
- Avoid locks on Main
- Prefer async APIs (Flow, suspend)
- Profile before release

---

## Mental model (important)

> If the main thread cannot reach its Looper quickly, input dies.

Everything else is secondary.

---

## Interview-level takeaway

**Input Dispatch Timeout is not a UI problem — it is a threading failure.**

If you understand this, you understand ANRs.

