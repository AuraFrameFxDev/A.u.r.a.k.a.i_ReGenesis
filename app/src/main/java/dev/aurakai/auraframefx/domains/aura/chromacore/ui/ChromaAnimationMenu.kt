package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 🛰️ CHROMA ANIMATION MENU (Level 3)
 * Unified interface for System & App animations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaAnimationMenu(
    onNavigateBack: () -> Unit,
    viewModel: ChromaCoreViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Animations & FX", fontWeight = FontWeight.Bold) },
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
                    "System Animations",
                    color = Color(0xFFFF6F00),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                TweakSwitch(
                    title = "Window Transition Scale",
                    description = "Speed up or slow down window openings",
                    checked = true,
                    onCheckedChange = { /* TODO: Apply via Global Settings */ }
                )
            }

            item {
                TweakSwitch(
                    title = "AURA FX Core",
                    description = "Enable advanced UI micro-animations",
                    checked = true,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Boot & Power",
                    color = Color(0xFFFF6F00),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                TweakSwitch(
                    title = "Custom Boot Animation",
                    description = "Override the default system boot animation",
                    checked = false,
                    onCheckedChange = { /* TODO: Requires root write to /system/media */ }
                )
            }
        }
    }
}
