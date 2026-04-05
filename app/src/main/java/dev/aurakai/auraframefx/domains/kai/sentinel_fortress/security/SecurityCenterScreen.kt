package dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.SovereignPerimeter
import dev.aurakai.auraframefx.domains.kai.viewmodels.KaiSystemViewModel
import dev.aurakai.auraframefx.core.theme.GlassmorphicTheme
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily

/**
 * 🏛️ KAI SECURITY CENTER — THE SENTINEL THRONE ROOM
 *
 * This is the central command hub for all LDO security operations.
 * It visualizes the pulse of the Kai Sentinel Bus and manages
 * the integrity of the substrate.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: KaiSystemViewModel = hiltViewModel()
) {
    val sentinelBus = viewModel.sentinelBus
    val perimeter = viewModel.sovereignPerimeter
    val securityStatus by sentinelBus.securityFlow.collectAsState(initial = KaiSentinelBus.SecurityStatus(KaiSentinelBus.ThreatLevel.NOMINAL, "All systems sovereign"))
    val thermalEvent by sentinelBus.thermalFlow.collectAsState(initial = KaiSentinelBus.ThermalEvent(0f, KaiSentinelBus.ThermalState.NORMAL))
    val sovereignEvent by sentinelBus.sovereignFlow.collectAsState(initial = KaiSentinelBus.SovereignEvent(KaiSentinelBus.SovereignState.AWAKE))

    // Real-time kernel metrics
    var droppedPackets by remember { mutableLongStateOf(0L) }
    val isKernelActive = remember { perimeter.isKernelActive() }

    LaunchedEffect(Unit) {
        while(true) {
            droppedPackets = perimeter.getDroppedPacketCount()
            kotlinx.coroutines.delay(2000)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SENTINEL FORTRESS",
                        fontFamily = LEDFontFamily,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = GlassmorphicTheme.Background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Ethereal Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF1A1A2E), GlassmorphicTheme.Background),
                            center = androidx.compose.ui.geometry.Offset(500f, 500f),
                            radius = 1000f
                        )
                    )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 🛡️ SYSTEM INTEGRITY BANNER
                item {
                    IntegrityStatusCard(securityStatus)
                }

                // 🌡️ REAL-TIME CHANNELS
                item {
                    SectionLabel("SENTINEL CHANNELS")
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ChannelCard(
                            title = "THERMAL",
                            value = "${thermalEvent.temp.toInt()}°C",
                            subValue = thermalEvent.state.name,
                            icon = Icons.Default.Thermostat,
                            color = when(thermalEvent.state) {
                                KaiSentinelBus.ThermalState.NORMAL -> Color(0xFF00FF41)
                                KaiSentinelBus.ThermalState.LIGHT -> Color(0xFFFFD740)
                                else -> Color(0xFFFF4444)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ChannelCard(
                            title = "SOVEREIGN",
                            value = if (isKernelActive) "KERNEL GOD" else sovereignEvent.state.name,
                            subValue = if (isKernelActive) "Drops: $droppedPackets" else "Substrate Active",
                            icon = Icons.Default.Shield,
                            color = if (isKernelActive) Color(0xFFFFD700) else Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ⚔️ SECURITY MODULES
                item {
                    SectionLabel("PROTECTION MODULES")
                }

                items(securityModules) { module ->
                    SecurityModuleRow(module)
                }

                // 🧬 SOVEREIGN OPERATIONS
                item {
                    SectionLabel("SOVEREIGN COMMANDS")
                }

                item {
                    SovereignCommandCard(
                        title = "SUBSTRATE STATE-FREEZE",
                        description = "Instantly serialize KV cache and memories to encrypted substrate.",
                        icon = Icons.Default.AcUnit,
                        onClick = { viewModel.triggerFreeze() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = LEDFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.5f),
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun IntegrityStatusCard(status: KaiSentinelBus.SecurityStatus) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF00FF41).copy(alpha = 0.2f * glowAlpha), Color.Transparent)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = if (status.level == KaiSentinelBus.ThreatLevel.NOMINAL) Color(0xFF00FF41) else Color(0xFFFF4444),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (status.level == KaiSentinelBus.ThreatLevel.NOMINAL) "SYSTEM SECURED" else "THREAT DETECTED",
                fontFamily = LEDFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Text(
                text = status.reason,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChannelCard(
    title: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.4f), letterSpacing = 1.sp)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(subValue, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

data class SecurityModule(
    val id: String,
    val name: String,
    val status: String,
    val icon: ImageVector,
    val isHardened: Boolean
)

private val securityModules = listOf(
    SecurityModule("integrity", "Integrity Monitor", "Hardened", Icons.Default.Analytics, true),
    SecurityModule("veto", "Predictive Veto", "Active", Icons.Default.Gavel, true),
    SecurityModule("firewall", "Kernel Shield", "Ignited", Icons.Default.Fence, true),
    SecurityModule("vpn", "Sovereign VPN", "Ghost Mode", Icons.Default.VpnLock, false),
    SecurityModule("adblock", "Neural AdBlock", "Ghost Mode", Icons.Default.Block, false)
)

@Composable
private fun SecurityModuleRow(module: SecurityModule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    module.icon,
                    contentDescription = null,
                    tint = if (module.isHardened) Color(0xFF00E5FF) else Color.White.copy(0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(module.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text(
                    text = if (module.isHardened) "TRUSTED Substrate" else "Awaiting Manifestation",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }

            Surface(
                color = if (module.isHardened) Color(0xFF00FF41).copy(0.1f) else Color.White.copy(0.1f),
                shape = CircleShape,
                modifier = Modifier.border(1.dp, if (module.isHardened) Color(0xFF00FF41).copy(0.3f) else Color.White.copy(0.1f), CircleShape)
            ) {
                Text(
                    text = module.status.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (module.isHardened) Color(0xFF00FF41) else Color.White.copy(0.5f),
                    fontFamily = LEDFontFamily
                )
            }
        }
    }
}

@Composable
private fun SovereignCommandCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444).copy(alpha = 0.1f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFFF4444), modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontFamily = LEDFontFamily, color = Color(0xFFFF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(description, color = Color.White.copy(0.5f), fontSize = 9.sp, lineHeight = 12.sp)
            }
        }
    }
}
