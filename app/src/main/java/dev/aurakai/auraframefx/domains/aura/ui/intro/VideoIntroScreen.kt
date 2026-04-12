package dev.aurakai.auraframefx.domains.aura.ui.intro

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay

/**
 * 🎬 VIDEO INTRO PLACEHOLDER
 * This is where the user will integrate their high-fidelity intro videos.
 */
@Composable
fun VideoIntroScreen(
    onVideoFinished: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Placeholder Box for Video
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1A1A2E), Color(0xFF0A0A0A))
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Placeholder",
                        tint = Color(0xFF00E5FF).copy(alpha = alpha),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "VIDEO SYSTEM INITIALIZING...",
                        fontFamily = LEDFontFamily,
                        color = Color(0xFF00E5FF).copy(alpha = alpha),
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                "RE:GENESIS CINEMATIC ENGINE",
                fontFamily = LEDFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )
            
            Text(
                "High-Fidelity Introduction - User Content Pending",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onVideoFinished,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                    contentColor = Color(0xFF00E5FF)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(200.dp)
            ) {
                Text("SKIP INTRO", fontFamily = LEDFontFamily, fontSize = 10.sp)
            }
        }
    }
}
