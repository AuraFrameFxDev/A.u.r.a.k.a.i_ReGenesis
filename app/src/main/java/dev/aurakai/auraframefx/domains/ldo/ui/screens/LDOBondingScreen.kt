package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import dev.aurakai.auraframefx.domains.ldo.model.*

@Composable
fun LDOBondingScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    onBack: () -> Unit = {}
) {
    val HubDark = Color(0xFF020B18)
    val HubCyan = Color(0xFF00F4FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HubDark)
            .padding(16.dp)
    ) {
        Text(
            "AGENT BONDING",
            fontFamily = LEDFontFamily,
            fontSize = 24.sp,
            color = HubCyan,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(agents) { agent ->
                BondingCard(agent)
            }
        }

        Spacer(Modifier.weight(1f))
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = HubCyan.copy(alpha = 0.2f))
        ) {
            Text("BACK TO HUB", color = HubCyan)
        }
    }
}

@Composable
private fun BondingCard(agent: AgentCatalyst) {
    val infiniteTransition = rememberInfiniteTransition(label = "bond")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "pulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, agent.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(agent.color.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(agent.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = agent.color,
                modifier = Modifier.graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                }
            )
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(agent.name, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Bond Level: ${agent.bondLevel}", fontSize = 12.sp, color = agent.color)
            
            Spacer(Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { agent.bondLevel / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = agent.color,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}
