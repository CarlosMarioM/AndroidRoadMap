# Operator Overloading in Kotlin (Use Cases and Risks)

This document explains **operator overloading** in Kotlin: what it is, when it is genuinely useful, and when it becomes a readability and maintenance hazard. Kotlin allows it, but it does **not** encourage abuse.

See runnable example: [`OperatorOverloadingExample.kt`](examples/OperatorOverloadingExample.kt)

---

## 1. What Operator Overloading Actually Is

Operator overloading lets you define the behavior of operators (`+`, `-`, `==`, `[]`, etc.) for your own types.

```kotlin
operator fun plus(other: Int): Int
```

This is **syntax translation**, not magic.

---

## 2. How Kotlin Operators Work

This:
```kotlin
a + b
```

Is compiled as:
```kotlin
a.plus(b)
```

Every operator maps to a **specific function name**. Nothing more.

---

## 3. Commonly Overloaded Operators

| Operator | Function |
|-------|----------|
| `+` | `plus()` |
| `-` | `minus()` |
| `*` | `times()` |
| `/` | `div()` |
| `==` | `equals()` |
| `[]` | `get()` / `set()` |
| `in` | `contains()` |
| `++` | `inc()` |

If the function name looks wrong for your logic, the operator is wrong.

---

## 4. Unary Operators

Kotlin also allows overloading unary operators (`+`, `-`, `!`).

| Operator | Function |
|-------|----------|
| `+a` | `unaryPlus()` |
| `-a` | `unaryMinus()` |
| `!a` | `not()` |

```kotlin
data class Vector(val x: Int, val y: Int)

operator fun Vector.unaryMinus() = Vector(-x, -y)
operator fun Vector.unaryPlus() = Vector(x, y) // Usually just returns `this`

operator fun Boolean.not() = !this // Already defined in standard library
```

Use these when it conceptually makes sense (e.g., negating a vector or a custom numeric type).

---

## 5. Legitimate Use Case: Math / Value Objects

```kotlin
@JvmInline
value class Money(val amount: Int)

operator fun Money.plus(other: Money): Money =
    Money(amount + other.amount)
```

This is clear, predictable, and correct.

---

## 6. Use Case: Collections-like Behavior

```kotlin
operator fun <T> List<T>.plus(element: T): List<T> =
    this + listOf(element)
```

Already in the standard library — because it makes sense.

---

## 7. Use Case: Domain-Specific DSLs

```kotlin
operator fun String.times(count: Int): String =
    repeat(count)
```

Readable. Obvious. Low surprise.

---

## 8. Indexing Operators (`[]`)

```kotlin
class a_phase.a_Kotlin_Language_Mastery.h_Operator_Overloading.examples.Matrix {
    operator fun get(x: Int, y: Int): Int = TODO()
}

val value = matrix[1, 2]
```

Good when modeling **actual indexed data**.

---

## 9. Comparison Operators

```kotlin
operator fun compareTo(other: a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item): Int
```

Enables:
```kotlin
item1 > item2
```

Only valid when a **total ordering** exists.

---

## 10. The Biggest Risk: Semantic Abuse

Bad:
```kotlin
operator fun User.plus(other: User): User
```

What does this mean?
- Merge?
- Copy?
- Replace?

If you need comments to explain it, don’t overload.

---

## 11. Hidden Side Effects (Deadly)

Never do this:

```kotlin
operator fun Counter.plusAssign(value: Int) {
    logToServer()
    count += value
}
```

Operators must be:
- Predictable
- Side-effect free (or extremely obvious)

---

## 12. `plus` vs `plusAssign`

```kotlin
val a = a + b      // returns new instance
var c = 0
c += 1             // mutates
```

This distinction matters for immutability.

Prefer `+` over `+=` in immutable models.

---

## 13. Operator Overloading and Readability

Rule of thumb:
> If a junior dev can’t guess what it does, it’s wrong.

Operators remove words. Words provide clarity.

---

## 14. Testing Overloaded Operators

If you overload operators:
- Unit test them explicitly
- Treat them like public APIs

Hidden behavior here is extremely costly.

---

## 15. Kotlin vs C++ Operator Overloading

Kotlin:
- Limited set of operators
- Fixed function names
- No custom operators

This is intentional. Kotlin favors safety over cleverness.

---

## 16. When You SHOULD Use Operator Overloading

✅ Value classes
✅ Math-like domains
✅ Immutable data transformations

---

## 17. When You Should NOT

❌ Business logic
❌ Network or database operations
❌ Anything with side effects

If it touches I/O, don’t hide it behind an operator.

---

## 18. Architectural Guidance

- Operators should feel *obvious*
- Meaning must be universal
- Surprise is a design failure

---

## 19. Mental Model

- Operators are syntax sugar
- Sugar rots teeth if abused

---

## 20. Final Reality Check

If you’re excited to overload operators everywhere, stop.

Most great Kotlin code uses **very little** operators.