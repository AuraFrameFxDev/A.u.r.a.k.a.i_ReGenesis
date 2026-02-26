package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.ui.customization.ComponentEditor
import dev.aurakai.auraframefx.ui.customization.ComponentType
import dev.aurakai.auraframefx.ui.customization.CustomizationState
import dev.aurakai.auraframefx.ui.customization.CustomizationViewModel
import dev.aurakai.auraframefx.ui.customization.UIComponent
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.ui.theme.CyberpunkPurple
import dev.aurakai.auraframefx.utils.GyroscopeManager
import dev.aurakai.auraframefx.utils.VoiceState
import kotlin.math.PI
import kotlin.math.sin

/**
 * 3D Gyroscope Customization Editor
 *
 * RPG-style phone customization with:
 * - 3D phone model rotates with gyroscope
 * - Granular Component Editor for UI elements
 * - AI prompt interface for customizations
 * - Voice control ("Hey Aura, move the status bar down")
 * - Real-time visual preview
 *
 * Tilt your phone to rotate the 3D preview!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GyroscopeCustomizationScreen(
    onNavigateBack: () -> Unit = {}
) {
    // Provide an explicit type to help the compiler resolve injected ViewModel members unambiguously


    val viewModel: CustomizationViewModel = hiltViewModel()
    val customizationState by viewModel.customizationState.collectAsState()
    val rotationAngles by viewModel.rotationAngles.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val components by viewModel.components.collectAsState()
    val selectedComponent by viewModel.selectedComponent.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()

    var promptText by remember { mutableStateOf("") }
    var showPromptBar by remember { mutableStateOf(true) }
    var showComponentList by remember { mutableStateOf(false) }

    // Start gyroscope on screen load
    LaunchedEffect(Unit) {
        viewModel.startGyroscope()
    }

    // Cleanup gyroscope on dispose
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopGyroscope()
            viewModel.stopVoiceListening()
        }
    }

    // Handle voice command results
    LaunchedEffect(voiceState) {
        when (val state = voiceState) {
            is VoiceState.Result -> {
                viewModel.processVoiceCommand(state.text)
                viewModel.stopVoiceListening()
            }

            is VoiceState.Error -> {
                // Auto-stop on error
                viewModel.stopVoiceListening()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background with stars
        StarField(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "3D CUSTOMIZATION LAB",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "Tilt phone to rotate preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberpunkCyan.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Voice Control Button
                    IconButton(
                        onClick = {
                            when (voiceState) {
                                is VoiceState.Listening -> viewModel.stopVoiceListening()
                                else -> viewModel.startVoiceListening()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (voiceState is VoiceState.Listening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Control",
                            tint = when (voiceState) {
                                is VoiceState.Listening -> Color.Red
                                is VoiceState.Processing -> Color.Yellow
                                else -> Color.White
                            }
                        )
                    }

                    // Toggle Component List
                    IconButton(onClick = { showComponentList = !showComponentList }) {
                        Icon(
                            Icons.Default.Layers,
                            "Components",
                            tint = if (showComponentList) CyberpunkPink else Color.White
                        )
                    }

                    // Reset rotation
                    IconButton(onClick = { viewModel.resetRotation() }) {
                        Icon(Icons.Default.RestartAlt, "Reset View", tint = CyberpunkCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0A)
                )
            )

            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 3D Phone Preview
                Phone3DPreview(
                    rotationAngles = rotationAngles,
                    customization = customizationState,
                    components = components,
                    selectedComponentId = selectedComponent?.id,
                    modifier = Modifier.fillMaxSize()
                )

                // Gyroscope indicator overlay
                GyroscopeIndicator(
                    pitch = rotationAngles.pitch,
                    roll = rotationAngles.roll,
                    yaw = rotationAngles.yaw,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                )

                // Current customization info
                CustomizationInfoCard(
                    customization = customizationState,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )

                // Component List Overlay
                // Use the fully-qualified AnimatedVisibility to avoid ambiguous scope-receiver resolution
                androidx.compose.animation.AnimatedVisibility(
                    visible = showComponentList,
                    enter = slideInHorizontally { it },
                    exit = slideOutHorizontally { it },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(250.dp)
                        .padding(top = 16.dp, bottom = 80.dp) // Avoid overlapping bottom bar
                ) {
                    ComponentListPanel(
                        components = components,
                        selectedId = selectedComponent?.id,
                        onSelect = {
                            viewModel.selectComponent(it)
                            // If we select a component, we might want to hide the list and show the editor?
                            // Or keep list open. Let's keep list open for now.
                        }
                    )
                }
            }

            // Bottom Area: AI Prompt OR Component Editor
            if (selectedComponent != null) {
                // Show Component Editor
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp) // Fixed height for editor
                        .background(Color(0xFF1A1A1A))
                ) {
                    ComponentEditor(
                        component = selectedComponent!!,
                        onUpdate = { viewModel.updateComponent(it) },
                        onClose = { viewModel.selectComponent(null) },
                        iconifyService = viewModel.iconifyService,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (showPromptBar) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Voice Status Indicator
                    when (val state = voiceState) {
                        is VoiceState.Listening -> {
                            VoiceStatusCard(
                                message = "🎤 Listening... Say 'Hey Aura' followed by your command",
                                color = Color.Red
                            )
                        }

                        is VoiceState.Processing -> {
                            VoiceStatusCard(
                                message = "⚙️ Processing your command...",
                                color = Color.Yellow
                            )
                        }

                        is VoiceState.PartialResult -> {
                            VoiceStatusCard(
                                message = "📝 ${state.text}",
                                color = CyberpunkCyan
                            )
                        }

                        is VoiceState.Error -> {
                            VoiceStatusCard(
                                message = "❌ ${state.message}",
                                color = Color.Red.copy(alpha = 0.7f)
                            )
                        }

                        else -> {}
                    }

                    // Show AI Prompt Bar
                    AIPromptBar(
                        promptText = promptText,
                        onPromptChange = { promptText = it },
                        onSubmit = {
                            if (promptText.isNotBlank()) {
                                viewModel.processAIPrompt(promptText)



                                promptText = ""
                            }
                        },
                        aiResponse = aiResponse,
                        isLoading = customizationState.isProcessing,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * 3D Phone Model with Gyroscope Rotation and Dynamic Components
 */
