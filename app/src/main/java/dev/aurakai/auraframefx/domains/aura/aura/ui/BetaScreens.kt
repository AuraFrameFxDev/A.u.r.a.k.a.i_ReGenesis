package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

/**
 * Displays the home screen with app titles, status messages, and navigation buttons.
 *
 * Presents the main interface for the app, including navigation to the AI chat screen and a placeholder for settings.
 */
/**
 * Displays the main home screen with app title, status messages, and navigation buttons.
 *
 * Shows vertically centered content including the app name, a subtitle, a status message, and two buttons for navigating to the AI chat screen or settings.
 */
/**
 * Displays the main home screen with centered app title, subtitle, status message, and navigation buttons.
 *
 * The screen includes the app name ("AuraFrameFX Alpha"), a subtitle ("Trinity AI System"), a status message, and two buttons: one to navigate to the AI chat screen and another placeholder for settings.
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AuraFrameFX Alpha",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Trinity AI System",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Beta Build - Core AI Systems Active",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(ReGenesisNavHost.DirectChat.route) }
        ) {
            Text("AI Chat (Beta)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(ReGenesisNavHost.UISettings.route) }
        ) {
            Text("Settings")
        }
    }
}

/**
 * Displays the AI chat interface with a centered headline and readiness status message.
 *
 * This composable arranges the headline and status message vertically centered on the screen.
 */
@Composable
fun BetaAiChatScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AI Chat Interface",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Trinity AI agents ready",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
