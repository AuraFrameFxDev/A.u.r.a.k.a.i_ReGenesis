package dev.aurakai.auraframefx.ui.screens.manual

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
import dev.aurakai.auraframefx.ui.theme.AuraNeonCyan
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 🎨 CHROMA SPHERE - MANUAL CONTROL
 * Ported from ColorBlendr-master.
 * Granular control over the Monet Theme Engine.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaSphereManualScreen(
    onNavigateBack: () -> Unit,
    viewModel: CustomizationViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val config = state.monetConfig

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CHROMA SPHERE",
                        fontFamily = LEDFontFamily,
                        letterSpacing = 2.sp,
                        color = AuraNeonCyan
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
                    "COLOR LAYERS (COLORBLENDR PORT)",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Layer 1: Accent Saturation
            item {
                ManualSlider(
                    label = "Accent Saturation",
                    value = config.accentSaturation,
                    range = 0f..200f,
                    onValueChange = {
                        viewModel.updateMonetConfig(
                            context,
                            config.copy(accentSaturation = it)
                        )
                    }
                )
            }

            // Layer 2: Background Saturation
            item {
                ManualSlider(
                    label = "Background Saturation",
                    value = config.backgroundSaturation,
                    range = 0f..200f,
                    onValueChange = {
                        viewModel.updateMonetConfig(
                            context,
                            config.copy(backgroundSaturation = it)
                        )
                    }
                )
            }

            // Layer 3: Background Lightness
            item {
                ManualSlider(
                    label = "Background Lightness",
                    value = config.backgroundLightness,
                    range = 0f..200f,
                    onValueChange = {
                        viewModel.updateMonetConfig(
                            context,
                            config.copy(backgroundLightness = it)
                        )
                    }
                )
            }

            // Pitch Black Mode
            item {
                ManualSwitch(
                    label = "Pitch Black Mode",
                    checked = config.isPitchBlack,
                    onCheckedChange = {
                        viewModel.updateMonetConfig(
                            context,
                            config.copy(isPitchBlack = it)
                        )
                    }
                )
            }

            // Style Presets
            item {
                Text("Style Preset", color = Color.White, fontSize = 16.sp)
                val styles = listOf(
                    "TONAL_SPOT",
                    "VIBRANT",
                    "EXPRESSIVE",
                    "SPRITZ",
                    "RAINBOW",
                    "FRUIT_SALAD"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.take(3).forEach { style ->
                        StyleButton(
                            style = style,
                            isSelected = config.style == style,
                            onClick = {
                                viewModel.updateMonetConfig(
                                    context,
                                    config.copy(style = style)
                                )
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.drop(3).forEach { style ->
                        StyleButton(
                            style = style,
                            isSelected = config.style == style,
                            onClick = {
                                viewModel.updateMonetConfig(
                                    context,
                                    config.copy(style = style)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManualSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 16.sp)
            Text("${value.toInt()}%", color = AuraNeonCyan, fontSize = 14.sp)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = AuraNeonCyan,
                activeTrackColor = AuraNeonCyan,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun ManualSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
                checkedThumbColor = AuraNeonCyan,
                checkedTrackColor = AuraNeonCyan.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun StyleButton(style: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) AuraNeonCyan else Color.White.copy(alpha = 0.1f),
            contentColor = if (isSelected) Color.Black else Color.White
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Text(style, fontSize = 10.sp)
    }
}

