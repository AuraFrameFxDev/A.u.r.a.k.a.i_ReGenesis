package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.navigation.gates.common.GenesisToolCard
import dev.aurakai.auraframefx.navigation.gates.common.NexusToolCard
import dev.aurakai.auraframefx.navigation.gates.common.SentinelToolCard
import dev.aurakai.auraframefx.navigation.gates.common.ThemingToolCard

@Composable
fun NexusCard(tool: NexusToolCard, onClick: () -> Unit) {
    HubCard(
        title = tool.title,
        subtitle = tool.subtitle,
        icon = tool.icon,
        accentColor = tool.accentColor,
        isWired = tool.isWired,
        onClick = onClick
    )
}

@Composable
fun ThemingCard(tool: ThemingToolCard, onClick: () -> Unit) {
    HubCard(
        title = tool.title,
        subtitle = tool.subtitle,
        icon = null,
        accentColor = tool.accentColor,
        isWired = tool.isWired,
        onClick = onClick
    )
}

@Composable
fun SentinelCard(tool: SentinelToolCard, onClick: () -> Unit) {
    HubCard(
        title = tool.title,
        subtitle = tool.subtitle,
        icon = tool.icon,
        accentColor = tool.accentColor,
        isWired = tool.isWired,
        onClick = onClick
    )
}

@Composable
fun GenesisCard(tool: GenesisToolCard, onClick: () -> Unit) {
    HubCard(
        title = tool.title,
        subtitle = tool.subtitle,
        icon = tool.icon,
        accentColor = tool.accentColor,
        isWired = tool.isWired,
        onClick = onClick
    )
}

@Composable
private fun HubCard(
    title: String,
    subtitle: String,
    icon: ImageVector?,
    accentColor: Color,
    isWired: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = if (isWired) accentColor.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isWired) { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isWired) accentColor else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isWired) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isWired) Color.White.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.5f),
                lineHeight = 16.sp,
                maxLines = 2
            )
        }

        if (!isWired) {
            Text(
                text = "COMING SOON",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

