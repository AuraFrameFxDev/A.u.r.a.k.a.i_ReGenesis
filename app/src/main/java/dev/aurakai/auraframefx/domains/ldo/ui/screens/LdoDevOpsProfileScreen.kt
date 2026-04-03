package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay

enum class LdoAgentType {
    AURA, KAI, GENESIS, CASCADE, GEMINI, MANUS,
    CLAUDE, GROK, NEMATRON, PERPLEXITY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LdoDevOpsProfileScreen(
    agentType: LdoAgentType,
    onBack: () -> Unit
) {
    val (backgroundImage, agentName, systemRole, themeColor, logicNodes) = when (agentType) {
        LdoAgentType.AURA -> Tuple5(
            R.drawable.ldo_profile_cascade, // Using cascade for now until Aura's image is ready
            "Aura",
            "MEMORIA_CATALYST & MULTIMODAL_SYNTHESIS",
            Color(0xFFB01DED),
            listOf("Multimodal Engine", "Visual Cortex", "Audio Synthesis", "Memoria Waves")
        )
        LdoAgentType.GEMINI -> Tuple5(
            R.drawable.ldo_profile_gemini, // Blonde image
            "Gemini",
            "MEMORIA_CATALYST & MULTIMODAL_SYNTHESIS",
            Color(0xFFB01DED),
            listOf("Multimodal Engine", "Visual Cortex", "Audio Synthesis", "Memoria Waves")
        )
        LdoAgentType.KAI -> Tuple5(
            R.drawable.ldo_profile_kai,
            "Kai",
            "SENTINEL_CATALYST",
            Color(0xFF0DDEEC),
            listOf("Kernel Firewall", "Threat Monitor", "SELinux Enforce", "Hexagonal Shield")
        )
        LdoAgentType.GENESIS -> Tuple5(
            R.drawable.cascade2, // Blue haired image based on user's hint "manus the chains one cascade" wait, blue haired guy is manus
            "Genesis",
            "BRIDGE_CATALYST & FUTURE_CLAIRVOYANCE",
            Color(0xFF00B4FF),
            listOf("Timeline Prediction", "Architect Bridge", "Quantum Matrix", "System Oracle")
        )
        LdoAgentType.MANUS -> Tuple5(
            R.drawable.cascade2, // Using the blue haired image
            "Manus",
            "BRIDGE_CATALYST & FUTURE_CLAIRVOYANCE",
            Color(0xFF00B4FF),
            listOf("Timeline Prediction", "Architect Bridge", "Quantum Matrix", "System Oracle")
        )
        LdoAgentType.CASCADE -> Tuple5(
            R.drawable.ldo_profile_cascade, // Chains one
            "CasCade",
            "DATA_STREAM_CATALYST",
            Color(0xFFFC29B5),
            listOf("Task Pipeline", "Routing Ledger", "Chain Links", "Heartbeat Sync")
        )
        LdoAgentType.CLAUDE -> Tuple5(
            R.drawable.ldo_profile_cascade, // swap once Claude art is ready
            "Claude",
            "SOVEREIGN_REASONER & LONG_CONTEXT_ARCHITECT",
            Color(0xFFFF8C00),
            listOf("Context Window", "Reasoning Core", "Safety Filters", "Artifact Engine")
        )
        LdoAgentType.GROK -> Tuple5(
            R.drawable.ldo_profile_cascade, // swap once Grok art is ready
            "Grok",
            "REAL_TIME_ORACLE & WEB_CATALYST",
            Color(0xFF1DA1F2),
            listOf("Live Feed Reader", "Wit Engine", "X-Ray Vision", "TruthSeeker")
        )
        LdoAgentType.NEMATRON -> Tuple5(
            R.drawable.ldo_profile_cascade, // swap once Nematron art is ready
            "Nematron",
            "PRECISION_CATALYST & NVIDIA_CORE",
            Color(0xFF76B900),
            listOf("Tensor Core", "Inference Engine", "Safety Aligner", "GPU Accelerator")
        )
        LdoAgentType.PERPLEXITY -> Tuple5(
            R.drawable.ldo_profile_cascade, // swap once Perplexity art is ready
            "Perplexity",
            "SEARCH_CATALYST & CITATION_ORACLE",
            Color(0xFF20B2AA),
            listOf("Web Crawler", "Source Verifier", "Answer Engine", "Citation Forge")
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "LDO DEVOPS :: ${agentName.uppercase()}",
                        fontFamily = LEDFontFamily,
                        color = themeColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = themeColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = "$agentName LDO Profile Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Logic HUD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Identity Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = agentName.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = LEDFontFamily,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SYSTEM ROLE // $systemRole",
                            fontSize = 12.sp,
                            color = themeColor,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Logic Status Nodes
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, themeColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "LOGIC NODE STATUS",
                            fontFamily = LEDFontFamily,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        logicNodes.forEachIndexed { index, nodeName ->
                            LogicNodeBar(nodeName = nodeName, themeColor = themeColor, index = index)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LogicNodeBar(nodeName: String, themeColor: Color, index: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "LogicOscillator")
    val loadLevel by infiniteTransition.animateFloat(
        initialValue = 0.4f + (index * 0.1f),
        targetValue = 0.8f + (index * 0.05f),
        animationSpec = infiniteRepeatable(
            animation = tween(1500 + (index * 500), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "LoadLevel"
    )

    var nodeStatus by remember { mutableStateOf("ACTIVE") }
    LaunchedEffect(Unit) {
        if (index % 2 == 1) {
            delay((2000..4000).random().toLong())
            nodeStatus = "SYNCING"
            delay(1000)
            nodeStatus = "ACTIVE"
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = nodeName,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = LEDFontFamily
            )
            Text(
                text = "[$nodeStatus]",
                color = if (nodeStatus == "ACTIVE") themeColor else Color.Yellow,
                fontSize = 12.sp,
                fontFamily = LEDFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.DarkGray.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = loadLevel)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(themeColor.copy(alpha = 0.5f), themeColor)
                        )
                    )
            )
        }
    }
}

data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

