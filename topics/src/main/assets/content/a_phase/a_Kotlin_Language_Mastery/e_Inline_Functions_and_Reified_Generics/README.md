# Inline Functions and Reified Generics

This document explains **inline functions** and **reified generics** in Kotlin. These are not syntactic sugar. They exist for **performance**, **type safety**, and **expressiveness**. Used wrong, they make code harder to debug. Used right, they remove entire classes of boilerplate.

See runnable example: [`InlineFunctionsExample.kt`](examples/InlineFunctionsExample.kt)

---

## 1. The Problem Kotlin Is Solving

On the JVM:
- Lambdas allocate objects
- Generics are erased at runtime

Inline + reified exist to fight those limitations.

---

## 2. Inline Functions — What They Are

```kotlin
inline fun logExecution(block: () -> Unit) {
    println("Start")
    block()
    println("End")
}
```

`inline` means:
> The function body is copied directly into the call site.

No function call. No lambda allocation.

---

## 3. Why Inline Exists (Real Reasons)

### Without inline
- Lambda = object allocation
- Extra stack frame

### With inline
- Zero allocation (in most cases)
- Faster execution

This matters in:
- Hot paths
- UI rendering
- Collection operations

---

## 4. Inline Functions and Lambdas

```kotlin
inline fun <T> measure(block: () -> T): T {
    val start = System.nanoTime()
    val result = block()
    println(System.nanoTime() - start)
    return result
}
```

This pattern is everywhere in the standard library.

---

## 5. Non-Local Returns (Critical Feature)

```kotlin
inline fun execute(block: () -> Unit) {
    block()
}

fun test() {
    execute {
        return // returns from test(), not lambda
    }
}
```

This only works because the function is inline.

---

## 6. noinline and crossinline

### noinline

```kotlin
inline fun example(noinline block: () -> Unit) {}
```

Use when:
- You need to store the lambda
- You need to pass it elsewhere

---

### crossinline

```kotlin
inline fun example(crossinline block: () -> Unit) {
    Runnable { block() }
}
```

Prevents non-local returns.

---

## 7. When NOT to Inline

❌ Large functions
❌ Rarely-called code
❌ Complex logic

Inlining increases bytecode size. Abuse it and you lose.

---

## 8. Inlining Properties

You can also apply `inline` to property accessors to eliminate the function call overhead for property access. This is useful for simple properties that are not just backing fields.

```kotlin
val currentTime: Long
    inline get() = System.currentTimeMillis()

var isConnected: Boolean
    inline get() = networkStatus == "connected"
    inline set(value) {
        networkStatus = if (value) "connected" else "disconnected"
    }
```

This is a more advanced feature and should be used only when the performance gain in a hot path is measurable and necessary.

---

## 9. Reified Generics — The JVM Limitation

This does NOT work:

```kotlin
fun <T> isType(value: Any): Boolean {
    return value is T // compiler error
}
```

Because generics are erased at runtime.

---

## 10. Reified Generics — The Fix

```kotlin
inline fun <reified T> isType(value: Any): Boolean {
    return value is T
}
```

Now the compiler knows `T` at the call site.

---

## 11. Why Reified Requires Inline

Because:
- Type info must be substituted at compile time
- That only works if the function is inlined

No inline → no reified. Period.

---

## 12. Real-World Example: Safe Casting

```kotlin
inline fun <reified T> Any.safeCast(): T? = this as? T
```

Usage:

```kotlin
val text = value.safeCast<String>()
```

Clean. Safe. Zero reflection.

---

## 13. Android Example: ViewModel Retrieval

```kotlin
inline fun <reified VM : ViewModel> Fragment.viewModel(): VM {
    return ViewModelProvider(this)[VM::class.java]
}
```

This is why Android KTX exists.

---

## 14. Reified vs Reflection

Reflection:
- Slow
- Unsafe
- Runtime errors

Reified:
- Compile-time safe
- Fast
- No reflection

Always prefer reified when possible.

---

## 15. Common Anti-Patterns

❌ Marking everything inline
❌ Inline + massive function bodies
❌ Using reified when passing Class<T> is clearer

Reified is powerful, not magical.

---

## 16. Mental Model

- `inline` = performance + control flow
- `reified` = runtime type safety
- Together = expressive APIs

---

## 17. Reality Check

If you use:
- `filter`, `map`, `let`, `run`
- Android KTX
- Compose

You are already relying on inline + reified.

Understanding them means **understanding Kotlin itself**.

---

## 18. Final Advice

Use inline to:
- Eliminate allocation
- Enable DSL-like APIs

Use reified to:
- Kill reflection
- Make APIs safer

