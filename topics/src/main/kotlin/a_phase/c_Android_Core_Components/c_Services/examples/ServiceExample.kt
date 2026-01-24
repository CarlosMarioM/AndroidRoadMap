package a_phase.c_Android_Core_Components.c_Services.examples

/**
 * This file conceptually demonstrates an Android Service's lifecycle
 * and the differences between started and foreground services.
 * It's a simplified Kotlin file, not a full Android project.
 *
 * To run this example in a real Android project:
 * 1. Create a new Android project.
 * 2. Create a new Service class (e.g., `MyBackgroundService`).
 * 3. Copy the relevant code snippets into the Service class and an Activity.
 * 4. Declare the Service in your `AndroidManifest.xml`.
 * 5. Handle permissions for foreground services (`FOREGROUND_SERVICE`).
 */

// --- Conceptual Android Imports ---
// import android.app.Notification
// import android.app.NotificationChannel
// import android.app.NotificationManager
// import android.app.PendingIntent
// import android.app.Service
// import android.content.Context
// import android.content.Intent
// import android.os.Build
// import android.os.IBinder
// import androidx.core.app.NotificationCompat
// import androidx.core.app.ServiceCompat

// --- Conceptual Service Class ---
class ConceptualService {
    private var isRunning: Boolean = false

    fun onCreate() {
        println("[Service] onCreate() called. Service is being created.")
        // Perform one-time setup
    }

    // For started services
    fun onStartCommand(intent: Map<String, Any?>, flags: Int, startId: Int): Int {
        println("[Service] onStartCommand() called. Service received a start request.")
        isRunning = true
        // Handle the command, potentially start background work
        val action = intent?.get("ACTION")
        when (action) {
            "START_FOREGROUND" -> startForegroundConceptual()
            "STOP_SERVICE" -> stopSelfConceptual()
            else -> {
                println("[Service] Handling general start command. Starting a conceptual background task.")
                // Simulate background work on a separate thread/coroutine
                Thread { 
                    for (i in 1..5) {
                        if (!isRunning) break
                        println("[Service] Conceptual background task running... $i")
                        Thread.sleep(1000)
                    }
                    if (isRunning) { // If not stopped by external command
                        println("[Service] Conceptual background task finished. Stopping self.")
                        stopSelfConceptual()
                    }
                }.start()
            }
        }
        // Return codes like START_STICKY, START_NOT_STICKY, START_REDELIVER_INTENT
        return 1 // Conceptual START_STICKY
    }

    // For bound services
    fun onBind(intent: Map<String, Any?>): Any? /* Conceptual IBinder */ {
        println("[Service] onBind() called. Service is being bound.")
        // Return an IBinder to allow clients to interact with the service
        return null // Conceptual null IBinder
    }

    // For bound services
    fun onUnbind(intent: Map<String, Any?>): Boolean {
        println("[Service] onUnbind() called. Service is being unbound.")
        // Cleanup resources related to binding
        return false
    }

    fun onDestroy() {
        println("[Service] onDestroy() called. Service is being destroyed.")
        isRunning = false
        // Release all resources: stop threads, cancel coroutines, etc.
    }

    private fun startForegroundConceptual() {
        println("[Service] Starting foreground service conceptually.")
        // In real Android:
        // 1. Create a NotificationChannel (API 26+)
        // 2. Create a Notification (NotificationCompat.Builder)
        // 3. call startForeground(NOTIFICATION_ID, notification)
        // 4. On Android 12+, use ServiceCompat.startForegroundService(context, intent)
        println("[Service] (Conceptual) Foreground notification shown.")
    }

    fun stopSelfConceptual() {
        println("[Service] stopSelf() called. Requesting service to stop.")
        isRunning = false
        // In real Android: call stopSelf() or stopForeground(true/false)
    }
}

// --- Conceptual Activity / Client ---
class ConceptualClient {
    fun startService(action: String) {
        println("\n[Client] Requesting service to start with action: $action")
        val serviceIntent = mapOf("ACTION" to action)
        myService.onStartCommand(serviceIntent, 0, 0) // Direct call for conceptual demo
    }

    fun stopService() {
        println("\n[Client] Requesting service to stop.")
        myService.stopSelfConceptual() // Direct call for conceptual demo
    }

    // Example of starting foreground service (conceptual)
    fun startForegroundService() {
        println("\n[Client] Requesting foreground service to start.")
        myService.onStartCommand(mapOf("ACTION" to "START_FOREGROUND"), 0, 0)
    }
}

// Global instance for conceptual demo
val myService = ConceptualService()

fun main() {
    println("--- Conceptual Service Lifecycle Example ---")

    val client = ConceptualClient()

    // --- Scenario 1: Started Service ---
    println("\n--- Scenario 1: Started Service ---")
    myService.onCreate()
    client.startService("DEFAULT")
    Thread.sleep(2500) // Let background task run a bit
    client.stopService()
    myService.onDestroy() // Manual onDestroy for conceptual flow

    // --- Scenario 2: Foreground Service ---
    println("\n--- Scenario 2: Foreground Service ---")
    myService.onCreate()
    client.startForegroundService() // Starts as foreground
    Thread.sleep(2000) // Let it run in foreground
    client.stopService() // Stop the foreground service
    myService.onDestroy()

    // --- Scenario 3: Service started and then implicitly stopped by system ---
    println("\n--- Scenario 3: Service started and implicit stop ---")
    myService.onCreate()
    client.startService("DEFAULT")
    Thread.sleep(6000) // Let the conceptual task finish and stopSelf()
    myService.onDestroy() // Conceptual destroy after stopSelf()

    println("----------------------------------------")
}
