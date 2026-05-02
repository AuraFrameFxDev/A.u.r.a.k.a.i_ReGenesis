package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel

@Composable
fun LDORosterScreen(
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
                "AGENT ROSTER",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Text("Loading roster…", color = Color.White.copy(0.5f))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.agents) { agent ->
                        RosterCard(agent)
                    }
                }
            }
        }
    }
}

@Composable
private fun RosterCard(agent: LDOAgentEntity) {
    val agentColor = Color(agent.colorHex)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(agentColor.copy(0.2f))
            ) {
                Text(agent.displayName.take(1), modifier = Modifier.align(Alignment.Center), color = agentColor)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(agent.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                Text(agent.role, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
