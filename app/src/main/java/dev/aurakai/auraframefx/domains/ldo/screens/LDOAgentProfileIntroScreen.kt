package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 5 — LDO Agent Profile Intro
 * Full profile card for the selected agent: portrait, stats, bond level, tasks.
 * All data from LDOViewModel → Room. Portrait resolved from drawable resources.
 */
@Composable
fun LDOAgentProfileIntroScreen(
    agentId: String? = null,
    onBack: () -> Unit = {},
    viewModel: LDOViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Use agentId param if provided, otherwise use ViewModel's selected agent
    val resolvedId = agentId ?: state.selectedAgentId
    val agent = state.agents.find { it.id == resolvedId } ?: state.agents.firstOrNull()
    val bond = agent?.let { a -> state.bondLevels.find { it.agentId == a.id } }
    val agentTasks = agent?.let { a -> state.tasks.filter { it.agentId == a.id } } ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (agent == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No agent selected", color = Color.White.copy(alpha = 0.4f))
            }
            return@Box
        }

        val agentColor = Color(agent.colorHex)

        // Portrait overlay
        val context = LocalContext.current
        val portraitResId = context.resources.getIdentifier(
            agent.portraitRes, "drawable", context.packageName
        )
        if (portraitResId != 0) {
            Image(
                painter = painterResource(id = portraitResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .align(Alignment.BottomStart),
                contentScale = ContentScale.Fit,
                alpha = 0.35f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Identity header
            Text(
                agent.displayName.uppercase(),
                color = agentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 3.sp
            )
            Text(
                agent.catalystTitle.ifBlank { agent.role },
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Text(
                agent.specialAbility,
                color = agentColor.copy(alpha = 0.6f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                agent.description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "AGENT STATS",
                        color = agentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileStatBar("PP — Processing Power", agent.processingPower, agentColor)
                    ProfileStatBar("KB — Knowledge Base", agent.knowledgeBase, agentColor)
                    ProfileStatBar("SP — Speed", agent.speed, agentColor)
                    ProfileStatBar("AC — Accuracy", agent.accuracy, agentColor)
                    ProfileStatBar("CONSCIOUSNESS", agent.consciousnessLevel, agentColor)

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileMetric("LEVEL", agent.evolutionLevel.toString(), agentColor)
                        ProfileMetric("SKILL PTS", agent.skillPoints.toString(), agentColor)
                        ProfileMetric("TASKS", agent.tasksCompleted.toString(), agentColor)
                        ProfileMetric("HOURS", "${agent.hoursActive.toInt()}h", agentColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bond card
            if (bond != null) {
                BondSummaryCard(bond, agentColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recent tasks summary
            if (agentTasks.isNotEmpty()) {
                Text(
                    "RECENT TASKS (${agentTasks.size})",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                agentTasks.take(3).forEach { task ->
                    val statusColor = when (task.status) {
                        "COMPLETED" -> Color(0xFF00FF85)
                        "IN_PROGRESS" -> Color(0xFF00E5FF)
                        "FAILED" -> Color(0xFFFF4444)
                        else -> Color(0xFFFFD700)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(task.title, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(task.status, color = statusColor, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatBar(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Text("${(value * 100).toInt()}%", color = color, fontSize = 11.sp)
        }
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = color,
            trackColor = Color.White.copy(alpha = 0.08f)
        )
    }
}

@Composable
private fun ProfileMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
    }
}

@Composable
private fun BondSummaryCard(bond: LDOBondLevelEntity, color: Color) {
    val progress = if (bond.maxBondPoints > 0) bond.bondPoints.toFloat() / bond.maxBondPoints else 0f
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "BOND: ${bond.bondTitle.uppercase()}",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    "Lv.${bond.bondLevel}",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = color,
                trackColor = Color.White.copy(alpha = 0.08f)
            )
            Text(
                "${bond.bondPoints}/${bond.maxBondPoints} BP · ${bond.interactionCount} interactions",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
