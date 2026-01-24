package a_phase.b_Coroutines_Fundamentals.f_Exception_Propagation_in_Coroutines.examples

/**
 * This file demonstrates various aspects of Exception Propagation in Kotlin Coroutines.
 * It covers the differences in exception handling between `launch` and `async`,
 * the role of `SupervisorJob`, and the use of `CoroutineExceptionHandler`.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" ExceptionPropagationExample.kt -include-runtime -d ExceptionPropagationExample.jar
 * java -jar ExceptionPropagationExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

fun main() = runBlocking {
    println("--- Exception Propagation Examples ---")

    // --- 1. Exception with `launch` ---
    // Exceptions launched directly in a CoroutineScope (not as children of another coroutine
    // with a Job) propagate up and crash the scope if unhandled.
    println("\n--- Exception with `launch` ---")
    val scope = CoroutineScope(Job()) // Simple Job, propagates exceptions

    val launchJob = scope.launch {
        println("[${Thread.currentThread().name}] Launching coroutine that will fail...")
        delay(100L)
        throw IllegalStateException("Crash from launch!")
    }

    try {
        launchJob.join() // Will rethrow the exception from the child
    } catch (e: Exception) {
        println("Caught exception from launchJob: ${e.message}")
    } finally {
        println("LaunchJob finished. Scope active: ${scope.isActive}")
        scope.cancel() // Must cancel the scope if not already cancelled
    }


    // --- 2. Exception with `async` ---
    // Exceptions from `async` are deferred until `await()` is called.
    println("\n--- Exception with `async` ---")
    val asyncScope = CoroutineScope(Job())

    val deferredResult = asyncScope.async {
        println("[${Thread.currentThread().name}] Async coroutine that will fail...")
        delay(100L)
        throw IllegalStateException("Crash from async!")
    }

    println("Async coroutine launched, but no crash yet (exception is deferred).")
    try {
        deferredResult.await() // Exception is thrown here
    } catch (e: Exception) {
        println("Caught exception from deferredResult.await(): ${e.message}")
    } finally {
        println("Async coroutine finished. Scope active: ${asyncScope.isActive}")
        asyncScope.cancel()
    }


    // --- 3. SupervisorJob for Isolated Failures ---
    // A child's failure does not cancel siblings or the parent `supervisorScope`.
    println("\n--- SupervisorJob for Isolated Failures ---")
    val supervisorScope = CoroutineScope(SupervisorJob())

    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception in SupervisorJob child via handler: $exception")
    }

    val supervisorJob1 = supervisorScope.launch(handler) {
        println("[${Thread.currentThread().name}] Supervisor Child 1 started (will fail).")
        delay(100L)
        throw IllegalStateException("Failure in Supervisor Child 1!")
    }

    val supervisorJob2 = supervisorScope.launch {
        println("[${Thread.currentThread().name}] Supervisor Child 2 started (should survive).")
        delay(500L)
        println("[${Thread.currentThread().name}] Supervisor Child 2 finished.")
    }

    delay(200L) // Give Job1 a chance to fail
    println("Supervisor Job 1 active: ${supervisorJob1.isActive}")
    println("Supervisor Job 2 active: ${supervisorJob2.isActive}") // Should still be active!

    supervisorJob1.join() // Wait for job1 to complete (including its failure)
    supervisorJob2.join() // Wait for job2 to complete
    println("SupervisorJob example finished. Scope active: ${supervisorScope.isActive}")
    supervisorScope.cancel() // Don't forget to cancel the supervisor scope
    println("SupervisorJob example complete.")


    // --- 4. CoroutineExceptionHandler at Scope Level ---
    println("\n--- CoroutineExceptionHandler at Scope Level ---")
    val scopeHandler = CoroutineExceptionHandler { _, exception ->
        println("Scope-level handler caught: $exception")
    }
    val scopeWithHandler = CoroutineScope(Job() + scopeHandler)

    scopeWithHandler.launch {
        println("[${Thread.currentThread().name}] Coroutine in scopeWithHandler (will fail)...")
        delay(100L)
        throw IllegalArgumentException("Error caught by scope-level handler!")
    }.join()

    println("Scope-level handler example finished. Scope active: ${scopeWithHandler.isActive}")
    scopeWithHandler.cancel() // Parent Job was cancelled by child failure
    println("Scope-level handler example complete.")


    // --- 5. try/catch inside Coroutine ---
    println("\n--- try/catch inside Coroutine ---")
    launch {
        try {
            println("[${Thread.currentThread().name}] Coroutine with internal try/catch...")
            delay(100L)
            throw IllegalStateException("Caught internally!")
        } catch (e: IllegalStateException) {
            println("Successfully caught internal exception: ${e.message}")
        } catch (e: CancellationException) {
            println("Caught cancellation, rethrowing to propagate!")
            throw e // Always rethrow CancellationException
        } finally {
            println("Internal try/catch finally block.")
        }
    }.join()
    println("Internal try/catch example complete.")


    println("[${Thread.currentThread().name}] Main finished.")
    println("------------------------------------")
}
