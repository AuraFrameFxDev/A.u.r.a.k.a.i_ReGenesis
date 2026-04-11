package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.backgrounds.DataRibbonsBackground
import dev.aurakai.auraframefx.domains.aura.ui.components.effects.HoloHUDOverlay
import dev.aurakai.auraframefx.domains.aura.ui.theme.AgentDomain

enum class BackgroundType {
    DATA_RIBBONS,
    CHAOS_LIGHTNING,
    FORTRESS_GRID,
    COMMAND_CENTER,
    CONSTELLATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberpunkScreenScaffold(
    title: String,
    subtitle: String? = null,
    agentDomain: AgentDomain,
    backgroundType: BackgroundType,
    showHudOverlay: Boolean = false,
    onNavigateBack: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Domain-Specific Background
        when (backgroundType) {
            BackgroundType.DATA_RIBBONS -> {
                DataRibbonsBackground(
                    baseColor = agentDomain.primaryColor,
                    accentColor = if (agentDomain == AgentDomain.AURA) Color.Cyan else Color.White // Basic logic for accent
                )
            }

            BackgroundType.FORTRESS_GRID -> {
                // Reuse existing HexagonGridBackground but tweaked for "Fortress"
                HexagonGridBackground(
                    primaryColor = agentDomain.primaryColor,
                    secondaryColor = Color.Red, // Kai heat map
                    accentColor = Color.Cyan
                )
            }
            // For now, mapping other types to existing or DataRibbons as fallback/placeholder
            // Ideally we would implement ChaosLightning and CommandCenter separately
            BackgroundType.CHAOS_LIGHTNING -> {
                // Placeholder: Using DataRibbons but more chaotic params if possible, or just default
                DataRibbonsBackground(
                    baseColor = agentDomain.primaryColor,
                    accentColor = Color.Magenta,
                    speedMin = 0.5f,
                    speedMax = 1.2f,
                    ribbons = 12
                )
            }

            BackgroundType.COMMAND_CENTER -> {
                // Placeholder: Cleaner look
                HexagonGridBackground(
                    primaryColor = agentDomain.primaryColor,
                    secondaryColor = Color(0xFF00C2A8),
                    hexSize = 40f,
                    alpha = 0.15f
                )
            }

            BackgroundType.CONSTELLATION -> {
                // Calm background
                DataRibbonsBackground(
                    baseColor = agentDomain.primaryColor,
                    ribbons = 3,
                    speedMax = 0.3f
                )
            }
        }

        // 2. Optional HUD Overlay
        if (showHudOverlay) {
            HoloHUDOverlay(color = agentDomain.primaryColor)
        }

        // 3. Content Overlay
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = agentDomain.primaryColor
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = agentDomain.primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f) // More transparent
                )
            )

            // Screen Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

