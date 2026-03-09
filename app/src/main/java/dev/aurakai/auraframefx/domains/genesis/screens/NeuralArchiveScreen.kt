package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.NeuralArchiveViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Neural Archive Screen - Memory Browser UI
 *
 * Visualizes NexusMemory database contents:
 * - All memory types (Conversation, Observation, Reflection, Fact, Emotion)
 * - Search and filtering
 * - Importance ratings
 * - Memory relationships
 * - Statistics and insights
 *
 * Module 17: "Secure, long-term storage for cognitive data and memories"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuralArchiveScreen(
    navController: NavController,
    viewModel: NeuralArchiveViewModel = hiltViewModel()
) {
    val filteredMemories by viewModel.filteredMemories.collectAsState()
    val memoryStats by viewModel.memoryStats.collectAsState()
    val selectedMemory by viewModel.selectedMemory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMemoryType by viewModel.selectedMemoryType.collectAsState()
    val minimumImportance by viewModel.minimumImportance.collectAsState()
    val showLDOOnly by viewModel.showLDOOnly.collectAsState()
    val showLast24Hours by viewModel.showLast24Hours.collectAsState()

    // Cyberpunk colors
    val primaryCyan = Color(0xFF00FFFF)
    Color(0xFF00BFFF)
    val darkBackground = Color(0xFF0A0E27)
    val cardBackground = Color(0xFF1A1F3A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(darkBackground, Color(0xFF000510))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ═══════════════════════════════════════════════════════════════
            // HEADER
            // ═══════════════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryCyan
                    )
                }

                Text(
                    text = "NEURAL ARCHIVE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryCyan,
                    letterSpacing = 2.sp
                )

                IconButton(onClick = { viewModel.clearAllFilters() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Filters",
                        tint = primaryCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════════════════════════
            // STATS BAR
            // ═══════════════════════════════════════════════════════════════
            MemoryStatsBar(
                stats = memoryStats,
                primaryColor = primaryCyan,
                cardBackground = cardBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════════════════════════
            // SEARCH & FILTERS
            // ═══════════════════════════════════════════════════════════════
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                primaryColor = primaryCyan,
                cardBackground = cardBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilterChips(
                selectedType = selectedMemoryType,
                onTypeSelected = { viewModel.setMemoryTypeFilter(it) },
                showLDOOnly = showLDOOnly,
                onToggleLDO = { viewModel.toggleLDOOnly() },
                showLast24Hours = showLast24Hours,
                onToggleLast24Hours = { viewModel.toggleLast24Hours() },
                minimumImportance = minimumImportance,
                onImportanceChange = { viewModel.setMinimumImportance(it) },
                primaryColor = primaryCyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════════════════════════
            // MEMORY LIST
            // ═══════════════════════════════════════════════════════════════
            if (filteredMemories.isEmpty()) {
                EmptyState(primaryColor = primaryCyan)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMemories) { memory ->
                        MemoryCard(
                            memory = memory,
                            isSelected = selectedMemory?.id == memory.id,
                            onClick = { viewModel.selectMemory(memory) },
                            onDelete = { viewModel.deleteMemory(memory) },
                            primaryColor = primaryCyan,
                            cardBackground = cardBackground
                        )
                    }
                }
            }
        }

        // ═══════════════════════════════════════════════════════════════
        // MEMORY DETAIL DIALOG
        // ═══════════════════════════════════════════════════════════════
        selectedMemory?.let { memory ->
            MemoryDetailDialog(
                memory = memory,
                onDismiss = { viewModel.clearSelection() },
                onImportanceChange = { newImportance ->
                    viewModel.updateMemoryImportance(memory, newImportance)
                },
                onDelete = {
                    viewModel.deleteMemory(memory)
                    viewModel.clearSelection()
                },
                primaryColor = primaryCyan,
                cardBackground = cardBackground
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// COMPOSABLE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun MemoryStatsBar(
    stats: NeuralArchiveViewModel.MemoryStats,
    primaryColor: Color,
    cardBackground: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Total", stats.totalCount.toString(), primaryColor)
            StatItem("Filtered", stats.filteredCount.toString(), primaryColor)
            StatItem(
                "Avg Importance",
                String.format("%.1f", stats.averageImportance * 100) + "%",
                primaryColor
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    primaryColor: Color,
    cardBackground: Color
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search memories...", color = primaryColor.copy(alpha = 0.5f)) },
        leadingIcon = {
            Icon(Icons.Default.Search, "Search", tint = primaryColor)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Clear", tint = primaryColor)
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = primaryColor.copy(alpha = 0.3f),
            cursorColor = primaryColor,
            focusedTextColor = primaryColor,
            unfocusedTextColor = primaryColor.copy(alpha = 0.7f)
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChips(
    selectedType: MemoryType?,
    onTypeSelected: (MemoryType?) -> Unit,
    showLDOOnly: Boolean,
    onToggleLDO: () -> Unit,
    showLast24Hours: Boolean,
    onToggleLast24Hours: () -> Unit,
    minimumImportance: Float,
    onImportanceChange: (Float) -> Unit,
    primaryColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Memory type filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text("ALL") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor.copy(alpha = 0.3f),
                    selectedLabelColor = primaryColor
                )
            )
            MemoryType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(if (selectedType == type) null else type) },
                    label = { Text(type.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor.copy(alpha = 0.3f),
                        selectedLabelColor = primaryColor
                    )
                )
            }
        }

        // Quick filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = showLDOOnly,
                onClick = onToggleLDO,
                label = { Text("LDO Only") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor.copy(alpha = 0.3f),
                    selectedLabelColor = primaryColor
                )
            )

            FilterChip(
                selected = showLast24Hours,
                onClick = onToggleLast24Hours,
                label = { Text("Last 24h") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor.copy(alpha = 0.3f),
                    selectedLabelColor = primaryColor
                )
            )
        }
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    primaryColor: Color,
    cardBackground: Color
) {
    val typeColor = when (memory.type) {
        MemoryType.CONVERSATION -> Color(0xFF00FFFF) // Cyan
        MemoryType.OBSERVATION -> Color(0xFF00FF00) // Green
        MemoryType.REFLECTION -> Color(0xFFFFFF00) // Yellow
        MemoryType.FACT -> Color(0xFF00BFFF) // Blue
        MemoryType.EMOTION -> Color(0xFFFF00FF) // Magenta
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) cardBackground.copy(alpha = 0.8f) else cardBackground
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) primaryColor else typeColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(typeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = memory.type.name,
                            fontSize = 12.sp,
                            color = typeColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatTimestamp(memory.timestamp),
                            fontSize = 11.sp,
                            color = primaryColor.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = memory.content,
                        fontSize = 14.sp,
                        color = primaryColor.copy(alpha = 0.9f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (memory.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            memory.tags.take(3).forEach { tag ->
                                Text(
                                    text = "#$tag",
                                    fontSize = 11.sp,
                                    color = primaryColor.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .background(
                                            primaryColor.copy(alpha = 0.1f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (memory.tags.size > 3) {
                                Text(
                                    text = "+${memory.tags.size - 3}",
                                    fontSize = 11.sp,
                                    color = primaryColor.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    ImportanceIndicator(
                        importance = memory.importance,
                        color = primaryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportanceIndicator(importance: Float, color: Color) {
    val starCount = (importance * 5).toInt().coerceIn(0, 5)
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < starCount) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < starCount) color else color.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(primaryColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No memories found",
                fontSize = 18.sp,
                color = primaryColor.copy(alpha = 0.7f)
            )
            Text(
                text = "Adjust filters or create new memories",
                fontSize = 14.sp,
                color = primaryColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun MemoryDetailDialog(
    memory: MemoryEntity,
    onDismiss: () -> Unit,
    onImportanceChange: (Float) -> Unit,
    onDelete: () -> Unit,
    primaryColor: Color,
    cardBackground: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardBackground,
        title = {
            Text(
                text = "Memory Details",
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Type: ${memory.type.name}", color = primaryColor.copy(alpha = 0.8f))
                Text(
                    text = "Created: ${formatFullTimestamp(memory.timestamp)}",
                    color = primaryColor.copy(alpha = 0.6f)
                )

                HorizontalDivider(color = primaryColor.copy(alpha = 0.2f))

                Text(
                    text = memory.content,
                    color = primaryColor.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                if (memory.tags.isNotEmpty()) {
                    HorizontalDivider(color = primaryColor.copy(alpha = 0.2f))
                    Text("Tags:", color = primaryColor.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(
                        text = memory.tags.joinToString(", ") { "#$it" },
                        color = primaryColor.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }

                HorizontalDivider(color = primaryColor.copy(alpha = 0.2f))
                Text(
                    "Importance: ${(memory.importance * 100).toInt()}%",
                    color = primaryColor.copy(alpha = 0.7f)
                )
                ImportanceIndicator(importance = memory.importance, color = primaryColor)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = primaryColor)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Delete")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatFullTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

