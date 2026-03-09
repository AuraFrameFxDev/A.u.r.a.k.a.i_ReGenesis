package dev.aurakai.auraframefx.domains.helpdesk.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
 * Tutorial Videos Screen
 * Step-by-step guides and feature walkthroughs
 */
@Composable
fun TutorialVideosScreen(
    onNavigateBack: () -> Unit = {}
) {
    val selectedCategory = remember { mutableStateOf("All") }
    val categories = listOf("All", "Getting Started", "Features", "Advanced", "Troubleshooting")

    val tutorials = listOf(
        TutorialVideo(
            "Welcome to AuraKai",
            "Get started with your AI-powered Android experience",
            "5:32",
            "Getting Started",
            Color(0xFF4169E1),
            true
        ),
        TutorialVideo(
            "Navigating the Gate System",
            "Learn how to access and use all available gates",
            "8:15",
            "Getting Started",
            Color(0xFF32CD32),
            true
        ),
        TutorialVideo(
            "Agent Hub Overview",
            "Manage your AI agents and assign tasks effectively",
            "12:45",
            "Features",
            Color(0xFFFFD700),
            true
        ),
        TutorialVideo(
            "Customizing Your UI",
            "Personalize your device with ChromaCore tools",
            "15:20",
            "Features",
            Color(0xFFFF69B4),
            true
        ),
        TutorialVideo(
            "Security Best Practices",
            "Keep your device and data secure with Sentinel's Fortress",
            "10:08",
            "Features",
            Color(0xFFDC143C),
            true
        ),
        TutorialVideo(
            "ROM Flashing Guide",
            "Safely flash custom ROMs using ROM Tools",
            "18:30",
            "Advanced",
            Color(0xFFFF4500),
            false
        ),
        TutorialVideo(
            "System Overrides",
            "Understanding and using Oracle Drive's override features",
            "14:22",
            "Advanced",
            Color(0xFF9370DB),
            false
        ),
        TutorialVideo(
            "Troubleshooting Common Issues",
            "Fix app crashes, performance problems, and more",
            "9:45",
            "Troubleshooting",
            Color(0xFF00CED1),
            true
        ),
        TutorialVideo(
            "Backup and Recovery",
            "Learn to backup your data and recover from issues",
            "11:15",
            "Troubleshooting",
            Color(0xFF98FB98),
            true
        )
    )

    val filteredTutorials = tutorials.filter { tutorial ->
        selectedCategory.value == "All" || tutorial.category == selectedCategory.value
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
            text = "🎥 TUTORIAL VIDEOS",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Step-by-step guides and feature walkthroughs",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFD700).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

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
                            Color(0xFFFFD700)
                        else
                            Color.Black.copy(alpha = 0.6f),
                        contentColor = if (selectedCategory.value == category)
                            Color.Black
                        else
                            Color(0xFFFFD700)
                    )
                ) {
                    Text(category, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tutorial Statistics
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
                val totalVideos = filteredTutorials.size
                val watchedVideos = filteredTutorials.count { it.watched }
                val totalDuration = filteredTutorials.sumOf { parseDuration(it.duration) }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalVideos",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Videos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$watchedVideos",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF32CD32),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Watched",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatDuration(totalDuration),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4169E1),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Time",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tutorial List
        Text(
            text = "Available Tutorials",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTutorials) { tutorial ->
                TutorialVideoCard(tutorial = tutorial)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Learning Path
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recommended Learning Path",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                val learningPath =
                    listOf("Getting Started", "Features", "Troubleshooting", "Advanced")
                learningPath.forEachIndexed { index, category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFD700)
                            ),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        val categoryVideos = tutorials.filter { it.category == category }
                        val watchedCount = categoryVideos.count { it.watched }

                        Text(
                            text = "$watchedCount/${categoryVideos.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (watchedCount == categoryVideos.size) Color(0xFF32CD32) else Color(
                                0xFFFFD700
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tutorial video card component
 */
@Composable
private fun TutorialVideoCard(tutorial: TutorialVideo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Play video */ },
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            tutorial.themeColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video Thumbnail Placeholder
            Card(
                modifier = Modifier.size(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = tutorial.themeColor.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = tutorial.themeColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tutorial.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Watched indicator
                    if (tutorial.watched) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Watched",
                            tint = Color(0xFF32CD32),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tutorial.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = tutorial.themeColor.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = tutorial.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = tutorial.themeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Duration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Duration",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tutorial.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper functions
 */
private fun parseDuration(duration: String): Int {
    val parts = duration.split(":")
    return if (parts.size == 2) {
        parts[0].toInt() * 60 + parts[1].toInt()
    } else {
        0
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60

    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}

/**
 * Data class for tutorial videos
 */
data class TutorialVideo(
    val title: String,
    val description: String,
    val duration: String,
    val category: String,
    val themeColor: Color,
    var watched: Boolean = false
)
