# Integration Testing in Android

This document explains **integration testing strategies for Android**, focusing on combining multiple components (ViewModels, repositories, database, network) to verify system behavior.

Integration tests verify that **units work correctly together**, bridging the gap between unit tests and UI tests.

---

## What is integration testing

- Tests **interactions between components**
- Can include:
  - ViewModel + Repository + Database
  - Repository + Network layer
  - Data flow from source to output
- Runs on JVM or device/emulator depending on dependencies
- Slower than unit tests but faster than full end-to-end tests

---

## Tools and dependencies

```gradle
androidTestImplementation 'androidx.test:core:1.5.0'
androidTestImplementation 'androidx.test:runner:1.5.2'
androidTestImplementation 'androidx.test:rules:1.5.0'
androidTestImplementation 'androidx.test.ext:junit:1.1.6'
androidTestImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
androidTestImplementation 'io.mockk:mockk-android:1.14.0'
androidTestImplementation 'androidx.room:room-testing:2.6.0'
```

- **AndroidX Test** → core framework for instrumentation tests
- **Room testing** → in-memory DB for testing persistence
- **Mockk** → mocking in instrumentation context
- **kotlinx-coroutines-test** → test flows and suspend functions

---

## Example: Repository + Database integration test

```kotlin
@HiltAndroidTest
class UserRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var db: UserDatabase
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).build()
        repository = UserRepository(db.userDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `insert and retrieve user`() = runTest {
        val user = User(id = 1, name = "Mario")
        repository.insertUser(user)

        val retrieved = repository.getUser(1)
        assertEquals(user, retrieved)
    }
}
```

- Uses **in-memory Room DB** for fast integration testing
- Verifies **repository correctly interacts with DAO**
- Can extend to **network + DB integration** using mocks or fake APIs

---

## Example: ViewModel + Repository integration test

```kotlin
@HiltAndroidTest
class UserViewModelTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    @Before fun setup() {
        hiltRule.inject()
        viewModel = UserViewModel(repository)
    }

    @Test fun `load user updates state`() = runTest {
        repository.insertUser(User(1, "Mario"))
        viewModel.loadUser(1)

        assertEquals(User(1, "Mario"), viewModel.user.value)
    }
}
```

- Ensures **ViewModel properly observes repository updates**
- Can include multiple layers and coroutines flows

---

## Senior-level best practices

1. Use **in-memory databases** for persistence tests to avoid side effects
2. Combine with **mock/fake network layers** to test repository behavior
3. Ensure **ViewModels observe flows correctly** in integration tests
4. Run tests on **CI/CD pipelines** with Android Emulator or Robolectric
5. Balance test scope: avoid overloading tests with full app dependencies

---

## Compose-specific considerations

- For UI + state integration tests, use **ComposeTestRule**
- Verify **state changes propagate correctly to Composables**
- Example: button click triggers repository update and UI change

---

## Mental model

> Integration tests = verification that your components play well together without requiring full UI.

---

## Interview takeaway

**Senior Android developers implement integration tests to validate multi-layer interactions**, ensuring ViewModels, repositories, database, and network components work together as expected.

