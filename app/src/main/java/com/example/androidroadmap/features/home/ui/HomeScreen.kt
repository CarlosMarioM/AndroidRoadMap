package com.example.androidroadmap.features.home.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.features.index.IndexActivity
import com.example.androidroadmap.theme.BackgroundDark
import com.example.androidroadmap.theme.TealAccent
import com.example.androidroadmap.theme.TextPrimary
import com.example.androidroadmap.theme.TextSecondary
import com.example.androidroadmap.ui.card.CardItem

private data class Route(val index : Int, val title : String, val onClick : () -> Unit)

@Composable
fun HomeScreen(){
    val context = LocalContext.current
    val routes = listOf(
        Route(0, "Index", onClick = {
            context.startActivity(Intent(context, IndexActivity::class.java))
        }),
        Route(1, "Examples",onClick =  {}),
        Route(2, "Practices",onClick =  {}),
        Route(3, "Challenges", onClick = {})

    )

    Scaffold {
        innerPadding ->
        Column (
            modifier = Modifier
                .background(BackgroundDark)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Home.kt",
                color = TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )

            Text(
                "Here you may find the resources, examples, practices and challenges along the app. ",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Thin,
                fontFamily = FontFamily.Monospace,
                modifier  = Modifier.padding(vertical = 0.dp)
                )

            Spacer(modifier = Modifier.padding(vertical = 40.dp))

            LazyColumn {
                items(routes) { route ->
                    CardItem(index = route.index, title = route.title, route.onClick)

                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() = HomeScreen()