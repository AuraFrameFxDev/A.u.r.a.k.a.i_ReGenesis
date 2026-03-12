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
 * Notch Bar Customization Screen
 * Adjust notch height, style, and visibility
 */
@Composable
fun NotchBarScreen(
    onNavigateBack: () -> Unit = {}
) {
    val notchHeight = remember { mutableStateOf(30f) }
    val notchStyle = remember { mutableStateOf("Rounded") }
    val notchVisible = remember { mutableStateOf(true) }
    val hideNotch = remember { mutableStateOf(false) }

    val notchStyles = listOf("Rounded", "Square", "Pill", "Dynamic")

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
            text = "📱 NOTCH BAR",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00FFFF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Customize notch appearance and behavior",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF00FFFF).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                // Mock status bar with notch
                if (!hideNotch.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(notchHeight.value.dp)
                            .background(
                                color = Color.Black,
                                shape = when (notchStyle.value) {
                                    "Rounded" -> RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                                    "Square" -> RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
                                    "Pill" -> RoundedCornerShape(50.dp)
                                    else -> RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Front Camera",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microphone",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = "Sensors",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Notch Preview",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controls
        Text(
            text = "Notch Settings",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Hide Notch Toggle
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
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Hide Notch",
                    tint = Color(0xFF00FFFF),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hide Notch Completely",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Remove notch from display (may affect functionality)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = hideNotch.value,
                    onCheckedChange = { hideNotch.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FFFF),
                        checkedTrackColor = Color(0xFF00FFFF).copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notch Height Slider
        if (!hideNotch.value) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Height,
                            contentDescription = "Height",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notch Height",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "${notchHeight.value.toInt()} dp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF00FFFF)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = notchHeight.value,
                        onValueChange = { notchHeight.value = it },
                        valueRange = 20f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00FFFF),
                            activeTrackColor = Color(0xFF00FFFF),
                            inactiveTrackColor = Color(0xFF00FFFF).copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notch Style Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notch Style",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    notchStyles.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = notchStyle.value == style,
                                onClick = { notchStyle.value = style },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF00FFFF)
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Apply Button
        Button(
            onClick = { /* Apply notch settings */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FFFF)
            )
        ) {
            Text("Apply Changes", color = Color.Black)
        }
    }
}
