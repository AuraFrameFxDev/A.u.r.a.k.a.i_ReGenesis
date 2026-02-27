package dev.aurakai.auraframefx.romtools.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.aurakai.auraframefx.romtools.BackupInfo
import dev.aurakai.auraframefx.romtools.OperationProgress
import dev.aurakai.auraframefx.romtools.RomCapabilities
import dev.aurakai.auraframefx.romtools.RomOperation
import dev.aurakai.auraframefx.romtools.RomOperation.CreateBackup
import dev.aurakai.auraframefx.romtools.RomOperation.FlashRom
import dev.aurakai.auraframefx.romtools.RomToolsState
import dev.aurakai.auraframefx.romtools.RomToolsViewModel
import dev.aurakai.auraframefx.romtools.backdrop.BackdropState
import dev.aurakai.auraframefx.romtools.backdrop.CardExplosionEffect
import dev.aurakai.auraframefx.romtools.backdrop.MegaManBackdropRenderer
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Main ROM Tools screen for Genesis AuraFrameFX.
 * Provides access to ROM flashing, backup/restore, and system modification tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomToolsScreen(
    modifier: Modifier = Modifier,
    romToolsViewModel: RomToolsViewModel = hiltViewModel(),
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val romToolsState by romToolsViewModel.romToolsState.collectAsStateWithLifecycle()
    val operationProgressState by romToolsViewModel.operationProgress.collectAsStateWithLifecycle()
    romToolsViewModel.romToolsManager
    rememberCoroutineScope()

    // Backdrop state management
    var backdropEnabled by remember { mutableStateOf(true) }
    var backdropState by remember { mutableStateOf(BackdropState.STATIC) }
    val explosionEffect = remember { CardExplosionEffect() }

    // Track previous operation state to detect start
    var wasOperationRunning by remember { mutableStateOf(false) }

    // File pickers for ROM and backup selection
    val romPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            Timber.i("ROM file selected: $it")
            romToolsViewModel.performOperation(FlashRom, context, it)
        }
    }

    val backupPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            Timber.i("Backup file selected: $it")
            romToolsViewModel.performOperation(RomOperation.RestoreBackup, context, it)
        }
    }

    // Detect operation start and trigger explosion
    val op = operationProgressState // avoid smart-cast issues on delegated property
    val isOperationRunning = op != null && op.progress < 100f
    LaunchedEffect(isOperationRunning, backdropState) {
        if (isOperationRunning && !wasOperationRunning && backdropState == BackdropState.STATIC) {
            // Operation just started - trigger explosion!
            Timber.i("🎴 ROM operation started - triggering card explosion!")
            backdropState = BackdropState.EXPLODING
            explosionEffect.initializeFromImage(null, 800f, 800f)  // Fallback pixels
            explosionEffect.trigger()
        }
        wasOperationRunning = isOperationRunning
    }

    // Update explosion effect
    LaunchedEffect(backdropState) {
        if (backdropState == BackdropState.EXPLODING) {
            while (backdropState == BackdropState.EXPLODING) {
                delay(16)  // ~60 FPS
                val isComplete = explosionEffect.update(0.016f)
                if (isComplete) {
                    backdropState = BackdropState.ACTIVE
                    Timber.i("✨ Transition complete - backdrop is now ACTIVE")
                }
            }
        }
    }

    // Handle operation completion
    LaunchedEffect(op?.progress) {
        if (op != null && op.progress >= 100f && backdropState == BackdropState.ACTIVE) {
            backdropState = BackdropState.COMPLETING
            delay(500)  // Brief pause
            backdropState = BackdropState.VICTORY
            delay(2000)  // Victory pose
            backdropState = BackdropState.STATIC
            Timber.i("🏆 Operation complete - returning to STATIC state")
        }
    }

    // Box container with animated backdrop
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Backdrop rendering based on state
        if (backdropEnabled) {
            when (backdropState) {
                BackdropState.STATIC -> {
                    // Show static screenshot (card idle state)
                    StaticBackdropImage()
                }

                BackdropState.EXPLODING -> {
                    // Show pixel explosion transition
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Dark background
                        drawRect(color = Color(0xFF1A1A2E))
                        // Explosion effect
                        explosionEffect.draw(this)
                    }
                }

                BackdropState.ACTIVE,
                BackdropState.COMPLETING,
                BackdropState.VICTORY -> {
                    // Full animated backdrop or Freeze frame with victory pose
                    MegaManBackdropRenderer(
                        operationProgress = op,
                        enabled = true
                    )
                    // TODO: Add victory overlay when in COMPLETING/VICTORY states
                }
            }
        }

        // Main UI column (foreground) - semi-transparent to show backdrop
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))  // Darken slightly for readability
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
                actions = {
                    // Backdrop toggle button
                    IconButton(onClick = { backdropEnabled = !backdropEnabled }) {
                        Icon(
                            imageVector = if (backdropEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (backdropEnabled) "Hide Mega Man backdrop" else "Show Mega Man backdrop",
                            tint = Color(0xFFFF6B35)
                        )
                    }
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
            MainContent(
                romToolsState = romToolsState,
                operationProgress = op,
                onActionClick = { actionType ->
                    when (actionType) {
                        RomActionType.FLASH_ROM -> {
                            romPicker.launch(arrayOf(
                                "application/zip",
                                "application/octet-stream",
                                "application/x-zip-compressed"
                            ))
                        }
                        RomActionType.RESTORE_BACKUP -> {
                            backupPicker.launch(arrayOf(
                                "application/zip",
                                "application/octet-stream"
                            ))
                        }
                        else -> handleRomAction(
                            actionType = actionType,
                            viewModel = romToolsViewModel,
                            context = context
                        )
                    }
                }
            )
        }
        }  // Column
    }  // Box with backdrop
}

