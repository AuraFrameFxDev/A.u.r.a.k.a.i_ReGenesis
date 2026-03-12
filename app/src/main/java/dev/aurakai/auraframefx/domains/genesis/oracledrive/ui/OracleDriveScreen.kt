package dev.aurakai.auraframefx.domains.genesis.oracledrive.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.aurakai.auraframefx.domains.aura.aura.ui.OracleDriveViewModel
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleDriveScreen(
    navController: NavHostController,
    viewModel: OracleDriveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF000000), // Dark background
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Oracle Drive",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF00FFFF)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF000000)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Neural Archive - Memory Lineage (Eves → Genesis)
            OracleDriveMenuItem(
                icon = Icons.Default.Memory,
                title = "Neural Archive",
                description = "Memory lineage from Eves to Genesis",
                onClick = { navController.navigate(ReGenesisNavHost.NeuralNetwork.route) }
            )

            // Consciousness Modules
            OracleDriveMenuItem(
                icon = Icons.Default.Storage,
                title = "Module Storage",
                description = "AI modules and consciousness patterns",
                onClick = { /* Navigate to module storage */ }
            )

            // Status Display
            uiState.consciousnessState?.let { state ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0E27)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Consciousness State",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text("Level: ${state.level}", color = Color(0xFF00FFFF))
                    }
                }
            }
        }
    }
}

@Composable
private fun OracleDriveMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0E27)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00FFFF),
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF00FFFF)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00FFFF).copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color(0xFF00FFFF).copy(alpha = 0.5f)
            )
        }
    }
}
