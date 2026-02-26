package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.navigation.NavDestination
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 🌊 CASCADE HUB - Sensory Matrix Domain
 * Central hub for Cascade's pattern recognition and data stream tools.
 */
@Composable
fun CascadeHubScreen(navController: NavController) {
    val tools = listOf(
        CascadeTool(
            title = "Cascade Vision",
            subtitle = "Pattern Recognition HUD",
            icon = Icons.Default.RemoveRedEye,
            destination = NavDestination.CascadeVision,
            color = Color(0xFF00FFD4)
        ),
        CascadeTool(
            title = "Data Monitoring",
            subtitle = "Real-time Stream Analysis",
            icon = Icons.Default.StackedLineChart,
            destination = NavDestination.DataStreamMonitoring,
            color = Color(0xFF00E5FF)
        ),
        CascadeTool(
            title = "Neural Link",
            subtitle = "Direct Matrix Interface",
            icon = Icons.Default.Hub,
            destination = NavDestination.NeuralNetwork,
            color = Color(0xFFB026FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020205),
                        Color(0xFF0A0A1F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Text(
                "CASCADE HUB",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = LEDFontFamily
            )
            Text(
                "SENSORY MATIX & PATTERN RECOGNITION",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontFamily = LEDFontFamily,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tools) { tool ->
                    CascadeToolCard(tool) {
                        navController.navigate(tool.destination.route)
                    }
                }
            }
        }
    }
}

@Composable
private fun CascadeToolCard(tool: CascadeTool, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = tool.color.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(tool.icon, contentDescription = null, tint = tool.color)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = tool.subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private data class CascadeTool(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: NavDestination,
    val color: Color
)
