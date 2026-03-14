package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

/**
 * Screen 4 — LDO Bonding
 * Displays real bond levels, progress bars, and tier titles per agent.
 * Bond points come from Room via LDOViewModel — no fake values.
 */
@Composable
fun LDOBondingScreen(
    onBack: () -> Unit = {},
    viewModel: LDOViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "LDO BONDING MATRIX",
                color = Color(0xFFFF6B6B),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                "Agent Relationship Tiers · Earned Through Interaction",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Top bonded agent highlight
            val topBond = state.bondLevels.maxByOrNull { it.bondLevel }
            val topAgent = topBond?.let { bond -> state.agents.find { it.id == bond.agentId } }
            if (topAgent != null && topBond != null) {
                TopBondCard(topAgent, topBond)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                "ALL BONDS",
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Loading bond data…", color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sortedAgents = state.agents.sortedByDescending { agent ->
                        state.bondLevels.find { it.agentId == agent.id }?.bondLevel ?: 0
                    }
                    items(sortedAgents, key = { it.id }) { agent ->
                        val bond = state.bondLevels.find { it.agentId == agent.id }
                        BondCard(
                            agent = agent,
                            bond = bond,
                            onInteract = { viewModel.interact(agent.id, 3) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBondCard(agent: LDOAgentEntity, bond: LDOBondLevelEntity) {
    val agentColor = Color(agent.colorHex)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = agentColor.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, agentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "HIGHEST BOND",
                color = agentColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
            Text(
                agent.displayName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                bond.bondTitle,
                color = agentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Bond Lv.${bond.bondLevel} · ${bond.interactionCount} interactions",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun BondCard(
    agent: LDOAgentEntity,
    bond: LDOBondLevelEntity?,
    onInteract: () -> Unit
) {
    val agentColor = Color(agent.colorHex)
    val bondProgress = if (bond != null && bond.maxBondPoints > 0)
        bond.bondPoints.toFloat() / bond.maxBondPoints else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            agent.displayName,
                            color = agentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Lv.${bond?.bondLevel ?: 0}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        bond?.bondTitle ?: "Stranger",
                        color = agentColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onInteract,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = agentColor.copy(alpha = 0.2f)
                    )
                ) {
                    Text("+3 BP", color = agentColor, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bond progress
            LinearProgressIndicator(
                progress = { bondProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = agentColor,
                trackColor = Color.White.copy(alpha = 0.08f)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${bond?.bondPoints ?: 0} / ${bond?.maxBondPoints ?: 100} BP",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
                Text(
                    "${bond?.interactionCount ?: 0} interactions",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
