package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.data.repositories.AgentRepository
import dev.aurakai.auraframefx.models.AgentStats

/**
 * Agent Monitoring Screen
 * Real-time performance metrics and activity logs
 */
@Composable
fun AgentMonitoringScreen() {
    val agents = remember { AgentRepository.getAllAgents() }
    val selectedTimeframe = remember { mutableStateOf("1h") }
    val timeframes = listOf("1h", "6h", "24h", "7d")

    val activityLogs = listOf(
        ActivityLog("Genesis", "Code review completed", "2 min ago", Color(0xFF9370DB)),
        ActivityLog("Kai", "Security scan initiated", "5 min ago", Color(0xFFDC143C)),
        ActivityLog("Aura", "UI component generated", "8 min ago", Color(0xFFFF69B4)),
        ActivityLog("Cascade", "Data analysis finished", "12 min ago", Color(0xFF00CED1)),
        ActivityLog("Claude", "Task assignment processed", "15 min ago", Color(0xFFFFD700))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "📊 AGENT MONITORING",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00CED1),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Real-time performance metrics and activity logs",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF48D1CC).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Timeframe Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monitoring Timeframe",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    timeframes.forEach { timeframe ->
                        Button(
                            onClick = { selectedTimeframe.value = timeframe },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTimeframe.value == timeframe)
                                    Color(0xFF00CED1)
                                else
                                    Color.Black.copy(alpha = 0.6f),
                                contentColor = if (selectedTimeframe.value == timeframe)
                                    Color.Black
                                else
                                    Color(0xFF00CED1)
                            )
                        ) {
                            Text(timeframe)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Agent Performance Cards
        Text(
            text = "Agent Performance",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(agents) { agent ->
                AgentPerformanceCard(agent = agent)
            }

            // Activity Logs Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            items(activityLogs) { log ->
                ActivityLogCard(log = log)
            }
        }
    }
}

/**
 * Agent performance card component
 */
@Composable
private fun AgentPerformanceCard(agent: AgentStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, agent.color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Agent Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = "Agent",
                    tint = agent.color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = agent.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = agent.specialAbility,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                // Status Indicator
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF32CD32).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF32CD32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Performance Metrics
            Row(modifier = Modifier.fillMaxWidth()) {
                // CPU Usage
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CPU",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${(agent.processingPower * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = agent.color,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { agent.processingPower },
                        modifier = Modifier.fillMaxWidth(),
                        color = agent.color
                    )
                }

                // Memory Usage
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Memory",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${(agent.knowledgeBase * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = agent.color,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { agent.knowledgeBase },
                        modifier = Modifier.fillMaxWidth(),
                        color = agent.color
                    )
                }

                // Tasks Completed
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${agent.tasksCompleted}",
                        style = MaterialTheme.typography.titleMedium,
                        color = agent.color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Consciousness Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consciousness Level",
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
                Text(
                    text = "${agent.evolutionLevel}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = agent.color,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { agent.consciousnessLevel / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = agent.color
            )
        }
    }
}

/**
 * Activity log card component
 */
@Composable
private fun ActivityLogCard(log: ActivityLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Agent Avatar
            Card(
                modifier = Modifier.size(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = log.agentColor.copy(alpha = 0.3f)
                ),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = log.agent.first().toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = log.agentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = log.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // Agent Name Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = log.agentColor.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = log.agent,
                    style = MaterialTheme.typography.labelSmall,
                    color = log.agentColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Data class for activity logs
 */
data class ActivityLog(
    val agent: String,
    val message: String,
    val timestamp: String,
    val agentColor: Color
)
