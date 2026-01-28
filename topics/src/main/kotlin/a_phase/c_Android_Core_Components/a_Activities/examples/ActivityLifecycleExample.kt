package a_phase.c_Android_Core_Components.a_Activities.examples

/**
 * This file conceptually demonstrates an Android Activity's lifecycle and state management.
 * It's a simplified Kotlin file, not a full Android project, illustrating key lifecycle methods,
 * how state can be saved and restored (`onSaveInstanceState`), and the conceptual role of
 * ViewModel and SavedStateHandle for persisting UI state across process death.
 *
 * To run this example in a real Android project:
 * 1. Create a new Android project.
 * 2. Create a new Activity class (e.g., `MainActivity`).
 * 3. Copy the relevant code snippets into the `MainActivity` and its associated ViewModel.
 * 4. Ensure you have the necessary ViewModel and SavedStateHandle dependencies in your build.gradle.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory // Added import
import com.example.androidroadmap.topics.R

class MyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var counter: Int = savedStateHandle.get<Int>("counter") ?: 0
    var message: String = savedStateHandle.get<String>("message") ?: "Hello"

    fun incrementCounter() {
        counter++
        savedStateHandle["counter"] = counter
    }

    fun doSomeWork() {
        println("ViewModel: Doing some background work...")
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel: onCleared() called. Releasing resources.")
    }

    class Factory(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MyViewModel(handle) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


class MyActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private lateinit var lifecycleCounterText: TextView
    private lateinit var lifecycleIncrementButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Activity: onCreate() called.")

        // Set up the ViewModel
        myViewModel = ViewModelProvider(
            this,
            MyViewModel.Factory(this, intent.extras)
        )[MyViewModel::class.java]

        setContentView(R.layout.activity_lifecycle_example)

        lifecycleCounterText = findViewById(R.id.lifecycle_counter_text)
        lifecycleIncrementButton = findViewById(R.id.lifecycle_increment_button)

        lifecycleCounterText.text = "Activity Lifecycle Example - Counter: ${myViewModel.counter}"

        lifecycleIncrementButton.setOnClickListener {
            myViewModel.incrementCounter()
            lifecycleCounterText.text = "Activity Lifecycle Example - Counter: ${myViewModel.counter}"
            println("Activity: Counter after interaction: ${myViewModel.counter}")
        }

        // Restore state for counter from ViewModel (handled by SavedStateHandle)
        println("Activity: Current counter from ViewModel: ${myViewModel.counter}")
    }

    override fun onStart() {
        super.onStart()
        println("Activity: onStart() called. Activity becoming visible.")
    }

    override fun onResume() {
        super.onResume()
        println("Activity: onResume() called. Activity is interactive.")
    }

    override fun onPause() {
        super.onPause()
        println("Activity: onPause() called. Activity partially obscured/losing focus.")
    }

    override fun onStop() {
        super.onStop()
        println("Activity: onStop() called. Activity no longer visible.")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Activity: onDestroy() called.")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("Activity: onSaveInstanceState() called. Saving small UI state.")
        // ViewModel's SavedStateHandle automatically saves state
    }
}


