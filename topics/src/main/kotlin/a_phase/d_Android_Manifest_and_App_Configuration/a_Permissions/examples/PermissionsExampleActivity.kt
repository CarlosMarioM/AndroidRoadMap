package a_phase.d_Android_Manifest_and_App_Configuration.a_Permissions.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class PermissionsExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationPermissionScreen()
        }
    }
}