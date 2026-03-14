package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.ui.components.NeuralLinkBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue

/**
 * Main gate navigation screen with horizontal pager and magical teleportation effects
 * Swipe between module gates and double-tap to enter with a magical transition
 */
/**
 * Displays the main gate navigation screen: a horizontal pager of gate cards with a glowing active card,
 * a magical particle background, and an enhanced page indicator showing nearby gates.
 *
 * The screen lets users swipe between gates and double-tap an active gate to navigate to its route.
 * Gates marked as "coming soon" are not navigable. If a gate is protected and the user is not authenticated,
 * navigation will direct the user to the login flow with a return destination.
 *
 * @param navController Controller used to perform navigation actions for gate selection and login flow.
 * @param modifier Optional modifier for the root container.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GateNavigationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Categorize gates
    val auraLabGates = GateConfigs.auraLabGates
    val mainModuleGates = GateConfigs.genesisCoreGates + GateConfigs.kaiGates + GateConfigs.agentNexusGates + GateConfigs.supportGates
    val allGates = auraLabGates + mainModuleGates

    val pagerState = rememberPagerState(pageCount = { allGates.size })
    val scope = rememberCoroutineScope()
    var isTransitioning by remember { mutableStateOf(false) }

    // Teleportation animation values
    val infiniteTransition = rememberInfiniteTransition(label = "gate_glow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f)) // Transparent background
    ) {
        // Magical particle background
        MagicalParticleField()

        // Main content without category tabs (cards have titles)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Horizontal pager for gate cards
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val config = allGates[page]
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Parallax effect
                            translationX = pageOffset * 100
                            alpha = 1f - (0.3f * pageOffset.absoluteValue)
                            scaleX = 0.9f + (0.1f * (1 - pageOffset.absoluteValue))
                            scaleY = 0.9f + (0.1f * (1 - pageOffset.absoluteValue))
                        }
                ) {
                    // Gate card with INSTANT teleportation
                    TeleportingGateCard(
                        config = config,
                        isActive = pagerState.currentPage == page,
                        glowIntensity = glowIntensity,
                        onDoubleTap = {
                            // Block "Coming Soon" gates
                            if (config.comingSoon) {
                                // TODO: Show Toast "Coming Soon!"
                                return@TeleportingGateCard
                            }

                            if (!isTransitioning) {
                                isTransitioning = true
                                scope.launch {
                                    try {
                                        // Check if gate requires authentication
                                        val protectedGates = setOf(
                                            "root_tools_toggles",
                                            "rom_tools",
                                            "oracle_drive",
                                            "xposed_panel",
                                            "sentinels_fortress"
                                        )

                                        // TODO: Implement actual authentication check with TokenManager
                                        // For now, allow all gates (user_preferences is always accessible)
                                        val isAuthenticated = true // Replace with: tokenManager.isAuthenticated

                                        if (protectedGates.contains(config.route) && !isAuthenticated) {
                                            // Navigate to login with return destination
                                            navController.navigate("login?returnTo=${config.route}") {
                                                launchSingleTop = true
                                            }
                                        } else {
                                            // INSTANT navigation
                                            navController.navigate(config.route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Timber.tag("GateNav").e(e, "Failed: ${config.route}")
                                    } finally {
                                        isTransitioning = false
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Enhanced page indicator with gate names
            GatePageIndicator(
                gates = allGates,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                pagerState = pagerState,
                navController = navController
            )
        }
    }
}

/**
 * Renders a gate card with active glow, hover/teleportation overlay and a double-tap entry action.
 *
 * Displays a radial glow behind the card when `isActive`, shows an intensified overlay while hovering,
 * and invokes `onDoubleTap` when the card is double-tapped.
 *
 * @param config Configuration for the gate (visuals, route, and flags).
 * @param isActive Whether this card is the currently focused/active page; enables glow and hover overlay.
 * @param glowIntensity Multiplier for the glow alpha (expected 0.0–1.0) to control glow strength.
 * @param onDoubleTap Callback invoked when the card is double-tapped (e.g., to navigate or enter the gate).
 * @param modifier Optional Compose modifier applied to the card container.
 */
