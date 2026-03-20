package dev.aurakai.auraframefx.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import javax.inject.Inject

@AndroidEntryPoint
class QuickSettingsConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuraFrameFXTheme {
                QuickSettingsConfigScreen {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, QuickSettingsConfigActivity::class.java)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSettingsConfigScreen(onNavigateBack: () -> Unit) {
    val tiles = remember {
        mutableStateListOf(
            QsTileConfig("sync", "Synchronize", true),
            QsTileConfig("power", "Power Management", true),
            QsTileConfig("temp", "Thermal Monitor", false),
            QsTileConfig("vision", "Vision HUD", true),
            QsTileConfig("fusion", "Genesis Fusion", false)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("QUICK SETTINGS CONFIG", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tiles) { tile ->
                TileConfigCard(tile) {
                    val index = tiles.indexOf(tile)
                    if (index != -1) {
                        tiles[index] = tile.copy(isEnabled = it)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class QsTileConfig(val id: String, val name: String, val isEnabled: Boolean)

@Composable
fun TileConfigCard(tile: QsTileConfig, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(tile.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("ID: ${tile.id}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Switch(
                checked = tile.isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}
