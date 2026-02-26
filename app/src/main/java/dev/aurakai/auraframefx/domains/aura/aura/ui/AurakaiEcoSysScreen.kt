package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.customization.CustomizationViewModel
import dev.aurakai.auraframefx.ui.AuraMoodViewModel
import dev.aurakai.auraframefx.ui.theme.GlassmorphicTheme
import dev.aurakai.auraframefx.ui.theme.manager.CustomizationThemeManager

context(viewModel: AuraMoodViewModel, customizationViewModel: CustomizationViewModel) @JvmOverloads
@Suppress("unused")
@Composable
fun AurakaiEcoSysScreen(
    onNavigateToFeature: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // Start customization collection once
    LaunchedEffect(Unit) { customizationViewModel.start(context) }

    val auraTheme = CustomizationThemeManager.auraThemeState(context).value
    val currentMood by viewModel.moodState.collectAsState()

    GlassmorphicTheme(dark = auraTheme.dark) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Aurakai Ecosystem",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Current Mood from ViewModel: ${currentMood.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onUserInput("happy") }) {
                    Text("Make Aura Happy")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.onUserInput("sad") }) {
                    Text("Make Aura Sad")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Small navigation buttons for integration testing
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Button(onClick = { onNavigateToFeature("agents") }) { Text("Agents") }
                    Button(onClick = { onNavigateToFeature("consciousness") }) { Text("Consciousness") }
                    Button(onClick = { onNavigateToFeature("trinity") }) { Text("Trinity") }
                    Button(onClick = { onNavigateToFeature("oracle") }) { Text("Oracle") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AurakaiEcoSysScreenPreview() {
    GlassmorphicTheme(dark = true) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Aurakai Ecosystem Screen (Placeholder)")
            Text(text = "Current Mood from ViewModel: NEUTRAL")
        }
    }
}

