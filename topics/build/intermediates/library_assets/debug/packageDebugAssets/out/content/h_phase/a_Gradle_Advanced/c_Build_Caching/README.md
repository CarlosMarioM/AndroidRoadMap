# Build Cache (Gradle) 

Build caching is about **not doing work twice**. That’s it.

If your build cache is slow, unreliable, or “randomly misses”, it’s because your tasks are **not cacheable**, **not deterministic**, or **poorly configured**.

This document explains:
- What the build cache actually is
- How Gradle decides a task is cacheable
- Local vs Remote cache (CI reality)
- What breaks caching (most projects)
- How to fix it like a senior

---

## What the Build Cache Really Does

For a given task execution, Gradle computes a **build cache key** based on:

- Task class
- Task inputs
- Task outputs
- Relevant environment properties

If an identical key exists in the cache:
→ Gradle **skips execution** and **restores outputs**

No task code runs. No CPU. No IO.

---

## Local vs Remote Build Cache

### Local Build Cache
- Stored on developer machine
- Enabled by default
- Useful for:
  - Clean builds
  - Branch switching
  - Re-running builds locally

```text
~/.gradle/caches/build-cache-*
```

### Remote Build Cache
- Shared across machines (CI + devs)
- Massive CI speedups when done right
- Requires **discipline**

Used correctly:
→ CI builds become incremental  
Used wrong:
→ Cache misses everywhere, zero benefit

---

## When a Task Is Cacheable

A task is cacheable only if:

1. It declares **all inputs**
2. It declares **all outputs**
3. It has **no undeclared side effects**
4. It is **deterministic**

### Example: Cacheable Task

```kotlin
@CacheableTask
abstract class GenerateVersionFile : DefaultTask() {

    @get:Input
    abstract val versionName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        outputFile.get().asFile.writeText(versionName.get())
    }
}
```

Miss any annotation → cache is broken.

---

## Why Your Cache Misses (Real Reasons)

### 1. Using System Time

```kotlin
val now = System.currentTimeMillis() // ❌
```

Non-deterministic = non-cacheable.

---

### 2. Reading Files Without Declaring Them

```kotlin
File("config.json").readText() // ❌ not declared
```

Gradle doesn’t know it matters → cache poisoned.

---

### 3. Writing Outside Declared Outputs

```kotlin
File("/tmp/output.txt").writeText("hi") // ❌
```

Side effect = cache disabled.

---

### 4. Environment Leakage

Bad:
- Absolute paths
- Usernames
- Hostnames
- OS-specific logic

Good:
- Normalize everything
- Use relative paths
- Declare inputs explicitly

---

## Android-Specific Reality

### Tasks That Cache Well
- Kotlin compilation
- Java compilation
- Resource processing
- R8 (mostly)
- Lint (with configuration cache)

### Tasks That Often Break Cache
- Custom Gradle scripts
- Poorly written plugins
- Codegen tasks
- Versioning tasks

---

## Enabling Build Cache (Correctly)

### settings.gradle(.kts)

```kotlin
buildCache {
    local {
        isEnabled = true
    }
    remote<HttpBuildCache> {
        url = uri("https://cache.company.com/")
        isPush = System.getenv("CI") == "true"
    }
}
```

**Only CI should push.**
Developers pull.

---

## CI Build Cache Strategy (Reality)

### Correct Pattern

- PR builds: pull-only
- Main branch: pull + push
- Release builds: pull-only

### Why?
Because:
- PRs are noisy
- Branch-specific outputs poison cache
- Releases must be deterministic

---

## Measuring Cache Effectiveness

Run:

```bash
./gradlew assemble --build-cache --info
```

Look for:

```text
FROM-CACHE
```

Or use build scans:

```bash
./gradlew assemble --scan
```

Metrics that matter:
- Cache hit ratio
- Task avoidance %
- Time saved

---

## Build Cache vs Configuration Cache (Do Not Confuse)

| Feature | Purpose |
|------|-------|
| Build Cache | Skips task execution |
| Configuration Cache | Skips configuration phase |

They are **orthogonal**.
You want **both**.

---

## Hard Truths

- If your tasks aren’t cacheable, remote cache is useless
- Cache misses are usually **your fault**
- “Gradle is slow” usually means “we broke determinism”
- Most teams enable cache without fixing tasks → placebo

---

## Senior Checklist

Before enabling remote cache:

- [ ] All custom tasks annotated
- [ ] No system time usage
- [ ] No hidden file reads
- [ ] No writes outside outputs
- [ ] CI push rules defined
- [ ] Build scans reviewed

If you skip this, don’t bother.

---

## Bottom Line

Build caching is not a toggle.
It’s a **contract**.

Honor it:
→ builds become fast  
Break it:
→ nothing improves  

That’s the deal.
