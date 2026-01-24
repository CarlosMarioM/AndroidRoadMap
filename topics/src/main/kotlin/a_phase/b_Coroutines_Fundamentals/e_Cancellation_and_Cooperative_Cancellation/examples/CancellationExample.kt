package a_phase.b_Coroutines_Fundamentals.e_Cancellation_and_Cooperative_Cancellation.examples

/**
 * This file demonstrates various aspects of Cancellation and Cooperative Cancellation in Kotlin Coroutines.
 * It covers how coroutines respond to cancellation, handling `CancellationException`,
 * using `NonCancellable` for cleanup, and `withTimeout`.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" CancellationExample.kt -include-runtime -d CancellationExample.jar
 * java -jar CancellationExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

// --- 1. Cooperative Function ---
// This function respects cancellation through suspension points (delay) and `isActive` checks.
suspend fun performCooperativeWork(job: Job) {
    println("[${Thread.currentThread().name}] Cooperative work started.")
    try {
        var i = 0
        while (job.isActive && i < 5) { // Check `isActive` status
            println("[${Thread.currentThread().name}] Cooperative work: step ${i++}")
            delay(200L) // Suspension point, also checks cancellation
        }
    } catch (e: CancellationException) {
        println("[${Thread.currentThread().name}] Cooperative work caught CancellationException: ${e.message}")
    } finally {
        // This block always executes on cancellation
        println("[${Thread.currentThread().name}] Cooperative work finally block for cleanup.")
    }
    println("[${Thread.currentThread().name}] Cooperative work finished/cancelled.")
}

// --- 2. Non-Cooperative Function ---
// This function does not have suspension points or `isActive` checks.
suspend fun performNonCooperativeWork() {
    println("[${Thread.currentThread().name}] Non-cooperative work started.")
    var i = 0
    while (i < 1_000_000_000) { // Long-running, CPU-bound loop
        i++
        if (i % 100_000_000 == 0) {
            println("[${Thread.currentThread().name}] Non-cooperative work: still running ($i)...")
        }
    }
    println("[${Thread.currentThread().name}] Non-cooperative work finished.")
}


fun main() = runBlocking {
    println("--- Cancellation and Cooperative Cancellation Examples ---")
    println("[${Thread.currentThread().name}] Main started.")

    // --- Example 1: Basic Cancellation of Cooperative Work ---
    println("\n--- Basic Cancellation ---")
    val job1 = launch {
        performCooperativeWork(this.coroutineContext[Job]!!)
    }
    delay(500L) // Let it run for a bit
    println("[${Thread.currentThread().name}] Cancelling job1...")
    job1.cancel() // Request cancellation
    job1.join() // Wait for job1 to finish its cleanup
    println("[${Thread.currentThread().name}] Basic Cancellation example finished. Job1 isActive: ${job1.isActive}, isCancelled: ${job1.isCancelled}.")

    // --- Example 2: Cancellation of Non-Cooperative Work (will run to completion) ---
    // Note: The main thread will be blocked until the non-cooperative work is done,
    // even after `job2.cancel()` is called.
    println("\n--- Non-Cooperative Work Cancellation (Ineffective) ---")
    val job2 = launch(Dispatchers.Default) { // Launch on Default to avoid blocking main `runBlocking`
        performNonCooperativeWork()
    }
    delay(100L) // Give it a head start
    println("[${Thread.currentThread().name}] Cancelling non-cooperative job2...")
    job2.cancel() // Request cancellation
    job2.join() // Wait for job2 to actually finish (which it will, eventually)
    println("[${Thread.currentThread().name}] Non-Cooperative Work Cancellation example finished. Job2 isActive: ${job2.isActive}, isCancelled: ${job2.isCancelled}.")


    // --- Example 3: NonCancellable for Critical Cleanup ---
    println("\n--- NonCancellable for Critical Cleanup ---")
    val job3 = launch {
        try {
            println("[${Thread.currentThread().name}] Job3: Task started.")
            delay(500L)
        } catch (e: CancellationException) {
            println("[${Thread.currentThread().name}] Job3: Caught CancellationException. Proceeding with NonCancellable cleanup.")
            withContext(NonCancellable) { // Execute cleanup block in a non-cancellable context
                println("[${Thread.currentThread().name}] Job3: Performing critical cleanup (non-cancellable)...")
                delay(300L) // This delay will not be cancelled
                println("[${Thread.currentThread().name}] Job3: Critical cleanup finished.")
            }
        } finally {
            println("[${Thread.currentThread().name}] Job3: Finally block executed.")
        }
    }
    delay(100L)
    job3.cancel()
    job3.join()
    println("[${Thread.currentThread().name}] NonCancellable example finished.")


    // --- Example 4: withTimeout ---
    println("\n--- withTimeout ---")
    try {
        withTimeout(400L) {
            performCooperativeWork(this.coroutineContext[Job]!!)
        }
    } catch (e: TimeoutCancellationException) {
        println("[${Thread.currentThread().name}] withTimeout caught TimeoutCancellationException: ${e.message}")
    }
    println("[${Thread.currentThread().name}] withTimeout example finished.")


    println("[${Thread.currentThread().name}] Main finished.")
    println("----------------------------------------------")
}
