# Data Classes vs Sealed Classes vs Value Classes

This document explains **what these three constructs are**, **what they are for**, and—more importantly—**when not to use them**. Kotlin gives you sharp tools. Misuse them and your design gets worse, not better.

See runnable example: [`DataSealedValueClasses.kt`](examples/DataSealedValueClasses.kt)

---

## 1. The Big Picture

| Construct | Purpose | Think of it as |
|---------|--------|----------------|
| `data class` | Hold data | A transparent container |
| `sealed class` | Model states / variants | A closed hierarchy |
| `value class` | Wrap a value safely | A zero-cost type alias |

If you confuse these, your architecture will rot quietly.

---

## 2. Data Classes

### What They Are

```kotlin
data class User(
    val id: Int,
    val name: String
)
```

A `data class` is for **plain data**.

Kotlin auto-generates:
- `equals()`
- `hashCode()`
- `toString()`
- `copy()`
- `componentN()`

---

### When to Use Data Classes

✅ DTOs (network, database)
✅ UI models
✅ Immutable state objects

If the class answers the question *“What data does this contain?”* — data class.

---

### When NOT to Use Data Classes

❌ Classes with behavior-heavy logic
❌ Domain entities with identity
❌ Objects with invariants that must never break

Bad example:
```kotlin
data class BankAccount(
    val balance: Double
)
```

This allows invalid states. `copy()` will happily break your rules.

---

### Hard Truth About `copy()`

`copy()` is powerful **and dangerous**.

If copying an object can violate business rules, **do not use a data class**.

---

## 3. Sealed Classes

### What They Are

```kotlin
sealed class a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result {
    data class Success(val data: String) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result()
    data class Error(val throwable: Throwable) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result()
}
```

A `sealed class` defines a **closed set of subclasses**.

The compiler knows **all possible cases**.

---

### Why Sealed Classes Exist

They let you model **state**, **outcomes**, and **events** safely.

```kotlin
when (result) {
    is a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Success -> handle(result.data)
    is a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Error -> showError()
}
```

No `else`. No forgotten cases. Compile-time safety.

---

### Common Use Cases

✅ UI states (`Loading`, `Content`, `Error`)
✅ Network results
✅ Navigation events
✅ Domain use-case outcomes

---

### Sealed Class vs Enum

Enums:
- No state per entry
- Limited extensibility

Sealed classes:
- Can carry data
- Can have logic
- Far more expressive

If you need data → enum is wrong.

---

### Sealed Interfaces

```kotlin
sealed interface UiState
```

Use when:
- You want multiple inheritance
- You don’t need shared state
- They enable more flexible type composition, allowing a type to implement multiple sealed interfaces without a common base class.

---

## 4. Value Classes (Inline Classes)

### What They Are

```kotlin
@JvmInline
value class a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId(val value: String)
```

A **type-safe wrapper** around a single value.

At runtime (usually):
- No allocation
- No wrapper object

---

### Why Value Classes Matter

This:
```kotlin
fun loadUser(id: String)
```

Allows bugs:
```kotlin
loadUser(email)
```

This does not:
```kotlin
fun loadUser(id: a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId)
```

You get **compile-time safety at zero runtime cost**.

---

### When to Use Value Classes

✅ IDs (`a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId`, `OrderId`)
✅ Units (`Meters`, `Kilograms`)
✅ Domain primitives

If it’s a primitive that means something — wrap it.

---

### Value Class Factory Example

```kotlin
@JvmInline
value class a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId(val value: String) {
    companion object {
        fun fromString(id: String): a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId {
            require(id.isNotBlank()) { "User ID cannot be blank" }
            return a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId(id)
        }
        fun fromInt(id: Int): a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId = a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId(id.toString())
    }
}
```

**Idiom:** Use companion objects to provide controlled construction or utility functions for value classes.

---

### Limitations (Important)

- Single property only
- Cannot be nullable internally
- Some boxing on generics / JVM boundaries (e.g., `val id: a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId? = null` or when `a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId` is used as a generic `T` in `List<T>`)

Still worth it.

---

## 5. Comparing the Three (Real Guidance)

### Question: "Am I just holding data?"
→ `data class`

---

### Question: "Am I modeling possible states or outcomes?"
→ `sealed class`

---

### Question: "Am I giving meaning to a primitive?"
→ `value class`

---

## 6. Composing Them Together (Correctly)

```kotlin
@JvmInline
value class a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId(val value: String)

sealed class LoadUserResult {
    data class Success(val user: User) : LoadUserResult()
    data class Error(val reason: String) : LoadUserResult()
}

data class User(
    val id: a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId,
    val name: String
)
```

This is **idiomatic, safe, and expressive Kotlin**.

---

## 7. Common Misuse Patterns

❌ Using `data class` everywhere
❌ Using enums instead of sealed classes
❌ Passing raw `String` IDs everywhere
❌ Modeling errors as `null`

These are design smells.

---

## 8. Architectural Rule of Thumb

- Data layer → `data class`
- Domain layer → `sealed class` + `value class`
- UI layer → sealed UI states

This separation scales.

---

## 9. Final Reality Check

If your Kotlin models:
- Allow impossible states
- Require comments to explain validity
- Crash due to wrong parameters

You picked the wrong construct.

Kotlin gives you the tools. Use the right one.

