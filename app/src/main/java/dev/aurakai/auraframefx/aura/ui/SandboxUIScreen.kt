package dev.aurakai.auraframefx.aura.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Aura's Sandbox UI - Experimental Component Testing Lab
 *
 * A visual playground for testing and previewing UI components,
 * animations, and design patterns before integrating them into
 * the main application.
 *
 * Features:
 * - Component gallery with live previews
 * - Animation testing playground
 * - Color scheme experiments
 * - Typography samples
 * - Material3 component showcase
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxUIScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Components", "Animations", "Colors", "Typography")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Aura's Sandbox UI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Experimental Component Lab",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content for each tab
            when (selectedTab) {
                0 -> ComponentsTab()
                1 -> AnimationsTab()
                2 -> ColorsTab()
                3 -> TypographyTab()
            }
        }
    }
}

@Composable
private fun ComponentsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Component Showcase",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Buttons Section
        item {
            ComponentSection("Buttons") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {}) { Text("Primary") }
                    OutlinedButton(onClick = {}) { Text("Outlined") }
                    TextButton(onClick = {}) { Text("Text") }
                }
            }
        }

        // Cards Section
        item {
            ComponentSection("Cards") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Sample Card",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "This is a Material3 card component with custom styling.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Chips Section
        item {
            ComponentSection("Chips") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Assist") },
                        leadingIcon = { Icon(Icons.Default.Star, null, Modifier.size(18.dp)) }
                    )
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Filter") }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Suggest") }
                    )
                }
            }
        }

        // Progress Indicators
        item {
            ComponentSection("Progress") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { 0.7f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        CircularProgressIndicator(progress = { 0.6f })
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimationsTab() {
    val infiniteTransition = rememberInfiniteTransition(label = "sandbox")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Magenta,
        targetValue = Color.Cyan,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    var expanded by remember { mutableStateOf(false) }
    val size by animateFloatAsState(
        targetValue = if (expanded) 200f else 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "size"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                "Animation Playground",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ComponentSection("Infinite Color Animation") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(animatedColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Infinite Transition",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            ComponentSection("Spring Animation") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(size.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { expanded = !expanded },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tap Me",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Size: ${size.toInt()}dp",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            PulsingCard()
        }
    }
}

@Composable
private fun PulsingCard() {
    var scale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            scale = 1.05f
            delay(500)
            scale = 1f
            delay(500)
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(500),
        label = "pulse"
    )

    ComponentSection("Pulsing Animation") {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding((animatedScale * 8).dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Pulsing Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ColorsTab() {
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Material3 Color Scheme",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(getColorSamples(colorScheme)) { sample ->
            ColorSwatch(sample.name, sample.color)
        }
    }
}

@Composable
private fun TypographyTab() {
    val typography = MaterialTheme.typography

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Typography Samples",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item { Text("Display Large", style = typography.displayLarge) }
        item { Text("Display Medium", style = typography.displayMedium) }
        item { Text("Display Small", style = typography.displaySmall) }
        item { Text("Headline Large", style = typography.headlineLarge) }
        item { Text("Headline Medium", style = typography.headlineMedium) }
        item { Text("Headline Small", style = typography.headlineSmall) }
        item { Text("Title Large", style = typography.titleLarge) }
        item { Text("Title Medium", style = typography.titleMedium) }
        item { Text("Title Small", style = typography.titleSmall) }
        item { Text("Body Large", style = typography.bodyLarge) }
        item { Text("Body Medium", style = typography.bodyMedium) }
        item { Text("Body Small", style = typography.bodySmall) }
        item { Text("Label Large", style = typography.labelLarge) }
        item { Text("Label Medium", style = typography.labelMedium) }
        item { Text("Label Small", style = typography.labelSmall) }
    }
}

@Composable
private fun ComponentSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

/**
 * Displays a horizontal color swatch with a label and its ARGB hex value.
 *
 * @param name The display label for the swatch.
 * @param color The color to present; its ARGB hex string is shown below the label.
 */
@Composable
private fun ColorSwatch(name: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                "#%08X".format(color.toArgb()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private data class ColorSample(val name: String, val color: Color)

private fun getColorSamples(scheme: ColorScheme) = listOf(
    ColorSample("Primary", scheme.primary),
    ColorSample("On Primary", scheme.onPrimary),
    ColorSample("Primary Container", scheme.primaryContainer),
    ColorSample("On Primary Container", scheme.onPrimaryContainer),
    ColorSample("Secondary", scheme.secondary),
    ColorSample("On Secondary", scheme.onSecondary),
    ColorSample("Secondary Container", scheme.secondaryContainer),
    ColorSample("On Secondary Container", scheme.onSecondaryContainer),
    ColorSample("Tertiary", scheme.tertiary),
    ColorSample("On Tertiary", scheme.onTertiary),
    ColorSample("Tertiary Container", scheme.tertiaryContainer),
    ColorSample("On Tertiary Container", scheme.onTertiaryContainer),
    ColorSample("Error", scheme.error),
    ColorSample("On Error", scheme.onError),
    ColorSample("Surface", scheme.surface),
    ColorSample("On Surface", scheme.onSurface),
    ColorSample("Background", scheme.background),
    ColorSample("On Background", scheme.onBackground)
)
