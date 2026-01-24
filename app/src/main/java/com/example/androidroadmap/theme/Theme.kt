import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.androidroadmap.features.landing.LandingScreen

@Composable
fun RoadMapAppTheme() {
    val navController = rememberNavController()
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LandingScreen(
                appName = "RoadMap",
            )
        }
    }
}