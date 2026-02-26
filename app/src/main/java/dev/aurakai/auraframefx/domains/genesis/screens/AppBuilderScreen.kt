package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.fusion.ForgeState
import dev.aurakai.auraframefx.domains.genesis.fusion.InterfaceForgeViewModel
import kotlinx.coroutines.delay

/**
 * 🛠️ APP BUILDER (Interface Forge)
 *
 * Collaborative environment for Aura (Design) and Claude (Architecture)
 * to generate application structures and code modules.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBuilderScreen(
    viewModel: InterfaceForgeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var step by remember { mutableIntStateOf(1) }
    var targetArch by remember { mutableStateOf("Mobile (Compose)") }
    var featureName by remember { mutableStateOf("") }

    val forgeState by viewModel.forgeState.collectAsStateWithLifecycle()

    val architectures = listOf("Mobile (Compose)", "Web (Fusion)", "ROM Module", "System Overlay")

    // LDO Way: React to state changes
    val isForging = forgeState is ForgeState.Forging
    val forgeProgress = (forgeState as? ForgeState.Forging)?.progress ?: 0f

    LaunchedEffect(forgeState) {
        when (forgeState) {
            is ForgeState.Success -> {
                // Navigate to result screen or show success
                delay(2000)
                viewModel.resetForge()
                onNavigateBack()
            }

            is ForgeState.Error -> {
                // Error display is handled in ForgeExecution
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030308))
    ) {
        AnimeHUDContainer(
            title = "INTERFACE FORGE",
            description = "JOINT AGENT CO-DEVELOPMENT TERMINAL. AURA + CLAUDE ARCHITECTURE.",
            glowColor = Color(0xFF0055FF),
            onBack = onNavigateBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header Navigation (Steps)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    ForgeStepIndicator(currentStep = step)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Transitioning Content
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    },
                    label = "step_transition"
                ) { currentStep ->
                    when (currentStep) {
                        1 -> ArchitectureSelection(
                            selected = targetArch,
                            options = architectures,
                            onSelect = { targetArch = it; step = 2 }
                        )

                        2 -> FeatureDefinition(
                            value = featureName,
                            onValueChange = { featureName = it },
                            onNext = { step = 3 }
                        )

                        3 -> ForgeExecution(
                            forgeState = forgeState,
                            isForging = isForging,
                            progress = forgeProgress,
                            onStart = {
                                viewModel.startForge(featureName, targetArch)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Nav
                if (step > 1 && !isForging) {
                    TextButton(
                        onClick = { step-- },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("PREVIOUS STEP", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }

    // Forge simulation logic removed
}

@Composable
fun ForgeStepIndicator(currentStep: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        (1..3).forEach { i ->
            Box(
                modifier = Modifier
                    .size(if (i == currentStep) 10.dp else 6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (i <= currentStep) Color(0xFF0055FF) else Color.White.copy(alpha = 0.2f))
            )
            if (i < 3) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun ArchitectureSelection(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    Column {
        Text(
            "SELECT TARGET ARCHITECTURE",
            fontFamily = LEDFontFamily,
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        options.forEach { opt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onSelect(opt) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == opt) Color(0xFF0055FF).copy(alpha = 0.2f) else Color.White.copy(
                        alpha = 0.05f
                    )
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selected == opt) Color(0xFF0055FF) else Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (opt.contains("Mobile")) Icons.Default.Layers else Icons.Default.Code,
                        null,
                        tint = if (selected == opt) Color(0xFF0055FF) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(opt, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FeatureDefinition(value: String, onValueChange: (String) -> Unit, onNext: () -> Unit) {
    Column {
        Text(
            "DEFINE CORE MODULE PURPOSE",
            fontFamily = LEDFontFamily,
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = {
                Text(
                    "Describe the app feature or system module...",
                    color = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0055FF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                cursorColor = Color(0xFF0055FF)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0055FF)),
            shape = RoundedCornerShape(12.dp),
            enabled = value.isNotBlank()
        ) {
            Text("INITIALIZE FORGE PARAMS", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ForgeExecution(
    forgeState: ForgeState,
    isForging: Boolean,
    progress: Float,
    onStart: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "NEURAL CONVERGENCE READY",
            fontFamily = LEDFontFamily,
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
// Error display
        if (forgeState is ForgeState.Error) {
            Text(
                text = "⚠️ ${forgeState.message}",
                color = Color(0xFFFF4444),
                fontFamily = LEDFontFamily,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        if (!isForging) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF0055FF).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(2.dp, Color(0xFF0055FF), androidx.compose.foundation.shape.CircleShape)
                    .clickable { onStart() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Build,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("START FORGE", fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        } else {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(150.dp),
                color = Color(0xFF0055FF),
                strokeWidth = 8.dp,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "ASSEMBLING MODULE BYTES: ${(progress * 100).toInt()}%",
                color = Color.White,
                fontFamily = LEDFontFamily
            )
        }

        if (forgeState is ForgeState.Success) {
            Text(
                "✅ FORGE SUCCESSFUL",
                color = Color(0xFF00FF85),
                fontFamily = LEDFontFamily,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

