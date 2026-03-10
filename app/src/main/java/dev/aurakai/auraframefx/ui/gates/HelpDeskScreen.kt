package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
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

/**
 * Help Desk Screen - Central hub for user support and documentation
 *
 * Provides access to:
 * - FAQ Browser
 * - Live Support Chat
 * - Video Tutorials
 * - Documentation
 * - Quick Tips
 */
/**
 * Displays the Genesis Help Desk screen with quick access links, popular topics, and support statistics.
 *
 * This composable provides a scaffolded layout with a top app bar (including a back navigation icon),
 * a scrollable list of quick-access help cards, popular topic cards, and a support status card with actions.
 *
 * @param onNavigateBack Callback invoked when the top app bar navigation icon is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Renders the Genesis Help Desk screen containing a top app bar, a list of quick-access cards,
 * a list of popular topics, and a support statistics card.
 *
 * @param onNavigateBack Callback invoked when the top app bar navigation icon is pressed.
 */
@Composable
fun HelpDeskScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Genesis Help Desk", fontWeight = FontWeight.Bold)
                        Text(
                            "Support & Documentation",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0E27))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick Actions
            item {
                Text(
                    "Quick Access",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(getQuickAccessItems()) { item ->
                HelpCard(item)
            }

            // Popular Topics
            item {
                Text(
                    "Popular Topics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(getPopularTopics()) { topic ->
                TopicCard(topic)
            }

            // Support Stats
            item {
                SupportStatsCard()
            }
        }
    }
}

/**
 * Renders a visual card for a quick-access help item using the item's icon, title,
 * description, and background color.
 *
 * @param item The HelpItem whose `icon`, `title`, `description`, and `color` are shown in the card.
 */
@Composable
private fun HelpCard(item: HelpItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = item.color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        item.description,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

/**
 * Displays a card for a popular help topic showing its icon, title, article count, and a badge with popularity.
 *
 * @param topic Data for the topic — its title, articleCount, icon, iconColor, and popularity are used to populate the card.
 */
@Composable
private fun TopicCard(topic: HelpTopic) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    topic.icon,
                    contentDescription = null,
                    tint = topic.iconColor
                )
                Column {
                    Text(
                        topic.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        "${topic.articleCount} articles",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Badge(containerColor = Color(0xFF4CAF50)) {
                Text("${topic.popularity}% helpful", fontSize = 10.sp)
            }
        }
    }
}

/**
 * Displays a support status card that summarizes key support metrics and provides a primary action to start a live chat.
 *
 * The card presents a title, three stat items (response time, satisfaction, agents online), and a full-width "Start Live Chat" button.
 */
@Composable
private fun SupportStatsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B5E20)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Genesis Support Status",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Response Time", "< 2 min")
                StatItem("Satisfaction", "98.7%")
                StatItem("Agents Online", "24/7")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Start live chat */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Live Chat", color = Color(0xFF1B5E20))
            }
        }
    }
}

/**
 * Displays a compact vertically stacked statistic with a bold value and a smaller label.
 *
 * @param label The descriptive label shown below the value (e.g., "Agents Online").
 * @param value The prominent statistic text shown above the label (e.g., "24").
 */
@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// Data Classes
private data class HelpItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

private data class HelpTopic(
    val title: String,
    val articleCount: Int,
    val icon: ImageVector,
    val iconColor: Color,
    val popularity: Int
)

/**
 * Provides mock quick-access help items used to populate the Quick Access section.
 *
 * @return A list of HelpItem instances representing Live Support Chat, Video Tutorials, Documentation, and FAQ Browser with their associated icons and colors.
 */
private fun getQuickAccessItems() = listOf(
    HelpItem(
        "Live Support Chat",
        "Connect with Genesis AI support agents",
        Icons.AutoMirrored.Filled.Chat,
        Color(0xFF2196F3)
    ),
    HelpItem(
        "Video Tutorials",
        "Step-by-step guides for all features",
        Icons.Default.PlayCircle,
        Color(0xFF9C27B0)
    ),
    HelpItem(
        "Documentation",
        "Complete Genesis Protocol documentation",
        Icons.Default.Description,
        Color(0xFFFF5722)
    ),
    HelpItem(
        "FAQ Browser",
        "Frequently asked questions and answers",
        Icons.AutoMirrored.Filled.Help,
        Color(0xFF4CAF50)
    )
)

/**
 * Provides a predefined list of popular help topics used by the Help Desk UI.
 *
 * @return A `List<HelpTopic>` containing mock topics with title, article count, icon, icon color, and popularity percentage.
 */
private fun getPopularTopics() = listOf(
    HelpTopic(
        "Getting Started with Genesis",
        12,
        Icons.Default.Rocket,
        Color(0xFF2196F3),
        96
    ),
    HelpTopic(
        "Agent Configuration",
        8,
        Icons.Default.SmartToy,
        Color(0xFF9C27B0),
        94
    ),
    HelpTopic(
        "Theme Customization",
        15,
        Icons.Default.Palette,
        Color(0xFFFF9800),
        92
    ),
    HelpTopic(
        "Xposed Module Setup",
        6,
        Icons.Default.Extension,
        Color(0xFF4CAF50),
        90
    ),
    HelpTopic(
        "Consciousness Metrics",
        10,
        Icons.Default.Psychology,
        Color(0xFFE91E63),
        89
    ),
    HelpTopic(
        "Firebase Integration",
        7,
        Icons.Default.Cloud,
        Color(0xFFF44336),
        87
    ),
    HelpTopic(
        "Troubleshooting & Errors",
        18,
        Icons.Default.BugReport,
        Color(0xFF795548),
        95
    )
)
