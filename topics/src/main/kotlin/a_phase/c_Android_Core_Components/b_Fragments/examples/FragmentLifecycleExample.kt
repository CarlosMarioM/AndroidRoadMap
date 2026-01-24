package a_phase.c_Android_Core_Components.b_Fragments.examples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.core.os.bundleOf
import com.example.androidroadmap.topics.R

/**
 * This file demonstrates an Android Fragment's lifecycle,
 * its View lifecycle, and inter-Fragment communication using the Fragment Result API.
 */

open class BaseLifecycleFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("[${this::class.simpleName}] Fragment: onCreate() called. Arguments: ${arguments}")
        // Initialize non-UI components, parse arguments
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("[${this::class.simpleName}] Fragment: onViewCreated() called. View is ready.")
        // Perform view-related setup, observe LiveData, set up listeners
        // This is where you'd use viewLifecycleOwner
    }

    override fun onStart() {
        super.onStart()
        println("[${this::class.simpleName}] Fragment: onStart() called. Fragment visible.")
    }

    override fun onResume() {
        super.onResume()
        println("[${this::class.simpleName}] Fragment: onResume() called. Fragment interactive.")
    }

    override fun onPause() {
        super.onPause()
        println("[${this::class.simpleName}] Fragment: onPause() called. Fragment losing focus.")
    }

    override fun onStop() {
        super.onStop()
        println("[${this::class.simpleName}] Fragment: onStop() called. Fragment no longer visible.")
        // Stop UI-bound observers here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("[${this::class.simpleName}] Fragment: onDestroyView() called. View destroyed.")
        // Clear all references to the fragment's view to prevent memory leaks
    }

    override fun onDestroy() {
        super.onDestroy()
        println("[${this::class.simpleName}] Fragment: onDestroy() called. Fragment instance destroyed.")
        // Release all resources tied to the Fragment (not its view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("[${this::class.simpleName}] Fragment: onSaveInstanceState() called.")
        outState.putString("fragment_data", "some_value")
    }
}

class HostFragment : BaseLifecycleFragment() {
    private lateinit var statusTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusTextView = view.findViewById(R.id.host_fragment_status_text)
        statusTextView.text = "Host Fragment: Ready"

        // Add the PickerFragment to the container
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PickerFragment())
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("[${this::class.simpleName}] Setting up Fragment Result Listener.")
        setFragmentResultListener("selection_request") { requestKey, bundle ->
            handleFragmentResult(requestKey, bundle)
        }
    }

    private fun handleFragmentResult(requestKey: String, bundle: Bundle) {
        if (requestKey == "selection_request") {
            val selectedItem = bundle.getString("selected_item")
            statusTextView.text = "Received: $selectedItem"
            println("[${this::class.simpleName}] Received result from PickerFragment: '$selectedItem'")
        }
    }
}

class PickerFragment : BaseLifecycleFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.picker_select_option_a_button).setOnClickListener {
            sendSelectionResult("Option A")
        }

        view.findViewById<Button>(R.id.picker_select_option_b_button).setOnClickListener {
            sendSelectionResult("Option B")
        }
    }

    fun sendSelectionResult(item: String) {
        println("[${this::class.simpleName}] Sending selection result: '$item'")
        setFragmentResult("selection_request", bundleOf("selected_item" to item))
    }
}
