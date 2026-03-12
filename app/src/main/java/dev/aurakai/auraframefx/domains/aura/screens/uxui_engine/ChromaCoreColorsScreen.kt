package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🎨 CHROMA CORE COLORS SCREEN
 * Modernized with Anime HUD aesthetics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaCoreColorsScreen(onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()

    var primaryColor by remember { mutableStateOf(Color(0xFF00E5FF)) }
    var accentColor by remember { mutableStateOf(Color(0xFFFF00FF)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030305))
    ) {
        AnimeHUDContainer(
            title = "CHROMA CORE",
            description = "DYNAMIC COLOR RECALIBRATION. ADAPTIVE CHROMIC PULSE SYNCHRONIZING WITH SYSTEM DOMAIN.",
            glowColor = primaryColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                ColorSelectionCard(
                    title = "PRIMARY PLASMA",
                    color = primaryColor,
                    onColorChange = { primaryColor = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ColorSelectionCard(
                    title = "ACCENT NEON",
                    color = accentColor,
                    onColorChange = { accentColor = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* Apply Selection */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.8f))
                ) {
                    Text(
                        "SYNC CHROMATIC LAYER",
                        fontWeight = FontWeight.Black,
                        fontFamily = LEDFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun ColorSelectionCard(
    title: String,
    color: Color,
    onColorChange: (Color) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ColorLens, null, tint = color)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontFamily = LEDFontFamily, color = color, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder for color slider or hex input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color, RoundedCornerShape(8.dp))
            )
        }
    }
}
