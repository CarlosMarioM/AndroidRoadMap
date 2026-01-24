# Launch Modes and Task Behavior

Activity launch modes and task behavior define **how your app’s screens are instantiated, stacked, and navigated**. Misunderstanding these leads to broken navigation, duplicate Activities, and user frustration.

This document explains what launch modes are, how they affect tasks, and what a senior developer needs to know.

---

## Core truth

- Android maintains a **task stack** for user navigation.
- Launch modes control **Activity creation, reuse, and back stack behavior**.
- Misusing them is a common source of navigation bugs.

---

## Activity launch modes

Four primary modes:

### 1. standard (default)
- Creates a **new instance** every time the Activity is started.
- Pushes onto the current task stack.
- `onCreate()` called each time.
- Use for **stateless or reusable screens**.

### 2. singleTop
- Reuses the **topmost instance** if it matches.
- If top is not the same, new instance is created.
- `onNewIntent()` called on the reused instance.
- Use when **duplicate top Activities are undesirable**.

### 3. singleTask
- Only **one instance per task**.
- Reuses the existing instance, clearing all above it.
- `onNewIntent()` called.
- Use for **entry points that must be unique**, like main dashboards.
- Dangerous if overused; can cause unintended clears of back stack.

### 4. singleInstance
- Only one instance **across all tasks**.
- Activity always lives in its **own task**.
- Rarely needed; mostly for system apps or special scenarios.

---

## Task behavior fundamentals

- **Tasks** represent what the user sees as “app screens” in Recents.
- Activities are stacked in **LIFO order**.
- Launch modes manipulate the stack:
  - standard → push
  - singleTop → reuse top
  - singleTask → bring to front and clear above
  - singleInstance → isolated task

Understanding task behavior is critical for **deep links and multi-window** support.

---

## Deep links & launch mode implications

- Standard → deep link creates new instance each time
- SingleTop → avoids duplicate top
- SingleTask / SingleInstance → reuses existing task

Incorrect mode choice leads to:
- Duplicate screens
- Unexpected back navigation
- Lost state

---

## Senior-level mental model

1. **Default to standard** unless a specific reason exists.
2. **singleTop** for duplicate prevention when launching from notifications or widgets.
3. **singleTask** for unique entry points or main dashboards.
4. **singleInstance** almost never, only when task isolation is required.

Always reason about:
- How back presses behave
- How notifications and deep links interact
- What happens on process death

---

## Common mistakes

- Using singleTask to “fix” navigation → breaks stack
- Using singleInstance without necessity → confuses multi-window
- Assuming singleTop prevents all duplicates → only top instance
- Not testing deep links with different launch modes

Remember: **launch modes manipulate the stack, they do not fix architecture**.

Design your navigation first, then apply launch modes sparingly and deliberately.

