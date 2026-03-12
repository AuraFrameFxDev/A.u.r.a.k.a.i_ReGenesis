package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * Module Manager Screen
 * Enable/disable modules and configuration
 */
@Composable
fun ModuleManagerScreen() {
    val modules = remember { mutableStateListOf(
        Module("UI Components", "User interface elements and layouts", true, "2.1.0", "UI"),
        Module("Data Storage", "Local data persistence and caching", true, "1.8.3", "Data"),
        Module("Network Services", "API clients and connectivity", true, "3.0.1", "Network"),
        Module("Security Framework", "Authentication and encryption", true, "2.4.2", "Security"),
        Module("AI Agents", "Intelligent assistants and automation", true, "1.9.5", "AI"),
        Module("Analytics", "Usage tracking and reporting", false, "1.2.0", "Analytics"),
        Module("Cloud Sync", "Remote data synchronization", false, "2.0.0", "Cloud"),
        Module("Notifications", "Push notifications and alerts", true, "1.5.3", "System"),
        Module("File Manager", "File operations and storage", true, "1.7.1", "System"),
        Module("Backup Service", "Automated data backup", false, "1.3.2", "Data")
    )}

    val selectedCategory = remember { mutableStateOf("All") }
    val categories = listOf("All", "UI", "Data", "Network", "Security", "AI", "Analytics", "Cloud", "System")

    val filteredModules = if (selectedCategory.value == "All") {
        modules
    } else {
        modules.filter { it.category == selectedCategory.value }
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
            text = "⚙️ MODULE MANAGER",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enable/disable modules and configuration",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFD700).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Category Filter
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Filter by Category",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    categories.forEach { category ->
                        Button(
                            onClick = { selectedCategory.value = category },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCategory.value == category)
                                    Color(0xFFFFD700)
                                else
                                    Color.Black.copy(alpha = 0.6f),
                                contentColor = if (selectedCategory.value == category)
                                    Color.Black
                                else
                                    Color(0xFFFFD700)
                            )
                        ) {
                            Text(category, fontSize = 12.sp)
                        }
                    }
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
                        color = Color(0xFF32CD32),
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
                        text = categories.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4169E1),
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
            text = "Module Configuration",
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
                    contentColor = Color(0xFF32CD32)
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
            onClick = { /* Apply module changes */ },
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
 * Module card component
 */
@Composable
private fun ModuleCard(
    module: Module,
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
            if (module.enabled) Color(0xFF32CD32).copy(alpha = 0.5f) else Color(0xFFDC143C).copy(alpha = 0.5f)
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
                tint = if (module.enabled) Color(0xFF32CD32) else Color(0xFFDC143C),
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
                            containerColor = Color(0xFF4169E1).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = module.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4169E1),
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

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Version: ${module.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Text(
                        text = if (module.enabled) "Enabled" else "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (module.enabled) Color(0xFF32CD32) else Color(0xFFDC143C),
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
                        tint = if (module.enabled) Color(0xFF32CD32) else Color(0xFFDC143C),
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
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Data class for modules
 */
data class Module(
    val name: String,
    val description: String,
    var enabled: Boolean,
    val version: String,
    val category: String
)
