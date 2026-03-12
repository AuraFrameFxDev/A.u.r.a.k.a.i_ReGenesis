package dev.aurakai.auraframefx.domains.kai.screens.security_shield

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import dev.aurakai.auraframefx.domains.kai.viewmodels.KaiSystemViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SecurityCenterScreen — Kai Domain Command Center.
 * State comes from KaiSystemViewModel (real root/bootloader/threat data).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: KaiSystemViewModel = hiltViewModel()
) {
    val state by viewModel.systemStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Sentinel Security",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "KAI DEFENSE PROTOCOL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Cyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // -- SYSTEM STATUS --
            item {
                Text(
                    "INTEGRITY STATUS",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            item {
                SecurityStatusCard(
                    title = "Root Authority",
                    status = if (state.hasRoot) "AUTHORIZED" else "DENIED",
                    statusColor = if (state.hasRoot) Color(0xFF00FFD4) else Color(0xFFFF4444),
                    icon = Icons.Default.FlashOn,
                    description = if (state.hasRoot)
                        "Superuser binaries detected and active."
                    else
                        "Root access not granted. Some features unavailable."
                )
            }

            item {
                SecurityStatusCard(
                    title = "Shizuku Bridge",
                    status = if (state.isShizukuAvailable) "ACTIVE" else "OFFLINE",
                    statusColor = if (state.isShizukuAvailable) Color.Cyan else Color(0xFFFF8C00),
                    icon = Icons.Default.Security,
                    description = if (state.isShizukuAvailable)
                        "Shizuku service is running and responsive."
                    else
                        "Shizuku not available. Start Shizuku app or use ADB."
                )
            }

            item {
                val threatColor = when (state.threatLevel) {
                    ThreatLevel.NONE, ThreatLevel.INFO -> Color(0xFF00FFD4)
                    ThreatLevel.LOW -> Color.Yellow
                    ThreatLevel.MEDIUM, ThreatLevel.WARNING -> Color(0xFFFF8C00)
                    ThreatLevel.HIGH, ThreatLevel.CRITICAL, ThreatLevel.AI_ERROR -> Color(0xFFFF4444)
                }
                SecurityStatusCard(
                    title = "Threat Level",
                    status = "${state.threatLevel.name}  (${state.detectedThreats} detected)",
                    statusColor = threatColor,
                    icon = Icons.Default.Power,
                    description = if (state.detectedThreats == 0)
                        "No active threats. System clean."
                    else
                        "Last scan: ${formatScanTime(state.lastScanTime)}"
                )
            }

            // -- QUICK ACTIONS --
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "QUICK DEFENSE",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.softReboot() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF333333),
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Soft Reboot", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.killGhosts() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF333333),
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Kill Ghosts", fontSize = 12.sp)
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.triggerSecurityScan() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF001F3F),
                        contentColor = Color.Cyan
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Run Security Scan", fontSize = 12.sp)
                }
            }

            // -- EVENT LOG --
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "SECURITY EVENT LOG",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.isLoading) {
                            SecurityLogEntry("—", "Scanning system…")
                        } else {
                            SecurityLogEntry(
                                formatScanTime(state.lastScanTime),
                                "Security scan completed — ${state.detectedThreats} threat(s) found."
                            )
                            SecurityLogEntry(
                                "—",
                                "Root: ${if (state.hasRoot) "GRANTED" else "DENIED"} | Shizuku: ${if (state.isShizukuAvailable) "UP" else "DOWN"}"
                            )
                            SecurityLogEntry(
                                "—",
                                "Bootloader: ${if (state.bootloaderUnlocked) "UNLOCKED" else "LOCKED"} | Verified: ${state.verifiedBootState}"
                            )
                            SecurityLogEntry(
                                "—",
                                "Developer Options: ${if (state.developerOptionsEnabled) "ENABLED" else "DISABLED"}"
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatScanTime(epochMs: Long): String {
    if (epochMs == 0L) return "never"
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(epochMs))
}

@Composable
private fun SecurityStatusCard(
    title: String,
    status: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = statusColor, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(status, color = statusColor, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(4.dp))
                Text(description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SecurityLogEntry(time: String, message: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            time,
            color = Color.Cyan.copy(0.6f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(64.dp)
        )
        Text(message, color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
    }
}
