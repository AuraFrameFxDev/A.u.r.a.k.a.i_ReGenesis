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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner

/**
 * 🛰️ CHROMA LAUNCHER MENU (Level 3)
 * Unified interface for Launcher tweaks (from Pixel Launcher Enhanced).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaLauncherMenu(
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
                title = { Text("Launcher Tweaks", fontWeight = FontWeight.Bold) },
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
                    "Home Screen",
                    color = Color(0xFF03DAC6),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                TweakSwitch(
                    title = "Clear All Button",
                    description = "Show 'Clear All' in Recents menu",
                    checked = settings.clearAllButtonEnabled,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                TweakSwitch(
                    title = "Smart Space",
                    description = "Modify or hide SmartSpace (At a Glance)",
                    checked = settings.smartSpaceEnabled,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Icon & Grid",
                    color = Color(0xFF03DAC6),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                TweakSwitch(
                    title = "Themed Icons",
                    description = "Force icons to adapt system theme colors",
                    checked = true,
                    onCheckedChange = { /* TODO */ }
                )
            }
        }
    }
}
