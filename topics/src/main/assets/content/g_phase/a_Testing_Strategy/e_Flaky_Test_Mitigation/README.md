# Flaky Test Mitigation in Android

This document explains **what flaky tests are, why they happen in Android**, and **how senior Android developers prevent and fix them**.

Flaky tests are one of the biggest productivity killers in mature Android codebases.

---

## What is a flaky test

A **flaky test**:
- Passes sometimes
- Fails sometimes
- Fails **without code changes**

Flakiness destroys trust in the test suite and slows teams down.

> A test that cannot be trusted is worse than no test.

---

## Common causes of flaky tests in Android

### 1. Asynchronous operations

- Coroutines not synchronized
- Background threads still running
- Delays, timers, or retries

```kotlin
// ❌ Flaky
viewModel.load()
assertEquals(State.Success, viewModel.state.value)
```

---

### 2. UI synchronization issues

- Espresso acting before UI is idle
- Compose recomposition not finished
- Animations still running

---

### 3. Real network or database usage

- Unstable network
- Backend changes
- Slow I/O

---

### 4. Shared or leaking state

- Static singletons
- Global dispatchers
- Tests depending on execution order

---

### 5. Time-based logic

- `delay()`
- `System.currentTimeMillis()`
- Expiring tokens

---

## Core mitigation strategies

### 1. Control time explicitly

Use `kotlinx-coroutines-test`.

```kotlin
@Test
fun emitsAfterDelay() = runTest {
    advanceTimeBy(1_000)
    advanceUntilIdle()
}
```

Never rely on real time in tests.

---

### 2. Control dispatchers

Inject dispatchers instead of using `Dispatchers.IO` or `Dispatchers.Main` directly.

```kotlin
class a_phase.c_Android_Core_Components.a_Activities.examples.MyViewModel(private val dispatcher: CoroutineDispatcher)
```

```kotlin
val testDispatcher = StandardTestDispatcher()
```

---

### 3. Use Idling Resources (Espresso)

```kotlin
IdlingRegistry.getInstance().register(myIdlingResource)
```

- Synchronizes Espresso with async work
- Required for networking and background jobs

---

### 4. Disable animations

Animations cause timing issues.

```bash
adb shell settings put global animator_duration_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global window_animation_scale 0
```

---

### 5. Prefer fakes over mocks

- Fakes reduce timing and interaction flakiness
- Mocks fail when internal implementation changes

---

## Compose-specific mitigation

### Use semantics, not visuals

```kotlin
Modifier.testTag("login_button")
```

Avoid matching by text or layout position.

---

### Wait for idle state

```kotlin
composeTestRule.waitForIdle()
```

Or:

```kotlin
composeTestRule.runOnIdle { }
```

---

## Espresso-specific mitigation

### Avoid Thread.sleep

```kotlin
// ❌ Never do this
Thread.sleep(1000)
```

Always synchronize via IdlingResources.

---

## Structural strategies (senior-level)

1. **Test pyramid discipline**
   - Many unit tests
   - Fewer integration tests
   - Minimal UI tests

2. **Hermetic tests**
   - No network
   - No shared state
   - Deterministic inputs

3. **One assertion per behavior**

4. **Fail fast**
   - If setup is wrong, crash immediately

---

## How to debug flaky tests

1. Run the test in a loop
   ```bash
   ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...
   ```

2. Add logging around async boundaries
3. Identify race conditions
4. Remove real dependencies one by one

---

## When to delete a test

Delete or rewrite a test if:
- It fails randomly
- It tests UI cosmetics
- It duplicates lower-level coverage

Bad tests cost more than missing tests.

---

## Mental model

> Flaky tests are a **design problem**, not a testing problem.

---

## Interview takeaway

**Senior Android developers aggressively eliminate flakiness** by controlling time, dispatchers, and dependencies, and by writing deterministic, hermetic tests that can be trusted.

