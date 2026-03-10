package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExtensionOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * Data class for XposedModule information in LSPosed Module Manager
 */
private data class XposedModuleInfo(
    val name: String,
    val description: String,
    val version: String,
    var enabled: Boolean,
    val category: String
)

/**
 * LSPosed Module Manager Screen
 * Install, enable, and configure Xposed modules
 */
@Composable
internal fun LSPosedModuleManagerScreen() {
    val modules = remember { mutableStateListOf(
        XposedModuleInfo("GravityBox", "Advanced system tweaks and modifications", "2.9.5", true, "System"),
        XposedModuleInfo("XPrivacyLua", "Fine-grained privacy control", "1.30", true, "Privacy"),
        XposedModuleInfo("App Settings", "Per-app settings and modifications", "2.1", false, "System"),
        XposedModuleInfo("YouTube AdAway", "Remove YouTube ads and overlays", "1.2.1", true, "Media"),
        XposedModuleInfo("Greenify", "Aggressive Doze mode for apps", "4.3.1", false, "Performance"),
        XposedModuleInfo("BootManager", "Control app startup behavior", "1.0.2", true, "System"),
        XposedModuleInfo("Amplify", "Battery and performance tweaks", "2.0.1", true, "Performance"),
        XposedModuleInfo("FakeID", "Spoof device identification", "1.1.0", false, "Security")
    )}

    val searchQuery = remember { mutableStateOf("") }
    val selectedCategory = remember { mutableStateOf("All") }
    val categories = listOf("All", "System", "Privacy", "Media", "Performance", "Security")

    val filteredModules = modules.filter { module ->
        (selectedCategory.value == "All" || module.category == selectedCategory.value) &&
        (searchQuery.value.isEmpty() || module.name.contains(searchQuery.value, ignoreCase = true) ||
         module.description.contains(searchQuery.value, ignoreCase = true))
    }

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
            text = "📦 MODULE MANAGER",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Install, enable, and configure Xposed modules",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFF8C69).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search modules") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFFF6B35)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF6B35),
                unfocusedBorderColor = Color(0xFFFF6B35).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFFFF6B35)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter
        Row(modifier = Modifier.fillMaxWidth()) {
            categories.forEach { category ->
                Button(
                    onClick = { selectedCategory.value = category },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory.value == category)
                            Color(0xFFFF6B35)
                        else
                            Color.Black.copy(alpha = 0.6f),
                        contentColor = if (selectedCategory.value == category)
                            Color.White
                        else
                            Color(0xFFFF6B35)
                    )
                ) {
                    Text(category, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Module Statistics
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val enabledCount = modules.count { it.enabled }
                val totalCount = modules.size

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$enabledCount/$totalCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalCount - enabledCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFDC143C),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${categories.size - 1}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD93D),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Module List
        Text(
            text = "Available Modules",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredModules) { module ->
                ModuleCard(
                    module = module,
                    onToggle = {
                        val index = modules.indexOf(module)
                        if (index != -1) {
                            modules[index] = module.copy(enabled = !module.enabled)
                        }
                    },
                    onConfigure = {
                        // Navigate to module configuration
                    }
                )
            }

            if (filteredModules.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ExtensionOff,
                                contentDescription = "No modules",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No modules found",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Try adjusting your search or category filter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bulk Actions
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    modules.replaceAll { it.copy(enabled = true) }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4ECDC4)
                )
            ) {
                Text("Enable All")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = {
                    modules.replaceAll { it.copy(enabled = false) }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFDC143C)
                )
            ) {
                Text("Disable All")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Changes
        Button(
            onClick = { /* Apply module changes and reboot if needed */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B35)
            )
        ) {
            Text("Apply Changes & Reboot", color = Color.Black)
        }
    }
}

/**
 * Module card component
 */
@Composable
private fun ModuleCard(
    module: XposedModuleInfo,
    onToggle: () -> Unit,
    onConfigure: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (module.enabled) Color(0xFF4ECDC4).copy(alpha = 0.5f) else Color(0xFFDC143C).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            Icon(
                imageVector = if (module.enabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = "Status",
                tint = if (module.enabled) Color(0xFF4ECDC4) else Color(0xFFDC143C),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = module.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Category Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = module.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF6B35),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Version
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Version",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "v${module.version}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    // Status
                    Text(
                        text = if (module.enabled) "Enabled" else "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (module.enabled) Color(0xFF4ECDC4) else Color(0xFFDC143C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Action Buttons
            Column {
                // Toggle Button
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (module.enabled) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                        contentDescription = "Toggle",
                        tint = if (module.enabled) Color(0xFF4ECDC4) else Color(0xFFDC143C),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Configure Button
                IconButton(
                    onClick = onConfigure,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configure",
                        tint = Color(0xFFFFD93D),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
