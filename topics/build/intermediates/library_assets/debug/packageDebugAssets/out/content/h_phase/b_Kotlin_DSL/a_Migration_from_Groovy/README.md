# Kotlin DSL Migration from Groovy (Gradle)

Migrating from Groovy DSL (build.gradle) to Kotlin DSL (build.gradle.kts) is not about fashion or syntax preference.
It’s about type safety, tooling, and long-term maintainability.

This document explains why, when, and how to migrate — and the traps that will waste your time if you don’t know them upfront.

## Why Kotlin DSL exists (real reasons)

Groovy DSL problems:

- Dynamic typing → errors appear at runtime

- IDE autocomplete is unreliable

- Refactoring is dangerous

- Hard to discover APIs

- Silent failures due to string-based configuration

Kotlin DSL fixes this by:

- Compile-time validation

- Full IDE support (autocomplete, refactor, navigation)

- Discoverable APIs

- Safer builds at scale

Trade-off: Kotlin DSL is stricter and less forgiving. That’s a feature, not a bug.

### When migration makes sense

Good candidates:

- Medium to large Android projects

- Long-lived codebases

- Teams with Kotlin experience

- CI-heavy environments

- Projects using version catalogs

Bad candidates:

- Small throwaway projects

- Teams unfamiliar with Kotlin

- Legacy builds with heavy Groovy metaprogramming

### File mapping
Groovy	Kotlin DSL
build.gradle	build.gradle.kts
settings.gradle	settings.gradle.kts
init.gradle	init.gradle.kts

You cannot mix syntax in the same file.

### Syntax fundamentals
Variables

Groovy:

def minSdk = 24


Kotlin DSL:

val minSdk = 24


No def. Ever.

Strings

Groovy:

applicationId "com.example.app"


Kotlin DSL:

applicationId = "com.example.app"


Assignment is explicit.

Method calls vs assignments

Groovy:

compileSdkVersion 34


Kotlin DSL:

compileSdk = 34


If autocomplete doesn’t show it, you’re probably using Groovy syntax by mistake.

Plugins block

Groovy:

plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}


Kotlin DSL:

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}


With versions:

plugins {
    id("com.android.application") version "8.3.0" apply false
}

Android block migration

Groovy:

android {
    compileSdkVersion 34

    defaultConfig {
        minSdkVersion 24
        targetSdkVersion 34
    }
}


Kotlin DSL:

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
}


Notice:

No Version suffix

Properties, not functions

Dependencies block

Groovy:

dependencies {
    implementation "androidx.core:core-ktx:1.12.0"
}


Kotlin DSL:

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
}


With version catalog:

dependencies {
    implementation(libs.androidx.core.ktx)
}

Accessing extra properties

Groovy:

ext {
    versionName = "1.0"
}


Kotlin DSL (bad, avoid):

extra["versionName"] = "1.0"


Kotlin DSL (recommended):

Use Version Catalogs

Or buildSrc

Or convention plugins

Tasks configuration

Groovy:

tasks.withType(Test) {
    useJUnitPlatform()
}


Kotlin DSL:

tasks.withType<Test> {
    useJUnitPlatform()
}


Typed APIs are mandatory.

Custom tasks

Groovy:

task cleanTmp(type: Delete) {
    delete "tmp"
}


Kotlin DSL:

tasks.register<Delete>("cleanTmp") {
    delete("tmp")
}


Lazy configuration is not optional in Kotlin DSL.

Common migration pain points (read this twice)
1. Groovy magic does not exist

No implicit getters, setters, or closures.

2. Strings are not config

If something is red, it’s broken — fix it, don’t workaround it.

3. Order matters more

Kotlin DSL is compiled. Missing references fail fast.

4. StackOverflow snippets will lie to you

Most examples are Groovy. Translate mentally or ignore.

Version Catalogs + Kotlin DSL (best combo)

Kotlin DSL shines with libs.versions.toml.

Example:

[versions]
kotlin = "1.9.22"

[libraries]
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }


Usage:

dependencies {
    implementation(libs.kotlin.stdlib)
}


This is the correct modern setup.

Migration strategy (do NOT big-bang)

Migrate settings.gradle → settings.gradle.kts

Introduce version catalogs

Migrate root build.gradle

Migrate one module at a time

Kill ext {} blocks

Introduce convention plugins if needed

Performance considerations

Reality check:

Kotlin DSL is slightly slower on first build

Negligible difference after configuration cache

Worth it for correctness alone

When Kotlin DSL hurts

Be honest:

Writing custom Gradle logic is more verbose

Error messages are longer

You must understand Gradle APIs

If that scares you, Groovy is already hurting you — just silently.

Bottom line

Kotlin DSL is:

Stricter

Safer

More maintainable

Better for teams and CI

Groovy DSL is:

Faster to write

Easier to abuse

Harder to maintain at scale

For senior Android projects, Kotlin DSL is the correct choice.