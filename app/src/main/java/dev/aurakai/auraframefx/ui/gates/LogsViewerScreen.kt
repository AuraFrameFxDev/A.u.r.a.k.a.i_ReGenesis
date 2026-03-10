package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
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
 * Logs Viewer Screen
 * View system logs and module activity
 */
@Composable
fun LogsViewerScreen(
    onNavigateBack: () -> Unit = {}
) {
    val logs = remember { mutableStateListOf(
        LogEntry("INFO", "LSPosed", "Framework initialized successfully", "10:30:15", Color(0xFF4ECDC4)),
        LogEntry("WARN", "GravityBox", "System UI hook applied", "10:29:42", Color(0xFFFFD93D)),
        LogEntry("ERROR", "XPrivacyLua", "Permission denied for location access", "10:28:33", Color(0xFFDC143C)),
        LogEntry("INFO", "App Settings", "Per-app configuration loaded", "10:27:18", Color(0xFF4ECDC4)),
        LogEntry("DEBUG", "YouTube AdAway", "Ad detection algorithm updated", "10:26:55", Color(0xFF9370DB)),
        LogEntry("INFO", "BootManager", "Startup optimization completed", "10:25:12", Color(0xFF4ECDC4)),
        LogEntry("WARN", "Amplify", "Battery calibration required", "10:24:38", Color(0xFFFFD93D)),
        LogEntry("INFO", "System", "All hooks loaded successfully", "10:23:05", Color(0xFF4ECDC4)),
        LogEntry("ERROR", "FakeID", "Device ID spoofing failed", "10:22:29", Color(0xFFDC143C)),
        LogEntry("DEBUG", "Network", "VPN connection established", "10:21:47", Color(0xFF9370DB))
    )}

    val selectedLevel = remember { mutableStateOf("All") }
    val logLevels = listOf("All", "DEBUG", "INFO", "WARN", "ERROR")
    val searchQuery = remember { mutableStateOf("") }

    val filteredLogs = logs.filter { log ->
        (selectedLevel.value == "All" || log.level == selectedLevel.value) &&
        (searchQuery.value.isEmpty() || log.message.contains(searchQuery.value, ignoreCase = true) ||
         log.source.contains(searchQuery.value, ignoreCase = true))
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
            text = "📋 LOGS VIEWER",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFD93D),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "View system logs and module activity",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFED4E).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search and Filter
        Row(modifier = Modifier.fillMaxWidth()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = { Text("Search logs") },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFFFD93D)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFD93D),
                    unfocusedBorderColor = Color(0xFFFFD93D).copy(alpha = 0.5f),
                    focusedLabelColor = Color(0xFFFFD93D)
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Level Filter
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(0.8f)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFFD93D)
                    )
                ) {
                    Text(selectedLevel.value)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.9f))
                ) {
                    logLevels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level, color = Color.White) },
                            onClick = {
                                selectedLevel.value = level
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Log Statistics
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
                val errorCount = logs.count { it.level == "ERROR" }
                val warnCount = logs.count { it.level == "WARN" }
                val infoCount = logs.count { it.level == "INFO" }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$errorCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFDC143C),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Errors",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$warnCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD93D),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Warnings",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$infoCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Info",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logs List
        Text(
            text = "Recent Logs (${filteredLogs.size})",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredLogs) { log ->
                LogEntryCard(log = log)
            }

            if (filteredLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ListAlt,
                                contentDescription = "No logs",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No logs found",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Try adjusting your search or filter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    // Clear all logs
                    logs.clear()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFDC143C)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Logs")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = {
                    // Export logs
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4ECDC4)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Export",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    // Refresh logs
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD93D)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Refresh", color = Color.Black)
            }
        }
    }
}

/**
 * Log entry card component
 */
@Composable
private fun LogEntryCard(log: LogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            log.color.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = log.color.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = log.level,
                    style = MaterialTheme.typography.labelSmall,
                    color = log.color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Timestamp
            Text(
                text = log.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Source
            Text(
                text = "[${log.source}]",
                style = MaterialTheme.typography.bodySmall,
                color = log.color,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Message
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Data class for log entries
 */
data class LogEntry(
    val level: String,
    val source: String,
    val message: String,
    val timestamp: String,
    val color: Color
)
