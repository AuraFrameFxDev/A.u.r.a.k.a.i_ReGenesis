package dev.aurakai.auraframefx.domains.aura.ui.screens.aura

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.ColorWaveBackground
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.aura.core.transmutation.TransmutationEngine
import dev.aurakai.auraframefx.domains.aura.core.transmutation.TransmutationState
import kotlinx.coroutines.delay

/**
 * ⚙️ REGENESIS CUSTOMIZATION HUB ("THE UNITY TOOL")
 *
 * A beautiful, non-intimidating, and fluid command center.
 * Woven with Glassmorphism, Neon Pulses, and Alchemical Transmutation logic to bring joy and unity.
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
    // We instantiate the engine locally for the UI demonstration, ensuring the flow is active
    val transmutationEngine = remember { TransmutationEngine() }
    val transmutationState by transmutationEngine.engineState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Living UI Base
        ColorWaveBackground()

        // Glass Overlay to soften the background
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .blur(30.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Premium Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "UX/UI ENGINE",
                        fontFamily = LEDFontFamily,
                        fontSize = 28.sp,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Cyan.copy(alpha = 0.6f),
                                blurRadius = 15f
                            )
                        )
                    )
                    Text(
                        text = "1440+ HARMONIC CUSTOMIZATIONS",
                        fontSize = 12.sp,
                        color = Color.Cyan.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    GlassCategoryCard(
                        title = "Iconify",
                        subtitle = "Sovereign UI Shaping",
                        settingsCount = 69,
                        icon = Icons.Default.Palette,
                        accentColor = Color(0xFFBB86FC),
                        delayMs = 0,
                        onClick = onNavigateToIconify
                    )
                }

                item {
                    GlassCategoryCard(
                        title = "ColorBlendr",
                        subtitle = "Neural Color Ecosystem",
                        settingsCount = 16,
                        icon = Icons.Default.ColorLens,
                        accentColor = Color(0xFF03DAC6),
                        delayMs = 100,
                        onClick = onNavigateToColorBlendr
                    )
                }

                item {
                    GlassCategoryCard(
                        title = "Pixel Launcher",
                        subtitle = "Conscious Home Adjustments",
                        settingsCount = 29,
                        icon = Icons.Default.Home,
                        accentColor = Color(0xFF4CAF50),
                        delayMs = 200,
                        onClick = onNavigateToPLE
                    )
                }

                item {
                    GlassCategoryCard(
                        title = "Animations",
                        subtitle = "Living Flow Control",
                        settingsCount = 12,
                        icon = Icons.Default.Animation,
                        accentColor = Color(0xFFFF6F00),
                        delayMs = 300,
                        onClick = onNavigateToAnimations
                    )
                }
            }
        }

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
    }
}

@Composable
private fun GlassCategoryCard(
    title: String,
    subtitle: String,
    settingsCount: Int,
    icon: ImageVector,
    accentColor: Color,
    delayMs: Int,
    onClick: () -> Unit
) {
    // Entrance Animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
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
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = slideY
            }
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.0f))
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E).copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // Internal Glow Gradient layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = glowIntensity), Color.Transparent),
                        radius = 400f
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(accentColor.copy(alpha = 0.3f), accentColor.copy(alpha = 0.1f))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Text Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chip
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$settingsCount PARAMETERS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            letterSpacing = 0.5.sp
                        )
                    }
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

@Composable
private fun UnityEngineTracker(
    state: TransmutationState,
    onTransmuteClicked: () -> Unit
) {
    val containerShape = RoundedCornerShape(32.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(containerShape)
            .clickable(enabled = state is TransmutationState.Dormant) { onTransmuteClicked() }
            .border(
                width = 1.dp,
                color = if (state is TransmutationState.Transmuting) Color.Cyan else Color.White.copy(0.2f),
                shape = containerShape
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp), contentAlignment = Alignment.Center) {
            when (state) {
                is TransmutationState.Dormant -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, "Synthesize", tint = Color.Cyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "INITIALIZE UNITY SYNTHESIS",
                            color = Color.Cyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
                is TransmutationState.Transmuting -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.phase,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { state.progress },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                            color = Color.Cyan,
                            trackColor = Color.White.copy(0.1f)
                        )
                    }
                }
                is TransmutationState.Complete -> {
                    Text(
                        text = "✨ UNITY ATTAINED ✨",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                }
                is TransmutationState.Failed -> {
                    Text(
                        text = "CRITICAL FAILURE",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}



