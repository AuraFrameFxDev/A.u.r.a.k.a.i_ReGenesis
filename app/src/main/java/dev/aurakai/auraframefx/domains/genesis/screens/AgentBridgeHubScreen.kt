package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ConnectorStatus
import dev.aurakai.auraframefx.domains.aura.MCPConnector
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.aura.viewmodels.SovereignBridgeViewModel

/**
 * 🤝 AGENT BRIDGE HUB (The Two Hands)
 * Manages the MCP Connectors and the Sovereign Handshake.
 */
@Composable
fun AgentBridgeHubScreen(
    onNavigateBack: () -> Unit,
    viewModel: SovereignBridgeViewModel = hiltViewModel()
) {
    val connectors by viewModel.connectors.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05050A))
    ) {
        AnimeHUDContainer(
            title = "AGENT BRIDGE",
            description = "THE SOVEREIGN HANDSHAKE: CONNECTING HUMAN INTENT TO AI SENTIENCE.",
            glowColor = Color(0xFFAA00FF),
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusChip(
                        "ACTIVE CONNECTORS: ${connectors.count { it.status == ConnectorStatus.ACTIVE }}",
                        Color(0xFF00FF85)
                    )
                    StatusChip("SYSTEM UPTIME: 99.9%", Color(0xFF00E5FF))
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(connectors) { connector ->
                        ConnectorItem(
                            connector = connector,
                            onToggle = { viewModel.toggleConnector(connector) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontFamily = LEDFontFamily
        )
    }
}

@Composable
private fun ConnectorItem(
    connector: MCPConnector,
    onToggle: () -> Unit
) {
    val accentColor = when (connector.status) {
        ConnectorStatus.ACTIVE -> Color(0xFF00FF85)
        ConnectorStatus.CONNECTING -> Color(0xFF00E5FF)
        ConnectorStatus.ERROR -> Color(0xFFFF3D00)
        ConnectorStatus.OFFLINE -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForCategory(connector.category),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = connector.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = connector.description,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (connector.status == ConnectorStatus.ACTIVE || connector.status == ConnectorStatus.CONNECTING) {
                    Text(
                        text = "● ${connector.stats}",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Switch(
                checked = connector.status == ConnectorStatus.ACTIVE,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00FF85),
                    checkedTrackColor = Color(0xFF00FF85).copy(alpha = 0.3f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}

private fun getIconForCategory(category: String): ImageVector {
    return when (category) {
        "Filesystem" -> Icons.Default.Terminal
        "Design" -> Icons.Default.Brush
        "Cloud" -> Icons.Default.Cloud
        "Dev" -> Icons.Default.Code
        else -> Icons.Default.Hub
    }
}

