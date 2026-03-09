package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.ui.components.SoftGlowBackground
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 💚 HELP SERVICES HUB (Level 2)
 *
 * ANIMATION: SoftGlowBackground
 * - Gentle green glow orbs
 * - Breathing/pulse effect
 * - Welcoming, supportive, friendly vibe
 *
 * Single style - Help should always feel consistent and calm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpDeskScreen(navController: NavController) {

    Box(modifier = Modifier.fillMaxSize()) {
        // 💚 HELP ANIMATED BACKGROUND - Soft, welcoming glow
        SoftGlowBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "HELP SERVICES",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "SUPPORT & DOCUMENTATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "How can we help you today?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Help options as cards
                HelpOptionCard(
                    title = "Live Agent Chat",
                    description = "Talk to a real-time AI assistant",
                    color = Color(0xFF4CAF50),
                    onClick = { navController.navigate("direct_chat") }
                )

                HelpOptionCard(
                    title = "Documentation",
                    description = "Browse guides and tutorials",
                    color = Color(0xFF00E5FF),
                    onClick = { navController.navigate("documentation") }
                )

                HelpOptionCard(
                    title = "FAQ Browser",
                    description = "Find answers to common questions",
                    color = Color(0xFFB026FF),
                    onClick = { navController.navigate("faq_browser") }
                )

                HelpOptionCard(
                    title = "Tutorial Videos",
                    description = "Watch step-by-step guides",
                    color = Color(0xFFFF6B00),
                    onClick = { navController.navigate("tutorial_videos") }
                )
            }
        }
    }
}

@Composable
private fun HelpOptionCard(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Arrow indicator
            Text(
                text = "→",
                color = color,
                fontSize = 24.sp
            )
        }
    }
}
