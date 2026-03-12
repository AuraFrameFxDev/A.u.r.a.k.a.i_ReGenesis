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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

/**
 * 🎨 STUB SCREEN - Beautiful placeholder for routes not yet implemented
 * 
 * Temporary neon blue cards matching the holographic aesthetic
 * Will be replaced with personality-matched gate cards later
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF0F1420),
                        Color(0xFF12192B)
                    )
                )
            )
    ) {
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
                    .size(200.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00D9FF).copy(alpha = 0.3f),
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
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Icon name
            Text(
                text = iconName,
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF00D9FF)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Description or coming soon
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00D9FF).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚧 COMING SOON",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF00D9FF)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = description ?: "This feature is currently under development",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Back Button
            if (navController != null) {
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D9FF).copy(alpha = 0.2f)
                    )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00D9FF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        color = Color.White
                    )
                }
            }
        }
    }
}

