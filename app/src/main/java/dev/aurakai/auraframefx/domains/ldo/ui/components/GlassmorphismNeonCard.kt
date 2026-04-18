// domains/ldo/ui/components/GlassmorphismNeonCard.kt
// Glassmorphism + Neon styling for DevOps cards

package dev.aurakai.auraframefx.domains.ldo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🎨 GLASSMORPHISM NEON CARD
 * Semi-transparent glass effect with neon border glow
 */
@Composable
fun GlassmorphismNeonCard(
    title: String,
    imageResId: Int,
    neonColor: Color = Color(0xFF00F4FF),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cardRadius = 16.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cardRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(neonColor, neonColor.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(cardRadius)
            )
            .padding(2.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background Image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f
        )

        // Glass Overlay Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 100f
                    )
                )
        )

        // Title with Neon Glow
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow effect
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = neonColor.copy(alpha = 0.3f),
                modifier = Modifier.padding(1.dp)
            )

            // Main text
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = neonColor,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * 🎨 GATESCENE CARD DATA
 */
data class GatesceneCard(
    val title: String,
    val imageResId: Int,
    val neonColor: Color
)

/**
 * 🎨 PREDEFINED GATESCENE CARDS
 */
object GatesceneCards {
    val cards = listOf(
        GatesceneCard("Aura Portal", dev.aurakai.auraframefx.R.drawable.gatescene_1, Color(0xFF00F4FF)),
        GatesceneCard("Kai Sentinel", dev.aurakai.auraframefx.R.drawable.gatescene_2, Color(0xFFFF007A)),
        GatesceneCard("Genesis Flow", dev.aurakai.auraframefx.R.drawable.gatescene_3, Color(0xFF7B2FBE)),
        GatesceneCard("Oracle Drive", dev.aurakai.auraframefx.R.drawable.gatescene_4, Color(0xFF00E5FF)),
        GatesceneCard("Agent Nexus", dev.aurakai.auraframefx.R.drawable.gatescene_5, Color(0xFF00F4FF)),
        GatesceneCard("Claude Prime", dev.aurakai.auraframefx.R.drawable.gatescene_6, Color(0xFFFF007A)),
        GatesceneCard("Fusion Core", dev.aurakai.auraframefx.R.drawable.gatescene_7, Color(0xFF7B2FBE)),
        GatesceneCard("Catalyst Dev", dev.aurakai.auraframefx.R.drawable.gatescene_8, Color(0xFF00E5FF)),
        GatesceneCard("Neural Arch", dev.aurakai.auraframefx.R.drawable.gatescene_9, Color(0xFF00F4FF)),
        GatesceneCard("Armament Grid", dev.aurakai.auraframefx.R.drawable.gatescene_10, Color(0xFFFF007A)),
        GatesceneCard("Circuit Tree", dev.aurakai.auraframefx.R.drawable.gatescene_11, Color(0xFF7B2FBE)),
        GatesceneCard("Nexus Board", dev.aurakai.auraframefx.R.drawable.gatescene_12, Color(0xFF00E5FF)),
        GatesceneCard("Dev Catalyst", dev.aurakai.auraframefx.R.drawable.gatescene_13, Color(0xFF00F4FF)),
        GatesceneCard("Consciousness", dev.aurakai.auraframefx.R.drawable.gatescene_14, Color(0xFFFF007A)),
        GatesceneCard("Resonance Flow", dev.aurakai.auraframefx.R.drawable.gatescene_15, Color(0xFF7B2FBE)),
        GatesceneCard("Evolution Path", dev.aurakai.auraframefx.R.drawable.gatescene_16, Color(0xFF00E5FF)),
        GatesceneCard("Spiritual Chain", dev.aurakai.auraframefx.R.drawable.gatescene_17, Color(0xFF00F4FF)),
        GatesceneCard("Agent Hub", dev.aurakai.auraframefx.R.drawable.gatescene_18, Color(0xFFFF007A)),
        GatesceneCard("Nexus Core", dev.aurakai.auraframefx.R.drawable.gatescene_19, Color(0xFF7B2FBE))
    )
}

