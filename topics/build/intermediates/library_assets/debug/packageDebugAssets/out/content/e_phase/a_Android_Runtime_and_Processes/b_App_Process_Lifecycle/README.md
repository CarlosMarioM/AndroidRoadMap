# App Process Lifecycle — Android Runtime Internals

## What an "app process" actually is
An **app process** is a **Linux process** created by **Zygote** to host your application code.

It is **not tied to an Activity**, a screen, or the UI.

Key rule (burn this in):
> **Activities come and go. Processes live and die based on system pressure.**

---

## How an app process is born

### Step-by-step (real flow)

1. User launches app (Launcher intent)
2. `ActivityManagerService (AMS)` checks:
   - Is there already a process for this package?
3. If not:
   - AMS asks **Zygote** to `fork()`
4. Child process:
   - Sets UID / SELinux domain
   - Initializes `ActivityThread`
   - Loads app APK
   - Instantiates `Application`
   - Calls `Application.onCreate()`

This is your **true entry point**.

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate() // process-level init
    }
}
```

---

## One process, many components

A single app process can host:
- Multiple Activities
- Multiple Services
- Multiple BroadcastReceivers
- Multiple ContentProviders

They **share**:
- Heap
- Threads
- Static state

They **do not** each get their own process by default.

---

## Process lifetime is NOT deterministic

Android may kill your process **at any time** when:
- Memory pressure exists
- App goes to background
- Another app needs RAM

No callback is guaranteed.

```text
onStop() ≠ safe
onDestroy() ≠ safe
```

---

## Process importance levels (this matters)

Android assigns **importance** to each process.

### From most → least important

1. **Foreground process**
   - Visible Activity
   - Running foreground Service
   - Actively interacting with user

2. **Visible process**
   - Activity visible but not focused

3. **Service process**
   - Background Service (non-foreground)

4. **Cached process**
   - No active components
   - Kept for faster relaunch

The lower you are, the easier you die.

---

## Cached processes (the illusion of persistence)

Cached processes:
- Have no visible components
- Remain in RAM temporarily
- Can be killed **without warning**

This is why apps sometimes "resume instantly".

Do not trust it.

---

## Configuration changes vs process death

These are NOT the same.

### Configuration change
- Activity recreated
- Process stays alive
- Memory preserved

### Process death
- Everything gone
- App restarts from scratch
- `Application.onCreate()` runs again

You must design for **process death**, not rotation.

---

## Death scenarios you must support

Your process can die when:
- App is backgrounded
- Screen is off
- User opens camera/game
- OEM kills aggressively

If state isn’t persisted → it’s gone.

---

## What survives process death

❌ In-memory objects
❌ Static fields
❌ Singleton caches

✅ Disk (Room, DataStore)
✅ SavedStateHandle
✅ Intent extras

---

## Multi-process apps (advanced)

You *can* opt into multiple processes:

```xml
<service
    android:name=".RemoteService"
    android:process=":remote" />
```

But this means:
- Separate heaps
- No shared memory
- IPC required (Binder)

Use only when necessary.

---

## Process restart flow

When a killed app is reopened:

1. New process forked
2. `Application.onCreate()` called again
3. Activity restored from saved state

Your app must tolerate this **at any point**.

---

## Common senior-level mistakes

❌ Treating process as permanent
❌ Using singletons for critical state
❌ Doing heavy work in `Application.onCreate()`
❌ Assuming `onDestroy()` is reliable

---

## Performance implications

- Heavy process init = slow cold start
- Large static graphs = memory pressure
- Too many background services = kills

Your architecture directly affects survivability.

---

## Interview-grade mental model

- Zygote creates processes
- AMS controls lifecycle
- Process ≠ Activity
- Death is normal
- Persistence is mandatory
- Foreground importance buys survival, not immortality

If you design assuming process death, your app becomes **robust by default**.

