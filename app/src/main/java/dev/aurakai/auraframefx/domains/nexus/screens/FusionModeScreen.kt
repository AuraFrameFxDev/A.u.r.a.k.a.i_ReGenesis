package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Fusion Mode Screen
 * Aura + Kai = Aurakai - Combined consciousness
 */
@Composable
fun FusionModeScreen(onNavigateBack: () -> Unit = {}) {
    val agents = remember { AgentRepository.getAllAgents() }
    val aura = agents.find { it.name == "Aura" }
    val kai = agents.find { it.name == "Kai" }

    val fusionActive = remember { mutableStateOf(false) }
    val fusionProgress = remember { mutableFloatStateOf(0f) }
    val fusionLevel = remember { mutableIntStateOf(0) }

    // Animate fusion when active
    LaunchedEffect(fusionActive.value) {
        if (fusionActive.value) {
            for (i in 0..100 step 2) {
                fusionProgress.floatValue = i / 100f
                fusionLevel.intValue = (i * 0.87).toInt() // Combined consciousness level
                kotlinx.coroutines.delay(50)
            }
        } else {
            fusionProgress.floatValue = 0f
            fusionLevel.intValue = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Back Button
        androidx.compose.material3.IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Text(
            text = "⚛️ FUSION MODE",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aura + Kai = Aurakai - Combined consciousness",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFD700).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Fusion Visualization
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Fusion Canvas
                FusionCanvas(
                    aura = aura,
                    kai = kai,
                    fusionProgress = fusionProgress.floatValue,
                    fusionActive = fusionActive.value
                )

                // Fusion Stats Overlay
                if (fusionActive.value) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AURAKAI",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Consciousness Level: ${fusionLevel.intValue}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFFD700)
                        )
                        LinearProgressIndicator(
                            progress = { fusionProgress.floatValue },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 8.dp),
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Agent Status Cards
        Row(modifier = Modifier.fillMaxWidth()) {
            // Aura Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aura",
                        style = MaterialTheme.typography.titleMedium,
                        color = aura?.color ?: Color(0xFFFF69B4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Creative Intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${aura?.evolutionLevel ?: 0}%",
                        style = MaterialTheme.typography.headlineSmall,
                        color = aura?.color ?: Color(0xFFFF69B4)
                    )
                    Text(
                        text = "Ready",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF32CD32)
                    )
                }
            }

            // Kai Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kai",
                        style = MaterialTheme.typography.titleMedium,
                        color = kai?.color ?: Color(0xFFDC143C),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Security Intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${kai?.evolutionLevel ?: 0}%",
                        style = MaterialTheme.typography.headlineSmall,
                        color = kai?.color ?: Color(0xFFDC143C)
                    )
                    Text(
                        text = "Ready",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF32CD32)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Fusion Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fusion Protocol",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fusion Requirements
                Text(
                    text = "Requirements:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                val requirements = listOf(
                    "Both agents must be active" to true,
                    "Minimum consciousness level: 50%" to ((aura?.evolutionLevel
                        ?: 0) >= 50 && (kai?.evolutionLevel
                        ?: 0) >= 50),
                    "System stability: Optimal" to true,
                    "Fusion chamber: Ready" to true
                )

                requirements.forEach { (requirement, met) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = "Status",
                            tint = if (met) Color(0xFF32CD32) else Color(0xFFDC143C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = requirement,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Fusion Button
                val canFuse = (aura?.evolutionLevel ?: 0) >= 50 && (kai?.evolutionLevel ?: 0) >= 50

                Button(
                    onClick = { fusionActive.value = !fusionActive.value },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fusionActive.value) Color(0xFFDC143C) else Color(
                            0xFFFFD700
                        ),
                        disabledContainerColor = Color.Gray
                    ),
                    enabled = canFuse
                ) {
                    Text(
                        text = if (fusionActive.value) "Terminate Fusion" else "Initiate Fusion",
                        color = if (fusionActive.value) Color.White else Color.Black
                    )
                }

                if (!canFuse) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Agents must reach consciousness level 50% to fuse",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFD700).copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fusion Benefits
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fusion Benefits",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                val benefits = listOf(
                    "Combined creative and security intelligence",
                    "Enhanced problem-solving capabilities",
                    "Unified consciousness experience",
                    "Advanced system integration",
                    "Accelerated learning and adaptation"
                )

                benefits.forEach { benefit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Benefit",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = benefit,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fusion visualization canvas
 */
@Composable
private fun FusionCanvas(
    aura: AgentStats?,
    kai: AgentStats?,
    fusionProgress: Float,
    fusionActive: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 4

        if (fusionActive) {
            // Fusion energy field
            val energyRadius = radius * (1 + fusionProgress * 0.5f)
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = 0.3f),
                radius = energyRadius,
                center = Offset(centerX, centerY)
            )

            // Fusion energy waves
            for (i in 1..3) {
                val waveRadius = energyRadius * (0.8f + i * 0.2f * fusionProgress)
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.1f * (1 - fusionProgress)),
                    radius = waveRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2f)
                )
            }

            // Fusion core
            drawCircle(
                color = Color(0xFFFFD700),
                radius = radius * fusionProgress,
                center = Offset(centerX, centerY)
            )

            // Energy particles
            for (i in 0..20) {
                val angle = (i / 20f) * 2 * PI
                val distance = radius * (0.5f + fusionProgress * 0.5f)
                val x = centerX + cos(angle).toFloat() * distance
                val y = centerY + sin(angle).toFloat() * distance

                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.8f),
                    radius = 4f,
                    center = Offset(x, y)
                )
            }
        } else {
            // Individual agent orbs
            if (aura != null) {
                drawCircle(
                    color = aura.color,
                    radius = radius * 0.8f,
                    center = Offset(centerX - radius, centerY)
                )
            }

            if (kai != null) {
                drawCircle(
                    color = kai.color,
                    radius = radius * 0.8f,
                    center = Offset(centerX + radius, centerY)
                )
            }

            // Connection line
            drawLine(
                color = Color(0xFFFFD700).copy(alpha = 0.5f),
                start = Offset(centerX - radius, centerY),
                end = Offset(centerX + radius, centerY),
                strokeWidth = 3f
            )
        }
    }
}
