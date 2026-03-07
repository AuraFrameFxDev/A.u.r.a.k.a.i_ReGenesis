package dev.aurakai.auraframefx.ui.gates

// ═══════════════════════════════════════════════════════════════════════════════
// ImmersiveGateCard.kt
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// DESIGN: No frames. No borders. The ART is the gate.
// Full-bleed artwork with subtle gradient overlays for legibility.
// Double-tap to enter — press-hold for preview.
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

/**
 * ImmersiveGateCard — the replacement for bordered GateCard.
 *
 * The art handles ALL visual framing. No stroke borders, no corner chrome.
 * Just the artwork, a subtle atmospheric gradient, and minimal HUD text.
 *
 * Drop-in replacement: same [GateConfig] data, same onDoubleTap callback.
 */
@Composable
fun ImmersiveGateCard(
    config: GateConfig,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit = {}
) {
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var pressed by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "immersive_gate")

    // Subtle breathe effect on the glow overlay — not the border
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Particle shimmer — horizontal scan line
    val scanLine by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan"
    )

    // Press scale
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press_scale"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(150)
            pressed = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = {
                        val now = System.currentTimeMillis()
                        if (now - lastTapTime < 350) onDoubleTap()
                        lastTapTime = now
                    }
                )
            }
    ) {
        // ── LAYER 1: Full-bleed artwork ──────────────────────────────────────
        if (config.pixelArtResId != null) {
            Image(
                painter = painterResource(id = config.pixelArtResId),
                contentDescription = config.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (config.pixelArtUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            ) {
                Text(
                    text = config.title, 
                    color = Color.White, 
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            // Fallback gradient when no art asset yet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                config.glowColor.copy(alpha = 0.3f),
                                config.backgroundColor
                            )
                        )
                    )
            )
        }

        // ── LAYER 2: Atmospheric depth gradient (bottom-heavy, non-blocking) ─
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.45f to Color.Transparent,
                            0.72f to Color(0x55000000),
                            1.0f to Color(0xCC000000)
                        )
                    )
                )
        )

        // ── LAYER 3: Glow pulse from dominant color (breathes, doesn't frame) ─
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            config.glowColor.copy(alpha = breathe),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.4f),
                        radius = Float.POSITIVE_INFINITY
                    )
                )
        )

        // ── LAYER 4: Scan line (subtle data stream feel) ──────────────────────
        if (!config.comingSoon) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopStart)
                    .offset(y = (scanLine * 1000).dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                config.glowColor.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // ── LAYER 5: HUD text overlay (minimal — the art already says who this is) ─
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Gate label — small, uppercase, glow color
            if (config.title.isNotBlank()) {
                Text(
                    text = config.title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = config.glowColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Description micro-text
            if (config.description.isNotBlank()) {
                Text(
                    text = config.description,
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Coming soon tag
            if (config.comingSoon) {
                Text(
                    text = "COMING SOON",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = config.glowColor.copy(alpha = 0.7f)
                )
            }
        }

        // ── LAYER 6: Double-tap hint (top-right corner, fades in) ────────────
        Text(
            text = "▶▶",
            fontSize = 10.sp,
            color = config.glowColor.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        )
    }
}
