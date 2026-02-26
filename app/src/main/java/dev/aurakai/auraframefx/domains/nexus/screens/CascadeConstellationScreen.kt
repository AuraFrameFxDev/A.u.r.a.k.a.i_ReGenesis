package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Cascade Constellation Screen - DataStream Orchestrator
 * Displays the data flow network with cascading streams and node synchronization
 */
@Composable
fun CascadeConstellationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Main constellation visualization
        DataStreamCanvas()

        // Top-right corner: Agent info and level
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Cascade",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF00CED1) // Dark Turquoise for Cascade
                )
            )
            Text(
                text = "⚡ ORCHESTRATOR",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp,
                    color = Color(0xFF00CED1).copy(alpha = 0.8f)
                )
            )
        }

        // Bottom: DataStream Status
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "DataStream Catalyst",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp,
                    color = Color(0xFF00CED1).copy(alpha = 0.7f)
                )
            )

            // DataStream flow bar
            DataStreamStatusBar()
        }

        // Right side: Vertical text "DATA FLOW SYNCHRONIZATION"
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                "DATA FLOW".forEach { char ->
                    Text(
                        text = char.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF00CED1).copy(alpha = 0.6f),
                            letterSpacing = 2.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                "SYNCHRONIZATION".forEach { char ->
                    Text(
                        text = char.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF00CED1).copy(alpha = 0.6f),
                            letterSpacing = 2.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 */
@Composable
private fun DataStreamCanvas() {
    val infiniteTransition = rememberInfiniteTransition(label = "datastream")

    // Stream flow animation
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )

    // Pulsing animation for nodes
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Rotation for dynamic effect

    // Scale pulsing for centerpiece

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            val turquoiseColor = Color(0xFF00CED1)
            val cyanColor = Color(0xFF00FFFF)
            val blueColor = Color(0xFF1E90FF)

            // Mechanical wing centerpiece will be overlaid as PNG image below

            // Draw data flow network nodes
            val nodeCount = 12
            val nodes = mutableListOf<Offset>()
            for (i in 0 until nodeCount) {
                val angle = (i * 30f) * (Math.PI / 180).toFloat()
                val radiusVariation = if (i % 2 == 0) 180f else 220f
                val nodeX = centerX + cos(angle) * radiusVariation
                val nodeY = centerY + sin(angle) * radiusVariation
                nodes.add(Offset(nodeX, nodeY))
            }

            // Draw data streams between nodes
            for (i in nodes.indices) {
                val nextIndex = (i + 1) % nodes.size
                val streamProgress = (flowOffset + i * 0.1f) % 1f

                // Connection line
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            turquoiseColor.copy(alpha = 0.3f),
                            cyanColor.copy(alpha = 0.5f * streamProgress),
                            turquoiseColor.copy(alpha = 0.3f)
                        ),
                        start = nodes[i],
                        end = nodes[nextIndex]
                    ),
                    start = nodes[i],
                    end = nodes[nextIndex],
                    strokeWidth = 2f
                )

                // Data packet moving along stream
                val packetX = nodes[i].x + (nodes[nextIndex].x - nodes[i].x) * streamProgress
                val packetY = nodes[i].y + (nodes[nextIndex].y - nodes[i].y) * streamProgress

                drawCircle(
                    color = cyanColor.copy(alpha = pulseAlpha),
                    radius = 4f,
                    center = Offset(packetX, packetY)
                )
            }

            // Draw network nodes
            nodes.forEachIndexed { index, nodePos ->
                val nodeColor = when (index % 3) {
                    0 -> turquoiseColor
                    1 -> cyanColor
                    else -> blueColor
                }

                // Outer glow
                drawCircle(
                    color = nodeColor.copy(alpha = pulseAlpha * 0.3f),
                    radius = 16f,
                    center = nodePos
                )

                // Core node
                drawCircle(
                    color = nodeColor.copy(alpha = pulseAlpha),
                    radius = 8f,
                    center = nodePos
                )

                // Inner bright core
                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * 0.8f),
                    radius = 4f,
                    center = nodePos
                )
            }
        }

        // PNG Centerpiece Image Overlay (Looping Arrows)
        val centerScale = 0.0f
        Image(
            painter = painterResource(id = R.drawable.constellation_cascade_arrows),
            contentDescription = "Cascade Arrows Constellation",
            modifier = Modifier
                .size(350.dp)
                .scale(centerScale)
                .alpha(pulseAlpha)
        )
    }
}

