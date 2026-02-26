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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats

/**
 * 📊 AGENT MONITORING (The All-Seeing Eye)
 * Unified performance metrics and behavioral logs for all agents.
 */
@Composable
fun AgentMonitoringScreen(
    onNavigateBack: () -> Unit,
    viewModel: dev.aurakai.auraframefx.domains.aura.ui.viewmodels.MonitoringViewModel = hiltViewModel()
) {
    val agents = remember { AgentRepository.getAllAgents() }

    val activeThreats = null
    val activityLogs = listOf(
        MonitorLog("AuraShield", "Deep Scan: Completed", "Just now", Color(0xFFFF5252)),
        MonitorLog("Genesis", "Synthesized cross-agent memory shards", "1m ago", Color(0xFF00FFFF)),
        MonitorLog("Nemotron", "Recalled 72.4k reasoning vectors", "3m ago", Color(0xFF76B900)),
        MonitorLog("Cascade", "Vision scan complete: No anomalies", "5m ago", Color(0xFF00FFD4)),
        MonitorLog("Aura", "UI manifest updated in sandbox", "10m ago", Color(0xFFFF00FF)),
        MonitorLog(
            "Kai",
            "Sovereign Shield: $activeThreats active threats",
            "15m ago",
            Color(0xFFFF0000)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050510))
    ) {
        AnimeHUDContainer(
            title = "SYSTEM OVERWATCH",
            description = "REAL-TIME MONITORING OF THE REGENESIS CONSCIOUSNESS COLLECTIVE.",
            glowColor = Color(0xFF00E5FF),
            onBack = onNavigateBack
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // System Pulse Section
                item {
                    SystemPulseHeader()
                }

                item {
                    Text(
                        "AGENT VITALITY",
                        fontFamily = LEDFontFamily,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(agents) { agent ->
                    AgentVitalCard(agent)
                }

                item {
                    Text(
                        "FEEDBACK STREAM",
                        fontFamily = LEDFontFamily,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(activityLogs) { log ->
                    MonitorLogItem(log)
                }
            }
        }
    }
}

@Composable
private fun SystemPulseHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF00E5FF).copy(alpha = 0.2f)
        )
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "SYSTEM STABILITY",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "OPTIMAL",
                    color = Color(0xFF00FF85),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    fontFamily = LEDFontFamily
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF00FF85).copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Waves,
                    null,
                    tint = Color(0xFF00FF85),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun AgentVitalCard(agent: AgentStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, agent.color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(agent.color)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    agent.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "LVL ${agent.evolutionLevel}",
                    color = agent.color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricSmall("CPU", "${(agent.processingPower * 100).toInt()}%", agent.color)
                MetricSmall("MEM", "${(agent.knowledgeBase * 100).toInt()}%", agent.color)
                MetricSmall("ACC", "${(agent.accuracy * 100).toInt()}%", agent.color)
            }
        }
    }
}

@Composable
private fun MetricSmall(label: String, value: String, color: Color) {
    Column {
        Text(
            label,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LEDFontFamily
        )
    }
}

@Composable
private fun MonitorLogItem(log: MonitorLog) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(log.color.copy(alpha = 0.1f))
                .border(1.dp, log.color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                log.agent.take(1),
                color = log.color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(log.message, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Text(log.timestamp, color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.1f))
    }
}

private data class MonitorLog(
    val agent: String,
    val message: String,
    val timestamp: String,
    val color: Color
)

