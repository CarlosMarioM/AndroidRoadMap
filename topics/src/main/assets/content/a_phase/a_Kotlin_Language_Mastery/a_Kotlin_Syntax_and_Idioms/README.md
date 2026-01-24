# Kotlin Syntax and Idioms

This document explains Kotlin syntax and its most important idioms. The goal is not to be cute or academic, but to show how Kotlin is actually meant to be written in real-world, production-grade code.

---

## 1. Basic Syntax (The Non-Negotiables)

### Variables

```kotlin
val immutable = 10   // read-only (preferred)
var mutable = 10     // mutable (use only when needed)
```

**Idiom:** Default to `val`. If you use `var` everywhere, you are writing Java with Kotlin keywords.

---

### Functions

```kotlin
fun sum(a: Int, b: Int): Int {
    return a + b
}

// Expression body (preferred when simple)
fun sum(a: Int, b: Int) = a + b

// Default and named arguments
fun a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.getGreet(name: String, message: String = "Hello") {
    println("$message, $name!")
}

a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.getGreet("John") // Prints "Hello, Mario!"
a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.getGreet("Josh", message = "Hi") // Prints "Hi, Josh!"
```

**Idiom:** If the function fits in one expression, use expression bodies. Use default and named arguments to avoid function overloads.

---

### String Templates & Multiline Strings

```kotlin
val name = "Kotlin"
println("Hello, $name") // Simple template
println("Name length: ${name.length}") // Expression template

val multiline = """
    This is a
    multiline string.
    It preserves indentation.
""".trimIndent()
```

**Idiom:** Use templates for readable strings. Use `trimIndent()` for clean multiline strings.

See example: [`BasicSyntax.kt`](../../../../examples/kotlin_syntax/BasicSyntax.kt)

---

## 2. Classes and Constructors

### Primary Constructor

```kotlin
class a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car(val brand: String, var speed: Int)
```

No boilerplate. No getters/setters. Kotlin generates them.

**Bad (Java mindset):**
```kotlin
class a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car {
    private var speed: Int = 0
}
```

---

### Init Blocks

```kotlin
class User(val name: String) {
    init {
        require(name.isNotBlank())
    }
}
```

Use `init` for validation or setup logic.

See example: [`ClassesAndConstructors.kt`](../../../../examples/kotlin_syntax/ClassesAndConstructors.kt)

---

## 3. Inheritance and Interfaces

### Open Classes

```kotlin
open class Vehicle {
    open fun start() {}
}

class a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car : Vehicle() {
    override fun start() {}
}
```

**Idiom:** Classes are `final` by default. This is intentional. If you make everything `open`, you are fighting the language.

---

### Interfaces

```kotlin
interface Drivable {
    fun drive()
}
```

Interfaces can contain default implementations:

```kotlin
interface a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Loggable {
    fun log() = println("Logging")
}
```

See example: [`Inheritance.kt`](../../../../examples/kotlin_syntax/Inheritance.kt)

---

## 4. Extension Functions & Properties (Critical Idiom)

Extend existing classes without inheriting from them. This is how you build fluent APIs.

```kotlin
// Extension function
fun String.initials(): String {
    return this.split(' ')
        .filter { it.isNotBlank() }
        .map { it.first() }
        .joinToString("")
}

val name = "John Doe"
println(name.initials()) // "MM"

// Extension property
val String.wordCount: Int
    get() = this.split(' ').size
    
println("One two three".wordCount) // 3
```

**Idiom:** If you have a utility function that operates on a specific type, consider making it an extension function.

See example: [`ExtensionFunctions.kt`](../../../../examples/kotlin_syntax/ExtensionFunctions.kt)
---

## 5. Null Safety (Core Kotlin Feature)

### Nullable vs Non-null

```kotlin
var name: String = "John"
var nickname: String? = null
```

If you see `?`, you **must** handle null.

---

### Safe Calls

```kotlin
nickname?.length
```

---

### Elvis Operator

```kotlin
val length = nickname?.length ?: 0
```

**Idiom:** Elvis (`?:`) replaces 90% of null checks.


---

### Not-null Assertion (Avoid)

```kotlin
nickname!!.length
```

This is a runtime crash waiting to happen. Use it only when interfacing with bad APIs.

See example: [`NullSafety.kt`](../../../../examples/kotlin_syntax/NullSafety.kt)

---

## 6. Equality (== vs ===)

A small but crucial difference from Java.

- `==` checks for **structural equality** (calls `equals()`).
- `===` checks for **referential equality** (are they the same object in memory?).

```kotlin
val user1 = User(1, "John") // Assuming User is a data class
val user2 = User(1, "John")

println(user1 == user2)  // true (because it's a data class)
println(user1 === user2) // false (different objects)
```

**Idiom:** Always use `==` for comparing data. Use `===` only when you specifically need to check for object identity.

