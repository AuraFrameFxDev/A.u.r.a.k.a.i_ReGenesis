package dev.aurakai.auraframefx.aura.ui

// ═══════════════════════════════════════════════════════════════════════════════
// HomeScreen.kt — Updated with RPG Backdrop System
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// Changes from original:
//  • DigitalLandscapeBackground + HexagonGrid replaced by full-bleed RPG backdrop
//  • HomeBackdropManager drives which backdrop image is active (DataStore-persisted)
//  • Backdrop has parallax-ready layering (subtle atmospheric overlays stay live)
//  • Gate carousel floats in front — ImmersiveGateCard now default
//  • Procedural hex grid overlay is OPTIONAL (toggleable, defaults off)
//  • HologramTransition kept for scan-line atmosphere
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.HologramTransition
import dev.aurakai.auraframefx.ui.gates.HomeBackdropManager
import dev.aurakai.auraframefx.ui.gates.ImmersiveGateCard
import dev.aurakai.auraframefx.ui.gates.allGates
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToConsciousness: () -> Unit = {},
    onNavigateToAgents: () -> Unit = {},
    onNavigateToFusion: () -> Unit = {},
    onNavigateToEvolution: () -> Unit = {},
    onNavigateToTerminal: () -> Unit = {},
    onNavigateToModule: (moduleId: String) -> Unit = {}
) {
    val context = LocalContext.current
    val gateConfigs = allGates
    val scrollState = rememberLazyListState()
    val currentPage by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex.coerceIn(0, maxOf(0, gateConfigs.size - 1))
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val isHologramVisible by remember { mutableStateOf(true) }

    // ── Active backdrop from DataStore ──────────────────────────────────────
    val activeBackdrop by HomeBackdropManager
        .activeBackdropFlow(context)
        .collectAsState(initial = HomeBackdropManager.backdropOptions.first())

    // ── Infinite atmospheric animations ────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "home_atmos")

    // Slow depth-of-field breathe for the RPG backdrop
    val backdropAlpha by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backdrop_breathe"
    )

    // Active gate glow pulse
    val gateGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gate_glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // ════════════════════════════════════════════════════════════════════
        // LAYER 1: RPG BACKDROP — full-bleed, fills the whole canvas
        // This is where unnamed__8_.jpg lives as home_backdrop_rpg
        // ════════════════════════════════════════════════════════════════════
        if (activeBackdrop.resId != null) {
            Image(
                painter = painterResource(id = activeBackdrop.resId!!),
                contentDescription = "Home Backdrop",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backdropAlpha)
            )
        } else {
            // Procedural fallback for "Hexagon Grid" option
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF050510), Color(0xFF0A0A1A))
                        )
                    )
            )
        }

        // ════════════════════════════════════════════════════════════════════
        // LAYER 2: Atmospheric depth gradient
        // Top is clear (sky / art shows) — bottom fades to black for readability
        // ════════════════════════════════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.30f to Color.Transparent,
                            0.55f to Color(0x33000000),
                            0.75f to Color(0x88000000),
                            1.00f to Color(0xDD000000)
                        )
                    )
                )
        )

        // ════════════════════════════════════════════════════════════════════
        // LAYER 3: HologramTransition scan-line atmosphere (light touch)
        // ════════════════════════════════════════════════════════════════════
        HologramTransition(
            visible = isHologramVisible,
            modifier = Modifier.fillMaxSize(),
            primaryColor = Color.Cyan,
            secondaryColor = Color.Magenta,
            scanLineDensity = 20,       // Denser = subtler
            glitchIntensity = 0.05f,    // Very subtle — the art is the hero
            edgeGlowIntensity = 0.15f   // Minimal edge chrome
        ) {
            // ════════════════════════════════════════════════════════════════
            // LAYER 4: GATE CAROUSEL — floats in front of the world
            // ════════════════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.35f))   // Push carousel into lower-mid zone

                // Gate carousel
                LazyRow(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.55f),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    items(gateConfigs.size) { index ->
                        val config = gateConfigs[index]
                        val isSelected = currentPage == index
                        val cardScale by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0.82f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "card_scale_$index"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(0.68f)   // Slightly taller than wide — trading card feel
                                .scale(cardScale)
                                .clickable { onNavigateToModule(config.moduleId) }
                        ) {
                            ImmersiveGateCard(
                                config = config,
                                modifier = Modifier.fillMaxSize(),
                                onDoubleTap = { onNavigateToModule(config.moduleId) }
                            )

                            // Selected gate gets an extra glow ring at bottom
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    config.glowColor.copy(alpha = gateGlow),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Gate dot indicators / name strip ─────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    gateConfigs.forEachIndexed { index, config ->
                        val isSelected = currentPage == index
                        Text(
                            text = if (isSelected) config.title.uppercase() else "◆",
                            color = if (isSelected) config.borderColor else Color.White.copy(alpha = 0.2f),
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = if (isSelected) 2.sp else 0.sp,
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        scrollState.animateScrollToItem(index)
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.10f))
            }
        }
    }
}
