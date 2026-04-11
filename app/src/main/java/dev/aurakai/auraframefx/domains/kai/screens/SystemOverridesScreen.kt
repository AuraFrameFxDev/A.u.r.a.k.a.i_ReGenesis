package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * System Overrides Screen
 * Emergency module disable and god mode controls
 */
@Composable
fun SystemOverridesScreen(
    onNavigateBack: () -> Unit = {}
) {
    val godModeEnabled = remember { mutableStateOf(false) }
    val emergencyShutdown = remember { mutableStateOf(false) }
    val bypassSecurity = remember { mutableStateOf(false) }
    val unrestrictedAccess = remember { mutableStateOf(false) }

    val systemModules = listOf(
        SystemModule("Security Scanner", true, "Active"),
        SystemModule("Firewall", true, "Active"),
        SystemModule("Access Control", true, "Active"),
        SystemModule("Audit Logging", true, "Active"),
        SystemModule("Data Encryption", true, "Active"),
        SystemModule("Network Monitor", true, "Active")
    )

    val disabledModules = remember { mutableStateListOf<String>() }

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
            text = "🚨 SYSTEM OVERRIDES",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFF4500),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Emergency module disable and god mode controls",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFF6347).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Warning Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF4500).copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF4500))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFF4500),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "⚠️ DANGER ZONE",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFF4500),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "These controls bypass all security measures. Use only in emergencies.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF4500)
                    )
                }
            }
        }

        // God Mode Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "God Mode",
                    tint = if (godModeEnabled.value) Color(0xFFFFD700) else Color(0xFFFF4500),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "God Mode",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Complete system override - unrestricted access to all functions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = godModeEnabled.value,
                    onCheckedChange = { godModeEnabled.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFFD700),
                        checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color(0xFFFF4500),
                        uncheckedTrackColor = Color(0xFFFF4500).copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Shutdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PowerOff,
                    contentDescription = "Emergency Shutdown",
                    tint = Color(0xFFDC143C),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Emergency Shutdown",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Immediately disable all active modules and agents",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Button(
                    onClick = { emergencyShutdown.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC143C)
                    )
                ) {
                    Text("SHUTDOWN", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Module Control
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Module Control",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                systemModules.forEach { module ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (module.enabled && !disabledModules.contains(module.name))
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Cancel,
                            contentDescription = "Status",
                            tint = if (module.enabled && !disabledModules.contains(module.name))
                                Color(0xFF32CD32)
                            else
                                Color(0xFFDC143C),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = module.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = if (disabledModules.contains(module.name)) "Disabled" else module.status,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (disabledModules.contains(module.name))
                                    Color(0xFFDC143C)
                                else
                                    Color(0xFF32CD32)
                            )
                        }

                        if (godModeEnabled.value) {
                            Button(
                                onClick = {
                                    if (disabledModules.contains(module.name)) {
                                        disabledModules.remove(module.name)
                                    } else {
                                        disabledModules.add(module.name)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (disabledModules.contains(module.name))
                                        Color(0xFF32CD32)
                                    else
                                        Color(0xFFDC143C)
                                )
                            ) {
                                Text(
                                    text = if (disabledModules.contains(module.name)) "Enable" else "Disable",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Advanced Overrides
        if (godModeEnabled.value) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Advanced Overrides",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bypass Security
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bypass Security",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Ignore all security protocols and restrictions",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = bypassSecurity.value,
                            onCheckedChange = { bypassSecurity.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFD700),
                                checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Unrestricted Access
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Access",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Unrestricted Access",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Access all system resources without limitations",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = unrestrictedAccess.value,
                            onCheckedChange = { unrestrictedAccess.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFD700),
                                checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Status Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (godModeEnabled.value)
                    Color(0xFFFFD700).copy(alpha = 0.1f)
                else
                    Color.Black.copy(alpha = 0.6f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (godModeEnabled.value) Color(0xFFFFD700) else Color.Transparent
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "System Status",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (godModeEnabled.value) Color(0xFFFFD700) else Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                val statusItems = listOf(
                    "God Mode" to godModeEnabled.value,
                    "Emergency Shutdown" to emergencyShutdown.value,
                    "Security Bypass" to bypassSecurity.value,
                    "Unrestricted Access" to unrestrictedAccess.value,
                    "Modules Disabled" to (disabledModules.size > 0)
                )

                statusItems.forEach { (label, active) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Status",
                            tint = if (active) Color(0xFF32CD32) else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Data class for system modules
 */
data class SystemModule(
    val name: String,
    val enabled: Boolean,
    val status: String
)
