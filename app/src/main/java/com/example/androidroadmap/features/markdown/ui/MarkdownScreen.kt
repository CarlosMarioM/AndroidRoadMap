import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.example.androidroadmap.R
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import org.commonmark.node.Text
import androidx.core.graphics.toColorInt

@Composable
fun MarkdownScreen(markdown: String) {
    val context = LocalContext.current



    val markwon = remember(context) {
        Markwon.builder(context)
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder
                        // Match the "IDE" colors from the design
                        .codeBlockTextColor("#1E1F22".toColorInt())
                        .codeBlockBackgroundColor("#2B2D30".toColorInt())
                        .codeBlockTextColor("#BCBEC4".toColorInt())
                        .codeBlockTypeface(android.graphics.Typeface.MONOSPACE)

                        // Use raw pixels or helper for spacing
                        .headingTypeface(android.graphics.Typeface.MONOSPACE)
                        .headingBreakHeight(0)
                        .codeBlockMargin(32)
                }
            })
            .build()
    }

    Scaffold(
        containerColor = Color(0xFF1E1F22), // Set Scaffold background to Dark
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF9F79EE), // Use the purple accent from the image
                contentColor = Color.White
            ) {
                Text("Info")
            }
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            factory = { ctx ->
                TextView(ctx).apply {
                    // This ensures the TextView itself doesn't have a white background
                    setTextColor("#BCBEC4".toColorInt())
                    textSize = 15f
                    // Add some line spacing for better readability
                    setLineSpacing(0f, 1.2f)
                }
            },
            update = { textView ->
                markwon.setMarkdown(textView, markdown)
            }
        )
    }
}