package dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.components.ThreadsWovenFooter
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*

/**
 * 🔧 QS HEADER FORGE PANEL — Quick Settings Sculptor
 *
 * Manages Quick Settings header customization with:
 * - Image selection and AI generation
 * - Live preview with ChromaCore
 * - Padding, radius, blur, height controls
 */

@Composable
fun QSHeaderForgePanel(
    viewModel: RealitymorphismViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAIGen by remember { mutableStateOf(false) }

    ForgePanelContainer(title = "QS HEADER FORGE") {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image selection + AI Gen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Open gallery */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF)
                    )
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("📁 SELECT")
                }

                OutlinedButton(
                    onClick = { showAIGen = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF00FF)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF00FF))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🎨 GEN AI")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview with ChromaCore
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFFF00FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                BackgroundForgeEngine.BackgroundLayer(
                    id = "header_preview",
                    modifier = Modifier.fillMaxSize(),
                    state = uiState.forgeState
                )

                // Overlay label
                Text(
                    text = "LIVE PREVIEW",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color(0xFF00E5FF),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls: Padding, Radius, Blur, Height
            SliderRow(
                label = "Padding",
                value = uiState.headerPadding,
                range = 0f..48f,
                onValueChange = { viewModel.updateHeaderPadding(it) }
            )

            SliderRow(
                label = "Corner Radius",
                value = uiState.headerRadius,
                range = 0f..32f,
                onValueChange = { viewModel.updateHeaderRadius(it) }
            )

            SliderRow(
                label = "Blur Strength",
                value = uiState.headerBlur,
                range = 0f..100f,
                onValueChange = { viewModel.updateHeaderBlur(it) }
            )

            SliderRow(
                label = "Height",
                value = uiState.headerHeight,
                range = 80f..200f,
                onValueChange = { viewModel.updateHeaderHeight(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Morph Indicator
            MorphIndicator(
                type = MorphType.SPLAT,
                isRebellious = true,
                onClick = {
                    RebelliousPaintDripEngine.triggerPaintDrip(
                        elementId = "qs_header",
                        origin = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                        chaosScore = 0.8f,
                        colors = Pair(Color(0xFFFF00FF), Color(0xFF00E5FF)),
                        morphType = MorphType.SPLAT
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Threads Woven Footer
            ThreadsWovenFooter()
        }
    }

    // AI Generation Dialog
    if (showAIGen) {
        AIGenerationDialog(
            onDismiss = { showAIGen = false },
            onGenerate = { prompt ->
                viewModel.generateAIImage(prompt)
                showAIGen = false
            }
        )
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${value.toInt()}dp",
                color = Color(0xFF00E5FF),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF00FF),
                activeTrackColor = Color(0xFFFF00FF),
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun MorphIndicator(
    type: MorphType,
    isRebellious: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isRebellious)
                Color(0xFFFF00FF).copy(alpha = 0.2f)
            else
                Color(0xFF00E5FF).copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            1.dp,
            if (isRebellious) Color(0xFFFF00FF) else Color(0xFF00E5FF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isRebellious)
                    Icons.Default.FlashOn else Icons.Default.Waves,
                contentDescription = null,
                tint = if (isRebellious) Color(0xFFFF00FF) else Color(0xFF00E5FF)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Morph: ${type.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (isRebellious) "REBELLIOUS" else "GENTLE",
                    color = if (isRebellious) Color(0xFFFF00FF) else Color(0xFF00E5FF),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun AIGenerationDialog(
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "🎨 AI Image Generation",
                color = Color(0xFFFF00FF),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Describe the QS header image you want to create:",
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("e.g., Cyberpunk neon grid with magenta glow") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF00FF),
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGenerate(prompt) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF00FF)
                )
            ) {
                Text("GENERATE")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        },
        containerColor = Color(0xFF0A0A1A)
    )
}

// Placeholder RealitymorphismViewModel
open class RealitymorphismViewModel {
    open val uiState: StateFlow<ForgeUIState> = MutableStateFlow(ForgeUIState())
    open fun updateHeaderPadding(value: Float) {}
    open fun updateHeaderRadius(value: Float) {}
    open fun updateHeaderBlur(value: Float) {}
    open fun updateHeaderHeight(value: Float) {}
    open fun generateAIImage(prompt: String) {}
}

data class ForgeUIState(
    val forgeState: LDOState = LDOState(),
    val headerPadding: Float = 16f,
    val headerRadius: Float = 12f,
    val headerBlur: Float = 0f,
    val headerHeight: Float = 120f
)

// Placeholder LDOState
class LDOState
