# Configuration Changes

Configuration changes are **system-triggered events** that can destroy and recreate Activities and Fragments. Mismanaging them leads to:
- lost UI state
- memory leaks
- broken UX

This document explains configuration changes, lifecycle implications, and best practices.

---

## Core truth

- Android **destroys and recreates** Activities/Fragments on certain changes.
- This includes:
  - Orientation change (portrait ↔ landscape)
  - Locale change
  - Font size or display size change
  - Night mode toggle
- Any in-memory UI state not saved is lost.

---

## Lifecycle impact

On a configuration change:
1. `onPause()` → commit unsaved changes
2. `onStop()` → release unnecessary resources
3. `onDestroy()` → Activity/Fragment destroyed
4. `onCreate()` → recreated with new configuration
5. `onStart()` → visible
6. `onResume()` → interactive

Key point:
> `onSaveInstanceState()` is your only guaranteed way to preserve transient UI state.

---

## Senior-level mental model

- **Activity/Fragment recreation is normal**; design for it.
- Views are ephemeral; **state must survive recreation**.
- ViewModel persists across configuration changes; `onSaveInstanceState()` handles small UI state.
- Never try to block configuration changes as a shortcut — this creates fragile code.

---

## State management strategies

1. **ViewModel**
   - Holds UI-related data across rotations
   - Does not survive process death

2. **onSaveInstanceState()**
   - Holds small, critical UI state
   - Used with `Bundle`
   - Survives recreation but not complex objects without Parcelable/Serializable

3. **Persistent storage**
   - Room, DataStore, or Preferences for long-term data
   - Survives process death

---

## Common pitfalls

- Keeping references to Views in fields → memory leaks
- Assuming `onCreate()` is called only once
- Forgetting to restore transient state → broken UI
- Blocking configuration changes via manifest flags → fragile apps
- Overusing `android:configChanges` → manual handling usually wrong

---

## Best practices

- Design Activities/Fragments to be **recreatable at any time**
- Use ViewModel for data retention across rotation
- Persist only essential transient UI state in `onSaveInstanceState()`
- Avoid storing heavy objects in Bundle
- Never assume single configuration for layouts or resources

---

## Takeaway

Configuration changes are **not a bug**, they are a system feature. Senior developers:
- Respect lifecycle callbacks
- Separate **state** from **view hierarchy**
- Persist only what is necessary
- Treat all UI as ephemeral and rebuildable

Correct handling ensures your app survives rotation, locale change, font size adjustments, and night mode toggles gracefully.

