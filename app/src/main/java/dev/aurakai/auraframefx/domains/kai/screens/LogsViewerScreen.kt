package dev.aurakai.auraframefx.domains.kai.screens

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
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.aurakai.auraframefx.domains.kai.viewmodels.KaiSystemViewModel
import dev.aurakai.auraframefx.domains.kai.viewmodels.LogEntry

/**
 * Logs Viewer Screen — Kai reads real logcat via root or fallback process.
 * State comes from KaiSystemViewModel. No local log list.
 */
@Composable
fun LogsViewerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: KaiSystemViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val logsState by viewModel.logsState.collectAsState()
    val logLevels = listOf("All", "DEBUG", "INFO", "WARN", "ERROR", "VERBOSE")

    // Local UI-only filter state (client-side; does not re-run logcat)
    val selectedLevel = remember { mutableStateOf("All") }
    val searchQuery = remember { mutableStateOf("") }

    // Kick off log stream when screen first appears
    LaunchedEffect(Unit) {
        viewModel.startLogStream()
    }

    val allEntries = logsState.entries
    val filteredLogs = allEntries.filter { log ->
        (selectedLevel.value == "All" || log.level == selectedLevel.value) &&
            (searchQuery.value.isEmpty() ||
                log.message.contains(searchQuery.value, ignoreCase = true) ||
                log.tag.contains(searchQuery.value, ignoreCase = true))
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
            text = if (logsState.isStreaming) "Loading logcat…" else "System logs — ${allEntries.size} entries",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFED4E).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search and Filter
        Row(modifier = Modifier.fillMaxWidth()) {
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

            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(0.8f)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD93D))
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
                val errorCount = allEntries.count { it.level == "ERROR" }
                val warnCount = allEntries.count { it.level == "WARN" }
                val infoCount = allEntries.count { it.level == "INFO" }

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

            if (filteredLogs.isEmpty() && !logsState.isStreaming) {
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
                                text = "Try adjusting your search or filter, or tap Refresh",
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
                onClick = { viewModel.clearLogs() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC143C))
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
            Button(
                onClick = { viewModel.startLogStream(logsState.filter) },
                enabled = !logsState.isStreaming,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD93D))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (logsState.isStreaming) "Loading…" else "Refresh", color = Color.Black)
            }
        }
    }
}

@Composable
private fun LogEntryCard(log: LogEntry) {
    val entryColor = Color(log.color)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, entryColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = entryColor.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = log.level,
                    style = MaterialTheme.typography.labelSmall,
                    color = entryColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = log.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "[${log.tag}]",
                style = MaterialTheme.typography.bodySmall,
                color = entryColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
