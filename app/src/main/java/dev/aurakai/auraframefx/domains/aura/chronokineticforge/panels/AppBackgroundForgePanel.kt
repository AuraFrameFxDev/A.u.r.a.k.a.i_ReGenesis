package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.AppBackgroundConfig
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.RealitymorphismViewModel

/**
 * 🎨 APP BACKGROUND FORGE PANEL
 *
 * Per-app PNG background customization with transparency, blur, and stretch controls.
 */

@Composable
fun AppBackgroundForgePanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedApp by remember { mutableStateOf<String?>(null) }

    val mockApps = listOf(
        "Chrome" to Icons.Default.Language,
        "Instagram" to Icons.Default.Camera,
        "Twitter" to Icons.Default.Chat,
        "Settings" to Icons.Default.Settings,
        "Gallery" to Icons.Default.Image,
        "Music" to Icons.Default.MusicNote
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "🎨 APP BACKGROUND FORGE",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Per-app PNG backgrounds with transparency & blur",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App List
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mockApps.size) { index ->
                val (appName, icon) = mockApps[index]
                val isSelected = selectedApp == appName

                AppBackgroundCard(
                    appName = appName,
                    icon = icon,
                    isSelected = isSelected,
                    config = uiState.appBackgrounds[appName] ?: AppBackgroundConfig(),
                    onClick = { selectedApp = appName }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selected App Editor
        selectedApp?.let { app ->
            val config = uiState.appBackgrounds[app] ?: AppBackgroundConfig()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF00FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Apps,
                            contentDescription = null,
                            tint = Color(0xFFFF00FF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            app,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background Type
                    Text("Background Type", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BackgroundTypeChip(
                            label = "Transparent",
                            selected = config.backgroundType == "transparent",
                            onClick = { /* Set transparent */ }
                        )
                        BackgroundTypeChip(
                            label = "Solid",
                            selected = config.backgroundType == "solid",
                            onClick = { /* Set solid */ }
                        )
                        BackgroundTypeChip(
                            label = "PNG Image",
                            selected = config.backgroundType == "image",
                            onClick = { /* Set image */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Image Selection
                    if (config.backgroundType == "image") {
                        OutlinedButton(
                            onClick = { /* Pick image */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF))
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SELECT PNG BACKGROUND")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Sliders
                    ForgeSlider(
                        label = "Opacity",
                        value = config.opacity,
                        onValueChange = { /* Update */ },
                        valueRange = 0f..1f
                    )

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Blur Behind", color = Color.White)
                        Switch(
                            checked = config.blurBehind,
                            onCheckedChange = { /* Toggle */ },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF00FF),
                                checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Stretch to Fill", color = Color.White)
                        Switch(
                            checked = config.stretch,
                            onCheckedChange = { /* Toggle */ },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF00FF),
                                checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Parallax Effect", color = Color.White)
                        Switch(
                            checked = config.parallax,
                            onCheckedChange = { /* Toggle */ },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF00FF),
                                checkedTrackColor = Color(0xFFFF00FF).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Select an app to customize its background",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun AppBackgroundCard(
    appName: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    config: AppBackgroundConfig,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF00FF) else Color.Transparent,
        label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (config.backgroundType != "transparent")
                            config.color.copy(alpha = config.opacity)
                        else
                            Color(0xFF2A2A2A)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = appName,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    appName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    when (config.backgroundType) {
                        "transparent" -> "Transparent"
                        "solid" -> "Solid color"
                        "image" -> "PNG: ${config.imagePath?.substringAfterLast("/") ?: "None"}"
                        else -> "Default"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFFFF00FF)
                )
            }
        }
    }
}

@Composable
private fun BackgroundTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFFFF00FF).copy(alpha = 0.3f) else Color(0xFF2A2A2A),
        border = BorderStroke(1.dp, if (selected) Color(0xFFFF00FF) else Color.Transparent)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color(0xFFFF00FF) else Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
