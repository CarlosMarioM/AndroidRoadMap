# UI Testing in Android

This document explains **UI testing strategies for Android**, including **Espresso for View-based UI** and **Compose Testing** for Jetpack Compose. It covers setup, examples, best practices, and senior-level considerations.

UI tests validate **user interactions and visual behavior** end-to-end.

---

## Tools and dependencies

```gradle
androidTestImplementation 'androidx.test.ext:junit:1.1.6'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
androidTestImplementation 'androidx.compose.ui:ui-test-junit4:1.6.0'
androidTestImplementation 'androidx.compose.ui:ui-test-manifest:1.6.0'
```

- **Espresso** → for traditional XML-based views
- **ComposeTestRule** → for Jetpack Compose UI
- **JUnit** → test runner framework

---

## Espresso Testing (View-based UI)

### Example
```kotlin
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginButton_click_showsWelcome() {
        onView(withId(R.id.username)).perform(typeText("Mario"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText("1234"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.welcome_text))
            .check(matches(withText("Welcome Mario")))
    }
}
```

- `onView(withId(...))` → locate UI element
- `perform(...)` → perform actions (click, typeText)
- `check(matches(...))` → assert UI state

### Best practices

- Keep tests **atomic and independent**
- Avoid network or DB dependency; use **mocked layers**
- Use **IdlingResources** to synchronize asynchronous work

---

## Compose Testing (Jetpack Compose UI)

### Example
```kotlin
@get:Rule val composeTestRule = createComposeRule()

@Test
fun loginButton_click_updatesWelcomeText() {
    composeTestRule.setContent {
        MyAppTheme {
            LoginScreen(viewModel = loginViewModel)
        }
    }

    composeTestRule.onNodeWithTag("username_input").performTextInput("Mario")
    composeTestRule.onNodeWithTag("password_input").performTextInput("1234")
    composeTestRule.onNodeWithTag("login_button").performClick()

    composeTestRule.onNodeWithTag("welcome_text").assertTextEquals("Welcome Mario")
}
```

- `onNodeWithTag` → locate composable by `Modifier.testTag`
- `performTextInput`, `performClick` → actions
- `assertTextEquals` → check state

### Best practices

- Tag **critical Composables** with `Modifier.testTag` for easier testing
- Keep tests **stateless**, rely on **ViewModel states**
- Combine with **unit tests** for underlying logic verification

---

## Senior-level best practices

1. **Separate unit and UI tests**: keep UI tests slower but realistic
2. **Mock network/database** to avoid flakiness
3. **Use Espresso for legacy views**, ComposeTestRule for Compose
4. **IdlingResources** or `runOnIdle` to wait for async operations
5. **CI/CD integration**: run tests on emulators or Firebase Test Lab
6. **Keep tests deterministic**: no random inputs or timing dependencies

---

## Mental model

> UI tests = verification that user interactions and visual state behave as expected, bridging the gap between unit and full end-to-end tests.

---

## Interview takeaway

**Senior Android developers know how to combine Espresso and Compose testing** to cover legacy and modern UI, ensuring user flows work correctly while keeping tests reliable and maintainable.

