package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.*

/**
 * 🔒 LOCK SCREEN FORGE PANEL
 *
 * Clock styles, shortcuts, notifications, and unlock transitions.
 */

@Composable
fun LockScreenForgePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val lockConfig = uiState.lockScreenConfig

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "🔒 LOCK SCREEN FORGE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Clock styles, shortcuts, and unlock animations",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Clock Style Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFFF00FF)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "CLOCK STYLE",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(ClockStyle.values().size) { index ->
                        val style = ClockStyle.values()[index]
                        ClockStyleCard(
                            style = style,
                            isSelected = lockConfig.clockStyle == style,
                            onClick = { viewModel.setLockScreenClockStyle(style) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "LOCK SCREEN FEATURES",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Shortcuts Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Shortcut,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Quick Shortcuts", color = Color.White)
                            Text(
                                "Camera, flashlight, etc.",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = lockConfig.shortcutsEnabled,
                        onCheckedChange = { viewModel.setLockScreenShortcuts(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFF00FF),
                            checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notifications Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Transparent Notifications", color = Color.White)
                            Text(
                                "Glass effect behind notifications",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = lockConfig.notificationsTransparent,
                        onCheckedChange = { /* Toggle */ },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFF00FF),
                            checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Unlock Animation
        Text(
            "UNLOCK TRANSITION",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 120.dp)
        ) {
            val effects = listOf("Slide", "Fade", "Zoom", "CRT", "Holo", "Glitch")
            items(effects.size) { index ->
                val effect = effects[index]
                val isSelected = when (effect) {
                    "Slide" -> lockConfig.transitionEffect is dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.TransitionForgeEffect.Slide
                    "Fade" -> lockConfig.transitionEffect is dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.TransitionForgeEffect.Fade
                    "Zoom" -> lockConfig.transitionEffect is dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.TransitionForgeEffect.Zoom
                    "CRT" -> lockConfig.transitionEffect is dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.TransitionForgeEffect.CRT
                    "Holo" -> lockConfig.transitionEffect is dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.TransitionForgeEffect.Hologram
                    "Glitch" -> lockConfig.transitionEffect is dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.TransitionForgeEffect.Glitch
                    else -> false
                }
                TransitionChip(label = effect, isSelected = isSelected, onClick = { /* Set transition */ })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom Wallpaper for Lock Screen
        OutlinedButton(
            onClick = { /* Set lock screen wallpaper */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF00FF)),
            border = BorderStroke(1.dp, Color(0xFFFF00FF))
        ) {
            Icon(Icons.Default.Wallpaper, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SET DIFFERENT LOCK SCREEN WALLPAPER")
        }
    }
}

@Composable
private fun ClockStyleCard(
    style: ClockStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF00FF) else Color.Transparent,
        label = "border"
    )

    val styleName = style.name.replace("_", " ").lowercase()
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    val styleColor = when (style) {
        ClockStyle.DIGITAL -> Color(0xFF00E5FF)
        ClockStyle.ANALOG -> Color(0xFFFFA500)
        ClockStyle.MINIMAL -> Color(0xFFFFFFFF)
        ClockStyle.HOLOGRAM -> Color(0xFFBB86FC)
        ClockStyle.SOVEREIGN -> Color(0xFFFF00FF)
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFFFF00FF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Clock preview icon
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = styleColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    styleName,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
