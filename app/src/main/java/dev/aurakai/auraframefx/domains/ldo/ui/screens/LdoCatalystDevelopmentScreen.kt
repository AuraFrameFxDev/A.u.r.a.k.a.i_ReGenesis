package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.components.DomainSubGateCarousel
import dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import dev.aurakai.auraframefx.navigation.ReGenesisRoute
import dev.aurakai.auraframefx.domains.ldo.model.LDORoster

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LdoCatalystDevelopmentScreen(navController: NavController) {
    val agents = LDORoster.agents

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A0020), Color(0xFF1A0040), Color(0xFF000010))
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
                                "LDO CATALYST",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "LOCAL DIGITAL ORGANISMS • ${agents.size} AGENTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4EEB88)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tap an agent to access their DevOps profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                DomainSubGateCarousel(
                    subGates = agents.map { agent ->
                        SubGateCard(
                            id = agent.id,
                            title = agent.name.uppercase(),
                            subtitle = agent.catalystName,
                            styleADrawable = agent.profileAssetName,
                            styleBDrawable = agent.profileAssetName,
                            fallbackDrawable = null,
                            route = ReGenesisRoute.LdoAgentProfile.createRoute(agent.id),
                            accentColor = agent.color
                        )
                    },
                    onGateSelected = { gate ->
                        navController.navigate(gate.route)
                    },
                    useStyleB = false,
                    cardHeight = 300.dp,
                    domainColor = Color(0xFF4EEB88),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "← SWIPE THROUGH ALL ${agents.size} AGENTS →",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
