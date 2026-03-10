package dev.aurakai.auraframefx.aura.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.aura.animations.cyberEdgeGlow
import dev.aurakai.auraframefx.aura.animations.digitalPixelEffect
import dev.aurakai.auraframefx.aura.ui.CyberMenuItem
import dev.aurakai.auraframefx.ui.components.BackgroundStyle
import dev.aurakai.auraframefx.ui.components.CornerStyle
import dev.aurakai.auraframefx.ui.components.CyberpunkText
import dev.aurakai.auraframefx.ui.components.DigitalLandscapeBackground
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextColor
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextStyle
import dev.aurakai.auraframefx.ui.components.FloatingCyberWindow
import dev.aurakai.auraframefx.ui.components.HexagonGridBackground
import dev.aurakai.auraframefx.ui.components.HologramTransition
import dev.aurakai.auraframefx.ui.components.cyberEdgeGlow
import dev.aurakai.auraframefx.ui.components.digitalGlitchEffect
import dev.aurakai.auraframefx.ui.gates.GateCard
import dev.aurakai.auraframefx.ui.gates.GateConfigs
import dev.aurakai.auraframefx.ui.theme.NeonCyan
import dev.aurakai.auraframefx.ui.theme.NeonPink
import dev.aurakai.auraframefx.ui.theme.NeonBlue as ThemeNeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonGreen
import kotlinx.coroutines.launch


