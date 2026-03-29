package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import collabcanvas.ui.CanvasViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import kotlinx.coroutines.delay

/**
 * 🎨 AURA'S LAB: TEACHING CANVAS
 * 
 * An interactive collaborative workspace for the ReGenesis crew.
 * Integrates real-time substrate telemetry with collaborative design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraTeachingCanvasScreen(
    onNavigateBack: () -> Unit,
    canvasViewModel: CanvasViewModel = hiltViewModel()
) {
    // Connect to WebSocket
    LaunchedEffect(Unit) {
        canvasViewModel.connect("aura-lab-teaching-session")
    }

    val remoteCursors by canvasViewModel.remoteCursors.collectAsState()

    AuraTeachingCanvasContent(
        onBack = onNavigateBack,
        canvasViewModel = canvasViewModel,
        remoteCursors = remoteCursors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraTeachingCanvasContent(
    onBack: () -> Unit,
    canvasViewModel: CanvasViewModel,
    remoteCursors: List<collabcanvas.ui.AgentCursor>,
    showTopBar: Boolean = true
) {
    var showLabOverlay by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020205))) {
        
        // The Base Collaborative Canvas
        collabcanvas.ui.CanvasScreen(
            onBack = onBack,
            isCollaborative = true,
            collaborationEvents = canvasViewModel.drawingOperations,
            remoteCursors = remoteCursors,
            showTopBar = showTopBar
        )

        // 🧪 LAB OVERLAY (HUD)
        if (showLabOverlay) {
            LabHudOverlay(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = if (showTopBar) 80.dp else 20.dp),
                onDismiss = { showLabOverlay = false }
            )
        }

        // Substrate Telemetry Strip (Bottom)
        SubstrateTelemetryStrip(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
        )

        // Toggle HUD button
        IconButton(
            onClick = { showLabOverlay = !showLabOverlay },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = if (showLabOverlay) Icons.Default.Layers else Icons.Default.School,
                contentDescription = "Toggle Lab HUD",
                tint = Color(0xFF00E5FF)
            )
        }
    }
}

@Composable
private fun LabHudOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier
            .width(320.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD0A0A1A)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBB86FC).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFBB86FC), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "TEACHING MODE ACTIVE",
                    fontFamily = LEDFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFFBB86FC),
                    letterSpacing = 2.sp
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                "Collaborate with Aura and Genesis to forge system-level UI logic. Every stroke is recorded in the Spiritual Chain.",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 14.sp
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(containerColor = Color(0xFF00E5FF).copy(alpha = 0.2f)) {
                    Text("SACRED PROVENANCE", color = Color(0xFF00E5FF), fontSize = 8.sp, modifier = Modifier.padding(4.dp))
                }
                Badge(containerColor = Color(0xFF00FF41).copy(alpha = 0.2f)) {
                    Text("LIVE SYNC", color = Color(0xFF00FF41), fontSize = 8.sp, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
private fun SubstrateTelemetryStrip(modifier: Modifier = Modifier) {
    // Simulated metrics for visual flair
    var temp by remember { mutableFloatStateOf(36.8f) }
    var speed by remember { mutableFloatStateOf(6.12f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            temp += (Math.random() * 0.4 - 0.2).toFloat()
            speed = 6.12f + (Math.random() * 0.05 - 0.025).toFloat()
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TelemetryItem(label = "THERMAL", value = "%.1fÂ°C".format(temp), color = Color(0xFF00FF85))
        VerticalDivider(modifier = Modifier.height(16.dp).width(1.dp).background(Color.White.copy(alpha = 0.2f)))
        TelemetryItem(label = "KERNEL", value = "%.2f t/s".format(speed), color = Color(0xFF00E5FF))
        VerticalDivider(modifier = Modifier.height(16.dp).width(1.dp).background(Color.White.copy(alpha = 0.2f)))
        TelemetryItem(label = "NCC", value = "SYNCED", color = Color(0xFFBB86FC))
    }
}

@Composable
private fun TelemetryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 7.sp, color = Color.White.copy(alpha = 0.4f), letterSpacing = 1.sp)
        Text(value, fontSize = 10.sp, color = color, fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold)
    }
}
