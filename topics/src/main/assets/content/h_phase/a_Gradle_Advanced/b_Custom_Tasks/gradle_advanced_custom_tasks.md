# Gradle Advanced Custom Tasks (Android / Kotlin)

This section is **not beginner-friendly**. It assumes you already understand:
- Gradle basics
- Android Gradle Plugin (AGP)
- Kotlin DSL
- Task graph fundamentals

If you don’t, stop here.

---

## What a Gradle Task *Really* Is

A Gradle task is:
- A **unit of work**
- With **inputs** and **outputs**
- Executed as part of a **task graph**

A task is *not* a script and *not* a shell command.

Under the hood:
- Gradle builds a **directed acyclic graph (DAG)**
- Tasks only run if their **inputs changed** (incremental build)

If your task ignores inputs/outputs, you broke caching.

---

## Task Types vs Ad‑Hoc Tasks

### ❌ Anti‑pattern: Anonymous tasks

```kotlin
tasks.create("doStuff") {
    doLast {
        println("Doing stuff")
    }
}
```

Problems:
- No inputs
- No outputs
- No cache
- No up‑to‑date checks

This is scripting, not Gradle.

---

### ✅ Correct: Typed Tasks

```kotlin
abstract class GenerateVersionTask : DefaultTask() {

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

Why this matters:
- Gradle can skip the task
- Works with build cache
- Parallel‑safe

Senior‑level Gradle always uses typed tasks.

---

## Registering Tasks (Configuration Avoidance)

Never eagerly create tasks.

### ❌ Wrong

```kotlin
tasks.create<GenerateVersionTask>("generateVersion")
```

### ✅ Right

```kotlin
tasks.register<GenerateVersionTask>("generateVersion") {
    versionName.set("1.0.0")
    outputFile.set(layout.buildDirectory.file("version.txt"))
}
```

Why:
- Task is created **only if needed**
- Faster configuration time

This matters a lot in CI.

---

## Task Inputs & Outputs (The Core Rule)

If Gradle doesn’t know:
- what your task reads
- what your task writes

Then Gradle **cannot optimize anything**.

### Common Input Types

```kotlin
@get:Input
val flag: Property<Boolean>

@get:InputFile
val configFile: RegularFileProperty

@get:InputFiles
val sourceFiles: ConfigurableFileCollection
```

### Output Types

```kotlin
@get:OutputFile
val output: RegularFileProperty

@get:OutputDirectory
val outputDir: DirectoryProperty
```

No inputs/outputs = no incremental build.

---

## Task Dependencies (DependsOn vs MustRunAfter)

### `dependsOn`

Hard dependency. Forces execution.

```kotlin
tasks.named("assemble") {
    dependsOn("generateVersion")
}
```

### `mustRunAfter`

Ordering only. No execution guarantee.

```kotlin
tasks.named("lint") {
    mustRunAfter("generateVersion")
}
```

Misusing these causes **slow builds**.

---

## Hooking into Android Build Variants

Never loop variants eagerly.

### Correct way (AGP 7+)

```kotlin
androidComponents {
    onVariants { variant ->
        val taskName = "generate${variant.name.capitalize()}Config"

        tasks.register<GenerateVersionTask>(taskName) {
            versionName.set(variant.versionName)
            outputFile.set(
                layout.buildDirectory.file("config/${variant.name}.txt")
            )
        }
    }
}
```

This is **variant‑aware**, lazy, and safe.

---

## Attaching to Existing Tasks

```kotlin
tasks.named("preBuild") {
    dependsOn("generateDebugConfig")
}
```

Avoid attaching to `assemble` unless you must.

Better hooks:
- `preBuild`
- `compileKotlin`
- `mergeResources`

---

## Incremental Tasks (Advanced)

Gradle supports **incremental task actions**.

```kotlin
@TaskAction
fun execute(inputs: IncrementalTaskInputs) {
    if (!inputs.isIncremental) {
        // full rebuild
    }
}
```

Use this only when:
- Large file sets
- Heavy IO work

Otherwise, don’t over‑optimize.

---

## Worker API (Parallel Execution)

For CPU‑heavy work:

```kotlin
abstract class MyTask : DefaultTask() {

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun run() {
        workerExecutor.noIsolation().submit(MyWorker::class.java) {
            // params
        }
    }
}
```

This enables:
- Parallelism
- Isolation
- Better CI utilization

---

## Anti‑Patterns That Kill CI Performance

❌ Using `doLast {}` everywhere
❌ Reading files without declaring inputs
❌ Writing to random directories
❌ Running shell commands inside tasks
❌ Using `afterEvaluate`

If you see these, refactor.

---

## When Custom Tasks Are Justified

Create a custom task only if:
- The logic is reused
- The task is cacheable
- It integrates with variants

Otherwise:
- Use existing Gradle tasks
- Or move logic to CI scripts

---

## Senior Rule of Thumb

If your custom task:
- Can’t be cached
- Isn’t incremental
- Breaks configuration cache

It doesn’t belong in the build.

Full stop.

