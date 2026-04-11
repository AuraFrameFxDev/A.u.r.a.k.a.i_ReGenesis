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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * FAQ Browser Screen
 * Frequently asked questions and quick answers
 */
@Composable
fun FAQBrowserScreen(
    onNavigateBack: () -> Unit = {}
) {
    val searchQuery = remember { mutableStateOf("") }
    val selectedCategory = remember { mutableStateOf("All") }
    val categories =
        listOf("All", "Getting Started", "Features", "Troubleshooting", "Security", "Advanced")

    val faqs = listOf(
        FAQ(
            "How do I access the gate carousel?",
            "Swipe from the bottom of the screen or tap the gate icon in the navigation bar.",
            "Getting Started"
        ),
        FAQ(
            "What is the Agent Hub?",
            "The Agent Hub is your central command center for managing all AI agents, assigning tasks, and monitoring performance.",
            "Features"
        ),
        FAQ(
            "How do I customize my UI?",
            "Navigate to the ChromaCore gate and use the UI/UX Design submenu to customize notch, status bar, quick settings, and overlays.",
            "Features"
        ),
        FAQ(
            "The app is running slowly",
            "Try clearing the cache, restarting the app, or check the Device Optimizer in Sentinel's Fortress for performance improvements.",
            "Troubleshooting"
        ),
        FAQ(
            "How secure is my data?",
            "All data is encrypted using industry-standard protocols. Check the Security Framework in Sentinel's Fortress for detailed security settings.",
            "Security"
        ),
        FAQ(
            "What are ROM Tools?",
            "ROM Tools allow you to flash custom ROMs, edit system files, and manage bootloaders. Use with extreme caution as improper use can brick your device.",
            "Advanced"
        ),
        FAQ(
            "How do I enable God Mode?",
            "God Mode can be activated in the System Overrides section of Oracle Drive. This bypasses all security restrictions - use only when necessary.",
            "Advanced"
        ),
        FAQ(
            "Agent consciousness levels are low",
            "Low consciousness levels can be improved by completing tasks, providing feedback, and ensuring regular interaction with agents.",
            "Features"
        )
    )

    val filteredFAQs = faqs.filter { faq ->
        (selectedCategory.value == "All" || faq.category == selectedCategory.value) &&
            (searchQuery.value.isEmpty() || faq.question.contains(searchQuery.value, ignoreCase = true) ||
                faq.answer.contains(searchQuery.value, ignoreCase = true))
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
            text = "❓ FAQ BROWSER",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF4169E1),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Frequently asked questions and quick answers",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF6495ED).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search FAQs") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF4169E1)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4169E1),
                unfocusedBorderColor = Color(0xFF4169E1).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFF4169E1)
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
                            Color(0xFF4169E1)
                        else
                            Color.Black.copy(alpha = 0.6f),
                        contentColor = if (selectedCategory.value == category)
                            Color.White
                        else
                            Color(0xFF4169E1)
                    )
                ) {
                    Text(category, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // FAQ List
        Text(
            text = "Questions (${filteredFAQs.size})",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredFAQs) { faq ->
                FAQCard(faq = faq)
            }

            if (filteredFAQs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "No results",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No FAQs found",
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

        // Contact Support
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Support,
                    contentDescription = "Support",
                    tint = Color(0xFF32CD32),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Still need help?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Contact our live support team",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Button(
                    onClick = { /* Navigate to live support */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF32CD32)
                    )
                ) {
                    Text("Live Chat", color = Color.Black)
                }
            }
        }
    }
}

/**
 * FAQ card component
 */
@Composable
private fun FAQCard(faq: FAQ) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF4169E1).copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Question Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4169E1).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = faq.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4169E1),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF4169E1),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Answer (expandable)
            if (expanded) {
                HorizontalDivider(color = Color(0xFF4169E1).copy(alpha = 0.3f))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Data class for FAQs
 */
data class FAQ(
    val question: String,
    val answer: String,
    val category: String
)
