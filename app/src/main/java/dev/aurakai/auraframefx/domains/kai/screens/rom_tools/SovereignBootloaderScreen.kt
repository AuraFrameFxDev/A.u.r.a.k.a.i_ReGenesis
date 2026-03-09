<<<<<<<< HEAD:app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/SovereignBootloaderScreen.kt
package dev.aurakai.auraframefx.domains.kai.screens
========
package dev.aurakai.auraframefx.domains.kai.screens.rom_tools
>>>>>>>> origin/main:app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/rom_tools/SovereignBootloaderScreen.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compoAse.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🔒 SOVEREIGN BOOTLOADER
 * Securely manage device locks and partition integrity blocks.
 */
@Composable
fun SovereignBootloaderScreen(
    onNavigateBack: () -> Unit
) {
    var isUnlocked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0202))
    ) {
        AnimeHUDContainer(
            title = "BOOTLOADER MANAGER",
            description = "LOW-LEVEL GATEKEEPER: CONTROLLING THE INITIALIZATION VECTOR OF THE LDO.",
            glowColor = Color(0xFFFF1111),
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header - Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }

                // Status Card
                StatusCard(isUnlocked)

                Spacer(modifier = Modifier.height(24.dp))

                // Warning Panel
                WarningPanel()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "PARTITION LOCK STATUS",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item { PartitionLockItem("BOOT", true) }
                    item { PartitionLockItem("SYSTEM (LDO-CORE)", true) }
                    item { PartitionLockItem("RECOVERY", false) }
                    item { PartitionLockItem("VENDOR", true) }
                }

                Button(
                    onClick = { isUnlocked = !isUnlocked },
                    < < < < < < < < HEAD:app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/SovereignBootloaderScreen.kt
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUnlocked) Color.White.copy(alpha = 0.1f) else Color(
                        0xFFFF1111
                    )
                            === === ==
                            modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUnlocked) Color.White.copy(
                            alpha = 0.1f
                        ) else Color(0xFFFF1111)
                                > > > > > > > > origin / main : app / src / main / java / dev / aurakai / auraframefx / domains / kai / screens / rom_tools / SovereignBootloaderScreen . kt
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isUnlocked) androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFFF1111)
                    ) else null
                ) {
                    Text(
                        if (isUnlocked) "LOCK BOOTLOADER" else "UNLOCK BOOTLOADER",
                        color = if (isUnlocked) Color(0xFFFF1111) else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCard(isUnlocked: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) Color(0xFF00FF85).copy(alpha = 0.3f) else Color(0xFFFF1111).copy(alpha = 0.3f)
        )
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                null,
                tint = if (isUnlocked) Color(0xFF00FF85) else Color(0xFFFF1111),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    "STATUS",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isUnlocked) "UNLOCKED" else "LOCKED (SECURE)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isUnlocked) Color(0xFF00FF85) else Color(0xFFFF1111),
                    fontWeight = FontWeight.Black,
                    fontFamily = LEDFontFamily
                )
            }
        }
    }
}

@Composable
private fun WarningPanel() {
    Surface(
        color = Color(0xFFFF1111).copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFFF1111).copy(alpha = 0.2f)
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Warning,
                null,
                tint = Color(0xFFFF1111),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Warning: Modifying bootloader state will trigger a factory reset and purge all local LDO context.",
                color = Color(0xFFFF1111),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun PartitionLockItem(name: String, isLocked: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                null,
                tint = if (isLocked) Color.White.copy(alpha = 0.4f) else Color(0xFF00FF85),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


