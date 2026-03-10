package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * Live ROM Editor Screen
 * Real-time system file editing with safety checks
 */
@Composable
fun LiveROMEditorScreen(
    onNavigateBack: () -> Unit = {}
) {
    val systemFiles = listOf(
        SystemFile("/system/build.prop", "Build properties", "Modified", Color(0xFFFF4500)),
        SystemFile("/system/etc/hosts", "Hosts file", "Clean", Color(0xFF32CD32)),
        SystemFile("/system/framework/framework.jar", "Framework JAR", "Modified", Color(0xFFFF4500)),
        SystemFile("/system/app/Settings.apk", "Settings APK", "Clean", Color(0xFF32CD32)),
        SystemFile("/system/lib/libandroid.so", "Android library", "Clean", Color(0xFF32CD32)),
        SystemFile("/system/etc/init.rc", "Init script", "Modified", Color(0xFFFF4500))
    )

    val selectedFile = remember { mutableStateOf<SystemFile?>(null) }
    val isEditing = remember { mutableStateOf(false) }
    val editContent = remember { mutableStateOf("") }

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
            text = "🔴 LIVE ROM EDITOR",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFF4500),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Edit system files in real-time with safety checks",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFF6347).copy(alpha = 0.8f)
        )

        // Safety Warning
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF4500).copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4500))
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
                    tint = Color(0xFFFF4500),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "⚠️ Changes take effect immediately. Backup before editing!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF4500),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // File List
        Text(
            text = "System Files",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(systemFiles) { file ->
                SystemFileCard(
                    file = file,
                    onClick = {
                        selectedFile.value = file
                        editContent.value = "# Sample content for ${file.path}\n# This is a mock file editor\n\nro.build.version.release=14\nro.build.version.sdk=34\n"
                        isEditing.value = true
                    }
                )
            }
        }

        // Edit Dialog
        if (isEditing.value && selectedFile.value != null) {
            AlertDialog(
                onDismissRequest = { isEditing.value = false },
                title = {
                    Text(
                        text = "Edit: ${selectedFile.value?.path}",
                        color = Color(0xFFFF4500)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "File Content:",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editContent.value,
                            onValueChange = { editContent.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color.White
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF4500),
                                unfocusedBorderColor = Color(0xFFFF4500).copy(alpha = 0.5f),
                                cursorColor = Color(0xFFFF4500)
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Mock save operation
                            isEditing.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF32CD32)
                        )
                    ) {
                        Text("Save Changes", color = Color.Black)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { isEditing.value = false },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF4500)
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Color.Black,
                titleContentColor = Color(0xFFFF4500)
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* Create backup */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFFD700)
                )
            ) {
                Text("Create Backup")
            }
            Button(
                onClick = { /* Restore from backup */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700)
                )
            ) {
                Text("Restore Backup", color = Color.Black)
            }
        }
    }
}

/**
 * System file card component
 */
@Composable
private fun SystemFileCard(
    file: SystemFile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, file.statusColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = "File",
                tint = file.statusColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.path,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = file.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Status Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = file.statusColor.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = file.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = file.statusColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Data class for system files
 */
data class SystemFile(
    val path: String,
    val description: String,
    val status: String,
    val statusColor: Color
)
