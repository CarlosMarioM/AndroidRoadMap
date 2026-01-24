# Sealed Interfaces in Kotlin

This document explains **sealed interfaces**, why they exist, and when they are the correct tool instead of sealed classes or enums. This is advanced Kotlin design—use it deliberately.

See runnable example: [`SealedInterfacesExample.kt`](examples/SealedInterfacesExample.kt)

---

## 1. What Is a Sealed Interface?

```kotlin
sealed interface UiState
```

A sealed interface defines a **closed set of implementations**, just like a sealed class—but without forcing a class hierarchy.

The compiler knows **all possible implementors** at compile time.

---

## 2. Why Sealed Interfaces Exist

Sealed classes force:
- Single inheritance
- A shared superclass

Sealed interfaces remove both constraints.

You get:
- Exhaustive `when` checks
- Multiple inheritance
- Flexible composition

---

## 3. Basic Example

```kotlin
sealed interface a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result

data class Success(val data: String) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result

data class Error(val message: String) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result
```

Usage:

```kotlin
when (result) {
    is Success -> handle(result.data)
    is Error -> showError(result.message)
}
```

No `else`. Compiler-enforced safety.

---

## 4. Sealed Interfaces with Generics

To make sealed results more reusable, you can introduce generics.

```kotlin
sealed interface a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<out T> {
    data class Success<T>(val data: T) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<T>
    data class Error(val message: String) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<Nothing>
}
```
- `out T`: Makes the type parameter covariant, which is a key part of this pattern.
- `a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<Nothing>`: `Nothing` is a subtype of all types, so `Error` can be used in any `a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<T>` context.

Usage:
```kotlin
fun handle(result: a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<User>) {
    when (result) {
        is a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Success -> println("Welcome, ${result.data.name}")
        is a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Error -> showError(result.message)
    }
}
```

This creates a type-safe, reusable wrapper for any operation that can succeed or fail.

---

## 5. Sealed Interface vs Sealed Class

| Aspect | Sealed Class | Sealed Interface |
|------|-------------|------------------|
| Inheritance | Single | Multiple |
| State | Can hold fields | No state |
| Constructors | Yes | No |
| Use case | State + behavior | Role / capability |

If you need shared state or base logic → sealed class.
If you need flexibility → sealed interface.

---

## 6. Real-World Use Case: UI State

```kotlin
sealed interface ScreenState

object Loading : ScreenState

data class Content(val items: List<String>) : ScreenState

data class Failure(val error: Throwable) : ScreenState
```

Why this works well:
- UI state is a *role*, not a base class
- Each state is independent
- Easy to extend without refactoring hierarchy

---

## 7. Combining Sealed Interfaces

```kotlin
sealed interface UiEvent
sealed interface NavigationEvent : UiEvent
sealed interface AnalyticsEvent : UiEvent
```

```kotlin
data class OpenProfile(val id: String) : NavigationEvent

data class ButtonClicked(val name: String) : AnalyticsEvent
```

This is impossible with sealed classes.

---

## 8. Multiple Inheritance (The Killer Feature)

```kotlin
sealed interface a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Retryable
sealed interface a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Loggable

sealed interface NetworkResult : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Retryable, a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Loggable
```

Now every implementation:
- Is part of a closed hierarchy
- Can be handled exhaustively
- Still composes behaviors

This is **clean modeling**, not inheritance abuse.

---

## 9. Sealed Interface vs Enum

Enums:
- Fixed values
- No payload per entry
- No composition

Sealed interfaces:
- Carry data
- Model behavior
- Scale with complexity

If you need data or evolution → enum is wrong.

---

## 10. File & Module Rules (Important)

Implementations must be:
- In the same package (Kotlin ≥ 1.5)
- Known at compile time

This preserves exhaustiveness.

---

## 11. Common Anti-Patterns

❌ Using sealed interfaces for simple constants
❌ Replacing all sealed classes blindly
❌ Over-fragmenting into tiny interfaces

If the model becomes hard to read, you lost.

---

## 12. When You Should Prefer Sealed Interfaces

✅ When multiple inheritance is needed
✅ When modeling roles or capabilities
✅ When states don’t share implementation

---

## 13. When You Should NOT

❌ When you need shared state
❌ When a base implementation makes sense
❌ When a data class alone is enough

---

## 14. Architectural Guidance

- Domain outcomes → sealed class or interface
- UI states → sealed interface
- Events → sealed interface
- Errors → sealed hierarchy (not null)

This scales without pain.

---

## 15. Mental Model

- Sealed class = *is-a with structure*
- Sealed interface = *is-a with capability*

Choose based on **constraints**, not preference.

---

## 16. Final Reality Check

If you’re modeling:
- State machines
- UI flows
- Domain results

And still using enums or nulls — you’re leaving correctness on the table.
