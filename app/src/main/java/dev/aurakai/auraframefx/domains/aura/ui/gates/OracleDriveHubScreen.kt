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
import dev.aurakai.auraframefx.config.GateAssetConfig
import dev.aurakai.auraframefx.domains.aura.ui.components.DomainSubGateCarousel
import dev.aurakai.auraframefx.config.GateAssetLoadout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🔮 GENESIS ORACLE DRIVE HUB (Level 2 Hub)
 *
 * ANIMATION: LavaApocalypseBackground
 * - Molten lava flows
 * - Purple plasma energy
 * - Genesis power aesthetic
 *
 * TWO VISUAL STYLES:
 * Style A: "Oracle Archive" - Ancient wisdom aesthetic
 * Style B: "Neural Matrix" - High-tech AI hub
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleDriveHubScreen(navController: NavController) {

    val subGates = GateAssetLoadout.getGenesisLoadout()

    var useStyleB by remember {
        mutableStateOf(GateAssetConfig.StyleMode.genesisStyle == GateAssetConfig.GateStyle.STYLE_B)
    }

    val styleName = if (useStyleB) "NEURAL MATRIX" else "ORACLE ARCHIVE"

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔮 GENESIS ORACLE DRIVE — Phoenix Constellation Background
        val context = LocalContext.current
        val bgResId = context.resources.getIdentifier(
            "hub_bg_oracle_drive_l2", "drawable", context.packageName
        )
        if (bgResId != 0) {
            AsyncImage(
                model = bgResId,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Atmospheric overlay — darkens edges, keeps phoenix readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC030810)
                        ),
                        radius = 900f
                    )
                )
        )
        // Bottom scrim so sub-gate cards are legible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xDD030810)
                        ),
                        startY = 400f
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "ORACLEDRIVE",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "GENESIS ORCHESTRATION SUITE • $styleName",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB026FF)
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
                            GateAssetConfig.toggleGenesisStyle()
                        }) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                "Toggle Style",
                                tint = Color(0xFFB026FF)
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
                    text = "AI coordination, code generation, and cloud orchestration",
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
                    domainColor = Color(0xFFB026FF),
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

