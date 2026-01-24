package a_phase.b_Coroutines_Fundamentals.b_Coroutine_Scopes_and_Lifecycle_Awareness.examples

/**
 * This file demonstrates key concepts of Coroutine Scopes and Lifecycle Awareness.
 * It covers creating scopes, Job vs SupervisorJob, and CoroutineExceptionHandler.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" CoroutineScopesExample.kt -include-runtime -d CoroutineScopesExample.jar
 * java -jar CoroutineScopesExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

fun main() : Unit = runBlocking {
    println("--- Coroutine Scopes and Lifecycle Awareness Examples ---")

    // --- 1. Basic CoroutineScope and Job ---
    // A CoroutineScope combines a Job and other Context elements.
    // The Job is the lifecycle of the scope.
    println("\n--- Basic CoroutineScope and Job ---")
    val scopeWithJob = CoroutineScope(Job() + Dispatchers.Default)

    val job1 = scopeWithJob.launch {
        println("[${Thread.currentThread().name}] Job 1 started.")
        delay(300L)
        println("[${Thread.currentThread().name}] Job 1 finished.")
    }

    val job2 = scopeWithJob.launch {
        println("[${Thread.currentThread().name}] Job 2 started (will fail).")
        delay(100L)
        throw IllegalStateException("Job 2 failed!") // This will cancel the parent Job
    }

    try {
        job1.join()
        job2.join() // This will throw the exception here
    } catch (e: Exception) {
        println("Caught exception from job2 in main: ${e.message}")
    }

    // After job2 failed, the parent Job is cancelled, so all children are cancelled.
    // job1 might not even complete if job2 fails before job1 finishes.
    println("Scope's Job active: ${scopeWithJob.isActive}")
    scopeWithJob.cancel() // Explicitly cancel the scope if not already cancelled by child failure
    println("Basic CoroutineScope and Job complete.")


    // --- 2. SupervisorJob for Isolated Failures ---
    // SupervisorJob allows child coroutines to fail independently without cancelling siblings.
    println("\n--- SupervisorJob for Isolated Failures ---")
    val scopeWithSupervisorJob = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val supervisorJob1 = scopeWithSupervisorJob.launch {
        println("[${Thread.currentThread().name}] Supervisor Job 1 started.")
        delay(300L)
        println("[${Thread.currentThread().name}] Supervisor Job 1 finished.")
    }

    val supervisorJob2 = scopeWithSupervisorJob.launch {
        println("[${Thread.currentThread().name}] Supervisor Job 2 started (will fail).")
        delay(100L)
        throw IllegalStateException("Supervisor Job 2 failed!")
    }

    try {
        supervisorJob2.join() // Join the failing job to observe its failure
    } catch (e: Exception) {
        println("Caught exception from supervisorJob2 in main: ${e.message}")
    }

    // Job 1 is still active because SupervisorJob isolates failures
    supervisorJob1.join()
    println("Supervisor Job 1 active: ${supervisorJob1.isActive}")
    println("Scope's Job active: ${scopeWithSupervisorJob.isActive}") // The scope is still active!
    scopeWithSupervisorJob.cancel() // Must explicitly cancel SupervisorJob scopes
    println("SupervisorJob for Isolated Failures complete.")


    // --- 3. CoroutineExceptionHandler ---
    // Catches uncaught exceptions in root coroutines within a scope.
    println("\n--- CoroutineExceptionHandler ---")
    val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler caught: $exception")
    }
    val scopeWithHandler = CoroutineScope(Job() + Dispatchers.Default + handler)

    scopeWithHandler.launch {
        println("[${Thread.currentThread().name}] Coroutine with handler started (will fail).")
        delay(100L)
        throw IllegalArgumentException("Error in coroutine with handler!")
    }.join()

    println("Scope's Job active after handled error: ${scopeWithHandler.isActive}")
    scopeWithHandler.cancel()
    println("CoroutineExceptionHandler complete.")


    // Note: Android-specific scopes like viewModelScope and lifecycleScope are concepts
    // that rely on Android's lifecycle mechanisms and cannot be directly demonstrated
    // in a plain Kotlin console application without an Android environment.
    // They are essentially `CoroutineScope` instances tied to Android lifecycles.

    println("-------------------------------------------------")
}
