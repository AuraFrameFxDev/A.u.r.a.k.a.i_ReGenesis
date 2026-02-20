package dev.aurakai.auraframefx.domains.aura.aura.ui

// Genesis-OS Cyberpunk UI Component
// Implements TODO items: cyberpunk theming, animations, and visual effects

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Genesis-OS Cyberpunk Menu Item Component
 *
 * A highly styled, animated menu item designed for the Genesis-OS cyberpunk aesthetic.
 * Features smooth color transitions, glow effects, and dynamic visual feedback.
 *
 * @param text The label to display on the menu item
 * @param onClick The action to perform when the menu item is clicked
 * @param modifier Modifier to adjust the layout or appearance of the menu item
 * @param isSelected Whether the menu item is currently selected, affecting its visual typography
 */
@Composable
fun CyberMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
) {
    // Cyberpunk color scheme
    val primaryCyan = Color(0xFF00F5FF)
    val secondaryCyan = Color(0xFF40E0D0)
    val darkPurple = Color(0xFF1A0B2E)
    val neonPink = Color(0xFFFF006E)
    val darkBg = Color(0xFF0A0A0A)

    // Animated values for smooth transitions
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.8f else 0.1f,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundAlpha"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) primaryCyan else Color(0xFFB0B0B0),
        animationSpec = tween(durationMillis = 200),
        label = "textColor"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.3f,
        animationSpec = tween(durationMillis = 250),
        label = "borderAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = primaryCyan.copy(alpha = 0.3f),
                spotColor = primaryCyan.copy(alpha = 0.5f)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isSelected) {
                        listOf(
                            darkPurple.copy(alpha = backgroundAlpha),
                            darkBg.copy(alpha = backgroundAlpha * 0.8f)
                        )
                    } else {
                        listOf(
                            darkBg.copy(alpha = backgroundAlpha),
                            Color.Transparent
                        )
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) primaryCyan.copy(alpha = borderAlpha)
                else secondaryCyan.copy(alpha = borderAlpha * 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.alpha(
                if (isSelected) 1f else 0.85f
            )
        )

        // Cyberpunk glow effect indicator for selected items
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    .alpha(0.6f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                primaryCyan.copy(alpha = 0.1f),
                                Color.Transparent,
                                neonPink.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
        }
    }
}
