package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import dev.aurakai.auraframefx.domains.aura.ui.components.GlitchText
import dev.aurakai.auraframefx.domains.aura.ui.components.HexagonGrid
import dev.aurakai.auraframefx.domains.aura.ui.components.SparkleButton

/**
 * 🎨 STUB SCREEN - Beautiful placeholder for routes not yet implemented
 * 
 * ReGenesis LDO Edition: Features HexagonGrid, GlitchText, and SparkleButton
 */
@Composable
fun StubScreen(
    title: String,
    iconName: String,
    navController: NavController? = null,
    description: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Geometric Background
        HexagonGrid(color = Color(0xFF00D9FF).copy(alpha = 0.05f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Holographic card frame
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00D9FF).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Construction,
                    contentDescription = iconName,
                    tint = Color(0xFF00D9FF),
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title with Glitch effect
            GlitchText(
                text = title.uppercase(),
                color = Color.White,
                glitchColor = Color(0xFF00D9FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "SUB-SYSTEM: $iconName",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF00D9FF).copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.03f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D9FF).copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "› INITIALIZING NEURAL PATHWAY...",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Green.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = description ?: "This system logic is currently being forged in Aura's Lab. Deployment scheduled for next cycle.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        lineHeight = 18.sp
                    )
                }
            }

            // Navigation
            if (navController != null) {
                Spacer(modifier = Modifier.height(48.dp))

                SparkleButton(
                    text = "Return to Gate",
                    onClick = { navController.popBackStack() },
                    color = Color(0xFF00D9FF)
                )
            }
        }
    }
}


