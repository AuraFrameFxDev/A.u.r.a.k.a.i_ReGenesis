package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
 * Status Bar Customization Screen
 * Configure icons, clock, and battery styles
 */
@Composable
fun StatusBarScreen() {
    val showIcons = remember { mutableStateOf(true) }
    val clockFormat = remember { mutableStateOf("24h") }
    val batteryStyle = remember { mutableStateOf("Icon") }
    val networkStyle = remember { mutableStateOf("Detailed") }
    val backgroundTransparent = remember { mutableStateOf(false) }

    val clockFormats = listOf("12h", "24h")
    val batteryStyles = listOf("Icon", "Percentage", "Icon + %", "Hidden")
    val networkStyles = listOf("Simple", "Detailed", "Minimal")

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
            text = "📶 STATUS BAR",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00FF00),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Customize status bar appearance and information",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF00FF00).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (backgroundTransparent.value)
                    Color.Black.copy(alpha = 0.3f)
                else
                    Color.Black.copy(alpha = 0.8f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side - Network/Carrier
                if (showIcons.value) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "WiFi",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = "Cellular",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        if (networkStyle.value == "Detailed") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LTE",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Center - Clock
                Text(
                    text = when (clockFormat.value) {
                        "12h" -> "2:30 PM"
                        else -> "14:30"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Right side - Battery/Notifications
                if (showIcons.value) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (batteryStyle.value) {
                            "Icon" -> Icon(
                                imageVector = Icons.Default.BatteryStd,
                                contentDescription = "Battery",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            "Percentage" -> Text(
                                text = "85%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                            "Icon + %" -> {
                                Icon(
                                    imageVector = Icons.Default.BatteryStd,
                                    contentDescription = "Battery",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "85",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controls
        Text(
            text = "Status Bar Settings",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Show Icons Toggle
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
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Show Icons",
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Show Status Icons",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Display network, battery, and notification icons",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = showIcons.value,
                    onCheckedChange = { showIcons.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FF00),
                        checkedTrackColor = Color(0xFF00FF00).copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clock Format
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Clock Format",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                clockFormats.forEach { format ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = clockFormat.value == format,
                            onClick = { clockFormat.value = format },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00FF00)
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = format,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Style
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Battery Display",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                batteryStyles.forEach { style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = batteryStyle.value == style,
                            onClick = { batteryStyle.value = style },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00FF00)
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = style,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network Style
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Network Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                networkStyles.forEach { style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = networkStyle.value == style,
                            onClick = { networkStyle.value = style },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00FF00)
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = style,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Background Transparency
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
                    imageVector = Icons.Default.Opacity,
                    contentDescription = "Transparency",
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Transparent Background",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Make status bar background semi-transparent",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = backgroundTransparent.value,
                    onCheckedChange = { backgroundTransparent.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FF00),
                        checkedTrackColor = Color(0xFF00FF00).copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Apply Button
        Button(
            onClick = { /* Apply status bar settings */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FF00)
            )
        ) {
            Text("Apply Changes", color = Color.Black)
        }
    }
}
