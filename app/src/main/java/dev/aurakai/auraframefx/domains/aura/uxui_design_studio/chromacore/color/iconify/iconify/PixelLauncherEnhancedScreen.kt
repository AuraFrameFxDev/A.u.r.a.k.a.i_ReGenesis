package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPurple

/**
 * 🚀 PixelLauncherEnhancedScreen — Enhanced Pixel Launcher Customization
 *
 * Full implementation of the Pixel Launcher theming interface with
 * icon pack selection, grid customization, and gesture configuration.
 */
@Composable
fun PixelLauncherEnhancedScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var gridSize by remember { mutableIntStateOf(4) }
    var iconPack by remember { mutableStateOf("Default") }
    val tabs = listOf("Icons", "Grid", "Gestures", "Backup")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
            .padding(16.dp)
    ) {
        // Header
        PixelLauncherHeader(navController)

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CyberpunkCyan,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) CyberpunkCyan else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when (selectedTab) {
            0 -> IconsTab(
                currentPack = iconPack,
                onPackSelected = { iconPack = it }
            )
            1 -> GridTab(
                gridSize = gridSize,
                onGridSizeChange = { gridSize = it }
            )
            2 -> GesturesTab()
            3 -> BackupTab(navController)
        }
    }
}

@Composable
private fun PixelLauncherHeader(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Pixel Launcher",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CyberpunkPink
            )
            Text(
                "Enhanced Customization",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CyberpunkPurple.copy(alpha = 0.2f))
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = CyberpunkPink
            )
        }
    }
}

@Composable
private fun IconsTab(
    currentPack: String,
    onPackSelected: (String) -> Unit
) {
    val iconPacks = listOf(
        IconPack("Default", "Stock Pixel icons", true, CyberpunkCyan),
        IconPack("Neon", "Glowing neon style", false, CyberpunkPink),
        IconPack("Minimal", "Clean and simple", false, CyberpunkPurple),
        IconPack("Retro", "Classic pixel art", false, Color(0xFFFFD93D)),
        IconPack("Glass", "Transparent glass", false, Color(0xFF4D96FF))
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(iconPacks) { pack ->
            IconPackCard(
                pack = pack,
                isSelected = pack.name == currentPack,
                onSelect = { onPackSelected(pack.name) }
            )
        }
    }
}

@Composable
private fun IconPackCard(
    pack: IconPack,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CyberpunkPurple.copy(alpha = 0.3f) else Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                pack.accentColor
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(pack.accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Apps,
                        contentDescription = null,
                        tint = pack.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        pack.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        pack.description,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = pack.accentColor
                )
            }
        }
    }
}

@Composable
private fun GridTab(
    gridSize: Int,
    onGridSizeChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1F)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Grid Size",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$gridSize x $gridSize icons per page",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = gridSize.toFloat(),
                onValueChange = { onGridSizeChange(it.toInt()) },
                valueRange = 3f..6f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = CyberpunkCyan,
                    activeTrackColor = CyberpunkCyan,
                    inactiveTrackColor = Color(0xFF3A3A3F)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid Preview
            Text(
                "Preview",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            GridPreview(gridSize = gridSize)
        }
    }
}

@Composable
private fun GridPreview(gridSize: Int) {
    val iconCount = gridSize * gridSize

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D0D0D))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(gridSize) { row ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(gridSize) { col ->
                    val index = row * gridSize + col
                    if (index < iconCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberpunkPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = CyberpunkCyan.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GesturesTab() {
    val gestures = listOf(
        GestureConfig("Swipe Up", "Open app drawer", Icons.Default.SwipeUp, true),
        GestureConfig("Swipe Down", "Open notifications", Icons.Default.SwipeDown, true),
        GestureConfig("Double Tap", "Lock screen", Icons.Default.TouchApp, false),
        GestureConfig("Long Press", "Edit mode", Icons.Default.Edit, true)
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(gestures) { gesture ->
            GestureCard(gesture)
        }
    }
}

@Composable
private fun GestureCard(gesture: GestureConfig) {
    var enabled by remember { mutableStateOf(gesture.enabled) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1F)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    gesture.icon,
                    contentDescription = null,
                    tint = if (enabled) CyberpunkCyan else Color.Gray
                )
                Column {
                    Text(
                        gesture.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        gesture.action,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = { enabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberpunkCyan,
                    checkedTrackColor = CyberpunkCyan.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun BackupTab(navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1F)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Backup & Restore",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Save your launcher configuration or restore from a previous backup",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Export backup */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberpunkCyan
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Backup, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Backup")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { /* Import backup */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberpunkPink
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(CyberpunkPink, CyberpunkPurple)
                        )
                    )
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import Backup")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1F)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Reset to Default",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFF6B6B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This will reset all launcher settings to their default values",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { /* Reset */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E))
                        )
                    )
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All Settings")
                }
            }
        }
    }
}

// Data classes
private data class IconPack(
    val name: String,
    val description: String,
    val isDefault: Boolean,
    val accentColor: Color
)

private data class GestureConfig(
    val name: String,
    val action: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val enabled: Boolean
)
