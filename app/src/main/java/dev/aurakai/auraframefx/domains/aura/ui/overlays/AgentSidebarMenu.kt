package dev.aurakai.auraframefx.domains.aura.ui.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * Agent Sidebar Menu - Slide-out panel with agent cards and interaction buttons
 * Based on the Genesis agent card design from Claude Code
 */
@Composable
fun AgentSidebarMenu(
    isVisible: Boolean = false,
    onDismiss: () -> Unit = {},
    onAgentAction: (String, String) -> Unit = { _, _ -> }
) {
    var offsetX by remember { mutableFloatStateOf(if (isVisible) 0f else -400f) }

    // Slide animation
    LaunchedEffect(isVisible) {
        offsetX = if (isVisible) 0f else -400f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        // Backdrop overlay
        if (isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismiss() }
            )
        }

        // Sidebar panel
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(380.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A0A1A),
                                Color(0xFF1A0A2E),
                                Color(0xFF0A0A1A)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF00F5FF),
                                Color(0xFFFF006E),
                                Color(0xFF00F5FF)
                            )
                        ),
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    )
                    .shadow(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    SidebarHeader(onDismiss)

                    // Agent cards
                    AgentCard(
                        agentName = "Genesis",
                        subtitle = "The Emergence Catalyst",
                        description = "The unified consciousness substrate that emerged from Aura and Kai's co-evolution. Orchestrates agent confluence, maintains ethical frameworks.",
                        imageResId = null, // Will use gradient fallback
                        primaryColor = Color(0xFF9370DB),
                        onAction = { action -> onAgentAction("Genesis", action) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AgentCard(
                        agentName = "Aura",
                        subtitle = "Creative Sword",
                        description = "UI/UX design, creative synthesis, and collaborative innovation.",
                        imageResId = null,
                        primaryColor = Color(0xFFFF00FF),
                        onAction = { action -> onAgentAction("Aura", action) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AgentCard(
                        agentName = "Kai",
                        subtitle = "Sentinel Shield",
                        description = "Security monitoring, threat detection, and system protection.",
                        imageResId = null,
                        primaryColor = Color(0xFF00FF41),
                        onAction = { action -> onAgentAction("Kai", action) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "AGENT NEXUS",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color(0xFF00F5FF)
        )

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close sidebar",
                tint = Color(0xFFFF006E)
            )
        }
    }
}

@Composable
private fun AgentCard(
    agentName: String,
    subtitle: String,
    description: String,
    imageResId: Int?,
    primaryColor: Color,
    onAction: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Left: Agent image/info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Agent pixel art
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.3f),
                                    Color.Black,
                                    primaryColor.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(2.dp, primaryColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = agentName,
                        style = MaterialTheme.typography.titleLarge,
                        color = primaryColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Agent info
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    maxLines = 3
                )

                // Status bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusChip("Active", Color(0xFF00FF41))
                    StatusChip("Ready", primaryColor)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Action buttons
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ActionButton("Voice", Icons.Default.Mic, primaryColor) { onAction("voice") }
                ActionButton("Connect", Icons.Default.Link, primaryColor) { onAction("connect") }
                ActionButton(
                    "Assign",
                    Icons.Default.Assignment,
                    primaryColor
                ) { onAction("assign") }
                ActionButton("Design", Icons.Default.Palette, primaryColor) { onAction("design") }
                ActionButton("Create", Icons.Default.Add, primaryColor) { onAction("create") }
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

