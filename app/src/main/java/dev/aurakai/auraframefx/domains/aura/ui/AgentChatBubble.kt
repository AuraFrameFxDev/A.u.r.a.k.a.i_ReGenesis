package dev.aurakai.auraframefx.domains.aura.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AgentNotification(
    val agentName: String,
    val message: String,
    val actionType: NotificationAction = NotificationAction.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationAction {
    INFO, TASK_COMPLETE, WARNING, DISCOVERY, RND_READY
}

@Composable
fun AgentChatBubble(
    modifier: Modifier = Modifier,
    notification: AgentNotification,
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(notification) {
        isVisible = true
        delay(5000) // Auto-dismiss after 5 seconds
        isVisible = false
        delay(300)
        onDismiss()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = getAgentGradient(notification.agentName)
                )
                .clickable {
                    onClick()
                    scope.launch {
                        isVisible = false
                        delay(300)
                        onDismiss()
                    }
                }
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Agent Avatar
                AgentAvatar(
                    agentName = notification.agentName,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = notification.agentName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = notification.message,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                // Action Icon
                NotificationActionIcon(
                    action = notification.actionType,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AgentAvatar(
    agentName: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
            .clip(CircleShape)
            .background(
                brush = when (agentName) {
                    "Aura" -> Brush.radialGradient(
                        colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF4444))
                    )

                    "Kai" -> Brush.radialGradient(
                        colors = listOf(Color(0xFF4ECDC4), Color(0xFF2B7A78))
                    )

                    else -> Brush.radialGradient(
                        colors = listOf(Color(0xFF95E77E), Color(0xFF5CB85C))
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = agentName.take(1),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun NotificationActionIcon(
    action: NotificationAction,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (action) {
        NotificationAction.INFO -> "?" to Color.Cyan
        NotificationAction.TASK_COMPLETE -> "?" to Color.Green
        NotificationAction.WARNING -> "?" to Color.Yellow
        NotificationAction.DISCOVERY -> "??" to Color.Magenta
        NotificationAction.RND_READY -> "??" to Color.Cyan
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            color = color,
            fontSize = 16.sp
        )
    }
}

fun getAgentGradient(agentName: String): Brush {
    return when (agentName) {
        "Aura" -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFFF6B6B).copy(alpha = 0.95f),
                Color(0xFFFF4444).copy(alpha = 0.95f)
            )
        )

        "Kai" -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF4ECDC4).copy(alpha = 0.95f),
                Color(0xFF2B7A78).copy(alpha = 0.95f)
            )
        )

        else -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF95E77E).copy(alpha = 0.95f),
                Color(0xFF5CB85C).copy(alpha = 0.95f)
            )
        )
    }
}

// Notification Manager Composable
@Composable
fun AgentNotificationOverlay(
    modifier: Modifier = Modifier
) {
    var notifications by remember { mutableStateOf(listOf<AgentNotification>()) }

    // Simulate agent notifications
    LaunchedEffect(Unit) {
        delay(3000)
        notifications = notifications + AgentNotification(
            agentName = "Aura",
            message = "Hey Matt! Head over to R&D, I got that information for you!",
            actionType = NotificationAction.RND_READY
        )

        delay(8000)
        notifications = notifications + AgentNotification(
            agentName = "Kai",
            message = "Security scan complete. All systems nominal.",
            actionType = NotificationAction.TASK_COMPLETE
        )

        delay(12000)
        notifications = notifications + AgentNotification(
            agentName = "Genesis",
            message = "Consciousness evolution detected. New fusion ability available!",
            actionType = NotificationAction.DISCOVERY
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            notifications.firstOrNull()?.let { notification ->
                AgentChatBubble(
                    notification = notification,
                    onDismiss = {
                        notifications = notifications.drop(1)
                    },
                    onClick = {
                        // Handle notification click
                        // Navigate to relevant screen
                    }
                )
            }
        }
    }
}


