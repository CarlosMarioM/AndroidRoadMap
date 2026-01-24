package a_phase.b_Coroutines_Fundamentals.a_Suspend_Functions.examples

/**
 * This file demonstrates key aspects of `suspend` functions in Kotlin Coroutines.
 * It covers basic usage, how `suspend` doesn't imply background execution,
 * `withContext` for main-safety, and cooperative cancellation.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" SuspendFunctionsExample.kt -include-runtime -d SuspendFunctionsExample.jar
 * java -jar SuspendFunctionsExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

// --- 1. Basic Suspend Function ---
// A suspend function can be paused and resumed. It does NOT automatically run on a background thread.
suspend fun performTaskA() {
    println("[${Thread.currentThread().name}] Task A started.")
    delay(500L) // This is a suspension point. The coroutine pauses, the thread is NOT blocked.
    println("[${Thread.currentThread().name}] Task A finished.")
}

// --- 2. Suspend Function that calls another Suspend Function ---
suspend fun performTaskB() {
    println("[${Thread.currentThread().name}] Task B started.")
    performTaskA() // Calling another suspend function
    delay(300L)
    println("[${Thread.currentThread().name}] Task B finished.")
}

// --- 3. Main-Safe Suspend Function (using withContext) ---
// This function ensures that its blocking or long-running work is moved to a background dispatcher.
// The caller doesn't need to know about thread switching.
suspend fun fetchDataFromNetwork(): String {
    println("[${Thread.currentThread().name}] Entering a_phase.b_Coroutines_Fundamentals.a_Suspend_Functions.examples.fetchDataFromNetwork. Moving to IO dispatcher...")
    return withContext(Dispatchers.IO) { // Switch to IO dispatcher
        println("[${Thread.currentThread().name}] Performing network request (blocking sim)...")
        delay(1200L) // Simulate a blocking network call on IO thread
        "Data from Server"
    }
}

// --- 4. Cooperative Cancellation ---
// A suspend function must cooperate with cancellation.
suspend fun performCancellableWork() = coroutineScope { // 'coroutineScope' creates a new scope for structured concurrency
    println("[${Thread.currentThread().name}] Cancellable work started.")
    try {
        var count = 0
        while (isActive) { // Check `isActive` status of the coroutine's Job
            println("[${Thread.currentThread().name}] Working... ${count++}")
            delay(100L) // Suspension point, also checks for cancellation
        }
    } catch (e: CancellationException) {
        println("[${Thread.currentThread().name}] Cancellable work was cancelled: ${e.message}")
    } finally {
        println("[${Thread.currentThread().name}] Cancellable work finally block executed.")
    }
}


fun main() = runBlocking { // This is a main-thread blocking coroutine scope for demonstration
    println("--- Suspend Functions Examples ---")
    println("[${Thread.currentThread().name}] Main started.")

    // Example 1 & 2: Sequential Suspend Function Calls
    println("\n--- Sequential Suspend Calls ---")
    performTaskB() // This will call a_phase.b_Coroutines_Fundamentals.a_Suspend_Functions.examples.performTaskA internally
    println("[${Thread.currentThread().name}] Sequential calls complete.")

    // Example 3: Main-Safe Suspend Function
    println("\n--- Main-Safe Suspend Function ---")
    val data = fetchDataFromNetwork() // This call is main-safe, it switches threads internally
    println("[${Thread.currentThread().name}] Received data: '$data'. Back on main-ish thread.")
    println("[${Thread.currentThread().name}] Main-Safe Suspend Function complete.")

    // Example 4: Cooperative Cancellation
    println("\n--- Cooperative Cancellation ---")
    val cancellableJob = launch { // Launch a new coroutine
        performCancellableWork()
    }
    delay(500L) // Let it run for a bit
    println("[${Thread.currentThread().name}] Cancelling the cancellable job...")
    cancellableJob.cancel() // Request cancellation
    cancellableJob.join() // Wait for it to finish (including finally block)
    println("[${Thread.currentThread().name}] Cooperative Cancellation complete.")

    println("[${Thread.currentThread().name}] Main finished.")
    println("------------------------------------")
}
