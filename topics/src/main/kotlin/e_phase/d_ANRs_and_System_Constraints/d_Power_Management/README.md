# Power Management on Android
## Doze & App Standby Buckets

This document explains **how Android restricts your app to save battery**, what actually happens under the hood, and how to design apps that *work with* the system instead of fighting it.

This is core **senior-level Android knowledge**.

---

## Why power management exists (context)

Android’s primary constraint is **battery**.

Unlike servers:
- CPU time is expensive
- Wakeups drain power
- Background work scales badly across thousands of apps

So Android aggressively **limits background execution**.

If you don’t understand these limits, your app will:
- Miss syncs
- Drop notifications
- Behave inconsistently

---

# Doze Mode

## What Doze is

**Doze** is a system state where Android dramatically reduces background activity when the device is:

- Unplugged
- Stationary
- Screen off
- Not actively used

Introduced in **Android 6.0 (API 23)**.

Doze is **device-centric**, not app-centric.

---

## What happens in Doze

When Doze is active:

- Network access is blocked
- Wake locks are ignored
- Jobs, alarms, syncs are deferred
- Background services are stopped

Your app is essentially **paused**, even if it’s “running”.

---

## Maintenance windows

Doze is not absolute.

The system periodically opens **maintenance windows** where:

- Network is temporarily allowed
- Deferred jobs can run
- Alarms may fire

As Doze deepens:
- Windows become **shorter**
- Windows become **less frequent**

You cannot rely on exact timing.

---

## Doze vs alarms

| Alarm type | Behavior in Doze |
|----------|-----------------|
| `setExact()` | ❌ Deferred |
| `setRepeating()` | ❌ Deferred |
| `setExactAndAllowWhileIdle()` | ✅ Limited |
| `setAlarmClock()` | ✅ Allowed |

> `allowWhileIdle` is **rate-limited**. Abuse gets throttled.

---

## Doze exemptions

An app may bypass Doze only if:

- It’s whitelisted by the user
- It’s a system app
- It uses `AlarmClock`
- It runs a foreground service

User whitelisting is **not guaranteed** and should never be required for core functionality.

---

## Correct Doze strategy

- Assume background work **will be delayed**
- Use WorkManager with constraints
- Design sync to be **eventual**, not immediate

---

# App Standby Buckets

## What they are

**App Standby Buckets** classify apps based on **user engagement**.

Introduced in **Android 9 (API 28)**.

This is **app-centric**, not device-centric.

---

## The buckets

| Bucket | Meaning |
|------|--------|
| **Active** | App in use right now |
| **Working Set** | Used recently |
| **Frequent** | Used often |
| **Rare** | Used occasionally |
| **Restricted** | Heavy background abuse |

The system moves apps automatically.

---

## What buckets affect

As your app moves down buckets:

- Job execution frequency drops
- Alarms are deferred longer
- Background services are blocked
- Network access is restricted

In **Restricted**:
- Jobs may never run
- Alarms may be ignored

---

## What causes demotion

Your app gets pushed down if:

- User stops opening it
- Excessive background work
- Wakelock abuse
- Too many background starts

This is **non-deterministic by design**.

---

## How to inspect your bucket

```bash
adb shell am get-standby-bucket your.package.name
```

---

## Buckets and WorkManager

WorkManager adapts automatically:

- Defers work based on bucket
- Respects Doze + Standby
- Coalesces jobs to save power

This is why WorkManager is preferred over raw services.

---

## Doze vs Standby (critical distinction)

| Doze | Standby Buckets |
|----|----------------|
| Device state | App state |
| Triggered by inactivity | Triggered by usage patterns |
| Affects all apps | Affects per app |
| Temporary | Long-term |

They **stack**.

Your app may be restricted by both simultaneously.

---

## Push notifications & FCM

- High-priority FCM can break through Doze
- Abusing high-priority messages causes throttling
- Standby bucket still applies

Push is **not a free pass**.

---

## Senior rules (print these)

- Background execution is **best-effort**
- Time guarantees do not exist
- Foreground work is king
- WorkManager is the default
- If timing matters → notify the user

---

## Mental model

> Android does not owe your app CPU time.

It lends it — when it’s cheap.

Design accordingly.

---

## Interview takeaway

**Doze protects the device.**

**Standby Buckets judge your app’s behavior.**

If you design assuming immediate background execution, your app is already broken.

