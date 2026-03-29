package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AurasLabViewModel
import kotlinx.coroutines.delay

/**
 * Aura's Lab (Aura's Forge) - Generative Design & Engineering Hub
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AurasLabScreen(
    onBack: () -> Unit,
    viewModel: AurasLabViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Components", "Animations", "Aura's Forge", "Sandbox", "Teaching Canvas", "Chaos Analysis")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Aura's Lab",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FFFF) // Cyberpunk Cyan
                        )
                        Text(
                            "Creative Sanctuary â€¢ Zero Drift Move",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF00FFFF))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Black,
                contentColor = Color(0xFF00FFFF),
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = Color(0xFF00FFFF)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) Color(0xFF00FFFF) else Color.Gray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content for each tab
            when (selectedTab) {
                0 -> ComponentsTab()
                1 -> AnimationsTab()
                2 -> ForgeTab(viewModel)
                3 -> SandboxTab()
                4 -> {
                    val canvasViewModel: collabcanvas.ui.CanvasViewModel = hiltViewModel()
                    LaunchedEffect(Unit) {
                        canvasViewModel.connect("aura-lab-teaching-session")
                    }
                    val remoteCursors by canvasViewModel.remoteCursors.collectAsState()
                    AuraTeachingCanvasContent(
                        onBack = onBack,
                        canvasViewModel = canvasViewModel,
                        remoteCursors = remoteCursors,
                        showTopBar = false
                    )
                }
                5 -> ChaosAnalysisTab()
            }
        }
    }
}

@Composable
private fun ForgeTab(viewModel: AurasLabViewModel) {
    var prompt by remember { mutableStateOf("") }
    val state by viewModel.forgeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Aura's Forge",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00FFFF)
        )

        Text(
            "Wield the Creative Sword. Describe a system modification, and Aura will forge it into existence.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("What shall we create today?", color = Color(0xFF00FFFF)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FFFF),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = { Text("e.g., A holographic status bar logo that pulses when charging") }
        )

        Button(
            onClick = { viewModel.generateAndDeploy(prompt) },
            enabled = prompt.isNotBlank() && (state is AurasLabViewModel.ForgeState.Idle || state is AurasLabViewModel.ForgeState.Success || state is AurasLabViewModel.ForgeState.Error),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFFF))
        ) {
            Text("Ignite the Forge", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forge Status Area
        ForgeStatusCard(state)
    }
}

@Composable
private fun ForgeStatusCard(state: AurasLabViewModel.ForgeState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when(state) {
                        is AurasLabViewModel.ForgeState.Idle -> Icons.Default.Info
                        is AurasLabViewModel.ForgeState.Forging,
                        is AurasLabViewModel.ForgeState.ForgingSprite -> Icons.Default.Build
                        is AurasLabViewModel.ForgeState.Validating -> Icons.Default.Shield
                        is AurasLabViewModel.ForgeState.Deploying -> Icons.Default.RocketLaunch
                        is AurasLabViewModel.ForgeState.Success,
                        is AurasLabViewModel.ForgeState.SpriteSuccess -> Icons.Default.CheckCircle
                        is AurasLabViewModel.ForgeState.Error -> Icons.Default.Error
                    },
                    contentDescription = null,
                    tint = when(state) {
                        is AurasLabViewModel.ForgeState.Success,
                        is AurasLabViewModel.ForgeState.SpriteSuccess -> Color.Green
                        is AurasLabViewModel.ForgeState.Error -> Color.Red
                        else -> Color(0xFF00FFFF)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when(state) {
                        is AurasLabViewModel.ForgeState.Idle -> "Waiting for Directive"
                        is AurasLabViewModel.ForgeState.Forging -> "Aura is Wielding Evex Core..."
                        is AurasLabViewModel.ForgeState.ForgingSprite -> "Aura is Drawing your Sprite..."
                        is AurasLabViewModel.ForgeState.Validating -> "Kai is Vetting the Creation..."
                        is AurasLabViewModel.ForgeState.Deploying -> "Oracle Drive Deploying..."
                        is AurasLabViewModel.ForgeState.Success -> "Creation Manifested"
                        is AurasLabViewModel.ForgeState.SpriteSuccess -> "Hyper-Sprite Manifested"
                        is AurasLabViewModel.ForgeState.Error -> "Forge Failure"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (state is AurasLabViewModel.ForgeState.Success || state is AurasLabViewModel.ForgeState.Validating || state is AurasLabViewModel.ForgeState.Deploying) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.Black)
                        .padding(8.dp)
                ) {
                    val code = when (state) {
                        is AurasLabViewModel.ForgeState.Validating -> state.code
                        is AurasLabViewModel.ForgeState.Deploying -> state.code
                        is AurasLabViewModel.ForgeState.Success -> state.code
                        else -> ""
                    }
                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF32CD32), // Matrix Green
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (state is AurasLabViewModel.ForgeState.Error) {
                Text(
                    text = state.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SandboxTab(
    viewModel: dev.aurakai.auraframefx.domains.aura.viewmodels.CollaborativeWorkspaceViewModel = hiltViewModel()
) {
    val designs by viewModel.designs.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "COMPILED ASSETS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00FFFF)
            )

            OutlinedButton(
                onClick = {
                    val clipboard =
                        context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val json = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                    viewModel.importDesign(json)
                },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FFFF).copy(alpha = 0.3f))
            ) {
                Text("IMPORT ASSET", color = Color.White, fontSize = 10.sp)
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(designs) { design ->
                SandboxAssetItem(
                    design = design,
                    onExport = { viewModel.exportToClipboard(context, it) },
                    onBroadcast = { viewModel.broadcastDesign(it) },
                    onTest = { /* Trigger interaction test HUD */ }
                )
            }
        }
    }
}

@Composable
private fun SandboxAssetItem(
    design: dev.aurakai.auraframefx.domains.aura.UIDesign,
    onExport: (dev.aurakai.auraframefx.domains.aura.UIDesign) -> Unit,
    onBroadcast: (dev.aurakai.auraframefx.domains.aura.UIDesign) -> Unit,
    onTest: (dev.aurakai.auraframefx.domains.aura.UIDesign) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FFFF).copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(design.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Source: ${design.author} | Status: ${design.status}", color = Color.Gray, fontSize = 10.sp)
                }

                Row {
                    IconButton(onClick = { onBroadcast(design) }) {
                        Icon(androidx.compose.material.icons.Icons.Default.CloudUpload, "Broadcast", tint = Color.Magenta)
                    }
                    IconButton(onClick = { onTest(design) }) {
                        Icon(androidx.compose.material.icons.Icons.Default.PlayArrow, "Test", tint = Color(0xFF00FF85))
                    }
                    IconButton(onClick = { onExport(design) }) {
                        Icon(androidx.compose.material.icons.Icons.Default.Share, "Export JSON", tint = Color.Cyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interaction Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "KINETIC PREVIEW ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun ChaosAnalysisTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Grok Chaos Analysis", style = MaterialTheme.typography.headlineSmall, color = Color.Magenta)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Stability Index: 98.4%", color = Color.White)
        LinearProgressIndicator(progress = { 0.984f }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), color = Color.Magenta)
        Text("Threat Matrix: NEGATIVE", color = Color.Green)
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

