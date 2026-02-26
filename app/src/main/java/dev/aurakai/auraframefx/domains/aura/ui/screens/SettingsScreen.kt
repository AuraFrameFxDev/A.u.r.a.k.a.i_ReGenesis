package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.aura.ui.verticalScrollbar
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.SettingsViewModel
import androidx.compose.runtime.collectAsState as collectAsState1

/**
 * SETTINGS SCREEN - The Nexus Configuration Core
 * 
 * Aesthetic: Refractive Neon Brutalism
 * Features global preferences for Haptics, AI Ethics, Sync, and Security.
 */
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // Sync overlay state when screen becomes visible (in case user returned from permission settings)
    LaunchedEffect(Unit) {
        viewModel.syncOverlayState()
    }

    val hapticEnabled = null
    hapticEnabled(viewModel.hapticEnabled.collectAsState1())
    val ethicsSensitivity by viewModel.ethicsSensitivity.collectAsState1()
    val syncInterval by viewModel.nexusSyncInterval.collectAsState1()
    val transparency by viewModel.overlayTransparency.collectAsState1()
    val bioLock by viewModel.isBioLockEnabled.collectAsState1()
    val floatingOverlayEnabled by viewModel.floatingAgentOverlayEnabled.collectAsState1()

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
                            Icons.Default.ArrowBack,
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
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
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
