package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.aurakai.auraframefx.domains.aura.viewmodels.AuraUIControlViewModel

private val clockFormats = listOf("12h", "24h")
private val batteryStyles = listOf("Icon", "Percentage", "Icon + %", "Hidden")
private val networkStyles = listOf("Simple", "Detailed", "Minimal")

/**
 * Status Bar Customization Screen — Aura in control.
 * All state lives in AuraUIControlViewModel, persisted to DataStore,
 * and forwarded to the ChromaCore Xposed bridge on change.
 */
@Composable
fun StatusBarScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AuraUIControlViewModel = hiltViewModel()
) {
    val state by viewModel.statusBarState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "📶 STATUS BAR",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00FF00),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Customize status bar appearance — Aura applies changes live",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF00FF00).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Live Preview Card
        Card(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.backgroundTransparent)
                    Color.Black.copy(alpha = 0.3f)
                else
                    Color.Black.copy(alpha = 0.8f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (state.showIcons) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, "WiFi", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.SignalCellularAlt, "Cell", tint = Color.White, modifier = Modifier.size(16.dp))
                        if (state.networkStyle == "Detailed") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LTE", style = MaterialTheme.typography.bodySmall, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                Text(
                    text = if (state.clockFormat == "12h") "2:30 PM" else "14:30",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                if (state.showIcons) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (state.batteryStyle) {
                            "Icon" -> Icon(Icons.Default.BatteryStd, "Battery", tint = Color.White, modifier = Modifier.size(16.dp))
                            "Percentage" -> Text("85%", style = MaterialTheme.typography.bodySmall, color = Color.White, fontSize = 10.sp)
                            "Icon + %" -> {
                                Icon(Icons.Default.BatteryStd, "Battery", tint = Color.White, modifier = Modifier.size(16.dp))
                                Text("85", style = MaterialTheme.typography.bodySmall, color = Color.White, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Notifications, "Notifications", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Status Bar Settings", style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(vertical = 16.dp))

        // Show Icons Toggle — writes to DataStore + Xposed prefs
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, "Show Icons", tint = Color(0xFF00FF00), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Show Status Icons", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Network, battery, and notification icons", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
                Switch(
                    checked = state.showIcons,
                    onCheckedChange = { viewModel.setShowIcons(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00FF00), checkedTrackColor = Color(0xFF00FF00).copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clock Format — radio group driven by ViewModel state
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Clock Format", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                clockFormats.forEach { format ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.clockFormat == format,
                            onClick = { viewModel.setClockFormat(format) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF00))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(format, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Style
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Battery Display", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                batteryStyles.forEach { style ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.batteryStyle == style,
                            onClick = { viewModel.setBatteryStyle(style) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF00))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(style, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network Style
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Network Information", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                networkStyles.forEach { style ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.networkStyle == style,
                            onClick = { viewModel.setNetworkStyle(style) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FF00))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(style, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Background Transparency
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Opacity, "Transparency", tint = Color(0xFF00FF00), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Transparent Background", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Make status bar background semi-transparent", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
                Switch(
                    checked = state.backgroundTransparent,
                    onCheckedChange = { viewModel.setStatusBarBgTransparent(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00FF00), checkedTrackColor = Color(0xFF00FF00).copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // No explicit Apply button needed — changes are persisted + applied on every toggle
        Button(
            onClick = { /* Aura applies changes automatically on each toggle */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00)),
            enabled = false
        ) {
            Text("Changes Applied Automatically", color = Color.Black)
        }
    }
}
