# When Dependency Injection Becomes Harmful

Dependency Injection (DI) is a tool. Used correctly, it improves testability and decoupling. Used blindly, it **actively damages codebases**. This document explains *exactly* when DI stops helping and starts hurting.

---

## The core truth

DI does **not**:
- Fix bad architecture
- Replace clear boundaries
- Make poor abstractions acceptable

DI only wires objects together. If the objects are wrong, DI amplifies the damage.

---

## Symptom-driven DI (the most common failure)

DI becomes harmful when it is introduced because:
- "We might need this later"
- "That’s how Clean Architecture says"
- "Everything should be injectable"

This leads to:
- Dozens of interfaces with one implementation
- Exploding constructor parameters
- Navigation logic hidden behind factories

If DI is added **before a real dependency problem exists**, it’s premature abstraction.

---

## Over-injection of simple objects

Bad example:

```kotlin
class Formatter @Inject constructor()
```

Injected everywhere, scoped, mocked in tests.

Reality:
- Stateless
- Cheap
- No external dependency

Correct approach:

```kotlin
val formatter = Formatter()
```

Inject behavior, not utilities.

---

## DI as a service locator (anti-pattern)

```kotlin
class SomeClass @Inject constructor(
    private val lazyRepo: Lazy<Repo>
)
```

Or worse:

```kotlin
EntryPointAccessors.fromApplication(...)
```

This hides dependencies, breaks reasoning, and destroys test clarity.

If a class doesn’t declare its dependencies explicitly, DI is being abused.

---

## Leaking lifetimes through DI

Classic bugs:
- `@Singleton` holding screen state
- Long-lived scopes holding `Context`
- ViewModels injected into singletons

DI does not manage lifecycle correctness — **you do**.

Wrong scope choices create leaks that are hard to detect.

---

## Constructor explosion

```kotlin
class Monster @Inject constructor(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F
)
```

This is not scalable design. It’s a sign of:
- Missing aggregation
- Missing domain boundaries
- God objects

DI exposes architectural rot.

---

## DI in UI layers (Compose-specific damage)

Common mistakes:
- Injecting repositories directly into Composables
- Creating scoped objects inside UI
- Using DI instead of state hoisting

Compose needs **data**, not dependencies.

Correct:
- DI at ViewModel boundary
- UI receives state and callbacks

---

## Testing illusion

DI-heavy codebases often claim "testability" but:
- Tests mock everything
- Behavior is never exercised
- Refactors break dozens of tests

If DI increases test fragility, it failed its purpose.

---

## When NOT using DI is better

Do NOT use DI for:
- Pure functions
- Value objects
- Simple mappers
- Formatting logic
- Local UI helpers

Clarity beats theoretical decoupling.

---

## When DI is justified

DI shines when:
- Managing expensive resources
- Crossing architectural boundaries
- Swapping implementations (real ones)
- Testing side effects

If removing DI wouldn’t hurt, it’s probably unnecessary.

---

## Rule of thumb (non-negotiable)

- Inject **policies**, not helpers
- Inject **boundaries**, not details
- If DI adds more code than value, remove it
- Architecture first, DI second

DI should disappear into the background. If it dominates the code, it’s already harmful.

