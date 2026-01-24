# JobScheduler — Capabilities, Limits, and When to Use It

> This section explains **what JobScheduler actually does**, how it differs from AlarmManager and WorkManager, its constraints and quotas, and when it is the right (or wrong) tool in modern Android.


## 1. What JobScheduler Really Is

JobScheduler is:
- A **system-managed job execution framework** (API 21+)
- Designed for **deferrable background work**
- Heavily optimized for **battery and system health**

JobScheduler is **NOT**:
- A precise scheduler
- A foreground task runner
- A guarantee of immediate execution

It exists to let the **system decide when your work runs**.

---

## 2. Historical Context

Before JobScheduler:
- Apps abused AlarmManager
- Background polling destroyed battery

JobScheduler introduced:
- Execution batching
- Constraint-based scheduling
- OS-controlled fairness

WorkManager later built **on top of this idea**.

---

## 3. Core Concepts

### Job
A unit of background work registered with the system.

### JobService
Your execution entry point.

```kotlin
class SyncJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        doWorkAsync(params)
        return true // work continues
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true // reschedule
    }
}
```

---

## 4. Constraints (Very Similar to WorkManager)

```kotlin
JobInfo.Builder(JOB_ID, component)
    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
    .setRequiresCharging(true)
    .setPersisted(true)
    .build()
```

Supported constraints:
- Network type
- Charging
- Idle (API dependent)
- Storage not low

Constraints are **ANDed**.

---

## 5. Execution Reality

Important truths:
- Jobs run **when the system feels like it**
- Timing is approximate
- Jobs may be delayed or dropped

Your code must:
- Be idempotent
- Handle restarts
- Expect partial execution

---

## 6. Threading Model (Easy to Get Wrong)

JobService callbacks:
- Run on the **main thread**

You MUST:
- Offload work to background threads
- Signal completion manually

```kotlin
override fun onStartJob(params: JobParameters): Boolean {
    scope.launch(Dispatchers.IO) {
        sync()
        jobFinished(params, false)
    }
    return true
}
```

Failure to do this leads to ANRs.

---

## 7. Job Lifetime and Process Death

- JobScheduler can restart jobs
- Your process can be killed anytime

If `onStopJob` is called:
- Cleanup
- Persist state

Returning `true` → reschedule
Returning `false` → drop

---

## 8. Periodic Jobs (Heavily Restricted)

```kotlin
.setPeriodic(15 * 60 * 1000)
```

Rules:
- Minimum 15 minutes
- Inexact timing
- Subject to batching

Exactly like WorkManager periodic work.

---

## 9. Quotas and Throttling

Modern Android applies:
- Execution quotas
- Standby buckets
- Background restrictions

Effects:
- Jobs may be deferred for hours
- Low-priority apps starve

You cannot override this.

---

## 10. Persistence Across Reboots

```kotlin
.setPersisted(true)
```

Requirements:
- `RECEIVE_BOOT_COMPLETED`

Even then:
- Execution timing is not guaranteed

---

## 11. JobScheduler vs WorkManager

| Concern | JobScheduler | WorkManager |
|------|-------------|------------|
| API level | 21+ | 14+ |
| Abstraction | Low | High |
| Persistence | Manual | Automatic |
| Retry handling | Manual | Built-in |
| Chaining | Manual | Built-in |
| Recommended today | Rarely | Yes |

---

## 12. When JobScheduler Is Still Used

Legitimate cases:
- System apps
- OEM software
- Framework-level libraries
- Extremely performance-sensitive scheduling

For app developers:
- Almost never

---

## 13. Common Production Mistakes

- Doing work on main thread
- Assuming exact timing
- Forgetting idempotency
- Ignoring stop callbacks
- Reimplementing WorkManager poorly

---

## 14. Rules of Thumb

- If you’re an app → use WorkManager
- If you need constraints → WorkManager
- If you need chaining → WorkManager
- If you need guarantees → WorkManager

JobScheduler is a **low-level primitive**.

---

## 15. How This Connects

JobScheduler underpins:
- WorkManager
- System background execution
- Battery optimization policies

Understanding it explains **why WorkManager behaves the way it does**.

---

> If you think JobScheduler gives you control, Android will remind you who’s in charge.

