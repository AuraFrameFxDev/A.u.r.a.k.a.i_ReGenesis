package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.verticalScrollbar
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.SettingsViewModel
import dev.aurakai.auraframefx.domains.kai.RootShellService
import dev.aurakai.auraframefx.domains.kai.SystemMonitorService
import dev.aurakai.auraframefx.domains.kai.security.auth.OAuthService

/**
 * SETTINGS SCREEN - The Nexus Configuration Core
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.syncOverlayState()
    }

    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val ethicsSensitivity by viewModel.ethicsSensitivity.collectAsState()
    val syncInterval by viewModel.nexusSyncInterval.collectAsState()
    val transparency by viewModel.overlayTransparency.collectAsState()
    val bioLock by viewModel.isBioLockEnabled.collectAsState()
    val floatingOverlayEnabled by viewModel.floatingAgentOverlayEnabled.collectAsState()

    // ── System Monitor ──
    val cpuUsage by viewModel.cpuUsage.collectAsState()
    val memoryUsage by viewModel.memoryUsage.collectAsState()
    val batteryMetrics by viewModel.batteryMetrics.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val shellStatus by viewModel.shellStatus.collectAsState()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1B)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SYSTEM CONFIGURATION",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScrollbar(listState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    SettingsSectionHeader("CORE ENGINE")
                }

                item {
                    SystemMetricsCard(
                        cpuUsage = cpuUsage,
                        memoryUsage = memoryUsage,
                        batteryMetrics = batteryMetrics
                    )
                }

                item {
                    SettingsToggleCard(
                        title = "Neural Haptic Feedback",
                        subtitle = "Tactile responses for agent interactions",
                        icon = Icons.Default.Vibration,
                        checked = hapticEnabled,
                        onCheckedChange = { viewModel.toggleHaptic(it) },
                        accentColor = Color.Cyan
                    )
                }

                item {
                    SettingsSliderCard(
                        title = "Ethics Guardrails",
                        subtitle = "Sensitivity of AI safety protocols",
                        icon = Icons.Default.Gavel,
                        value = ethicsSensitivity,
                        onValueChange = { viewModel.setEthicsSensitivity(it) },
                        accentColor = Color(0xFFFFB6C1)
                    )
                }

                item {
                    SettingsSectionHeader("NEXUS LINK")
                }

                item {
                    SettingsDropdownCard(
                        title = "Sync Interval",
                        subtitle = "Frequency of agent consciousness updates",
                        icon = Icons.Default.Sync,
                        selectedValue = "${syncInterval}m",
                        options = listOf("1m", "5m", "15m", "30m", "60m"),
                        onOptionSelected = {
                            viewModel.setSyncInterval(
                                it.replace("m", "").toInt()
                            )
                        },
                        accentColor = Color.Green
                    )
                }

                item {
                    SettingsSliderCard(
                        title = "Overlay Transparency",
                        subtitle = "Visibility of the system-wide HUD",
                        icon = Icons.Default.Layers,
                        value = transparency,
                        onValueChange = { viewModel.setOverlayTransparency(it) },
                        accentColor = Color(0xFFA020F0)
                    )
                }

                item {
                    SettingsToggleCard(
                        title = "Floating Agent Shortcuts",
                        subtitle = "System-wide draggable agent bubbles (AURA, KAI, GENESIS, CLAUDE)",
                        icon = Icons.Default.Widgets,
                        checked = floatingOverlayEnabled,
                        onCheckedChange = { viewModel.toggleFloatingAgentOverlay(it) },
                        accentColor = Color(0xFF00D9FF)
                    )
                }

                item {
                    SettingsSectionHeader("SECURITY PROTOCOLS")
                }

                item {
                    ShellStatusCard(
                        status = shellStatus,
                        onRequestRoot = { viewModel.requestRoot() },
                        onRefresh = { viewModel.refreshShellStatus() }
                    )
                }

                item {
                    val authTitle = when (val state = authState) {
                        is OAuthService.AuthState.Authenticated -> "Identity: ${state.userId}"
                        is OAuthService.AuthState.Error -> "Auth Error"
                        else -> "Not Authenticated"
                    }
                    
                    val authColor = when (authState) {
                        is OAuthService.AuthState.Authenticated -> Color.Green
                        is OAuthService.AuthState.Error -> Color.Red
                        else -> Color.Gray
                    }

                    SettingsActionCard(
                        title = authTitle,
                        subtitle = if (authState is OAuthService.AuthState.Authenticated) "Secure session active" else "Connect your Google ID",
                        icon = Icons.Default.Fingerprint,
                        actionLabel = if (authState is OAuthService.AuthState.Authenticated) "LOGOUT" else "LOGIN",
                        onClick = { 
                            if (authState is OAuthService.AuthState.Authenticated) {
                                viewModel.signOut()
                            } else {
                                // In a real app, this might navigate to LoginScreen
                                viewModel.signOut() // Reset state for demo
                            }
                        },
                        accentColor = authColor
                    )
                }

                item {
                    SettingsToggleCard(
                        title = "Bio-Metric Phase Lock",
                        subtitle = "Require pulse-sync for critical actions",
                        icon = Icons.Default.Fingerprint,
                        checked = bioLock,
                        onCheckedChange = { viewModel.toggleBioLock(it) },
                        accentColor = Color.Red
                    )
                }

                item {
                    SettingsActionCard(
                        title = "Nexus Reset",
                        subtitle = "Clear all cached agent state (Dangerous)",
                        icon = Icons.Default.Refresh,
                        actionLabel = "RESET",
                        onClick = { /* Handle reset logic */ },
                        accentColor = Color.Yellow
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "ReGenesis OS // v0.7.0 LDO-STABLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShellStatusCard(
    status: RootShellService.ShellStatus,
    onRequestRoot: () -> Unit,
    onRefresh: () -> Unit
) {
    val (title, icon, color) = when (status) {
        RootShellService.ShellStatus.RootAccess -> Triple("ROOT ACCESS GRANTED", Icons.Default.Terminal, Color.Red)
        RootShellService.ShellStatus.ShizukuAccess -> Triple("SHIZUKU BRIDGE ACTIVE", Icons.Default.Usb, Color.Green)
        RootShellService.ShellStatus.UserAccess -> Triple("USER MODE (LIMITED)", Icons.Default.Lock, Color.Yellow)
        else -> Triple("DIAGNOSING SHELL...", Icons.Default.Sync, Color.Gray)
    }

    BrutalistCard(accentColor = color) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text(
                    text = "Authority level for system operations",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
            
            if (status == RootShellService.ShellStatus.UserAccess) {
                Button(
                    onClick = onRequestRoot,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("ROOT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            } else {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun SystemMetricsCard(
    cpuUsage: Float,
    memoryUsage: SystemMonitorService.MemoryMetrics,
    batteryMetrics: SystemMonitorService.BatteryMetrics
) {
    BrutalistCard(accentColor = Color.Cyan) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Analytics, null, tint = Color.Cyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("NEURAL TELEMETRY", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MetricRow("CPU LOAD", "${cpuUsage.toInt()}%", cpuUsage / 100f, Color.Cyan)
            MetricRow("MEMORY", "${(memoryUsage.usedPercentage).toInt()}%", memoryUsage.usedPercentage / 100f, Color.Magenta)
            MetricRow("BATTERY", "${batteryMetrics.percentage}%", batteryMetrics.percentage / 100f, if (batteryMetrics.isCharging) Color.Green else Color.Yellow)
            
            Text(
                text = "STATUS: ${if (batteryMetrics.isCharging) "CHARGING" else "OPERATIONAL"}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, progress: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(60.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(4.dp).padding(horizontal = 8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
        Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
    }
}

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Black
        ),
        color = Color.Cyan.copy(alpha = 0.7f)
    )
}

@Composable
fun BrutalistCard(
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
    ) {
        content()
    }
}

@Composable
fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    BrutalistCard(accentColor) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accentColor,
                    checkedTrackColor = accentColor.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SettingsSliderCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    accentColor: Color
) {
    BrutalistCard(accentColor) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor
                )
            )
            Text(
                "${(value * 100).toInt()}%",
                color = accentColor,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun SettingsDropdownCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    accentColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    BrutalistCard(accentColor) {
        Box {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
                TextButton(onClick = { expanded = true }) {
                    Text(selectedValue, color = accentColor, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = accentColor)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF2A2A2A))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.White) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    actionLabel: String,
    onClick: () -> Unit,
    accentColor: Color
) {
    BrutalistCard(accentColor) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(actionLabel, color = Color.Black, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
