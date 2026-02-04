package com.example.androidroadmap.features.examples.weather_app.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.features.examples.weather_app.WeatherUiState
import com.example.androidroadmap.features.examples.weather_app.WeatherViewModel
import com.example.androidroadmap.domain.model.weather.SysResponse
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is
        WeatherUiState.Loading -> LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp),
            color = Color.White
        )
        else -> {}
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (uiState) {
                    is WeatherUiState.Success -> (uiState as WeatherUiState.Success).data.name
                    else -> "-"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(60.dp))

            when (uiState) {
                is WeatherUiState.Success ->
                    Text(
                        text =
                            "${(uiState as WeatherUiState.Success).data.main.temp} °",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.White
                    )

                else -> CircularProgressIndicator()
            }

            Text(
                text = when (uiState) {
                    is WeatherUiState.Success -> "${(uiState as WeatherUiState.Success).data.weather.firstOrNull()?.main}"
                    else -> "-"
                },
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            when (uiState) {
                is WeatherUiState.Success -> DescriptionCard(
                    (uiState
                            as WeatherUiState.Success).data.sys
                )
                else -> LinearProgressIndicator()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DescriptionCard(response: SysResponse) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(32.dp),
        border = null
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sunrise & Sunset",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Sunrise is at ${epochToDateTime(response.sunrise)}", color = Color.White)
                Text("Sunset is at ${epochToDateTime(response.sunset)}", color = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun epochToDateTime(value: Long): String {
    val instant = Instant.ofEpochSecond(value)
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return formatter.format(zonedDateTime)
}
