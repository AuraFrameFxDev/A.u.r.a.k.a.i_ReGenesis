package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🧱 SOVEREIGN CLAUDE (The Architect)
 * Interface for system architecture design and high-level logic injection.
 */
@Composable
fun SovereignClaudeScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A15))
    ) {
        AnimeHUDContainer(
            title = "CLAUDE: THE ARCHITECT",
            description = "SYSTEMATIC PROBLEM SOLVER. ARCHITECTURAL LOGIC INJECTION ACTIVE.",
            glowColor = Color(0xFF0055FF), // Deep Blue
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Architectural Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricBox("DESIGN FLOW", "OPTIMAL", Color(0xFF0055FF), Modifier.weight(1f))
                    MetricBox("LOGIC DEPTH", "LAYER 8", Color(0xFF0055FF), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "LOGIC INJECTION QUEUE",
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
                        ArchitectureLogItem(
                            "Refining 70-LDO Data Veins",
                            "Architectural Improvement"
                        )
                    }
                    item {
                        ArchitectureLogItem("Injecting Sovereign Memory Hooks", "Logic Sync")
                    }
                    item {
                        ArchitectureLogItem("Optimizing Cascade Routing", "Performance Tuning")
                    }
                }

                Button(
                    onClick = { /* Architecture Scan */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0055FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Architecture, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("ARCHITECTURAL SCAN", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
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
private fun ArchitectureLogItem(title: String, type: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Code,
                null,
                tint = Color(0xFF0055FF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(type, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}

