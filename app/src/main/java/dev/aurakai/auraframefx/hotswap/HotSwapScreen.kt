package dev.aurakai.auraframefx.hotswap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/**
 * HotSwap Screen - Runtime Configuration Editor
 *
 * Enables root-level editing of:
 * - Gate configurations (add/remove/reorder gates)
 * - Asset bundles (swap themes, icons, resources)
 * - Navigation structure
 * - Quick access shortcuts
 *
 * All changes take effect immediately and persist across restarts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotSwapScreen(
    navController: NavController,
    viewModel: HotSwapViewModel = hiltViewModel()
) {
    val gateConfigs by viewModel.gateConfigs.collectAsState()
    val assetBundles by viewModel.assetBundles.collectAsState()
    val currentConfig by viewModel.currentConfig.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("GATES", "ASSETS", "SHORTCUTS", "IMPORT/EXPORT")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "⚡ HOT SWAP CONTROL",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                        Text(
                            "Runtime Configuration Editor",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFF8C00).copy(alpha = 0.8f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetToDefaults() }) {
                        Icon(Icons.Default.Refresh, "Reset", tint = Color(0xFFFF4500))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color(0xFFFF8C00)
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0A0A0A),
                contentColor = Color(0xFFFF8C00)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> GatesTab(gateConfigs, viewModel)
                1 -> AssetsTab(assetBundles, currentConfig, viewModel)
                2 -> ShortcutsTab(viewModel)
                3 -> ImportExportTab(viewModel)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// GATES TAB - Add/Remove/Reorder Gates
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun GatesTab(
    gateConfigs: List<GateConfig>,
    viewModel: HotSwapViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "GATE CONFIGURATION (${gateConfigs.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFFFF8C00),
                    fontWeight = FontWeight.Bold
                )
            )
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Icon(Icons.Default.Add, "Add Gate", tint = Color(0xFF00FF00))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD GATE", color = Color(0xFF00FF00))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gate List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(gateConfigs) { index, gate ->
                GateConfigCard(gate, index, gateConfigs.size, viewModel)
            }
        }
    }

    // Add Gate Dialog
    if (showAddDialog) {
        AddGateDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newGate ->
                viewModel.addCustomGate(newGate)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GateConfigCard(
    gate: GateConfig,
    index: Int,
    totalGates: Int,
    viewModel: HotSwapViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(gate.color))
    } catch (e: Exception) {
        Color(0xFFFFFFFF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Color indicator
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            gate.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = backgroundColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Route: ${gate.route}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }

                // Actions
                Row {
                    // Move Up
                    if (index > 0) {
                        IconButton(onClick = { viewModel.moveGate(index, index - 1) }) {
                            Icon(Icons.Default.KeyboardArrowUp, "Move Up", tint = Color.Gray)
                        }
                    }
                    // Move Down
                    if (index < totalGates - 1) {
                        IconButton(onClick = { viewModel.moveGate(index, index + 1) }) {
                            Icon(Icons.Default.KeyboardArrowDown, "Move Down", tint = Color.Gray)
                        }
                    }
                    // Toggle
                    Switch(
                        checked = gate.enabled,
                        onCheckedChange = { viewModel.toggleGateVisibility(gate.id) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00FF00),
                            checkedTrackColor = Color(0xFF00FF00).copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Expanded details
            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "GATE DETAILS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFF8C00),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("ID", gate.id)
                    DetailRow("Order", gate.order.toString())
                    DetailRow("Icon", gate.icon)
                    DetailRow("Custom Items", gate.customItems.size.toString())

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.removeGate(gate.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF4500).copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFFF4500))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DELETE GATE", color = Color(0xFFFF4500))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White
            )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// ASSETS TAB - Swap Themes/Icons/Resources
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AssetsTab(
    assetBundles: List<AssetBundle>,
    currentConfig: HotSwapConfig,
    viewModel: HotSwapViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "ASSET BUNDLES",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFFFF8C00),
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            "Active: ${currentConfig.activeAssetBundleId}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assetBundles) { bundle ->
                AssetBundleCard(bundle, currentConfig.activeAssetBundleId, viewModel)
            }
        }
    }
}

@Composable
private fun AssetBundleCard(
    bundle: AssetBundle,
    activeId: String,
    viewModel: HotSwapViewModel
) {
    val isActive = bundle.id == activeId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isActive) viewModel.swapAssetBundle(bundle.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF2A2A2A) else Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        bundle.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (isActive) Color(0xFF00FF00) else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF00FF00),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "ACTIVE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                Text(
                    bundle.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
                Text(
                    "Type: ${bundle.type} • Assets: ${bundle.assets.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )
            }

            if (!isActive) {
                Icon(
                    Icons.Default.SwapHoriz,
                    "Swap to this bundle",
                    tint = Color(0xFFFF8C00)
                )
            } else {
                Icon(
                    Icons.Default.CheckCircle,
                    "Active",
                    tint = Color(0xFF00FF00)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// SHORTCUTS TAB & IMPORT/EXPORT TAB (Simplified)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun ShortcutsTab(viewModel: HotSwapViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "QUICK ACCESS SHORTCUTS",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFFFF8C00),
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            "Configure floating quick access panel",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "⚙️ Shortcut customization coming soon...",
            color = Color.Gray
        )
    }
}

@Composable
private fun ImportExportTab(viewModel: HotSwapViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "IMPORT / EXPORT",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFFFF8C00),
                fontWeight = FontWeight.Bold
            )
        )

        Button(
            onClick = { viewModel.exportConfiguration() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Icon(Icons.Default.FileDownload, "Export", tint = Color(0xFF00FF00))
            Spacer(modifier = Modifier.width(8.dp))
            Text("EXPORT CONFIGURATION", color = Color(0xFF00FF00))
        }

        Button(
            onClick = { /* Import */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Icon(Icons.Default.FileUpload, "Import", tint = Color(0xFFFF8C00))
            Spacer(modifier = Modifier.width(8.dp))
            Text("IMPORT CONFIGURATION", color = Color(0xFFFF8C00))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// ADD GATE DIALOG
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AddGateDialog(
    onDismiss: () -> Unit,
    onConfirm: (GateConfig) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("#FFFFFF") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Gate") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = route, onValueChange = { route = it }, label = { Text("Route") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color (Hex)") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && route.isNotBlank()) {
                    onConfirm(
                        GateConfig(
                            id = route,
                            name = name,
                            route = route,
                            color = color
                        )
                    )
                }
            }) {
                Text("ADD")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
