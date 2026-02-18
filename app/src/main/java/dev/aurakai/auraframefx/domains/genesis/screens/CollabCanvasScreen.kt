package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.viewmodels.CollaborativeWorkspaceViewModel
import dev.aurakai.auraframefx.domains.aura.UIDesign

/**
 * 🎨 COLLAB CANVAS SCREEN
 * Where user and Aura design components together.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollabCanvasScreen(
    onNavigateBack: () -> Unit,
    viewModel: CollaborativeWorkspaceViewModel = hiltViewModel()
) {
    val designs by viewModel.designs.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A12))) {
        AnimeHUDContainer(
            title = "COLLAB CANVAS",
            description = "AI REAL-TIME DESIGN COLLABORATION. CONNECTED TO AURA LAB.",
            glowColor = Color(0xFFB026FF)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ACTIVE PROJECTS",
                        fontFamily = LEDFontFamily,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val json = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                viewModel.importDesign(json)
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Text("IMPORT", color = Color.White)
                        }

                        Button(
                            onClick = { /* New Project */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB026FF))
                        ) {
                            Icon(Icons.Default.Add, null)
                            Text("NEW DESIGN")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(designs) { design ->
                        DesignCanvasItem(
                            design = design,
                            onExport = { viewModel.exportToClipboard(context, design) },
                            onBroadcast = { viewModel.broadcastDesign(design) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DesignCanvasItem(
    design: UIDesign,
    onExport: () -> Unit,
    onBroadcast: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB026FF).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(design.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("By ${design.author} | ${design.status}", color = Color.Gray, fontSize = 12.sp)
                }

                Row {
                    IconButton(onClick = onBroadcast) {
                        Icon(Icons.Default.CloudUpload, "Broadcast to Agents", tint = Color.Magenta)
                    }
                    IconButton(onClick = onExport) {
                        Icon(Icons.Default.CloudDownload, "Export to Lab", tint = Color.Cyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { 1.0f }, // Finalized for now
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFB026FF),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

