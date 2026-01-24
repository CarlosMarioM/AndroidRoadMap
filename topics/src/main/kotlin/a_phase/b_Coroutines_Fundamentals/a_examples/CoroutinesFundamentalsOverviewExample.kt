package a_phase.b_Coroutines_Fundamentals.a_examples

/**
 * This file demonstrates basic Coroutines Fundamentals.
 * It covers launching coroutines, switching dispatchers, and using async/await.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" CoroutinesFundamentalsOverviewExample.kt -include-runtime -d CoroutinesFundamentalsOverviewExample.jar
 * java -jar CoroutinesFundamentalsOverviewExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

// Simulate a network request that takes some time and returns a string
suspend fun fetchUserData(): String {
    println("[${Thread.currentThread().name}] Fetching user data...")
    delay(1000L) // Simulate network delay
    return "User: Alice"
}

// Simulate a database operation that takes some time and returns an integer
suspend fun saveLogEntry(log: String): Int {
    println("[${Thread.currentThread().name}] Saving log: '$log'...")
    delay(500L) // Simulate database write delay
    return 1 // Number of affected rows
}

fun main(): Unit = runBlocking { // `runBlocking` is a coroutine builder that blocks the main thread
    println("--- Coroutines Fundamentals Overview Examples ---")

    // --- 1. Basic Coroutine Launch ---
    // Launch a coroutine in the current scope. It's "fire-and-forget" if not joined.
    println("\n--- Basic Launch ---")
    val job = launch {
        println("[${Thread.currentThread().name}] Coroutine launched!")
        delay(200L) // Suspend for a bit
        println("[${Thread.currentThread().name}] Coroutine finished.")
    }
    println("[${Thread.currentThread().name}] Main thread continues...")
    job.join() // Wait for the launched coroutine to complete
    println("[${Thread.currentThread().name}] Basic Launch complete.")

    // --- 2. Dispatchers (main-safety with withContext) ---
    // Demonstrates switching execution context for main-safety
    println("\n--- Dispatchers and withContext ---")
    val userResult = withContext(Dispatchers.Default) { // Switched to a CPU-bound thread pool
        println("[${Thread.currentThread().name}] Starting intensive computation...")
        delay(300L) // Simulate computation
        "Computed Data"
    }
    println("[${Thread.currentThread().name}] a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result of computation: $userResult")

    // Simulating a UI update (would typically be on Main dispatcher in Android)
    launch(Dispatchers.Unconfined) { // Unconfined is a special dispatcher, often behaves like Main for simple cases
        println("[${Thread.currentThread().name}] Updating UI with: $userResult")
    }
    delay(100L) // Give UI update a chance to print

    // --- 3. async / await (getting a result) ---
    // Launch multiple tasks concurrently and wait for their results
    println("\n--- Async / Await ---")
    val deferredUserData = async(Dispatchers.IO) { // Runs on I/O dispatcher
        fetchUserData()
    }
    val deferredLogSave = async(Dispatchers.IO) { // Runs on I/O dispatcher
        saveLogEntry("User data fetched")
    }

    // Wait for results
    val userData = deferredUserData.await()
    val affectedRows = deferredLogSave.await()

    println("[${Thread.currentThread().name}] Combined Results: $userData, Logged $affectedRows rows.")
    println("[${Thread.currentThread().name}] Async / Await complete.")


    // --- 4. Custom CoroutineScope ---
    // Demonstrates how to create and manage a custom scope
    println("\n--- Custom CoroutineScope ---")
    val customScope = CoroutineScope(Dispatchers.Default + Job()) // Default dispatcher + new Job
    println("[${Thread.currentThread().name}] Custom scope created.")

    val customJob = customScope.launch {
        println("[${Thread.currentThread().name}] Coroutine in custom scope started.")
        delay(400L)
        println("[${Thread.currentThread().name}] Coroutine in custom scope finished.")
    }
    customJob.join() // Wait for custom job
    customScope.cancel() // Don't forget to cancel custom scopes!
    println("[${Thread.currentThread().name}] Custom scope cancelled.")


    println("----------------------------------------")
}