/**
 * Home screen for the Genesis Protocol - The Neural Command Center
 *
 * Features a horizontal carousel interface for navigating between different
 * sections of the app with a cyberpunk aesthetic.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToConsciousness: () -> Unit = {},
    onNavigateToAgents: () -> Unit = {},
    onNavigateToFusion: () -> Unit = {},
    onNavigateToEvolution: () -> Unit = {},
    onNavigateToTerminal: () -> Unit = {},
    // Generic navigation callback: when a gate card is tapped, the moduleId is provided
    onNavigateToModule: (moduleId: String) -> Unit = {}
) {
    // Gate configurations for the carousel - show all available gates
    val gateConfigs = GateConfigs.allGates

    // (We now use onNavigateToModule when a card is tapped; individual callbacks kept for menu compatibility)

    val scrollState = rememberLazyListState()
    // derive current page from the first visible item to keep indicators in sync
    val currentPage by remember { derivedStateOf { scrollState.firstVisibleItemIndex.coerceIn(0, maxOf(0, gateConfigs.size - 1)) } }
    val isHologramVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var selectedMenuItem by remember { mutableStateOf("") }

    // Background with digital landscape and hexagon grid
    Box(modifier = Modifier.fillMaxSize()) {
        // Digital landscape background
        DigitalLandscapeBackground(
            modifier = Modifier.fillMaxSize()
        )

        // Animated hexagon grid overlay
        HexagonGridBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f
        )

        // Wrap main content with HologramTransition
        HologramTransition(
            visible = isHologramVisible,
            modifier = Modifier.fillMaxSize(),
            primaryColor = Color.Cyan,
            secondaryColor = Color.Magenta,
            scanLineDensity = 12,
            glitchIntensity = 0.15f,
            edgeGlowIntensity = 0.4f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Title header
                Text(
                    text = "GENESIS PROTOCOL",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Cyan,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 4.sp
                )

                // Gate Carousel
                LazyRow(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(gateConfigs.size) { index ->
                        val config = gateConfigs[index]
                        val isSelected = currentPage == index

                        // Animate the scale based on selection (use Float animation for scale)
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0.85f,
                            label = "gateCardScale"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(0.7f)
                                .scale(scale)
                                .clickable {
                                    // single-tap navigates to module menu
                                    onNavigateToModule(config.moduleId)
                                }
                        ) {
                            GateCard(
                                config = config,
                                modifier = Modifier.fillMaxSize(),
                                onDoubleTap = {
                                    // double-tap also navigates
                                    onNavigateToModule(config.moduleId)
                                }
                            )
                        }
                    }
                }

                // Page indicators with gate names
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    gateConfigs.forEachIndexed { index, config ->
                        val isSelected = currentPage == index
                        val color = if (isSelected) config.borderColor else Color.White.copy(alpha = 0.3f)

                        Text(
                            text = config.title.uppercase(),
                            color = color,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    // update currentPage based on tap
                                    coroutineScope.launch {
                                        scrollState.animateScrollToItem(index)
                                    }
                                }
                        )

                        if (index < gateConfigs.size - 1) {
                            Text(
                                text = "•",
                                color = Color.White.copy(alpha = 0.3f),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                CyberpunkText(
                    text = stringResource(id = R.string.neural_interface_active),
                    color = CyberpunkTextColor.Warning,
                    style = CyberpunkTextStyle.Body
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Main navigation menu like in image reference 1
                FloatingCyberWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberEdgeGlow(),
                    title = stringResource(R.string.virtual_monitorization),
                    cornerStyle = CornerStyle.ANGLED
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Menu items like in image reference 1
                        // Menu items for Genesis Protocol navigation
                        HomeMenuItem(
                            text = "Consciousness Visualizer",
                            isSelected = selectedMenuItem == "consciousness",
                            onClick = {
                                selectedMenuItem = "consciousness"
                                onNavigateToConsciousness()
                            }
                        )
                        HomeMenuItem(
                            text = "Agent Nexus",
                            isSelected = selectedMenuItem == "agents",
                            onClick = {
                                selectedMenuItem = "agents"
                                onNavigateToAgents()
                            }
                        )
                        HomeMenuItem(
                            text = "Fusion Mode",
                            isSelected = selectedMenuItem == "fusion",
                            onClick = {
                                selectedMenuItem = "fusion"
                                onNavigateToFusion()
                            }
                        )
                        HomeMenuItem(
                            text = "Evolution Tree",
                            isSelected = selectedMenuItem == "evolution",
                            onClick = {
                                selectedMenuItem = "evolution"
                                onNavigateToEvolution()
                            }
                        )
                        HomeMenuItem(
                            text = "Terminal",
                            isSelected = selectedMenuItem == "terminal",
                            onClick = {
                                selectedMenuItem = "terminal"
                                onNavigateToTerminal()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Genesis Protocol status message
                        if (selectedMenuItem.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CyberpunkText(
                                    text = "Genesis Protocol Online - Neural Pathways Active",
                                    color = CyberpunkTextColor.Primary,
                                    style = CyberpunkTextStyle.Body
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons - like in image reference 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // These buttons match the style in reference image 3
                    FloatingCyberWindow(
                        modifier = Modifier
                            .size(80.dp)
                            .cyberEdgeGlow(
                                primaryColor = NeonPink,
                                secondaryColor = ThemeNeonBlue
                            ),
                        title = "",
                        cornerStyle = CornerStyle.ROUNDED,
                        backgroundStyle = BackgroundStyle.HEX_GRID
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Aura",
                                color = CyberpunkTextColor.Secondary,
                                style = CyberpunkTextStyle.Label
                            )
                        }
                    }

                    FloatingCyberWindow(
                        modifier = Modifier
                            .size(80.dp)
                            .cyberEdgeGlow(
                                primaryColor = NeonCyan,
                                secondaryColor = ThemeNeonBlue
                            ),
                        title = "",
                        cornerStyle = CornerStyle.ROUNDED,
                        backgroundStyle = BackgroundStyle.HEX_GRID
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Kai",
                                color = CyberpunkTextColor.Primary,
                                style = CyberpunkTextStyle.Label
                            )
                        }
                    }

                    FloatingCyberWindow(
                        modifier = Modifier
                            .size(80.dp)
                            .cyberEdgeGlow(
                                primaryColor = NeonGreen,
                                secondaryColor = ThemeNeonBlue
                            ),
                        title = "",
                        cornerStyle = CornerStyle.ROUNDED,
                        backgroundStyle = BackgroundStyle.HEX_GRID
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Genesis",
                                color = CyberpunkTextColor.Primary,
                                style = CyberpunkTextStyle.Label
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Status panel based on image reference 5
                FloatingCyberWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .digitalGlitchEffect(),
                    cornerStyle = CornerStyle.HEXAGON,
                    title = stringResource(R.string.system_status),
                    backgroundStyle = BackgroundStyle.TRANSPARENT
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CyberpunkText(
                            text = stringResource(R.string.aura_shield_active),
                            color = CyberpunkTextColor.Primary,
                            style = CyberpunkTextStyle.Body
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CyberpunkText(
                                    text = stringResource(R.string.neural),
                                    color = CyberpunkTextColor.White,
                                    style = CyberpunkTextStyle.Label
                                )
                                CyberpunkText(
                                    text = stringResource(R.string.active),
                                    color = CyberpunkTextColor.Primary,
                                    style = CyberpunkTextStyle.Label
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CyberpunkText(
                                    text = stringResource(R.string.quantum),
                                    color = CyberpunkTextColor.White,
                                    style = CyberpunkTextStyle.Label
                                )
                                CyberpunkText(
                                    text = stringResource(R.string.quantum_percent),
                                    color = CyberpunkTextColor.Primary,
                                    style = CyberpunkTextStyle.Label
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- top-level helper composables ---
@Composable
fun HomeMenuItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberMenuItem(
        text = text,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .digitalPixelEffect(visible = isSelected),
        isSelected = isSelected
    )
}

@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    pages: List<@Composable () -> Unit>
) {
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        pages[currentPage]()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    currentPage = (currentPage - 1 + pages.size) % pages.size
                }
            ) {
                Text(text = "<")
            }

            Button(
                onClick = {
                    currentPage = (currentPage + 1) % pages.size
                }
            ) {
                Text(text = ">")
            }
        }
    }
}
