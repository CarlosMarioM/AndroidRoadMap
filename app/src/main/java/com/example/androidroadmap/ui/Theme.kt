import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.androidroadmap.ui.OrangeAccent
import com.example.androidroadmap.ui.PurpleAccent
import com.example.androidroadmap.ui.TealAccent

@Composable
fun RoadMapAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = TealAccent,
            secondary = OrangeAccent,
            inversePrimary = PurpleAccent

        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}