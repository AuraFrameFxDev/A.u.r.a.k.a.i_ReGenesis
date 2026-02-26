package dev.aurakai.auraframefx.ui.screens.manual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.customization.CustomizationViewModel
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 🔮 ORACLE DRIVE - MANUAL CONTROL
 * Ported from Iconify-beta.
 * Deep SystemUI customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleDriveManualScreen(
    onNavigateBack: () -> Unit,
    viewModel: CustomizationViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val config = state.systemUIConfig

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ORACLE DRIVE",
                        fontFamily = LEDFontFamily,
                        letterSpacing = 2.sp,
                        color = NeonPurple
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
                    containerColor = Color.Black.copy(alpha = 0.9f),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    "SYSTEMUI LAYERS (ICONIFY PORT)",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Lockscreen Clock Style
            item {
                Text("Lockscreen Clock Style", color = Color.White, fontSize = 16.sp)
                Slider(
                    value = config.lockscreenClockStyle.toFloat(),
                    onValueChange = {
                        viewModel.updateSystemUIConfig(
                            context,
                            config.copy(lockscreenClockStyle = it.toInt())
                        )
                    },
                    valueRange = 0f..60f,
                    steps = 60,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonPurple,
                        activeTrackColor = NeonPurple
                    )
                )
                Text(
                    "Style ID: ${config.lockscreenClockStyle}",
                    color = NeonPurple,
                    fontSize = 12.sp
                )
            }

            // Battery Style
            item {
                Text("Battery Icon Style", color = Color.White, fontSize = 16.sp)
                Slider(
                    value = config.batteryStyle.toFloat(),
                    onValueChange = {
                        viewModel.updateSystemUIConfig(
                            context,
                            config.copy(batteryStyle = it.toInt())
                        )
                    },
                    valueRange = 0f..39f, // Based on Iconify constants
                    steps = 39,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonPurple,
                        activeTrackColor = NeonPurple
                    )
                )
                Text("Style ID: ${config.batteryStyle}", color = NeonPurple, fontSize = 12.sp)
            }

            // QS Transparency
            item {
                ManualSlider(
                    label = "Quick Settings Transparency",
                    value = config.qsTransparency * 100f,
                    range = 0f..100f,
                    accentColor = NeonPurple,
                    onValueChange = {
                        viewModel.updateSystemUIConfig(
                            context,
                            config.copy(qsTransparency = it / 100f)
                        )
                    }
                )
            }

            // Blur Radius
            item {
                ManualSlider(
                    label = "System Blur Radius",
                    value = config.blurRadius.toFloat(),
                    range = 0f..100f,
                    accentColor = NeonPurple,
                    onValueChange = {
                        viewModel.updateSystemUIConfig(
                            context,
                            config.copy(blurRadius = it.toInt())
                        )
                    }
                )
            }

            // Navigation Pill
            item {
                ManualSwitch(
                    label = "Hide Navigation Pill",
                    checked = config.hidePill,
                    accentColor = NeonPurple,
                    onCheckedChange = {
                        viewModel.updateSystemUIConfig(
                            context,
                            config.copy(hidePill = it)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ManualSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 16.sp)
            Text("${value.toInt()}", color = accentColor, fontSize = 14.sp)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun ManualSwitch(
    label: String,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
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

