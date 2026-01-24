# WorkManager — Constraints, Chaining, and Correct Usage

> This section explains **what WorkManager is actually for**, how its constraint system works, how chaining behaves internally, and how to design background work that survives process death without abusing the API.

Senior-level. No myths.

---

## 1. What WorkManager Really Is

WorkManager is:
- A **reliable background task scheduler**
- Designed for **deferrable, guaranteed execution**
- Backed by different schedulers depending on API level

WorkManager is **NOT**:
- A real-time execution engine
- A replacement for coroutines
- A job queue for UI-driven logic

If the task must:
- Survive app restarts
- Survive device reboot
- Respect system conditions

→ WorkManager is the correct tool.

---

## 2. How WorkManager Runs Work (Internals)

Internally, WorkManager delegates to:
- JobScheduler (API 23+)
- AlarmManager + BroadcastReceiver (older devices)

Work is:
- Persisted in an internal Room database
- Re-scheduled after process death

This persistence is why WorkManager exists.

---

## 3. Constraints: When Work Is Allowed to Run

Constraints define **execution eligibility**, not execution time.

### Common Constraints

```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresCharging(true)
    .setRequiresBatteryNotLow(true)
    .build()
```

Supported constraints:
- Network state
- Charging state
- Battery level
- Storage availability
- Device idle (API dependent)

Constraints are **ANDed**, not ORed.

---

## 4. Constraints Are Not Triggers

A common misunderstanding:

> "Run this work when network becomes available"

No.

WorkManager:
- Does NOT react instantly
- Schedules work **when constraints are satisfied**

Execution timing is at the system’s discretion.

If you need immediate reaction → use foreground services or push.

---

## 5. Defining a Worker Correctly

### CoroutineWorker (Preferred)

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result {
        return try {
            sync()
            a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.success()
        } catch (e: IOException) {
            a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.retry()
        }
    }
}
```

Rules:
- `doWork()` must be **idempotent**
- Never block threads manually
- Respect cancellation

---

## 6. a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result Semantics (Critical)

| a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result | Meaning |
|------|--------|
| success() | Work completed |
| retry() | Temporary failure |
| failure() | Permanent failure |

Returning the wrong result causes:
- Infinite retries
- Silent failures
- Battery drain

---

## 7. Chaining Work

Chaining expresses **ordering**, not data flow.

```kotlin
val sync = OneTimeWorkRequestBuilder<SyncWorker>().build()
val cleanup = OneTimeWorkRequestBuilder<CleanupWorker>().build()

WorkManager.getInstance(context)
    .beginWith(sync)
    .then(cleanup)
    .enqueue()
```

Rules:
- Next work runs **only if previous succeeds**
- Failure breaks the chain

---

## 8. Passing Data Between Workers

Use `Data` — small, serializable payloads only.

```kotlin
val data = workDataOf("userId" to "123")
```

Limits:
- ~10KB
- No complex objects

For large data:
- Persist externally (DB, file)
- Pass references

---

## 9. Unique Work and Policies

Unique work prevents duplicate scheduling.

```kotlin
WorkManager.getInstance(context)
    .enqueueUniqueWork(
        "sync",
        ExistingWorkPolicy.KEEP,
        syncRequest
    )
```

Policies:
- KEEP → ignore new
- REPLACE → cancel old
- APPEND → chain after

Choose deliberately.

---

## 10. Periodic Work (Read This Twice)

```kotlin
PeriodicWorkRequestBuilder<SyncWorker>(
    15, TimeUnit.MINUTES
)
```

Rules:
- Minimum interval: **15 minutes**
- Not exact timing
- Constraints still apply

Periodic work is for **maintenance**, not syncing UI state.

---

## 11. Backoff and Retry Strategy

```kotlin
.setBackoffCriteria(
    BackoffPolicy.EXPONENTIAL,
    30, TimeUnit.SECONDS
)
```

Retry rules:
- Only for transient failures
- Combine with `a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.retry()`

Never retry on:
- Validation errors
- Authentication errors

---

## 12. WorkManager + DI (Hilt)

Use `@HiltWorker`:

```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: Repo
) : CoroutineWorker(context, params)
```

Workers are:
- Short-lived
- Not scoped like ViewModels

---

## 13. Observing Work State

```kotlin
WorkManager.getInstance(context)
    .getWorkInfoByIdLiveData(id)
```

States:
- ENQUEUED
- RUNNING
- SUCCEEDED
- FAILED
- CANCELLED

Observation is best-effort, not guaranteed.

---

## 14. Common Production Mistakes

- Using WorkManager for immediate tasks
- Doing UI work in workers
- Blocking threads
- Retrying permanent failures
- Passing large data blobs
- Assuming exact timing

---

## 15. When NOT to Use WorkManager

Do NOT use it for:
- User-triggered immediate actions
- Streaming
- Real-time sync
- Long-running foreground tasks

Use:
- Coroutines
- Foreground services
- Push-triggered work

---

## 16. How This Connects

WorkManager fits with:
- Offline-first sync
- Retry strategies
- Data consistency
- App resilience

It does NOT replace:
- Flow
- Paging
- Coroutines

---

> WorkManager is boring, slow, and reliable — which is exactly the point.

