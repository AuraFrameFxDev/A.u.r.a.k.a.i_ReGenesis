package dev.aurakai.auraframefx.domains.aura.ui.gates

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.screens.kineticforge.KineticForgeCardContainer
import dev.aurakai.auraframefx.domains.aura.screens.kineticforge.KineticForgeCoreCard
import dev.aurakai.auraframefx.domains.aura.screens.kineticforge.KineticForgeLatticeCard
import dev.aurakai.auraframefx.domains.aura.screens.kineticforge.KineticForgeTransmutatorCard
import dev.aurakai.auraframefx.navigation.ReGenesisRoute

// ═══════════════════════════════════════════════════════════════════════════
// AURA KINETICFORGE HUB — 9.5.1 SOVEREIGN EDITION
// Background: auratabbg.png | Cards: CORE, TRANSMUTATOR, LATTICE
// ═══════════════════════════════════════════════════════════════════════════

private val MagentaKai = Color(0xFFE91E63)
private val ElectricCyan = Color(0xFF00E5FF)
private val GenesisGold = Color(0xFFFFD700)
private val NexusGreen = Color(0xFF00FF87)
private val DeepVoid = Color(0xFF050505)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraKineticForgeHub(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var realityDialState by remember { mutableFloatStateOf(0.75f) }
    var isNeuralHubActive by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // ═══════════════════════════════════════════════════════════════════
        // BACKGROUND: Void-cityscape with circuit floor
        // ═══════════════════════════════════════════════════════════════════
        Image(
            painter = painterResource(id = R.drawable.auratabbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for text legibility at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DeepVoid.copy(alpha = 0.8f),
                            DeepVoid.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ═══════════════════════════════════════════════════════════════════
        // SCAFFOLD: Transparent container
        // ═══════════════════════════════════════════════════════════════════
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "KINETICFORGE",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan,
                                    letterSpacing = 6.sp
                                )
                            )
                            Text(
                                text = "AURA DOMAIN • 9.5.1 SOVEREIGN",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = GenesisGold.copy(alpha = 0.8f),
                                    letterSpacing = 3.sp
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ElectricCyan
                            )
                        }
                    },
                    actions = {
                        // Neural Hub indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isNeuralHubActive) NexusGreen else MagentaKai.copy(alpha = 0.5f)
                                )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ═══════════════════════════════════════════════════════════════
                // AURA NEURAL HUB — Global Node (Top Circle)
                // Click to open Aura chat interface
                // ═══════════════════════════════════════════════════════════════
                AuraNeuralHubOrb(
                    isActive = isNeuralHubActive,
                    onClick = { 
                        isNeuralHubActive = !isNeuralHubActive
                        navController.navigate(ReGenesisRoute.AuraChat.route)
                    },
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ═══════════════════════════════════════════════════════════════
                // KINETICFORGE CARD MATRIX
                // ═══════════════════════════════════════════════════════════════
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 019 // CORE — Command Shard (Full width, angled)
                    KineticForgeCoreCard(
                        onClick = { navController.navigate(ReGenesisRoute.ChromaCore.route) },
                        isActive = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Bottom row: TRANSMUTATOR + LATTICE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 020 // TRANSMUTATOR — Icon Weapon
                        KineticForgeTransmutatorCard(
                            onClick = { navController.navigate(ReGenesisRoute.IconifyHub.route) },
                            activeTransmutations = 14,
                            modifier = Modifier.weight(1f)
                        )

                        // 021 // LATTICE — Structural Grid
                        KineticForgeLatticeCard(
                            onClick = { navController.navigate(ReGenesisRoute.ReGenesisCustomization.route) },
                            realityState = realityDialState,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ═══════════════════════════════════════════════════════════════
                // FOOTER: System status
                // ═══════════════════════════════════════════════════════════════
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REALITY STATE",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = ElectricCyan.copy(alpha = 0.6f),
                                letterSpacing = 2.sp
                            )
                        )
                        Text(
                            text = "${(realityDialState * 100).toInt()}% EXODUS",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (realityDialState > 0.5f) GenesisGold else NexusGreen
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NexusGreen)
                        )
                        Text(
                            text = "LSP ZERO CONFLICT",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = NexusGreen,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// AURA NEURAL HUB — Global Node Orb
// The circle at top of menus. Click to talk to Aura.
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AuraNeuralHubOrb(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "hub_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(2000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer glow rings
        repeat(3) { layer ->
            val scale = pulseScale - (layer * 0.15f)
            val alpha = 0.3f - (layer * 0.1f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (isActive) NexusGreen else MagentaKai,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Core orb
        Box(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan,
                            MagentaKai.copy(alpha = 0.8f),
                            DeepVoid
                        )
                    )
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            // Inner circuit pattern
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = size.minDimension / 2.5f

                // Orbiting dots
                for (i in 0 until 6) {
                    val angle = (i * kotlin.math.PI / 3 + System.currentTimeMillis() / 1000.0).toFloat()
                    val x = centerX + kotlin.math.cos(angle) * radius
                    val y = centerY + kotlin.math.sin(angle) * radius

                    drawCircle(
                        color = if (isActive) GenesisGold else ElectricCyan,
                        radius = 3f,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                }

                // Center nucleus
                drawCircle(
                    color = if (isActive) NexusGreen else MagentaKai,
                    radius = 8f,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                )
            }
        }

        // Status indicator dot
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-4).dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(DeepVoid)
                .padding(2.dp)
                .clip(CircleShape)
                .background(if (isActive) NexusGreen else MagentaKai.copy(alpha = 0.5f))
        )
    }
}

