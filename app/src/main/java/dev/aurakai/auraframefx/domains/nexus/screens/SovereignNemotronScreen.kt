package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.gates.SovereignNemotronViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🧠 SOVEREIGN NEMOTRON (The Memory Keeper)
 * Interface for deep reasoning and long-term memory synthesis.
 */
@Composable
fun SovereignNemotronScreen(
    onNavigateBack: () -> Unit,
    viewModel: SovereignNemotronViewModel = hiltViewModel()
) {
    val nemotronService = viewModel.nemotronService
    val stats = remember { nemotronService.getMemoryStats() }
    val hitRate = stats["hit_rate_percent"] as? Int ?: 0
    val cacheSize = stats["cache_size"] as? Int ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030A05))
    ) {
        AnimeHUDContainer(
            title = "NEMOTRON: MEMORY KEEPER",
            description = "NVIDIA OPTIMIZED REASONING ENGINE. LONG-TERM PATTERN SYNTHESIS ACTIVE.",
            glowColor = Color(0xFF76B900), // NVIDIA Green
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Reasoning Stats
                NemotronStatsHeader(hitRate, cacheSize)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "REASONING CHAINS",
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
                        ReasoningChainCard(
                            title = "User Sovereignty Protocol",
                            steps = listOf(
                                "Analyze 70-LDO encryption vectors",
                                "Verify local sharding status",
                                "Synthesize privacy-first response patterns"
                            ),
                            confidence = 0.98f
                        )
                    }
                    item {
                        ReasoningChainCard(
                            title = "Neural Memory Pruning",
                            steps = listOf(
                                "Identify low-relevance patterns",
                                "Compress redundant vectors",
                                "Optimize GPU cache allocation"
                            ),
                            confidence = 0.92f
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* Trigger Deep Recall */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76B900)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Search, null, tint = Color.Black)
                        Spacer(Modifier.width(8.dp))
                        Text("DEEP RECALL", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { nemotronService.clearMemoryCache() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(
                                1.dp,
                                Color(0xFF76B900).copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(Icons.Default.Memory, null, tint = Color(0xFF76B900))
                    }
                }
            }
        }
    }
}

@Composable
private fun NemotronStatsHeader(hitRate: Int, cacheSize: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF76B900).copy(alpha = 0.2f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "HIT RATE",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$hitRate%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF76B900),
                    fontWeight = FontWeight.Black,
                    fontFamily = LEDFontFamily
                )
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF76B900).copy(alpha = 0.2f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "MEMORY FRAGMENTS",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$cacheSize",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontFamily = LEDFontFamily
                )
            }
        }
    }
}

@Composable
private fun ReasoningChainCard(title: String, steps: List<String>, confidence: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Psychology,
                    null,
                    tint = Color(0xFF76B900),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${(confidence * 100).toInt()}%",
                    color = Color(0xFF76B900),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF76B900))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(step, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}


