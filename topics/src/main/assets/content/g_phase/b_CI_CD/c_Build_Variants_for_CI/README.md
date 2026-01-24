# Build Variants Strategy for CI in Android

This document explains **how to design and manage Android build variants specifically for CI/CD**, focusing on **speed, correctness, and cost control**.

Build variants are one of the most misunderstood — and most abused — parts of Android CI.

---

## What build variants really are

A build variant = **Build Type × Product Flavor**

Examples:
- `debug`
- `release`
- `stagingDebug`
- `prodRelease`

Each variant:
- Compiles separately
- Runs tests separately
- Produces separate APK/AAB outputs

> Every extra variant multiplies CI time.

---

## The CI problem with variants

Common mistakes:
- Building **all variants** on every commit
- Running tests on **release variants** unnecessarily
- Letting product flavors explode unchecked

a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result:
- Slow CI
- Wasted compute
- Developers bypassing pipelines

---

## Core CI principles for variants

1. **CI does not need all variants**
2. **Debug variants are enough for validation**
3. **Release variants are for confidence, not feedback**
4. **Variants must map to pipeline stages**

---

## Recommended variant strategy

### Minimal variant set

| Purpose | Variant |
|------|--------|
| PR validation | `debug` |
| Main branch | `debug` |
| Staging | `stagingDebug` |
| Release | `release` |

Anything beyond this must justify its cost.

---

## Variant filtering in Gradle

Prevent CI from even creating unnecessary variants.

```kotlin
android {
    variantFilter {
        val isRelease = buildType.name == "release"
        val isCi = System.getenv("CI") == "true"

        if (isCi && isRelease) {
            ignore = true
        }
    }
}
```

This is one of the **highest-impact CI optimizations**.

---

## Separate CI build types (advanced)

Create a lightweight build type only for CI.

```kotlin
buildTypes {
    create("ci") {
        initWith(getByName("debug"))
        matchingFallbacks += listOf("debug")
        isDebuggable = true
    }
}
```

Use it for:
- Faster builds
- Disabled analytics
- Mocked endpoints

---

## Flavor usage in CI

### Good uses

- Backend environment switching
- Feature gating
- White-label apps

### Bad uses

- Regional logic
- Runtime behavior differences
- Overlapping responsibilities

> If flavors affect logic, tests multiply.

---

## Mapping variants to pipelines

### Example

**Pull Request**
```bash
./gradlew assembleDebug testDebugUnitTest
```

**Main branch**
```bash
./gradlew assembleDebug connectedDebugAndroidTest
```

**Release**
```bash
./gradlew bundleRelease
```

---

## Testing strategy per variant

- Unit tests → `debug` only
- Integration tests → `debug` / `ci`
- UI tests → limited variants
- Release tests → smoke tests only

Never run full UI suites on every variant.

---

## Variant-specific configuration

Use `BuildConfig` flags responsibly.

```kotlin
buildConfigField("Boolean", "USE_MOCKS", "true")
```

Avoid branching production logic heavily by variant.

---

## Common anti-patterns

- One flavor per environment *and* per client
- Release variant tested on every PR
- Variant-specific business logic
- Dynamic variant generation

These scale terribly.

---

## Senior rules of thumb

1. If CI builds more than 2 variants per pipeline, it’s suspicious
2. Release variants belong to release pipelines
3. Variants exist to **reduce**, not increase, complexity
4. CI should never discover variants it doesn’t need

---

## Mental model

> Build variants are a **matrix multiplier**. Control the matrix or pay the cost.

---

## Interview takeaway

**Senior Android developers design build variants intentionally for CI**, limiting variant explosion, mapping variants to pipeline stages, and aggressively filtering unnecessary builds to keep feedback fast and reliable.