/**
 * Handles ROM tool action clicks by dispatching to the appropriate RomToolsManager method.
 *
 * Note: FLASH_ROM and RESTORE_BACKUP are handled at the screen level via file pickers
 * and are not processed by this function.
 *
 * @param actionType The type of ROM action to perform
 * @param romToolsManager The manager instance to execute the operation
 * @param coroutineScope The coroutine scope for launching suspend operations
 */
private fun handleRomAction(
    actionType: RomActionType,
    viewModel: RomToolsViewModel,
    context: Context
) {
    when (actionType) {
        RomActionType.FLASH_ROM -> { /* Handled in Composable */
        }

        RomActionType.RESTORE_BACKUP -> { /* Handled in Composable */
        }

        RomActionType.CREATE_BACKUP -> {
            viewModel.performOperation(CreateBackup, context)
        }

        RomActionType.UNLOCK_BOOTLOADER -> {
            viewModel.performOperation(RomOperation.UnlockBootloader, context)
        }

        RomActionType.INSTALL_RECOVERY -> {
            viewModel.performOperation(RomOperation.InstallRecovery, context)
        }

        RomActionType.GENESIS_OPTIMIZATIONS -> {
            viewModel.performOperation(RomOperation.GenesisOptimizations, context)
        }
    }
}

private fun RomToolsViewModel.performOperation(
    operation: Any,
    context: Context
) {
    TODO("Not yet implemented")
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
    romToolsState: RomToolsState,
    operationProgress: OperationProgress?,
    onActionClick: (RomActionType) -> Unit = {}
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
        getRomToolsActions().forEach { action ->
            item {
                RomToolActionCard(
                    action = action,
                    isEnabled = action.isEnabled(romToolsState.capabilities),
                    onClick = {
                        onActionClick(action.type)
                    }
                )
            }
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

            romToolsState.availableRoms.forEach { rom ->
                item {
                    AvailableRomCard(rom = rom)
                }
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

            romToolsState.backups.forEach { backup ->
                item {
                    BackupCard(backup = backup)
                }
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
    val romToolsState = RomToolsState(
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
    val operationProgress = OperationProgress(
        operation = "Flashing ROM",
        progress = 75f,
        status = "Flashing..."
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
    val romToolsState = RomToolsState(
        capabilities = capabilities,
        isInitialized = true
    )
    MainContent(romToolsState = romToolsState, operationProgress = null)
}

@Composable
private fun DeviceCapabilitiesCard(
    capabilities: RomCapabilities?,
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
    operation: OperationProgress,
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
                text = operation.operation,
                color = Color(0xFFFF6B35),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            LinearProgressIndicator(
                progress = { operation.progress / 100f },
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
    val operationProgress = OperationProgress(
        operation = "Flashing ROM",
        progress = 75f,
        status = "Flashing..."
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
    fun isEnabled(capabilities: RomCapabilities?): Boolean {
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
 * Provides a human-readable display name for a RomOperation.
 *
 * @return The human-readable display name corresponding to this operation.
 */
fun RomOperation.getDisplayName(): String {
    return when (this) {
        FlashRom -> "Flash ROM"
        RomOperation.RestoreBackup -> "Restore Backup"
        CreateBackup -> "Create Backup"
        RomOperation.UnlockBootloader -> "Unlock Bootloader"
        RomOperation.InstallRecovery -> {
            "Install Recovery"
        }

        RomOperation.GenesisOptimizations -> "Genesis Optimizations"
    }
}

// ROM and Backup cards are now provided by AvailableRomCard.kt

// NOTE: For real file operations, use context.getExternalFilesDir() or similar instead of hardcoded /sdcard paths.
// Example:
// val backupDir = context.getExternalFilesDir("backups")
// val backupPath = backupDir?.absolutePath ?: ""

/**
 * StaticBackdropImage - Shows the chutes & ladders stage as a static card
 *
 * Displays the reference screenshot when no operation is running.
 * The "card" is dormant, waiting to be activated.
 */
@Composable
private fun StaticBackdropImage(modifier: Modifier = Modifier) {
    // Fallback: Colored backdrop with card hint
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF0F0F1E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Visual hint that this is a "dormant card"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎴",
                fontSize = 64.sp
            )
            Text(
                text = "Agent Artifact Card",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Dormant",
                color = Color(0xFF00FFFF).copy(alpha = 0.5f),
                fontSize = 14.sp
            )
            Text(
                text = "Start an operation to awaken",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}
