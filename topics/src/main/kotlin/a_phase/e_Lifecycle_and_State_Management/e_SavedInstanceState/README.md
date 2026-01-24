# SavedInstanceState

`SavedInstanceState` is **the system-provided mechanism** to preserve small, critical UI state across Activity and Fragment recreation, including **process death**. Misusing it leads to lost data, crashes, and bad UX.

---

## Core truth

- `onSaveInstanceState()` is called **before an Activity or Fragment is destroyed** by the system.
- The Bundle passed to `onSaveInstanceState()` is restored in `onCreate()` or `onRestoreInstanceState()`.
- Not for storing large objects or complex data structures.
- Survives **process death**, unlike in-memory ViewModel data.

---

## Lifecycle integration

### Saving state
```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("username", username)
}
```

### Restoring state
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val username = savedInstanceState?.getString("username")
}
```

Key points:
- `savedInstanceState` may be `null` on first launch
- Only small, serializable data should be stored

---

## Use cases

- Restoring **UI selections** (checkbox, scroll position)
- Temporary **form input** when orientation changes
- Current **navigation state**
- Short-lived **flags or counters**

Not for:
- Large datasets
- Database entities
- Complex objects that are better handled via ViewModel or persistent storage

---

## Best practices

1. **Keep it minimal**
   - Bundle size should be small to avoid transaction failures

2. **Prefer ViewModel for large or complex state**
   - Works across configuration changes
   - Does not survive process death unless combined with SavedStateHandle

3. **Combine with SavedStateHandle for process death**
   - Allows ViewModel to restore critical state after process death

4. **Always check for null**
   - `savedInstanceState` is null for cold start

5. **Serialize carefully**
   - Only Parcelable, Serializable, or primitives

---

## Common pitfalls

- Storing large collections → `TransactionTooLargeException`
- Ignoring `null` check → crash on first launch
- Mixing UI state with business state → unclear responsibility
- Relying solely on ViewModel for critical UI state across process death

---

## Senior-level mental model

`SavedInstanceState` is **for transient, critical UI state** that must survive destruction and recreation.

- Not a replacement for persistent storage
- Not a replacement for ViewModel
- Works best in **combination with ViewModel and SavedStateHandle**

Think of it as a **tiny survival kit**: essential data to prevent user frustration when the system kills your Activity or Fragment.

