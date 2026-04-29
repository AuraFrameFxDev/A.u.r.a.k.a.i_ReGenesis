package dev.aurakai.auraframefx.domains.aura.ui.screens.aura

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.ColorWaveBackground
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlinx.coroutines.delay

/**
 * âš™ï¸ REGENESIS CUSTOMIZATION HUB
 *
 * Central command for all system-level customizations including:
 * - Iconify (Mahmud0808)
 * - ColorBlendr (Mahmud0808)
 * - PixelLauncherEnhanced (Mahmud0808)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisCustomizationHub(
    onNavigateBack: () -> Unit,
    onNavigateToIconify: () -> Unit,
    onNavigateToColorBlendr: () -> Unit,
    onNavigateToPLE: () -> Unit,
    onNavigateToAnimations: () -> Unit
) {
    // Handle hardware back button
    BackHandler(onBack = onNavigateBack)

    Box(modifier = Modifier.fillMaxSize()) {
        ColorWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "UX/UI ENGINE",
                    fontFamily = LEDFontFamily,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            Text(
                text = "1440+ SYSTEM CUSTOMIZATIONS",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 56.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    CategoryCard(
                        title = "Iconify",
                        subtitle = "UI Customization Engine",
                        settingsCount = 69, // Using hardcoded or summary counts
                        icon = Icons.Default.Palette,
                        accentColor = Color(0xFFBB86FC),
                        onClick = onNavigateToIconify
                    )
                }

                item {
                    CategoryCard(
                        title = "ColorBlendr",
                        subtitle = "Material You Color Engine",
                        settingsCount = 16,
                        icon = Icons.Default.ColorLens,
                        accentColor = Color(0xFF6200EE),
                        onClick = onNavigateToColorBlendr
                    )
                }

                item {
                    CategoryCard(
                        title = "Pixel Launcher",
                        subtitle = "Launcher Enhancements",
                        settingsCount = 29,
                        icon = Icons.Default.Home,
                        accentColor = Color(0xFF4CAF50),
                        onClick = onNavigateToPLE
                    )
                }

                item {
                    CategoryCard(
                        title = "Animations",
                        subtitle = "Lock Screen & System Animations",
                        settingsCount = 12,
                        icon = Icons.Default.Animation,
                        accentColor = Color(0xFFFF6F00),
                        onClick = onNavigateToAnimations
                    )
                }
            }
        }

/*
        // Floating Unity Transmutation Action
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            UnityEngineTracker(
                state = transmutationState,
                onTransmuteClicked = {
                    transmutationEngine.transmuteCatalysts(listOf("UX_INTENT", "HAPTIC_SYNTH", "COLOR_SOUL"))
                }
            )
        }
        */
    }
}

@Composable
private fun CategoryCard(
    title: String,
    subtitle: String,
    settingsCount: Int,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    // Entrance Animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val slideY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "slideY"
    )

    // Breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glowIntensity"
    )

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.2f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$settingsCount SETTINGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}





