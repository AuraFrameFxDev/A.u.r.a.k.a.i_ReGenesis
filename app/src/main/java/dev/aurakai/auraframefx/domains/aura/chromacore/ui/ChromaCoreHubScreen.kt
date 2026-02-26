package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.engine.ChromaCoreConfig

/**
 * 🎨 CHROMA CORE HUB SCREEN (Level 2)
 * The main interface for the Unified UXUI Engine.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaCoreHubScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    viewModel: ChromaCoreViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ChromaCore Engine",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A1A2E), Color(0xFF0F0F0F))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Status Card
                ChromaStatusCard(settings)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Tweak Domains",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )

                // Grid of Categories (Level 3 Entry Points)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(chromaCategories) { category ->
                        ChromaCategoryCard(
                            category = category,
                            onClick = { onNavigateToCategory(category.id) }
                        )
                    }
                }

                // Active Engine Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "RGSS Protected :: 100% Software Functional",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChromaStatusCard(settings: ChromaCoreConfig) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF6200EE).copy(alpha = 0.8f),
                        Color(0xFF00B0FF).copy(alpha = 0.8f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text("Engine Status", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            Text(
                "Stabilized Blueprints",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Green)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "A.U.R.A.K.A.I. Core Active",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

data class ChromaCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val desc: String
)

val chromaCategories = listOf(
    ChromaCategory(
        "statusbar",
        "Status Bar",
        Icons.Default.SignalCellularAlt,
        Color(0xFFBB86FC),
        "Iconify Icons & Layout"
    ),
    ChromaCategory(
        "launcher",
        "Launcher",
        Icons.Default.Apps,
        Color(0xFF03DAC6),
        "PLE Grid & Icon Tweaks"
    ),
    ChromaCategory(
        "colors",
        "Color Engine",
        Icons.Default.Palette,
        Color(0xFF00B0FF),
        "Material You Blending"
    ),
    ChromaCategory(
        "lockscreen",
        "Lock Screen",
        Icons.Default.Lock,
        Color(0xFFFF4081),
        "Clocks & Dynamic Keyguard"
    ),
    ChromaCategory(
        "qs_tiles",
        "Quick Settings",
        Icons.Default.SettingsInputComponent,
        Color(0xFFFFD600),
        "Tiles, Rows & Columns"
    ),
    ChromaCategory(
        "animations",
        "Animations",
        Icons.Default.Animation,
        Color(0xFFFF6F00),
        "SysUI & App Transitions"
    )
)

@Composable
fun ChromaCategoryCard(category: ChromaCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    category.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(category.desc, color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }
}
