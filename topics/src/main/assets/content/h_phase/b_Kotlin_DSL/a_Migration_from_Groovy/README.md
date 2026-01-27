# Gradle Migration from Groovy to Kotlin DSL (Android)

> This document explains **why**, **when**, and **how** to migrate Gradle build scripts from **Groovy** to **Kotlin DSL** in Android projects — without breaking your build or your sanity.

This is written for developers who already understand Gradle basics and want **type safety, better tooling, and long-term maintainability**.

---

## Why migrate at all? (No marketing fluff)

Groovy works — but it’s dynamically typed and error-prone at scale.

Kotlin DSL gives you:
- **Compile-time safety** (your build fails early, not at runtime)
- **IDE autocompletion & refactoring that actually works**
- **Easier multi-module maintenance**
- **Future-proofing** (Google + Gradle are clearly betting on Kotlin DSL)

Trade-offs (be honest):
- Slightly **slower configuration time** (usually negligible)
- **More verbose syntax**
- Migration has a learning curve

If your project is tiny → don’t bother.
If it’s long-lived or multi-module → migrate.

---

## Migration Strategy (DO THIS IN ORDER)

### 1. Do NOT migrate everything at once

Safe order:
1. `settings.gradle` → `settings.gradle.kts`
2. Root `build.gradle` → `build.gradle.kts`
3. `buildSrc` (if present)
4. Module-level scripts

Gradle allows **mixed Groovy + Kotlin DSL**. Use that.

---

## Step 1 — settings.gradle → settings.gradle.kts

### Groovy
```groovy
rootProject.name = "MyApp"
include ':app', ':core', ':feature:login'
```

### Kotlin DSL
```kotlin
rootProject.name = "MyApp"
include(":app", ":core", ":feature:login")
```

### Repositories & version catalogs

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
```

---

## Step 2 — Root build.gradle → build.gradle.kts

### Groovy
```groovy
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.2.0"
    }
}
```

### Kotlin DSL
```kotlin
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
    }
}
```

### Plugins block (preferred)

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
```

---

## Step 3 — Module build.gradle → build.gradle.kts

### Applying plugins

#### Groovy
```groovy
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
```

#### Kotlin DSL
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
```

---

## Android block differences (IMPORTANT)

### Groovy
```groovy
android {
    compileSdk 34

    defaultConfig {
        minSdk 24
        targetSdk 34
    }
}
```

### Kotlin DSL
```kotlin
android {
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
}
```

Rule of thumb:
- Groovy → implicit setters
- Kotlin DSL → **explicit property assignment**

---

## Dependencies syntax

### Groovy
```groovy
dependencies {
    implementation "androidx.core:core-ktx:1.12.0"
}
```

### Kotlin DSL
```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
}
```

---

## Using Version Catalogs (libs.versions.toml)

Once migrated, **this is where Kotlin DSL shines**.

```toml
[versions]
kotlin = "1.9.22"

[libraries]
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "kotlin" }
```

```kotlin
dependencies {
    implementation(libs.core.ktx)
}
```

No strings. No typos. Refactor-safe.

---

## Common Migration Errors (and why they happen)

### ❌ Unresolved reference

Cause:
- Kotlin DSL runs **before** plugins are applied

Fix:
- Move logic into `afterEvaluate {}` or plugin blocks

---

### ❌ Could not get unknown property

Cause:
- Groovy allowed dynamic access
- Kotlin DSL does not

Fix:
- Use explicit APIs
- Read the plugin’s Kotlin DSL docs

---

### ❌ Accessing extra properties

#### Groovy
```groovy
ext.versionCode = 1
```

#### Kotlin DSL
```kotlin
extra["versionCode"] = 1
```

Access:
```kotlin
val versionCode: Int by extra
```

---

## buildSrc and Kotlin DSL (Strongly Recommended)

Move shared logic out of scripts.

```kotlin
object Versions {
    const val kotlin = "1.9.22"
}
```

```kotlin
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
}
```

Cleaner. Safer. Scales better.

---

## Performance Notes (Reality check)

- First build may be slower
- Incremental builds are usually fine
- Configuration cache works **better** with Kotlin DSL

If performance tanks → you migrated incorrectly.

---

## When NOT to migrate

Don’t migrate if:
- The project is near EOL
- You barely touch Gradle
- You don’t control the build

Migration has a cost. Make it count.

---

## Final Verdict

Groovy is flexible.
Kotlin DSL is **correct**.

For modern Android projects, Kotlin DSL is the right long-term choice.

If your build breaks during migration — good. It means you were relying on undefined behavior.

