# Service Timeout (Android ANR)

## What it is
A **Service Timeout** is an **ANR** raised when an Android `Service` fails to complete required lifecycle work within strict system time limits.

Unlike Broadcast ANRs, this usually happens when **starting or binding to a service** and blocking the main thread.

---

## System time limits

| Scenario | Timeout |
|--------|--------|
| `startService()` / `onStartCommand()` | ~20 seconds |
| `bindService()` / `onBind()` | ~10 seconds |
| Foreground service start (post-Android 8) | ~5 seconds to call `startForeground()` |

> These are **hard watchdog limits** enforced by `ActivityManagerService`.

---

## Where the timeout happens

Execution flow:

1. System issues a **Binder call** to your app
2. Main thread enters service lifecycle callback
3. Watchdog timer starts
4. Callback must **return** before timeout

If it doesn’t → **Service ANR**.

---

## Common causes

### Heavy work in `onStartCommand()`
```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    database.migrate()      // ❌ disk IO
    syncRemoteData()        // ❌ network
    return START_STICKY
}
```

### Blocking in `onBind()`
```kotlin
override fun onBind(intent: Intent): IBinder {
    Thread.sleep(8000) // ❌ blocks bind
    return binder
}
```

### Waiting synchronously
```kotlin
runBlocking {
    repository.refresh()
}
```

---

## Foreground service pitfalls

Common mistake:
```kotlin
override fun onCreate() {
    heavyInit() // ❌ before startForeground()
}
```

Correct pattern:
```kotlin
override fun onCreate() {
    startForeground(NOTIFICATION_ID, notification)
    launchBackgroundWork()
}
```

Failure to call `startForeground()` fast enough → **Foreground Service ANR**.

---

## Correct patterns

### Delegate work immediately

```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    CoroutineScope(Dispatchers.IO).launch {
        doWork()
        stopSelf(startId)
    }
    return START_NOT_STICKY
}
```

Rules:
- Lifecycle method returns immediately
- Work happens off main thread

---

### Prefer WorkManager

```kotlin
WorkManager.getInstance(context)
    .enqueue(OneTimeWorkRequest.from(SyncWorker::class.java))
```

Why:
- No ANR risk
- Survives process death
- Respects background limits

---

### Bound services: be fast

```kotlin
override fun onBind(intent: Intent): IBinder {
    return binder // nothing else
}
```

Move initialization elsewhere.

---

## How it shows in ANR traces

Typical log:
```
ANR in com.example.app
Reason: Executing service com.example/.MyService
```

Main thread stack often shows:
- Disk IO
- Locks
- Long initialization

---

## Debugging workflow

1. Identify service type (started / bound / foreground)
2. Inspect lifecycle callbacks
3. Verify `startForeground()` timing
4. Check main-thread blocking
5. Replace with WorkManager if possible

---

## Hard rules (senior-level)

- Services are **not workers**
- Lifecycle callbacks must be trivial
- Foreground ≠ unlimited time
- If it can be deferred → **don’t use a service**

---

## Relation to other ANRs

| Type | Trigger |
|----|-------|
| Input Dispatch Timeout | UI blocked |
| Broadcast Timeout | `onReceive()` blocked |
| Service Timeout | Service lifecycle blocked |

---

## Key takeaway

A Service exists to **expose capability**, not to do work synchronously.

If you block its lifecycle, the system will stop waiting.

