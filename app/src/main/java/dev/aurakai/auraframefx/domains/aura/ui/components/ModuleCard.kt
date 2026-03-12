package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 📦 Module Card Component
 *
 * Glass card displaying an app module with:
 * - Minimal icon
 * - Module name
 * - Brief description
 * - Interactive click handling
 */

/**
 * Module data class
 */
data class AppModule(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color = Color.White,
    val cardStyle: GlassCardStyle = GlassCardStyles.Default,
    val enabled: Boolean = true
)

/**
 * 📦 Module Card Composable
 *
 * Usage:
 * ```
 * ModuleCard(
 *     module = AppModule(
 *         id = "lsposed",
 *         name = "LSPosed Hooks",
 *         description = "System-level UI injection and modification",
 *         icon = Icons.Default.Build
 *     ),
 *     onClick = { /* navigate to module */ }
 * )
 * ```
 */
@Composable
fun ModuleCard(
    module: AppModule,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        style = module.cardStyle,
        onClick = if (module.enabled) onClick else null
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = module.name,
                    modifier = Modifier.size(48.dp),
                    tint = if (module.enabled) module.iconColor else module.iconColor.copy(alpha = 0.3f)
                )
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Module name
                Text(
                    text = module.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (module.enabled) Color.White else Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                Text(
                    text = module.description,
                    fontSize = 13.sp,
                    color = if (module.enabled) Color.White.copy(alpha = 0.7f) else Color.White.copy(
                        alpha = 0.3f
                    ),
                    lineHeight = 16.sp
                )

                // Status indicator
                if (!module.enabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Coming Soon",
                        fontSize = 11.sp,
                        color = Color(0xFFFF00FF).copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * 🎯 Compact Module Card
 *
 * Smaller version for grid layouts
 */
@Composable
fun CompactModuleCard(
    module: AppModule,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    GlassCard(
        modifier = modifier
            .width(160.dp)
            .height(160.dp),
        style = module.cardStyle,
        onClick = if (module.enabled) onClick else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Icon(
                imageVector = module.icon,
                contentDescription = module.name,
                modifier = Modifier.size(48.dp),
                tint = if (module.enabled) module.iconColor else module.iconColor.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Module name
            Text(
                text = module.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (module.enabled) Color.White else Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = module.description,
                fontSize = 11.sp,
                color = if (module.enabled) Color.White.copy(alpha = 0.6f) else Color.White.copy(
                    alpha = 0.3f
                ),
                maxLines = 2,
                lineHeight = 13.sp
            )
        }
    }
}

/**
 * 🌟 Featured Module Card
 *
 * Large hero card for highlighting a module
 */
@Composable
fun FeaturedModuleCard(
    module: AppModule,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    GradientGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        gradientColors = listOf(
            module.cardStyle.borderGradient[0].copy(alpha = 0.3f),
            module.cardStyle.borderGradient[1].copy(alpha = 0.1f)
        ),
        borderGradient = module.cardStyle.borderGradient,
        onClick = if (module.enabled) onClick else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = module.name,
                        modifier = Modifier.size(64.dp),
                        tint = module.iconColor
                    )
                }

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = module.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = module.description,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

/**
 * 📊 Module Category Header
 */
@Composable
fun ModuleCategoryHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                tint = Color(0xFFFF00FF)
            )
        }

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

