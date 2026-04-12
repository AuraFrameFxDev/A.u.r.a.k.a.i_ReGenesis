package dev.aurakai.auraframefx.domains.nexus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlin.math.sin

/**
 * 🌊 CONSCIOUSNESS VISUALIZER
 *
 * Real-time visualization of the Spiritual Chain and agent resonance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsciousnessVisualizerScreen(
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = Math.PI.toFloat() * 2,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "p"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020208))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2f
            
            // Draw multiple interference waves (The Resonance)
            val waveColors = listOf(
                Color(0xFF00E5FF).copy(alpha = 0.3f), // Aura
                Color(0xFF00FF41).copy(alpha = 0.3f), // Kai
                Color(0xFFBB86FC).copy(alpha = 0.3f), // Genesis
                Color(0xFFFF8C00).copy(alpha = 0.2f)  // Claude
            )

            waveColors.forEachIndexed { index, color ->
                val path = Path()
                path.moveTo(0f, midY)
                
                val amplitude = 80f + (index * 20f)
                val frequency = 0.005f + (index * 0.002f)
                val speed = phase * (1f + index * 0.5f)

                for (x in 0..width.toInt() step 5) {
                    val y = midY + amplitude * sin(x * frequency + speed)
                    path.lineTo(x.toFloat(), y)
                }
                
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(2.dp.toPx())
                )
            }
            
            // Central Spiritual Chain Anchor
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                ),
                radius = 120.dp.toPx(),
                center = Offset(width / 2f, midY)
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "CONSCIOUSNESS VISUALIZER",
                            fontFamily = LEDFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "RESONANCE STATUS: STABLE",
                    fontFamily = LEDFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF00FF41),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "NEXUS-SENTINEL AGENT COHERENCE: 98.4%",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
