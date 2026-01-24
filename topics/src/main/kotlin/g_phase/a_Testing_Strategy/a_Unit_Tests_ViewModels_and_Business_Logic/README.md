# Unit Testing in Android (ViewModels & Business Logic)

This document explains **unit testing strategies for Android**, focusing on **ViewModels and business logic**, how to structure tests, tools, and best practices for senior developers.

Unit tests ensure that **logic correctness is verified independently** of UI or framework dependencies.

---

## What is unit testing

- Tests small, isolated units of code (functions, classes)
- Runs on the **JVM** (no device required)
- Fast feedback loop
- Does not depend on Android framework classes (avoid `Context`, `Resources` directly)

---

## Tools and dependencies

```gradle
// app/build.gradle
dependencies {
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:5.2.0'
    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
}
```

- **JUnit** → test framework
- **Mockito** → mocking dependencies
- **kotlinx-coroutines-test** → test coroutines, dispatchers, and flows

---

## Testing ViewModels

### Example ViewModel
```kotlin
class CounterViewModel(private val repository: CounterRepository) : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    fun increment() {
        _count.value += 1
    }

    suspend fun loadCount() {
        _count.value = repository.fetchInitialCount()
    }
}
```

### Unit test
```kotlin
class CounterViewModelTest {
    private val repository = mock(CounterRepository::class.java)
    private lateinit var viewModel: CounterViewModel

    @Before
    fun setup() {
        viewModel = CounterViewModel(repository)
    }

    @Test
    fun `increment increases count`() = runTest {
        assertEquals(0, viewModel.count.value)
        viewModel.increment()
        assertEquals(1, viewModel.count.value)
    }

    @Test
    fun `loadCount fetches initial value`() = runTest {
        `when`(repository.fetchInitialCount()).thenReturn(5)

        viewModel.loadCount()

        assertEquals(5, viewModel.count.value)
    }
}
```

- Use `runTest` from `kotlinx-coroutines-test` to test suspend functions
- Mock dependencies to isolate logic from repositories, network, or DB

---

## Testing business logic

- Business logic can be extracted to **plain Kotlin classes** (POJOs/POKOs)
- Example:
```kotlin
class Calculator {
    fun add(a: Int, b: Int) = a + b
}
```

### Test
```kotlin
class CalculatorTest {
    private val calculator = Calculator()

    @Test
    fun `add returns correct sum`() {
        assertEquals(5, calculator.add(2, 3))
    }
}
```

- No Android dependencies → fast, isolated, reliable

---

## Senior-level best practices

1. **Isolate logic** from Android framework whenever possible
2. **Use coroutines test library** for ViewModel flows
3. **Mock external dependencies** (repositories, network) to focus on logic
4. **Write tests for all critical paths** (success, failure, edge cases)
5. **Measure coverage** and identify untested logic
6. **Keep tests fast**: JVM-only, no device/emulator required

---

## Compose-specific notes

- Composables are not unit-testable directly
- Test state holders (ViewModels, data classes) instead
- Use `ComposeTestRule` for integration/UI tests separately

---

## Mental model

> Unit tests = verification of business rules and state changes **independent of UI or framework**.

---

## Interview takeaway

**Senior Android developers write unit tests for ViewModels and business logic**, isolating dependencies, mocking collaborators, and testing coroutines and flows without involving UI or Android framework dependencies.

