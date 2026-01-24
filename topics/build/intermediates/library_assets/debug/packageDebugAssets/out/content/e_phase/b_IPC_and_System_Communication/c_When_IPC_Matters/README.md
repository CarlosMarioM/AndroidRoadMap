# When IPC Matters — Android Architecture Decisions

## The core rule (no fluff)

> **IPC only matters when code runs in different processes.**

If everything runs in the same process, IPC is noise.

Most apps do **not** need IPC.

---

## What IPC actually costs

Crossing a process boundary means:
- Context switch (user ↔ kernel)
- Data marshaling (Parcel)
- Thread pool usage
- Possible blocking

Compared to in-process calls:
- Slower
- Less reliable
- Harder to debug

IPC is never free.

---

## Situations where IPC DOES matter

### 1. Multi-process apps

If you explicitly declare:
```xml
android:process=":remote"
```

You **must** use IPC.

Common reasons:
- Media playback engines
- Isolated processes
- Crash containment

---

### 2. Isolated processes

Isolated processes:
- Have no app permissions
- Cannot access app storage

IPC is the **only** communication channel.

Typical pattern:
```text
Main app ↔ AIDL ↔ Isolated service
```

---

### 3. System services

Every call to:
- ActivityManager
- PackageManager
- WindowManager

Is an IPC call.

You don’t choose IPC here — Android does.

---

### 4. App-to-app communication

Examples:
- Bound services exposed to other apps
- ContentProviders

Security + IPC are mandatory.

---

### 5. Plugins, SDKs, engines

When running:
- Third-party engines
- Script runtimes
- Untrusted SDKs

IPC gives:
- Fault isolation
- Security boundaries

---

## Situations where IPC does NOT matter

### 1. UI → ViewModel → Repository

This is **in-process**.

Using IPC abstractions here is architectural malpractice.

---

### 2. Background work inside your app

Use:
- Coroutines
- WorkManager

Not IPC.

---

### 3. Modularized apps

Gradle modules ≠ processes.

Modules share:
- Memory
- Threads
- Heap

IPC is irrelevant.

---

## Binder vs alternatives (decision rules)

| Use case | Correct tool |
|-------|-------------|
| Same process | Direct call |
| Cross-process, frequent | AIDL |
| Cross-process, simple | Messenger |
| Data sharing | ContentProvider |
| Background work | WorkManager |

If you choose Binder accidentally, you chose wrong.

---

## Performance and ANRs

IPC causes ANRs when:
- Main thread waits on Binder
- Remote process is slow or dead

Rule:
> **Never block the UI thread on IPC.**

---

## Security implications

IPC boundaries:
- Enforce permissions
- Expose attack surface

If IPC isn’t needed:
- Don’t create it

Less IPC = less risk.

---

## Overengineering trap (very common)

Mistake:
> “We’ll use AIDL to make it scalable.”

Reality:
- You added latency
- You added failure modes
- You added maintenance cost

Scalability ≠ IPC.

---

## Interview-grade mental model

- IPC matters only across processes
- Most apps are single-process
- Binder is expensive but powerful
- Isolation justifies IPC
- Overusing IPC is a design smell

If you know **when NOT to use IPC**, you’re already senior.

