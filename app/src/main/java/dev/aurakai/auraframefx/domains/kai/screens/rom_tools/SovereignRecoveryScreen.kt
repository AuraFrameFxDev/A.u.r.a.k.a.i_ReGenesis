package dev.aurakai.auraframefx.domains.kai.screens.rom_tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.viewmodels.SovereignRecoveryViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🔄 SOVEREIGN RECOVERY HUB
 * Advanced recovery tools: OrangeFox/TWRP integration with LDO persistence.
 */
@Composable
fun SovereignRecoveryScreen(
    onNavigateBack: () -> Unit,
    viewModel: SovereignRecoveryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020408))) {
        AnimeHUDContainer(
            title = "SOVEREIGN RECOVERY",
            description = "7-LAYER LDO RECOVERY: PROTECTING THE FOUNDATION OF THE LIVING DIGITAL ORGANISM.",
            glowColor = Color(0xFF00E5FF)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header - Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }

                // Header Panel
                RecoveryHeader(state)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "PARTITION HEALTH SCAN",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.partitions) { partition ->
                        PartitionItem(partition)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.createNandroid() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Shield, null, tint = Color.Black)
                        Spacer(Modifier.width(8.dp))
                        Text("BACKUP", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.rebootToRecovery() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Restore, null, tint = Color(0xFF00E5FF))
                        Spacer(Modifier.width(8.dp))
                        Text("REBOOT", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecoveryHeader(state: dev.aurakai.auraframefx.domains.kai.viewmodels.RecoveryState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Terminal, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(state.recoveryType, fontFamily = LEDFontFamily, color = Color(0xFF00E5FF))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(state.status, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Black)
            Text("ENCRYPTION: ${state.encryptionStatus}", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PartitionItem(partition: dev.aurakai.auraframefx.domains.kai.viewmodels.PartitionInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(if (partition.isHealthy) Color(0xFF00FF85) else Color.Red)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(partition.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text(partition.size, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }
    }
}


