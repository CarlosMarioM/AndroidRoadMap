# Isolated Processes — Android Runtime & Security

## What an isolated process actually is
An **isolated process** is a special kind of Android app process that:

- Has **no app UID** (not your app’s UID)
- Has **no direct access** to your app’s permissions
- Cannot access:
  - Your app’s files
  - Your app’s preferences
  - Your app’s databases

It exists **only to run code in isolation**.

Think of it as:
> “Run this code, but trust it with nothing.”

---

## Why isolated processes exist (the real reason)

Isolated processes are designed for:
- **Security boundaries**
- **Untrusted or risky work**
- **Crash containment**

Common use cases:
- Rendering web content
- Media decoding
- Executing third-party or plugin code
- Heavy background computation that must not compromise the app

---

## How isolated processes are created

You opt into isolation **explicitly** in the manifest:

```xml
<service
    android:name=".IsolatedService"
    android:process=":isolated"
    android:isolatedProcess="true" />
```

Key flags:
- `android:isolatedProcess="true"` → critical
- `android:process` → defines a separate process

This service:
- Runs in a **brand-new process**
- Has a **random UID** assigned by the system

---

## What isolated processes cannot do

By design, isolated processes:

❌ Cannot access app internal storage
❌ Cannot access shared preferences
❌ Cannot use app permissions
❌ Cannot use most system services directly

Even if your app has permissions — the isolated process does not.

---

## How isolated processes communicate

Since they have no shared memory or storage access, communication must be via:

- **Binder / AIDL**
- Messenger
- IPC callbacks

Example (AIDL conceptually):

```aidl
interface Worker {
    void doWork(String input);
}
```

Your main process:
- Binds to the isolated service
- Sends data
- Receives results

---

## Isolated vs normal multi-process

| Aspect | Normal secondary process | Isolated process |
|-----|--------------------------|------------------|
| UID | Same app UID | Random UID |
| Permissions | Yes | No |
| File access | Yes | No |
| Security boundary | Weak | Strong |
| IPC required | Optional | Mandatory |

Isolated processes are **true sandboxes**.

---

## Lifecycle characteristics

- Created on demand
- Killed aggressively under memory pressure
- No persistence guarantees

Treat them as:
> Disposable workers

Never store state inside them.

---

## Crash isolation (why this matters)

If code inside an isolated process:
- Crashes
- Deadlocks
- Runs out of memory

👉 **Your main app process survives**.

This is one of their biggest benefits.

---

## Real-world Android examples

Android itself uses isolated processes for:
- **WebView rendering**
- Media codecs
- System components that parse untrusted input

This is why a WebView crash often does **not** kill the app.

---

## Performance implications

Costs:
- Extra process startup
- IPC overhead
- No shared memory

Benefits:
- Safety
- Stability
- Fault containment

Use only when the risk justifies the cost.

---

## Common mistakes (senior-level traps)

❌ Trying to access SharedPreferences
❌ Expecting permissions to work
❌ Treating isolated processes as long-lived
❌ Putting business logic that needs app state

---

## When you SHOULD use isolated processes

✅ Executing untrusted code
✅ Parsing large or risky data
✅ Third-party engines or plugins
✅ Protecting app stability

## When you SHOULD NOT

❌ Normal background work
❌ Anything needing app permissions
❌ Simple async tasks

---

## Interview-grade summary

- Isolated processes run with **no app identity**
- They have **zero permissions**
- Communication is **IPC-only**
- They are aggressively killed
- They provide **real security and crash isolation**

If you understand isolated processes, you understand **Android’s security model beyond permissions**.

