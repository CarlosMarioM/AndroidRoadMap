package com.example.androidroadmap.features.examples.finance_simulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidroadmap.features.examples.finance_simulator.ticker.TickerActivity
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinanceSimulatorActivity : ComponentActivity() {
    private val viewModel : FinanceSimulatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceSimulatorScreen(
                onTickerClicked = { result ->
                    val intent = Intent(this, TickerActivity::class.java).apply {
                        putExtra("ticker", result.ticker)
                    }
                    startActivity(intent)
                }
            )
        }
    }
}