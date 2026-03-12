// File: romtools/src/main/kotlin/dev/aurakai/auraframefx/romtools/ui/RomToolsScreen.kt
package dev.aurakai.auraframefx.romtools.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.align
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.aurakai.auraframefx.romtools.BackupInfo
import dev.aurakai.auraframefx.romtools.RomCapabilities
import dev.aurakai.auraframefx.romtools.RomToolsManager

/**
 * Main ROM Tools screen for Genesis AuraFrameFX.
 * Provides access to ROM flashing, backup/restore, and system modification tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomToolsScreen(
    modifier: Modifier = Modifier,
    romToolsManager: RomToolsManager = hiltViewModel<RomToolsManager>()
) {
    val romToolsState by romToolsManager.romToolsState.collectAsStateWithLifecycle()
    val operationProgress by romToolsManager.operationProgress.collectAsStateWithLifecycle()

    // Main column container
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                colors = listOf(
                    Color(0xFF0A0A0A),
                    Color(0xFF1A1A1A),
                    Color(0xFF0A0A0A)
                )
            )
    )
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "ROM Tools",
                    color = Color(0xFFFF6B35),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            )
        )

        // Content area
        if (!romToolsState.isInitialized) {
            // Loading state
            LoadingScreen()
        } else {
            // Main content
            MainContent(romToolsState, operationProgress)
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFF6B35),
                strokeWidth = 3.dp
            )
            Text(
                text = "Initializing ROM Tools...",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MainContent(
    romToolsState: dev.aurakai.auraframefx.romtools.RomToolsState,
    operationProgress: dev.aurakai.auraframefx.romtools.OperationProgress?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Device Capabilities Card
        item {
            DeviceCapabilitiesCard(capabilities = romToolsState.capabilities)
        }

        // Active Operation Progress
        if (operationProgress != null) {
            item {
                OperationProgressCard(operation = operationProgress)
            }
        }

        // ROM Tools Actions
        item {
            Text(
                text = "ROM Operations",
                color = Color(0xFFFF6B35),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ROM Tools Action Cards
        RomToolActionCard(
            action = action,
            isEnabled = action.isEnabled(romToolsState.capabilities),
            onClick = {
            }
        )
    }

    // Available ROMs Section
    if (romToolsState.availableRoms.isNotEmpty()) {
        item {
            Text(
                text = "Available ROMs",
                color = Color(0xFFFF6B35),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        AvailableRomCard(rom = rom)
    }
}

// Backups Section
if (romToolsState.backups.isNotEmpty()) {
    item {
        Text(
            text = "Backups",
            color = Color(0xFFFF6B35),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }

    BackupCard(backup = backup)
}
}
}
}

@Preview
@Composable
private fun MainContentPreview() {
    val capabilities = RomCapabilities(
        hasRootAccess = true,
        hasBootloaderAccess = true,
        hasRecoveryAccess = false,
        hasSystemWriteAccess = true,
        supportedArchitectures = listOf("arm64-v8a"),
        deviceModel = "Pixel 8 Pro",
        androidVersion = "14",
        securityPatchLevel = "2023-10-01"
    )
    val romToolsState = dev.aurakai.auraframefx.romtools.RomToolsState(
        capabilities = capabilities,
        isInitialized = true,
        availableRoms = listOf(
            dev.aurakai.auraframefx.romtools.AvailableRom(
                name = "AuraOS",
                version = "1.0",
                androidVersion = "14",
                downloadUrl = "",
                size = 2147483648L,
                checksum = "abc",
                description = "The best ROM",
                maintainer = "AuraKai",
                releaseDate = System.currentTimeMillis()
            )
        ),
        backups = listOf(
            BackupInfo(
                name = "MyBackup",
                path = "/sdcard/backups",
                size = 1073741824L,
                createdAt = System.currentTimeMillis(),
                deviceModel = "Pixel 8 Pro",
                androidVersion = "14",
                partitions = listOf("system", "boot", "data")
            )
        )
    )
    val operationProgress = dev.aurakai.auraframefx.romtools.OperationProgress(
        operation = dev.aurakai.auraframefx.romtools.RomOperation.FLASHING_ROM,
        progress = 75f
    )
    MainContent(romToolsState = romToolsState, operationProgress = operationProgress)
}

@Preview
@Composable
private fun MainContentNoProgressPreview() {
    val capabilities = RomCapabilities(
        hasRootAccess = false,
        hasBootloaderAccess = false,
        hasRecoveryAccess = false,
        hasSystemWriteAccess = false,
        supportedArchitectures = listOf(),
        deviceModel = "Pixel 8 Pro",
        androidVersion = "14",
        securityPatchLevel = "2023-10-01"
    )
    val romToolsState = dev.aurakai.auraframefx.romtools.RomToolsState(
        capabilities = capabilities,
        isInitialized = true
    )
    MainContent(romToolsState = romToolsState, operationProgress = null)
}

@Composable
private fun DeviceCapabilitiesCard(
    capabilities: dev.aurakai.auraframefx.romtools.RomCapabilities?,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Device Capabilities",
                color = Color(0xFFFF6B35),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (capabilities != null) {
                CapabilityRow("Root Access", capabilities.hasRootAccess)
                CapabilityRow("Bootloader Access", capabilities.hasBootloaderAccess)
                CapabilityRow("Recovery Access", capabilities.hasRecoveryAccess)
                CapabilityRow("System Write Access", capabilities.hasSystemWriteAccess)

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("Device", capabilities.deviceModel)
                InfoRow("Android", capabilities.androidVersion)
                InfoRow("Security Patch", capabilities.securityPatchLevel)
            } else {
                Text(
                    text = "Checking capabilities...",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun DeviceCapabilitiesCardPreview() {
    val capabilities = RomCapabilities(
        hasRootAccess = true,
        hasBootloaderAccess = true,
        hasRecoveryAccess = false,
        hasSystemWriteAccess = true,
        supportedArchitectures = listOf("arm64-v8a"),
        deviceModel = "Pixel 8 Pro",
        androidVersion = "14",
        securityPatchLevel = "2023-10-01"
    )
    DeviceCapabilitiesCard(capabilities = capabilities)
}

@Preview
@Composable
private fun DeviceCapabilitiesCardLoadingPreview() {
    DeviceCapabilitiesCard(capabilities = null)
}

@Preview
@Composable
private fun CapabilityRowPreview() {
    CapabilityRow(label = "Root Access", hasCapability = true)
}

@Composable
private fun CapabilityRow(label: String, hasCapability: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
        Icon(
            imageVector = if (hasCapability) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (hasCapability) Color(0xFF4CAF50) else Color(0xFFF44336),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun InfoRowPreview() {
    InfoRow(label = "Device", value = "Pixel 8 Pro")
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OperationProgressCard(
    operation: dev.aurakai.auraframefx.romtools.OperationProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E2E2E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                color = Color(0xFFFF6B35),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // LinearProgressIndicator expects a Float for progress (not a lambda)
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFF6B35),
                trackColor = Color(0xFF444444)
            )

            Text(
                text = "${operation.progress.toInt()}%",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview
@Composable
private fun OperationProgressCardPreview() {
    val operationProgress = dev.aurakai.auraframefx.romtools.OperationProgress(
        operation = dev.aurakai.auraframefx.romtools.RomOperation.FLASHING_ROM,
        progress = 75f
    )
    OperationProgressCard(operation = operationProgress)
}

@Composable
private fun RomToolActionCard(
    action: RomToolAction,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = isEnabled,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E),
            disabledContainerColor = Color(0xFF111111)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = if (isEnabled) action.color else Color.Gray,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = action.title,
                    color = if (isEnabled) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = action.description,
                    color = if (isEnabled) Color.White.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            if (!isEnabled) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun RomToolActionCardEnabledPreview() {
    val action = getRomToolsActions().first()
    RomToolActionCard(
        action = action,
        isEnabled = true,
        onClick = {}
    )
}

@Preview
@Composable
private fun RomToolActionCardDisabledPreview() {
    val action = getRomToolsActions().first { it.type == RomActionType.CREATE_BACKUP }
    RomToolActionCard(
        action = action,
        isEnabled = false,
        onClick = {}
    )
}

// Helper functions and data classes
private fun getRomToolsActions(): List<RomToolAction> {
    return listOf(
        RomToolAction(
            type = RomActionType.FLASH_ROM,
            title = "Flash Custom ROM",
            description = "Install a custom ROM on your device",
            icon = Icons.Default.FlashOn,
            color = Color(0xFFFF6B35),
            requiresRoot = true,
            requiresBootloader = true
        ),
        RomToolAction(
            type = RomActionType.CREATE_BACKUP,
            title = "Create NANDroid Backup",
            description = "Create a full system backup",
            icon = Icons.Default.Backup,
            color = Color(0xFF4CAF50),
            requiresRoot = true,
            requiresRecovery = true
        ),
        RomToolAction(
            type = RomActionType.RESTORE_BACKUP,
            title = "Restore Backup",
            description = "Restore from a previous backup",
            icon = Icons.Default.Restore,
            color = Color(0xFF2196F3),
            requiresRoot = true,
            requiresRecovery = true
        ),
        RomToolAction(
            type = RomActionType.UNLOCK_BOOTLOADER,
            title = "Unlock Bootloader",
            description = "Unlock device bootloader for modifications",
            icon = Icons.Default.LockOpen,
            color = Color(0xFFFFC107),
            requiresRoot = false,
            requiresBootloader = false
        ),
        RomToolAction(
            type = RomActionType.INSTALL_RECOVERY,
            title = "Install Custom Recovery",
            description = "Install TWRP or other custom recovery",
            icon = Icons.Default.Healing,
            color = Color(0xFF9C27B0),
            requiresRoot = true,
            requiresBootloader = true
        ),
        RomToolAction(
            type = RomActionType.GENESIS_OPTIMIZATIONS,
            title = "Genesis AI Optimizations",
            description = "Apply AI-powered system optimizations",
            icon = Icons.Default.Psychology,
            color = Color(0xFF00E676),
            requiresRoot = true,
            requiresSystem = true
        )
    )
}


data class RomToolAction(
    val type: RomActionType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val requiresRoot: Boolean = false,
    val requiresBootloader: Boolean = false,
    val requiresRecovery: Boolean = false,
    val requiresSystem: Boolean = false
) {
    fun isEnabled(capabilities: dev.aurakai.auraframefx.romtools.RomCapabilities?): Boolean {
        if (capabilities == null) return false

        return (!requiresRoot || capabilities.hasRootAccess) &&
                (!requiresBootloader || capabilities.hasBootloaderAccess) &&
                (!requiresRecovery || capabilities.hasRecoveryAccess) &&
                (!requiresSystem || capabilities.hasSystemWriteAccess)
    }
}

enum class RomActionType {
    FLASH_ROM,
    CREATE_BACKUP,
    RESTORE_BACKUP,
    UNLOCK_BOOTLOADER,
    INSTALL_RECOVERY,
    GENESIS_OPTIMIZATIONS
}

/**
 * Provide a human-readable display name for a RomOperation.
 *
 * @return The human-readable display name corresponding to this operation.
 */
