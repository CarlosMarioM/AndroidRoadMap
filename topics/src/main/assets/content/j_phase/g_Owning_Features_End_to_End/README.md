# Type-safe Accessors in Gradle (Kotlin DSL)

## What are Type-safe Accessors (straight talk)
Type-safe accessors are **generated Kotlin properties and functions** that replace string-based access in Gradle.

They eliminate:
- `project(":module")`
- `getByName("release")`
- `"implementation"` as a string

And replace them with **compile-time checked APIs**.

If you typo something, the build **does not compile**.
That’s the whole point.

---

## Why they exist
Groovy allowed:
- Dynamic resolution
- Late failures
- Silent misconfigurations

Kotlin DSL does **not**.

Type-safe accessors give:
- IDE autocomplete
- Refactoring safety
- Faster feedback
- Fewer runtime Gradle errors

---

## Where type-safe accessors apply

| Area | Example |
|----|----|
| Projects | `projects.core`, `projects.feature.login` |
| Configurations | `implementation`, `debugImplementation` |
| Tasks | `tasks.named<Jar>("jar")` |
| Extensions | `android {}`, `kotlin {}` |
| Version catalogs | `libs.coroutines.core` |

---

## How they are generated (important)
Gradle generates accessors during:
```
Settings evaluation
↓
Build configuration phase
↓
Kotlin DSL accessor generation
```

They are compiled into:
```
.gradle/kotlin-dsl/accessors/
```

This is why:
- First sync is slow
- Breaking `settings.gradle.kts` breaks everything

---

## Project accessors (multi-module)

### settings.gradle.kts
```kotlin
rootProject.name = "MyApp"

include(":core")
include(":feature:login")
include(":feature:profile")
```

### Usage
```kotlin
dependencies {
    implementation(projects.core)
    implementation(projects.feature.login)
}
```

No strings. Fully safe.

---

## Configuration accessors

### Groovy (old)
```groovy
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib"
}
```

### Kotlin DSL
```kotlin
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}
```

Gradle generates:
- `implementation()`
- `testImplementation()`
- `debugImplementation()`

Misspell it → compile error.

---

## Task accessors

### Unsafe
```kotlin
tasks.getByName("assembleRelease")
```

### Safe
```kotlin
tasks.named("assembleRelease")
```

### Fully typed
```kotlin
tasks.named<Jar>("jar") {
    archiveBaseName.set("my-lib")
}
```

If the task doesn’t exist → build fails immediately.

---

## Extension accessors

### Android plugin example
```kotlin
android {
    compileSdk = 34
}
```

`android` is a generated accessor from the Android Gradle Plugin.

Same applies to:
- `kotlin`
- `composeOptions`
- `publishing`

If the plugin isn’t applied → accessor doesn’t exist.

---

## Version Catalog type-safe accessors

### libs.versions.toml
```toml
[libraries]
coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version = "1.8.1" }
```

### Usage
```kotlin
dependencies {
    implementation(libs.coroutines.core)
}
```

Generated structure:
```
libs.coroutines.core
```

Rename in TOML → compiler tells you everywhere it breaks.

---

## Plugin accessors

### Version catalog
```toml
[plugins]
android-application = { id = "com.android.application", version = "8.3.0" }
```

### Usage
```kotlin
plugins {
    alias(libs.plugins.android.application)
}
```

This is the **cleanest possible Gradle setup** today.

---

## Common pitfalls (real-world)

### ❌ Accessor not found
Cause:
- Plugin not applied
- Settings file broken
- Cache corrupted

Fix:
```bash
./gradlew --stop
rm -rf .gradle
```

### ❌ Slow sync
Cause:
- Accessor regeneration
- Huge version catalogs

Fix:
- Enable configuration cache
- Reduce dynamic includes

---

## Performance implications
Type-safe accessors:
- Increase first configuration time
- Improve long-term stability
- Reduce runtime failures

For large projects, this is **always worth it**.

---

## When NOT to rely on them
- Highly dynamic builds
- Generated module names
- Custom DSLs with runtime behavior

In those cases, explicit APIs are clearer.

---

## Senior-level takeaway
If your Gradle build:
- Still uses strings everywhere
- Has no version catalogs
- Avoids Kotlin DSL

You are choosing **fragility over correctness**.

Type-safe accessors are not optional anymore.
They are table stakes.

---

## Checklist
- [ ] Kotlin DSL enabled
- [ ] Version catalogs used
- [ ] Project accessors enabled
- [ ] No string-based task lookups
- [ ] Plugins applied explicitly

---

End of document.
