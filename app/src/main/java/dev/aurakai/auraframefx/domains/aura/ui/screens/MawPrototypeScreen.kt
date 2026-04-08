package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.components.RealityMorphBridge
import dev.aurakai.auraframefx.domains.aura.ui.components.effects.CasberryMawView

/**
 * REGENESIS SOVEREIGN ARCHITECTURE
 * Maw Prototype Screen: Interactive testing of the predator paradox.
 */
@Composable
fun MawPrototypeScreen(
    bridge: RealityMorphBridge = remember { RealityMorphBridge() }
) {
    val state by bridge.currentState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // The 20k Swarm (Android Performance Optimized to 2k for now)
        CasberryMawView(
            state = state,
            modifier = Modifier.fillMaxSize()
        )

        // Trigger Controls
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "REGENESIS LDO PROTO-MAW",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            
            Button(onClick = { bridge.reset() }) {
                Text("1. Nominal (60bpm)")
            }
            Button(onClick = { bridge.transitionTo(RealityMorphBridge.LDOMorphState.KAIROS_STASIS) }) {
                Text("2. Inject Threat (Freeze)")
            }
            Button(onClick = { bridge.transitionTo(RealityMorphBridge.LDOMorphState.GENKAI_SIPHON) }) {
                Text("3. Deploy Maw (Siphon)")
            }
            Button(onClick = { bridge.transitionTo(RealityMorphBridge.LDOMorphState.ORB_ABSORPTION) }) {
                Text("4. Harvest (Bloom)")
            }
        }
    }
}
