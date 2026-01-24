# Background Execution Limits — Modern Android Reality

> This section explains **how modern Android actively prevents apps from running in the background**, why these limits exist, and how you are expected to design around them.

This is not theory. This is how the OS behaves today.

---

## 1. The Core Principle

Modern Android is built around one rule:

> **If the user is not interacting with your app, it should not be doing work.**

Everything else flows from this.

---

## 2. Historical Context (Why This Exists)

Android background limits exist because:
- Apps abused background services
- Battery drain was extreme
- OEMs added aggressive task killers

Google responded by:
- Centralizing background execution
- Enforcing system scheduling
- Removing developer control

---

## 3. App States That Matter

Android categorizes apps into execution buckets:

- **Foreground** – visible or interacting
- **Foreground Service** – visible via notification
- **Background** – recently used, not visible
- **Cached** – inactive, reclaimable

Only the first two have real execution privileges.

---

## 4. Android 8.0+ (Oreo): The Big Shift

### Background Service Ban

- Apps cannot start background services freely
- `startService()` from background → crash

```kotlin
ContextCompat.startForegroundService(context, intent)
```

Even then:
- You must promote to FGS within seconds

---

### Broadcast Limitations

Implicit broadcasts are restricted:
- No background wakeups for most system events

a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result:
- Apps cannot "listen" passively anymore

---

## 5. JobScheduler & WorkManager Enforcement

Android enforces background work through:
- JobScheduler
- WorkManager (recommended)

Key behaviors:
- Work is **batched**
- Execution is **delayed**
- Timing is **inexact**

Reliability comes from **eventual execution**, not immediacy.

---

## 6. Doze Mode

When the device is idle:
- CPU access is suspended
- Network access is restricted
- Jobs are deferred

Apps are given **maintenance windows**.

You cannot opt out.

---

## 7. App Standby Buckets

Apps are ranked by usage:

- Active
- Working Set
- Frequent
- Rare
- Restricted

Lower buckets mean:
- Fewer background runs
- Longer delays
- Throttled jobs

This is automatic and user-influenced.

---

## 8. Android 12+: Background Launch Restrictions

Apps cannot:
- Launch activities from background
- Start FGS without user action

Allowed exceptions are rare and shrinking.

---

## 9. Android 13–14+: User Control Tightening

Users can:
- Restrict background usage per app
- Disable background activity

The system will enforce these choices aggressively.

---

## 10. Network & Alarm Throttling

Even allowed background work:
- Has network quotas
- Has execution caps
- Has alarm batching

Exact timing is no longer reliable.

---

## 11. What You Are Allowed To Do

### Allowed Patterns

- User-initiated foreground work
- Deferred background work via WorkManager
- Push-triggered work (FCM → WM)
- Short-lived foreground services

---

### Disallowed Patterns

- Silent background loops
- Periodic polling
- Keep-alive services
- Time-based reliability assumptions

---

## 12. Correct Architectural Response

Design for:
- Event-driven execution
- Idempotent work
- Retry-safe logic
- State persistence

Example:
```text
User action → UI → ViewModel → WorkManager
Push event → Receiver → WorkManager
Alarm → WM (never direct work)
```

---

## 13. Background Execution Decision a_phase.a_Kotlin_Language_Mastery.h_Operator_Overloading.examples.Matrix

| Requirement | Correct Tool |
|-----------|-------------|
| Immediate + visible | Foreground Service |
| Reliable execution | WorkManager |
| Exact wall-clock time | AlarmManager |
| System-level scheduling | JobScheduler |

---

## 14. Common Developer Mistakes

- Fighting the scheduler
- Expecting exact timing
- Using FGS as a loophole
- Ignoring app standby buckets
- Not persisting state

These all fail in production.

---

## 15. Mental Model to Keep

Android is not hostile.

It simply assumes:
> Your app is unimportant unless the user proves otherwise.

Design accordingly.

---

> If your feature depends on continuous background execution, it will break.

