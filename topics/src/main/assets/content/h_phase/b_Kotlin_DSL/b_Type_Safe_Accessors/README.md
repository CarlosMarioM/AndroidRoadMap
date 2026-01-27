# Gradle Type-safe Accessors (Kotlin DSL)

> Type-safe accessors are **the real payoff** of migrating to Kotlin DSL.
> This document explains **what they are**, **how they work**, and **how to use them correctly** in Android projects — without cargo-culting.

If you’re not using type-safe accessors, you’re basically running Kotlin DSL with the handbrake on.

---

## What are Type-safe Accessors (plain truth)

Type-safe accessors are **generated Kotlin properties** that replace string-based access to:
- Projects
- Dependencies
- Version catalogs
- Extensions

Instead of this (Groovy-style):
```kotlin
implementation("androidx.core:core-ktx:1.12.0")
```

You get this:
```kotlin
implementation(libs.androidx.core.ktx)
```

And instead of:
```kotlin
project(":core")
```

You get:
```kotlin
projects.core
```

No strings. No typos. Compile-time safety.

---

## Why Type-safe Accessors Matter (for real projects)

They give you:
- **Compile-time validation** of dependencies and modules
- **IDE navigation** (Cmd/Ctrl + click actually works)
- **Refactor safety** across large multi-module builds
- **Cleaner, intention-revealing build scripts**

Without them:
- Renames break silently
- Typos compile
- Builds fail at runtime

That’s unacceptable at scale.

---

## Enabling Type-safe Project Accessors

This is **not optional** for modern builds.

### settings.gradle.kts
```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```

After syncing, Gradle generates:
```kotlin
projects.app
projects.core
projects.feature.login
```

Instead of string paths.

---

## Using Type-safe Project Accessors

### Before
```kotlin
dependencies {
    implementation(project(":core"))
}
```

### After
```kotlin
dependencies {
    implementation(projects.core)
}
```

Nested modules:
```kotlin
implementation(projects.feature.login)
```

Renaming a module now updates **every reference automatically**.

---

## Version Catalogs (libs.versions.toml)

This is where type-safe accessors shine the most.

### libs.versions.toml
```toml
[versions]
kotlin = "1.9.22"
coroutines = "1.7.3"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "kotlin" }
coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }

[plugins]
android-application = { id = "com.android.application", version = "8.2.0" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

Gradle generates:
```kotlin
libs.androidx.core.ktx
libs.coroutines.core
libs.plugins.android.application
```

---

## Using Type-safe Dependency Accessors

### Dependencies block
```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
}
```

No coordinates. No guessing. IDE autocomplete guides you.

---

## Plugin Accessors (IMPORTANT)

### Root build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
```

### Module
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}
```

This eliminates:
- Plugin version duplication
- Plugin drift between modules

---

## Generated Accessors: How They Actually Work

Gradle generates these during **configuration**:
- Located under `.gradle/kotlin-dsl/accessors`
- Re-generated when:
  - `settings.gradle.kts` changes
  - `libs.versions.toml` changes
  - Module structure changes

If autocomplete breaks → **invalidate caches or re-sync**.

---

## Common Mistakes (and how to avoid them)

### ❌ Accessor not found

Causes:
- You didn’t sync
- You mistyped the TOML key
- Gradle cache is stale

Fix:
- Re-sync
- Check generated accessors
- Don’t fight the IDE

---

### ❌ Hyphen vs camelCase confusion

Rule:
```text
androidx-core-ktx → libs.androidx.core.ktx
```

Gradle replaces:
- `-` → `.`
- camelCase preserved

Know this or suffer.

---

### ❌ Overengineering the catalog

If everything becomes:
```kotlin
libs.foo.bar.baz.qux
```

You failed.

Keep catalogs:
- Flat
- Predictable
- Human-readable

---

## Type-safe Accessors vs buildSrc

Use **both**, but correctly:

- Version catalogs → dependencies & plugins
- buildSrc / convention plugins → logic

Do NOT:
- Put dependencies in buildSrc
- Put logic in TOML

Each tool has a job.

---

## When Type-safe Accessors Are Not Worth It

Rare cases:
- Tiny, single-module apps
- Disposable PoCs

Everywhere else → use them.

---

## Final Verdict

Type-safe accessors turn Gradle from a stringly-typed mess into a **real, refactorable system**.

If your build breaks after enabling them, that’s not a Gradle problem — that’s your build finally telling the truth.