@Composable
private fun TeleportingGateCard(
    config: GateConfig,
    isActive: Boolean,
    glowIntensity: Float,
    onDoubleTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovering by remember { mutableStateOf(false) }
    var showEnterOverlay by remember { mutableStateOf(false) }
    val hoverScale by animateFloatAsState(
        targetValue = if (isHovering) 1.02f else 1f,
        label = "hover_scale"
    )
    // When enter overlay triggers, auto hide after duration
    LaunchedEffect(showEnterOverlay) {
        if (showEnterOverlay) {
            // Hide shortly before navigation occurs (navigation delay is 800ms)
            delay(700)
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = hoverScale
                scaleY = hoverScale
            }
    ) {
        // Glow effect behind card
        if (isActive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                config.glowColor.copy(alpha = 0.3f * glowIntensity),
                                Color.Transparent
                            ),
                            radius = 500f
                        )
                    )
            )
        }

        // Direct GateCard without cheesy transitions
        GateCard(
            config = config,
            onDoubleTap = {
                onDoubleTap()
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // TODO: Implement hover detection for desktop
                }
        )

        // Teleportation effect overlay (visible during hover)
        if (isActive && isHovering) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                config.glowColor.copy(alpha = 0.5f),
                                Color.Transparent
                            ),
                            radius = 400f
                        )
                    )
            )
        }
    }
}

@Composable
private fun MagicalParticleField() {
    NeuralLinkBackground(
        speed = 0.5f,
        primaryColor = Color(0xFF00FFFF), // Cyan
        secondaryColor = Color(0xFF0000FF) // Blue
    )
}

/**
 * Renders a centered, enhanced page indicator for gate cards that shows nearby gate titles and allows quick navigation.
 *
 * Shows up to three gates around the current page; displays leading/trailing ellipses when there are more gates.
 * The active gate is displayed as a labeled pill and tapping it navigates to the gate's route unless the gate is marked
 * as coming soon. Inactive gates are shown as dots and tapping a dot animates the pager to that page.
 *
 * @param gates List of gate configurations to display in the indicator.
 * @param currentPage Index of the currently selected page.
 * @param modifier Modifier applied to the Row containing the indicator.
 * @param pagerState PagerState used to animate scrolling to a selected page.
 * @param navController NavController used to navigate into a gate when the active pill is tapped.
 */
@Composable
private fun GatePageIndicator(
    gates: List<GateConfig>,
    currentPage: Int,
    modifier: Modifier = Modifier,
    pagerState: androidx.compose.foundation.pager.PagerState,
    navController: NavController
) {
    val visibleGates = 3 // Number of gates to show in the indicator
    val startIndex = (currentPage - visibleGates / 2).coerceAtLeast(0)
    val endIndex = (startIndex + visibleGates).coerceAtMost(gates.size)
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show ellipsis if there are more gates before
        if (startIndex > 0) {
            Text(
                text = "...",
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Show visible gates
        for (i in startIndex until endIndex) {
            val isActive = i == currentPage
            val gate = gates[i]

            // Active gate indicator (larger and colored)
            if (isActive) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            color = gate.borderColor.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable {
                            // Navigate into the active gate on tap (unless it's coming soon)
                            if (!gate.comingSoon) {
                                try {
                                    navController.navigate(gate.route) {
                                        launchSingleTop = true
                                    }
                                } catch (e: Exception) {
                                    Timber.tag("GateNav").e(e, "Failed to navigate: ${gate.route}")
                                }
                            } else {
                                // TODO: Show a Snackbar/Toast informing user that feature is coming soon
                            }
                        }
                ) {
                    Text(
                        text = gate.title.uppercase(),
                        color = gate.borderColor,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            } else {
                // Inactive gate indicator (smaller and dimmer) - scroll pager when clicked
                Text(
                    text = "•",
                    color = gate.borderColor.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable {
                            scope.launch {
                                try {
                                    pagerState.animateScrollToPage(i)
                                } catch (e: Exception) {
                                    Timber.tag("GateNav").e(e, "Failed to scroll to page $i")
                                }
                            }
                        }
                )
            }
        }

        // Show ellipsis if there are more gates after
        if (endIndex < gates.size) {
            Text(
                text = "...",
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}