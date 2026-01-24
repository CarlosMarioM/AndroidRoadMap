# Gradle Advanced Build Lifecycle (Android)

This document explains the **advanced Gradle build lifecycle** with an Android focus. It is meant for **senior Android developers** who need to understand *what actually happens* when Gradle runs, how it impacts CI/CD performance, and how to avoid subtle build bugs.

If you don’t understand the lifecycle, you will write slow, fragile builds without realizing it.

---

## The real Gradle lifecycle (high level)

Gradle execution is split into **three strict phases**:

1. **Initialization phase**
2. **Configuration phase**
3. **Execution phase**

Everything else (tasks, plugins, variants, caching) fits into these phases.

---

## 1. Initialization phase

### What happens

- Gradle determines **which projects are included**
- Reads `settings.gradle(.kts)`
- Creates the project graph

```kotlin
// settings.gradle.kts
include(":app", ":core", ":data")
```

### Why it matters

- Multi-module structure is locked here
- Composite builds are wired here
- CI performance starts here

Mistakes here affect **every build**.

---

## 2. Configuration phase

### What happens

- All `build.gradle(.kts)` files are evaluated
- Plugins are applied
- Tasks are **created and configured**
- Android variants are calculated

⚠️ **Every project is configured**, even if no task runs.

---

### Common senior mistake

```kotlin
// ❌ Runs during configuration
val version = gitVersion()
```

This executes **on every build**, even `./gradlew help`.

---

### Correct approach: lazy configuration

```kotlin
// ✅ Deferred until execution
tasks.register("printVersion") {
    doLast {
        println(gitVersion())
    }
}
```

---

## Configuration avoidance (critical)

Gradle 5+ introduced **configuration avoidance APIs**.

```kotlin
// ❌ Eager
tasks.getByName("assemble") { }

// ✅ Lazy
tasks.named("assemble") { }
```

This directly reduces:
- CI time
- Memory usage
- Variant explosion cost

---

## Android-specific configuration cost

Android plugins do heavy work in configuration:
- Variant generation
- Source set wiring
- Manifest merging logic

This is why **variant filtering** is so impactful.

---

## 3. Execution phase

### What happens

- Only requested tasks execute
- Task graph is finalized
- Inputs and outputs are checked
- Cache hits or misses occur

Gradle executes tasks **only if needed**.

---

## Incremental tasks

A task is incremental if:
- Inputs are declared
- Outputs are declared

```kotlin
tasks.register<Copy>("copyAssets") {
    from("src/assets")
    into(layout.buildDirectory.dir("assets"))
}
```

Without inputs/outputs → no incremental build → slow CI.

---

## Up-to-date checks

Gradle skips tasks when:
- Inputs unchanged
- Outputs exist

```text
UP-TO-DATE
FROM-CACHE
```

If you never see these, your build is broken.

---

## Build cache (advanced)

### Local vs remote cache

- Local: developer speed
- Remote: CI speed

Tasks must be:
- Pure
- Deterministic
- Declared correctly

---

## Task graph finalization

```kotlin
gradle.taskGraph.whenReady {
    // ❌ Too late to create tasks
}
```

Use this only for **diagnostics**, not logic.

---

## Plugins and lifecycle

Plugins hook into lifecycle phases:

- `apply()` → configuration phase
- Task registration → configuration
- `doFirst` / `doLast` → execution

Bad plugins break builds silently.

---

## Common anti-patterns

- Heavy logic in `build.gradle`
- Reading files or git during configuration
- Dynamic task creation during execution
- Ignoring configuration avoidance

These scale terribly in CI.

---

## Senior rules of thumb

1. Configuration should be **fast and boring**
2. Execution should be **incremental and cacheable**
3. If CI is slow, inspect configuration first
4. Variants multiply configuration cost

---

## Mental model

> Initialization decides *what exists*, configuration decides *what could run*, execution decides *what actually runs*.

---

## Interview takeaway

**Senior Android developers understand the Gradle lifecycle deeply**, using configuration avoidance, incremental tasks, and cache-friendly builds to keep local and CI builds fast, predictable, and scalable.

