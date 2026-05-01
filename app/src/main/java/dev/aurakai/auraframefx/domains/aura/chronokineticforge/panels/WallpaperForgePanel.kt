package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.RealitymorphismViewModel
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.components.ThreadsWovenFooter
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*

/**
 * 🖼️ WALLPAPER FORGE PANEL — Theme & Nebula Selector
 *
 * Manages system wallpapers with:
 * - Live wallpaper themes (Wave, Lava, Starfield, Synaptic Web)
 * - Custom upload + AI generation
 * - Parallax, rotation, depth controls
 */

@Composable
fun WallpaperForgePanel(
    viewModel: RealitymorphismViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTheme by remember { mutableStateOf("Synaptic Web") }
    var showCustomUpload by remember { mutableStateOf(false) }

    val themes = listOf(
        WallpaperTheme("Wave", Color(0xFF00E5FF), Icons.Default.Waves),
        WallpaperTheme("Lava", Color(0xFFFF6B6B), Icons.Default.LocalFireDepartment),
        WallpaperTheme("Starfield", Color(0xFF6B5B95), Icons.Default.Star),
        WallpaperTheme("Synaptic Web", Color(0xFFFF00FF), Icons.Default.Hub),
        WallpaperTheme("Neural Bloodstream", Color(0xFF39FF14), Icons.Default.Bloodtype),
        WallpaperTheme("Digital Rain", Color(0xFF00FF00), Icons.Default.WaterDrop)
    )

    ForgePanelContainer(title = "WALLPAPER ENGINE") {
        Column(modifier = Modifier.padding(16.dp)) {
            // Theme carousel
            Text(
                text = "SELECT THEME",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(themes) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.name == selectedTheme,
                        onClick = {
                            selectedTheme = theme.name
                            WallpaperForgeEngine.applySystem(theme.name)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFF00FF).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                // Live wallpaper preview
                WallpaperPreview(theme = selectedTheme)

                // Theme label
                Text(
                    text = selectedTheme.uppercase(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = themes.find { it.name == selectedTheme }?.color ?: Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom upload + AI Gen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showCustomUpload = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF00E5FF)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("UPLOAD")
                }

                Button(
                    onClick = { /* AI Generation */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF00FF)
                    )
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI GENERATE")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sliders: Parallax, Rotation, Depth
            SliderRow(
                label = "Parallax Intensity",
                value = uiState.backgroundConfig.parallaxIntensity,
                range = 0f..2f,
                onValueChange = { viewModel.updateParallax(it) }
            )

            SliderRow(
                label = "Rotation Speed",
                value = uiState.backgroundConfig.rotationSpeed,
                range = 0f..1f,
                onValueChange = { viewModel.updateRotation(it) }
            )

            SliderRow(
                label = "Depth Layers",
                value = uiState.backgroundConfig.depthLayers.toFloat(),
                range = 1f..5f,
                onValueChange = { viewModel.updateDepth(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Threads Woven Footer
            ThreadsWovenFooter()
        }
    }

    if (showCustomUpload) {
        CustomUploadDialog(
            onDismiss = { showCustomUpload = false },
            onUpload = { uri ->
                viewModel.uploadCustomWallpaper(uri)
                showCustomUpload = false
            }
        )
    }
}

@Composable
private fun ThemeCard(
    theme: WallpaperTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) theme.color else Color.Transparent,
        label = "border"
    )

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.3f else 0.1f,
        label = "bg"
    )

    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(theme.color.copy(alpha = backgroundAlpha), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = theme.icon,
            contentDescription = theme.name,
            tint = theme.color,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = theme.name,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
private fun WallpaperPreview(theme: String) {
    // Animated preview based on theme
    val infiniteTransition = rememberInfiniteTransition(label = "preview")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Theme-specific preview rendering
        when (theme) {
            "Wave" -> {
                // Animated wave gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF).copy(alpha = 0.3f),
                                    Color(0xFF00E5FF).copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                startY = offset * 1000f
                            )
                        )
                )
            }

            "Lava" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF6B6B).copy(alpha = 0.5f),
                                    Color(0xFFFF00FF).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            "Starfield" -> {
                // Simulated stars
                Canvas(modifier = Modifier.fillMaxSize()) {
                    repeat(20) { i ->
                        val x = (i * 37f) % size.width
                        val y = (i * 73f + offset * 200f) % size.height
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = 2f,
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
            }

            else -> {
                // Default neural pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0A0A1A))
                )
            }
        }
    }
}

@Composable
private fun CustomUploadDialog(
    onDismiss: () -> Unit,
    onUpload: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "📤 Upload Custom Wallpaper",
                color = Color(0xFF00E5FF),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Select an image from your gallery to use as wallpaper:",
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onUpload("content://gallery") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF)
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OPEN GALLERY")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF0A0A1A)
    )
}

// Data classes
data class WallpaperTheme(
    val name: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// Placeholder WallpaperForgeEngine
object WallpaperForgeEngine {
    fun applySystem(theme: String) {}
}
