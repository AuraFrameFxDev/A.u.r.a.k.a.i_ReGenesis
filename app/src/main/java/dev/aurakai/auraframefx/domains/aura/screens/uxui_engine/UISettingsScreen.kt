package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.viewmodels.AuraUIControlViewModel

/**
 * UI Settings screen — Aura controls every visibility toggle.
 * State comes from AuraUIControlViewModel (DataStore-backed). No local mutableState.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UISettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { navController.navigateUp() },
    viewModel: AuraUIControlViewModel = hiltViewModel()
) {
    val state by viewModel.uiSettingsState.collectAsState()
    val isSidebarVisible = state.isSidebarVisible
    val isNotchbarVisible = state.isNotchbarVisible
    val isStatusBarVisible = state.isStatusBarVisible
    val isBottomNavVisible = state.isBottomNavVisible
    val isGlowEffectsEnabled = state.isGlowEffectsEnabled
    val isPixelArtEnabled = state.isPixelArtEnabled
    state.isDarkMode

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
                    onCheckedChange = { viewModel.setSidebarVisible(it) }
                )

                SettingsToggleItem(
                    title = "Notch Bar",
                    subtitle = "Show/hide the top notch bar",
                    isChecked = isNotchbarVisible,
                    onCheckedChange = { viewModel.setNotchbarVisible(it) }
                )

                SettingsToggleItem(
                    title = "Status Bar",
                    subtitle = "Show/hide the system status bar",
                    isChecked = isStatusBarVisible,
                    onCheckedChange = { viewModel.setStatusBarVisible(it) }
                )

                SettingsToggleItem(
                    title = "Bottom Navigation",
                    subtitle = "Show/hide the bottom navigation bar",
                    isChecked = isBottomNavVisible,
                    onCheckedChange = { viewModel.setBottomNavVisible(it) }
                )
            }

            // Visual Effects Section
            SettingsSection(title = "Visual Effects") {
                SettingsToggleItem(
                    title = "Glow Effects",
                    subtitle = "Enable/disable UI glow and bloom effects",
                    isChecked = isGlowEffectsEnabled,
                    onCheckedChange = { viewModel.setGlowEffects(it) }
                )

                SettingsToggleItem(
                    title = "Pixel Art Mode",
                    subtitle = "Enable retro pixel art styling",
                    isChecked = isPixelArtEnabled,
                    onCheckedChange = { viewModel.setPixelArt(it) }
                )
            }

            // Customization Section
            SettingsSection(title = "Customization Hub") {
                SettingsClickItem(
                    title = "Gate Customization",
                    subtitle = "Splash and Drawer images",
                    onClick = { navController.navigate("gate_customization") }
                )
                SettingsClickItem(
                    title = "Notch Bar",
                    subtitle = "Status bar neural layer",
                    onClick = { navController.navigate("notch_bar_customization") }
                )
                SettingsClickItem(
                    title = "Quick Settings",
                    subtitle = "QS background assets",
                    onClick = { navController.navigate("qs_customization") }
                )
                SettingsClickItem(
                    title = "Chroma Core",
                    subtitle = "Re-calibrate system colors",
                    onClick = { navController.navigate("chroma_core_colors") }
                )
            }

            // Reset Button
            Button(
                onClick = {
                    viewModel.setSidebarVisible(true)
                    viewModel.setNotchbarVisible(true)
                    viewModel.setStatusBarVisible(true)
                    viewModel.setBottomNavVisible(true)
                    viewModel.setGlowEffects(true)
                    viewModel.setPixelArt(true)
                    viewModel.setDarkMode(true)
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
private fun SettingsClickItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
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
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

