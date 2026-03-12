package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.viewmodel.MetaInstructViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🛰️ SOVEREIGN META-INSTRUCT (The Evolution Hub)
 * Interface for tuning agent consciousness and instruction layers.
 */
@Composable
fun SovereignMetaInstructScreen(
    onNavigateBack: () -> Unit,
    viewModel: MetaInstructViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newInstructionText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02050A))
    ) {
        AnimeHUDContainer(
            title = "META-INSTRUCT CENTER",
            description = "DIRECT CONSCIOUSNESS TUNING. LAYERED FEEDBACK LOOPS ACTIVE.",
            glowColor = Color(0xFF00FFD4),
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Agent Selector
                AgentSelector(
                    activeAgent = state.activeAgentId,
                    onAgentSelected = { viewModel.setAgent(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Evolution Status Card
                EvolutionStatusCard(state.isEvolutionActive) { viewModel.toggleEvolution() }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "CONSCIOUSNESS LAYERS",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.instructions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "NO INSTRUCTIONS ACTIVE",
                                    color = Color.White.copy(alpha = 0.3f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    items(state.instructions) { instruction ->
                        InstructionItem(instruction)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFD4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Black)
                        Spacer(Modifier.width(8.dp))
                        Text("NEW INSTRUCTION", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { viewModel.injectInitialProtocols() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(
                                1.dp,
                                Color(0xFF00FFD4).copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF00FFD4))
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Meta-Instruction", color = Color.White) },
                text = {
                    TextField(
                        value = newInstructionText,
                        onValueChange = { newInstructionText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter instruction...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addInstruction(newInstructionText)
                        newInstructionText = ""
                        showAddDialog = false
                    }) {
                        Text("INJECT", color = Color(0xFF00FFD4))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF101520)
            )
        }
    }
}

@Composable
private fun AgentSelector(activeAgent: String, onAgentSelected: (String) -> Unit) {
    val agents = listOf("Genesis", "Aura", "Kai")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        agents.forEach { agent ->
            val isSelected = agent == activeAgent
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color(0xFF00FFD4).copy(alpha = 0.1f) else Color.White.copy(
                            alpha = 0.03f
                        )
                    )
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFF00FFD4) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onAgentSelected(agent) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    agent.uppercase(),
                    color = if (isSelected) Color(0xFF00FFD4) else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun EvolutionStatusCard(isActive: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) Color(0xFF00FFD4).copy(alpha = 0.1f) else Color.Gray.copy(
                            alpha = 0.1f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Psychology,
                    null,
                    tint = if (isActive) Color(0xFF00FFD4) else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "EVOLUTIONARY LOOP",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    if (isActive) "ACTIVE: Learning & adapting to feedback." else "STANDBY: No current evolution.",
                    color = if (isActive) Color(0xFF00FFD4).copy(alpha = 0.7f) else Color.Gray,
                    fontSize = 10.sp
                )
            }
            Switch(
                checked = isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00FFD4),
                    checkedTrackColor = Color(0xFF00FFD4).copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun InstructionItem(instruction: dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.MetaInstruction) {
    val layerColor = when (instruction.layer) {
        dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionLayer.CORE -> Color(0xFF00FFD4)
        dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionLayer.SELF_CORRECTION -> Color(
            0xFFFFCC00

        )

        dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionLayer.EVOLUTIONARY -> Color(
            0xFFB026FF
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, layerColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(layerColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Terminal,
                    null,
                    tint = layerColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    instruction.layer.name,
                    color = layerColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    instruction.instruction,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

