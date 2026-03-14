package dev.aurakai.auraframefx.romtools.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.romtools.AvailableRom
import dev.aurakai.auraframefx.romtools.BackupInfo
import dev.aurakai.auraframefx.romtools.OperationProgress
import dev.aurakai.auraframefx.romtools.RomCapabilities

/**
 * Displays a card summarizing an available ROM.
 */
@Composable
fun AvailableRomCard(
    rom: AvailableRom,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = rom.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = rom.description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = "Version: ${rom.version} (Android ${rom.androidVersion})",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = "Size: ${rom.size / (1024 * 1024)} MB",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Displays a card summarizing a NANDroid backup.
 */
@Composable
fun BackupCard(
    backup: BackupInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = backup.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Date: ${backup.createdAt}", // Ideally format this date
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = "Size: ${backup.size / (1024 * 1024)} MB | Partitions: ${backup.partitions.joinToString()}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}