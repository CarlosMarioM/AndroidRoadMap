# CI/CD Test Automation for Android

This document explains **how to design, automate, and scale Android tests in CI/CD**, focusing on **reliability, speed, and signal quality**.

Test automation is not about running *all* tests — it’s about running the **right tests at the right time**.

---

## What test automation means in CI/CD

In CI/CD, test automation is:
- Automatic execution of tests on every change
- Deterministic and repeatable
- Designed to give **fast feedback**

Bad automation:
- Slow
- Flaky
- Ignored by developers

---

## The Android test pyramid (reality-based)

### Ideal distribution

1. **Unit tests (JVM)** – ~70–80%
2. **Integration tests** – ~15–20%
3. **UI / Instrumentation tests** – ~5–10%

Android teams usually fail by:
- Over-investing in UI tests
- Under-investing in unit tests

---

## Test types and where they run

| Test type | Gradle task | Runs on |
|---------|------------|--------|
| Unit | `testDebugUnitTest` | JVM |
| Integration | `test` / `androidTest` | JVM / Emulator |
| UI (Espresso/Compose) | `connectedDebugAndroidTest` | Emulator / Device |

---

## CI pipeline structure (recommended)

### Pull Request (fast feedback)

```bash
./gradlew lint testDebugUnitTest
```

- < 10 minutes
- No emulator
- High signal

---

### Main branch / nightly

```bash
./gradlew testDebugUnitTest connectedDebugAndroidTest
```

- Integration + limited UI tests
- Emulator required

---

### Release pipeline

```bash
./gradlew bundleRelease
./gradlew connectedReleaseAndroidTest
```

- Full confidence
- Slow but infrequent

---

## Emulator strategy

### Best practices

- Use **x86_64 system images**
- Disable animations
- Reuse emulator snapshot
- One emulator per job

```bash
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

---

## Sharding UI tests

Split tests across multiple emulators.

```bash
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.numShards=4 \
  -Pandroid.testInstrumentationRunnerArguments.shardIndex=0
```

Reduces UI test time dramatically.

---

## Flakiness control in CI

### Mandatory rules

1. No real network
2. No real time
3. No shared state
4. No `Thread.sleep`

### CI-only retries (carefully)

```yaml
retry: 2
```

Retries hide symptoms — never the cure.

---

## Test data management

- Use fakes or in-memory DBs
- Reset state before every test
- Avoid static singletons

---

## Reporting and visibility

### Required outputs

- JUnit XML reports
- Test duration
- Failure logs

### Why this matters

- Identify slow tests
- Detect flaky patterns
- Improve feedback quality

---

## Security and secrets

- Never hardcode API keys
- Use CI secrets manager
- Mock authenticated APIs

---

## When automation becomes harmful

- UI tests block every PR
- Developers ignore failures
- CI time > feedback value

At that point, reduce scope.

---

## Senior-level checklist

- JVM tests first
- UI tests last
- Deterministic inputs
- Sharded emulators
- Fast PR feedback
- Observability in failures

---

## Mental model

> CI test automation is a **feedback system**, not a safety net.

---

## Interview takeaway

**Senior Android developers design test automation strategically**, balancing speed, confidence, and cost by running the right tests at the right stages of CI/CD.

