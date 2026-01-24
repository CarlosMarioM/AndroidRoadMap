package a_phase.b_Coroutines_Fundamentals.c_Dispatchers.examples

/**
 * This file demonstrates the usage of various Coroutine Dispatchers in Kotlin.
 * It illustrates how different dispatchers affect thread execution for coroutines.
 *
 * To run this file, you need a Kotlin compiler with kotlinx-coroutines-core
 * dependency. You can compile and run it from the command line:
 * kotlinc -cp "kotlinx-coroutines-core-*.jar" DispatchersExample.kt -include-runtime -d DispatchersExample.jar
 * java -jar DispatchersExample.jar
 *
 * NOTE: Replace "kotlinx-coroutines-core-*.jar" with the actual path to the JAR.
 * You might need to download it or find it in your Gradle cache.
 * A simpler way for quick tests is to run directly in an IDE like IntelliJ IDEA.
 */

import kotlinx.coroutines.*

// This function simulates a CPU-intensive task
suspend fun doCpuIntensiveWork(tag: String) = withContext(Dispatchers.Default) {
    println("[${Thread.currentThread().name}] $tag: Starting CPU work...")
    var sum = 0L
    for (i in 1..1_000_000_000) { // Large loop to simulate heavy computation
        sum += i
    }
    println("[${Thread.currentThread().name}] $tag: Finished CPU work. Sum: $sum")
}

// This function simulates a blocking I/O operation
suspend fun doIoIntensiveWork(tag: String) = withContext(Dispatchers.IO) {
    println("[${Thread.currentThread().name}] $tag: Starting I/O work (simulated)...")
    delay(1500L) // Simulate network request or disk read
    println("[${Thread.currentThread().name}] $tag: Finished I/O work.")
}


fun main() = runBlocking {
    println("--- Dispatchers Examples ---")
    println("[${Thread.currentThread().name}] Main thread started.")

    // --- 1. Dispatchers.Default ---
    // For CPU-bound work. Uses a shared pool of on-demand created JVM threads.
    println("\n--- Dispatchers.Default (CPU-bound) ---")
    launch(Dispatchers.Default) {
        doCpuIntensiveWork("Default Dispatcher")
    }

    // --- 2. Dispatchers.IO ---
    // For blocking I/O operations. Uses a shared pool of on-demand created JVM threads.
    println("\n--- Dispatchers.IO (I/O-bound) ---")
    launch(Dispatchers.IO) {
        doIoIntensiveWork("IO Dispatcher")
    }

    // --- 3. Dispatchers.Main (Conceptual) ---
    // In a non-Android JVM app, Dispatchers.Main is usually not available unless a UI framework
    // (like JavaFX or Swing) provides one. Here, we'll simulate its role.
    println("\n--- Dispatchers.Main (Conceptual) ---")
    launch(Dispatchers.Default) { // Using Default as a stand-in for Main in this console app
        println("[${Thread.currentThread().name}] Updating UI (simulated) on Main-like Dispatcher.")
        // In Android, this would be: withContext(Dispatchers.Main) { /* update UI */ }
    }

    // --- 4. Dispatchers.Unconfined ---
    // Not confined to any specific thread. Runs in the current thread until the first suspension.
    // After suspension, it resumes in the thread that was used by the suspending function.
    println("\n--- Dispatchers.Unconfined ---")
    launch(Dispatchers.Unconfined) {
        println("[${Thread.currentThread().name}] Unconfined: Started in current thread.")
        delay(100L) // Suspension point
        println("[${Thread.currentThread().name}] Unconfined: Resumed in whatever thread the delay finished on.")
    }

    // --- Combining Dispatchers ---
    // Coroutine context can be composed. Here, we specify a name for the coroutine for logging.
    println("\n--- Combining Dispatchers and Context ---")
    launch(Dispatchers.IO + CoroutineName("MyWorkerCoroutine")) {
        println("[${Thread.currentThread().name}] Coroutine with custom name on IO Dispatcher.")
        doCpuIntensiveWork("Combined Dispatcher") // This will switch to Default within the suspend function
        println("[${Thread.currentThread().name}] Back to IO Dispatcher after CPU work.")
    }

    // Keep main running to observe background coroutines
    delay(3000L)
    println("[${Thread.currentThread().name}] Main thread finished.")
    println("----------------------------")
}
