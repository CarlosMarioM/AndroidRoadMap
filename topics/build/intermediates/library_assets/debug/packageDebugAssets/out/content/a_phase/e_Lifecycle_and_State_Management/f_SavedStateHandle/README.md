# SavedStateHandle

`SavedStateHandle` is **the bridge between ViewModel state and system state preservation**, allowing your UI data to survive **process death** while maintaining **ViewModel advantages**. Misusing it can lead to lost state, leaks, or excessive persistence.

---

## Core truth

- `SavedStateHandle` is injected into a ViewModel by **`ViewModelProvider`**
- Provides a **key-value map** to store small, critical state
- Automatically **restores values after process death**
- Works only for **primitive, Parcelable, or Serializable objects**
- Complements `onSaveInstanceState()`, but scoped to ViewModel

---

## Typical usage

### Initialization
```kotlin
class a_phase.c_Android_Core_Components.a_Activities.examples.MyViewModel(private val state: SavedStateHandle) : ViewModel() {
    var username: String?
        get() = state["username"]
        set(value) { state["username"] = value }
}
```

### Observing changes
```kotlin
state.getLiveData<String>("username").observe(lifecycleOwner) { name ->
    textView.text = name
}
```

Key points:
- LiveData from `SavedStateHandle` **survives configuration changes and process death**
- Ideal for UI-related transient state
- Not for large datasets or complex objects

---

## Lifecycle integration

- **Process death**: `SavedStateHandle` persists the values across process recreation
- **Configuration change**: values remain intact in the same ViewModel
- Works seamlessly with `ViewModel` lifecycle

---

## Best practices

1. **Keep it minimal**
   - Only store critical, small, serializable UI state

2. **Combine with ViewModel**
   - ViewModel handles rotation; SavedStateHandle handles process death

3. **Avoid business logic state**
   - Repository or persistent storage is better for long-term or complex data

4. **Use LiveData / StateFlow**
   - `state.getLiveData()` or `state.getStateFlow()` for reactive UI updates

5. **Always initialize defaults**
   - Prevent nulls on first launch

---

## Common pitfalls

- Storing large objects → transaction size errors
- Treating SavedStateHandle as a replacement for database
- Forgetting to provide keys consistently → state loss
- Mixing UI and domain state → unclear separation of concerns

---

## Senior-level mental model

- ViewModel = **rotation-safe, in-memory state**
- SavedStateHandle = **process-death-safe, small UI state**
- Combined, they give **resilient, reactive UI**
- Treat it as **transient survival kit** for your UI: essential data only

Think:
> If the system kills the app, what tiny state must I restore to prevent user frustration? That goes into SavedStateHandle.

