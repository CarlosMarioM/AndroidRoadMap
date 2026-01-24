# Services: started, bound, and foreground

See conceptual example: [`ServiceExample.kt`](examples/ServiceExample.kt)

Services are **not background threads** and **not a way to bypass lifecycle rules**.
They exist for a very narrow set of system-level use cases.

Most apps that use Services shouldn’t.

---

## What a Service really is

A `Service` is:
- A **component with no UI**
- Managed by the system
- Potentially long‑running

It is *not*:
- A worker thread
- A coroutine scope
- A general background task mechanism

A Service runs on the **main thread by default**. If that surprises you, that’s already a red flag.

---

## Why Services exist

Services were created to support:
- Work that must continue **without UI**
- Interaction with other processes
- Long‑lived system operations

They were *not* designed for:
- Simple background work
- Periodic jobs
- Network requests tied to UI

That’s why newer APIs exist.

---

## Started services

### What they are

A **started service**:
- Is started via `startService()` / `startForegroundService()`
- Runs independently of the caller
- Stops only when `stopSelf()` is called or system kills it

Lifecycle:
- `onCreate()` → `onStartCommand()` → running → `onDestroy()`

---

### When started services make sense

Use only when:
- Work must continue without UI
- Work has a **clear end condition**
- System‑level persistence is required

Examples:
- File uploads that must survive UI exit
- Media playback coordination (legacy)

---

### When started services are wrong

Do NOT use for:
- Simple background tasks
- Network requests
- Sync jobs
- Anything that can be deferred

This is why `WorkManager` exists.

---

## Bound services

### What they are

A **bound service**:
- Exposes an API via `IBinder`
- Exists only while clients are bound
- Dies when last client unbinds

Think of it as **in‑process or cross‑process dependency injection**.

---

### When bound services make sense

Use only when:
- Multiple clients need shared access
- Continuous interaction is required
- You need IPC (AIDL)

Examples:
- Media playback control
- System‑like shared resources

Most apps never need this.

---

### Why bound services are risky

Problems:
- Tight coupling
- Complex lifecycle
- Easy to leak contexts

If you don’t fully understand binding/unbinding timing, don’t use them.

---

## Foreground services

### What they are

A **foreground service**:
- Is a started service
- Shows a **persistent notification**
- Signals to the system: *“this matters to the user”*

Required for long‑running background work on modern Android.

---

### Foreground service rules (strict)

- Must show notification immediately
- User must understand why it’s running
- System is aggressive if abused
- On Android 12 (API 31) and higher, starting a foreground service from the background has new restrictions. Use `ServiceCompat.startForegroundService()` for compatibility.

Android will kill or restrict apps that misuse foreground services.

---

### Valid use cases

Examples:
- Media playback
- Navigation / GPS tracking
- Active call or VoIP
- Ongoing user‑visible tasks

If the user doesn’t notice it, it shouldn’t be foreground.

---

## Threading inside Services

Critical rule:

> **Services do not give you background threads.**

You must manage concurrency yourself:
- Coroutines
- Executors
- HandlerThreads

Blocking the main thread inside a Service will ANR the app.

---

## Services vs modern alternatives

In 2024+ Android:

- Short, deferrable work → `WorkManager`
- Periodic/background sync → `WorkManager`
- UI‑bound async work → Coroutines + ViewModel
- App‑wide state → Application scope

Services are the **last resort**, not the default.

---

## Common production mistakes

- Using Services as background workers
- Forgetting to stop started services
- Running blocking work on main thread
- Abusing foreground services to avoid limits

These lead to crashes, ANRs, and Play Store policy issues.

---

## Senior-level mental model

Services are **system contracts**, not convenience tools.

Rules:
- Use them only when the system requires them
- Keep them minimal
- Delegate work elsewhere

If you reach for a Service first, you’re probably solving the wrong problem.

Most apps should rarely — if ever — need them.

