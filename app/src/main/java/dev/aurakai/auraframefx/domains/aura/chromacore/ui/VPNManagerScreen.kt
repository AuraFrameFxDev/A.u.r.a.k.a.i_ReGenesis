package dev.aurakai.auraframefx.domains.aura.aura.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VPNManagerScreen() {
    val vpnEnabled = remember { mutableStateOf(false) }
    val selectedProfile = remember { mutableStateOf("Work VPN") }
    val autoConnect = remember { mutableStateOf(true) }

    val vpnProfiles = listOf("Work VPN", "Home VPN", "Travel VPN", "Custom VPN")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "🔒 VPN Manager",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF4169E1) // Royal Blue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Manage VPN connections and security profiles",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // VPN Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VpnLock,
                    contentDescription = "VPN Status",
                    tint = if (vpnEnabled.value) Color.Green else Color.Red,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (vpnEnabled.value) "VPN Connected" else "VPN Disconnected",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (vpnEnabled.value) Color.Green else Color.Red
                    )
                    Text(
                        text = "Secure your internet traffic",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = vpnEnabled.value,
                    onCheckedChange = { vpnEnabled.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // VPN Profiles
        Text(
            text = "VPN Profiles",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        vpnProfiles.forEach { profile ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedProfile.value == profile)
                        Color(0xFF4169E1).copy(alpha = 0.2f)
                    else
                        Color.Black.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedProfile.value == profile,
                        onClick = { selectedProfile.value = profile }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = when (profile) {
                                "Work VPN" -> "Corporate network access"
                                "Home VPN" -> "Secure home network"
                                "Travel VPN" -> "Public WiFi protection"
                                else -> "Custom configuration"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    if (selectedProfile.value == profile) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF4169E1),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Auto-connect settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Connection Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auto-connect on startup",
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )
                    Switch(
                        checked = autoConnect.value,
                        onCheckedChange = { autoConnect.value = it }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Automatically connect to selected VPN when app starts",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Connect button
        Button(
            onClick = { /* Handle VPN connection */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (vpnEnabled.value) Color.Red else Color(0xFF4169E1)
            )
        ) {
            Text(
                text = if (vpnEnabled.value) "Disconnect VPN" else "Connect VPN",
                color = Color.White
            )
        }
    }
}
