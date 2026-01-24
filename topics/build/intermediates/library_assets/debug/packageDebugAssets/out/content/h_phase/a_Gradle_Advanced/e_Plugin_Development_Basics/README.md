# Gradle Plugin Development Basics (No BS)

This document covers **Gradle plugin development from a senior Android / JVM perspective**.
Not hello-world fluff. This is what actually matters in real projects.

---

## What a Gradle Plugin Really Is

A Gradle plugin is:
- Code that **configures the build model**
- Executed during the **configuration phase**
- Meant to **wire tasks, extensions, and conventions**, not run heavy logic

If your plugin does real work at configuration time, it's already wrong.

---

## Types of Gradle Plugins

### 1. Script Plugins
```kotlin
apply(from = "my-script.gradle.kts")
```
- Just shared build logic
- No versioning
- No isolation
- Fine for small projects, useless at scale

### 2. Precompiled Script Plugins (Recommended for teams)
```text
build-logic/
 └── src/main/kotlin/my.android.library.gradle.kts
```
- Compiled
- Typed accessors
- Fast iteration
- Best ROI for Android monorepos

### 3. Binary Plugins
- Published artifacts
- Versioned
- Used across repos
- Higher overhead, higher payoff

---

## Plugin Project Setup (Binary Plugin)

```kotlin
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        register("myPlugin") {
            id = "com.mycompany.myplugin"
            implementationClass = "MyPlugin"
        }
    }
}
```

Rule:
> If you don't need to publish it, **don't create a binary plugin**.

---

## Plugin Entry a_phase.a_Kotlin_Language_Mastery.h_Operator_Overloading.examples.Point

```kotlin
class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // configuration only
    }
}
```

Never:
- Run IO
- Touch the network
- Read files eagerly

Do:
- Register extensions
- Register tasks
- Configure existing tasks

---

## Extensions (Your Plugin API)

```kotlin
abstract class MyExtension {
    abstract val enabled: Property<Boolean>
}
```

```kotlin
project.extensions.create("myPlugin", MyExtension::class.java)
```

Rules:
- Extensions define **inputs**
- Tasks define **behavior**
- Never mix them

---

## Task Registration (Correct Way)

```kotlin
tasks.register<MyTask>("myTask") {
    enabled.set(extension.enabled)
}
```

Never use:
```kotlin
tasks.create(...)
```

If you still do, your plugin breaks configuration cache.

---

## Lazy Configuration (Mandatory)

Gradle is lazy.
Your plugin must be lazier.

Use:
- `Provider<T>`
- `Property<T>`
- `DirectoryProperty`
- `RegularFileProperty`

If you call `.get()` during configuration, you probably messed up.

---

## Task Implementation

```kotlin
abstract class MyTask : DefaultTask() {

    @get:Input
    abstract val enabled: Property<Boolean>

    @TaskAction
    fun run() {
        if (!enabled.get()) return
    }
}
```

Rules:
- Declare inputs/outputs or caching is dead
- TaskAction must be deterministic
- No hidden state

---

## Configuration vs Execution Phase (Tattoo This)

**Configuration phase**
- Plugin `apply()`
- Task registration
- Wiring graph

**Execution phase**
- `@TaskAction`
- Actual work

If you don't know which phase you're in, your build is already slow.

---

## Android Plugins (Special Pain)

AGP:
- Is configuration-heavy
- Breaks frequently
- Has unstable APIs

Use:
```kotlin
plugins.withId("com.android.library") {
    extensions.getByType<LibraryExtension>()
}
```

Never assume:
- Variant existence
- Task names
- Execution order

---

## Variant-Aware Configuration (Android)

```kotlin
androidComponents {
    onVariants { variant ->
        // safe variant access
    }
}
```

If you still use `applicationVariants`, you're living in the past.

---

## Publishing Plugins

Only publish if:
- Multiple repos
- Long-lived logic
- Versioning matters

Otherwise:
**build-logic included build** wins.

---

## Testing Gradle Plugins

### Unit Tests
- Test task logic in isolation

### Functional Tests (Important)
```kotlin
GradleRunner.create()
    .withProjectDir(testProjectDir)
    .withArguments("myTask")
    .build()
```

If you don't have functional tests, your plugin will break silently.

---

## Common Anti-Patterns (Kill These)

- Doing work in `apply()`
- Eager task creation
- Reading files at configuration time
- Using `afterEvaluate`
- Hardcoding paths
- Depending on task names

---

## When You Actually Need a Plugin

You need a plugin when:
- Build logic is repeated
- Convention beats flexibility
- Humans keep misconfiguring builds

You do NOT need a plugin when:
- It's a one-off script
- It's repo-specific
- You're experimenting

---

## Senior Rule of Thumb

> A good plugin makes builds **boring, fast, and predictable**.

Anything else is tech debt.

---

END.
