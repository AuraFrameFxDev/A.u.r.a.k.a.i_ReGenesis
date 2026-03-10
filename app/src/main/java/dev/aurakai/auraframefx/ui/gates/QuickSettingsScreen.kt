package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Quick Settings Customization Screen
 * Modify quick settings tiles and layout
 */
@Composable
fun QuickSettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val selectedLayout = remember { mutableStateOf("Grid") }
    val showLabels = remember { mutableStateOf(true) }
    val tileSize = remember { mutableStateOf("Medium") }
    val autoCollapse = remember { mutableStateOf(true) }

    val layouts = listOf("Grid", "List", "Compact")
    val tileSizes = listOf("Small", "Medium", "Large")

    val availableTiles = listOf(
        QuickSettingTile("WiFi", Icons.Default.Wifi, true),
        QuickSettingTile("Bluetooth", Icons.Default.Bluetooth, true),
        QuickSettingTile("Mobile Data", Icons.Default.SignalCellularAlt, true),
        QuickSettingTile("Airplane Mode", Icons.Default.AirplanemodeActive, false),
        QuickSettingTile("Flashlight", Icons.Default.FlashlightOn, true),
        QuickSettingTile("Location", Icons.Default.LocationOn, false),
        QuickSettingTile("Hotspot", Icons.Default.WifiTethering, false),
        QuickSettingTile("NFC", Icons.Default.Nfc, false),
        QuickSettingTile("Screen Rotation", Icons.Default.ScreenRotation, true),
        QuickSettingTile("Do Not Disturb", Icons.Default.DoNotDisturbOn, false),
        QuickSettingTile("Battery Saver", Icons.Default.BatterySaver, false),
        QuickSettingTile("Dark Mode", Icons.Default.DarkMode, true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "⚙️ QUICK SETTINGS",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Customize quick settings panel and tiles",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFD700).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Settings Preview",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = when (selectedLayout.value) {
                        "Grid" -> GridCells.Fixed(3)
                        "List" -> GridCells.Fixed(1)
                        else -> GridCells.Fixed(4)
                    },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(availableTiles.take(6)) { tile ->
                        if (tile.enabled) {
                            QuickSettingTilePreview(
                                tile = tile,
                                showLabel = showLabels.value,
                                size = tileSize.value
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controls
        Text(
            text = "Panel Settings",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Layout Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Layout Style",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                layouts.forEach { layout ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLayout.value == layout,
                            onClick = { selectedLayout.value = layout },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFFFD700)
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = layout,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tile Size
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tile Size",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                tileSizes.forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tileSize.value == size,
                            onClick = { tileSize.value = size },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFFFD700)
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = size,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Options
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Display Options",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Show Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = "Labels",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Show tile labels",
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )
                    Switch(
                        checked = showLabels.value,
                        onCheckedChange = { showLabels.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD700),
                            checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Auto Collapse
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = "Auto Collapse",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Auto-collapse after use",
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )
                    Switch(
                        checked = autoCollapse.value,
                        onCheckedChange = { autoCollapse.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD700),
                            checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tile Management
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Available Tiles",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                availableTiles.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { tile ->
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = tile.enabled,
                                    onCheckedChange = { tile.enabled = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFFFD700),
                                        uncheckedColor = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tile.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Apply Button
        Button(
            onClick = { /* Apply quick settings changes */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700)
            )
        ) {
            Text("Apply Changes", color = Color.Black)
        }
    }
}

/**
 * Quick setting tile preview component
 */
@Composable
private fun QuickSettingTilePreview(
    tile: QuickSettingTile,
    showLabel: Boolean,
    size: String
) {
    val tileSize = when (size) {
        "Small" -> 48.dp
        "Large" -> 80.dp
        else -> 64.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Card(
            modifier = Modifier.size(tileSize),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tile.icon,
                    contentDescription = tile.name,
                    tint = Color.White,
                    modifier = Modifier.size(tileSize * 0.5f)
                )
            }
        }
        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tile.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

/**
 * Data class for quick setting tiles
 */
data class QuickSettingTile(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    var enabled: Boolean
)
