# Kotlin Coroutines on Android: Fundamentals

This document provides a foundational understanding of Kotlin Coroutines, focusing on their application within Android development to simplify asynchronous programming.

See runnable example: [`CoroutinesFundamentalsOverviewExample.kt`](examples/CoroutinesFundamentalsOverviewExample.kt)

---

## What is a Coroutine?

A **coroutine** is a light-weight thread. It's a concurrency design pattern that allows you to write asynchronous, non-blocking code in a sequential and easy-to-understand manner. Unlike traditional threads, coroutines don't block the underlying thread; instead, they *suspend* their execution and resume later. This makes them highly efficient and lightweight, enabling many coroutines to run on a single thread.

**Key Concept: `suspend` functions**
The `suspend` keyword marks a function or a lambda as one that can be paused and resumed later. `suspend` functions can only be called from other `suspend` functions or from a coroutine builder (like `launch` or `async`). They do not block the thread they are running on, but rather yield control back to the dispatcher when they encounter a long-running operation.

---

## Why Coroutines on Android?

On Android, coroutines are the recommended solution for managing long-running tasks that might otherwise block the main thread, causing your app to become unresponsive (ANRs). They offer:

*   **Lightweight:** Run many coroutines on a single thread due to suspension, saving memory over blocking and supporting many concurrent operations.
*   **Fewer memory leaks:** Use structured concurrency to run operations within a defined `CoroutineScope`.
*   **Built-in cancellation support:** Cancellation is propagated automatically through the running coroutine hierarchy.

*   **Jetpack integration:** Many Jetpack libraries provide full coroutines support and their own `CoroutineScope` for structured concurrency.

---

## CoroutineScope: The Foundation of Structured Concurrency

A `CoroutineScope` defines the lifecycle of coroutines. It's crucial for structured concurrency, ensuring that all coroutines launched within a scope are either completed or cancelled when the scope itself is cancelled. This helps prevent memory leaks and resource exhaustion.

*   **`viewModelScope`:** A predefined `CoroutineScope` provided by ViewModel KTX extensions. It automatically cancels coroutines when the ViewModel is cleared.
*   **`lifecycleScope`:** Similar to `viewModelScope` but tied to the lifecycle of an `Activity` or `Fragment`.

You can also create custom `CoroutineScope` instances for other specific lifecycles in your application.

---

## Coroutine Builders: `launch` vs `async`/`await`

Coroutine builders are functions that create new coroutines.

*   **`launch`**: Starts a new coroutine without blocking the current thread and returns a `Job`. It's typically used for "fire-and-forget" operations that don't need to return a result immediately.
    ```kotlin
    myCoroutineScope.launch {
        // Perform an operation that doesn't return a value
        delay(1000L) // Example suspend function
        println("Task finished")
    }
    ```

*   **`async`**: Starts a new coroutine and returns its result as a `Deferred<T>`. `Deferred` is a non-blocking future that can be `await()`ed to get the result. Use `async` when you need to perform an operation asynchronously and get a result from it.
    ```kotlin
    myCoroutineScope.launch {
        val result1 = async { performTask1() }
        val result2 = async { performTask2() }
        println("a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result of both tasks: ${result1.await()} and ${result2.await()}")
    }
    ```

*   **`await()`**: A suspend function on `Deferred<T>` that suspends the current coroutine until the deferred value is computed and then returns it.

---

## Avoiding `GlobalScope` (Warning!)

`GlobalScope` is a root `CoroutineScope` that lives for the entire lifetime of the application. Launching coroutines in `GlobalScope` does **not** adhere to structured concurrency, meaning they are not automatically cancelled and can lead to memory leaks or unexpected background work. **Avoid `GlobalScope` for application-specific logic.** Use a structured `CoroutineScope` tied to a lifecycle instead.

---

## Examples Overview

Based on the Guide to App Architecture, the examples in this topic make a network request and return the result to the main thread, where the app can then display the result to the user.

