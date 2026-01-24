# Activities: role, lifecycle, and task stack

See conceptual example: [`ActivityLifecycleExample.kt`](examples/ActivityLifecycleExample.kt)

Activities are one of the most misunderstood Android components.
They are **not screens**, **not controllers**, and **not app state holders**.

Misusing Activities leads to:
- broken navigation
- memory leaks
- duplicated logic
- unpredictable lifecycle bugs

---

## What an Activity really is

An `Activity` is:
- A **UI entry point**
- A **window host**
- A **lifecycle owner tied to the system**

It is *not*:
- A business logic layer
- A state container
- A navigation manager

If logic lives in Activities, the architecture is already compromised.

---

## The real role of an Activity

Correct responsibilities:
- Inflate UI (or host Compose)
- Bind UI to state
- Handle system-level events (permissions, intents)
- Delegate work to ViewModels / controllers

Incorrect responsibilities:
- Fetching data
- Holding domain state
- Managing coroutines manually
- Coordinating app-wide behavior

Activities should be **thin**.

---

## Activity lifecycle (what actually matters)

The lifecycle exists because **Android owns your process**, not you.

Key states:

### `onCreate`
- One-time initialization
- View setup
- Dependency injection

Should NOT:
- Start long-running work
- Depend on UI visibility

---

### `onStart` / `onResume`

- Activity becomes visible / interactive
- Safe point for UI-related work

Use cases:
- Start observing UI state
- Resume animations

---

### `onPause`

- UI losing focus
- Must be fast

Use cases:
- Pause animations
- Save lightweight UI state

Never block here. Ever.

---

### `onStop`

- Activity fully hidden

Use cases:
- Release UI resources
- Stop UI-bound observers

---

### `onDestroy`

- Activity is finishing OR being recreated

Important:
- Not guaranteed for process death
- Never rely on it for critical persistence

---

## Configuration changes (the trap)

Rotation, locale, font scale, dark mode:
- Destroy and recreate Activity

What survives:
- ViewModel
- SavedInstanceState (limited)

What does NOT:
- Views
- References to Context
- UI state unless explicitly handled

If your Activity assumes it won’t be recreated, it’s broken.

---

## SavedInstanceState (what it’s for)

`savedInstanceState` is for:
- Small UI state
- Scroll positions
- Selected tabs

It is NOT for:
- Business data
- Network results
- Large objects

Abusing it leads to crashes and ANRs. For more robust, lifecycle-aware UI state persistence across process death, especially when integrated with ViewModels, consider using `SavedStateHandle`.

---

## The task stack (critical but ignored)

A **task** is a stack of Activities.

Rules:
- Back button pops the stack
- System manages task switching
- Activities can move between tasks

This is OS-level behavior, not app-level.

---

## Launch modes (what they actually do)

### `standard`
- Default
- New instance every time

### `singleTop`
- Reuses top if same Activity
- Otherwise creates new

### `singleTask`
- One instance per task
- Clears above it

### `singleInstance`
- Own task
- Rarely justified

Using the wrong launch mode breaks navigation.

---

## Intents and entry points

Activities can be started by:
- Your app
- Other apps
- The system

This means:
- Never assume a clean state
- Always validate intent data
- Activities must be defensive

Activities are **public-facing APIs**.

---

## Activities vs Fragments (clear separation)

Activity:
- Hosts windows
- Owns navigation root

Fragment:
- Represents UI pieces
- Handles screen-level UI logic

If an Activity manages multiple UI states manually, Fragments or Compose navigation are missing.

---

## Compose changes the UI, not the role

With Compose:
- Activity still owns lifecycle
- Still recreated on config change
- Still tied to task stack

Compose removes views, not system rules.

---

## Common real-world mistakes

- Treating Activity as a ViewModel
- Launching coroutines tied to Activity manually
- Storing state in fields
- Ignoring task stack behavior
- Fighting configuration changes

Android always wins.

---

## Senior-level mental model

An Activity is a **system-managed container**, nothing more.

Design rules:
- Keep it thin
- Assume recreation
- Delegate everything
- Respect the task stack

If your app logic depends on an Activity surviving, it will fail.

Activities don’t define your app.
They host it.

