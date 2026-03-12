package dev.aurakai.auraframefx.domains.aura.screens.chromacore

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.ThemeViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.service.Color as ThemeColor

/**
 * Instant Color Picker - Iconify-style instant theme switching
 * Tap a color chip -> instant app-wide color change (Android 12+ compatible)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstantColorPickerScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val currentColor by themeViewModel.color.collectAsState()

    // Preset color palette - cyberpunk neon noir theme
    val colorPalette = listOf(
        ThemeColor.BLUE to Color(0xFF00BFFF),     // Deep Sky Blue (default)
        ThemeColor.RED to Color(0xFFFF1744),      // Neon Red
        ThemeColor.GREEN to Color(0xFF00FF9C),    // Neon Green
        ThemeColor.BLUE to Color(0xFF00E5FF),     // Cyan
        ThemeColor.RED to Color(0xFFFF0099),      // Hot Pink
        ThemeColor.GREEN to Color(0xFF76FF03),    // Lime
        ThemeColor.BLUE to Color(0xFF651FFF),     // Deep Purple
        ThemeColor.RED to Color(0xFFFF6E40),      // Orange
        ThemeColor.GREEN to Color(0xFF1DE9B6),    // Teal
        ThemeColor.BLUE to Color(0xFF2979FF),     // Blue
        ThemeColor.RED to Color(0xFFD500F9),      // Magenta
        ThemeColor.GREEN to Color(0xFFEEFF41),    // Yellow
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Instant Color Picker",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            "Tap to apply • Android 12+ Material You",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Cyan
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Info Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "⚡ Instant Theme Switching",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Cyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap any color to instantly change the app theme. No root required on Android 12+. Changes apply immediately to all screens, status bars, and navigation bars.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Color Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(colorPalette) { (themeColor, displayColor) ->
                    ColorChip(
                        color = displayColor,
                        isSelected = themeColor == currentColor,
                        onClick = {
                            // INSTANT UPDATE - no delay, no confirmation
                            themeViewModel.setColor(themeColor)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Current Selection
            Text(
                text = "Current: ${currentColor.name}",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(scale),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun animateDpAsState(
    targetValue: androidx.compose.ui.unit.Dp,
    animationSpec: androidx.compose.animation.core.AnimationSpec<androidx.compose.ui.unit.Dp>
): State<androidx.compose.ui.unit.Dp> {
    return androidx.compose.animation.core.animateDpAsState(
        targetValue = targetValue,
        animationSpec = animationSpec
    )
}


