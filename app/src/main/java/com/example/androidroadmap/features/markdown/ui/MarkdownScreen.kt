import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.features.content_list.ContentListViewModel
import com.example.androidroadmap.features.markdown.MarkdownUiState
import com.example.androidroadmap.features.markdown.MarkdownViewModel
import com.example.androidroadmap.ui.CenteredError
import com.example.androidroadmap.ui.CenteredLoader
import com.example.androidroadmap.ui.theme.BackgroundDark

@Composable
fun MarkdownScreen(
    subtopicId: String,
    viewModel: MarkdownViewModel = viewModel(),
    onScrolledToEnd: () -> Unit = {},
    floatingActionClick: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val markwon = remember(context) {
        Markwon.builder(context)
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder
                        .codeBlockTextColor("#BCBEC4".toColorInt())
                        .codeBlockBackgroundColor("#2B2D30".toColorInt())
                        .codeBlockTypeface(android.graphics.Typeface.MONOSPACE)
                        .headingTypeface(android.graphics.Typeface.MONOSPACE)
                        .headingBreakHeight(0)
                        .codeBlockMargin(32)
                }
            })
            .build()
    }

    LaunchedEffect(subtopicId) { }

    LaunchedEffect(scrollState.value) {
        snapshotFlow { scrollState.value == scrollState.maxValue }
            .collect { isScrolledToEnd ->
                if (isScrolledToEnd) {
                    onScrolledToEnd()
                }
            }
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            floatingActionClick?.let {
                FloatingActionButton(onClick = it, containerColor = Color(0xFF9F79EE)) {
                    Text("Info", color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is MarkdownUiState.Loading -> CenteredLoader()
            is MarkdownUiState.Error -> CenteredError((uiState as MarkdownUiState.Error).message)
            is MarkdownUiState.Success -> AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),

                factory = { ctx ->
                    TextView(ctx).apply {
                        setTextColor("#BCBEC4".toColorInt())
                        textSize = 15f
                        setLineSpacing(0f, 1.2f)
                    }
                },
                update = { textView ->
                    markwon.setMarkdown(textView, (uiState as MarkdownUiState.Success).content ?: "")
                }
            )

        }

    }
}
