# CI/CD Gradle Task Optimization for Android

This document explains **how to optimize Gradle tasks for Android CI/CD pipelines**, focusing on **speed, determinism, and cost reduction**.

This is a senior-level topic: slow or flaky CI is a scalability problem, not just an inconvenience.

---

## The problem CI/CD faces with Android

Android builds are slow because they:
- Have large dependency graphs
- Perform annotation processing
- Compile multiple variants
- Run expensive instrumentation tests

Unoptimized pipelines:
- Waste money
- Block developers
- Encourage skipping tests

---

## Core principles of Gradle optimization

1. **Do less work**
2. **Reuse previous work**
3. **Avoid unnecessary variants**
4. **Fail fast**

Everything else is implementation detail.

---

## Enable Gradle build cache

### Local + remote cache

```properties
# gradle.properties
org.gradle.caching=true
```

- Reuses task outputs across builds
- Massive CI speedups
- Remote cache is critical for CI

> If you’re not using build cache in CI, you’re burning money.

---

## Parallel execution

```properties
org.gradle.parallel=true
org.gradle.workers.max=4
```

- Runs independent tasks in parallel
- Tune workers based on CI machine cores

---

## Configuration avoidance

Avoid eagerly configuring tasks.

```kotlin
// ❌ Bad
tasks.getByName("assemble") { }

// ✅ Good
tasks.named("assemble") { }
```

- Reduces configuration time
- Critical for large multi-module projects

---

## Incremental compilation

```properties
kotlin.incremental=true
kotlin.incremental.useClasspathSnapshot=true
```

- Recompiles only changed files
- Huge impact on Kotlin-heavy projects

---

## Avoid building all variants

### Limit variants in CI

```kotlin
android {
    variantFilter {
        if (name.contains("release")) ignore = true
    }
}
```

- CI usually needs only `debug`
- Build release only on tagged builds

---

## Split pipelines by responsibility

### Example

- PR pipeline:
  - `:lint`
  - `:testDebugUnitTest`

- Main branch:
  - `:assembleDebug`
  - Integration tests

- Release pipeline:
  - `:bundleRelease`

Don’t run everything all the time.

---

## Test task optimization

### Prefer JVM tests

```bash
./gradlew testDebugUnitTest
```

- Faster
- More stable

### Limit instrumentation tests

```bash
./gradlew connectedDebugAndroidTest
```

- Run only on critical branches
- Shard tests when possible

---

## Disable unnecessary tasks

```properties
android.enableR8.fullMode=true
```

```kotlin
tasks.withType<Test> {
    maxParallelForks = 2
}
```

- Reduce memory pressure
- Avoid CI OOM crashes

---

## Dependency optimization

- Avoid dynamic versions (`+`)
- Use dependency locking

```bash
./gradlew dependencies --write-locks
```

Ensures reproducible builds.

---

## CI environment tuning

- Increase heap size

```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

- Use SSD-backed runners
- Reuse Gradle daemon between jobs when possible

---

## Metrics to watch

- Build time per task
- Cache hit rate
- Test duration
- Failure causes

Use `--scan` to analyze builds.

---

## Common anti-patterns

- Running `clean` on every build
- Building all variants
- Running UI tests on every PR
- Ignoring cache misses

---

## Mental model

> CI optimization is about **eliminating waste**, not speeding up a slow machine.

---

## Interview takeaway

**Senior Android developers design CI pipelines intentionally**, optimizing Gradle tasks, leveraging caching and parallelism, and structuring pipelines to maximize feedback speed while maintaining confidence.