Specifically, the ViewModel Architecture component calls the repository layer on the main thread to trigger the network request. This guide iterates through various solutions that use coroutines to keep the main thread unblocked.

ViewModel includes a set of KTX extensions that work directly with coroutines. These extensions are in the `lifecycle-viewmodel-ktx` library and are used in this guide.

---


Dependency Info
To use coroutines in your Android project, add the following dependency to your app's build.gradle file:

```gradle

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
}
```
Executing in a Background Thread
Making a network request on the main thread causes it to wait, or block, until it receives a response. Since the thread is blocked, the OS isn't able to call onDraw(), which causes your app to freeze and potentially leads to an Application Not Responding (ANR) dialog.

The Repository Layer
First, let's look at the LoginRepository class making a synchronous request:


```kotlin
sealed class a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<out R> {
    data class Success<out T>(val data: T) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<T>()
    data class Error(val exception: Exception) : a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<Nothing>()
}

class LoginRepository(private val responseParser: LoginResponseParser) {
    private const val loginUrl = "https://example.com/login"

    // Function that makes the network request, blocking the current thread
    fun makeLoginRequest(jsonBody: String): a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<LoginResponse> {
        val url = URL(loginUrl)
        (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())
            return a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Success(responseParser.parse(inputStream))
        }
        return a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Error(Exception("Cannot open HttpURLConnection"))
    }
}
```
The Initial ViewModel
The LoginViewModel triggers the network request when the user clicks a button. In this state, it blocks the UI thread:

```kotlin

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {
    fun login(username: String, token: String) {
        val jsonBody = "{ username: \"$username\", token: \"$token\"}"
        // This blocks the main thread
        loginRepository.makeLoginRequest(jsonBody)
    }
}
```
Moving to a Coroutine
We can use viewModelScope.launch(Dispatchers.IO) to move the execution off the UI thread:

```kotlin

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {
    fun login(username: String, token: String) {
        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            loginRepository.makeLoginRequest(jsonBody)
        }
    }
}
```
Key Breakdown:

- viewModelScope: A predefined CoroutineScope included with ViewModel KTX extensions. It automatically cancels coroutines when the ViewModel is destroyed.

- launch: A function that creates a coroutine and dispatches execution.

- Dispatchers.IO: Indicates that this coroutine should execute on a thread reserved for I/O operations.

### Use Coroutines for Main-Safety
A function is main-safe when it doesn't block UI updates on the main thread. We can use withContext() to make makeLoginRequest main-safe.

### Updated Repository
```kotlin

class LoginRepository(...) {
    suspend fun makeLoginRequest(jsonBody: String): a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result<LoginResponse> {
        // Move the execution of the coroutine to the I/O dispatcher
        return withContext(Dispatchers.IO) {
            // Blocking network request code resides here
        }
    }
}
```
### Updated ViewModel
Now, the ViewModel can launch the coroutine on the main thread safely, because makeLoginRequest handles the thread switching internally:

```kotlin

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {
    fun login(username: String, token: String) {
        // Create a new coroutine on the UI thread
        viewModelScope.launch {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            // Suspend execution until makeLoginRequest finishes
            val result = loginRepository.makeLoginRequest(jsonBody)
            
            // Resume execution on the main thread to handle the result
            when (result) {
                is a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Success<LoginResponse> -> // Happy path
                else -> // Show error in UI
            }
        }
    }
}
### Handling Exceptions
To handle exceptions thrown by the Repository layer, use a standard try-catch block within your coroutine:

```kotlin

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {
    fun login(username: String, token: String) {
        viewModelScope.launch {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            val result = try {
                loginRepository.makeLoginRequest(jsonBody)
            } catch(e: Exception) {
                a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Error(Exception("Network request failed"))
            }
            
            when (result) {
                is a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.Success<LoginResponse> -> // Happy path
                else -> // Show error in UI
            }
        }
    }
}
```
### Additional Resources
Coroutines overview (JetBrains)

Coroutines guide (JetBrains)

Improve app performance with Kotlin coroutines