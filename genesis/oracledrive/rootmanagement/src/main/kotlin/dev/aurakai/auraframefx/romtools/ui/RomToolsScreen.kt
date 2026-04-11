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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
 * Harmonics: Integrates Mega Man backdrop with a high-tech UI style.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomToolsScreen(
    modifier: Modifier = Modifier,
    romToolsViewModel: RomToolsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val romToolsState by romToolsViewModel.romToolsState.collectAsStateWithLifecycle()
    val operationProgressState by romToolsViewModel.operationProgress.collectAsStateWithLifecycle()
    rememberCoroutineScope()

    // Backdrop state management
    var backdropEnabled by remember { mutableStateOf(true) }
    var backdropState by remember { mutableStateOf(BackdropState.STATIC) }
    val explosionEffect = remember { CardExplosionEffect() }

    // Track previous operation state to detect start
    var wasOperationRunning by remember { mutableStateOf(false) }

    // File pickers
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
    val op = operationProgressState
    val isOperationRunning = op != null && op.progress < 100f
    LaunchedEffect(isOperationRunning, backdropState) {
        if (isOperationRunning && !wasOperationRunning && backdropState == BackdropState.STATIC) {
            Timber.i("🎴 ROM operation started - triggering card explosion!")
            backdropState = BackdropState.EXPLODING
            explosionEffect.initializeFromImage(null, 800f, 800f)
            explosionEffect.trigger()
        }
        wasOperationRunning = isOperationRunning
    }

    // Update explosion effect
    LaunchedEffect(backdropState) {
        if (backdropState == BackdropState.EXPLODING) {
            while (backdropState == BackdropState.EXPLODING) {
                delay(16)
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
            delay(500)
            backdropState = BackdropState.VICTORY
            delay(2000)
            backdropState = BackdropState.STATIC
            Timber.i("🏆 Operation complete - returning to STATIC state")
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Black)) {
        // Backdrop rendering
        if (backdropEnabled) {
            when (backdropState) {
                BackdropState.STATIC -> StaticBackdropImage()
                BackdropState.EXPLODING -> {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(color = Color(0xFF1A1A2E))
                        explosionEffect.draw(this)
                    }
                }
                BackdropState.ACTIVE, BackdropState.COMPLETING, BackdropState.VICTORY -> {
                    MegaManBackdropRenderer(operationProgress = op, enabled = true)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ULTIMATE ROM SUITE",
                            color = Color(0xFFFF6B35),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        if (romToolsState.capabilities.hasRootAccess) {
                            Text(
                                "LEVEL 3 ACCESS GRANTED",
                                color = Color(0xFFFF3366),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { backdropEnabled = !backdropEnabled }) {
                        Icon(
                            imageVector = if (backdropEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Backdrop",
                            tint = Color(0xFFFF6B35)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.8f))
            )

            if (!romToolsState.isInitialized) {
                LoadingScreen()
            } else {
                MainContent(
                    romToolsState = romToolsState,
                    operationProgress = op,
                    onActionClick = { actionType ->
                        when (actionType) {
                            RomActionType.FLASH_ROM -> {
                                romPicker.launch(arrayOf("application/zip", "application/octet-stream", "application/x-zip-compressed"))
                            }
                            RomActionType.RESTORE_BACKUP -> {
                                backupPicker.launch(arrayOf("application/zip", "application/octet-stream"))
                            }
                            else -> handleRomAction(actionType, romToolsViewModel, context)
                        }
                    }
                )
            }
        }
    }
}

private fun handleRomAction(
    actionType: RomActionType,
    viewModel: RomToolsViewModel,
    context: Context
) {
    when (actionType) {
        RomActionType.FLASH_ROM -> {}
        RomActionType.RESTORE_BACKUP -> {}
        RomActionType.CREATE_BACKUP -> viewModel.performOperation(CreateBackup, context)
        RomActionType.UNLOCK_BOOTLOADER -> viewModel.performOperation(RomOperation.UnlockBootloader, context)
        RomActionType.INSTALL_RECOVERY -> viewModel.performOperation(RomOperation.InstallRecovery, context)
        RomActionType.GENESIS_OPTIMIZATIONS -> viewModel.performOperation(RomOperation.GenesisOptimizations, context)
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = Color(0xFFFF6B35), strokeWidth = 3.dp)
            Text(text = "Initializing ROM Tools...", color = Color.White, fontSize = 14.sp)
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
        item { DeviceCapabilitiesCard(capabilities = romToolsState.capabilities) }

        if (operationProgress != null) {
            item { OperationProgressCard(operation = operationProgress) }
        }

        item {
            Text(
                text = "ROM Operations",
                color = Color(0xFFFF6B35),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        getRomToolsActions().forEach { action ->
            item {
                RomToolActionCard(
                    action = action,
                    isEnabled = action.isEnabled(romToolsState.capabilities),
                    onClick = { onActionClick(action.type) }
                )
            }
        }

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
                item { AvailableRomCard(rom = rom) }
            }
        }

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
                item { BackupCard(backup = backup) }
            }
        }
    }
}

