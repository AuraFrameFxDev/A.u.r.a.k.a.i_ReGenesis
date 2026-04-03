package dev.aurakai.auraframefx.domains.aura.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.components.DomainSubGateCarousel
import dev.aurakai.auraframefx.domains.aura.ui.components.IcyTundraBackground
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor.GateAssetConfig

/**
 * 🛡️ KAI SENTINEL HUB (Level 2 Hub)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaiSentinelHubScreen(navController: NavController) {

    // val subGates = GateAssetLoadout.getKaiLoadout()
    val subGates = emptyList<dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard>()

    var useStyleB by remember {
        mutableStateOf(false) // Defaulting since StyleMode might be stubbed
    }

    val styleName = if (useStyleB) "CYBER SENTINEL" else "FORTRESS"

    Box(modifier = Modifier.fillMaxSize()) {
        // 🛡️ KAI'S ANIMATED BACKGROUND - Icy Tundra!
        IcyTundraBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "SENTINEL'S FORTRESS",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "KAI'S SECURITY DOMAIN • $styleName",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF00FF85)
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
                        }) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                "Toggle Style",
                                tint = Color(0xFF00FF85)
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
                    text = "Security, ROM, bootloader, and root protocols",
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
                    domainColor = Color(0xFF00FF85),
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