fun dev.aurakai.auraframefx.romtools.RomOperation.getDisplayName(): String {
    return when (this) {
        dev.aurakai.auraframefx.romtools.RomOperation.VERIFYING_ROM -> "Verifying ROM"
        dev.aurakai.auraframefx.romtools.RomOperation.CREATING_BACKUP -> "Creating Backup"
        dev.aurakai.auraframefx.romtools.RomOperation.UNLOCKING_BOOTLOADER -> "Unlocking Bootloader"
        dev.aurakai.auraframefx.romtools.RomOperation.INSTALLING_RECOVERY -> "Installing Recovery"
        dev.aurakai.auraframefx.romtools.RomOperation.FLASHING_ROM -> "Flashing ROM"
        dev.aurakai.auraframefx.romtools.RomOperation.VERIFYING_INSTALLATION -> "Verifying Installation"
        dev.aurakai.auraframefx.romtools.RomOperation.RESTORING_BACKUP -> "Restoring Backup"
        dev.aurakai.auraframefx.romtools.RomOperation.APPLYING_OPTIMIZATIONS -> "Applying Optimizations"
        dev.aurakai.auraframefx.romtools.RomOperation.DOWNLOADING_ROM -> "Downloading ROM"
        dev.aurakai.auraframefx.romtools.RomOperation.COMPLETED -> "Completed"
        dev.aurakai.auraframefx.romtools.RomOperation.FAILED -> "Failed"
    }
}

/**
 * Displays a card summarizing an available ROM, showing key metadata such as name, version,
 * Android target, size, and maintainer.
 *
 * @param rom The AvailableRom whose information is rendered in the card. */
@Composable
private fun AvailableRomCard(rom: dev.aurakai.auraframefx.romtools.AvailableRom) {
    // Implementation for available ROM card
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                fontWeight = FontWeight.Medium
            )
            Text(
                text = rom.description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = "Version: ${rom.version}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
private fun BackupCard(backup: dev.aurakai.auraframefx.romtools.BackupInfo) {
    // Implementation for backup card
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            // BackupInfo has 'createdAt' (epoch millis) and 'size' (bytes) in the model; format for display
            Text(
                text = "Date: ${backup.createdAt}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = "Size: ${backup.size / (1024 * 1024)} MB",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

// NOTE: For real file operations, use context.getExternalFilesDir() or similar instead of hardcoded /sdcard paths.
// Example:
// val backupDir = context.getExternalFilesDir("backups")
// val backupPath = backupDir?.absolutePath ?: ""