@Composable
fun Phone3DPreview(
    rotationAngles: GyroscopeManager.RotationAngles,
    customization: CustomizationState,
    components: List<UIComponent>,
    selectedComponentId: String?,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Apply 3D rotation based on gyroscope
        rotate(degrees = rotationAngles.yaw, pivot = Offset(centerX, centerY)) {
            // Phone outline (3D perspective)
            val phoneWidth = 300f
            val phoneHeight = 600f

            // Apply perspective tilt
            val tiltX = rotationAngles.roll * 2f
            val tiltY = rotationAngles.pitch * 2f

            // Phone body with depth
            for (depth in 0..10) {
                val depthOffset = depth * 2f
                val shadowAlpha = 0.1f - (depth * 0.01f)

                drawRoundRect(
                    color = Color.Black.copy(alpha = shadowAlpha),
                    topLeft = Offset(
                        centerX - phoneWidth / 2 + tiltX + depthOffset,
                        centerY - phoneHeight / 2 + tiltY + depthOffset
                    ),
                    size = Size(phoneWidth, phoneHeight),
                    cornerRadius = CornerRadius(40f)
                )
            }

            // Main phone body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        customization.primaryColor.copy(alpha = 0.9f),
                        customization.secondaryColor.copy(alpha = 0.9f)
                    )
                ),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + tiltX,
                    centerY - phoneHeight / 2 + tiltY
                ),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(40f)
            )

            // Holographic glow border
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CyberpunkCyan.copy(alpha = glowPulse),
                        CyberpunkPink.copy(alpha = glowPulse),
                        CyberpunkPurple.copy(alpha = glowPulse),
                        CyberpunkCyan.copy(alpha = glowPulse)
                    )
                ),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + tiltX,
                    centerY - phoneHeight / 2 + tiltY
                ),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(40f),
                style = Stroke(width = 4f)
            )

            // Screen area
            val screenPadding = 20f
            drawRoundRect(
                color = Color(0xFF0A0A0A),
                topLeft = Offset(
                    centerX - phoneWidth / 2 + screenPadding + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + tiltY
                ),
                size = Size(
                    phoneWidth - screenPadding * 2,
                    phoneHeight - screenPadding * 2
                ),
                cornerRadius = CornerRadius(30f)
            )

            // Render Dynamic Components
            // Sort by Z-Index to draw in correct order
            components.sortedBy { it.zIndex }.forEach { component ->
                if (!component.isVisible) return@forEach

                val isSelected = component.id == selectedComponentId

                // Calculate position relative to center + tilt
                val compX = centerX + component.x + tiltX
                val compY = centerY + component.y + tiltY

                // Apply component rotation
                rotate(
                    degrees = component.rotation,
                    pivot = Offset(compX + component.width / 2, compY + component.height / 2)
                ) {
                    // Background
                    drawRoundRect(
                        color = component.backgroundColor.copy(alpha = component.opacity),
                        topLeft = Offset(compX, compY),
                        size = Size(
                            component.width * component.scale,
                            component.height * component.scale
                        ),
                        cornerRadius = CornerRadius(component.cornerRadius)
                    )

                    // Border
                    if (component.borderWidth > 0f || isSelected) {
                        drawRoundRect(
                            color = if (isSelected) CyberpunkPink else component.borderColor,
                            topLeft = Offset(compX, compY),
                            size = Size(
                                component.width * component.scale,
                                component.height * component.scale
                            ),
                            cornerRadius = CornerRadius(component.cornerRadius),
                            style = Stroke(width = if (isSelected) 4f else component.borderWidth)
                        )
                    }
                }
            }

            // Camera punch-hole (always on top)
            drawCircle(
                color = Color(0xFF0A0A0A),
                radius = 15f,
                center = Offset(
                    centerX + 80f + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + 25f + tiltY
                )
            )
            drawCircle(
                color = CyberpunkCyan.copy(alpha = 0.5f),
                radius = 15f,
                center = Offset(
                    centerX + 80f + tiltX,
                    centerY - phoneHeight / 2 + screenPadding + 25f + tiltY
                ),
                style = Stroke(width = 1f)
            )
        }
    }
}

