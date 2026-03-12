package dev.aurakai.auraframefx.sandbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Aura's Creative Sandbox 🎨
 *
 * This is where I test, refine, and perfect every UI component
 * before it touches the production code. My digital laboratory.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎨 Aura's Creative Sandbox",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE94560)
                )
                Text(
                    text = "Welcome to the Lab!",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "Where UI components are forged and perfected",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Background Testing Section
        SandboxSection(title = "🌌 Dynamic Visuals") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0C29))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Premium Background Design
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F0C29),
                                    Color(0xFF302B63),
                                    Color(0xFF24243E)
                                )
                            )
                        )
                    }
                    Text(
                        text = "Quantum Canvas Active",
                        color = Color(0xFF00F5FF),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Component Testing Section
        SandboxSection(title = "🔮 Neural Halo Interface") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
//                dev.aurakai.auraframefx.aura.ui.HaloView(
//                    modifier = Modifier.fillMaxSize()
//                )
                Text(
                    "HaloView Placeholder (Move to shared lib)",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        SandboxSection(title = "🧪 ChromaCore Diagnostics") {
            val sourceColor = Color(0xFFE94560)
            val blendedColor =
                dev.aurakai.colorblendr.ChromaCore.blendColors(sourceColor, Color(0xFF00F5FF), 0.5f)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Dynamic Palette Generation",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorBox(sourceColor, "Source")
                    ColorBox(blendedColor, "Blended")
                    val complementary =
                        dev.aurakai.colorblendr.ChromaCore.generateHarmonics(sourceColor)[2] // 2 is complementary in generateHarmonics
                    ColorBox(complementary, "Harmonic")
                }
            }
        }

        // Footer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF533483).copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "🔬 This is where magic happens - Aura",
                modifier = Modifier.padding(16.dp),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ColorBox(color: Color, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.size(60.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {}
        Text(name, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
    }
}


@Composable
private fun SandboxSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00F5FF)
        )
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SandboxScreenPreview() {
    SandboxScreen()
}
