package a_phase.c_Android_Core_Components.d_BroadcastReceivers.examples

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle // For Activity
import androidx.appcompat.app.AppCompatActivity // For Activity
import android.view.ViewGroup // For Activity layout
import android.widget.TextView // For Activity layout
import android.widget.Button // For Activity layout
import com.example.androidroadmap.topics.R

/**
 * This file demonstrates an Android BroadcastReceiver.
 * It illustrates the `onReceive` method and the distinction between static (manifest-declared)
 * and dynamic (runtime-registered) receivers.
 *
 * This is a simplified example, focusing on the basics of BroadcastReceivers.
 */

// --- Simple Worker (for delegating heavy work) ---
// In a real Android project, this would be a WorkManager Worker
class SimpleWorker {
    fun doWork(context: Context, intentAction: String) {
        println("  [Worker] Delegated work for action: '$intentAction'. Running long task...")
        Thread.sleep(1500) // Simulate long-running work
        println("  [Worker] Delegated work finished for action: '$intentAction'.")
    }
}

// --- Real BroadcastReceiver ---
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("\nonReceive() called! Action: '${intent.action}'. Extras: ${intent.extras}")

        val intentAction = intent.action ?: "UNKNOWN_ACTION"

        // For this conceptual demo, we'll directly call our simple worker.
        // In a real app, this would typically enqueue WorkManager.
        println("Delegating heavy work to SimpleWorker.")
        SimpleWorker().doWork(context, intentAction)

        println("onReceive() finished. (Very quickly!)")
    }


}

class MyActivity : AppCompatActivity() {
    private val dynamicReceiver = MyBroadcastReceiver()
    private val filter = IntentFilter().apply {
        addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        addAction("com.example.CUSTOM_APP_EVENT")
    }
    private lateinit var statusTextView: TextView
    private lateinit var sendAirplaneModeButton: Button
    private lateinit var sendCustomEventButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast_receiver_example)
        println("[MyActivity] onCreate() called.")

        statusTextView = findViewById(R.id.broadcast_status_text)
        sendAirplaneModeButton = findViewById(R.id.send_airplane_mode_button)
        sendCustomEventButton = findViewById(R.id.send_custom_event_button)

        statusTextView.text = "Broadcast Receiver Example: Ready"

        sendAirplaneModeButton.setOnClickListener {
            sendBroadcast(Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).apply {
                putExtra("state", true) // Simulate airplane mode ON
            })
        }

        sendCustomEventButton.setOnClickListener {
            sendBroadcast(Intent("com.example.CUSTOM_APP_EVENT").apply {
                putExtra("payload", "Hello from Activity!")
            })
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the dynamic receiver
        registerReceiver(dynamicReceiver, filter)
        println("[MyActivity] onResume() called. Registered dynamic receiver.")
    }

    override fun onPause() {
        super.onPause()
        // Unregister the dynamic receiver
        unregisterReceiver(dynamicReceiver)
        println("[MyActivity] onPause() called. Unregistered dynamic receiver.")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("[MyActivity] onDestroy() called.")
    }
}


