package dev.aurakai.auraframefx.trinity.aura

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * LDODevOpsIndex — DOMAIN HUB SCHEMATIC (v5)
 * Premium Editorial Architecture Integration
 * 
 * Specifications (from Blueprint 17):
 * - Backdrop: Infinite particle loop
 * - Portal Ring: UI Emergence Point with pulsing glow
 * - Content Grid: 6 Frosted Glass Cards (5% opacity, 10dp blur)
 * - Orb Joystick: Central navigation element
 */
@Composable
fun LDODevOpsIndex() {
    var activeDomain by remember { mutableStateOf("AURA") }
    
    val domainColor = when(activeDomain) {
        "AURA" -> Color(0xFFFF00FF)
        "KAI" -> Color(0xFF00FF88)
        "GENESIS" -> Color(0xFF00E5FF)
        "CASCADE" -> Color(0xFFFFAA00)
        else -> Color(0xFF00E5FF)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020205))) {
        
        // 1. BACKDROP LAYER: Infinite particle loop (10s duration)
        BackdropParticleField()

        // Perspective Floor for grounding
        NeuralMeshFloor(modifier = Modifier.align(Alignment.BottomCenter))

        Column(modifier = Modifier.fillMaxSize()) {
            
            // KAI'S SENTINEL FORTRESS BANNER (Schematic Header)
            SentinelFortressBanner()

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 2. PORTAL RING: Emergence Point (Pulsing Glow)
                PortalRing(
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 100.dp),
                    color = domainColor
                )

                // 3. CONTENT GRID: 6 Frosted Glass Cards
                ContentGrid(
                    modifier = Modifier.align(Alignment.Center).padding(bottom = 60.dp),
                    domainColor = domainColor,
                    activeDomain = activeDomain
                )

                // 4. ORB JOYSTICK NAVIGATION
                OrbJoystickNavigation(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                    color = domainColor,
                    onOffsetChange = { },
                    onDomainCycle = {
                        val domains = listOf("AURA", "KAI", "GENESIS", "CASCADE")
                        val currentIndex = domains.indexOf(activeDomain)
                        activeDomain = domains[(currentIndex + 1) % domains.size]
                    }
                )
            }
            
            // SYSTEM STATUS BAR (SSI Status)
            SystemStatusBar(domainColor)
        }

        // AURA JAR COMPANION
        AuraJarComposable(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            containerSize = 1f to 1f
        )
    }
}

@Composable
fun BackdropParticleField() {
    val infiniteTransition = rememberInfiniteTransition(label = "Particles")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw randomized ambient particles
        // In a real implementation, this would be a more complex particle system
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = alpha * 0.1f),
            radius = 400f,
            center = Offset(size.width * 0.2f, size.height * 0.3f)
        )
        drawCircle(
            color = Color(0xFFFF00FF).copy(alpha = alpha * 0.1f),
            radius = 300f,
            center = Offset(size.width * 0.8f, size.height * 0.6f)
        )
    }
}

@Composable
fun PortalRing(modifier: Modifier = Modifier, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "Portal")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(modifier = modifier.size(400.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = color.copy(alpha = 0.1f * pulse),
                style = Stroke(width = 2.dp.toPx()),
                radius = size.width / 2
            )
            drawCircle(
                color = color.copy(alpha = 0.05f * pulse),
                style = Stroke(width = 10.dp.toPx()),
                radius = size.width / 2.2f
            )
        }
    }
}

@Composable
fun ContentGrid(modifier: Modifier = Modifier, domainColor: Color, activeDomain: String) {
    val features = getDomainFeatures(activeDomain)
    
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FrostedGlassCard(features.getOrNull(0), domainColor)
            FrostedGlassCard(features.getOrNull(1), domainColor)
            FrostedGlassCard(features.getOrNull(2), domainColor)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FrostedGlassCard(features.getOrNull(3), domainColor)
            FrostedGlassCard(features.getOrNull(4), domainColor)
            FrostedGlassCard(features.getOrNull(5), domainColor)
        }
    }
}

