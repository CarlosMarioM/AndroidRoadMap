package com.example.androidroadmap.features.examples.finance_simulator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.domain.model.massive.TickerDetails
import com.example.androidroadmap.domain.model.massive.TickerResponse
import com.example.androidroadmap.domain.model.massive.TickerResult
import com.example.androidroadmap.ui.BackgroundDark
import com.example.androidroadmap.ui.CardSurface
import com.example.androidroadmap.ui.composables.CenteredError
import com.example.androidroadmap.ui.composables.Header


@Composable
fun FinanceSimulatorScreen(onTickerClicked: (TickerResult) -> Unit) {
    Scaffold { innerPadding ->
        Column(
            Modifier
                .background(BackgroundDark)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)

        ) {
            Header("Finance Simulator", "An example of Flow usage")
            CoinSelector()
            Spacer(Modifier.height(60.dp))
            Chart(onTickerClicked)
            Spacer(Modifier.height(60.dp))
            Bar()
        }
    }

}

@Composable
private fun CoinSelector() {
    Box(
        Modifier
            .background(CardSurface)
            .height(100.dp)
            .width(500.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {
                Text(
                    "Search",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .height(45.dp)
                        .width(50.dp)
                        .background(Color.White)
                )

            }
            Text(
                "COIN",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 50.sp,
                letterSpacing = 50.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun Chart(onTickerClicked: (TickerResult) -> Unit, viewModel: FinanceSimulatorViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(
        Modifier
            .background(CardSurface)
            .height(400.dp)
            .width(500.dp)
    ) {
        when (state) {
            is FinanceUiState.Success<*> -> LazyColumn {
                if ((state as FinanceUiState.Success<*>).data is TickerResponse) {
                    items((state as FinanceUiState.Success<TickerResponse>).data.results) {
                        TickerItem(it, onTickerClicked)
                    }
                }

            }
            is FinanceUiState.Error -> CenteredError(
                (state as FinanceUiState.Error).exception.message ?: "Unknown Error"
            )
            is FinanceUiState.Loading -> CircularProgressIndicator()
        }
    }
}

@Composable
fun TickerItem(ticker: TickerResult, onTickerClicked: (TickerResult) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                true, onClick = {
                   onTickerClicked(ticker)
                })
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ticker.ticker,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White // Dark theme look
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = ticker.market.uppercase(),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
            Text(
                text = ticker.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val statusColor = if (ticker.active) Color(0xFF00C805) else Color.Red
            Text(
                text = if (ticker.active) "ACTIVE" else "DELISTED",
                color = statusColor,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = ticker.primaryExchange ?: "OTC",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun Bar() {
    Box(
        Modifier
            .background(CardSurface)
            .height(60.dp)
            .width(500.dp)
    )
}

@Preview
@Composable
fun FinanceSimulatorScreenPreview() = FinanceSimulatorScreen({})