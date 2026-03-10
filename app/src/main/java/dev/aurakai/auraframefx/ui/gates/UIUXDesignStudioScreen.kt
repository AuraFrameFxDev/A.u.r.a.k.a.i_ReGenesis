package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/**
 * UI/UX Design Studio Screen
 * Comprehensive design tools for creating and prototyping interfaces
 */
@Composable
fun UIUXDesignStudioScreen(navController: NavHostController) {
    val tools = listOf(
        DesignTool("Layout Builder", Icons.Default.Dashboard, Color(0xFFFF00FF)),
        DesignTool("Typography", Icons.Default.TextFields, Color(0xFF00FFFF)),
        DesignTool("Color Palette", Icons.Default.Palette, Color(0xFFFFFF00)),
        DesignTool("Iconography", Icons.Default.Star, Color(0xFFFF1493)),
        DesignTool("Animations", Icons.Default.PlayCircle, Color(0xFF00FF00)),
        DesignTool("Components", Icons.Default.Widgets, Color(0xFFA020F0))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D002D), // Dark Magenta
                        Color.Black
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = "Design Studio",
                tint = Color(0xFFFF00FF),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "DESIGN STUDIO",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFF00FF),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Create. Prototype. Innovate.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00FFFF)
                )
            }
        }

        // Canvas Preview Area (Mock)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Preview",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Interactive Canvas Preview",
                        color = Color.Gray
                    )
                }

                // Floating Action Button Mock
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF00FF))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, "Add", tint = Color.White)
                }
            }
        }

        // Tools Grid
        Text(
            text = "Design Tools",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tools) { tool ->
                DesignToolCard(tool)
            }
        }
    }
}

@Composable
private fun DesignToolCard(tool: DesignTool) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, tool.color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.name,
                tint = tool.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tool.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class DesignTool(
    val name: String,
    val icon: ImageVector,
    val color: Color
)
