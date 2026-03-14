package dev.aurakai.auraframefx.romtools.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This file originally contained an invalid large screen implementation and referenced
// globals like `romToolsState` and `operationProgress`. Replace that with two
// clear composables:
//  - AvailableRomsScreen: a small screen that lists ROMs and backups and shows optional progress.
//  - AvailableRomCard: a simple card that displays a single ROM (rom is Any to avoid
//    introducing or assuming project-specific types).
// Also add minimal stubs for a few referenced cards so this file compiles independently.

@Composable
fun AvailableRomsScreen(
    availableRoms: List<Any>,
    backups: List<Any> = emptyList(),
    capabilities: Any? = null,
    operationProgress: Any? = null,
    modifier: Modifier = Modifier,
    onActionClick: (Any) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Device capabilities header (optional)
        if (capabilities != null) {
            DeviceCapabilitiesCard(capabilities)
        }

        // Operation progress (optional)
        if (operationProgress != null) {
            OperationProgressCard(operationProgress)
        }

        // ROMs header
        Text(
            text = "Available ROMs",
            color = Color(0xFFFF6B35),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // List ROMs
        for (rom in availableRoms) {
            AvailableRomCard(rom = rom, modifier = Modifier)
        }

        // Backups section
        if (backups.isNotEmpty()) {
            Text(
                text = "Backups",
                color = Color(0xFFFF6B35),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            for (backup in backups) {
                BackupCard(backup)
            }
        }
    }
}

@Composable
fun AvailableRomCard(rom: Any, modifier: Modifier = Modifier) {
    // Minimal, readable card that shows the rom.toString() and a small subtitle.
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = rom.toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Details: ${rom::class.simpleName}",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
    }
}

// --- Small local stubs so this file doesn't rely on globals from other files ---

@Composable
fun DeviceCapabilitiesCard(capabilities: Any?) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Device Capabilities", color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = capabilities?.toString() ?: "-", color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun OperationProgressCard(progress: Any?) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Operation Progress", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = progress?.toString() ?: "No active operation",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RomToolActionCard(action: Any?, isEnabled: Boolean = true, onClick: () -> Unit = {}) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = action?.toString() ?: "Action", color = Color.White)
            Text(
                text = if (isEnabled) "Enabled" else "Disabled",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BackupCard(backup: Any?) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = backup?.toString() ?: "Backup", color = Color.White)
            Text(text = "Details", color = Color.LightGray, fontSize = 12.sp)
        }
    }
}