# BroadcastReceivers: static vs dynamic

See conceptual example: [`BroadcastReceiverExample.kt`](examples/BroadcastReceiverExample.kt)

BroadcastReceivers are **one of the most misunderstood Android components** because they look simple and behave dangerously.

They are not event buses, not observers, and not a background execution loophole.

---

## What a BroadcastReceiver really is

A `BroadcastReceiver` is:
- A **system callback mechanism**
- Triggered by **Intents broadcast by the system or apps**
- Short‑lived and **time‑constrained**

It is *not*:
- A background worker
- A lifecycle-aware component
- A place to run logic

A receiver exists to **react**, not to **process**.

---

## Why BroadcastReceivers exist

They exist so the system and apps can:
- Announce system events
- Notify interested parties
- Decouple senders from receivers

Examples:
- Connectivity changes
- Boot completed
- Package installed
- Battery state

Receivers are **entry points**, not execution environments.

---

## Execution constraints (critical)

Rules enforced by the system:
- Very short execution window
- Main thread execution
- Process may be killed immediately after

If you do heavy work in a receiver, Android will punish you.

---

## Static (manifest-declared) receivers

### What they are

Declared in `AndroidManifest.xml`:

```xml
<receiver
    android:name=".BootReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

They can:
- Wake your app process
- Run even when app is not started

---

### When static receivers make sense

Use only for:
- System-critical events
- App initialization triggers
- Rare, unavoidable entry points

Examples:
- `BOOT_COMPLETED`
- `PACKAGE_REPLACED`

Static receivers are **high privilege**.

---

### Modern Android restrictions

Since Android 8.0:
- Many implicit broadcasts are blocked
- Background execution is heavily limited

Static receivers are no longer general-purpose tools.

---

## Dynamic (runtime-registered) receivers

### What they are

Registered in code:

```kotlin
registerReceiver(receiver, filter)
```

Bound to:
- Context
- Lifecycle (if managed correctly)

---

### When dynamic receivers make sense

Use when:
- App is running
- UI is visible or active
- You only care while alive

Examples:
- Connectivity while screen is open
- Headset plug events
- Local app events

Dynamic receivers are **safer and preferred**.

---

### Lifecycle management (non-negotiable)

If you register dynamically, you **must** unregister:

```kotlin
override fun onStart() {
    registerReceiver(...)
}

override fun onStop() {
    unregisterReceiver(...)
}
```

Failure to unregister = crash or leak.

---

## Receivers must stay thin

Correct receiver behavior:
- Validate intent
- Delegate work
- Exit immediately

Correct pattern:
```kotlin
class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkManager.enqueue(...)
    }
}
```

Receivers should **never** do real work.

---

## Receivers + background work

Because of background limits:
- Use `WorkManager`
- Or start a foreground service (rare)

Never assume you can:
- Start threads
- Launch long coroutines
- Block

Receivers are not execution contexts.

---

## 9. Security implications

Receivers are potential attack surfaces.

Rules:
- Always validate intent data
- Avoid exported receivers
- Use explicit intents when possible
- For dynamically registered receivers, consider `Context.registerReceiver(..., RECEIVER_NOT_EXPORTED)` on API 33+ to prevent other apps from sending broadcasts.

A careless receiver is a vulnerability.

---

## Common real-world mistakes

- Doing network calls in receivers
- Forgetting to unregister dynamic receivers
- Overusing static receivers
- Treating receivers as event systems

Most bugs here show up as:
- Random crashes
- Missed events
- App killed silently

---

## Senior-level mental model

BroadcastReceivers are **interrupt handlers**, not workers.

Rules:
- React fast
- Delegate immediately
- Assume process death

If your receiver contains logic, it’s wrong.

BroadcastReceivers don’t run your app.
They knock on the door.

