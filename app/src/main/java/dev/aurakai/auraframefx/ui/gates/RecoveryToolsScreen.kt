package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Recovery Tools Screen
 * TWRP integration and backup management
 */
@Composable
fun RecoveryToolsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val backups = listOf(
        BackupInfo("Daily Backup", "2025-11-30 10:30", "2.1 GB", "Complete", Color(0xFF32CD32)),
        BackupInfo("Weekly Backup", "2025-11-25 15:45", "2.3 GB", "Complete", Color(0xFF32CD32)),
        BackupInfo("System Backup", "2025-11-20 09:15", "1.8 GB", "Complete", Color(0xFF32CD32)),
        BackupInfo("Apps Only", "2025-11-15 14:20", "856 MB", "Complete", Color(0xFF32CD32))
    )

    val isBackingUp = remember { mutableStateOf(false) }
    val backupProgress = remember { mutableStateOf(0f) }
    val selectedBackup = remember { mutableStateOf<BackupInfo?>(null) }

    LaunchedEffect(isBackingUp.value) {
        if (isBackingUp.value) {
            for (i in 0..100 step 3) {
                backupProgress.value = i / 100f
                kotlinx.coroutines.delay(75)
            }
            isBackingUp.value = false
            backupProgress.value = 0f
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
            text = "🔄 RECOVERY TOOLS",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF32CD32),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "TWRP integration and backup management",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF32CD32).copy(alpha = 0.8f)
        )

        // TWRP Status
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
                    imageVector = Icons.Default.Restore,
                    contentDescription = "TWRP Status",
                    tint = Color(0xFF32CD32),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TWRP Recovery",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Installed & Ready",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF32CD32),
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { /* Reboot to recovery */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF32CD32)
                    )
                ) {
                    Text("Reboot", color = Color.Black)
                }
            }
        }

        // Backup Progress
        if (isBackingUp.value) {
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
                        text = "Creating backup...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF32CD32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { backupProgress.value },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF32CD32)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(backupProgress.value * 100).toInt()}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Backup Management
        Text(
            text = "Backup Management",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(backups) { backup ->
                BackupCard(
                    backup = backup,
                    onClick = {
                        selectedBackup.value = backup
                    }
                )
            }
        }

        // Restore Options
        if (selectedBackup.value != null) {
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
                        text = "Selected: ${selectedBackup.value?.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF32CD32)
                    )
                    Text(
                        text = "Created: ${selectedBackup.value?.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { selectedBackup.value = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF32CD32)
                            )
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { /* Restore backup */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF32CD32)
                            )
                        ) {
                            Text("Restore", color = Color.Black)
                        }
                    }
                }
            }
        }

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (!isBackingUp.value) {
                        isBackingUp.value = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isBackingUp.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF32CD32)
                )
            ) {
                Text("Create Backup", color = Color.Black)
            }
            OutlinedButton(
                onClick = { /* Wipe data */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF4500)
                )
            ) {
                Text("Wipe Data")
            }
        }
    }
}

/**
 * Backup card component
 */
@Composable
private fun BackupCard(
    backup: BackupInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, backup.color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Backup,
                contentDescription = "Backup",
                tint = backup.color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = backup.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${backup.date} • ${backup.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = backup.color.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = backup.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = backup.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Data class for backup information
 */
data class BackupInfo(
    val name: String,
    val date: String,
    val size: String,
    val status: String,
    val color: Color
)
