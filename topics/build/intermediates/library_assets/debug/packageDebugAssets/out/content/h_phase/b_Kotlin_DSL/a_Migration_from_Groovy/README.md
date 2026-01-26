# Kotlin DSL Migration from Groovy (Gradle)

## Overview

Migrating from **Groovy DSL (`build.gradle`)** to **Kotlin DSL (`build.gradle.kts`)** is not cosmetic.
It is a shift from dynamic scripting to **typed, compile-time safe build logic**.

---

## Why Migrate

Groovy DSL:
- Dynamic typing
- Runtime errors
- Weak IDE support
- String-based configuration

Kotlin DSL:
- Compile-time safety
- Strong autocomplete
- Refactor-safe
- Discoverable APIs

---

## Migration Strategy

### Recommended Order

1. `settings.gradle` → `settings.gradle.kts`
2. Root `build.gradle` → `build.gradle.kts`
3. Version Catalogs (`libs.versions.toml`)
4. Module build files
5. Custom scripts

---

## File Renaming

| Groovy | Kotlin DSL |
|------|-----------|
| build.gradle | build.gradle.kts |
| settings.gradle | settings.gradle.kts |
| init.gradle | init.gradle.kts |

---

## Syntax Differences

### Method Calls

Groovy:
```groovy
compileSdkVersion 34
```

Kotlin:
```kotlin
compileSdk = 34
```

---

### Strings

Groovy:
```groovy
applicationId "com.example.app"
```

Kotlin:
```kotlin
applicationId = "com.example.app"
```

---

## Plugins Block

Groovy:
```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
```

Kotlin:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
```

---

## Android Block

Groovy:
```groovy
android {
    compileSdkVersion 34
    defaultConfig {
        minSdkVersion 24
        targetSdkVersion 34
    }
}
```

Kotlin:
```kotlin
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
}
```

---

## Dependencies

Groovy:
```groovy
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
}
```

Kotlin:
```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
}
```

---

## Tasks

Groovy:
```groovy
tasks.withType(Test) {
    useJUnitPlatform()
}
```

Kotlin:
```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## Final Notes

Kotlin DSL is stricter by design.
That strictness is the value.
