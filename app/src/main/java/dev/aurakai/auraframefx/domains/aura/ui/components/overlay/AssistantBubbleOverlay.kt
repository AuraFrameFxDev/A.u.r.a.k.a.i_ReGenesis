package dev.aurakai.auraframefx.domains.aura.ui.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🕵️ ASSISTANT BUBBLE OVERLAY (Level 4 Agent Stage)
 * Displays the authoritative agent for the current gate.
 * Wraps the bubble service interaction logic visually.
 */
@Composable
fun AssistantBubbleOverlay(
    activeCategory: AgentCapabilityCategory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        // Agent Authority Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(getCategoryColor(activeCategory), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AUTHORITY: ${activeCategory.name}",
                fontFamily = LEDFontFamily,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

private fun getCategoryColor(category: AgentCapabilityCategory): Color {
    return when (category) {
        AgentCapabilityCategory.COORDINATION -> Color(0xFF00E5FF)
        AgentCapabilityCategory.ANALYSIS -> Color(0xFF39FF14)
        AgentCapabilityCategory.CREATIVE -> Color(0xFFFF1493)
        AgentCapabilityCategory.SPECIALIZED -> Color(0xFF7B2FFF)
        else -> Color.Gray
    }
}

