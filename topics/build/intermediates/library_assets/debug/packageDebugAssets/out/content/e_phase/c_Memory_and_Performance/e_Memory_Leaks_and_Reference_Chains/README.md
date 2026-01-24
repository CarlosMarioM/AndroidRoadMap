# Memory Leaks and Reference Chains (Android)

This document explains **what memory leaks really are**, how **reference chains keep objects alive**, why leaks cause **OOMs and jank**, and how senior Android engineers **find and fix them**. This is not a tips list — this is the mental model.

---

## 1. What a memory leak actually is

A **memory leak** occurs when an object that is no longer needed is **still reachable** from a GC root.

Key truth:
> Garbage Collection does not free unused objects — it frees **unreachable** objects.

If something is reachable, it will never be collected.

---

## 2. GC roots (what keeps objects alive)

Objects are considered reachable if they can be reached from a **GC root**.

Common GC roots in Android:
- Static fields
- Active threads
- Thread-local variables
- JNI global references
- System class loaders

Leaks are almost always a path from a GC root to an object that should be dead.

---

## 3. Reference chains (the real problem)

A **reference chain** is a sequence of references keeping an object alive:

```
GC Root → A → B → C → Leaked Object
```

It doesn’t matter how indirect the chain is.

If the chain exists, the object survives GC.

---

## 4. Why leaks get worse over time

Leaked objects:
- Accumulate
- Promote to old generation
- Increase GC cost
- Eventually trigger OOM

This is why many apps:
- Work fine initially
- Crash after long sessions

---

## 5. Classic Android leak patterns

### Activity / Fragment leaks

```kotlin
class a_phase.c_Android_Core_Components.a_Activities.examples.MyActivity : Activity() {
    companion object {
        var leakedActivity: a_phase.c_Android_Core_Components.a_Activities.examples.MyActivity? = null
    }
}
```

Static references outlive lifecycles.

---

### ViewModel leaks

```kotlin
class a_phase.c_Android_Core_Components.a_Activities.examples.MyViewModel : ViewModel() {
    lateinit var activity: Activity // BAD
}
```

ViewModels must never hold Views or Activities.

---

### Listener / callback leaks

```kotlin
someManager.registerListener(this)
// forget to unregister
```

The manager becomes the GC root.

---

### Coroutine leaks

```kotlin
GlobalScope.launch {
    while (true) {
        delay(1000)
    }
}
```

GlobalScope outlives everything.

---

### Flow / LiveData leaks

- Collecting without lifecycle awareness
- Hot flows with no cancellation

```kotlin
flow.collect { } // without lifecycle
```

---

## 6. JNI and native reference chains

Native code can leak Java objects:

```cpp
jobject global = env->NewGlobalRef(obj);
// Must call DeleteGlobalRef
```

JNI global refs are **GC roots**.

These leaks bypass Java tooling entirely.

---

## 7. Compose-specific leaks

Compose leaks usually come from:
- Remembering objects with hidden references
- Storing Context in state
- Long-lived lambdas capturing UI objects

```kotlin
val ctx = LocalContext.current
remember { SomeObject(ctx) } // potential leak
```

Remember stores until composition is disposed.

---

## 8. Why weak references don’t "fix" leaks

```kotlin
WeakReference(activity)
```

Weak references:
- Avoid retention
- Do not fix ownership mistakes
- Often hide real problems

If you need a weak reference, you probably need **better ownership**.

---

## 9. How seniors debug leaks

Real workflow:

1. Trigger suspected leak
2. Capture heap dump
3. Find retained object
4. Inspect reference chain
5. Identify wrong owner
6. Fix ownership

Tools:
- Android Studio Memory Profiler
- LeakCanary
- `dumpsys meminfo`

---

## 10. Reading a heap dump correctly

When inspecting a leaked object:
- Ignore object count
- Focus on **retained size**
- Follow reference paths upward

The first unexpected owner is the bug.

---

## 11. Ownership rules (non-negotiable)

- Activities own Views
- ViewModels own state
- Application owns singletons
- UI must never own long-lived objects

Violating ownership causes leaks.

---

## 12. Leak prevention patterns

Correct patterns:
- Clear references in `onCleared()`
- Use lifecycle-aware collectors
- Cancel coroutines
- Bound caches

```kotlin
override fun onCleared() {
    job.cancel()
}
```

---

## 13. Senior-level red flags

- Heap grows while UI is idle
- Old-gen size never shrinks
- Crashes after long usage
- LeakCanary reports ignored

These are **reference chain bugs**.

---

## 14. Final summary

- Leaks are reachability problems
- Reference chains explain all leaks
- GC is not broken — ownership is
- Heap dumps beat guessing
- Fix the owner, not the symptom

Understanding reference chains is **mandatory** for reliable Android apps.

