package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.*
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*

/**
 * 🏠 HOME SCREEN FORGE PANEL
 *
 * Rotation settings, grid layout, transitions, icon sizing.
 */

@Composable
fun HomeScreenForgePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val homeConfig = uiState.homeScreenConfig

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "🏠 HOME SCREEN FORGE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Rotation, transitions, and launcher layout",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Rotation Settings
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ScreenRotation,
                        contentDescription = null,
                        tint = Color(0xFFFF00FF)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "SCREEN ROTATION",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(ScreenRotation.values().size) { index ->
                        val rotation = ScreenRotation.values()[index]
                        RotationChip(
                            rotation = rotation,
                            isSelected = homeConfig.rotation == rotation,
                            onClick = { viewModel.setHomeScreenRotation(rotation) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid Settings
        Text(
            "LAUNCHER GRID",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Columns: ", color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = homeConfig.gridColumns.toFloat(),
                onValueChange = { viewModel.setHomeScreenGridColumns(it.toInt()) },
                valueRange = 3f..6f,
                steps = 2,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFF00FF),
                    activeTrackColor = Color(0xFFFF00FF),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${homeConfig.gridColumns}",
                color = Color(0xFFFF00FF),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ForgeSlider(
            label = "Icon Size",
            value = homeConfig.iconSize,
            onValueChange = { viewModel.setHomeScreenIconSize(it) },
            valueRange = 40f..80f
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Transition Effects
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Animation,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "TRANSITION EFFECTS",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Home Screen Transition", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 150.dp)
                ) {
                    val effects = listOf(
                        "Slide" to TransitionForgeEffect.Slide(),
                        "Fade" to TransitionForgeEffect.Fade(),
                        "Zoom" to TransitionForgeEffect.Zoom(),
                        "Cube" to TransitionForgeEffect.Cube3D(),
                        "Glitch" to TransitionForgeEffect.Glitch(),
                        "Holo" to TransitionForgeEffect.Hologram(),
                        "CRT" to TransitionForgeEffect.CRT(),
                        "Spin" to TransitionForgeEffect.Spin()
                    )

                    items(effects.size) { index ->
                        val (name, effect) = effects[index]
                        TransitionChip(
                            label = name,
                            isSelected = homeConfig.transitionEffect::class == effect::class,
                            onClick = { viewModel.setHomeScreenTransition(effect) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Speed
                Text("Animation Speed", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fast", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = uiState.transitionConfig.globalSpeedMultiplier,
                        onValueChange = { viewModel.setTransitionSpeed(it) },
                        valueRange = 0.5f..2f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF00FF),
                            activeTrackColor = Color(0xFFFF00FF)
                        )
                    )
                    Text("Slow", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preview Button
        Button(
            onClick = { viewModel.previewChanges() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF)
            )
        ) {
            Icon(Icons.Default.Preview, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("PREVIEW ON HOME SCREEN")
        }
    }
}

@Composable
private fun RotationChip(
    rotation: ScreenRotation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val label = when (rotation) {
        ScreenRotation.PORTRAIT_ONLY -> "Portrait"
        ScreenRotation.LANDSCAPE_ONLY -> "Landscape"
        ScreenRotation.AUTO_ROTATE -> "Auto"
        ScreenRotation.FORCE_LANDSCAPE -> "Force Land"
    }

    val icon = when (rotation) {
        ScreenRotation.PORTRAIT_ONLY -> Icons.Default.StayCurrentPortrait
        ScreenRotation.LANDSCAPE_ONLY -> Icons.Default.StayCurrentLandscape
        ScreenRotation.AUTO_ROTATE -> Icons.Default.ScreenRotation
        ScreenRotation.FORCE_LANDSCAPE -> Icons.Default.ScreenLockLandscape
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFFFF00FF).copy(alpha = 0.3f) else Color(0xFF2A2A2A),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFFF00FF) else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color(0xFFFF00FF) else Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = if (isSelected) Color(0xFFFF00FF) else Color.White, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TransitionChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.3f) else Color(0xFF2A2A2A),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            color = if (isSelected) Color(0xFF00E5FF) else Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
