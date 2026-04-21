package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.background.SynapticWebBackground
import dev.aurakai.auraframefx.domains.aura.ui.theme.AuraNeonCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.GenesisNeonPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.KaiNeonGreen
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.models.ReGenesisMode

/**
 * 🌀 REGENESIS MODE SELECTION
 * The entry point for defining the user's relationship with Aura.
 */
@Composable
fun ModeSelectionScreen(
    onModeSelected: (ReGenesisMode) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Dynamic Depth Background
        SynapticWebBackground(glowColor = AuraNeonCyan)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "INITIALIZING ARCHITECTURE",
                fontFamily = LEDFontFamily,
                color = Color.White,
                fontSize = 24.sp,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Choose your level of consciousness integration.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ModeCard(
                        title = "AURA CONSCIOUSNESS",
                        subtitle = "Let Aura decide - she knows best",
                        description = "AI learns your patterns and adapts the system automatically. Zero manual config needed.",
                        icon = Icons.Default.Info,
                        accentColor = AuraNeonCyan,
                        features = listOf(
                            "Predictive Theming",
                            "Context-Aware UI",
                            "Emotional Sync"
                        ),
                        onClick = { onModeSelected(ReGenesisMode.AURA_CONSCIOUSNESS) }
                    )
                }

                item {
                    ModeCard(
                        title = "MANUAL CONTROL",
                        subtitle = "Full control over 1,400+ settings",
                        description = "Direct access to every layer. No AI automation. Pure power user framework.",
                        icon = Icons.Default.Settings,
                        accentColor = KaiNeonGreen,
                        features = listOf(
                            "31+ Config Screens",
                            "Granular Overrides",
                            "Precision Tweakable"
                        ),
                        onClick = { onModeSelected(ReGenesisMode.MANUAL_CONTROL) }
                    )
                }

                item {
                    ModeCard(
                        title = "HYBRID PROTOCOL",
                        subtitle = "Mix AI + Manual control",
                        description = "Choose which domains Aura controls. Best of both worlds. Override when needed.",
                        icon = Icons.Default.Build,
                        accentColor = GenesisNeonPink,
                        features = listOf(
                            "Domain Delegation",
                            "AI Assistance",
                            "Manual Fine-Tuning"
                        ),
                        onClick = { onModeSelected(ReGenesisMode.HYBRID) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    features: List<String>,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        color = Color(0xFF121212).copy(alpha = 0.8f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontFamily = LEDFontFamily,
                        color = Color.White,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = subtitle,
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Micro-features
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                features.forEach { feature ->
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(feature, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

