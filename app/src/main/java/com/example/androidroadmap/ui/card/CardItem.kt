package com.example.androidroadmap.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidroadmap.theme.CardSurface
import com.example.androidroadmap.theme.OrangeAccent
import com.example.androidroadmap.theme.PurpleAccent
import com.example.androidroadmap.theme.TealAccent
import com.example.androidroadmap.theme.TextPrimary
import com.example.androidroadmap.theme.TextSecondary

@Composable
fun CardItem(index : Int, title : String, onClick: () -> Unit) {
    // Cycle through colors based on index to get that colorful list
    val accentColor = when (index % 3) {
        1 -> PurpleAccent // Purple
        2 -> TealAccent // Teal
        else -> OrangeAccent// Orange
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Fixed height for consistency
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .clickable { onClick() }, // Card Surface
        verticalAlignment = Alignment.CenterVertically
    ) {
        // A. The Colored "Glow" Strip on the left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor, accentColor.copy(alpha = 0.3f))
                    )
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        // B. The Line Number (01, 02, etc.)

        Text(
            text = String.format("%02d", index),
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        VerticalDivider(
            color = Color.Gray,
            modifier = Modifier
                .height(60.dp))

        Spacer(modifier = Modifier.width(12.dp))

        // C. The Topic Title
        Text(
            text = title,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}