# Broadcast Timeout (Android ANR)

## What it is
A **Broadcast Timeout** is a specific type of **ANR (Application Not Responding)** triggered when a `BroadcastReceiver` takes too long to finish executing.

Android enforces **strict time limits** on broadcast handling to protect system responsiveness.

If your app exceeds those limits, the system kills responsiveness and raises an ANR.

---

## System time limits

| Broadcast type | Time limit |
|---------------|-----------|
| **Foreground broadcast** | ~10 seconds |
| **Background broadcast** | ~60 seconds |

These limits are **wall-clock time on the main thread**.

> Coroutines, threads, or async calls **do NOT extend the timeout**.

---

## Where this runs

By default, a `BroadcastReceiver` executes on:
- The **main thread**
- Inside the app’s **process**
- Under the system’s **ActivityManager supervision**

Execution flow:

1. System delivers broadcast via **Binder**
2. Main thread enters `onReceive()`
3. System starts a timeout watchdog
4. `onReceive()` must **return** before timeout

If it doesn’t → **Broadcast ANR**.

---

## Common causes

### Heavy work inside `onReceive`
```kotlin
override fun onReceive(context: Context, intent: Intent) {
    database.cleanUpOldData()   // ❌ disk IO
    api.syncNow()               // ❌ network
}
```

### Waiting synchronously
```kotlin
runBlocking {
    repository.refresh()
}
```

### Misusing coroutines
```kotlin
override fun onReceive(context: Context, intent: Intent) {
    GlobalScope.launch {
        doWork()
    }
}
```

> Launching async work does **not** stop the timeout.

---

## Ordered broadcasts (extra danger)

For **ordered broadcasts**:
- Receivers run **one at a time**
- Your delay blocks **all following receivers**
- Time spent is *amplified system-wide*

Bad pattern:
```kotlin
override fun onReceive(...) {
    Thread.sleep(5000) // ❌ blocks the chain
}
```

---

## Correct patterns

### Use `goAsync()`

```kotlin
override fun onReceive(context: Context, intent: Intent) {
    val pendingResult = goAsync()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            doShortWork()
        } finally {
            pendingResult.finish()
        }
    }
}
```

Rules:
- You **must call `finish()`**
- Still limited by system watchdog
- Only for **short deferrable work**

---

### Delegate to WorkManager (recommended)

```kotlin
override fun onReceive(context: Context, intent: Intent) {
    WorkManager.getInstance(context)
        .enqueue(OneTimeWorkRequest.from(SyncWorker::class.java))
}
```

Why this works:
- Broadcast returns immediately
- Work runs under background execution rules
- Survives process death

---

### Start a foreground service (rare cases)

Only when:
- User-visible
- Immediate work required
- Notification shown

```kotlin
context.startForegroundService(intent)
```

---

## How it shows in ANR traces

Typical log:
```
ANR in com.example.app
Reason: Broadcast of Intent { ... }
```

Stack trace shows:
- Main thread inside `onReceive`
- Often blocked on IO, locks, or sleeps

---

## Debugging workflow

1. Identify broadcast type (implicit / explicit / ordered)
2. Check `onReceive()` duration
3. Verify no blocking calls
4. Confirm `goAsync()` usage correctness
5. Prefer WorkManager

---

## Hard rules (senior-level)

- `onReceive()` must be **fast and boring**
- No disk, no network, no waits
- Async ≠ safe
- If work matters → **delegate**

---

## Relation to other ANRs

| Type | Trigger |
|----|-------|
| Input Dispatch Timeout | UI blocked |
| Broadcast Timeout | `onReceive()` blocked |
| Service Timeout | `onStartCommand()` blocked |

---

## Key takeaway

A BroadcastReceiver is **not a worker**.

It is a **signal handler**.

Handle the signal, schedule the work, return immediately.

