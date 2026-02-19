package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * 🛰️ CHROMA COLOR ENGINE MENU (Level 3)
 * Unified interface for Color Engine tweaks (from ColorBlendr).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaColorEngineMenu(
    onNavigateBack: () -> Unit,
    viewModel: ChromaCoreViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Color Engine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Material You / Monet",
                    color = Color(0xFF00B0FF),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                TweakSwitch(
                    title = "Dynamic Colors",
                    description = "Extract colors from wallpaper (Android 12+)",
                    checked = settings.useDynamicColors,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                TweakSwitch(
                    title = "Custom Seed Color",
                    description = "Override wallpaper colors with custom seed",
                    checked = settings.customPaletteEnabled,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                TweakSwitch(
                    title = "Per-App Colors",
                    description = "Different color schemes for specific apps",
                    checked = settings.perAppColorsEnabled,
                    onCheckedChange = { /* TODO */ }
                )
            }
        }
    }
}
