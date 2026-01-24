
# Gradle Build Scans (Senior-Level, No BS)

Build Scans are **diagnostic artifacts**, not dashboards for managers.
Used correctly, they answer *why* a build is slow, flaky, or non-deterministic.
Used wrong, they’re just expensive telemetry.

This document explains how seniors actually use Build Scans.

---

## What a Build Scan Really Is

A **Build Scan** is a **full execution trace** of a Gradle build:
- Task graph
- Task execution times
- Cache hits/misses
- Dependency resolution
- Configuration vs execution time
- Environment + JVM + Gradle version
- Failure context

Think of it as a **profiling snapshot**, not logging.

---

## When Build Scans Are Worth Using

Use Build Scans when:
- Builds are slow **and you don’t know why**
- CI behaves differently from local
- Cache hits are lower than expected
- Configuration time keeps growing
- A task randomly becomes expensive
- You are migrating to:
  - Configuration Cache
  - New AGP
  - New Gradle version

Do **not** use Build Scans as:
- Always-on telemetry
- Performance KPIs
- A replacement for understanding Gradle

---

## Enabling Build Scans

### Local (one-off)
```bash
./gradlew assembleDebug --scan
```

### CI (controlled)
```bash
./gradlew build --scan --no-daemon
```

### Gradle Enterprise Plugin
```kotlin
plugins {
    id("com.gradle.enterprise") version "3.16.2"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
```

⚠️ Never auto-enable scans for every CI build unless you enjoy burning money.

---

## Reading a Build Scan (What Actually Matters)

### 1. Configuration vs Execution Time

If **configuration time > 20–30%**, you have:
- Eager task creation
- `afterEvaluate` abuse
- Heavy logic in `build.gradle`
- Broken configuration cache

Fix configuration first. Always.

---

### 2. Task Timeline

Look for:
- Long-running tasks
- Tasks executing when they shouldn’t
- Serialization bottlenecks
- Unexpected task dependencies

Red flags:
- `compileKotlin` rerunning unnecessarily
- `mergeResources` running on no-op changes
- Custom tasks with no inputs/outputs

---

### 3. Cache Section (Critical)

Check:
- Task cacheability
- Cache hits vs misses
- Why tasks are NOT cacheable

Common cache killers:
- Reading environment variables
- Writing outside declared outputs
- Non-deterministic file order
- Timestamps
- Random values

If cache hit rate < 70% in CI, you’re doing it wrong.

---

### 4. Dependency Resolution

Look for:
- Repeated resolution of the same modules
- Dynamic versions (`+`, `latest.release`)
- SNAPSHOT churn
- Multiple repositories queried

Dependency resolution should be **boring**.

---

### 5. Environment Diff (CI vs Local)

Compare:
- JVM version
- OS
- File system
- Gradle version
- AGP version

One mismatch = cache miss factory.

---

## Build Scans in CI (Rules That Matter)

**Rule 1: Sample, don’t spam**
- Enable scans on:
  - Failed builds
  - Nightly builds
  - Performance regression branches

**Rule 2: Always upload failed builds**
Failures contain the most valuable data.

**Rule 3: Lock toolchains**
If your CI machines differ, your scans lie.

---

## Common Senior-Level Findings

### “Gradle is slow”
Reality:
- Configuration phase bloated
- Cache disabled by custom tasks
- Kotlin compiler forced full recompiles

### “CI cache doesn’t work”
Reality:
- Non-relocatable paths
- Absolute file references
- Different JDK vendors

### “Incremental build is broken”
Reality:
- Task inputs not declared
- Custom code touching files directly

---

## Build Scans vs Profiler

| Tool | Use Case |
|-----|---------|
| Build Scan | System-wide diagnosis |
| Gradle Profiler | Reproducible benchmarks |
| Android Studio Profiler | Runtime app performance |

Use **Build Scans to discover**, **Profiler to verify**.

---

## What Build Scans Won’t Fix

- Bad architecture
- Too many modules with no boundaries
- Overusing annotation processors
- Kotlin abuse in build scripts

Build Scans expose problems.
They don’t solve them.

---

## Senior Advice (Straight Talk)

If you can’t explain:
- Why a task ran
- Why it wasn’t cached
- Why CI differs from local

You don’t understand your build yet.

Build Scans are your mirror.
Look carefully.

---
