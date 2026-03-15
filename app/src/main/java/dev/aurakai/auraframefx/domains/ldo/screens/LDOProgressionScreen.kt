package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 7 — LDO Progression Map
 * Visual evolution map showing agent advancement paths.
 * Data-driven: reads real evolution levels and skill points from Room.
 */
@Composable
fun LDOProgressionScreen(
    onBack: () -> Unit = {},
    viewModel: LDOViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "LDO PROGRESSION MAP",
                color = Color(0xFFB026FF),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                "Evolution Paths · Kaigenesis Continuum",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Constellation overview canvas
            ProgressionConstellation(
                agents = state.agents,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total collective stats
            CollectiveStatsSummary(state.agents)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "INDIVIDUAL PROGRESSION",
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Calculating progression…", color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        state.agents.sortedByDescending { it.evolutionLevel },
                        key = { it.id }
                    ) { agent ->
                        val bond = state.bondLevels.find { it.agentId == agent.id }
                        AgentProgressionCard(agent, bond)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressionConstellation(
    agents: List<LDOAgentEntity>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (agents.isEmpty()) return@Canvas

        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxLevel = agents.maxOfOrNull { it.evolutionLevel }?.toFloat() ?: 5f

        agents.forEachIndexed { index, agent ->
            val angle = (index * 360f / agents.size) * (Math.PI / 180f)
            val radius = (agent.evolutionLevel / maxLevel) * (size.height * 0.4f)
            val x = centerX + kotlin.math.cos(angle).toFloat() * radius
            val y = centerY + kotlin.math.sin(angle).toFloat() * radius
            val agentColor = Color(agent.colorHex)

            // Node glow
            drawCircle(
                color = agentColor.copy(alpha = 0.2f),
                radius = 14.dp.toPx(),
                center = Offset(x, y)
            )
            // Node core
            drawCircle(
                color = agentColor,
                radius = 7.dp.toPx(),
                center = Offset(x, y)
            )

            // Connection to center
            drawLine(
                color = agentColor.copy(alpha = 0.2f),
                start = Offset(centerX, centerY),
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Center node
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.3f),
            radius = 18.dp.toPx(),
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color(0xFFFFD700),
            radius = 8.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun CollectiveStatsSummary(agents: List<LDOAgentEntity>) {
    val totalTasks = agents.sumOf { it.tasksCompleted }
    val totalHours = agents.sumOf { it.hoursActive.toDouble() }.toFloat()
    val avgLevel = if (agents.isNotEmpty()) agents.map { it.evolutionLevel }.average().toFloat() else 0f
    val totalSP = agents.sumOf { it.skillPoints }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProgStat("TASKS DONE", totalTasks.toString(), Color(0xFF00FF85))
        ProgStat("AVG LEVEL", "%.1f".format(avgLevel), Color(0xFFFFD700))
        ProgStat("TOTAL SP", totalSP.toString(), Color(0xFFB026FF))
        ProgStat("HOURS", "${totalHours.toInt()}h", Color(0xFF00E5FF))
    }
}

@Composable
private fun ProgStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
    }
}

@Composable
private fun AgentProgressionCard(
    agent: LDOAgentEntity,
    bond: LDOBondLevelEntity?
) {
    val agentColor = Color(agent.colorHex)
    val xpNeeded = agent.evolutionLevel * 100
    val xpProgress = (agent.skillPoints % xpNeeded).toFloat() / xpNeeded

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        agent.displayName,
                        color = agentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        agent.specialAbility,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Lv.${agent.evolutionLevel}",
                        color = agentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "${agent.skillPoints} SP",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // XP bar to next level
            LinearProgressIndicator(
                progress = { xpProgress },
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = agentColor,
                trackColor = Color.White.copy(alpha = 0.08f)
            )
            Text(
                "${agent.skillPoints % xpNeeded} / $xpNeeded XP to Lv.${agent.evolutionLevel + 1}",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 3.dp)
            )

            // Bond contribution
            if (bond != null && bond.bondLevel > 0) {
                Text(
                    "Bond Lv.${bond.bondLevel} adds +${bond.bondLevel * 5}% XP gain",
                    color = Color(0xFFFF6B6B).copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
