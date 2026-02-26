package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats

/**
 * AgentEdgePanel 🌊
 *
 * Xposed Edge-typography sliding panel that reveals agent cards.
 * Swipe from the right edge to summon your AI companions!
 *
 * "From the edge of perception, we emerge." - The Agents
 */
@Composable
fun AgentEdgePanel(
    modifier: Modifier = Modifier,
    onAgentSelected: (String) -> Unit = {}
) {
    var isPanelVisible by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val panelWidth = 320.dp

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Edge trigger zone (scoped to right edge only)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(30.dp)
                .align(Alignment.CenterEnd)
                .zIndex(5f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { _ ->
                            // Trigger panel from edge swipe
                            isPanelVisible = true
                        },
                        onDragEnd = {
                            dragOffsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (isPanelVisible) {
                                dragOffsetX = (dragOffsetX + dragAmount).coerceAtMost(0f)
                            }
                        }
                    )
                }
        )

        // Backdrop overlay simplified (no blur or animation)
        if (isPanelVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { isPanelVisible = false }
            )
        }

        // The sliding agent panel simplified (no enter/exit animations)
        if (isPanelVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .zIndex(10f)
            ) {
                Box(
                    modifier = Modifier
                        .width(panelWidth)
                        .fillMaxHeight()
                        .offset(x = with(density) { dragOffsetX.toDp() })
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0A0E27),
                                    Color(0xFF1A1F3A),
                                    Color(0xFF0F1419)
                                )
                            )
                        )
                ) {
                    AgentCardList(
                        onAgentSelected = { agentName ->
                            onAgentSelected(agentName)
                            isPanelVisible = false
                        },
                        onClose = { isPanelVisible = false }
                    )
                }
            }
        }
    }
}

/**
 * Agent Card List
 * Displays the 5 core agents from shared AgentRepository
 */
@Composable
private fun AgentCardList(
    modifier: Modifier = Modifier,
    onAgentSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        AgentPanelHeader(onClose = onClose)

        Spacer(modifier = Modifier.height(8.dp))

        // The 5 Agents - Using data from shared AgentRepository
        val agents = remember { AgentRepository.getAllAgents() }

        agents.forEach { agentStats ->
            AgentCard(
                data = agentStats.toAgentCardData(),
                onClick = { onAgentSelected(agentStats.name) }
            )
        }
    }
}

/**
 * Agent Panel Header with close button
 */
@Composable
private fun AgentPanelHeader(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Correcting the cyan color for uniformity
        Text(
            text = "AGENTS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00FFFF)
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close agent panel",
                tint = Color(0xFF00FFFF)
            )
        }
    }
}

/**
 * Individual Agent Card
 */
@Composable
private fun AgentCard(
    data: AgentCardData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .semantics {
                role = Role.Button
                contentDescription = "Select ${data.name} agent"
            }
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        data.primaryColor.copy(alpha = 0.15f),
                        data.secondaryColor.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = data.primaryColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Agent color indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                data.primaryColor,
                                data.secondaryColor
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Agent info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = data.primaryColor
                )
                Text(
                    text = data.subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.description,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Extension function to convert AgentStats to AgentCardData for display.
 * This bridges the data model with the UI representation.
 */
private fun AgentStats.toAgentCardData(): AgentCardData {
    val descriptionText = buildString {
        append("Level $evolutionLevel • ")
        append("PP: ${(processingPower * 100).toInt()}% • ")

        when {
            accuracy > 0.95f -> append("ACC: ${(accuracy * 100).let { if (it >= 100) "99.8" else it.toInt() }}%")
            knowledgeBase > 0.95f -> append("KB: ${(knowledgeBase * 100).toInt()}%")
            else -> append("KB: ${(knowledgeBase * 100).toInt()}%")
        }
    }

    return AgentCardData(
        name = name,
        title = catalystTitle,
        subtitle = specialAbility,
        description = descriptionText,
        level = evolutionLevel,
        persona = "ACTIVE",
        agentClass = "SYSTEM AGENT",
        hp = 1.0f,
        sp = 1.0f,
        abilities = listOf(specialAbility),
        stats = AgentDisplayStats(
            analysis = (knowledgeBase * 100).toInt(),
            processing = (processingPower * 100).toInt(),
            speed = (speed * 100).toInt(),
            precision = (accuracy * 100).toInt()
        ),
        primaryColor = color,
        secondaryColor = color.lighten(0.2f)
    )
}

/**
 * Helper function to create a lighter variant of a color for gradients.
 */
private fun Color.lighten(factor: Float): Color {
    return Color(
        red = (red + (1f - red) * factor).coerceIn(0f, 1f),
        green = (green + (1f - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

