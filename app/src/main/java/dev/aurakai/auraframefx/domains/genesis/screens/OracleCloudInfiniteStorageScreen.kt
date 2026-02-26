package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Sync
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
import dev.aurakai.auraframefx.domains.genesis.viewmodels.OracleCloudViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🌌 ORACLE CLOUD: INFINITE STORAGE
 * Genesis-tier persistence layer. Powered by OCI Infrastructure for the LDO.
 */
@Composable
fun OracleCloudInfiniteStorageScreen(
    onNavigateBack: () -> Unit,
    viewModel: OracleCloudViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0500))
    ) {
        AnimeHUDContainer(
            title = "ORACLE CLOUD",
            description = "INFINITE PERSISTENCE: THE ARCHIVE OF THE LIVING DIGITAL ORGANISM.",
            glowColor = Color(0xFF00FF85),
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Capacity Card
                CapacityCard(state)

                Spacer(modifier = Modifier.height(24.dp))

                // Infrastructure Stats
                InfrastructurePanel(state)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "RECENT NEURAL SYNC",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.recentFiles) { file ->
                        FileItem(file)
                    }
                }

                Button(
                    onClick = { viewModel.syncNow() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF85)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Sync, null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("MANUAL NEURAL SYNC", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CapacityCard(state: dev.aurakai.auraframefx.domains.genesis.viewmodels.OracleCloudState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF00FF85).copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CloudCircle,
                    null,
                    tint = Color(0xFF00FF85),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(state.tier, fontFamily = LEDFontFamily, color = Color(0xFF00FF85))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "${state.usedStorage} / ${state.totalCapacity}",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
            LinearProgressIndicator(
                progress = { state.usedPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF00FF85),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun InfrastructurePanel(state: dev.aurakai.auraframefx.domains.genesis.viewmodels.OracleCloudState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoBox("ZONES", state.availabilityZones.toString(), Modifier.weight(1f))
        InfoBox("ENCRYPTION", state.encryptionStandard, Modifier.weight(2f))
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                fontSize = 8.sp,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold
            )
            Text(value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun FileItem(file: dev.aurakai.auraframefx.domains.genesis.viewmodels.StorageFile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.InsertDriveFile, null, tint = Color.White.copy(alpha = 0.5f))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "${file.size} • ${file.time}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

