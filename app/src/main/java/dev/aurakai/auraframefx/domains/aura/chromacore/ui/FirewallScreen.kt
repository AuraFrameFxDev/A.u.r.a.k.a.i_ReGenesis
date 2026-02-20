package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FirewallScreen() {
    val firewallEnabled = remember { mutableStateOf(true) }
    val blockUnknown = remember { mutableStateOf(false) }
    val logTraffic = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Firewall",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Configure network security, monitor connections, and block potential threats.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Firewall settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Firewall", modifier = Modifier.weight(1f))
            Switch(checked = firewallEnabled.value, onCheckedChange = { firewallEnabled.value = it })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Block Unknown Connections", modifier = Modifier.weight(1f))
            Switch(checked = blockUnknown.value, onCheckedChange = { blockUnknown.value = it })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Log Traffic", modifier = Modifier.weight(1f))
            Switch(checked = logTraffic.value, onCheckedChange = { logTraffic.value = it })
        }
    }
}
