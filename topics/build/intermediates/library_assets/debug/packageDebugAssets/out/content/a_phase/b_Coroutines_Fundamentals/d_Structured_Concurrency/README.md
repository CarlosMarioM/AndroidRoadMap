# Structured concurrency

See runnable example: [`StructuredConcurrencyExample.kt`](examples/StructuredConcurrencyExample.kt)

Structured concurrency is **the core reason Kotlin coroutines are usable at scale**.
Without it, coroutines degrade into untracked background work — worse than threads.

This is not an optional concept. It is the foundation.

---

## What structured concurrency means

Structured concurrency enforces this rule:

> **The lifetime of concurrent work must be bounded by the lifetime of its parent.**

In practice:
- Every coroutine has a parent
- Parents wait for children
- Cancellation flows downward
- Errors propagate upward

If any of these are missing, structure is broken.

---

## The problem it solves (real-world)

Before structured concurrency:
- Background work outlives screens
- Errors disappear silently
- Cancellation is manual and fragile
- Memory leaks are common

With structured concurrency:
- Work is scoped
- Lifetimes are explicit
- Failure is predictable
- Cleanup is automatic

This is not about elegance. It’s about **control**.

---

## Coroutine builders that enforce structure

### `coroutineScope`

Creates a new scope and **waits for all children**.

```kotlin
suspend fun load() = coroutineScope {
    val a = async { fetchA() }
    val b = async { fetchB() }
    a.await() + b.await()
}
```

Rules:
- If one child fails → scope fails
- All children are cancelled

This is the default and safest behavior.

---

### `supervisorScope`

Allows **independent failure**.

```kotlin
supervisorScope {
    launch { loadA() }
    launch { loadB() }
}
```

Rules:
- One child fails → others keep running
- Parent still controls lifetime

Use only when partial failure is acceptable.

---

## Parent–child relationship

Key guarantees:
- Parent cannot complete until children complete
- Cancelling parent cancels all children
- Child failure affects parent (unless supervised)

This guarantees **no orphan coroutines**.

If work escapes the parent, you broke structure.

---

## What breaks structured concurrency

These are structural violations:

### `GlobalScope`
- No parent
- No cancellation
- No error propagation

Equivalent to raw threads.

---

### Launching work that outlives the caller

```kotlin
suspend fun bad() {
    CoroutineScope(Dispatchers.IO).launch {
        doWork()
    }
}
```

The caller has no control. This is architectural damage.

---

### Fire-and-forget inside suspend functions

If a suspend function launches background work and returns immediately, it lies.

Suspend functions must represent **completed work**, not started work.

---

## Error propagation rules

Default behavior:
- Child throws exception
- Parent scope cancels
- Siblings cancel
- Exception bubbles up

This is intentional.

Swallowing errors or isolating everything with supervisors hides failures and delays crashes until production.

---

## Cancellation propagation

Cancellation is:
- Automatic
- Hierarchical
- Cooperative

```kotlin
parentJob.cancel()
```

Results in:
- All children receiving cancellation
- Suspension points throwing `CancellationException`

Ignoring cancellation breaks structure.

---

## Structured concurrency vs manual management

Manual approach:
- Track jobs
- Cancel them manually
- Handle races
- Handle errors per job

Structured approach:
- Let the scope manage it
- Fewer states
- Fewer bugs

Less code. More guarantees.

---

## Android-specific implications

### ViewModels

`viewModelScope` enforces structure:
- All coroutines cancelled on `onCleared()`
- No background leaks

Launching work outside it breaks lifecycle safety.

---

### UI layers

UI should:
- Start work
- Observe results
- Cancel automatically

It should never own long-lived jobs.

---

## Libraries and structured concurrency

Library rules:
- Do not use `GlobalScope`
- Do not create hidden scopes
- Respect caller scope

Libraries should provide **suspend functions**, not lifecycle management.

---

## Real-world failure patterns

- Background sync running after logout
- ViewModel cleared but jobs still running
- Errors swallowed due to supervisors
- Zombie coroutines updating dead UI

All are violations of structure.

---

## Senior-level mental model

Think in trees, not threads.

Ask:
- Who is the parent?
- When does it end?
- What happens on failure?

If the tree isn’t obvious from the code, the design is wrong.

Structured concurrency doesn’t make concurrency easier.
It makes **wrong concurrency impossible — if you respect it**.

