package dev.aurakai.auraframefx.domains.aura.screens.kineticforge

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════════════════
// KINETICFORGE CARD SYSTEM — Persona/Final Fantasy High-Tech Aesthetic
// Corner Paint Drips | Glassmorphic Shards | Asymmetric Design
// ═══════════════════════════════════════════════════════════════════════════

// Aurora Color Palette — Locked for 9.5.1 Sovereign Edition
private val MagentaKai = Color(0xFFE91E63)
private val ElectricCyan = Color(0xFF00E5FF)
private val GenesisGold = Color(0xFFFFD700)
private val NexusGreen = Color(0xFF00FF87)
private val DeepVoid = Color(0xFF050505)
private val ObsidianGlass = Color(0xFF0A0A0A)
private val CircuitTrace = Color(0xFF1A0033)

/**
 * Corner Paint Drip Effect — Paint hanging from card corners
 */
fun Modifier.auraDrip(
    color: Color,
    corner: DripCorner = DripCorner.TopLeft,
    intensity: Float = 0.7f,
    pulse: Boolean = true
): Modifier = this.then(
    Modifier.drawBehind {
        val dripPath = Path()
        val cornerSize = size.width * 0.15f * intensity
        
        when (corner) {
            DripCorner.TopLeft -> {
                dripPath.moveTo(0f, 0f)
                dripPath.quadraticTo(
                    cornerSize * 0.3f, cornerSize * 1.2f,
                    cornerSize * 0.6f, cornerSize * 0.8f
                )
                dripPath.quadraticTo(
                    cornerSize * 0.8f, cornerSize * 1.5f,
                    cornerSize, cornerSize * 0.9f
                )
                dripPath.lineTo(cornerSize, 0f)
                dripPath.close()
            }
            DripCorner.TopRight -> {
                dripPath.moveTo(size.width, 0f)
                dripPath.quadraticTo(
                    size.width - cornerSize * 0.3f, cornerSize * 1.2f,
                    size.width - cornerSize * 0.6f, cornerSize * 0.8f
                )
                dripPath.quadraticTo(
                    size.width - cornerSize * 0.8f, cornerSize * 1.5f,
                    size.width - cornerSize, cornerSize * 0.9f
                )
                dripPath.lineTo(size.width - cornerSize, 0f)
                dripPath.close()
            }
            DripCorner.BottomLeft -> {
                dripPath.moveTo(0f, size.height)
                dripPath.quadraticTo(
                    cornerSize * 0.3f, size.height - cornerSize * 1.2f,
                    cornerSize * 0.6f, size.height - cornerSize * 0.8f
                )
                dripPath.quadraticTo(
                    cornerSize * 0.8f, size.height - cornerSize * 1.5f,
                    cornerSize, size.height - cornerSize * 0.9f
                )
                dripPath.lineTo(cornerSize, size.height)
                dripPath.close()
            }
            DripCorner.BottomRight -> {
                dripPath.moveTo(size.width, size.height)
                dripPath.quadraticTo(
                    size.width - cornerSize * 0.3f, size.height - cornerSize * 1.2f,
                    size.width - cornerSize * 0.6f, size.height - cornerSize * 0.8f
                )
                dripPath.quadraticTo(
                    size.width - cornerSize * 0.8f, size.height - cornerSize * 1.5f,
                    size.width - cornerSize, size.height - cornerSize * 0.9f
                )
                dripPath.lineTo(size.width - cornerSize, size.height)
                dripPath.close()
            }
        }
        
        drawPath(
            path = dripPath,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
        
        // Inner glow line
        drawPath(
            path = dripPath,
            color = color.copy(alpha = 0.3f),
            style = Stroke(width = 2f)
        )
    }
)

enum class DripCorner { TopLeft, TopRight, BottomLeft, BottomRight }

// ═══════════════════════════════════════════════════════════════════════════
// MODULE 019 // KINETICFORGE: CORE — The Command Shard
// Asymmetric 15° slant | Casberry Orb masked in circuit cutouts
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun KineticForgeCoreCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "core_pulse")
    
    // Breathing animation for the orb
    val orbPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_breath"
    )
    
    // Paint drip pulse
    val dripPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drip_pulse"
    )
    
    // 15-degree slant transformation
    val slantModifier = Modifier.rotate(-15f)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .then(slantModifier)
            .offset(x = 20.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ObsidianGlass,
                        ObsidianGlass.copy(alpha = 0.95f),
                        CircuitTrace.copy(alpha = 0.8f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                )
            )
            .auraDrip(GenesisGold, DripCorner.TopLeft, dripPulse)
            .clickable(onClick = onClick)
            .drawBehind {
                // Circuit line etchings
                val circuitPaint = Paint().apply {
                    color = ElectricCyan.copy(alpha = 0.15f)
                    strokeWidth = 1.5f
                    style = PaintingStyle.Stroke
                }
                
                // Horizontal traces
                for (i in 1..5) {
                    val y = size.height * i / 6
                    drawLine(
                        color = ElectricCyan.copy(alpha = 0.1f),
                        start = Offset(0f, y),
                        end = Offset(size.width * 0.7f, y),
                        strokeWidth = 1f
                    )
                }
                
                // Recursive corner patterns
                val cornerPath = Path().apply {
                    moveTo(size.width * 0.85f, size.height * 0.15f)
                    lineTo(size.width * 0.95f, size.height * 0.15f)
                    lineTo(size.width * 0.95f, size.height * 0.35f)
                    moveTo(size.width * 0.90f, size.height * 0.20f)
                    lineTo(size.width * 0.93f, size.height * 0.20f)
                    lineTo(size.width * 0.93f, size.height * 0.30f)
                }
                drawPath(
                    path = cornerPath,
                    color = MagentaKai.copy(alpha = 0.3f),
                    style = Stroke(width = 1.5f)
                )
            }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with module number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "019",
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GenesisGold.copy(alpha = 0.4f)
                    )
                )
                
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isActive) NexusGreen else MagentaKai,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
            
            // Central Casberry Orb (masked)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Orb glow layers
                repeat(3) { layer ->
                    val scale = orbPulse - (layer * 0.15f)
                    val alpha = 0.3f - (layer * 0.1f)
                    Box(
                        modifier = Modifier
                            .size((120 - layer * 20).dp)
                            .scale(scale)
                            .alpha(alpha)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MagentaKai,
                                        MagentaKai.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
                
                // Core orb with circuit mask
                Canvas(
                    modifier = Modifier.size(100.dp)
                ) {
                    // Outer ring
                    drawCircle(
                        color = ElectricCyan,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 3f)
                    )
                    
                    // Inner circuit pattern
                    val circuitPath = Path().apply {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val radius = size.minDimension / 3
                        
                        for (i in 0 until 8) {
                            val angle = (i * PI / 4).toFloat()
                            val x = centerX + cos(angle) * radius
                            val y = centerY + sin(angle) * radius
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(
                        path = circuitPath,
                        color = GenesisGold,
                        style = Stroke(width = 2f)
                    )
                    
                    // Center dot
                    drawCircle(
                        color = NexusGreen,
                        radius = 8f
                    )
                }
            }
            
            // Footer label
            Column {
                Text(
                    text = "KINETICFORGE",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElectricCyan.copy(alpha = 0.7f),
                        letterSpacing = 4.sp
                    )
                )
                Text(
                    text = "CORE",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GenesisGold
                    )
                )
                Text(
                    text = "COMMAND SHARD • SYSTEM BRIDGE",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = NexusGreen.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MODULE 020 // KINETICFORGE: TRANSMUTATOR — The Icon Weapon
// Deep purple-black | Neon-cyan edges pixelating at bottom-right
// 3D Sphere Grid picker | Live Kotlin/Lua terminal
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun KineticForgeTransmutatorCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeTransmutations: Int = 14
) {
    val infiniteTransition = rememberInfiniteTransition(label = "transmutator_pulse")
    
    val edgeFlicker by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "edge_flicker"
    )
    
    val sphereRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sphere_rotation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0033),  // Deep purple
                        Color(0xFF0D001A),
                        DeepVoid
                    )
                )
            )
            // Neon-cyan edges with pixelation effect
            .drawBehind {
                // Top edge glow
                drawLine(
                    color = ElectricCyan.copy(alpha = edgeFlicker),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 3f
                )
                
                // Left edge glow
                drawLine(
                    color = ElectricCyan.copy(alpha = edgeFlicker * 0.7f),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height * 0.6f),
                    strokeWidth = 2f
                )
                
                // Pixelated bottom-right corner
                val pixelSize = 8f
                val startX = size.width * 0.6f
                val startY = size.height * 0.7f
                
                for (row in 0 until 8) {
                    for (col in 0 until 8 - row) {
                        val alpha = (1f - (row + col) / 14f) * edgeFlicker
                        drawRect(
                            color = ElectricCyan.copy(alpha = alpha),
                            topLeft = Offset(
                                startX + col * pixelSize,
                                startY + row * pixelSize
                            ),
                            size = Size(pixelSize - 1, pixelSize - 1)
                        )
                    }
                }
            }
            .auraDrip(MagentaKai, DripCorner.BottomRight, 0.8f)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Info panel
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "020",
                        style = TextStyle(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan.copy(alpha = 0.3f)
                        )
                    )
                    
                    Text(
                        text = "TRANSMUTATOR",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            letterSpacing = 3.sp
                        )
                    )
                    
                    Text(
                        text = "ICON & ASSET SHREDDER",
                        style = TextStyle(
                            fontSize = 9.sp,
                            color = NexusGreen.copy(alpha = 0.7f),
                            letterSpacing = 2.sp
                        )
                    )
                }
                
                // Live terminal window
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = "> GEN_HOOK [ICON_PACK_047]",
                        style = TextStyle(
                            fontSize = 8.sp,
                            color = NexusGreen,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "> SUCCESS • 23ms",
                        style = TextStyle(
                            fontSize = 8.sp,
                            color = ElectricCyan,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "> SWARM_CONF: PEAK",
                        style = TextStyle(
                            fontSize = 8.sp,
                            color = MagentaKai,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    )
                }
            }
            
            // Right: 3D Sphere Grid visualization
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val baseRadius = size.minDimension / 3
                    
                    // Rotating sphere grid
                    val rotationRad = (sphereRotation * PI / 180).toFloat()
                    
                    // Draw sphere rings
                    for (ring in 0..2) {
                        val ringRadius = baseRadius * (0.6f + ring * 0.2f)
                        val tiltOffset = sin(rotationRad + ring) * 15f
                        
                        drawCircle(
                            color = MagentaKai.copy(alpha = 0.3f - ring * 0.1f),
                            radius = ringRadius,
                            center = Offset(centerX, centerY + tiltOffset),
                            style = Stroke(width = 1.5f)
                        )
                    }
                    
                    // Meridian lines
                    for (i in 0 until 6) {
                        val angle = (i * PI / 3 + rotationRad).toFloat()
                        val x1 = centerX + cos(angle) * baseRadius * 0.3f
                        val y1 = centerY + sin(angle) * baseRadius * 0.3f
                        val x2 = centerX + cos(angle) * baseRadius
                        val y2 = centerY + sin(angle) * baseRadius * 0.7f
                        
                        drawLine(
                            color = GenesisGold.copy(alpha = 0.4f),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 1f
                        )
                    }
                    
                    // Center nucleus
                    drawCircle(
                        color = NexusGreen,
                        radius = 6f,
                        center = Offset(centerX, centerY)
                    )
                }
                
                // Active count badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = MagentaKai.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ACTIVE: $activeTransmutations",
                        style = TextStyle(
                            fontSize = 9.sp,
                            color = MagentaKai,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MODULE 021 // KINETICFORGE: LATTICE — The Structural Grid
// Wireframe transparency | Glowing pink outline vibrating
// Reality-Override Dial | Casberry pulse on setting change
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun KineticForgeLatticeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    realityState: Float = 0.7f // 0.0 = Stock, 1.0 = Full Exodus
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lattice_vibe")
    
    val outlineVibration by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outline_vibe"
    )
    
    val dialGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dial_glow"
    )
    
    val scope = rememberCoroutineScope()
    var localReality by remember { mutableFloatStateOf(realityState) }
    var isDragging by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .drawBehind {
                // Glowing pink vibrating outline
                val outlinePath = Path().apply {
                    addRect(
                        Rect(
                            left = 2f + outlineVibration,
                            top = 2f - outlineVibration,
                            right = size.width - 2f + outlineVibration,
                            bottom = size.height - 2f - outlineVibration
                        )
                    )
                }
                
                drawPath(
                    path = outlinePath,
                    color = MagentaKai.copy(alpha = 0.6f),
                    style = Stroke(width = 2f)
                )
                
                // Inner wireframe grid (showing background through)
                val gridSpacing = 30f
                for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                    drawLine(
                        color = ElectricCyan.copy(alpha = 0.08f),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = 0.5f
                    )
                }
                for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                    drawLine(
                        color = ElectricCyan.copy(alpha = 0.08f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = 0.5f
                    )
                }
                
                // Corner reinforcements
                drawCircle(
                    color = GenesisGold.copy(alpha = 0.3f),
                    radius = 8f,
                    center = Offset(15f, 15f)
                )
                drawCircle(
                    color = GenesisGold.copy(alpha = 0.3f),
                    radius = 8f,
                    center = Offset(size.width - 15f, size.height - 15f)
                )
            }
            .auraDrip(ElectricCyan, DripCorner.BottomLeft, 0.6f)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "021",
                        style = TextStyle(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MagentaKai.copy(alpha = 0.4f)
                        )
                    )
                    Text(
                        text = "LATTICE",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagentaKai,
                            letterSpacing = 4.sp
                        )
                    )
                }
                
                // Stock <-> Exodus indicator
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "STOCK",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = if (localReality < 0.5f) NexusGreen else Color.Gray,
                                fontWeight = if (localReality < 0.5f) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Text(
                            text = "◄",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = ElectricCyan
                            )
                        )
                        Text(
                            text = "EXODUS",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = if (localReality >= 0.5f) GenesisGold else Color.Gray,
                                fontWeight = if (localReality >= 0.5f) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
            
            // Central Reality-Override Dial
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false },
                            onDragCancel = { isDragging = false },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    localReality = (localReality + dragAmount.x / 500f)
                                        .coerceIn(0f, 1f)
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Dial glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(dialGlow)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MagentaKai.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
                
                Canvas(modifier = Modifier.size(100.dp)) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.minDimension / 2 - 10f
                    
                    // Outer ring
                    drawCircle(
                        color = MagentaKai,
                        radius = radius,
                        style = Stroke(width = 3f)
                    )
                    
                    // Progress arc (reality state)
                    val sweepAngle = localReality * 360f
                    drawArc(
                        color = GenesisGold,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 4f)
                    )
                    
                    // Ticks
                    for (i in 0 until 12) {
                        val angle = (i * PI / 6).toFloat() - PI.toFloat() / 2
                        val x1 = centerX + cos(angle) * (radius - 8)
                        val y1 = centerY + sin(angle) * (radius - 8)
                        val x2 = centerX + cos(angle) * radius
                        val y2 = centerY + sin(angle) * radius
                        
                        drawLine(
                            color = if (i / 12f <= localReality) NexusGreen else ElectricCyan.copy(alpha = 0.3f),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 2f
                        )
                    }
                    
                    // Handle at current reality position
                    val handleAngle = (localReality * 2 * PI - PI / 2).toFloat()
                    val handleX = centerX + cos(handleAngle) * radius * 0.8f
                    val handleY = centerY + sin(handleAngle) * radius * 0.8f
                    
                    drawCircle(
                        color = if (isDragging) NexusGreen else GenesisGold,
                        radius = 10f,
                        center = Offset(handleX, handleY)
                    )
                    
                    // Center text
                    // (Would need Compose Canvas text drawing - simplified here)
                }
                
                // Drag hint
                if (isDragging) {
                    Text(
                        text = "${(localReality * 100).toInt()}%",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NexusGreen
                        )
                    )
                }
            }
            
            // Footer with LSPosed status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STRUCTURAL OVERRIDE",
                    style = TextStyle(
                        fontSize = 9.sp,
                        color = ElectricCyan.copy(alpha = 0.7f),
                        letterSpacing = 2.sp
                    )
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NexusGreen, RoundedCornerShape(50))
                    )
                    Text(
                        text = "LSP: ZERO CONFLICTS",
                        style = TextStyle(
                            fontSize = 8.sp,
                            color = NexusGreen,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// PREVIEW / CONTAINER — How to use the 3 cards together
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun KineticForgeCardContainer(
    onCoreClick: () -> Unit = {},
    onTransmutatorClick: () -> Unit = {},
    onLatticeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top: Core (angled, commanding)
        KineticForgeCoreCard(
            onClick = onCoreClick,
            isActive = true
        )
        
        // Bottom row: Transmutator + Lattice side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KineticForgeTransmutatorCard(
                onClick = onTransmutatorClick,
                modifier = Modifier.weight(1f),
                activeTransmutations = 14
            )
            
            KineticForgeLatticeCard(
                onClick = onLatticeClick,
                modifier = Modifier.weight(1f),
                realityState = 0.75f
            )
        }
    }
}
