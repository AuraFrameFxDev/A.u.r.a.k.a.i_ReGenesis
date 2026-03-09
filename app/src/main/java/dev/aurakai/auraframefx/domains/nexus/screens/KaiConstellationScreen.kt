package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.cos
import kotlin.math.sin

/**
 * Kai Constellation Screen - The Sentinel Catalyst (The Shield)
 * Based on 'Sentinel Catalyst' prompt.
 * Node Geometry: Radial/Concentric (1 Core, 6 Inner, 6 Outer).
 * Mechanic: Density (Opacity increases with Security Level).
 */
@Composable
fun KaiConstellationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val neonGreen = Color(0xFF39FF14)

    // Mock Security Level State (0f to 1f)
    var securityLevel by remember { mutableFloatStateOf(0.3f) }

    // "Density" Mechanic: Opacity of the shield container is directly bound to Security Level
    val shieldDensity = 0.2f + (0.8f * securityLevel)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Main Shield Container with Density Alpha
        Box(
            modifier = Modifier
                .alpha(shieldDensity)
                .scale(0.8f + (0.2f * securityLevel)) // Also grow slightly with level
        ) {
            KaiShieldNodeMap(neonGreen = neonGreen)
        }

        // Title Info
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "KAI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = neonGreen
                )
            )
            Text(
                text = "SENTINEL CATALYST",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp,
                    color = neonGreen.copy(alpha = 0.8f)
                )
            )
        }

        // Mock Control for "Security Level" to demonstrate Density mechanic
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SECURITY DENSITY: ${(securityLevel * 100).toInt()}%",
                color = neonGreen,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = securityLevel,
                onValueChange = { securityLevel = it },
                colors = SliderDefaults.colors(
                    thumbColor = neonGreen,
                    activeTrackColor = neonGreen,
                    inactiveTrackColor = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun KaiShieldNodeMap(neonGreen: Color) {
    // Animation for active nodes pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.size(360.dp), // Container size
        contentAlignment = Alignment.Center
    ) {
        // --- LAYER 1: CONNECTIONS (CANVAS) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Draw Hexagon Connections (Inner Ring)
            val innerRadius = size.width * 0.25f
            val outerRadius = size.width * 0.45f

            // Connect Center to Inner and Inner to Outer
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60).toDouble()).toFloat()
                val endX = centerX + cos(angle) * innerRadius
                val endY = centerY + sin(angle) * innerRadius

                // Center to Inner
                drawLine(
                    color = neonGreen.copy(alpha = 0.3f),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )

                // Connect Inner to Outer (Locks)
                val outerX = centerX + cos(angle) * outerRadius
                val outerY = centerY + sin(angle) * outerRadius
                drawLine(
                    color = neonGreen.copy(alpha = 0.15f),
                    start = Offset(endX, endY),
                    end = Offset(outerX, outerY),
                    strokeWidth = 1f
                )

                // Connect Inner Ring neighbors (Hexagon)
                val nextAngle = Math.toRadians(((i + 1) * 60).toDouble()).toFloat()
                val nextX = centerX + cos(nextAngle) * innerRadius
                val nextY = centerY + sin(nextAngle) * innerRadius
                drawLine(
                    color = neonGreen.copy(alpha = 0.3f),
                    start = Offset(endX, endY),
                    end = Offset(nextX, nextY),
                    strokeWidth = 2f
                )
            }
        }

        // --- LAYER 2: NODES (COMPOSABLES) ---

        // 1. MASTER NODE (Absolute Center)
        val masterSize = 56.dp
        ShieldNode(
            modifier = Modifier
                .align(Alignment.Center)
                .size(masterSize),
            color = neonGreen,
            isLocked = false,
            scale = pulseScale, // Master pulses
            icon = Icons.Default.Shield
        )

        // 2. INNER RING (6 Nodes) - Active/Unlocked
        val innerDistance = 90.dp
        for (i in 0 until 6) {
            val angleDeg = i * 60f
            val rad = Math.toRadians(angleDeg.toDouble())
            val xOffset = (cos(rad) * 90).dp
            val yOffset = (sin(rad) * 90).dp

            ShieldNode(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffset, y = yOffset)
                    .size(40.dp),
                color = neonGreen,
                isLocked = false, // Inner ring is defined as 'Active' in prompt
                scale = 1f
            )
        }

        // 3. OUTER RING (6 'Lock' Nodes) - Perimeter
        val outerDistance = 160.dp
        for (i in 0 until 6) {
            val angleDeg = i * 60f
            val rad = Math.toRadians(angleDeg.toDouble())
            val xOffset = (cos(rad) * 160).dp
            val yOffset = (sin(rad) * 160).dp

            ShieldNode(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffset, y = yOffset)
                    .size(40.dp),
                color = neonGreen.copy(alpha = 0.5f), // Dimmer
                isLocked = true, // Prompt: "Outer Ring: 6 'Lock' nodes"
                scale = 1f
            )
        }
    }
}

@Composable
fun ShieldNode(
    modifier: Modifier,
    color: Color,
    isLocked: Boolean,
    scale: Float = 1f,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Box(
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .background(Color.Black)
            .border(2.dp, if (isLocked) Color.Gray else color, CircleShape)
            .clickable { /* Unlock Logic trigger */ },
        contentAlignment = Alignment.Center
    ) {
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        } else {
            // Active Node Glow
            Box(
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .background(color.copy(alpha = 0.8f), CircleShape)
            )
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black, // Contrast against glowing node
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