See example: [`Equality.kt`](../../../../examples/kotlin_syntax/Equality.kt)

---

## 7. Data Classes (Stop Writing Boilerplate)

```kotlin
data class User(
    val id: Int,
    val name: String
)
```

Auto-generates:
- `equals()`
- `hashCode()`
- `toString()`
- `copy()`
- `componentN()` functions (for destructuring)

**Idiom:** If a class holds data, it should probably be a `data class`.

See example: [`DataClasses.kt`](../../../../examples/kotlin_syntax/DataClasses.kt)

---

## 8. Destructuring Declarations

Unpack objects and collections into variables. Works on any object that has `componentN()` functions (like data classes, pairs, triples).

```kotlin
val user = User(1, "John")
val (id, name) = user // Destructuring the user object

for ((key, value) in myMap) {
    println("$key -> $value")
}
```
**Idiom:** Use destructuring for more readable access to data members.

See example: [`Destructuring.kt`](../../../../examples/kotlin_syntax/Destructuring.kt)

---

## 9. Control Flow as Expressions

### If as Expression

```kotlin
val max = if (a > b) a else b
```

No ternary operator. You don’t need one.

---

### When Expression (Super-powered Switch)

`when` is far more powerful than a `switch` statement.

```kotlin
val result = when (x) {
    1 -> "One"
    2 -> "Two"
    is Long -> "A Long value"
    in 3..10 -> "Between 3 and 10"
    else -> "Unknown"
}

// Can be used without an argument
val description = when {
    name == "Admin" -> "Is an administrator"
    name.startsWith("Test") -> "Is a test account"
    else -> "Guest user"
}
```

**Idiom:** `when` replaces `switch` and many chains of `if-else if`. The compiler enforces that `when` expressions are exhaustive when matching sealed classes or enums, which is a powerful safety feature.

See example: [`ControlFlow.kt`](../../../../examples/kotlin_syntax/ControlFlow.kt)

---

## 10. Collections and Functional Style

### Lambdas

```kotlin
val doubled = list.map { it * 2 }
```

`it` is the implicit parameter when there is only one.

---

### Common Operations

```kotlin
list.filter { it > 5 }
list.firstOrNull()
list.any { it == 3 }
list.all { it > 0 }
```

**Idiom:** Prefer collection operations over loops unless performance demands otherwise.

See example: [`Collections.kt`](../../../../examples/kotlin_syntax/Collections.kt)

---

## 11. Scope Functions (Used Correctly)

### let

```kotlin
user?.let {
    println(it.name)
}
```

Use for **null-safe execution**.

---

### apply

```kotlin
val car = a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car("Tesla", 0).apply {
    speed = 100
}
```

Use for **object configuration**.

---

### also

```kotlin
user.also {
    logger.log(it.name)
}
```

Use for **side effects**.

---

**Hard Truth:** If you chain `let { apply { also { } } }`, you are being clever, not readable.

See example: [`ScopeFunctions.kt`](../../../../examples/kotlin_syntax/ScopeFunctions.kt)

---

## 12. Sealed Classes (Controlled Hierarchies)

```kotlin
sealed class a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result {
    data class Success(val data: String) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result()
    data class Error(val error: Throwable) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result()
}
```

Perfect for state, UI events, and result handling.

---

## 13. Top-Level Declarations

Kotlin doesn't require everything to be in a class. You can have functions and properties at the top level of a file.

```kotlin
// In file StringUtils.kt
package com.example.utils

const val DEFAULT_ENCODING = "UTF-8"

fun isEmail(s: String): Boolean {
    // ... logic
}
```

**Idiom:** Use top-level declarations for stateless utility functions and constants.

---

## 14. Companion Objects and Constants

This is Kotlin’s answer to `static` members.

```kotlin
class Config {
    companion object {
        const val MAX_RETRIES = 3
    }
}
```

**Idiom**: Use `companion object` for functions or properties that are conceptually tied to a type but not to a specific instance of it. `const val` is a true compile-time constant.

---

## 15. Idiomatic Kotlin Mindset (Important)

- Prefer **immutability**
- Prefer **expressions over statements**
- Avoid Java-style verbosity
- Trust the type system
- If something feels repetitive, Kotlin probably has a feature for it

---

## 16. What Bad Kotlin Looks Like

```kotlin
if (value != null) {
    value.doSomething()
}
```

**Good Kotlin:**

```kotlin
value?.doSomething()
```

If your Kotlin looks like Java, you’re doing it wrong.

---

## 17. Recommended Resources (Worth Your Time)

- Official Kotlin Docs (JetBrains)
- Kotlin Koans
- "Effective Kotlin" by Marcin Moskala
- Android / Backend open-source Kotlin projects

---

This is the baseline. Master this and Kotlin stops feeling magical and starts feeling precise and sharp.
