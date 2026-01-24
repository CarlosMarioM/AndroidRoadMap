# Collections and Immutability in Kotlin

This document explains **Kotlin collections** and **immutability** the way they are meant to be used. If you mutate everything by default, you are negating one of Kotlin’s biggest advantages: predictable, safe state.

See runnable example: [`CollectionsImmutabilityExample.kt`](examples/CollectionsImmutabilityExample.kt)

---

## 1. The Core Principle

**Immutability is the default. Mutation is the exception.**

Mutable state:
- Is harder to reason about
- Breaks concurrency
- Causes UI bugs

Kotlin gives you tools to avoid it — if you actually use them.

---

## 2. Read-Only vs Mutable Collections

```kotlin
val readOnly: List<Int> = listOf(1, 2, 3)
val mutable: MutableList<Int> = mutableListOf(1, 2, 3)
```

Important truth:
- `List` is **read-only**, not deeply immutable
- `MutableList` allows mutation

If you expose `MutableList`, you’ve lost control.

---

## 3. Prefer Read-Only in APIs

```kotlin
fun process(items: List<a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item>) { }
```

Not:

```kotlin
fun process(items: MutableList<a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item>) { }
```

**Rule:**
- Accept read-only
- Mutate internally if needed

---

## 4. Creating New Collections Instead of Mutating

Bad:
```kotlin
items.add(newItem)
```

Good:
```kotlin
val updated = items + newItem
```

This creates a new list.

Yes, allocation happens. No, it usually doesn’t matter.

---

## 5. Common Immutable Operations

```kotlin
val filtered = list.filter { it > 5 }
val mapped = list.map { it * 2 }
val combined = list1 + list2
val removed = list - element
```

Original collections remain untouched.

---

## 6. When Mutation Is Acceptable

Mutation is fine when:

✅ Inside a small, private scope
✅ In performance-critical code
✅ While building a collection

Example:

```kotlin
val result = mutableListOf<Int>()
for (i in 1..100) {
    result.add(i * 2)
}
```

Expose it as `List`, not `MutableList`.

---

## 7. Collection Builders (`buildList`, `buildSet`, `buildMap`)

These functions allow you to build mutable collections efficiently within a lambda, and then return an immutable collection as the result. This combines the performance of mutable operations during construction with the safety of immutability for the final collection.

```kotlin
val squares = buildList {
    for (i in 1..5) {
        add(i * i)
    }
}
// squares is now an immutable List<Int>: [1, 4, 9, 16, 25]
```

This is an idiomatic way to construct complex collections.

---

## 8. Defensive Copies

```kotlin
class User(private val _roles: List<String>) {
    val roles: List<String> = _roles.toList()
}
```

This prevents external mutation.

If you skip this, bugs will find you.

---

## 9. Persistent Data Structures (Conceptual)

Kotlin’s standard library does **not** provide true persistent collections.

But immutability + copying still gives:
- Predictable state
- Easier debugging
- Safe UI updates

If you need real persistence, look at:
- kotlinx.collections.immutable

---

## 10. Collections and Equality

```kotlin
listOf(1, 2, 3) == listOf(1, 2, 3) // true
```

Structural equality.

Mutation breaks expectations in state comparison.

This matters for:
- UI diffing
- State reducers
- Testing

---

## 11. Data Classes + Collections

```kotlin
data class State(
    val items: List<a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item>
)
```

Update pattern:

```kotlin
state.copy(items = state.items + newItem)
```

This is idiomatic, safe, and predictable.

---

## 12. Common Anti-Patterns

❌ Exposing `MutableList` publicly
❌ Mutating collections passed as parameters
❌ Sharing mutable collections across layers

These create invisible side effects.

---

## 13. Performance Reality Check

- Most apps are not bottlenecked by allocations
- UI bugs cost more than memory
- Optimize only when profiling says so

Premature mutation is worse than premature optimization.

---

## 14. Collections in Multithreading

Immutable collections:
- Are thread-safe by default
- Require no synchronization

Mutable collections:
- Need locks
- Break easily under concurrency

Immutability scales. Mutation doesn’t.

---

## 15. Kotlin vs Java Mindset

Java habit:
```java
list.add(x);
```

Kotlin habit:
```kotlin
list + x
```

One mutates. One models change.

---

## 16. Mental Model

- Collections represent *state*, not storage
- Changes produce new state
- Old state stays valid

This unlocks safe UI and domain modeling.

