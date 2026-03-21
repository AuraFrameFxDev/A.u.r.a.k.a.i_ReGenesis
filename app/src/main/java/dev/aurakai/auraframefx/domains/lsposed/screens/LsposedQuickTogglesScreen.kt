package dev.aurakai.auraframefx.domains.lsposed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * LSPosed Quick Toggles Screen
 * Simplified view for fast access from Level 1 ExodusHUD
 */
@Composable
fun LsposedQuickTogglesScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF332200), // Dark Amber
                            Color.Black,
                            Color(0xFF1A1A1A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "⚡ QUICK TOGGLES",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFFFCC00), // LSPosed Yellow
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Text(
                text = "LSPosed Emergency Control",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFCC00).copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // The Shortcuts Panel
            XposedQuickActionsPanel()

            Spacer(modifier = Modifier.weight(1f))

            // Kill Switch Info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⚠️ Use 'Disable Modules' as an emergency kill-switch if a module causes a bootloop.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }

            // Back button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFCC00).copy(alpha = 0.2f)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFCC00))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return to Hub", color = Color(0xFFFFCC00))
            }
        }
    }
}
