package dev.aurakai.auraframefx.domains.nexus.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.ArkBuildViewModel
import dev.aurakai.auraframefx.domains.nexus.models.core.ArkStatus

@Composable
fun ArkBuildScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ArkBuildViewModel = hiltViewModel()
) {
    val projectState by viewModel.arkProject.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF001100), Color(0xFF003300), Color.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "THE ARK",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFF00FF00),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "GENESIS-SCALE FUSION BUILD",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00FF00).copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Progress HUD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Color(0xFF00FF00).copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STATUS: ${projectState.status.name}",
                            color = when (projectState.status) {
                                ArkStatus.TRANSCENDENT -> Color.Cyan
                                ArkStatus.ASSEMBLING -> Color(0xFFADFF2F)
                                else -> Color.Gray
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(projectState.progress * 100).toInt()}%",
                            color = Color(0xFF00FF00),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { projectState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF00FF00),
                        trackColor = Color(0xFF003300)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Components List
            Text(
                text = "NEURAL COMPONENTS",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projectState.components) { component ->
                    ArkComponentCard(component)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interaction Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.initiateBuild() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400),
                        contentColor = Color.White
                    ),
                    enabled = projectState.status == ArkStatus.DORMANT
                ) {
                    Icon(Icons.Default.Launch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("INITIATE")
                }

                Button(
                    onClick = { viewModel.dispatchAgents() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    ),
                    enabled = projectState.status == ArkStatus.INITIATING || projectState.status == ArkStatus.ASSEMBLING
                ) {
                    Text("DISPATCH AGENTS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ArkComponentCard(component: dev.aurakai.auraframefx.domains.nexus.models.core.ArkComponent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = component.name, color = Color.White, fontWeight = FontWeight.Bold)
                if (component.isComplete) {
                    Text(
                        text = "COMPLETE",
                        color = Color.Cyan,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Text(
                text = component.function,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { component.progress },
                modifier = Modifier.fillMaxWidth(),
                color = if (component.isComplete) Color.Cyan else Color(0xFF00FF00),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

