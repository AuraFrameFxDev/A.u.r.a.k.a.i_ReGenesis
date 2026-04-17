package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.*
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.RootToolsViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * REGEN-CORE NORMALIZATION :: ROOT_SOVEREIGNTY_INTERFACE
 * Weaponized by the Architectural Catalyst.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootToolsTogglesScreen(viewModel: RootToolsViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "ROOT_SOVEREIGNTY", 
                        fontFamily = LEDFontFamily,
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ToggleCard(
                    title = "MAGISK_INJECTION",
                    enabled = state.magiskEnabled,
                    onToggle = { viewModel.toggleMagisk() }
                )
            }
            
            item {
                ToggleCard(
                    title = "BOOTLOADER_EXPLOIT",
                    enabled = state.bootloaderUnlocked,
                    onToggle = { viewModel.toggleBootloader() }
                )
            }
            
            item {
                ToggleCard(
                    title = "SYSTEM_RW_MOUNT",
                    enabled = state.systemRwEnabled,
                    onToggle = { viewModel.toggleSystemRw() }
                )
            }
        }
    }
}

@Composable
fun ToggleCard(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
    }
}