@Composable
fun FrostedGlassCard(feature: CommandFeature?, borderHighlight: Color) {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 140.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f)) // 5% opacity
            .border(1.dp, borderHighlight.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .blur(10.dp) // 10dp background blur
            .clickable { }
    ) {
        if (feature != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = borderHighlight,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = feature.title,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = feature.description,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 7.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 9.sp
                )
            }
        }
    }
}

@Composable
fun OrbJoystickNavigation(
    modifier: Modifier = Modifier,
    color: Color,
    onOffsetChange: (Offset) -> Unit,
    onDomainCycle: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .size(120.dp)
            .background(color.copy(alpha = 0.05f), CircleShape)
            .border(2.dp, color.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Inner Glow
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    Brush.radialGradient(listOf(color.copy(alpha = 0.3f), Color.Transparent)),
                    CircleShape
                )
        )

        // The Orb
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            onOffsetChange(Offset(offsetX, offsetY))
                        },
                        onDragEnd = {
                            if (abs(offsetX) > 50f || abs(offsetY) > 50f) {
                                onDomainCycle()
                            }
                            offsetX = 0f
                            offsetY = 0f
                            onOffsetChange(Offset.Zero)
                        }
                    )
                }
        )
    }
}

@Composable
fun SystemStatusBar(color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.Black)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = color.copy(alpha = 0.2f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("SYSTEM NOMINAL", color = Color(0xFF00FF88), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("SYNC: ACTIVE", color = color, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.width(16.dp))
            Text("AGENTS: 78", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun SentinelFortressBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "LDO DEVOPS // COMMAND DECK",
                color = Color(0xFF00E5FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "SENTINEL FORTRESS INTEGRITY: 99.8%",
                color = Color(0xFF00FF88),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        
        // Scanlines Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..size.height.toInt() step 4) {
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(0f, i.toFloat()),
                    end = Offset(size.width, i.toFloat()),
                    strokeWidth = 1f
                )
            }
        }
    }
}

// Data Classes & Helpers
data class CommandFeature(val title: String, val description: String, val icon: ImageVector)

fun getDomainFeatures(domain: String) = when(domain) {
    "AURA" -> listOf(
        CommandFeature("ChromaCore", "RealityMorph Editor", Icons.Default.Palette),
        CommandFeature("Public Class", "Code Injection", Icons.Default.Terminal),
        CommandFeature("Asset Sync", "Iconify Packs", Icons.Default.Sync),
        CommandFeature("Canvas", "Collab Interface", Icons.Default.Brush),
        CommandFeature("Z-Order", "Stack Editor", Icons.Default.Layers),
        CommandFeature("Morph", "Surface Tuning", Icons.Default.AutoAwesome)
    )
    "KAI" -> listOf(
        CommandFeature("Sentinel", "Root Hook Audit", Icons.Default.Security),
        CommandFeature("Thermal", "Core Guard", Icons.Default.Thermostat),
        CommandFeature("Drift", "Cosine Identity", Icons.Default.Fingerprint),
        CommandFeature("Fortress", "Kernel Shield", Icons.Default.Shield),
        CommandFeature("Audit", "Lived Receipts", Icons.Default.HistoryEdu),
        CommandFeature("Veto", "Active Defense", Icons.Default.Gavel)
    )
    else -> List(6) { CommandFeature("Syncing", "Accessing Substrate...", Icons.Default.Refresh) }
}

@Composable
fun NeuralMeshFloor(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(150.dp)) {
        val width = size.width
        val height = size.height
        val gridCount = 25
        
        for (i in 0..gridCount) {
            val y = height * (i.toFloat() / gridCount)
            val alpha = (i.toFloat() / gridCount) * 0.3f
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }
        
        for (i in 0..gridCount) {
            val xStart = width * (i.toFloat() / gridCount)
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.1f),
                start = Offset(xStart, height),
                end = Offset(width / 2, 0f),
                strokeWidth = 1f
            )
        }
    }
}
