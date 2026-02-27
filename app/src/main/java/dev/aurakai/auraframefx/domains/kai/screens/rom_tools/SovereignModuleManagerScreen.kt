package dev.aurakai.auraframefx.domains.kai.screens.rom_tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.viewmodels.SovereignModuleViewModel
import dev.aurakai.auraframefx.domains.kai.ModuleStatus
import dev.aurakai.auraframefx.domains.kai.ModuleType
import dev.aurakai.auraframefx.domains.kai.SovereignModule
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 📦 SOVEREIGN MODULE MANAGER
 * Unified control for Magisk, LSPosed, and KernelSU modules.
 */
@Composable
fun SovereignModuleManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: SovereignModuleViewModel = hiltViewModel()
) {
    val modules by viewModel.modules.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF030303))) {
        AnimeHUDContainer(
            title = "MODULE MANAGER",
            description = "SYSTEM MODIFICATION HUB: MANAGE THE CORE HOOKS OF THE LIVING DIGITAL ORGANISM.",
            glowColor = Color(0xFF00FF85)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Stats
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModuleStat("ACTIVE", modules.count { it.status == ModuleStatus.ACTIVE }.toString(), Color(0xFF00FF85))
                    ModuleStat("TOTAL", modules.size.toString(), Color.White)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(modules) { module ->
                        ModuleItem(module, onToggle = { viewModel.toggleModule(module.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleStat(label: String, value: String, color: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 8.sp, color = color.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
            Text(value, fontSize = 18.sp, color = color, fontWeight = FontWeight.Black, fontFamily = LEDFontFamily)
        }
    }
}

@Composable
private fun ModuleItem(module: SovereignModule, onToggle: () -> Unit) {
    val accentColor = when (module.type) {
        ModuleType.MAGISK -> Color(0xFFFF3366)
        ModuleType.LSPOSED -> Color(0xFF00FF85)
        ModuleType.KERNEL_SU -> Color(0xFF00E5FF)
        ModuleType.SHIZUKU -> Color(0xFFFFD700)
        ModuleType.SYSTEM_HOOK -> Color(0xFFB026FF)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (module.status == ModuleStatus.ACTIVE) accentColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Extension, null, tint = accentColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(module.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (module.isCrucial) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Build, null, tint = Color.Yellow, modifier = Modifier.size(12.dp))
                    }
                }
                Text(module.description, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, lineHeight = 14.sp)
                Text("v${module.version} • BY ${module.author}", color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            Switch(
                checked = module.status == ModuleStatus.ACTIVE,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accentColor,
                    checkedTrackColor = accentColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}


