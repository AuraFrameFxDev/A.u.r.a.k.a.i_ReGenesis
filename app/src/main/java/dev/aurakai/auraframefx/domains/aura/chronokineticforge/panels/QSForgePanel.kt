package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.RealitymorphismViewModel

/**
 * ⚙️ QUICK SETTINGS FORGE PANEL
 *
 * Header images, padding, margin, expand, stretch, and all QS customizations.
 */

@Composable
fun QSForgePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val qsConfig = uiState.qsHeaderConfig

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Text(
            "⚙️ QUICK SETTINGS FORGE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Drag-drop QS panel customization with header images",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // QS Preview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // QS Header Image Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(qsConfig.height.dp)
                        .clip(RoundedCornerShape(qsConfig.cornerRadius.dp))
                        .background(Color(0xFF2A2A2A)),
                    contentAlignment = Alignment.Center
                ) {
                    if (qsConfig.imagePath != null) {
                        // Would show actual image here
                        Text(
                            "🖼️ ${qsConfig.imagePath?.substringAfterLast("/")}",
                            color = Color.White
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color(0xFFFF00FF).copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Drop PNG header image here",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // QS Toggles Mock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (index < 2) Color(0xFFFF00FF).copy(alpha = 0.3f)
                                    else Color(0xFF2A2A2A)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when (index) {
                                    0 -> Icons.Default.Wifi
                                    1 -> Icons.Default.Bluetooth
                                    2 -> Icons.Default.FlashlightOn
                                    else -> Icons.Default.DoNotDisturb
                                },
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Controls
        Text(
            "HEADER IMAGE",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* Pick image */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF))
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SELECT PNG")
            }

            OutlinedButton(
                onClick = { /* Camera */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF))
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CAMERA")
            }

            OutlinedButton(
                onClick = { /* AI Generate */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF00FF)),
                border = BorderStroke(1.dp, Color(0xFFFF00FF))
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI GEN")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sliders
        ForgeSlider(
            label = "Padding",
            value = qsConfig.padding,
            onValueChange = viewModel::setQSHeaderPadding,
            valueRange = 0f..32f
        )

        ForgeSlider(
            label = "Corner Radius",
            value = qsConfig.cornerRadius,
            onValueChange = viewModel::setQSHeaderCornerRadius,
            valueRange = 0f..32f
        )

        ForgeSlider(
            label = "Height",
            value = qsConfig.height,
            onValueChange = viewModel::setQSHeaderHeight,
            valueRange = 80f..200f
        )

        ForgeSlider(
            label = "Blur",
            value = qsConfig.blur,
            onValueChange = viewModel::setQSHeaderBlur,
            valueRange = 0f..20f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stretch Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Stretch to Fit", color = Color.White)
            Switch(
                checked = qsConfig.stretch,
                onCheckedChange = { /* Toggle */ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFF00FF),
                    checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun ForgeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()}dp", color = Color(0xFFFF00FF))
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF00FF),
                activeTrackColor = Color(0xFFFF00FF),
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
