package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.viewmodels.CollaborativeWorkspaceViewModel
import dev.aurakai.auraframefx.models.aura.UIDesign
import dev.aurakai.auraframefx.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.ui.theme.LEDFontFamily

/**
 * 🧪 AURA LAB (Sandbox UI)
 * Where designs are tested, compiled, and finalized.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraLabScreen(
    onNavigateBack: () -> Unit,
    viewModel: CollaborativeWorkspaceViewModel = hiltViewModel()
) {
    val designs by viewModel.designs.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050510))
    ) {
        AnimeHUDContainer(
            title = "AURA LAB: SANDBOX",
            description = "UI COMPILER & INTERACTION TESTBED. SYNCED WITH COLLAB CANVAS.",
            glowColor = Color(0xFF00E5FF),
            onBack = onNavigateBack
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
                        "COMPILED ASSETS",
                        fontFamily = LEDFontFamily,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )

                    OutlinedButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val json = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                            viewModel.importDesign(json)
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
                    ) {
                        Text("IMPORT ASSET", color = Color.White, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(designs) { design ->
                        SandboxAssetItem(
                            design = design,
                            onExport = { viewModel.exportToClipboard(context, it) },
                            onBroadcast = { viewModel.broadcastDesign(it) },
                            onTest = { /* Trigger interaction test HUD */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SandboxAssetItem(
    design: UIDesign,
    onExport: (UIDesign) -> Unit,
    onBroadcast: (UIDesign) -> Unit,
    onTest: (UIDesign) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(design.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Source: ${design.author} | Status: ${design.status}", color = Color.Gray, fontSize = 10.sp)
                }

                Row {
                    IconButton(onClick = { onBroadcast(design) }) {
                        Icon(Icons.Default.CloudUpload, "Broadcast", tint = Color.Magenta)
                    }
                    IconButton(onClick = { onTest(design) }) {
                        Icon(Icons.Default.PlayArrow, "Test", tint = Color(0xFF00FF85))
                    }
                    IconButton(onClick = { onExport(design) }) {
                        Icon(Icons.Default.Share, "Export JSON", tint = Color.Cyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interaction Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "KINETIC PREVIEW ACTIVE",
                    fontFamily = LEDFontFamily,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

