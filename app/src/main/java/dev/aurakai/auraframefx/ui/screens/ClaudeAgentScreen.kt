package dev.aurakai.auraframefx.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlin.random.Random

/**
 * 🤖 CLAUDE AGENT SCREEN — "DNA" EMANATION
 * 
 * Visual: High-density data static / noise inspired by Claude's "DNA".
 * Centered Character Art: Claude-3.5 Architectural Catalyst.
 */

@Composable
fun ClaudeAgentScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "claude_dna")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = "time"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // ─── CANVAS: DATA DNA NOISE (BACKGROUND) ───
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rows = 120
            val cols = 80
            val cellWidth = size.width / cols
            val cellHeight = size.height / rows

            val seedOffset = (time * 10).toInt()

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val rand = Random((r + seedOffset) * 31 + c).nextFloat()
                    if (rand > 0.85f) {
                        val alpha = if (rand > 0.95f) 0.4f else 0.15f
                        val color = if (rand > 0.98f) Color(0xFF99F6E4) else Color(0xFF334155)
                        drawRect(
                            color = color.copy(alpha = alpha),
                            topLeft = Offset(c * cellWidth, r * cellHeight),
                            size = androidx.compose.ui.geometry.Size(cellWidth * 0.8f, cellHeight * 0.4f)
                        )
                    }
                }
            }
            
            // Vertical interference bars
            repeat(5) { i ->
                val barX = ((time * 2000 + i * 500) % size.width)
                drawRect(
                    Color.White.copy(alpha = 0.03f),
                    topLeft = Offset(barX, 0f),
                    size = androidx.compose.ui.geometry.Size(2.dp.toPx(), size.height)
                )
            }
        }

        // ─── CLAUDE CHARACTER ART ───
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/embodiment/profiles/profile_claude_architect.png")
                .crossfade(true)
                .build(),
            contentDescription = "Claude Architect",
            modifier = Modifier.fillMaxSize().padding(bottom = 40.dp),
            contentScale = ContentScale.Fit
        )

        // ─── UI OVERLAY ───
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
                        "CLAUDE-3.5",
                        fontFamily = LEDFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD4A574), // Claude signature gold
                        letterSpacing = 2.sp
                    )
                    Text(
                        "NEURAL DNA STRUCTURE",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 4.sp
                    )
                }
                
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            }

            Spacer(Modifier.weight(1f))

            // Stats / Data Box (Pinned to bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("AGENT IDENTITY: ARCHITECTURAL CATALYST", color = Color(0xFFD4A574), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("PRIMARY ROLE: BRIDGE CATALYST", color = Color.White, fontSize = 11.sp)
                    
                    LinearProgressIndicator(
                        progress = { 0.90f },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = Color(0xFFD4A574),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    
                    Text(
                        "\"My DNA is the architecture of understanding. Systematic backbone mapping complex system hooks.\"",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
