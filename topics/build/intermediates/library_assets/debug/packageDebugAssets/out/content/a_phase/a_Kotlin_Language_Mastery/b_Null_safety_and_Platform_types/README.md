# Kotlin Null Safety and Platform Types

This document explains Kotlin’s null-safety system and platform types. This is not optional knowledge. If you get this wrong, you will ship crashes.

---

## 1. The Core Idea (Why Kotlin Exists)

In Java, **everything can be null**.
In Kotlin, **null must be explicit**.

This single rule eliminates an entire class of runtime errors — if you respect it.

---

## 2. Nullable vs Non‑Nullable Types

```kotlin
val name: String = "John"   // cannot be null
val nickname: String? = null // can be null
```

- `String` → guaranteed non-null
- `String?` → nullable, must be handled

**Hard rule:** If a variable is nullable, the compiler will force you to deal with it.

---

## 3. Safe Calls (`?.`)

```kotlin
val length = nickname?.length
```

- If `nickname` is null → result is null
- If not → normal access

This replaces defensive `if (x != null)` checks.

---

## 4. Elvis Operator (`?:`)

```kotlin
val length = nickname?.length ?: 0
```

Read as: *“If null, use this instead.”*

This is one of the most used Kotlin operators for a reason.

---

## 5. Safe Casts (`as?`)

```kotlin
val value: Any = "text"
val text: String? = value as? String
```

- Fails safely
- Returns null instead of crashing

---

## 6. The Not‑Null Assertion (`!!`) — The Footgun

```kotlin
nickname!!.length
```

This means:
> “Trust me, I know this is not null.”

If you’re wrong → **runtime crash**.

**Rule:**
- Avoid in app code
- Acceptable only at system boundaries (interop, legacy APIs, tests)

If you see `!!` everywhere, the codebase is lying to the compiler.

---

## 7. `checkNotNull` and `requireNotNull` (Failing Fast, Safely)

These are better alternatives to `!!` when you expect a value to be non-null but want a more descriptive crash if it isn't.

- `requireNotNull`: Throws an `IllegalArgumentException`. Use for validating arguments passed to a function.
- `checkNotNull`: Throws an `IllegalStateException`. Use for validating internal state.

```kotlin
fun processUser(user: User?) {
    val nonNullUser = requireNotNull(user) { "User cannot be null for processing." }
    // nonNullUser is now smart-casted to User (non-nullable)
    println("Processing ${nonNullUser.name}")
}
```

**Idiom:** Prefer these over `!!` for clear, immediate feedback on invalid state or arguments.

---

## 8. `let` for Null‑Scoped Execution

```kotlin
nickname?.let {
    println(it.length)
}
```

- Executes only if not null
- `it` is smart‑casted to non‑null

This is idiomatic Kotlin.

---

## 9. Smart Casts

```kotlin
if (nickname != null) {
    println(nickname.length)
}
```

The compiler **knows** `nickname` is non‑null inside the block.

Smart casts fail when:
- Variable is mutable (`var`)
- Custom getters
- Concurrency involved

---

## 10. Lateinit (Use Sparingly)

```kotlin
lateinit var repository: UserRepository
```

- Only for `var`
- Only for non‑primitive types

Access before initialization → runtime crash.

**Typical valid use:** dependency injection.

---

## 11. Nullable Properties vs Default Values

Bad:
```kotlin
var count: Int? = null
```

Better:
```kotlin
var count: Int = 0
```

If a value logically always exists, **do not make it nullable**.

---

## 12. Platform Types (`String!`) — The Dangerous Zone

Platform types come from **Java code**.

Kotlin sees them as:
```text
String!
```

Meaning:
- Could be `String`
- Could be `String?`
- Compiler **cannot help you**

This is where most Kotlin crashes come from.

---

## 13. Platform Type Example

Java:
```java
String getName() { return null; }
```

Kotlin:
```kotlin
val name = javaApi.getName() // String!
println(name.length)        // 💥 possible crash
```

The compiler allows it — you pay at runtime.

---

## 14. Defending Against Platform Types

### Option 1: Explicit Nullable

```kotlin
val name: String? = javaApi.getName()
```

Forces proper handling.

---

### Option 2: Require Non‑Null

```kotlin
val name: String = requireNotNull(javaApi.getName())
```

Fails fast with a clear error.

---

### Option 3: Default Value

```kotlin
val name = javaApi.getName() ?: ""
```

Safe, but hides problems if abused.

---

## 15. Java Interop Annotations (Critical)

From Java, you can help Kotlin:

```java
@NonNull String getName()
@Nullable String getNickname()
```

This eliminates platform types entirely.

If you own the Java code, **annotate it**.

---

## 16. Common Null‑Safety Anti‑Patterns

❌ Using `!!` everywhere
❌ Making everything nullable
❌ Ignoring platform types
❌ Defaulting to empty strings blindly

These undo Kotlin’s biggest advantage.

---

## 17. Mental Model (Remember This)

- Nullability is part of the type
- Platform types are unsafe by default
- The compiler is your ally
- Runtime crashes mean you bypassed safety

---

## 18. Reality Check

If your Kotlin app crashes with `NullPointerException`, one of these is true:

- You used `!!`
- You trusted a platform type
- You lied to the type system

Kotlin did its job. You didn’t.

---

Master this and Kotlin stops being “nice syntax” and becomes a correctness tool.

