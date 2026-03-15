package dev.aurakai.auraframefx.domains.kai.screens.security_shield

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import dev.aurakai.auraframefx.domains.kai.viewmodels.KaiSystemViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityStatusCard(
    title: String,
    status: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SecurityLogEntry(
    time: String,
    message: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "[$time]",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Cyan,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )
    }
}

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
                    ThreatLevel.NONE -> Color(0xFF00FFD4)
                    ThreatLevel.INFO -> Color.Cyan
                    ThreatLevel.LOW -> Color.Yellow
                    ThreatLevel.MEDIUM -> Color(0xFFFF8C00)
                    ThreatLevel.WARNING -> Color(0xFFFF8C00)
                    ThreatLevel.HIGH, ThreatLevel.CRITICAL -> Color(0xFFFF4444)
                    ThreatLevel.AI_ERROR -> Color.Magenta
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
fun SecurityCenterScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "firewall",
            title = "Global Firewall",
            subtitle = "Manage app traffic rules",
            icon = Icons.Filled.Security,
            route = "action_firewall",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "selinux",
            title = "SELinux Status",
            subtitle = "Enforcing mode controls",
            icon = Icons.Filled.AdminPanelSettings,
            route = "action_selinux",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "app_ops",
            title = "AppOps Manager",
            subtitle = "Deep permission granulars",
            icon = Icons.Filled.Gavel,
            route = "action_app_ops",
            accentColor = Color(0xFFFF6B00)
        ),
        GridMenuItem(
            id = "malware_scan",
            title = "Threat Scan",
            subtitle = "Analyze heuristics",
            icon = Icons.Filled.Warning,
            route = "action_scan",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "SECURITY CENTER",
        subtitle = "SENTINEL PROTOCOLS",
        menuItems = menuItems,
        onItemClick = { /* TODO */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_security_firewall,
        accentColor = Color(0xFF00FF85)
    )
}
