package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🔲 HOLOGRAPHIC INFO PANEL
 * Refined for maximum legibility in the Split-Hologram architecture.
 * Uses an opaque black core to prevent background visual noise from "mangling" the text.
 */
@Composable
fun HolographicInfoPanel(
    title: String,
    description: String,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.05f),
                        Color.Black.copy(alpha = 0.8f), // Opaque center = READABLE TEXT
                        glowColor.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(glowColor.copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = title.uppercase(),
                color = glowColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            )
        }
    }
}

