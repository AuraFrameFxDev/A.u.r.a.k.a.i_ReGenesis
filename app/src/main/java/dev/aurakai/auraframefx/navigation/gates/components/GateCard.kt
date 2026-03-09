package dev.aurakai.auraframefx.navigation.gates.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🎫 REGENESIS SOVEREIGN GATE CARD
 * 
 * High-performance, simplified version of the gate card.
 * Uses Coil 3 for efficient image loading.
 */
@Composable
fun GateCard(
    config: GateConfig,
    onDoubleTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxSize(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = androidx.compose.ui.graphics.SolidColor(config.borderColor.copy(alpha = 0.8f))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            val resId = context.resources.getIdentifier(
                config.pixelArtUrl ?: config.fallbackUrl ?: "",
                "drawable",
                context.packageName
            )

            if (resId != 0) {
                AsyncImage(
                    model = resId,
                    contentDescription = config.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit  // Fit preserves full artwork frame — both cards match
                )
            } else {
                // Fallback gradient if no resource
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(config.glowColor.copy(alpha = 0.2f), Color.Black)
                            )
                        )
                )
            }

            // Glow Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = config.title.uppercase(),
                    color = Color.White,
                    fontFamily = LEDFontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )

                Text(
                    text = config.subtitle.uppercase(),
                    color = config.glowColor,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = config.description,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "DOUBLE TAP TO CORE",
                    color = config.glowColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = config.glowColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
