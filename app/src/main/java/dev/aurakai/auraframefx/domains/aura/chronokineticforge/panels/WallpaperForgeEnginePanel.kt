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
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.RealitymorphismViewModel
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.*
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*

/**
 * 🌌 WALLPAPER FORGE ENGINE PANEL
 *
 * Live wallpapers, system wallpaper integration, animated backgrounds.
 */

@Composable
fun WallpaperForgeEnginePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "🌌 WALLPAPER FORGE ENGINE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Live wallpapers, parallax, and system integration",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Live Wallpapers Grid
        Text(
            "LIVE WALLPAPERS",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.heightIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(AnimatedBackgroundTheme.values().size) { index ->
                val theme = AnimatedBackgroundTheme.values()[index]
                LiveWallpaperCard(
                    theme = theme,
                    isSelected = uiState.backgroundConfig.animatedTheme == theme
                            && uiState.backgroundConfig.type == BackgroundType.Animated,
                    onClick = { viewModel.setAnimatedTheme(theme) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Gradient Themes
        Text(
            "GRADIENT THEMES",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.heightIn(max = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(GradientTheme.values().size) { index ->
                val theme = GradientTheme.values()[index]
                GradientThemeChip(
                    theme = theme,
                    isSelected = uiState.backgroundConfig.gradientTheme == theme
                            && uiState.backgroundConfig.type == BackgroundType.Gradient,
                    onClick = {
                        viewModel.setGradientTheme(theme)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom Upload
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "CUSTOM WALLPAPER",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF00E5FF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Upload */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF))
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("UPLOAD PNG/JPG")
                    }

                    OutlinedButton(
                        onClick = { /* AI Gen */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF00FF)),
                        border = BorderStroke(1.dp, Color(0xFFFF00FF))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI GENERATE")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Parallax Settings
        ForgeSlider(
            label = "Parallax Strength",
            value = uiState.backgroundConfig.parallaxStrength,
            onValueChange = { viewModel.setParallax(uiState.backgroundConfig.parallaxEnabled, it) },
            valueRange = 0f..1f
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Parallax", color = Color.White)
            Switch(
                checked = uiState.backgroundConfig.parallaxEnabled,
                onCheckedChange = { viewModel.setParallax(it, uiState.backgroundConfig.parallaxStrength) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFF00FF),
                    checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wallpaper in Apps
        Text(
            "WALLPAPER IN APPS",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WallpaperInAppChip(label = "Never", selected = false, onClick = {})
            WallpaperInAppChip(label = "Blurred", selected = true, onClick = {})
            WallpaperInAppChip(label = "Full Opacity", selected = false, onClick = {})
        }
    }
}

@Composable
private fun LiveWallpaperCard(
    theme: AnimatedBackgroundTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF00FF) else Color.Transparent,
        label = "border"
    )

    val themeName = theme.name.replace("_", " ").lowercase()
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    Card(
        modifier = Modifier
            .aspectRatio(0.75f)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when (theme) {
                AnimatedBackgroundTheme.STARFIELDr -> Color(0xFF000011)
                AnimatedBackgroundTheme.LAVA_APOCALYPSE -> Color(0xFFB71C1C)
                AnimatedBackgroundTheme.ICY_TUNDRA -> Color(0xFFE0F7FA)
                AnimatedBackgroundTheme.PAINT_SPLASH -> Color(0xFFFF00FF)
                AnimatedBackgroundTheme.DATA_RIBBONS -> Color(0xFF006064)
                AnimatedBackgroundTheme.HEXAGON_GRID -> Color(0xFF004D40)
                AnimatedBackgroundTheme.NEURAL_LINK -> Color(0xFF1A0033)
                AnimatedBackgroundTheme.SYNAPTIC_WEB -> Color(0xFF3700B3)
                AnimatedBackgroundTheme.DIGITAL_LANDSCAPE -> Color(0xFF004D40)
                AnimatedBackgroundTheme.CYBERPUNK -> Color(0xFF000000)
                AnimatedBackgroundTheme.BIOMED -> Color(0xFF00E676)
                AnimatedBackgroundTheme.DATA_VISUALIZATION -> Color(0xFF2962FF)
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
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

            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            ) {
                Text(
                    themeName,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun GradientThemeChip(
    theme: GradientTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = when (theme) {
        GradientTheme.AURA_MAGENTA -> listOf(Color(0xFFFF00FF), Color(0xFF6200EE))
        GradientTheme.KAI_CYAN -> listOf(Color(0xFF00E5FF), Color(0xFF006064))
        GradientTheme.GENESIS_PURPLE -> listOf(Color(0xFFBB86FC), Color(0xFF3700B3))
        GradientTheme.NEXUS_DEEP_BLUE -> listOf(Color(0xFF2962FF), Color(0xFF000051))
        GradientTheme.SUNSET_ORANGE -> listOf(Color(0xFFFF6F00), Color(0xFFB71C1C))
        GradientTheme.FOREST_GREEN -> listOf(Color(0xFF00E676), Color(0xFF1B5E20))
        GradientTheme.VOLCANIC_RED -> listOf(Color(0xFFFF1744), Color(0xFF3E2723))
        GradientTheme.AURORA -> listOf(Color(0xFF18FFFF), Color(0xFFE040FB), Color(0xFFFF00FF))
    }

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF00FF) else Color.Transparent,
        label = "border"
    )

    val primaryColor = colors.first()

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = primaryColor.copy(alpha = 0.3f),
        border = BorderStroke(2.dp, borderColor),
        modifier = Modifier.height(48.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                theme.name.split("_").first().lowercase()
                    .replaceFirstChar { it.uppercase() },
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun WallpaperInAppChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFFFF00FF).copy(alpha = 0.3f) else Color(0xFF2A2A2A),
        border = BorderStroke(1.dp, if (selected) Color(0xFFFF00FF) else Color.Transparent)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color(0xFFFF00FF) else Color.White
        )
    }
}
