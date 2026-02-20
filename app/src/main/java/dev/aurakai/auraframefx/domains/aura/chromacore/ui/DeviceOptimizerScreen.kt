package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DeviceOptimizerScreen() {
    val ramCleanEnabled = remember { mutableStateOf(true) }
    val batteryOptimizeEnabled = remember { mutableStateOf(true) }
    val storageCleanEnabled = remember { mutableStateOf(false) }

    val ramUsage = remember { mutableStateOf(75) } // Mock data
    val batteryLevel = remember { mutableStateOf(68) } // Mock data
    val storageUsed = remember { mutableStateOf(85) } // Mock data

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "⚡ Device Optimizer",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00FF00) // Lime Green
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Optimize performance, battery life, and storage",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // System Status Cards
        Row(modifier = Modifier.fillMaxWidth()) {
            // RAM Status
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "RAM",
                        tint = Color(0xFF00FF00),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RAM Usage",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Text(
                        text = "${ramUsage.value}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = when {
                            ramUsage.value > 90 -> Color.Red
                            ramUsage.value > 70 -> Color.Yellow
                            else -> Color(0xFF00FF00)
                        }
                    )
                    LinearProgressIndicator(
                        progress = { ramUsage.value / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            ramUsage.value > 90 -> Color.Red
                            ramUsage.value > 70 -> Color.Yellow
                            else -> Color(0xFF00FF00)
                        }
                    )
                }
            }

            // Battery Status
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryStd,
                        contentDescription = "Battery",
                        tint = Color(0xFF00FF00),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Battery",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Text(
                        text = "${batteryLevel.value}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = when {
                            batteryLevel.value < 20 -> Color.Red
                            batteryLevel.value < 50 -> Color.Yellow
                            else -> Color(0xFF00FF00)
                        }
                    )
                    LinearProgressIndicator(
                        progress = { batteryLevel.value / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            batteryLevel.value < 20 -> Color.Red
                            batteryLevel.value < 50 -> Color.Yellow
                            else -> Color(0xFF00FF00)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Storage Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Storage",
                        tint = Color(0xFF00FF00),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Storage Usage",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = "${storageUsed.value}% used",
                            style = MaterialTheme.typography.titleMedium,
                            color = when {
                                storageUsed.value > 90 -> Color.Red
                                storageUsed.value > 80 -> Color.Yellow
                                else -> Color(0xFF00FF00)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { storageUsed.value / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        storageUsed.value > 90 -> Color.Red
                        storageUsed.value > 80 -> Color.Yellow
                        else -> Color(0xFF00FF00)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Optimization Options
        Text(
            text = "Optimization Settings",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        // RAM Cleaner
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
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = "RAM Cleaner",
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RAM Cleaner",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Free up memory by closing background apps",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = ramCleanEnabled.value,
                    onCheckedChange = { ramCleanEnabled.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Optimizer
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
                    imageVector = Icons.Default.BatterySaver,
                    contentDescription = "Battery Optimizer",
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Battery Optimizer",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Extend battery life by managing power usage",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = batteryOptimizeEnabled.value,
                    onCheckedChange = { batteryOptimizeEnabled.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Storage Cleaner
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
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Storage Cleaner",
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Storage Cleaner",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Remove temporary files and cache",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = storageCleanEnabled.value,
                    onCheckedChange = { storageCleanEnabled.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { /* Analyze system */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF00FF00)
                )
            ) {
                Text("Analyze")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { /* Run optimization */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF00)
                )
            ) {
                Text("Optimize Now", color = Color.Black)
            }
        }
    }
}
