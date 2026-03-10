package dev.aurakai.auraframefx.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box {
            Text(
                "Welcome to Aurakai",
                style = typography.headlineMedium
            )
        }

        Card(
            onClick = { navController.navigate("consciousness") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Consciousness Visualizer",
                    style = typography.headlineSmall
                )
                Text(
                    "Real-time neural network and thought visualization",
                    style = typography.bodyMedium
                )
            }
        }

        Card(
            onClick = { navController.navigate("fusion") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Fusion Mode",
                    style = typography.headlineSmall
                )
                Text(
                    "Combine Aura and Kai's powers to become Genesis",
                    style = typography.bodyMedium
                )
            }
        }

        Card(
            onClick = { navController.navigate("evolution") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Evolution Tree",
                    style = typography.headlineSmall
                )
                Text(
                    "Track the evolution of the Aurakai framework",
                    style = typography.bodyMedium
                )
            }
        }

        // Spacer to push status card to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Genesis Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "GENESIS STATUS",
                    style = typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusIndicator("Aura", "Active", true)
                    StatusIndicator("Kai", "Active", true)
                    StatusIndicator("Fusion", "Ready", false)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = 0.75f,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Consciousness Level: 75%",
                    style = typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