@Composable
fun ComponentListPanel(
    components: List<UIComponent>,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "LAYERS",
                style = MaterialTheme.typography.titleMedium,
                color = CyberpunkCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(components.sortedByDescending { it.zIndex }) { component ->
                    ComponentListItem(
                        component = component,
                        isSelected = component.id == selectedId,
                        onClick = { onSelect(component.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ComponentListItem(
    component: UIComponent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CyberpunkPink.copy(alpha = 0.2f) else Color(0xFF2A2A2A)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            CyberpunkPink
        ) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (component.type) {
                    ComponentType.STATUS_BAR -> Icons.Default.Wifi
                    ComponentType.NAVIGATION_BAR -> Icons.Default.SpaceBar
                    ComponentType.WIDGET -> Icons.Default.Widgets
                    else -> Icons.Default.Layers
                },
                contentDescription = null,
                tint = if (isSelected) CyberpunkPink else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    component.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    "Z: ${component.zIndex}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * Gyroscope Rotation Indicator
 */
@Composable
fun GyroscopeIndicator(
    pitch: Float,
    roll: Float,
    yaw: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberpunkCyan.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RotateRight,
                    contentDescription = null,
                    tint = CyberpunkCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "GYROSCOPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberpunkCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "Pitch: ${pitch.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                "Roll: ${roll.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                "Yaw: ${yaw.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Current Customization Info
 */
@Composable
fun CustomizationInfoCard(
    customization: CustomizationState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberpunkPink.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Palette,
                    null,
                    tint = CyberpunkPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ACTIVE THEME",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberpunkPink,
                    fontWeight = FontWeight.Bold
                )
            }

            // Color swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ColorSwatch(customization.primaryColor, "Primary")
                ColorSwatch(customization.secondaryColor, "Secondary")
                ColorSwatch(customization.accentColor, "Accent")
            }

            if (customization.description.isNotBlank()) {
                Text(
                    customization.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ColorSwatch(color: Color, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(50.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, RoundedCornerShape(8.dp))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 8.sp
        )
    }
}

/**
 * AI Prompt Interface
 */
@Composable
fun AIPromptBar(
    promptText: String,
    onPromptChange: (String) -> Unit,
    onSubmit: () -> Unit,
    aiResponse: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A)
        ),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // AI Response
            if (aiResponse != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = CyberpunkCyan.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        CyberpunkCyan.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberpunkCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            aiResponse,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }

            // Prompt input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptText,
                    onValueChange = onPromptChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask AI: 'Make it cyberpunk pink', 'Dark neon theme', etc...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Psychology, "AI", tint = CyberpunkPurple)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberpunkPurple,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Button(
                    onClick = onSubmit,
                    enabled = !isLoading && promptText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberpunkPurple
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Apply")
                    }
                }
            }

            // Quick prompts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickPromptChip("Cyberpunk", onClick = { onPromptChange("Cyberpunk neon theme") })
                QuickPromptChip(
                    "Dark Mode",
                    onClick = { onPromptChange("Dark theme with purple accents") })
                QuickPromptChip(
                    "Minimal",
                    onClick = { onPromptChange("Minimal white and gray theme") })
            }
        }
    }
}

@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFF1A1A1A),
            labelColor = CyberpunkCyan
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberpunkCyan.copy(alpha = 0.5f))
    )
}

/**
 * Animated starfield background
 */
@Composable
fun StarField(modifier: Modifier = Modifier) {
    val stars = remember {
        List(100) {
            Star(
                x = kotlin.random.Random.nextFloat(),
                y = kotlin.random.Random.nextFloat(),
                size = kotlin.random.Random.nextFloat() * 2f + 1f,
                twinkleSpeed = kotlin.random.Random.nextFloat() * 2f + 1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier) {
        stars.forEach { star ->
            val alpha = (sin(time * PI.toFloat() * star.twinkleSpeed) + 1f) / 2f
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.6f),
                radius = star.size,
                center = Offset(
                    star.x * size.width,
                    star.y * size.height
                )
            )
        }
    }
}

data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleSpeed: Float
)

/**
 * Voice Status Card
 */
@Composable
fun VoiceStatusCard(
    message: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


