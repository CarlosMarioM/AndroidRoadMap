# Activity Lifecycle (cold start → background → kill)

Understanding the Activity lifecycle is **non-negotiable for a senior Android developer**. Misunderstanding it causes:
- lost state,
- memory leaks,
- crashes,
- broken UX.

This document explains the **full lifecycle**, including cold start, backgrounding, and process death.

---

## Core truth

Activities are **managed by the system**, not the app.

- System creates, stops, and destroys Activities to **manage memory and resources**.
- Lifecycle callbacks are **notifications**, not guarantees of execution order.
- Any code that assumes the Activity is always alive is broken.

---

## Cold start

Definition: **Activity created in a new process**

Lifecycle sequence:
1. `Application.onCreate()` → app initialization
2. `Activity.onCreate()` → UI setup, state restoration
3. `Activity.onStart()` → visible but not interactive
4. `Activity.onResume()` → foreground, interactive

Notes:
- Cold start is **slow** → affects perceived performance
- Minimize work in `onCreate()`
- Use lazy initialization where possible

---

## Normal lifecycle transitions

### Foreground → Background

1. User navigates away or presses Home
2. `onPause()` → commit unsaved changes, stop animations
3. `onStop()` → release resources not needed in background

Notes:
- Activity still exists in memory (unless system kills it)
- Avoid heavy background work; use Services or WorkManager

### Background → Foreground

1. `onRestart()` → before `onStart()`, optional
2. `onStart()` → visible
3. `onResume()` → interactive

Always **restore transient state** in `onStart()` / `onResume()`. Do not assume `onCreate()` is called again.

---

## Process death and recreation

- Android may **kill backgrounded processes** to reclaim memory
- When user returns:
  - `onCreate()` called with saved instance state
  - `onStart()` and `onResume()` follow

Key points:
- Always persist critical state via `onSaveInstanceState()` or ViewModel
- Never assume fields are intact after process death
- Memory leaks appear when observers or callbacks outlive lifecycle

---

## Common lifecycle pitfalls

1. Heavy initialization in `onCreate()` → cold start lag
2. Storing state only in memory → lost on process death
3. Doing background work directly in Activity → leaks
4. Not handling configuration changes → unnecessary destruction and recreation
5. Observers not removed in `onStop()` / `onDestroy()` → memory leaks

---

## Best practices

- Use **ViewModel** for UI-related state across rotations
- Use **onSaveInstanceState** for small, critical UI state
- Keep **onCreate() lightweight**
- Avoid direct background work; use coroutines with lifecycle scope, WorkManager, or Services
- Always assume your Activity can be killed anytime

---

## Senior-level mental model

Think of an Activity as **ephemeral**:
- It can be created, paused, stopped, or destroyed at any time by the system.
- Your code must survive **configuration changes, backgrounding, and process death**.
- UI state is transient; persistent state is elsewhere (ViewModel, repository, database).

Rule:
> Never trust the lifecycle to maintain your data. Design for recreation.

Understand this, and your app behaves correctly under all real-world conditions.

