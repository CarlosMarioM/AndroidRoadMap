package a_phase.b_Coroutines_Fundamentals.d_Structured_Concurrency.examples

/**
 * This file demonstrates Structured Concurrency concepts in Kotlin Coroutines.
 * It covers `coroutineScope`, `supervisorScope`, parent-child relationships,
 * error propagation, and `withTimeout` for managing coroutine lifetimes.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" StructuredConcurrencyExample.kt -include-runtime -d StructuredConcurrencyExample.jar
 * java -jar StructuredConcurrencyExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

// Simulate a task that might fail or take time
suspend fun performTask(name: String, shouldFail: Boolean = false, delayTime: Long = 200L) {
    println("[${Thread.currentThread().name}] Task '$name' started.")
    delay(delayTime)
    if (shouldFail) {
        println("[${Thread.currentThread().name}] Task '$name' FAILED.")
        throw IllegalStateException("Failure in $name!")
    }
    println("[${Thread.currentThread().name}] Task '$name' finished.")
}

fun main() = runBlocking {
    println("--- Structured Concurrency Examples ---")

    // --- 1. coroutineScope (Fail Fast) ---
    // A child failure cancels the entire coroutineScope and its siblings.
    println("\n--- coroutineScope (Fail Fast) ---")
    try {
        coroutineScope {
            launch { performTask("ScopeChild1") }
            launch { performTask("ScopeChild2", shouldFail = true, delayTime = 100L) }
            launch { performTask("ScopeChild3") }
        }
    } catch (e: Exception) {
        println("Caught exception from coroutineScope: ${e.message}")
    }
    println("--- End of example")
}