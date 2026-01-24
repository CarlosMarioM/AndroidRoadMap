# Gradle Advanced Custom Tasks (Android)

This document explains **how to design, implement, and reason about advanced custom Gradle tasks** in Android projects.

This is not about writing tasks that "work" — it’s about writing tasks that are **correct, cacheable, incremental, and CI-friendly**.

---

## When you should write a custom task

Valid reasons:
- Code generation
- Asset preprocessing
- Validation or verification steps
- Build-time automation

Invalid reasons:
- Orchestrating business logic
- Replacing proper tools
- Doing runtime work at build time

If a task exists only to "glue" commands, rethink it.

---

## Task anatomy (what actually matters)

A correct task must define:
- Inputs
- Outputs
- Action

Anything else is optional.

---

## Lazy task registration (mandatory)

Always use `tasks.register`, never `create`.

```kotlin
tasks.register("generateVersion") {
    doLast {
        println("1.0.0")
    }
}
```

This avoids eager configuration and keeps CI fast.

---

## Typed tasks (preferred)

Use typed tasks for anything non-trivial.

```kotlin
abstract class GenerateConfigTask : DefaultTask() {

    @get:Input
    abstract val versionName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun run() {
        outputFile.get().asFile.writeText(versionName.get())
    }
}
```

Registering:

```kotlin
tasks.register<GenerateConfigTask>("generateConfig") {
    versionName.set("1.0.0")
    outputFile.set(layout.buildDirectory.file("config.txt"))
}
```

---

## Inputs and outputs (non-negotiable)

Without declared inputs/outputs:
- No incremental builds
- No build cache
- Slow CI

Common annotations:
- `@Input`
- `@InputFile`
- `@InputDirectory`
- `@OutputFile`
- `@OutputDirectory`

---

## Incremental tasks

Use incremental APIs when processing large inputs.

```kotlin
@TaskAction
fun run(inputs: InputChanges) {
    if (!inputs.isIncremental) {
        // full rebuild
    }

    inputs.getFileChanges(inputDir).forEach { change ->
        when (change.changeType) {
            ChangeType.ADDED -> {}
            ChangeType.MODIFIED -> {}
            ChangeType.REMOVED -> {}
        }
    }
}
```

Incremental tasks are a massive CI win.

---

## Cacheable tasks

Make tasks cacheable explicitly.

```kotlin
@CacheableTask
abstract class GenerateConfigTask : DefaultTask()
```

Rules:
- Deterministic output
- No external state
- No timestamps or random values

---

## Avoid configuration-time work

❌ Bad:

```kotlin
val hash = file("input.txt").readText()
```

✅ Good:

```kotlin
@TaskAction
fun run() {
    val hash = inputFile.get().asFile.readText()
}
```

Configuration must stay cheap.

---

## Wiring tasks together

Prefer task providers.

```kotlin
val generate by tasks.registering(GenerateConfigTask::class)

tasks.named("assemble") {
    dependsOn(generate)
}
```

Never call `get()` during configuration unless required.

---

## Android-specific task integration

Hook tasks into variants carefully.

```kotlin
android.applicationVariants.all { variant ->
    val task = tasks.register<GenerateConfigTask>("gen${variant.name}") {
        versionName.set(variant.versionName)
    }

    variant.registerGeneratedResFolders(files(task.map { it.outputFile }))
}
```

Variant hooks multiply cost — use sparingly.

---

## Debugging custom tasks

- Run with `--info` or `--debug`
- Use `--scan`
- Inspect UP-TO-DATE and FROM-CACHE

If your task never caches, it’s wrong.

---

## Common anti-patterns

- Writing files outside build directory
- Using `println` instead of logging
- Reading environment variables during execution without inputs
- Creating tasks dynamically during execution

---

## Senior rules of thumb

1. If it can’t be cached, it must justify itself
2. Tasks should be pure functions of inputs
3. Configuration must stay boring
4. Variant-aware tasks are expensive

---

## Mental model

> A Gradle task is a **pure transformation** from declared inputs to declared outputs.

---

## Interview takeaway

**Senior Android developers write custom Gradle tasks that are lazy, incremental, and cacheable**, fully aligned with the Gradle lifecycle to keep local and CI builds fast and predictable.

