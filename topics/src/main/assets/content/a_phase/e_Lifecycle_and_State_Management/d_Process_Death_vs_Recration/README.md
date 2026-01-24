# Process Death vs Recreation

Understanding **process death** is crucial for building robust Android apps. Many developers confuse **configuration-based recreation** with **process death**, leading to lost data and crashes.

This document clarifies the distinction and provides a senior-level approach.

---

## Core truth

- **Configuration changes** → Activity/Fragment is destroyed and recreated in the **same process**.
- **Process death** → Android kills the **entire app process** in the background to reclaim memory.
- When returning to the app after process death, **all in-memory state is gone**, including ViewModels.

---

## Lifecycle behavior

### Configuration change
- `onDestroy()` → Activity destroyed
- `onCreate()` → Activity recreated with new configuration
- ViewModel persists across rotation
- `onSaveInstanceState()` restores small transient UI state

### Process death
- System kills the process completely
- Returning to app triggers a **cold start**
- ViewModels are gone
- Only data saved in **persistent storage or Bundle** survives

Key difference:
> ViewModel survives rotation, but **not process death**.

---

## Surviving process death

### 1. onSaveInstanceState()
- Bundle can store small, serializable UI state
- Survives process death
- Limitations: not suitable for large objects, complex data

### 2. Persistent storage
- Room, DataStore, SharedPreferences
- Always survives process death
- Best for critical app data

### 3. Hybrid approach
- ViewModel + savedStateHandle
- Allows restoring state after process death if used with SavedStateRegistry

---

## Senior-level mental model

- **Configuration changes** → normal lifecycle, predictable, ViewModel intact
- **Process death** → unpredictable, all in-memory state lost
- Always design for process death
- Use ViewModel for in-memory UI state across rotations
- Use Bundle or persistent storage for critical data that must survive process death

---

## Common pitfalls

1. Assuming ViewModel survives process death → lost data
2. Forgetting to save transient UI state in Bundle → broken restoration
3. Storing large objects in Bundle → crash or ANR
4. Treating configuration change restoration as sufficient for all scenarios
5. Ignoring app death caused by background memory pressure → inconsistent UX

---

## Best practices

- Treat all UI state as **ephemeral**
- Use **ViewModel + savedStateHandle** for rotation and partial state restoration
- Persist critical data externally (database, DataStore) for process death
- Always test app behavior under low-memory kills (via Developer Options or ADB)
- Never rely solely on Activity/Fragment fields for critical data

---

## Takeaway

Process death is the **real threat**; configuration change is a convenience feature. Senior developers:
- Separate transient UI state from persistent data
- Handle recreation via ViewModel and savedStateHandle
- Persist essential data externally
- Design for **ephemeral memory**, survive cold starts gracefully

