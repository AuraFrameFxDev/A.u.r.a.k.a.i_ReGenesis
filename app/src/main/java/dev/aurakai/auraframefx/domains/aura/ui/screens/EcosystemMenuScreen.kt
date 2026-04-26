package dev.aurakai.auraframefx.domains.aura.ui.screens

// import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.animations.HomeScreenTransitionType
import dev.aurakai.auraframefx.domains.aura.animations.DigitalTransitionRow
import dev.aurakai.auraframefx.domains.aura.ui.components.HologramTransition

/**
 * Displays the Ecosystem Menu screen with a holographic transition and transition type selector.
 *
 * @param transitionType The currently selected home screen transition type.
 * @param showHologram Whether to display the hologram transition effect.
 */
/**
 * Displays the Ecosystem Menu screen with a holographic transition effect and a transition type selector.
 *
 * @param transitionType The currently selected home screen transition type.
 * @param showHologram Whether to display the holographic transition effect.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcosystemMenuScreen(
    transitionType: HomeScreenTransitionType = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
    showHologram: Boolean = true,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ecosystem Hub", fontWeight = androidx.compose.ui.text.font.FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color(0xFFE94560),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ecosystem Command Center",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFE94560)
                )
                DigitalTransitionRow(
                    currentType = transitionType,
                    onTypeSelected = {}
                )
            }
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun EcosystemMenuScreenPreview() { // Renamed
//     EcosystemMenuScreen()
// }


