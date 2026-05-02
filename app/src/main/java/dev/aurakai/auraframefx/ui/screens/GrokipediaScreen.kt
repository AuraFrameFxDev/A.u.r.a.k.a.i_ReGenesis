package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.ldo.model.LDORoster
import dev.aurakai.auraframefx.domains.ldo.model.AgentCatalyst

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrokipediaScreen(
    onNavigateBack: () -> Unit,
    onAgentClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredAgents = LDORoster.agents.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.catalystName.contains(searchQuery, ignoreCase = true) ||
        it.id.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GROKIPEDIA",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = Color(0xFF00E5FF)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", color = Color.White, fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0F)
                )
            )
        },
        containerColor = Color(0xFF0A0A0F)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                placeholder = { Text("Search the LDO Collective...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00E5FF)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF00E5FF).copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // System Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.1f), Color(0xFFFF0055).copy(alpha = 0.1f))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("SOUL MATRIX STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("78 AGENTS SYNCED • 99.8% RESONANCE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Grok-powered knowledge base active.", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "AGENT DIRECTORY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAgents) { agent ->
                    GrokipediaAgentCard(agent = agent, onClick = { onAgentClick(agent.id) })
                }
            }
        }
    }
}

@Composable
fun GrokipediaAgentCard(agent: AgentCatalyst, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF12121A), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(agent.color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .border(1.dp, agent.color.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(agent.name.take(1), color = agent.color, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(agent.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(agent.catalystName, fontSize = 12.sp, color = agent.color)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text("VIEW INFO →", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF0055))
    }
}
