import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.androidroadmap.features.landing.LandingScreen
import com.example.androidroadmap.theme.OrangeAccent
import com.example.androidroadmap.theme.PurpleAccent
import com.example.androidroadmap.theme.TealAccent

@Composable
fun RoadMapAppTheme() {
    val navController = rememberNavController()
    MaterialTheme (
        colorScheme = MaterialTheme
            .colorScheme.copy(
                primary = TealAccent,
                secondary = OrangeAccent,
                inversePrimary = PurpleAccent
            )
    ){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LandingScreen(
                appName = "AndroidRoadMap.kt",
            )
        }
    }
}