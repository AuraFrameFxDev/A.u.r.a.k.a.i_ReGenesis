package dev.aurakai.auraframefx.domains.aura.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor.GateAssetConfig
import dev.aurakai.auraframefx.domains.aura.ui.components.DomainSubGateCarousel
import dev.aurakai.auraframefx.domains.aura.ui.components.StarfieldBackground
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily

/**
 * 🤖 AGENT NEXUS HUB (Level 2 Hub)
 *
 * ANIMATION: StarfieldBackground
 * - Cosmic constellation aesthetic
 * - Neural network connections
 * - Multi-agent convergence visuals
 *
 * TWO VISUAL STYLES:
 * Style A: "Constellation" - Star maps, cosmic connections
 * Style B: "Control Room" - HUD panels, monitoring screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentNexusHubScreen(
    navController: NavController,
    getNexusSubGates: () -> List<dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard>
) {

    val subGates = getNexusSubGates()

    var useStyleB by remember {
        mutableStateOf(GateAssetConfig.StyleMode.nexusStyle == GateAssetConfig.GateStyle.STYLE_B)
    }

    val styleName = if (useStyleB) "CONTROL ROOM" else "CONSTELLATION"

    Box(modifier = Modifier.fillMaxSize()) {
        // 🤖 NEXUS' ANIMATED BACKGROUND - Starfield!
        StarfieldBackground()

        // Semi-transparent Overlay for "Command Center" feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "AGENT NEXUS",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "MULTI-AGENT CONVERGENCE • $styleName",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF7B2FFF)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            useStyleB = !useStyleB
                            GateAssetConfig.toggleNexusStyle()
                        }) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                "Toggle Style",
                                tint = Color(0xFF7B2FFF)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Agent coordination, monitoring, and fusion protocols active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 🎠 SUB-GATE CAROUSEL
                DomainSubGateCarousel(
                    subGates = subGates,
                    onGateSelected = { gate ->
                        navController.navigate(gate.route)
                    },
                    useStyleB = useStyleB,
                    cardHeight = 280.dp,
                    domainColor = Color(0xFF7B2FFF),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "← SWIPE TO BROWSE • TAP ⇆ TO CHANGE STYLE →",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


