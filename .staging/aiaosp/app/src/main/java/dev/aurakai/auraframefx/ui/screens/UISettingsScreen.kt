package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


/**
 * UI Settings screen for toggling various UI components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UISettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { navController.navigateUp() }
) {
    // State for UI toggles
    var isSidebarVisible by remember { mutableStateOf(true) }
    var isNotchbarVisible by remember { mutableStateOf(true) }
    var isStatusBarVisible by remember { mutableStateOf(true) }
    var isBottomNavVisible by remember { mutableStateOf(true) }
    var isGlowEffectsEnabled by remember { mutableStateOf(true) }
    var isPixelArtEnabled by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "UI Settings",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Layout Section
            SettingsSection(title = "Layout") {
                SettingsToggleItem(
                    title = "Sidebar",
                    subtitle = "Show/hide the main sidebar",
                    isChecked = isSidebarVisible,
                    onCheckedChange = { isSidebarVisible = it }
                )

                SettingsToggleItem(
                    title = "Notch Bar",
                    subtitle = "Show/hide the top notch bar",
                    isChecked = isNotchbarVisible,
                    onCheckedChange = { isNotchbarVisible = it }
                )

                SettingsToggleItem(
                    title = "Status Bar",
                    subtitle = "Show/hide the system status bar",
                    isChecked = isStatusBarVisible,
                    onCheckedChange = { isStatusBarVisible = it }
                )

                SettingsToggleItem(
                    title = "Bottom Navigation",
                    subtitle = "Show/hide the bottom navigation bar",
                    isChecked = isBottomNavVisible,
                    onCheckedChange = { isBottomNavVisible = it }
                )
            }

            // Visual Effects Section
            SettingsSection(title = "Visual Effects") {
                SettingsToggleItem(
                    title = "Glow Effects",
                    subtitle = "Enable/disable UI glow and bloom effects",
                    isChecked = isGlowEffectsEnabled,
                    onCheckedChange = { isGlowEffectsEnabled = it }
                )

                SettingsToggleItem(
                    title = "Pixel Art Mode",
                    subtitle = "Enable retro pixel art styling",
                    isChecked = isPixelArtEnabled,
                    onCheckedChange = { isPixelArtEnabled = it }
                )
            }

            // Theme Section
            SettingsSection(title = "Theme") {
                SettingsToggleItem(
                    title = "Dark Mode",
                    subtitle = "Toggle between light and dark theme",
                    isChecked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
                )
            }

            // Reset Button
            Button(
                onClick = {
                    // Reset all toggles to default
                    isSidebarVisible = true
                    isNotchbarVisible = true
                    isStatusBarVisible = true
                    isBottomNavVisible = true
                    isGlowEffectsEnabled = true
                    isPixelArtEnabled = true
                    isDarkMode = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Reset to Defaults")
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
