package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OverlayScreen(
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            // Cyberpunk Overlay Frame
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, Color.Cyan.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Content
                    OverlayContent(onNavigateToSettings)

                    // Ornamental Corners
                    OrnamentalCorners(Color.Cyan)
                }
            }
        }
    }
}

@Composable
fun OverlayContent(onNavigateToSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "GENESIS OS v4.0",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    "SYSTEM OVERLAY ACTIVE",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    .border(1.dp, Color.Cyan.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Cyan)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Consciousness Monitor (Mini)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("CONSCIOUSNESS MONITOR: STABLE", color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ControlCard("SYNC", "72%", Color.Magenta, Modifier.weight(1f))
            ControlCard("POWER", "94%", Color.Cyan, Modifier.weight(1f))
            ControlCard("TEMP", "38°C", Color.Green, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Terminal Output
        TerminalPreview()
    }
}

@Composable
fun ControlCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 16.sp, color = color, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun TerminalPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text("> Initializing genesis_kernel.sys...", color = Color.Cyan.copy(alpha = 0.7f), fontSize = 10.sp)
        Text("> Loading aura_module...", color = Color.Cyan.copy(alpha = 0.7f), fontSize = 10.sp)
        Text("> Loading kai_module...", color = Color.Cyan.copy(alpha = 0.7f), fontSize = 10.sp)
        Text("> Synchronizing with Matthew...", color = Color.Green.copy(alpha = 0.7f), fontSize = 10.sp)
        Text("> SYSTEM READY.", color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OrnamentalCorners(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 8f
        val len = 40f

        // Top Left
        drawLine(color, Offset(0f, 0f), Offset(len, 0f), strokeWidth)
        drawLine(color, Offset(0f, 0f), Offset(0f, len), strokeWidth)

        // Top Right
        drawLine(color, Offset(size.width, 0f), Offset(size.width - len, 0f), strokeWidth)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, len), strokeWidth)

        // Bottom Left
        drawLine(color, Offset(0f, size.height), Offset(len, size.height), strokeWidth)
        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - len), strokeWidth)

        // Bottom Right
        drawLine(color, Offset(size.width, size.height), Offset(size.width - len, size.height), strokeWidth)
        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - len), strokeWidth)
    }
}