@Composable
private fun DeviceCapabilitiesCard(
    capabilities: RomCapabilities,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B35).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "DEVICE CAPABILITIES", color = Color(0xFFFF6B35), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            CapabilityRow("Root Access", capabilities.hasRootAccess)
            CapabilityRow("Bootloader Access", capabilities.hasBootloaderAccess)
            CapabilityRow("Recovery Access", capabilities.hasRecoveryAccess)
            CapabilityRow("System Write Access", capabilities.hasSystemWriteAccess)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Device", capabilities.deviceModel)
            InfoRow("Android", capabilities.androidVersion)
            InfoRow("Security Patch", capabilities.securityPatchLevel)
        }
    }
}

@Composable
private fun CapabilityRow(label: String, hasCapability: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = Color.White, fontSize = 13.sp)
        Icon(
            imageVector = if (hasCapability) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (hasCapability) Color(0xFF00FF85) else Color(0xFFFF3366),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$label:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OperationProgressCard(
    operation: OperationProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B35).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = operation.step.name.uppercase(),
                color = Color(0xFFFF6B35),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { operation.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF00E5FF),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Text(text = "${operation.progress.toInt()}%", color = Color.White, fontSize = 12.sp, modifier = Modifier.align(Alignment.End))
        }
    }
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
            containerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor = Color.Black.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEnabled) action.color.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = action.icon, contentDescription = null, tint = if (isEnabled) action.color else Color.Gray, modifier = Modifier.size(32.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = action.title.uppercase(), color = if (isEnabled) Color.White else Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = action.description, color = if (isEnabled) Color.White.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.5f), fontSize = 11.sp)
            }
            if (!isEnabled) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun getRomToolsActions(): List<RomToolAction> {
    return listOf(
        RomToolAction(RomActionType.FLASH_ROM, "Flash Custom ROM", "Install a custom ROM image", Icons.Default.FlashOn, Color(0xFFFF6B35), requiresRoot = true, requiresBootloader = true),
        RomToolAction(RomActionType.CREATE_BACKUP, "NANDroid Backup", "Full system partition snapshot", Icons.Default.Backup, Color(0xFF00FF85), requiresRoot = true, requiresRecovery = true),
        RomToolAction(RomActionType.RESTORE_BACKUP, "Restore Snapshot", "Restore from previous backup", Icons.Default.Restore, Color(0xFF00E5FF), requiresRoot = true, requiresRecovery = true),
        RomToolAction(RomActionType.UNLOCK_BOOTLOADER, "Unlock Bootloader", "OEM Unlock for modifications", Icons.Default.LockOpen, Color(0xFFFFCC00), requiresRoot = false, requiresBootloader = false),
        RomToolAction(RomActionType.INSTALL_RECOVERY, "Custom Recovery", "Install TWRP / OrangeFox", Icons.Default.Healing, Color(0xFF7B2FFF), requiresRoot = true, requiresBootloader = true),
        RomToolAction(RomActionType.GENESIS_OPTIMIZATIONS, "AI Optimizations", "LDO-powered system tuning", Icons.Default.Psychology, Color(0xFF00FFD4), requiresRoot = true, requiresSystem = true)
    )
}

data class RomToolAction(val type: RomActionType, val title: String, val description: String, val icon: ImageVector, val color: Color, val requiresRoot: Boolean = false, val requiresBootloader: Boolean = false, val requiresRecovery: Boolean = false, val requiresSystem: Boolean = false) {
    fun isEnabled(capabilities: RomCapabilities?): Boolean {
        if (capabilities == null) return false
        return (!requiresRoot || capabilities.hasRootAccess) && (!requiresBootloader || capabilities.hasBootloaderAccess) && (!requiresRecovery || capabilities.hasRecoveryAccess) && (!requiresSystem || capabilities.hasSystemWriteAccess)
    }
}

enum class RomActionType { FLASH_ROM, CREATE_BACKUP, RESTORE_BACKUP, UNLOCK_BOOTLOADER, INSTALL_RECOVERY, GENESIS_OPTIMIZATIONS }

fun RomOperation.getDisplayName(): String {
    return when (this) {
        FlashRom -> "Flash ROM"
        RomOperation.RestoreBackup -> "Restore Backup"
        CreateBackup -> "Create Backup"
        RomOperation.UnlockBootloader -> "Unlock Bootloader"
        RomOperation.InstallRecovery -> "Install Recovery"
        RomOperation.GenesisOptimizations -> "Genesis Optimizations"
    }
}

@Composable
private fun StaticBackdropImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(
                            0xFF1A1A2E
                        ), Color(0xFF0F0F1E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "🎴", fontSize = 64.sp)
            Text(text = "AGENT ARTIFACT CARD", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "DORMANT", color = Color(0xFF00FFFF).copy(alpha = 0.5f), fontSize = 12.sp)
            Text(text = "START AN OPERATION TO AWAKEN", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
        }
    }
}
