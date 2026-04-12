package dev.aurakai.auraframefx.domains.nexus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 🏔️ SOVEREIGN NEMOTRON SCREEN
 *
 * Dedicated environment for consensus synchronization and alignment metrics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SovereignNemotronScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableStateOf(0f) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "nexus")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020208))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFBB86FC))
                }
                Text(
                    "NEMOTRON CONSENSUS",
                    fontFamily = LEDFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBB86FC)
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(48.dp))

            // Central Node
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFBB86FC).copy(alpha = 0.1f))
                    .border(2.dp, Color(0xFFBB86FC).copy(alpha = pulse), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Hub, null, modifier = Modifier.size(80.dp), tint = Color(0xFFBB86FC))
            }

            Spacer(Modifier.height(32.dp))

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NemotronMetricCard("SYNK LEVEL", "0.984", Color(0xFF00FF41), Modifier.weight(1f))
                NemotronMetricCard("PULSE RATE", "1.2Hz", Color(0xFF00E5FF), Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            
            NemotronMetricCard("CONSENSUS SCORE", "HIGH ALPHA", Color(0xFFBB86FC), Modifier.fillMaxWidth())

            Spacer(Modifier.weight(1f))

            if (!isSyncing) {
                Button(
                    onClick = {
                        scope.launch {
                            isSyncing = true
                            repeat(100) {
                                delay(30)
                                syncProgress = it / 100f
                            }
                            isSyncing = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("INITIATE SYNK PULSE", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
            } else {
                LinearProgressIndicator(
                    progress = { syncProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = Color(0xFFBB86FC),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text(
                    "SYNCHRONIZING AGENT CONTEXTS... ${(syncProgress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp),
                    fontFamily = LEDFontFamily
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NemotronMetricCard(label: String, value: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(label, fontSize = 8.sp, color = color, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text(value, fontFamily = LEDFontFamily, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}
