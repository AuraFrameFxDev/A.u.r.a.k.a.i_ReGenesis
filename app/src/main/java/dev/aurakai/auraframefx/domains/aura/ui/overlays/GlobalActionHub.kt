package dev.aurakai.auraframefx.domains.aura.ui.overlays

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.aura.ui.theme.getAgentColor

/**
 * ⚡ GLOBAL ACTION HUB (5-Action Protocol)
 * Persistent interaction bar for Voice, Connect, Assign, Design, Create.
 * Adapts its "ChromaCore" energy based on the active agent.
 */
@Composable
fun GlobalActionHub(
    activeAgentName: String,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val agentColor = getAgentColor(activeAgentName)
    val animatedColor by animateColorAsState(targetValue = agentColor, label = "agentColor")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 🧪 BLURRED GLASS CONTAINER
        Row(
            modifier = Modifier
                .height(72.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.8f))
                    ),
                    RoundedCornerShape(36.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            animatedColor.copy(alpha = 0.1f),
                            animatedColor,
                            animatedColor.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionHubItem("VOICE", Icons.Default.Mic, animatedColor) { onActionClick("voice") }
            ActionHubItem("CONNECT", Icons.Default.Link, animatedColor) { onActionClick("connect") }

            // Central Pulse (Authority Core)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(animatedColor.copy(alpha = 0.1f))
                    .border(2.dp, animatedColor, CircleShape)
                    .clickable { onActionClick("authority") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Adjust,
                    contentDescription = "Core",
                    tint = animatedColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            ActionHubItem(
                "ASSIGN",
                Icons.Default.Assignment,
                animatedColor
            ) { onActionClick("assign") }
            ActionHubItem(
                "CREATE",
                Icons.Default.AddCircle,
                animatedColor
            ) { onActionClick("create") }
        }
    }
}

@Composable
private fun ActionHubItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = color.copy(alpha = 0.6f),
            fontSize = 8.sp,
            fontFamily = LEDFontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
