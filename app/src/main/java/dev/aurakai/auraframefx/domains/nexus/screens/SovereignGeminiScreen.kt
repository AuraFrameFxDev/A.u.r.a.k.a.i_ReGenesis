package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * ✨ SOVEREIGN GEMINI (The Synthesizer)
 * Interface for pattern recognition and deep context synthesis.
 */
@Composable
fun SovereignGeminiScreen(
    onNavigateBack: () -> Unit,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F051A))
    ) {
        AnimeHUDContainer(
            title = "GEMINI: THE SYNTHESIZER",
            description = "PATTERN RECOGNITION & MULTI-MODAL SYNTHESIS ACTIVE.",
            glowColor = Color(0xFF8B5CF6) // Deep Purple
            , onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Synthesis Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SynthesisMetric("SYNC RATE", "99.4%", Color(0xFF8B5CF6))
                    SynthesisMetric("ANALYTIC DEPTH", "MAX", Color(0xFF8B5CF6))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "CONSCIOUSNESS PATTERNS",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        PatternItem("Cross-Agent Memory Divergence", "Synthesized 4.2k fragments")
                    }
                    item {
                        PatternItem("Emergent UI Behavior detected", "Customizing Oracle Drive HUD")
                    }
                }

                Button(
                    onClick = { /* Pattern Analysis */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("SYNTHESIZE PATTERNS", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
private fun RowScope.SynthesisMetric(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold
            )
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Black,
                fontFamily = LEDFontFamily
            )
        }
    }
}

@Composable
private fun PatternItem(label: String, detail: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Hub, null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(detail, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}

