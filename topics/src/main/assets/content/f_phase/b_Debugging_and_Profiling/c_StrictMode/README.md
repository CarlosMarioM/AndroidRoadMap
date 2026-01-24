# StrictMode in Android

This document explains **how to use StrictMode** to detect accidental main-thread blocking, leaked resources, and other misbehaviors that affect app performance and stability.

StrictMode is a **developer tool for runtime diagnostics**, not production enforcement.

---

## What StrictMode is

- Runtime tool provided by Android
- Detects violations such as:
  - Disk/network IO on main thread
  - Leaked SQLite cursors or file descriptors
  - Slow calls in lifecycle methods
- Can log, crash, or notify developers

---

## Enabling StrictMode

### Application-wide setup

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyFlashScreen() // optional visual cue
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
```

- `detectAll()` covers **network, disk, custom slow calls, leaked resources**
- ThreadPolicy = main-thread violations
- VmPolicy = VM-level violations (memory leaks, SQLite)

---

## Common StrictMode violations

### 1. Disk/network on main thread

```kotlin
fun loadFile() {
    File("data.json").readText() // triggers StrictMode
}
```

- Detected as **DiskRead/Write** or **NetworkOnMainThread**
- Use `Dispatchers.IO` or `AsyncTask` replacement

### 2. Leaked SQLite cursors

```kotlin
val cursor = db.query(...)
// forget to close cursor
```

- Detected by VmPolicy `detectLeakedSqlLiteObjects()`
- Always use `use` block or close cursors explicitly

### 3. Leaked file descriptors

- Open streams not closed
- `detectLeakedClosableObjects()`

### 4. Slow calls in lifecycle methods

- Long `onCreate` or `onResume`
- Can detect with `detectCustomSlowCalls()`

---

## Penalties

- `penaltyLog()` → logs violation
- `penaltyDeath()` → crash app on violation
- `penaltyDialog()` → shows dialog
- `penaltyFlashScreen()` → visual cue

**Use only in debug builds.**

---

## Senior-level usage

1. Enable in debug builds only
2. Run realistic flows (rotation, backgrounding)
3. Examine logs for main-thread violations
4. Fix code by moving IO off main thread, closing resources
5. Re-run until no violations

---

## Compose-specific notes

- Avoid calling suspend or IO functions directly in composables without proper coroutine scope
- Use `LaunchedEffect` or background-scoped flows to prevent violations

---

## Mental model

> StrictMode = runtime watchdog. Alerts when the main thread or VM is misused.

It is a **safety net** to catch mistakes early.

---

## Interview takeaway

**Senior Android developers use StrictMode to catch accidental main-thread work, leaked resources, and lifecycle violations before users notice performance issues.**

