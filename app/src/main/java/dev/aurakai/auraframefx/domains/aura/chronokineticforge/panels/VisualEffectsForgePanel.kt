package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.components.ThreadsWovenFooter
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*

/**
 * ✨ VISUAL EFFECTS FORGE PANEL — Reality Manipulator
 *
 * Controls high-intensity visual effects:
 * - Ghost Shimmers for third-party app overlays
 * - Holographic RIP manifestations
 * - Particle density and RIP intensity
 */

@Composable
fun VisualEffectsForgePanel(
    viewModel: RealitymorphismViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var ghostShimmerEnabled by remember { mutableStateOf(true) }
    var shimmerIntensity by remember { mutableStateOf(0.7f) }
    var holographicRipEnabled by remember { mutableStateOf(false) }
    var particleDensity by remember { mutableStateOf(0.8f) }

    ForgePanelContainer(title = "REALITY EFFECTS") {
        Column(modifier = Modifier.padding(16.dp)) {
            // Effect Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EffectTile(
                    title = "GHOST SHIMMER",
                    icon = Icons.Default.Water,
                    color = Color(0xFF00E5FF),
                    isActive = ghostShimmerEnabled,
                    onClick = {
                        ghostShimmerEnabled = !ghostShimmerEnabled
                        if (ghostShimmerEnabled) {
                            viewModel.triggerGhostShimmer()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                EffectTile(
                    title = "HOLOGRAPHIC RIP",
                    icon = Icons.Default.Stream,
                    color = Color(0xFFFF00FF),
                    isActive = holographicRipEnabled,
                    onClick = {
                        holographicRipEnabled = !holographicRipEnabled
                        if (holographicRipEnabled) {
                            viewModel.triggerPortalRip()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ghost Shimmer Controls
            if (ghostShimmerEnabled) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00E5FF).copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ghost Shimmer Settings",
                            color = Color(0xFF00E5FF),
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Intensity slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Intensity", color = Color.White)
                            Text("${(shimmerIntensity * 100).toInt()}%", color = Color(0xFF00E5FF))
                        }

                        Slider(
                            value = shimmerIntensity,
                            onValueChange = {
                                shimmerIntensity = it
                                viewModel.updateShimmerIntensity(it)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00E5FF),
                                activeTrackColor = Color(0xFF00E5FF)
                            )
                        )

                        // Third-party apps toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = uiState.thirdPartyShimmer,
                                onCheckedChange = { viewModel.toggleThirdPartyShimmer(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF00E5FF),
                                    checkedTrackColor = Color(0xFF00E5FF).copy(alpha = 0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply to third-party apps",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Holographic RIP Controls
            if (holographicRipEnabled) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF00FF).copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Holographic RIP Settings",
                            color = Color(0xFFFF00FF),
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Creates dimensional tears in the UI fabric for dramatic transitions",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Particle Density Control
            Text(
                text = "PARTICLE DENSITY",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Grain,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = particleDensity,
                    onValueChange = {
                        particleDensity = it
                        ParticleBloodstreamEngine.setDensity(it)
                    },
                    valueRange = 0.2f..1.5f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF00FF),
                        activeTrackColor = Color(0xFFFF00FF)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${(particleDensity * 100).toInt()}%",
                    color = Color(0xFFFF00FF),
                    modifier = Modifier.width(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Trigger Rebellious Morph Button
            Button(
                onClick = {
                    RebelliousPaintDripEngine.triggerPaintDrip(
                        elementId = "global",
                        origin = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                        chaosScore = 0.9f,
                        colors = Pair(Color(0xFFFF00FF), Color(0xFF00E5FF)),
                        morphType = MorphType.ELASTIC
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF00FF)
                )
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("TRIGGER REBELLIOUS MORPH")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Success Cue: Nucleus Glow
            ConfidenceRing(value = viewModel.syncConfidence)

            Spacer(modifier = Modifier.height(8.dp))

            // Threads Woven Footer
            ThreadsWovenFooter()
        }
    }
}

@Composable
private fun EffectTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) color.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.5f),
        label = "bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isActive) color else Color.Transparent,
        label = "border"
    )

    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) color else Color.Gray,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = if (isActive) color else Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            if (isActive) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, RoundedCornerShape(50))
                )
            }
        }
    }
}

@Composable
private fun ConfidenceRing(value: Float) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SYNC CONFIDENCE",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Nucleus glow indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00E5FF).copy(alpha = value),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(value * 100).toInt()}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Placeholder ParticleBloodstreamEngine extension
fun ParticleBloodstreamEngine.setDensity(density: Float) {}
