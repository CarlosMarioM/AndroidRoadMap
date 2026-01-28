package a_phase.d_Android_Manifest_and_App_Configuration.a_Permissions.examples
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

/**
 * This file demonstrates the correct way to handle Android permissions,
 * distinguishing between "normal" and "dangerous" permissions as outlined
 * in the Android developer documentation.
 *
 * NORMAL PERMISSIONS (e.g., INTERNET):
 * - These are declared in the AndroidManifest.xml and are granted automatically
 *   at install time.
 * - No special runtime handling is required.
 * - Example declaration in AndroidManifest.xml:
 *   <uses-permission android:name="android.permission.INTERNET" />
 *
 * DANGEROUS PERMISSIONS (e.g., ACCESS_FINE_LOCATION, CAMERA):
 * - These require explicit user consent at runtime.
 * - The process involves:
 *   1. Declaring the permission in AndroidManifest.xml.
 *   2. Checking if the permission has already been granted.
 *   3. Requesting the permission from the user if it hasn't been granted.
 *   4. Handling the user's response (grant or denial).
 *   5. Gracefully degrading the feature if the permission is denied.
 *
 * This example focuses on the runtime handling of a dangerous permission (ACCESS_FINE_LOCATION).
 *
 * To make this example runnable, you would typically set it as the content
 * of your main activity like this:
 *
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContent {
 *             RoadMapAppTheme { // Or your app's theme
 *                 LocationPermissionScreen()
 *             }
 *         }
 *     }
 * }
 *
 * And you must declare the permission in your AndroidManifest.xml:
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *
 */
@Composable
fun LocationPermissionScreen() {

    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher for the permission request
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (permissionGranted) {
            // UI to show when permission is granted
            Text("Location permission has been granted.")
            Text("You can now access location-based features.")
        } else {
            // UI to show when permission is not granted
            Text("Location permission is required for this feature.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text("Request Location Permission")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationPermissionScreenPreview() {
    LocationPermissionScreen()
}
