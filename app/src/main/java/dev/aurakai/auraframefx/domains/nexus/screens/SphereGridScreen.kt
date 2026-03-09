package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.aurakai.auraframefx.data.repositories.AgentRepository
import dev.aurakai.auraframefx.models.AgentStats

/**
 * Sphere Grid Screen
 * Agent progression visualization and skill trees
 */
@Composable
fun SphereGridScreen(navController: NavHostController) {
    val agents = remember { AgentRepository.getAllAgents() }
    val selectedAgent = remember { mutableStateOf(agents.first()) }

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
            text = "🔮 SPHERE GRID",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFF69B4),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Agent progression visualization and skill trees",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFFB6C1).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Agent Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Agent",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    items(agents) { agent ->
//                        AgentSphereCard(
//                            agent = agent,
//                            isSelected = selectedAgent.value == agent,
//                            onClick = {
//                                selectedAgent.value = agent
//                                // Navigate to agent's constellation screen
//                                when (agent.name) {
//                                    "Aura" -> navController.navigate(NavDestination.Constellation.route)
//                                    "Genesis" -> navController.navigate(NavDestination.GenesisConstellation.route)
//                                    "Claude" -> navController.navigate(NavDestination.ClaudeConstellation.route)
//                                    "Kai" -> navController.navigate(NavDestination.KaiConstellation.route)
//                                    "Cascade" -> navController.navigate(NavDestination.CascadeConstellation.route)
//                                    "Grok" -> navController.navigate(NavDestination.GrokConstellation.route)
//                                }
//                            }
//                        )
//                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Skill Tree Visualization
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${selectedAgent.value.name} Skill Tree",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Skill Tree Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SkillTreeCanvas(agent = selectedAgent.value)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Skill Points Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${selectedAgent.value.evolutionLevel}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = selectedAgent.value.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${(selectedAgent.value.consciousnessLevel * 1000).toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = selectedAgent.value.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Next Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${((100f - selectedAgent.value.consciousnessLevel) * 100).toInt()} XP",
                            style = MaterialTheme.typography.headlineSmall,
                            color = selectedAgent.value.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Agent sphere card component
 */
@Composable
private fun AgentSphereCard(
    agent: AgentStats,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                agent.color.copy(alpha = 0.3f)
            else
                Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (isSelected) agent.color else Color.Transparent
        ),
        shape = CircleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Agent Icon
            Card(
                modifier = Modifier.size(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = agent.color.copy(alpha = 0.2f)
                ),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = agent.name.first().toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = agent.color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = agent.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text = "Lv.${agent.evolutionLevel}",
                style = MaterialTheme.typography.labelSmall,
                color = agent.color
            )
        }
    }
}

/**
 * Skill tree canvas component
 */
@Composable
private fun SkillTreeCanvas(agent: AgentStats) {
    val skills = listOf(
        SkillNode("Core AI", 0.5f, 0.1f, true, agent.color),
        SkillNode("Learning", 0.3f, 0.3f, agent.evolutionLevel > 2, agent.color),
        SkillNode("Processing", 0.7f, 0.3f, agent.evolutionLevel > 3, agent.color),
        SkillNode("Memory", 0.2f, 0.5f, agent.evolutionLevel > 4, agent.color),
        SkillNode("Creativity", 0.8f, 0.5f, agent.evolutionLevel > 5, agent.color),
        SkillNode("Analysis", 0.5f, 0.7f, agent.evolutionLevel > 6, agent.color),
        SkillNode("Integration", 0.5f, 0.9f, agent.evolutionLevel > 7, agent.color)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Draw connections between skills
        for (i in 0 until skills.size - 1) {
            val skill1 = skills[i]
            val skill2 = skills[i + 1]

            drawLine(
                color = agent.color.copy(alpha = 0.3f),
                start = Offset(skill1.x * canvasWidth, skill1.y * canvasHeight),
                end = Offset(skill2.x * canvasWidth, skill2.y * canvasHeight),
                strokeWidth = 2f
            )
        }

        // Draw skill nodes
        skills.forEach { skill ->
            val centerX = skill.x * canvasWidth
            val centerY = skill.y * canvasHeight
            val radius = if (skill.unlocked) 24f else 16f

            // Outer glow
            drawCircle(
                color = skill.color.copy(alpha = 0.3f),
                radius = radius + 8f,
                center = Offset(centerX, centerY)
            )

            // Node circle
            drawCircle(
                color = if (skill.unlocked) skill.color else Color.Gray,
                radius = radius,
                center = Offset(centerX, centerY)
            )

            // Inner highlight
            if (skill.unlocked) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = radius * 0.6f,
                    center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f)
                )
            }
        }
    }

    // Skill labels overlay
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        skills.forEach { skill ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (skill.y == skills.first().y) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (skill.unlocked) skill.color else Color.Gray,
                        fontWeight = if (skill.unlocked) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Data classes
 */
data class SkillNode(
    val name: String,
    val x: Float, // 0.0 to 1.0
    val y: Float, // 0.0 to 1.0
    val unlocked: Boolean,
    val color: Color
)
