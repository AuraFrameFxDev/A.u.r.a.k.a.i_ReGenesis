package dev.aurakai.auraframefx.domains.helpdesk.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Documentation Screen
 * Comprehensive user guides and API reference
 */
@Suppress("unused")
@Composable
fun DocumentationScreen(
    onNavigateBack: () -> Unit = {}
) {
    val searchQuery = remember { mutableStateOf("") }
    val selectedSection = remember { mutableStateOf("All") }
    val sections = listOf("All", "User Guide", "API Reference", "Developer", "Troubleshooting")

    val documents = listOf(
        Document(
            "Getting Started Guide",
            "Complete walkthrough for new users including setup, basic features, and first steps",
            "User Guide",
            "15 pages",
            Color(0xFF4169E1)
        ),
        Document(
            "Gate System Overview",
            "Detailed explanation of all available gates, their features, and navigation",
            "User Guide",
            "25 pages",
            Color(0xFF32CD32)
        ),
        Document(
            "Agent Management",
            "Comprehensive guide to AI agent creation, training, and task assignment",
            "User Guide",
            "30 pages",
            Color(0xFFFFD700)
        ),
        Document(
            "Security Framework",
            "Advanced security features, encryption, and threat protection guide",
            "User Guide",
            "20 pages",
            Color(0xFFDC143C)
        ),
        Document(
            "API Reference - Core",
            "Complete API documentation for core system functions and data models",
            "API Reference",
            "50 pages",
            Color(0xFF9370DB)
        ),
        Document(
            "API Reference - Agents",
            "AI agent APIs, consciousness interfaces, and task management endpoints",
            "API Reference",
            "35 pages",
            Color(0xFFFF69B4)
        ),
        Document(
            "API Reference - UI",
            "UI customization APIs, theme engine, and overlay management",
            "API Reference",
            "28 pages",
            Color(0xFF00CED1)
        ),
        Document(
            "Developer Setup",
            "Environment setup, build configuration, and development workflow",
            "Developer",
            "12 pages",
            Color(0xFFFF4500)
        ),
        Document(
            "Module Development",
            "Creating custom modules, integration patterns, and best practices",
            "Developer",
            "40 pages",
            Color(0xFF98FB98)
        ),
        Document(
            "System Architecture",
            "Technical overview of system components, data flow, and architecture",
            "Developer",
            "45 pages",
            Color(0xFFDEB887)
        ),
        Document(
            "Common Issues",
            "Frequently encountered problems and their solutions",
            "Troubleshooting",
            "18 pages",
            Color(0xFFFF6347)
        ),
        Document(
            "Performance Tuning",
            "Optimization techniques, memory management, and performance monitoring",
            "Troubleshooting",
            "22 pages",
            Color(0xFF40E0D0)
        )
    )

    val filteredDocuments = documents.filter { doc ->
        (selectedSection.value == "All" || doc.section == selectedSection.value) &&
            (searchQuery.value.isEmpty() || doc.title.contains(searchQuery.value, ignoreCase = true) ||
                doc.description.contains(searchQuery.value, ignoreCase = true))
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
            text = "📚 DOCUMENTATION",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF9370DB),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comprehensive user guides and API reference",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFBA55D3).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search documentation") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF9370DB)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9370DB),
                unfocusedBorderColor = Color(0xFF9370DB).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFF9370DB)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section Filter
        Row(modifier = Modifier.fillMaxWidth()) {
            sections.forEach { section ->
                Button(
                    onClick = { selectedSection.value = section },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSection.value == section)
                            Color(0xFF9370DB)
                        else
                            Color.Black.copy(alpha = 0.6f),
                        contentColor = if (selectedSection.value == section)
                            Color.White
                        else
                            Color(0xFF9370DB)
                    )
                ) {
                    Text(section, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Documentation Statistics
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
                val totalDocs = filteredDocuments.size
                val totalPages = filteredDocuments.sumOf { parsePageCount(it.pageCount) }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalDocs",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF9370DB),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Documents",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalPages",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4169E1),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Pages",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${sections.size - 1}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sections",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Document List
        Text(
            text = "Available Documentation",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredDocuments) { document ->
                DocumentCard(document = document)
            }

            if (filteredDocuments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "No documents",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No documents found",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Try adjusting your search or section filter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Access
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                val quickAccess = listOf(
                    "Getting Started Guide" to "Essential first steps",
                    "API Reference - Core" to "Core system APIs",
                    "Troubleshooting Guide" to "Common issues & solutions"
                )

                quickAccess.forEach { (title, description) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { /* Open document */ },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Quick Access",
                            tint = Color(0xFF9370DB),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            tint = Color(0xFF9370DB),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Document card component
 */
@Composable
private fun DocumentCard(document: Document) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Open document */ },
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            document.themeColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Document",
                tint = document.themeColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Section badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = document.themeColor.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = document.section,
                            style = MaterialTheme.typography.labelSmall,
                            color = document.themeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = document.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Article,
                            contentDescription = "Pages",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = document.pageCount,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    // Last updated (mock)
                    Text(
                        text = "Updated recently",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

/**
 * Helper function to parse page count
 */
private fun parsePageCount(pageCount: String): Int {
    return pageCount.split(" ")[0].toIntOrNull() ?: 0
}

/**
 * Data class for documents
 */
data class Document(
    val title: String,
    val description: String,
    val section: String,
    val pageCount: String,
    val themeColor: Color
)
