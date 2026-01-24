# Stack vs Heap (Android Runtime)

This document explains **stack vs heap** in Android at a level required to understand **crashes, ANRs, memory leaks, and performance bugs**. This is not theory — this is how apps actually fail.

---

## 1. What the stack actually is

The **stack** is a thread-local memory region used for:
- Function call frames
- Local primitive variables
- References to heap objects
- Return addresses

Key properties:
- One stack **per thread**
- Automatically allocated and freed
- Very fast
- Fixed size

> If a thread dies, its stack is gone immediately.

---

## 2. What the heap actually is (recap)

The **heap** is process-wide memory used for:
- Objects
- Arrays
- Collections
- State

Key properties:
- Shared by all threads
- Managed by ART (Java/Kotlin heap)
- Garbage collected

The stack never owns objects — it only holds **references**.

---

## 3. Stack vs heap at a glance

| Aspect | Stack | Heap |
|------|------|------|
| Scope | Thread | Process |
| Lifetime | Function call | Until unreachable |
| Size | Fixed | Limited but flexible |
| Speed | Very fast | Slower |
| GC | No | Yes |

---

## 4. What lives where (important)

```kotlin
fun example() {
    val x = 5          // stack
    val list = listOf(1, 2, 3) // reference on stack, object on heap
}
```

- `x` lives on the stack
- `list` reference lives on the stack
- `List` object lives on the heap

Misunderstanding this causes bad memory assumptions.

---

## 5. Stack overflow (real causes)

A **StackOverflowError** happens when:
- Call depth exceeds stack size

Common causes:
- Infinite recursion
- Deep recursive tree traversal
- Accidental mutual recursion

```kotlin
fun crash() {
    crash()
}
```

This has **nothing to do with heap size**.

---

## 6. Heap overflow (OOM)

An **OutOfMemoryError** happens when:
- Heap limit is exceeded
- Native memory pushes process over limit

```kotlin
val list = mutableListOf<ByteArray>()
while (true) {
    list.add(ByteArray(10_000_000))
}
```

Stack is fine. Heap is dead.

---

## 7. Why stacks matter for ANRs

ANRs are about **threads not responding**, not memory.

Typical causes:
- Main thread blocked waiting for work
- Synchronized locks
- Blocking I/O
- Binder calls

When a thread blocks:
- Its stack frame is frozen
- System detects timeout

Stack traces in ANRs show **exactly where the thread is stuck**.

---

## 8. Stack traces: how to read them

Stack traces show:
- Call order (top = current)
- Thread state

```text
at loadData()
at fetchRemote()
at onCreate()
```

Top frame = problem.

Heap dumps won’t help you here.

---

## 9. Coroutines: stack vs heap reality

Coroutines:
- Suspend functions **do not grow the stack**
- State is stored on the heap

```kotlin
suspend fun load() {
    delay(1000)
}
```

This avoids stack overflow, but increases heap usage.

---

## 10. Compose and stack usage

Compose:
- Uses shallow call stacks
- Heavy state stored on heap

Compose issues are **almost never stack issues**.

---

## 11. Debugging: stack vs heap

If you see:
- `StackOverflowError` → recursion / stack
- `OutOfMemoryError` → heap / native
- ANR traces → stack blocking

Tools:
- Stack trace → stack problem
- Heap dump → heap problem

Wrong tool = wasted time.

---

## 12. Senior-level rules

- Stack problems are depth problems
- Heap problems are ownership problems
- ANRs are blocking problems
- Stack traces beat heap dumps for ANRs

---

## 13. Final summary

- Stack = execution
- Heap = data
- Stack dies fast, heap dies slow
- Know which one you’re debugging before acting

Understanding this distinction prevents **weeks of wasted debugging**.

