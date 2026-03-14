package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Bootloader Manager Screen
 * Unlock/lock bootloader and manage partitions
 */
@Composable
fun BootloaderManagerScreen(
    onNavigateBack: () -> Unit = {}
) {
    val bootloaderStatus = remember { mutableStateOf("Locked") }
    val isProcessing = remember { mutableStateOf(false) }
    val processProgress = remember { mutableStateOf(0f) }

    val partitions = listOf(
        PartitionInfo("Boot", "/dev/block/bootdevice/by-name/boot", "Active", Color(0xFFDC143C)),
        PartitionInfo("System", "/dev/block/bootdevice/by-name/system", "Read-Only", Color(0xFFDC143C)),
        PartitionInfo("Vendor", "/dev/block/bootdevice/by-name/vendor", "Read-Only", Color(0xFFDC143C)),
        PartitionInfo("Recovery", "/dev/block/bootdevice/by-name/recovery", "Stock", Color(0xFFFFD700))
    )

    LaunchedEffect(isProcessing.value) {
        if (isProcessing.value) {
            for (i in 0..100 step 5) {
                processProgress.value = i / 100f
                kotlinx.coroutines.delay(100)
            }
            isProcessing.value = false
            processProgress.value = 0f
            bootloaderStatus.value = if (bootloaderStatus.value == "Locked") "Unlocked" else "Locked"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "🔓 BOOTLOADER MANAGER",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFDC143C),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Unlock/lock bootloader and manage partitions",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFDC143C).copy(alpha = 0.8f)
        )

        // Bootloader Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (bootloaderStatus.value == "Locked") Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Bootloader Status",
                    tint = if (bootloaderStatus.value == "Locked") Color(0xFFDC143C) else Color(0xFF32CD32),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bootloader Status",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = bootloaderStatus.value,
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (bootloaderStatus.value == "Locked") Color(0xFFDC143C) else Color(0xFF32CD32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Warning Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFDC143C).copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC143C))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFDC143C),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "⚠️ Bootloader operations may void warranty and require factory reset!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFDC143C),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Processing Progress
        if (isProcessing.value) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${if (bootloaderStatus.value == "Locked") "Unlocking" else "Locking"} bootloader...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFDC143C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { processProgress.value },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFDC143C)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(processProgress.value * 100).toInt()}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Partition Information
        Text(
            text = "Partition Information",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        partitions.forEach { partition ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Partition",
                        tint = partition.color,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = partition.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = partition.path,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = partition.color.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = partition.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = partition.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (!isProcessing.value) {
                        isProcessing.value = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (bootloaderStatus.value == "Locked") Color(0xFFDC143C) else Color(0xFF32CD32)
                )
            ) {
                Text(
                    text = if (bootloaderStatus.value == "Locked") "Unlock Bootloader" else "Lock Bootloader",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { /* Check status */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC143C)
                    )
                ) {
                    Text("Check Status")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { /* Advanced options */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC143C)
                    )
                ) {
                    Text("Advanced")
                }
            }
        }
    }
}

/**
 * Data class for partition information
 */
data class PartitionInfo(
    val name: String,
    val path: String,
    val status: String,
    val color: Color
)
