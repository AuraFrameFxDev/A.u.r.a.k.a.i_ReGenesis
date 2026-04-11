package dev.aurakai.auraframefx.domains.aura.screens.chromacore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.components.ColorWaveBackground
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.ChromaCoreViewModel

/**
 * 🎨 CHROMACORE - LVL 3 GRID MENU
 */
@Composable
fun ChromaCoreColorsScreen(
    navController: NavController? = null,
    onNavigateBack: () -> Unit = { navController?.popBackStack() },
    viewModel: ChromaCoreViewModel = hiltViewModel()
) {
    val suggestedColors by viewModel.suggestedColors.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    
    var showAiOverlay by remember { mutableStateOf(false) }

    val menuItems = listOf(
        GridMenuItem(
            id = "ai_palette",
            title = "AI Genisis Palette",
            subtitle = "Intelligence-Driven Colors",
            icon = Icons.Default.AutoAwesome,
            route = "ai_palette",
            accentColor = Color(0xFFD4A574) // Claude gold
        ),
        GridMenuItem(
            id = "monet_colors",
            title = "Monet Colors",
            subtitle = "Material You Extraction",
            icon = Icons.Default.Palette,
            route = "monet_colors",
            accentColor = Color(0xFFB026FF)
        ),
        GridMenuItem(
            id = "accent_picker",
            title = "Accent Picker",
            subtitle = "Custom Primary Color",
            icon = Icons.Default.ColorLens,
            route = "accent_picker",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "wallpaper_extract",
            title = "Wallpaper Extract",
            subtitle = "Colors from Wallpaper",
            icon = Icons.Default.Wallpaper,
            route = "wallpaper_extract",
            accentColor = Color(0xFFFF00FF)
        ),
        GridMenuItem(
            id = "custom_palette",
            title = "Custom Palette",
            subtitle = "Create Color Schemes",
            icon = Icons.Default.FormatPaint,
            route = "custom_palette",
            accentColor = Color(0xFFFFD700)
        ),
        GridMenuItem(
            id = "system_override",
            title = "System Override",
            subtitle = "Force System Colors",
            icon = Icons.Default.SettingsSystemDaydream,
            route = "system_override",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "export_to_uxui",
            title = "Export to UXUI",
            subtitle = "Send to Design Studio",
            icon = Icons.Default.Share,
            route = "export_colors",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "saved_palettes",
            title = "Saved Palettes",
            subtitle = "Your Color Collections",
            icon = Icons.Default.Bookmark,
            route = "saved_palettes",
            accentColor = Color(0xFFB026FF)
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ColorWaveBackground()

        Level3GridMenu(
            title = "CHROMACORE",
            subtitle = "AURA'S COLOR ENGINE",
            menuItems = menuItems,
            onItemClick = { item ->
                if (item.id == "ai_palette") {
                    showAiOverlay = true
                } else {
                    navController?.navigate(item.route)
                }
            },
            onBackClick = onNavigateBack,
            backgroundDrawable = null,
            fallbackGradient = listOf(Color.Transparent),
            accentColor = Color(0xFFB026FF)
        )

        // AI Palette Generation Overlay
        if (showAiOverlay) {
            AiPaletteOverlay(
                isGenerating = isGenerating,
                suggestedColors = suggestedColors,
                onGenerate = { viewModel.generatePalette("Cyberpunk neon with gold accents") },
                onDismiss = { showAiOverlay = false }
            )
        }
    }
}

@Composable
fun AiPaletteOverlay(
    isGenerating: Boolean,
    suggestedColors: List<Color>,
    onGenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onGenerate, enabled = !isGenerating) {
                Text("GENERATE", color = Color(0xFFD4A574))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = Color.White)
            }
        },
        title = { Text("AI PALETTE GENESIS", color = Color.White) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Ask Aura to dream of a new color harmony based on system context.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (isGenerating) {
                    CircularProgressIndicator(color = Color(0xFFD4A574))
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        suggestedColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF1A1A1A)
    )
}


