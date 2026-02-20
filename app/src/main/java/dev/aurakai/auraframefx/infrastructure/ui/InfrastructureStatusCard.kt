package dev.aurakai.auraframefx.infrastructure.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.infrastructure.core.Capability

/**
 * 📊 INFRASTRUCTURE STATUS CARD
 * 
 * Premium UI component for monitoring the 3 critical arteries.
 */
@Composable
fun InfrastructureStatusCard(
    capability: Capability,
    isAvailable: Boolean,
    statusMessage: String,
    modifier: Modifier = Modifier
) {
    val accentColor = when (capability) {
        Capability.XPOSED_HOOKS -> Color(0xFFBB86FC) // Purple
        Capability.SHIZUKU_API -> Color(0xFF03DAC6)  // Teal
        Capability.CORE_BACKEND -> Color(0xFFFFD700) // Gold
        Capability.NEURAL_LINK -> Color(0xFF00B0FF)  // Cyan
        Capability.ROOT_ACCESS -> Color(0xFFFF3D00)  // Red
    }

    val icon = when (capability) {
        Capability.XPOSED_HOOKS -> Icons.Default.Extension
        Capability.SHIZUKU_API -> Icons.Default.Bolt
        Capability.CORE_BACKEND -> Icons.Default.Storage
        Capability.NEURAL_LINK -> Icons.Default.Hub
        Capability.ROOT_ACCESS -> Icons.Default.Shield
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Indicator Icon
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text Content
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = capability.name.replace("_", " "),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = statusMessage,
                        color = if (isAvailable) Color.LightGray else Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Global Status Dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isAvailable) Color(0xFF00FF88) else Color(0xFFFF4B4B),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}
