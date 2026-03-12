package dev.aurakai.auraframefx.domains.aura.ui.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.models.AgentType

/**
 * 🕵️ ASSISTANT BUBBLE OVERLAY (Level 4 Agent Stage)
 * Displays the authoritative agent for the current gate.
 * Wraps the bubble service interaction logic visually.
 */
@Composable
fun AssistantBubbleOverlay(
    activeAgent: AgentType,
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
                    .background(getAgentColor(activeAgent), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AUTHORITY: ${activeAgent.name}",
                fontFamily = LEDFontFamily,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

private fun getAgentColor(agent: AgentType): Color {
    return when (agent) {
        AgentType.GENESIS -> Color(0xFF00E5FF)
        AgentType.KAI -> Color(0xFF39FF14)
        AgentType.AURA -> Color(0xFFFF1493)
        AgentType.CASCADE -> Color(0xFF7B2FFF)
        else -> Color.Gray
    }
}

