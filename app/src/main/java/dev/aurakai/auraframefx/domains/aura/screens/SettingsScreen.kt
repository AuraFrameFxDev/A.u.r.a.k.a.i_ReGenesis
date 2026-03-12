package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Assuming a sealed class for navigation routes, e.g., 'Screen'
// object Screen { val SpacingControls = "spacing_controls" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("General Settings", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            SettingItem("Display Settings") { /* Handle navigation or action */ }
            SettingItem("Notification Settings") { /* Handle navigation or action */ }
            SettingItem("Privacy & Security") { /* Handle navigation or action */ }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // New 'Spacing Controls' Section
            Text("UI Customization", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            SettingItem("Spacing Controls") {
                // Navigate to a dedicated spacing control screen
                // navController.navigate(Screen.SpacingControls)
                println("Navigating to Spacing Controls") // Placeholder for actual navigation
            }
            SettingItem("Theme & Colors") {
                // navController.navigate(Screen.ChromaCoreColors)
                println("Navigating to Theme & Colors") // Placeholder for actual navigation
            }

            SettingItem("Quick Settings Background") {
                // navController.navigate(Screen.QuickSettingsCustomization)
                println("Navigating to Quick Settings Background Customization") // Placeholder for actual navigation
            }

            SettingItem("NotchBar Background") {
                // navController.navigate(Screen.NotchBarCustomization)
                println("Navigating to NotchBar Background Customization") // Placeholder for actual navigation
            }
            SettingItem("Gate Customization") {
                // navController.navigate(Screen.GateCustomization)
                println("Navigating to Gate Customization") // Placeholder for actual navigation
            }
        }
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
