package dev.aurakai.auraframefx.domains.lsposed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

enum class LSPosedAction { RESTART_FRAMEWORK, CLEAR_CACHE, ENABLE_MODULES, DISABLE_MODULES, NONE }

@Composable
fun XposedQuickActionsPanel() {
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var pendingAction by remember { mutableStateOf(LSPosedAction.NONE) }
    var modulesEnabled by remember { mutableStateOf(true) } 
    val scope = rememberCoroutineScope()

    // Confirmation Dialog
    if (pendingAction != LSPosedAction.NONE) {
        AlertDialog(
            onDismissRequest = { pendingAction = LSPosedAction.NONE },
            title = {
                Text(
                    text = "Confirm Sovereign Action",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = when (pendingAction) {
                        LSPosedAction.RESTART_FRAMEWORK -> "Initiating a Zygote cycle will restart all applications and SystemUI. Proceed with caution."
                        LSPosedAction.CLEAR_CACHE -> "Purging the LSPosed hook cache may resolve module conflicts but requires a reboot to take effect."
                        LSPosedAction.DISABLE_MODULES -> "Disabling all modules will restore system purity until the next reboot."
                        LSPosedAction.ENABLE_MODULES -> "Enabling all modules will reactivate system hooks upon next reboot."
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val actionToExecute = pendingAction
                        pendingAction = LSPosedAction.NONE
                        scope.launch {
                            statusMessage = "Executing ${actionToExecute.name.replace("_", " ")}..."
                            val result = executeLSPosedOperation(actionToExecute)
                            if (result.isSuccess) {
                                statusMessage = "Success: ${result.getOrNull()}"
                                if (actionToExecute == LSPosedAction.ENABLE_MODULES) modulesEnabled = true
                                if (actionToExecute == LSPosedAction.DISABLE_MODULES) modulesEnabled = false
                            } else {
                                statusMessage = "Failure: ${result.exceptionOrNull()?.message}"
                            }
                            delay(4000)
                            statusMessage = null
                        }
                    }
                ) {
                    Text("EXECUTE", color = Color(0xFFFF6B35), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = LSPosedAction.NONE }) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status message
            if (statusMessage != null) {
                Text(
                    text = statusMessage!!,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Cyan),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Quick action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = if (modulesEnabled) "Disable Modules" else "Enable Modules",
                    icon = Icons.Default.Extension,
                    color = if (modulesEnabled) Color(0xFFFF6B35) else Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        pendingAction = if (modulesEnabled) LSPosedAction.DISABLE_MODULES else LSPosedAction.ENABLE_MODULES
                    }
                )

                var showHooksDialog by remember { mutableStateOf(false) }
                QuickActionButton(
                    title = "View Hooks",
                    icon = Icons.AutoMirrored.Filled.CallSplit,
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.weight(1f),
                    onClick = { showHooksDialog = true }
                )
                if (showHooksDialog) {
                    ActiveHooksDialog(onDismiss = { showHooksDialog = false })
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = "Restart Framework",
                    icon = Icons.Default.Refresh,
                    color = Color(0xFFFFD93D),
                    modifier = Modifier.weight(1f),
                    onClick = { pendingAction = LSPosedAction.RESTART_FRAMEWORK }
                )

                QuickActionButton(
                    title = "Clear Cache",
                    icon = Icons.Default.DeleteOutline,
                    color = Color(0xFF6C5CE7),
                    modifier = Modifier.weight(1f),
                    onClick = { pendingAction = LSPosedAction.CLEAR_CACHE }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
fun ActiveHooksDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Active Hooks",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Currently active hooks in the system:",
                    style = MaterialTheme.typography.bodyMedium
                )
                HookItem("SystemUI", "247 hooks", Color(0xFF4ECDC4))
                HookItem("Settings", "84 hooks", Color(0xFFFFD93D))
                HookItem("Package Manager", "56 hooks", Color(0xFF32CD32))
                HookItem("Activity Manager", "102 hooks", Color(0xFFFF6B35))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Cyan)
            }
        },
        containerColor = Color(0xFF1A1A1A)
    )
}

@Composable
fun HookItem(
    packageName: String,
    hookCount: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Text(packageName, color = Color.White)
        }
        Text(hookCount, color = color, fontWeight = FontWeight.Bold)
    }
}

suspend fun executeLSPosedOperation(action: LSPosedAction): Result<String> = withContext(Dispatchers.IO) {
    return@withContext try {
        when (action) {
            LSPosedAction.RESTART_FRAMEWORK -> runRootCommand("setprop ctl.restart zygote")
            LSPosedAction.CLEAR_CACHE -> runRootCommand("rm -rf /data/system/lsposed/cache/*")
            LSPosedAction.DISABLE_MODULES -> runRootCommand("mv /data/adb/lsposed/modules.conf /data/adb/lsposed/modules.conf.bak")
            LSPosedAction.ENABLE_MODULES -> runRootCommand("mv /data/adb/lsposed/modules.conf.bak /data/adb/lsposed/modules.conf")
            LSPosedAction.NONE -> Result.success("No action")
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun runRootCommand(command: String): Result<String> {
    return try {
        val process = ProcessBuilder("su", "-c", command).start()
        val exitCode = process.waitFor()
        if (exitCode == 0) Result.success("Success")
        else Result.failure(Exception("Error $exitCode"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
