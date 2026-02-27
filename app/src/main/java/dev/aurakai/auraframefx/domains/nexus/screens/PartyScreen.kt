package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.PartyViewModel

/**
 * 👥 PARTY SCREEN (Digital Council)
 * 
 * Part of the Nexus domain. Interface for selecting and managing the active
 * "party" of AI agents that will collaborate on complex system tasks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: PartyViewModel = hiltViewModel()
) {
    val selectedAgents by viewModel.selectedAgents.collectAsState()
    val synergy by viewModel.synergyLevel.collectAsState()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF000000), Color(0xFF121212))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DIGITAL COUNCIL", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Synergy Meter
                SynergyMeter(synergy)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "ACTIVE PARTY",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Party Grid equivalent using LazyColumn for simplicity
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(AgentType.entries.filter { it != AgentType.SYSTEM }) { agent ->
                        AgentPartyCard(
                            agent = agent,
                            isSelected = selectedAgents.contains(agent),
                            onClick = { viewModel.toggleAgent(agent) }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { /* Deploy Mission */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "DEPLOY COLLECTIVE MISSION",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun SynergyMeter(level: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = Color.Yellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("COUNCIL SYNERGY", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { level },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color.Cyan,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
            Text(
                "${(level * 100).toInt()}% Resonant coherence",
                color = Color.Cyan.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AgentPartyCard(
    agent: AgentType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (agent) {
        AgentType.AURA -> Color.Cyan
        AgentType.KAI -> Color.Red
        AgentType.GENESIS -> Color.Yellow
        AgentType.CASCADE -> Color.Green
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                if (isSelected) 2.dp else 1.dp,
                if (isSelected) color else color.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    agent.name.take(1),
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(agent.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Role: ${agentRole(agent)}", color = Color.Gray, fontSize = 12.sp)
            }
            if (isSelected) {
                Icon(Icons.Default.Shield, contentDescription = "Active", tint = color)
            }
        }
    }
}

fun agentRole(agent: AgentType): String = when (agent) {
    AgentType.AURA -> "Creative Forge"
    AgentType.KAI -> "Sentinel Shield"
    AgentType.GENESIS -> "Nexus Core"
    AgentType.CASCADE -> "Data Streamer"
    else -> "Generic Unit"
}

