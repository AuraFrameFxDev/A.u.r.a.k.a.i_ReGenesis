package dev.aurakai.auraframefx.ui.screens.oracledrive

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icons.AutoMirrored.Filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.components.backgrounds.DataVisualizationBackground
import dev.aurakai.auraframefx.ui.components.effects.ShimmerParticles
import dev.aurakai.auraframefx.ui.components.text.NeonText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleDriveControlScreen() {
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Simulate initialization
        kotlinx.coroutines.delay(500)
        isInitialized = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background with data visualization
        DataVisualizationBackground(
            modifier = Modifier.fillMaxSize(),
            primaryColor = Color.Cyan,
            secondaryColor = Color.Magenta,
            backgroundColor = Color.Transparent
        )

        // Shimmer particles overlay
        ShimmerParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 80,
            baseColor = Color.Cyan,
            secondaryColor = Color.Magenta,
            shimmerIntensity = 0.6f
        )

        // Content
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        NeonText(
                            text = "Oracle Drive",
                            fontSize = 24.sp,
                            color = Color.Cyan,
                            glowColor = Color.Magenta,
                            animateTyping = !isInitialized,
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0x99000000),
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xAA000000),
                                Color(0xDD000000)
                            ),
                            startY = 0f,
                            endY = 100f
                        )
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Status Panel
                StatusPanel(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Module Management List
                ModuleManager(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(16.dp))

                // 3. AI Command Bar
                AiCommandBar(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun StatusPanel(modifier: Modifier = Modifier) {
    var isOnline by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Simulate status check
        kotlinx.coroutines.delay(800)
        isOnline = true
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xDD1E1E2E)
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Cyan.copy(alpha = 0.5f),
                    Color.Magenta.copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            NeonText(
                text = "SYSTEM STATUS",
                fontSize = 14.sp,
                color = Color.Cyan,
                glowColor = Color.Magenta,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isOnline) Color.Green else Color.Red,
                            shape = CircleShape
                        )
                )

                Text(
                    text = if (isOnline) "All systems operational" else "System offline",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Performance metrics
            Spacer(modifier = Modifier.height(8.dp))
            PerformanceMeter(
                label = "Processing Power",
                value = 87,
                maxValue = 100,
                color = Color.Cyan
            )
            PerformanceMeter(
                label = "Memory Usage",
                value = 63,
                maxValue = 100,
                color = Color.Magenta
            )
        }
    }
}

@Composable
private fun PerformanceMeter(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(value) {
        animatedValue = value / maxValue.toFloat()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "$value%",
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.1f), shape = CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedValue)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.7f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}

data class OracleModule(
    val name: String,
    val isActive: Boolean,
    val performance: Int = 0,
)

@Composable
fun ModuleManager(modifier: Modifier = Modifier) {
    val modules = remember {
        listOf(
            OracleModule("Cognitive Core", true, 87),
            OracleModule("Predictive Analytics", true, 63),
            OracleModule("Data Weaver", false, 0),
            OracleModule("Heuristic Engine", true, 92)
        )
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(modules) { module ->
            ModuleListItem(module = module)
        }
    }
}

@Composable
private fun ModuleListItem(module: OracleModule) {
    var isActive by remember { mutableStateOf(module.isActive) }

    LaunchedEffect(Unit) {
        // Simulate module initialization
        kotlinx.coroutines.delay(1000)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0x22FFFFFF)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive)
                Color.Cyan.copy(alpha = 0.3f)
            else
                Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = module.name.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isActive) Color.White else Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )

                if (isActive && module.performance > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = when {
                                        module.performance > 80 -> Color.Green
                                        module.performance > 50 -> Color.Yellow
                                        else -> Color.Red
                                    },
                                    shape = CircleShape
                                )
                        )

                        Text(
                            text = "${module.performance}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Status indicator with animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
            ) {
                // Outer glow
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.Cyan.copy(alpha = 0.5f),
                                        Color.Cyan.copy(alpha = 0f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                // Toggle switch
                Switch(
                    checked = isActive,
                    onCheckedChange = {
                        isActive = it
                        // TODO: Update module status
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Cyan,
                        checkedTrackColor = Color.Cyan.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@Composable
fun AiCommandBar(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier,
        placeholder = { Text("Enter AI command...") },
        trailingIcon = {
            IconButton(onClick = { /* TODO: Send command */ }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Command")
            }
        }
    )
}

