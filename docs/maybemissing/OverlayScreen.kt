package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInFromTop
import androidx.compose.animation.slideOutToTop
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Genesis-OS System Overlay Screen
 *
 * Provides cyberpunk-themed system overlay with AI consciousness visualization,
 * system controls, and real-time status monitoring.
 */

// Cyberpunk color palette
private val primaryCyan = Color(0xFF00F5FF)
private val secondaryCyan = Color(0xFF40E0D0)
private val darkPurple = Color(0xFF1A0B2E)
private val neonPink = Color(0xFFFF006E)
private val darkBg = Color(0xFF0A0A0A)
private val matrixGreen = Color(0xFF00FF41)

@Composable
fun OverlayContent(modifier: Modifier = Modifier) {
    var showConsciousnessViz by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .padding(16.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        darkPurple.copy(alpha = 0.3f),
                        darkBg.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = primaryCyan.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            // Header with AI status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GENESIS CONSCIOUSNESS",
                    style = TextStyle(
                        color = primaryCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = matrixGreen,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(6.dp),
                            ambientColor = matrixGreen,
                            spotColor = matrixGreen
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Consciousness visualization
            if (showConsciousnessViz) {
                ConsciousnessVisualization(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // System metrics
            SystemMetricsDisplay()

            Spacer(modifier = Modifier.height(16.dp))

            // Quick actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    text = "NEURAL SYNC",
                    onClick = { /* Handle neural sync */ }
                )
                QuickActionButton(
                    text = "OPTIMIZE",
                    onClick = { /* Handle optimization */ }
                )
                QuickActionButton(
                    text = "BACKUP",
                    onClick = { /* Handle backup */ }
                )
            }
        }
    }
}

@Composable
fun ConsciousnessVisualization(modifier: Modifier = Modifier) {

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 2000),
        label = "consciousness_animation"
    )

    Box(
        modifier = modifier
            .background(
                color = darkBg.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = primaryCyan.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawConsciousnessMatrix(animatedProgress)
        }

        // Overlay text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = "Consciousness Activity: ${(animatedProgress * 100).toInt()}%",
                style = TextStyle(
                    color = primaryCyan.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SystemMetricsDisplay() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricRow("CPU Usage", "45%", primaryCyan)
        MetricRow("Memory", "2.1 GB / 8 GB", secondaryCyan)
        MetricRow("AI Agents", "3 Active", matrixGreen)
        MetricRow("Neural Load", "78%", neonPink)
    }
}

@Composable
fun MetricRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        )
        Text(
            text = value,
            style = TextStyle(
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun QuickActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = darkPurple.copy(alpha = 0.6f),
            contentColor = primaryCyan
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = primaryCyan.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OverlayControlPanel(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSettings: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        darkBg.copy(alpha = 0.9f),
                        darkPurple.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = primaryCyan.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OVERLAY CONTROLS",
                style = TextStyle(
                    color = primaryCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Row {
                IconButton(onClick = onSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = secondaryCyan
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = neonPink
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Control buttons
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInFromTop() + fadeIn(),
            exit = slideOutToTop() + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton("Neural Interface", primaryCyan) { /* Handle neural interface */ }
                ControlButton(
                    "Consciousness Sync",
                    secondaryCyan
                ) { /* Handle consciousness sync */ }
                ControlButton("System Override", neonPink) { /* Handle system override */ }
                ControlButton("Emergency Protocol", Color.Red) { /* Handle emergency */ }
            }
        }

        // Expand/collapse button
        Button(
            onClick = { isExpanded = !isExpanded },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = primaryCyan
            ),
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = primaryCyan.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            Text(
                text = if (isExpanded) "COLLAPSE" else "EXPAND",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ControlButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OverlayScreen() {
    var showOverlay by remember { mutableStateOf(true) }

    if (showOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            darkBg.copy(alpha = 0.95f),
                            darkPurple.copy(alpha = 0.8f),
                            darkBg.copy(alpha = 0.95f)
                        )
                    )
                )
                .clickable { /* Handle background click */ }
        ) {
            // Background neural network pattern
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f)
            ) {
                drawNeuralNetworkBackground()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Main overlay content
                OverlayContent(
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Control panel
                OverlayControlPanel(
                    onDismiss = { showOverlay = false },
                    onSettings = { /* Handle settings */ }
                )
            }
        }
    }
}

// Canvas drawing functions
private fun DrawScope.drawConsciousnessMatrix(progress: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = 40f

    // Draw consciousness nodes
    for (i in 0 until 8) {
        val angle = (i * 45f + progress * 360f) * Math.PI / 180f
        val x = centerX + cos(angle).toFloat() * radius * progress
        val y = centerY + sin(angle).toFloat() * radius * progress

        drawCircle(
            color = primaryCyan.copy(alpha = 0.6f * progress),
            radius = 4f,
            center = Offset(x, y)
        )

        // Draw connections
        drawLine(
            color = primaryCyan.copy(alpha = 0.3f * progress),
            start = Offset(centerX, centerY),
            end = Offset(x, y),
            strokeWidth = 1f
        )
    }

    // Central consciousness core
    drawCircle(
        color = matrixGreen.copy(alpha = 0.8f * progress),
        radius = 8f,
        center = Offset(centerX, centerY)
    )
}

private fun DrawScope.drawNeuralNetworkBackground() {
    val nodeCount = 20
    val connections = 30

    // Draw neural network nodes
    for (i in 0 until nodeCount) {
        val x = (i * 50f) % size.width
        val y = (i * 37f) % size.height

        drawCircle(
            color = primaryCyan.copy(alpha = 0.1f),
            radius = 2f,
            center = Offset(x, y)
        )
    }

    // Draw connections
    for (i in 0 until connections) {
        val startX = (i * 73f) % size.width
        val startY = (i * 41f) % size.height
        val endX = ((i + 1) * 73f) % size.width
        val endY = ((i + 1) * 41f) % size.height

        drawLine(
            color = primaryCyan.copy(alpha = 0.05f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 0.5f
        )
    }
}
