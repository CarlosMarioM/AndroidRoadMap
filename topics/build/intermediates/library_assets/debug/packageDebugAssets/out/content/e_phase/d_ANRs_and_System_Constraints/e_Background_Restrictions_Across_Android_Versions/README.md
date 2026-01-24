# Background Restrictions Across Android Versions

This document explains **how Android progressively restricted background execution**, why each change was introduced, and how senior Android developers design systems that survive **all API levels**.

This is not trivia — this is why many apps "randomly stop working" on newer Android versions.

---

## The big picture

Android evolved from:

> "Apps can do work whenever they want"

To:

> "Apps may only do work when the system allows it"

Every Android release tightened the rules.

If you don’t track this evolution, you will design broken architectures.

---

## Android 5.x and below (Pre-Lollipop)

### Reality

- No real background limits
- Services could run indefinitely
- Alarms were mostly reliable
- WakeLocks widely abused

### Consequence

- Massive battery drain
- Poor system stability

This era is **why restrictions exist at all**.

---

## Android 6.0 (API 23) — Doze & App Standby

### Introduced

- **Doze Mode** (device-level idle)
- **App Standby** (unused apps throttled)

### Impact

- Background network access deferred
- Alarms delayed
- Jobs batched

### Developer shift

- Background work became **eventual**
- Immediate guarantees disappeared

---

## Android 7.0 (API 24–25) — Broadcast limits

### Introduced

- **Implicit broadcast restrictions**

### Impact

- Manifest-declared implicit receivers stopped firing
- System-only broadcasts allowed

### Required change

- Use explicit broadcasts
- Use JobScheduler / WorkManager

---

## Android 8.0 (API 26) — The big hammer

This is the **most important version change**.

### Background execution limits

- Background services **banned**
- App has seconds after going background
- Silent background starts blocked

### Foreground services

- Mandatory notification
- **5-second rule** to call `startForeground()`

### Background location limits

- Location throttled aggressively

### Architectural consequence

> Services stopped being workers.

This is where **WorkManager became mandatory**.

---

## Android 9 (API 28) — App Standby Buckets

### Introduced

- Active / Working Set / Frequent / Rare / Restricted

### Impact

- Execution frequency depends on user engagement
- Background work throttled long-term

### Key insight

> The system judges your app’s behavior over time.

---

## Android 10 (API 29) — Privacy & location

### Changes

- Background location permission split
- Stricter access to sensors
- More limits on background starts

### a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result

- Silent tracking became impossible

---

## Android 11 (API 30) — Visibility & launches

### Introduced

- Package visibility restrictions
- Background activity launch limits

### Impact

- Apps cannot freely launch UI
- Explicit user action required

---

## Android 12 (API 31–32) — Exact alarms & foreground services

### Exact alarm restrictions

- `SCHEDULE_EXACT_ALARM` permission
- User-visible justification required

### Foreground service types

- Must declare purpose (location, media, data sync)
- Mismatch can crash app

---

## Android 13 (API 33) — User control

### Changes

- Notification permission required
- Users can revoke permissions aggressively

### Consequence

- Background UX must be explicit

---

## Android 14 (API 34) — Tightened enforcement

### Changes

- Stricter background start restrictions
- Foreground service abuse detection
- Safer implicit intent handling

Android now enforces rules that previously only existed on paper.

---

## Summary table

| Android | Key restriction |
|------|----------------|
| ≤5.x | No limits |
| 6.0 | Doze, App Standby |
| 7.0 | Broadcast limits |
| 8.0 | Background service ban |
| 9.0 | Standby Buckets |
| 10 | Background location |
| 11 | Activity launch limits |
| 12 | Exact alarm permission |
| 13 | Notification permission |
| 14 | Hard enforcement |

---

## Senior design rules

- Background work is **best-effort**
- No timing guarantees
- Foreground = user-visible
- WorkManager is default
- Services expose capability, not work

---

## Mental model

> Android is not hostile to apps.

It is hostile to **silent background work**.

Design with user visibility, or expect throttling.

---

## Interview takeaway

**Every Android version reduced background freedom.**

If your architecture assumes otherwise, it is already obsolete.

