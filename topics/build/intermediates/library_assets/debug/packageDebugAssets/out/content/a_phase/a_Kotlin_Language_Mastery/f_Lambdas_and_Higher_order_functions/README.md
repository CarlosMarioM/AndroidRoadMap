# Lambdas and Higher-Order Functions in Kotlin

This document explains **lambdas** and **higher-order functions** in Kotlin. This is not functional-programming cosplay — this is how modern Kotlin is written. If you don’t understand this well, you will write verbose, slow, or unreadable code.

See runnable example: [`LambdasExample.kt`](examples/LambdasExample.kt)

---

## 1. What a Lambda Actually Is

A lambda is an **anonymous function** that can be:
- Stored in a variable
- Passed as an argument
- Returned from a function

```kotlin
val a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.getGreet = { name: String -> "Hello $name" }
```

Type:
```kotlin
(String) -> String
```

---

## 2. Calling a Lambda

```kotlin
a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.getGreet("Mario")
```

Or explicitly:

```kotlin
a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.getGreet.invoke("Mario")
```

Both are equivalent. The first is idiomatic.

---

## 3. Higher-Order Functions

A higher-order function:
- Takes a function as a parameter, or
- Returns a function

```kotlin
fun execute(block: () -> Unit) {
    block()
}
```

This is foundational Kotlin.

---

## 4. Function Types (Read This Carefully)

```kotlin
(Int, Int) -> Int
```

Means:
- Takes two `Int`
- Returns an `Int`

Examples:

```kotlin
val sum: (Int, Int) -> Int = { a, b -> a + b }
```

---

## 5. Type Aliases for Function Types

When function types get complex, they hurt readability. Use `typealias` to give them a clear name.

```kotlin
typealias a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.OnItemClickListener = (item: a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item, position: Int) -> Unit
```

Now, instead of this:
```kotlin
fun setListener(listener: (a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item, Int) -> Unit) { ... }
```

You write this:
```kotlin
fun setListener(listener: a_phase.a_Kotlin_Language_Mastery.`f_Lambdas_and_Higher-order_functions`.examples.OnItemClickListener) { ... }
```

This is self-documenting and much easier to maintain.

---

## 6. Trailing Lambda Syntax

```kotlin
execute {
    println("Hello")
}
```

If the **last parameter** is a lambda, it goes outside parentheses.

This is why Kotlin reads cleanly.

---

## 7. `it` — Implicit Lambda Parameter

```kotlin
list.map { it * 2 }
```

Rules:
- Only when there is **one parameter**
- Use it sparingly

If it hurts readability, name it.

---

## 8. Multi-Line Lambdas

```kotlin
val result = run {
    val a = 10
    val b = 20
    a + b
}
```

Last expression is the return value.

---

## 9. Lambdas vs Anonymous Functions

Anonymous function:

```kotlin
val sum = fun(a: Int, b: Int): Int {
    return a + b
}
```

Use when:
- You need explicit `return`
- You want clearer control flow

Otherwise, lambdas win.

---

## 10. Closures (Captured Variables)

```kotlin
var count = 0

val increment = {
    count++
}
```

The lambda **captures** `count`.

Important:
- Captured vars are heap-allocated
- Overuse hurts performance

---

## 11. Common Higher-Order Functions

```kotlin
list.map { it * 2 }
list.filter { it > 5 }
list.forEach { println(it) }
list.firstOrNull()
list.any { it == 3 }
```

This replaces 90% of loops.

---

## 12. Lambdas and Performance (Hard Truth)

- Lambdas allocate objects
- Captured variables allocate more

Mitigations:
- `inline` functions
- Avoid capturing mutable state

Kotlin is expressive, not free.

---

## 13. Returning Lambdas

```kotlin
fun multiplier(factor: Int): (Int) -> Int {
    return { value -> value * factor }
}
```

This is powerful — and dangerous if abused.

---

## 14. Lambdas with Receivers

```kotlin
fun buildString(block: StringBuilder.() -> Unit): String {
    return StringBuilder().apply(block).toString()
}
```

Inside the lambda, `this` is `StringBuilder`.

This powers:
- DSLs
- Builders
- Compose

---

## 15. Real-World Example: Configuration

```kotlin
val car = a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car().apply {
    speed = 100
    color = "Red"
}
```

Clean. Scoped. No noise.

---

## 16. When Lambdas Hurt Readability

❌ Deeply nested lambdas
❌ Anonymous logic-heavy blocks
❌ Clever one-liners no one can debug

Readability beats cleverness.

---

## 17. Lambdas vs Loops

Loops:
- Faster in hot paths
- Easier to debug step-by-step

Lambdas:
- Declarative
- Safer
- More expressive

Choose intentionally.

---

## 18. Mental Model

- Lambdas = behavior as data
- Higher-order functions = abstraction tool
- Inline = performance escape hatch
