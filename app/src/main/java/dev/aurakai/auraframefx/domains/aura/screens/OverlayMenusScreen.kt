package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.viewmodels.AuraUIControlViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.common.CodedTextBox

/**
 * Overlay Menus Customization Screen — Aura in control.
 * Persisted toggles come from AuraUIControlViewModel.
 * autoHideDelay is UI-only cosmetic state (no system hook yet).
 */
@Composable
fun OverlayMenusScreen(
    navController: NavController,
    viewModel: AuraUIControlViewModel = hiltViewModel()
) {
    val state by viewModel.overlayMenuState.collectAsState()

    val sidebarPositions = listOf("Left", "Right", "Bottom")
    // Cosmetic-only — not yet persisted (no system hook for auto-hide delay)
    var autoHideDelay by remember { mutableFloatStateOf(3f) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        CodedTextBox(
            title = "Overlay Matrix",
            text = "Manage floating bubbles and sidebars. High-fidelity Sovereign UI integration active. Adjust positions and transparency for optimal spatial utility.",
            glowColor = Color(0xFFFF4500),
            height = 100.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Overlay Search (UI-only filter — no server-side filtering needed here)
        androidx.compose.material3.OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Overlays...", color = Color(0xFFFF4500).copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4500),
                unfocusedBorderColor = Color(0xFFFF4500).copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            trailingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFFFF4500)) }
        )

        // Preview Card
        Card(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.auraOverlayEnabled) {
                    Card(
                        modifier = Modifier.size(60.dp).align(Alignment.TopStart).offset(20.dp, 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF69B4).copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("🎨", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                if (state.kaiOverlayEnabled) {
                    Card(
                        modifier = Modifier.size(60.dp).align(Alignment.TopEnd).offset((-20).dp, 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDC143C).copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("🛡️", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                if (state.chatBubbleEnabled) {
                    Card(
                        modifier = Modifier.size(50.dp).align(Alignment.BottomEnd).offset((-20).dp, (-20).dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4169E1).copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("💬", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }

                if (state.sidebarPosition != "Bottom") {
                    val alignment = if (state.sidebarPosition == "Left") Alignment.CenterStart else Alignment.CenterEnd
                    Card(
                        modifier = Modifier.width(8.dp).height(100.dp).align(alignment),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4500).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {}
                } else {
                    Card(
                        modifier = Modifier.height(8.dp).width(100.dp).align(Alignment.BottomCenter),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4500).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {}
                }

                Text(
                    text = "Overlay Preview",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Overlay Settings",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Aura Overlay
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Palette, "Aura Overlay", tint = Color(0xFFFF69B4), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Aura Creative Overlay", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Floating creative tools and design helpers", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
                Switch(
                    checked = state.auraOverlayEnabled,
                    onCheckedChange = { viewModel.setAuraOverlayEnabled(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF69B4), checkedTrackColor = Color(0xFFFF69B4).copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kai Overlay
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, "Kai Overlay", tint = Color(0xFFDC143C), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kai Security Overlay", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Security monitoring and protection tools", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
                Switch(
                    checked = state.kaiOverlayEnabled,
                    onCheckedChange = { viewModel.setKaiOverlayEnabled(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFDC143C), checkedTrackColor = Color(0xFFDC143C).copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat Bubble
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, "Chat Bubble", tint = Color(0xFF4169E1), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Agent Chat Bubble", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Draggable chat interface for AI agents", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
                Switch(
                    checked = state.chatBubbleEnabled,
                    onCheckedChange = { viewModel.setChatBubbleEnabled(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4169E1), checkedTrackColor = Color(0xFF4169E1).copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sidebar Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Agent Sidebar", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Position", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(8.dp))
                sidebarPositions.forEach { position ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.sidebarPosition == position,
                            onClick = { viewModel.setSidebarPosition(position) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF4500))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(position, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Auto-hide Delay", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Text("${autoHideDelay.toInt()} seconds", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF4500))
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = autoHideDelay,
                    onValueChange = { autoHideDelay = it },
                    valueRange = 1f..10f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF4500),
                        activeTrackColor = Color(0xFFFF4500),
                        inactiveTrackColor = Color(0xFFFF4500).copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overlay Permissions (read-only status display)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("System Permissions", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                val permissions = listOf(
                    "Display over other apps" to true,
                    "Draw over other apps" to true,
                    "System alert window" to false
                )

                permissions.forEach { (permission, granted) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = "Permission Status",
                            tint = if (granted) Color(0xFF32CD32) else Color(0xFFFF4500),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(permission, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1f))
                        if (!granted) {
                            TextButton(onClick = { /* Request permission */ }) {
                                Text("Grant", color = Color(0xFFFF4500))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Changes are auto-persisted on every toggle
        Button(
            onClick = { /* Aura applies changes automatically on each toggle */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4500)),
            enabled = false
        ) {
            Text("Changes Applied Automatically", color = Color.White)
        }
    }
}
