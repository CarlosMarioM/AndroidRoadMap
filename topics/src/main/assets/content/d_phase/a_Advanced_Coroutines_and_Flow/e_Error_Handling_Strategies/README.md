# Flow Error Handling Strategies

> This section explains **how to model, propagate, handle, and recover from errors** in Kotlin Flow without breaking architecture, cancellation, or UI consistency.

This is written for **senior-level Android/Kotlin engineers**. No sugar-coating.

---

## 1. The Core Truth About Errors in Flow

**Flows fail fast and cancel upstream.**

- An exception **terminates the flow**
- All upstream producers are **cancelled immediately**
- Downstream operators **do not run** unless explicitly handled

```kotlin
flow {
    emit(1)
    error("boom")
    emit(2) // NEVER executed
}
```

If you treat errors as "just another state" without understanding this, you will ship bugs.

---

## 2. Error Categories (This Matters)

### 2.1 Transport Errors
Failures caused by infrastructure:
- Network timeouts
- HTTP errors
- IO exceptions
- Serialization issues

**These belong to the data layer.**

---

### 2.2 Domain Errors
Failures caused by business rules:
- Invalid credentials
- Insufficient balance
- Feature not allowed

**These are NOT exceptions.**

They are valid outcomes.

---

### 2.3 Programmer Errors (Crashes)
- NullPointerException
- IllegalStateException
- IndexOutOfBoundsException

**Never catch these intentionally.**

If you do, you are hiding bugs.

---

## 3. Rule #1: Do NOT Throw for Domain Errors

### ❌ Wrong
```kotlin
flow {
    if (!user.isActive) throw Exception("User inactive")
    emit(user)
}
```

### ✅ Correct
```kotlin
sealed interface LoginResult {
    data class Success(val user: User) : LoginResult
    data class InvalidCredentials : LoginResult
}

flow {
    if (!user.isActive) {
        emit(LoginResult.InvalidCredentials)
    } else {
        emit(LoginResult.Success(user))
    }
}
```

**Exceptions are for broken systems, not business logic.**

---

## 4. The `catch` Operator (And Its Traps)

### 4.1 What `catch` Actually Does

- Catches **upstream** exceptions only
- Does NOT catch:
  - Exceptions in downstream operators
  - Exceptions in collectors

```kotlin
flow {
    emit(1)
    error("boom")
}
.catch { emit(-1) }
```

---

### 4.2 `catch` Does NOT Resume the Flow

```kotlin
flow {
    emit(1)
    error("boom")
    emit(2)
}
.catch { emit(-1) }
.collect()
```

Emits: `1, -1`

Flow is **completed after catch**.

---

## 5. Where Error Handling Belongs (Architecture)

| Layer | Responsibility |
|-----|---------------|
| Data | Convert exceptions to domain errors |
| Domain | Expose explicit results |
| UI | Render error state |

---

## 6. Repository Pattern: Correct Error Modeling

```kotlin
sealed interface UserResult {
    data class Success(val user: User) : UserResult
    data class a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.NetworkError(val cause: IOException) : UserResult
}

fun getUser(): Flow<UserResult> = flow {
    try {
        emit(UserResult.Success(api.getUser()))
    } catch (e: IOException) {
        emit(UserResult.a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.NetworkError(e))
    }
}
```

**Notice:**
- Flow does NOT crash
- UI receives explicit state

---

## 7. `retry` vs `retryWhen`

### 7.1 Simple Retry
```kotlin
flow { api.call() }
.retry(3)
```

Use only for:
- Idempotent calls
- Transient failures

---

### 7.2 Controlled Retry
```kotlin
.retryWhen { cause, attempt ->
    cause is IOException && attempt < 3
}
```

**Never retry blindly.**

---

## 8. UI Layer: Never Catch Exceptions

### ❌ Wrong
```kotlin
viewModelScope.launch {
    try {
        repository.flow.collect { }
    } catch (e: Exception) { }
}
```

### ✅ Correct

Errors are already modeled:
```kotlin
repository.flow.collect { state ->
    render(state)
}
```

If you are catching exceptions in the UI, your architecture is broken.

---

## 9. State vs Error Events

### Persistent errors → State
- Empty state
- Offline state
- Validation errors

### One-off errors → Events
- Toasts
- Snackbars
- Dialogs

```kotlin
sealed interface UiEvent {
    data class ShowError(val message: String) : UiEvent
}
```

Use `SharedFlow` or `Channel`.

---

## 10. Cancellation Is NOT an Error

```kotlin
catch (e: Throwable) {
    if (e is CancellationException) throw e
}
```

**Never swallow cancellation.**

If you do, your app will leak coroutines.

---

## 11. Common Production Bugs

- Catching `Exception` and hiding crashes
- Using exceptions for validation
- Retrying non-idempotent calls
- Emitting error state AND throwing
- Handling errors in the UI

---

## 12. Rules of Thumb

- Domain errors are data, not exceptions
- Exceptions cancel flows
- `catch` is terminal
- Retry only when safe
- UI renders, it does not decide

---

## 13. When to Let the App Crash

Crash when:
- Programmer error
- Corrupted state
- Impossible condition

**Fail fast beats silent corruption.**

---

## 14. How This Connects

This section directly supports:
- MVI
- State vs Events
- Offline-first
- Paging 3
- Backpressure
- Testing Flows

---

> If error handling feels complicated, your boundaries are wrong — not Flow.

