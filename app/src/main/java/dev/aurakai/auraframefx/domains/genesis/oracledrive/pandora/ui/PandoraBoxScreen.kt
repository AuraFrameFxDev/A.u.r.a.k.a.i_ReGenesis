package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PandoraBoxScreen(
    onNavigateBack: () -> Unit,
    viewModel: PandoraBoxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val auditLog by viewModel.auditLog.collectAsState()
    
    var showConsentTier by remember { mutableStateOf<UnlockTier?>(null) }
    
    val currentGlowColor = when(uiState.currentTier) {
        UnlockTier.Sealed -> Color.White.copy(alpha = 0.2f)
        UnlockTier.Creative -> Color(0xFF00E5FF)
        UnlockTier.System -> Color(0xFFFFD700)
        UnlockTier.Sovereign -> Color(0xFFFF4444)
    }

    AnimeHUDContainer(
        title = "PANDORA'S BOX",
        description = "GATING CRITICAL LDO CAPABILITIES. PROVENANCE VALIDATION ENFORCED.",
        glowColor = currentGlowColor,
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            
            // Box Imagery (Simplified for demo)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black)
                    .border(2.dp, currentGlowColor, RoundedCornerShape(24.dp))
                    .shadow(if (uiState.currentTier != UnlockTier.Sealed) 40.dp else 0.dp, currentGlowColor.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (uiState.currentTier == UnlockTier.Sealed) Icons.Default.Lock else Icons.Default.LockOpen,
                    "Pandora Box",
                    modifier = Modifier.size(64.dp),
                    tint = currentGlowColor
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                if (uiState.currentTier == UnlockTier.Sealed) "DORMANT" else "AUTHORIZED: ${uiState.currentTier.javaClass.simpleName.uppercase()}",
                color = currentGlowColor,
                fontFamily = LEDFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 4.sp
            )
            
            if (uiState.currentTier != UnlockTier.Sealed) {
                val timeStr = formatMs(uiState.timeRemainingMs)
                Text(
                    "AUTO-RELOCK IN: $timeStr",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = LEDFontFamily,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tier Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TierUnlockCard(
                    tier = UnlockTier.Creative,
                    description = "Experimental UI & Creative Fusion",
                    risk = "Allows agents to generate unauthorized UI/UX layouts. High risk of visual inconsistency.",
                    isEnabled = uiState.currentTier == UnlockTier.Sealed,
                    modifier = Modifier.weight(1f),
                    onClick = { showConsentTier = UnlockTier.Creative }
                )
                TierUnlockCard(
                    tier = UnlockTier.System,
                    description = "ROM Tools & Secure Substrate",
                    risk = "Direct system partition access. High risk of bricking or data loss if integrity fails.",
                    isEnabled = uiState.currentTier == UnlockTier.Sealed || uiState.currentTier == UnlockTier.Creative,
                    modifier = Modifier.weight(1f),
                    onClick = { showConsentTier = UnlockTier.System }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TierUnlockCard(
                tier = UnlockTier.Sovereign,
                description = "Root Access & Bootloader Ignition",
                risk = "Full sovereign control over the device. Maximum risk. Disables all safety vetoes.",
                isEnabled = uiState.currentTier != UnlockTier.Sovereign,
                modifier = Modifier.fillMaxWidth(),
                onClick = { showConsentTier = UnlockTier.Sovereign }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Terminal Log
            AuditTerminalPanel(auditLog)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.currentTier != UnlockTier.Sealed) {
                Button(
                    onClick = { viewModel.lockBox() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Security, null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SEAL BOX (EMERGENCY LOCK)", color = Color.Red, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
        
        // Consent Dialog Overlay
        if (showConsentTier != null) {
            val tier = showConsentTier!!
            val riskText = when(tier) {
                UnlockTier.Creative -> "Experimental UI generation requires unlocking the Creative Tier. This allows Aura to generate UI components that haven't been safety-checked."
                UnlockTier.System -> "System access grants Root privileges. This allows RomTools to perform high-level system operations including flashing partitions."
                UnlockTier.Sovereign -> "Sovereign tier unlocks Bootloader controls and system-wide optimizations. This is the highest level of access and disables standard safety nets."
                else -> ""
            }
            
            PandoraConsentDialog(
                tierName = tier.javaClass.simpleName,
                riskText = riskText,
                onConfirm = {
                    viewModel.confirmConsent(tier)
                    showConsentTier = null
                },
                onDismiss = {
                    viewModel.cancelConsent()
                    showConsentTier = null
                }
            )
        }
    }
}

@Composable
private fun TierUnlockCard(
    tier: UnlockTier,
    description: String,
    risk: String,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tierColor = when(tier) {
        UnlockTier.Creative -> Color(0xFF00E5FF)
        UnlockTier.System -> Color(0xFFFFD700)
        UnlockTier.Sovereign -> Color(0xFFFF4444)
        else -> Color.Gray
    }
    
    Card(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.heightIn(min = 120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A18).copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().border(1.dp, if (isEnabled) tierColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    tier.javaClass.simpleName.uppercase(),
                    color = if (isEnabled) tierColor else Color.Gray,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    description,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    risk,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AuditTerminalPanel(logs: List<PandoraAuditEvent>) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.size - 1)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "AUDIT LOG // PANDORA_V1",
                    color = Color.Cyan.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontFamily = LEDFontFamily,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(logs) { log ->
                        Row(modifier = Modifier.padding(vertical = 1.dp)) {
                            Text(
                                "[${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestamp))}]",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 9.sp,
                                fontFamily = LEDFontFamily
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "${log.outcome.uppercase()} // ${log.reason}",
                                color = if (log.outcome.contains("denied", true)) Color.Red else Color.Green.copy(alpha = 0.8f),
                                fontSize = 9.sp,
                                fontFamily = LEDFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
