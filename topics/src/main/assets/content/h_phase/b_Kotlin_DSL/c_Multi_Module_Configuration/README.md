# Gradle Multi-module Configuration (Android)

> Multi-module builds are **not optional** once an Android project grows.
> This document explains **how to structure**, **configure**, and **scale** a multi-module Android project using **Kotlin DSL**, **type-safe accessors**, and **modern Gradle practices**.

No toy examples. This is how real projects stay sane.

---

## Why Multi-module Exists (Reality check)

If your app is still single-module:
- Build times get worse
- Ownership is unclear
- Features become entangled
- Testing is painful

Multi-module gives you:
- **Faster builds** (parallelism + caching)
- **Clear boundaries**
- **Better testability**
- **Independent evolution of features**

But only if you structure it correctly.

---

## Canonical Android Module Types

You do NOT need 20 kinds. Keep it simple.

### 1. `:app`
- Android application module
- Entry point only
- Minimal logic

### 2. `:core:*`
- Shared business logic
- Utils, domain models, data layers
- No Android UI (unless explicitly needed)

### 3. `:feature:*`
- One feature per module
- UI + ViewModels + feature-specific logic
- Depends on `core`, never on other features

### 4. Optional `:designsystem`
- Compose / Views / themes
- Zero business logic

---

## Recommended Folder Structure

```text
root
├── app
├── core
│   ├── common
│   ├── data
│   └── domain
├── feature
│   ├── login
│   ├── profile
│   └── settings
└── build-logic (or buildSrc)
```

Flat enough to navigate. Structured enough to scale.

---

## settings.gradle.kts (The source of truth)

```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "MyApp"

include(
    ":app",
    ":core:common",
    ":core:data",
    ":core:domain",
    ":feature:login",
    ":feature:profile"
)
```

Everything starts here. If it’s not included, it doesn’t exist.

---

## Using Type-safe Project Accessors

Stop writing string paths.

```kotlin
dependencies {
    implementation(projects.core.domain)
    implementation(projects.feature.login)
}
```

If this doesn’t autocomplete, your setup is broken.

---

## Dependency Rules (DO NOT BREAK THESE)

### Allowed
```text
app → feature → core
```

### Forbidden
```text
feature → feature
core → feature
```

If features depend on each other, you failed modularization.

Fix it by extracting shared logic into `core`.

---

## build.gradle.kts Per Module (Minimalism wins)

### Feature module
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.feature.login"
}

dependencies {
    implementation(projects.core.domain)
}
```

No duplication. No cleverness.

---

## Shared Configuration (The RIGHT way)

If you see this repeated everywhere:
```kotlin
compileSdk = 34
minSdk = 24
```

You are doing it wrong.

---

## Convention Plugins (Mandatory at scale)

Create a **build-logic module** (preferred over raw buildSrc):

```text
build-logic
└── src/main/kotlin
    ├── android-library.gradle.kts
    ├── android-feature.gradle.kts
    └── android-app.gradle.kts
```

### Example: android-feature.gradle.kts
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
}
```

Apply it:
```kotlin
plugins {
    id("android-feature")
}
```

This is how duplication dies.

---

## Version Catalogs Across Modules

One catalog. One source of truth.

```kotlin
dependencies {
    implementation(libs.coroutines.core)
}
```

If modules define their own versions → chaos.

---

## API vs implementation (Critical)

### Core modules
```kotlin
dependencies {
    api(libs.kotlin.stdlib)
}
```

### Feature modules
```kotlin
dependencies {
    implementation(projects.core.domain)
}
```

Rule:
- Use `api` **only** when exposing types across module boundaries
- Default to `implementation`

Wrong usage = slower builds.

---

## Testing in Multi-module Projects

Each module:
- Has its own unit tests
- Can be tested in isolation

Core modules should have:
- Highest test coverage
- Zero Android dependencies if possible

---

## Build Performance Benefits (When done right)

- Parallel compilation
- Better configuration cache hits
- Smaller recompilation surface

If builds are slower after modularization:
👉 Your dependency graph is wrong.

---

## Common Anti-patterns

### ❌ God-core module

If `core` knows everything → it’s not core, it’s a dump.

Split it.

---

### ❌ Feature cross-dependencies

"Just this one time" turns into spaghetti.

Don’t do it.

---

### ❌ Copy-pasted Gradle blocks

If you copy configs between modules, you already lost.

Use convention plugins.

---

## Final Verdict

Multi-module builds are about **discipline**, not complexity.

Done right:
- Faster builds
- Clear ownership
- Fearless refactoring

Done wrong:
- Slower builds
- Circular dependencies
- Developer misery

Gradle won’t save you. Architecture will.