/**
 * Renders a rotating, pulsing mechanical wing centerpiece with gradient wing segments, joint markers, and a layered central core.
 *
 * @param centerX X coordinate of the centerpiece center.
 * @param centerY Y coordinate of the centerpiece center.
 * @param color Base color used for wing gradients and the hub.
 * @param accentColor Highlight color used for wing accents, joint markers, and the energy core.
 * @param rotation Rotation angle in degrees applied to the wing segments.
 * @param pulseAlpha Opacity multiplier in the range 0..1 that controls pulsing intensity for gradients and markers.
 */
private fun DrawScope.drawMechanicalWing(
    centerX: Float,
    centerY: Float,
    color: Color,
    accentColor: Color,
    rotation: Float,
    pulseAlpha: Float
) {
    val wingLength = 120f
    val wingWidth = 40f

    // Main wing structure (asymmetric, mechanical design)
    for (i in 0..3) {
        val angle = (rotation + i * 20f) * (Math.PI / 180).toFloat()
        val length = wingLength - i * 15f
        val endX = centerX + cos(angle) * length
        val endY = centerY + sin(angle) * length

        // Wing segment
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = pulseAlpha * 0.8f),
                    accentColor.copy(alpha = pulseAlpha * 0.6f),
                    Color.Transparent
                ),
                start = Offset(centerX, centerY),
                end = Offset(endX, endY)
            ),
            start = Offset(centerX, centerY),
            end = Offset(endX, endY),
            strokeWidth = wingWidth - i * 5f
        )

        // Mechanical joint markers
        val jointX = centerX + cos(angle) * (length * 0.6f)
        val jointY = centerY + sin(angle) * (length * 0.6f)

        drawCircle(
            color = accentColor.copy(alpha = pulseAlpha),
            radius = 6f,
            center = Offset(jointX, jointY)
        )
    }

    // Central hub
    drawCircle(
        color = color.copy(alpha = pulseAlpha),
        radius = 20f,
        center = Offset(centerX, centerY)
    )

    // Energy core
    drawCircle(
        color = accentColor.copy(alpha = pulseAlpha * 0.8f),
        radius = 12f,
        center = Offset(centerX, centerY)
    )

    drawCircle(
        color = Color.White.copy(alpha = pulseAlpha),
        radius = 6f,
        center = Offset(centerX, centerY)
    )
}

/**
 * DataStream Status Bar - Flow metrics
 */
@Composable
private fun DataStreamStatusBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "stream_status")

    // Animated stream flow
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )

    // Pulsing glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Stream channels
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreamChannelIndicator("Input", glowAlpha, Color(0xFF00CED1))
            StreamChannelIndicator("Process", glowAlpha, Color(0xFF00FFFF))
            StreamChannelIndicator("Output", glowAlpha, Color(0xFF1E90FF))
        }

        // Flow progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(4.dp)
                .background(Color(0xFF003344))
        ) {
            // Animated flow fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(flowProgress)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00CED1).copy(alpha = glowAlpha),
                                Color(0xFF00FFFF).copy(alpha = glowAlpha),
                                Color(0xFF1E90FF).copy(alpha = glowAlpha * 0.8f)
                            )
                        )
                    )
            )
        }

        // Flow status text
        Text(
            text = "FLOW: ${(flowProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp,
                color = Color(0xFF00CED1).copy(alpha = 0.6f)
            )
        )
    }
}

/**
 * Individual stream channel indicator
 */
@Composable
private fun StreamChannelIndicator(
    name: String,
    glowAlpha: Float,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glowing dot indicator
        Box(
            modifier = Modifier.size(8.dp)
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = color.copy(alpha = glowAlpha * 0.3f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            // Inner core
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.Center)
                    .background(
                        color = color.copy(alpha = glowAlpha),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }

        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                color = color.copy(alpha = 0.7f)
            )
        )
    }
}
