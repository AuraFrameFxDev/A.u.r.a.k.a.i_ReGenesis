package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ChromaCore - SYSTEM-WIDE Color Customization
 *
 * Complete Android system color palette editor for rooted devices.
 * Modifies AOSP system colors, Material 3 theme, status bar, nav bar, and all UI elements.
 *
 * This is NOT just app colors - this targets the ENTIRE DEVICE.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaCoreColorsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // Material 3 Color Scheme - ALL 40+ colors
    var primary by remember { mutableStateOf(colorScheme.primary) }
    var onPrimary by remember { mutableStateOf(colorScheme.onPrimary) }
    var primaryContainer by remember { mutableStateOf(colorScheme.primaryContainer) }
    var onPrimaryContainer by remember { mutableStateOf(colorScheme.onPrimaryContainer) }

    var secondary by remember { mutableStateOf(colorScheme.secondary) }
    var onSecondary by remember { mutableStateOf(colorScheme.onSecondary) }
    var secondaryContainer by remember { mutableStateOf(colorScheme.secondaryContainer) }
    var onSecondaryContainer by remember { mutableStateOf(colorScheme.onSecondaryContainer) }

    var tertiary by remember { mutableStateOf(colorScheme.tertiary) }
    var onTertiary by remember { mutableStateOf(colorScheme.onTertiary) }
    var tertiaryContainer by remember { mutableStateOf(colorScheme.tertiaryContainer) }
    var onTertiaryContainer by remember { mutableStateOf(colorScheme.onTertiaryContainer) }

    var error by remember { mutableStateOf(colorScheme.error) }
    var onError by remember { mutableStateOf(colorScheme.onError) }
    var errorContainer by remember { mutableStateOf(colorScheme.errorContainer) }
    var onErrorContainer by remember { mutableStateOf(colorScheme.onErrorContainer) }

    var background by remember { mutableStateOf(colorScheme.background) }
    var onBackground by remember { mutableStateOf(colorScheme.onBackground) }

    var surface by remember { mutableStateOf(colorScheme.surface) }
    var onSurface by remember { mutableStateOf(colorScheme.onSurface) }
    var surfaceVariant by remember { mutableStateOf(colorScheme.surfaceVariant) }
    var onSurfaceVariant by remember { mutableStateOf(colorScheme.onSurfaceVariant) }

    var surfaceTint by remember { mutableStateOf(colorScheme.surfaceTint) }
    var inverseSurface by remember { mutableStateOf(colorScheme.inverseSurface) }
    var inverseOnSurface by remember { mutableStateOf(colorScheme.inverseOnSurface) }
    var inversePrimary by remember { mutableStateOf(colorScheme.inversePrimary) }

    var outline by remember { mutableStateOf(colorScheme.outline) }
    var outlineVariant by remember { mutableStateOf(colorScheme.outlineVariant) }
    var scrim by remember { mutableStateOf(colorScheme.scrim) }

    // System UI Colors (AOSP-specific)
    var statusBarBackground by remember { mutableStateOf(Color(0xFF000000)) }
    var statusBarIcons by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    var navigationBarBackground by remember { mutableStateOf(Color(0xFF000000)) }
    var navigationBarIcons by remember { mutableStateOf(Color(0xFFFFFFFF)) }

    var selectedCategory by remember { mutableStateOf("Primary Colors") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, null, tint = Color.Cyan, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "ChromaCore",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                "SYSTEM-WIDE Color Editor",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Cyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = listOf("Primary Colors", "Secondary Colors", "Tertiary Colors",
                    "Error Colors", "Surface Colors", "System UI").indexOf(selectedCategory),
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.Cyan,
                edgePadding = 0.dp
            ) {
                listOf("Primary Colors", "Secondary Colors", "Tertiary Colors",
                    "Error Colors", "Surface Colors", "System UI").forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedCategory) {
                    "Primary Colors" -> {
                        item {
                            CategoryHeader("Primary Color Family", "Main brand colors used throughout the system")
                        }
                        item { SystemColorCard("Primary", "Primary brand color", primary) { primary = it } }
                        item { SystemColorCard("On Primary", "Text/icons on primary", onPrimary) { onPrimary = it } }
                        item { SystemColorCard("Primary Container", "Contained primary elements", primaryContainer) { primaryContainer = it } }
                        item { SystemColorCard("On Primary Container", "Text on primary containers", onPrimaryContainer) { onPrimaryContainer = it } }
                    }

                    "Secondary Colors" -> {
                        item {
                            CategoryHeader("Secondary Color Family", "Supporting colors for less prominent UI elements")
                        }
                        item { SystemColorCard("Secondary", "Secondary accent color", secondary) { secondary = it } }
                        item { SystemColorCard("On Secondary", "Text/icons on secondary", onSecondary) { onSecondary = it } }
                        item { SystemColorCard("Secondary Container", "Contained secondary elements", secondaryContainer) { secondaryContainer = it } }
                        item { SystemColorCard("On Secondary Container", "Text on secondary containers", onSecondaryContainer) { onSecondaryContainer = it } }
                    }

                    "Tertiary Colors" -> {
                        item {
                            CategoryHeader("Tertiary Color Family", "Contrasting accent colors for visual interest")
                        }
                        item { SystemColorCard("Tertiary", "Tertiary accent color", tertiary) { tertiary = it } }
                        item { SystemColorCard("On Tertiary", "Text/icons on tertiary", onTertiary) { onTertiary = it } }
                        item { SystemColorCard("Tertiary Container", "Contained tertiary elements", tertiaryContainer) { tertiaryContainer = it } }
                        item { SystemColorCard("On Tertiary Container", "Text on tertiary containers", onTertiaryContainer) { onTertiaryContainer = it } }
                    }

                    "Error Colors" -> {
                        item {
                            CategoryHeader("Error Color Family", "Colors for error states and warnings")
                        }
                        item { SystemColorCard("Error", "Error/warning color", error) { error = it } }
                        item { SystemColorCard("On Error", "Text/icons on error", onError) { onError = it } }
                        item { SystemColorCard("Error Container", "Contained error elements", errorContainer) { errorContainer = it } }
                        item { SystemColorCard("On Error Container", "Text on error containers", onErrorContainer) { onErrorContainer = it } }
                    }

                    "Surface Colors" -> {
                        item {
                            CategoryHeader("Surface & Background Colors", "Base colors for backgrounds, cards, and surfaces")
                        }
                        item { SystemColorCard("Background", "Main background", background) { background = it } }
                        item { SystemColorCard("On Background", "Text on background", onBackground) { onBackground = it } }
                        item { SystemColorCard("Surface", "Card/surface color", surface) { surface = it } }
                        item { SystemColorCard("On Surface", "Text on surface", onSurface) { onSurface = it } }
                        item { SystemColorCard("Surface Variant", "Alternative surface", surfaceVariant) { surfaceVariant = it } }
                        item { SystemColorCard("On Surface Variant", "Text on surface variant", onSurfaceVariant) { onSurfaceVariant = it } }
                        item { SystemColorCard("Surface Tint", "Surface tint overlay", surfaceTint) { surfaceTint = it } }
                        item { SystemColorCard("Inverse Surface", "Inverted surface", inverseSurface) { inverseSurface = it } }
                        item { SystemColorCard("Inverse On Surface", "Text on inverted surface", inverseOnSurface) { inverseOnSurface = it } }
                        item { SystemColorCard("Inverse Primary", "Inverted primary", inversePrimary) { inversePrimary = it } }
                        item { SystemColorCard("Outline", "Border/divider color", outline) { outline = it } }
                        item { SystemColorCard("Outline Variant", "Alternative outline", outlineVariant) { outlineVariant = it } }
                        item { SystemColorCard("Scrim", "Overlay scrim", scrim) { scrim = it } }
                    }

                    "System UI" -> {
                        item {
                            CategoryHeader("AOSP System UI Colors", "Status bar, navigation bar, and system-wide UI elements (requires root)")
                        }
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B35).copy(alpha = 0.1f)),
                                modifier = Modifier.fillMaxWidth(),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B35))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("⚠️", fontSize = 20.sp)
                                    Text(
                                        "Root access required to modify system UI colors. Changes will apply device-wide.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFFFF6B35),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                        item { SystemColorCard("Status Bar Background", "Top status bar background", statusBarBackground) { statusBarBackground = it } }
                        item { SystemColorCard("Status Bar Icons", "Status bar icon colors", statusBarIcons) { statusBarIcons = it } }
                        item { SystemColorCard("Navigation Bar Background", "Bottom nav bar background", navigationBarBackground) { navigationBarBackground = it } }
                        item { SystemColorCard("Navigation Bar Icons", "Nav bar button colors", navigationBarIcons) { navigationBarIcons = it } }
                    }
                }
            }

            // Action Bar
            Surface(
                color = Color(0xFF0A0A0A),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: Reset to defaults */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Cyan)
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = { /* TODO: Apply system-wide with root */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Apply System-Wide", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
private fun SystemColorCard(
    title: String,
    description: String,
    color: Color,
    onColorChange: (Color) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color, RoundedCornerShape(8.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // RGB Sliders
            ColorRGBSliders(color = color, onColorChange = onColorChange)
        }
    }
}

@Composable
private fun ColorRGBSliders(color: Color, onColorChange: (Color) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorChannelSlider("R", color.red, Color.Red) { onColorChange(color.copy(red = it)) }
        ColorChannelSlider("G", color.green, Color.Green) { onColorChange(color.copy(green = it)) }
        ColorChannelSlider("B", color.blue, Color.Blue) { onColorChange(color.copy(blue = it)) }
    }
}

@Composable
private fun ColorChannelSlider(
    label: String,
    value: Float,
    trackColor: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.width(20.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = trackColor,
                activeTrackColor = trackColor,
                inactiveTrackColor = trackColor.copy(alpha = 0.2f)
            )
        )

        Text(
            text = "${(value * 255).toInt()}",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f)
            ),
            modifier = Modifier.width(35.dp)
        )
    }
}
