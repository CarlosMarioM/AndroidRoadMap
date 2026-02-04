package com.example.androidroadmap.features.examples.finance_simulator.ticker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.domain.model.massive.TickerDetails
import com.example.androidroadmap.domain.model.massive.TickerDetailsResponse
import com.example.androidroadmap.ui.composables.CenteredError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerScreen(viewModel: TickerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (state) {
                            is TickerUiState.Success<*> -> (state as TickerUiState.Success<TickerDetailsResponse>).data.results.name
                            else -> ""
                        }, fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Add to watchlist */ }) {
                        Icon(Icons.Default.StarBorder, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF131722))
            )
        },
        containerColor = Color(0xFF131722) // TradingView dark navy
    ) { padding ->
        when (state) {
            is TickerUiState.Loading -> CircularProgressIndicator()
            is TickerUiState.Error -> CenteredError((state as TickerUiState.Error).error)
            is TickerUiState.Success<*> -> {
                val details = (state as TickerUiState.Success<TickerDetailsResponse>).data
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // 1. Branding Header
                    item {
                        BrandingHeader(details.results)
                    }

                    // 2. Market Stats Grid
                    item {
                        MarketStatsGrid(details.results)
                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                    }

                    // 3. About Section
                    item {
                        AboutSection(details.results)
                    }

                    // 4. Contact/Address Info
                    item {
                        //AddressCard(details)
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BrandingHeader(details: TickerDetails) {
    Row(
        modifier = Modifier.padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Column {
            Text(details.name, style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Text(
                text = "${details.primaryExchange} • ${details.market.uppercase()}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MarketStatsGrid(details: TickerDetails) {
    val stats = listOf(
        "Market Cap" to (details.marketCap),
        "Employees" to (details.totalEmployees?.toString() ?: "—"),
        "SIC Code" to (details.sicCode ?: "—"),
        "List Date" to (details.listDate ?: "—")
    )

    Column {
        stats.chunked(2).forEach { rowStats ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowStats.forEach { (label, value) ->
                    Column(Modifier.weight(1f)) {
                        Text(
                            label,
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            value.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutSection(details: TickerDetails) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("About", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            text = details.description ?: "No description available.",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
        )

        details.homepageUrl?.let { url ->
            TextButton(onClick = { /* Open URL */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Visit Website", color = Color(0xFF2962FF))
                Icon(
                    Icons.Default.OpenInNew,
                    null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

