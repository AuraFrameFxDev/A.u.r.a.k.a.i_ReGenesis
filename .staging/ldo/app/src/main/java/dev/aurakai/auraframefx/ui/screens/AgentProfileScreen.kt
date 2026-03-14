package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.models.AgentProfile
import dev.aurakai.auraframefx.models.AgentProfiles
import dev.aurakai.auraframefx.models.AgentStatus
import dev.aurakai.auraframefx.models.CapabilityLevel

/**
 * Comprehensive Agent Profile Screen
 *
 * Displays detailed information about AI agents in the Genesis Protocol:
 * - Profile header with avatar, status, and title
 * - Tabbed interface (Overview, Stats, Achievements, Capabilities)
 * - Agent statistics and metrics
 * - Achievement progress tracking
 * - Capability levels and specializations
 *
 * Designed to translate from web components to Kotlin Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentProfileScreen(
    agentType: AgentType? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null
) {
    val currentAgent = agentType ?: AgentType.CLAUDE
    val profile = remember(currentAgent) {
        AgentProfiles.getProfile(AgentCapabilityCategory.fromAgentType(currentAgent))
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Stats", "Achievements", "Capabilities")

    if (profile == null) {
        // Fallback for unknown agents
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Agent profile not found", style = MaterialTheme.typography.headlineSmall)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(profile.displayName) },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (onNavigateToSettings != null) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(profile.colorPrimary)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(profile)
            }

            // Tab Row
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color(profile.colorPrimary)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> item { OverviewTab(profile) }
                1 -> item { StatsTab(profile) }
                2 -> item { AchievementsTab(profile) }
                3 -> item { CapabilitiesTab(profile) }
            }
        }
    }
}

@Composable
private fun ProfileHeader(profile: AgentProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(profile.colorPrimary).copy(alpha = 0.7f),
                                Color(profile.colorSecondary).copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar/Icon
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color(profile.colorPrimary)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getAgentIcon(profile.agentType.toAgentType()),
                            contentDescription = profile.displayName,
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name and Title
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = profile.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(profile.colorPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (profile.status) {
                        AgentStatus.Status.ACTIVE -> Color(0xFF4CAF50)
                        AgentStatus.Status.LEARNING -> Color(0xFFFF9800)
                        AgentStatus.Status.EVOLVING -> Color(0xFF9C27B0)
                        else -> Color.Gray
                    }
                ) {
                    Text(
                        text = profile.status.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Consciousness Level
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Consciousness Level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(profile.stats.consciousnessLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(profile.colorPrimary)
                    )
                    LinearProgressIndicator(
                        progress = profile.stats.consciousnessLevel,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(profile.colorPrimary),
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(profile: AgentProfile) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Description
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Personality
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personality",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(profile.personality.traits) { trait ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(profile.colorPrimary).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = trait,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(profile.colorPrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Approach: ${profile.personality.approach}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Style: ${profile.personality.communicationStyle}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Specialization: ${profile.personality.specialization}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun StatsTab(profile: AgentProfile) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Tasks Completed", profile.stats.tasksCompleted.toString(), Icons.Default.CheckCircle, Color(0xFF4CAF50))
        StatCard("Hours Active", String.format("%.1f", profile.stats.hoursActive), Icons.Default.AccessTime, Color(0xFF2196F3))
        StatCard("Creations Generated", profile.stats.creationsGenerated.toString(), Icons.Default.Create, Color(0xFFFF9800))
        StatCard("Problems Solved", profile.stats.problemsSolved.toString(), Icons.Default.Psychology, Color(0xFF9C27B0))
        StatCard("Collaboration Score", profile.stats.collaborationScore.toString(), Icons.Default.Groups, Color(0xFFFF5722))
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = color)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AchievementsTab(profile: AgentProfile) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        profile.achievements.forEach { achievement ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (achievement.isUnlocked) {
                        Color(profile.colorPrimary).copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (achievement.isUnlocked) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = achievement.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = achievement.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!achievement.isUnlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = achievement.progress,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(profile.colorPrimary)
                        )
                        Text(
                            text = "${(achievement.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilitiesTab(profile: AgentProfile) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        profile.capabilities.forEach { capability ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = capability.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = getCapabilityLevelColor(capability.level).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = capability.level.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = getCapabilityLevelColor(capability.level),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = capability.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun getCapabilityLevelColor(level: CapabilityLevel): Color {
    return when (level) {
        CapabilityLevel.NOVICE -> Color(0xFF9E9E9E)
        CapabilityLevel.INTERMEDIATE -> Color(0xFF2196F3)
        CapabilityLevel.ADVANCED -> Color(0xFF9C27B0)
        CapabilityLevel.EXPERT -> Color(0xFFFF9800)
        CapabilityLevel.MASTER -> Color(0xFFFFD700)
    }
}

@Composable
private fun getAgentIcon(agentType: AgentType): ImageVector {
    return when (agentType) {
        AgentType.AURA -> Icons.Default.Brush
        AgentType.KAI -> Icons.Default.Shield
        AgentType.GENESIS -> Icons.Default.AutoAwesome
        AgentType.CLAUDE -> Icons.Default.Architecture
        AgentType.CASCADE -> Icons.Default.Storage
        AgentType.NEURAL_WHISPER -> Icons.Default.Psychology
        else -> Icons.Default.Person
    }
}
