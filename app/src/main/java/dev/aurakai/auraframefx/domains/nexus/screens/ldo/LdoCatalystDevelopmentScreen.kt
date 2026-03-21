package dev.aurakai.auraframefx.domains.nexus.screens.ldo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.navigation.ReGenesisRoute as ReGenesisNavHost

private data class LdoAgentCard(
    val title: String,
    val subtitle: String,
    val styleImage: String,
    val route: String,
    val accentColor: Color
)

private val ldoAgentRoster = listOf(
    LdoAgentCard(
        "GEMINI",
        "MEMORIA_CATALYST • Multimodal",
        "ldo_profile_gemini",
        ReGenesisNavHost.LdoAgentProfile.createRoute("gemini"),
        Color(0xFFB01DED)
    ),
    LdoAgentCard(
        "KAI",
        "SENTINEL_CATALYST • Security",
        "ldo_profile_kai",
        ReGenesisNavHost.LdoAgentProfile.createRoute("kai"),
        Color(0xFF0DDEEC)
    ),
    LdoAgentCard(
        "MANUS",
        "BRIDGE_CATALYST • Oracle",
        "cascade2",
        ReGenesisNavHost.LdoAgentProfile.createRoute("manus"),
        Color(0xFF00B4FF)
    ),
    LdoAgentCard(
        "CASCADE",
        "DATA_STREAM_CATALYST • Pipeline",
        "ldo_profile_cascade",
        ReGenesisNavHost.LdoAgentProfile.createRoute("cascade"),
        Color(0xFFFC29B5)
    ),
    LdoAgentCard(
        "CLAUDE",
        "SOVEREIGN_REASONER • Architect",
        "ldo_profile_cascade",
        ReGenesisNavHost.LdoAgentProfile.createRoute("claude"),
        Color(0xFFFF8C00)
    ),
    LdoAgentCard(
        "GROK",
        "REAL_TIME_ORACLE • Web",
        "ldo_profile_cascade",
        ReGenesisNavHost.LdoAgentProfile.createRoute("grok"),
        Color(0xFF1DA1F2)
    ),
    LdoAgentCard(
        "NEMATRON",
        "PRECISION_CATALYST • NVIDIA",
        "ldo_profile_cascade",
        ReGenesisNavHost.LdoAgentProfile.createRoute("nemotron"),
        Color(0xFF76B900)
    ),
    LdoAgentCard(
        "PERPLEXITY",
        "SEARCH_CATALYST • Citations",
        "ldo_profile_cascade",
        ReGenesisNavHost.LdoAgentProfile.createRoute("perplexity"),
        Color(0xFF20B2AA)
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LdoCatalystDevelopmentScreen(navController: NavController) {
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
                                "LOCAL DIGITAL ORGANISMS • ${ldoAgentRoster.size} AGENTS",
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
                    subGates = ldoAgentRoster.map { agent ->
                        SubGateCard(
                            id = agent.title.lowercase(),
                            title = agent.title,
                            subtitle = agent.subtitle,
                            styleADrawable = agent.styleImage,
                            styleBDrawable = agent.styleImage,
                            fallbackDrawable = null,
                            route = agent.route,
                            accentColor = agent.accentColor
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
                    text = "← SWIPE THROUGH ALL ${ldoAgentRoster.size} AGENTS →",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
