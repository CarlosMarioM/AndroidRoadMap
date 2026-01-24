# AlarmManager — Limitations, Reality, and When (Not) to Use It

> This section explains **what AlarmManager really does today**, why it is heavily restricted by the OS, its sharp limitations, and when its use is justified versus when it is a mistake.

Senior-level. Straight talk.

---

## 1. What AlarmManager Actually Is

AlarmManager is:
- A **time-based trigger mechanism**
- Used to send an intent at (roughly) a specified time

AlarmManager is **NOT**:
- A background execution engine
- A reliability guarantee
- A scheduler that survives modern Android restrictions gracefully

Its role has been **systematically reduced** since Android 6.0.

---

## 2. Historical Context (Why It Exists)

Originally, AlarmManager:
- Could wake apps freely
- Had near-exact timing
- Was commonly abused for polling

This caused:
- Massive battery drain
- Poor system health

Modern Android deliberately cripples it.

---

## 3. Doze Mode: The First Big Limitation

Introduced in Android 6.0.

Effects:
- Network access restricted
- Wakeups deferred
- Alarms batched

During Doze:
- Normal alarms **do not fire on time**
- System decides when your app wakes

This alone breaks most naive AlarmManager use cases.

---

## 4. Exact vs Inexact Alarms

### Inexact Alarms (Default)

```kotlin
set(
    AlarmManager.RTC_WAKEUP,
    triggerAtMillis,
    pendingIntent
)
```

Behavior:
- Execution time is approximate
- Alarms are batched
- Better for battery

---

### Exact Alarms (Heavily Restricted)

```kotlin
setExactAndAllowWhileIdle(
    AlarmManager.RTC_WAKEUP,
    triggerAtMillis,
    pendingIntent
)
```

Limitations:
- Strict quotas
- User-visible permission (Android 12+)
- System may still delay

Exact alarms are now **exceptional**, not normal.

---

## 5. Android 12+: Exact Alarm Permission

To use exact alarms reliably:

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

Reality:
- User can deny it
- Play Store reviews scrutinize it
- Misuse leads to rejection

Most apps **should not request this**.

---

## 6. Process Death and Reliability

AlarmManager:
- Does NOT persist execution logic
- Only delivers an intent

If:
- Your process is dead
- Receiver logic crashes

→ The work is lost unless you reschedule manually

This is why WorkManager exists.

---

## 7. AlarmManager + BroadcastReceiver Costs

Each alarm:
- Wakes the device
- Spins up a process
- Executes minimal code

Heavy work:
- Is strongly discouraged
- Will get throttled or ANR’d

Receivers must be:
- Fast
- Minimal

---

## 8. Repeating Alarms Are a Trap

```kotlin
setRepeating(...)
```

Reality:
- Repeating alarms are inexact
- Subject to batching
- Drift over time

They are **not periodic schedulers**.

Use WorkManager periodic work instead.

---

## 9. When AlarmManager Is Still Valid

Legitimate use cases:
- Calendar events
- Alarm clocks
- Timers
- User-facing reminders at a specific time

Common trait:
- **User expects exact-ish timing**
- **User understands battery impact**

---

## 10. When AlarmManager Is the Wrong Tool

Do NOT use it for:
- Background sync
- Polling servers
- Retry logic
- Periodic maintenance
- Offline-first workflows

These are WorkManager problems.

---

## 11. AlarmManager vs WorkManager

| Concern | AlarmManager | WorkManager |
|------|-------------|------------|
| Exact timing | Sometimes | No |
| Battery-friendly | No | Yes |
| Survives reboot | Manual | Yes |
| Constraints | No | Yes |
| Guaranteed execution | No | Yes |

---

## 12. Typical Architecture (Correct)

AlarmManager should:
1. Wake the app
2. Delegate immediately

```kotlin
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkManager.getInstance(context)
            .enqueue(syncWork)
    }
}
```

Alarm triggers → WorkManager does work.

---

## 13. Common Production Mistakes

- Using AlarmManager for polling
- Assuming exact timing
- Heavy work in receivers
- Forgetting reboot rescheduling
- Requesting exact alarm permission unnecessarily

---

## 14. Rules of Thumb

- AlarmManager is for **time**, not **work**
- If the task must run → WorkManager
- If the user expects a specific time → AlarmManager
- If battery matters → not AlarmManager

---

## 15. How This Connects

AlarmManager fits narrowly into:
- Notification scheduling
- User-facing alarms

It does NOT fit into:
- Offline-first
- Sync engines
- Retry systems

---

> If you think you need AlarmManager, you probably don’t.

