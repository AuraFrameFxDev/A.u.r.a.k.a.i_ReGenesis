package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 🛰️ CHROMA STATUS BAR MENU (Level 3)
 * Unified interface for Status Bar tweaks (from Iconify).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaStatusBarMenu(
    onNavigateBack: () -> Unit,
    viewModel: ChromaCoreViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Bar Tweaks", fontWeight = FontWeight.Bold) },
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
                    "Iconify Integrations",
                    color = Color(0xFFBB86FC),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                TweakSwitch(
                    title = "Status Bar Logo",
                    description = "Show a custom logo in the status bar",
                    checked = settings.statusbarLogoEnabled,
                    onCheckedChange = { viewModel.toggleStatusbarLogo(it) }
                )
            }

            item {
                TweakSwitch(
                    title = "Colored Battery",
                    description = "Apply system theme colors to battery icon",
                    checked = settings.coloredBatteryEnabled,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                TweakSwitch(
                    title = "Dual Status Bar",
                    description = "Split icons into two rows (Experimental)",
                    checked = false,
                    onCheckedChange = { /* TODO */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Clock & Date",
                    color = Color(0xFFBB86FC),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // More tweaks...
            item {
                TweakSwitch(
                    title = "Clock Position",
                    description = "Move clock to Left, Center, or Right",
                    checked = true,
                    onCheckedChange = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun TweakSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(description, color = Color.Gray, fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFBB86FC),
                    checkedTrackColor = Color(0xFFBB86FC).copy(alpha = 0.5f)
                )
            )
        }
    }
}
