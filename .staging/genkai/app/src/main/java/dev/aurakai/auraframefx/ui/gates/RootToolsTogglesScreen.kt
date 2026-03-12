package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * Root Tools Quick Toggles Screen
 *
 * Provides quick access to common root operations with toggle switches:
 * - Bootloader Lock/Unlock
 * - Recovery Mode Access
 * - System Partition Mount/Unmount
 * - Magisk Module Enable/Disable
 * - Root Permission Granting
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootToolsTogglesScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // State for toggles
    var bootloaderUnlocked by remember { mutableStateOf(false) }
    var systemPartitionRW by remember { mutableStateOf(false) }
    var magiskEnabled by remember { mutableStateOf(true) }
    var rootGranted by remember { mutableStateOf(true) }

    // Operation states
    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf<RootToggleAction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Root Tools",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.Cyan
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Quick Toggles",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Quick access to common root operations. Be careful with destructive actions.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Status message card
                if (statusMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Cyan.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color.Cyan
                                )
                                Text(
                                    text = statusMessage!!,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Bootloader Toggle
                item {
                    RootToggleCard(
                        title = "Bootloader",
                        description = "Unlock/lock device bootloader",
                        icon = Icons.Default.Lock,
                        isEnabled = bootloaderUnlocked,
                        isDangerous = true,
                        onToggle = { enabled ->
                            showConfirmDialog = if (enabled) {
                                RootToggleAction.UnlockBootloader
                            } else {
                                RootToggleAction.LockBootloader
                            }
                        },
                        isProcessing = isProcessing
                    )
                }

                // Recovery Mode Access
                item {
                    RootToggleCard(
                        title = "Recovery Mode",
                        description = "Quick access to recovery mode",
                        icon = Icons.Default.Settings,
                        isEnabled = false,
                        isDangerous = false,
                        onToggle = {
                            scope.launch {
                                isProcessing = true
                                statusMessage = "Rebooting to recovery mode..."
                                // TODO: Implement recovery mode reboot
                                kotlinx.coroutines.delay(2000)
                                statusMessage = "Device should reboot to recovery"
                                isProcessing = false
                            }
                        },
                        isProcessing = isProcessing,
                        isActionButton = true
                    )
                }

                // System Partition Toggle
                item {
                    RootToggleCard(
                        title = "System Partition R/W",
                        description = "Mount system partition as read-write",
                        icon = Icons.Default.Build,
                        isEnabled = systemPartitionRW,
                        isDangerous = true,
                        onToggle = { enabled ->
                            scope.launch {
                                isProcessing = true
                                statusMessage = if (enabled) {
                                    "Mounting system as read-write..."
                                } else {
                                    "Remounting system as read-only..."
                                }
                                // TODO: Implement system partition mount
                                kotlinx.coroutines.delay(1500)
                                systemPartitionRW = enabled
                                statusMessage = if (enabled) {
                                    "System partition is now read-write"
                                } else {
                                    "System partition is now read-only"
                                }
                                isProcessing = false
                            }
                        },
                        isProcessing = isProcessing
                    )
                }

                // Magisk Module Toggle
                item {
                    RootToggleCard(
                        title = "Magisk Modules",
                        description = "Enable/disable all Magisk modules",
                        icon = Icons.Default.Extension,
                        isEnabled = magiskEnabled,
                        isDangerous = false,
                        onToggle = { enabled ->
                            scope.launch {
                                isProcessing = true
                                statusMessage = if (enabled) {
                                    "Enabling Magisk modules..."
                                } else {
                                    "Disabling Magisk modules..."
                                }
                                // TODO: Implement Magisk module toggle
                                kotlinx.coroutines.delay(1500)
                                magiskEnabled = enabled
                                statusMessage = "Magisk modules ${if (enabled) "enabled" else "disabled"}"
                                isProcessing = false
                            }
                        },
                        isProcessing = isProcessing
                    )
                }

                // Root Permission Toggle
                item {
                    RootToggleCard(
                        title = "Root Access",
                        description = "Grant/revoke root permissions for apps",
                        icon = Icons.Default.AdminPanelSettings,
                        isEnabled = rootGranted,
                        isDangerous = true,
                        onToggle = { enabled ->
                            scope.launch {
                                isProcessing = true
                                statusMessage = if (enabled) {
                                    "Granting root access..."
                                } else {
                                    "Revoking root access..."
                                }
                                // TODO: Implement root permission toggle
                                kotlinx.coroutines.delay(1500)
                                rootGranted = enabled
                                statusMessage = "Root access ${if (enabled) "granted" else "revoked"}"
                                isProcessing = false
                            }
                        },
                        isProcessing = isProcessing
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Confirmation Dialog
            if (showConfirmDialog != null) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = null },
                    title = {
                        Text(
                            "Confirm Action",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    text = {
                        Text(
                            when (showConfirmDialog) {
                                RootToggleAction.UnlockBootloader ->
                                    "⚠️ WARNING: Unlocking the bootloader will ERASE ALL DATA on this device. This action cannot be undone. Are you sure?"
                                RootToggleAction.LockBootloader ->
                                    "⚠️ WARNING: Locking the bootloader may prevent booting if custom ROM is installed. Continue?"
                                else -> "Are you sure you want to perform this action?"
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    when (showConfirmDialog) {
                                        RootToggleAction.UnlockBootloader -> {
                                            statusMessage = "Unlocking bootloader..."
                                            // TODO: Implement bootloader unlock
                                            kotlinx.coroutines.delay(2000)
                                            bootloaderUnlocked = true
                                            statusMessage = "Bootloader unlocked successfully"
                                        }
                                        RootToggleAction.LockBootloader -> {
                                            statusMessage = "Locking bootloader..."
                                            // TODO: Implement bootloader lock
                                            kotlinx.coroutines.delay(2000)
                                            bootloaderUnlocked = false
                                            statusMessage = "Bootloader locked successfully"
                                        }
                                        else -> {}
                                    }
                                    isProcessing = false
                                    showConfirmDialog = null
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = null }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = Color(0xFF1A1A1A)
                )
            }
        }
    }
}

@Composable
private fun RootToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    isEnabled: Boolean,
    isDangerous: Boolean,
    onToggle: (Boolean) -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier,
    isActionButton: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDangerous) {
                Color.Red.copy(alpha = 0.1f)
            } else {
                Color(0xFF1A1A1A)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDangerous) Color.Red else Color.Cyan,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    )
                    if (isDangerous) {
                        Text(
                            text = "⚠️ Destructive operation",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Red
                            )
                        )
                    }
                }
            }

            if (isActionButton) {
                Button(
                    onClick = { onToggle(true) },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Launch")
                }
            } else {
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    enabled = !isProcessing,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = if (isDangerous) Color.Red else Color.Cyan,
                        checkedTrackColor = if (isDangerous) Color.Red.copy(alpha = 0.5f) else Color.Cyan.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

private sealed class RootToggleAction {
    object UnlockBootloader : RootToggleAction()
    object LockBootloader : RootToggleAction()
}
