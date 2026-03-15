package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.aurakai.auraframefx.domains.aura.viewmodels.AuraUIControlViewModel

// Static tile catalogue — what's available; enabled state comes from the ViewModel
private data class TileDefinition(val name: String, val icon: ImageVector)

private val TILE_CATALOGUE = listOf(
    TileDefinition("WiFi", Icons.Default.Wifi),
    TileDefinition("Bluetooth", Icons.Default.Bluetooth),
    TileDefinition("Mobile Data", Icons.Default.SignalCellularAlt),
    TileDefinition("Airplane Mode", Icons.Default.AirplanemodeActive),
    TileDefinition("Flashlight", Icons.Default.FlashlightOn),
    TileDefinition("Location", Icons.Default.LocationOn),
    TileDefinition("Hotspot", Icons.Default.WifiTethering),
    TileDefinition("NFC", Icons.Default.Nfc),
    TileDefinition("Screen Rotation", Icons.Default.ScreenRotation),
    TileDefinition("Do Not Disturb", Icons.Default.DoNotDisturbOn),
    TileDefinition("Battery Saver", Icons.Default.BatterySaver),
    TileDefinition("Dark Mode", Icons.Default.DarkMode),
    TileDefinition("Aura Overlay", Icons.Default.Face)
)

/**
 * Quick Settings Customization Screen — Aura in control.
 * All state persisted via AuraUIControlViewModel / DataStore.
 */
@Composable
fun QuickSettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AuraUIControlViewModel = hiltViewModel()
) {
    val state by viewModel.quickSettingsState.collectAsState()

    val layouts = listOf("Grid", "List", "Compact")
    val tileSizes = listOf("Small", "Medium", "Large")

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
            text = "Customize quick settings panel and tiles — Aura applies changes live",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFD700).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Settings Preview",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = when (state.layout) {
                        "Grid" -> GridCells.Fixed(3)
                        "List" -> GridCells.Fixed(1)
                        else -> GridCells.Fixed(4)
                    },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(TILE_CATALOGUE.filter { it.name in state.enabledTiles }.take(6)) { tile ->
                        QuickSettingTilePreview(
                            name = tile.name,
                            icon = tile.icon,
                            showLabel = state.showLabels,
                            size = state.tileSize
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                Text("Layout Style", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                layouts.forEach { layout ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.layout == layout,
                            onClick = { viewModel.setQsLayout(layout) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFD700))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(layout, style = MaterialTheme.typography.bodyMedium, color = Color.White)
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
                Text("Tile Size", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                tileSizes.forEach { size ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.tileSize == size,
                            onClick = { viewModel.setQsTileSize(size) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFD700))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(size, style = MaterialTheme.typography.bodyMedium, color = Color.White)
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
                Text("Display Options", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Label, "Labels", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Show tile labels", modifier = Modifier.weight(1f), color = Color.White)
                    Switch(
                        checked = state.showLabels,
                        onCheckedChange = { viewModel.setQsShowLabels(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD700),
                            checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExpandLess, "Auto Collapse", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Auto-collapse after use", modifier = Modifier.weight(1f), color = Color.White)
                    Switch(
                        checked = state.autoCollapse,
                        onCheckedChange = { viewModel.setQsAutoCollapse(it) },
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
                Text("Available Tiles", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                TILE_CATALOGUE.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { tile ->
                            Row(
                                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = tile.name in state.enabledTiles,
                                    onCheckedChange = { viewModel.toggleQsTile(tile.name) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFFFD700),
                                        uncheckedColor = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(tile.name, style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Changes are auto-persisted on every toggle
        Button(
            onClick = { /* Aura applies changes automatically on each toggle */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            enabled = false
        ) {
            Text("Changes Applied Automatically", color = Color.Black)
        }
    }
}

@Composable
private fun QuickSettingTilePreview(
    name: String,
    icon: ImageVector,
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
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, name, tint = Color.White, modifier = Modifier.size(tileSize * 0.5f))
            }
        }
        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
