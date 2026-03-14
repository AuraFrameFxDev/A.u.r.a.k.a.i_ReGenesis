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
        SandboxSection(title = "🌌 Background Components") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // TODO: Add CyberpunkBackgrounds when available
                    Text(
                        text = "🌌 Cyberpunk Background Placeholder",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Component Testing Section
        SandboxSection(title = "🔮 Interactive Components") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Aura Orb Test
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // TODO: Add PlaceholderAuraOrb when available
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.size(60.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE94560))
                        ) {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                    Text(
                        text = "Aura Orb",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Halo View Test
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: Add HaloView when available
                        Card(
                            modifier = Modifier.size(60.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF00F5FF))
                        ) {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                    Text(
                        text = "Halo View",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Animation Testing Section
        SandboxSection(title = "⚡ Digital Transitions") {
            var showTransition by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showTransition = !showTransition },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE94560)
                    )
                ) {
                    Text(
                        text = if (showTransition) "Hide Transition" else "Test Digital Transition",
                        color = Color.White
                    )
                }

                if (showTransition) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // TODO: Add DigitalTransitions when available
                            Text(
                                text = "✨ Digital Materialization Placeholder ✨",
                                color = Color(0xFF00F5FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Color Palette Testing
        SandboxSection(title = "🎨 Color Palette") {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(120.dp),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val colors = listOf(
                    Color(0xFFE94560) to "Neon Red",
                    Color(0xFF00F5FF) to "Cyan",
                    Color(0xFFFFD700) to "Gold",
                    Color(0xFF1A1A2E) to "Dark Blue",
                    Color(0xFF16213E) to "Navy",
                    Color(0xFF0F3460) to "Deep Blue",
                    Color(0xFF533483) to "Purple",
                    Color(0xFF7209B7) to "Magenta"
                )

                items(colors.size) { index ->
                    val (color, name) = colors[index]
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = color)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 8.sp,
                                color = if (color.luminance() > 0.5f) Color.Black else Color.White
                            )
                        }
                    }
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
