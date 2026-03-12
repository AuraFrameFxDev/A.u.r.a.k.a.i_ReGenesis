package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.MonitoringViewModel

/**
 * 📊 MONITORING HUDS SCREEN
 * 
 * Provides real-time visual telemetery for system performance, 
 * network integrity, and neural load metrics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringHUDsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val cpu by viewModel.cpuUsage.collectAsState()
    val ram by viewModel.ramUsage.collectAsState()
    val latency by viewModel.latency.collectAsState()
    val integrity by viewModel.integrity.collectAsState()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF00050A), Color(0xFF001525))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "REAL-TIME DASHBOARDS",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
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
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
                .padding(16.dp)
        ) {

            // Integrity Pulse Matrix
            IntegrityPulseCard(integrity)

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    HudGaugeCard(
                        "CPU LOAD",
                        "${cpu.toInt()}%",
                        cpu / 100f,
                        Icons.Default.Speed,
                        Color.Cyan
                    )
                }
                item {
                    HudGaugeCard(
                        "RAM USAGE",
                        "${String.format("%.1f", ram)}GB",
                        ram / 4f,
                        Icons.Default.Memory,
                        Color.Magenta
                    )
                }
                item {
                    HudGaugeCard(
                        "LATENCY",
                        "${latency}ms",
                        (latency / 500f).coerceIn(0f, 1f),
                        Icons.Default.Public,
                        Color.Green
                    )
                }
                item {
                    HudGaugeCard("THREATS", "0 DETECTED", 0f, Icons.Default.Warning, Color.Red)
                }
            }
        }
    }
}

@Composable
fun IntegrityPulseCard(integrity: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.Cyan.copy(alpha = 0.1f),
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawArc(
                        color = Color.Cyan,
                        startAngle = -90f,
                        sweepAngle = 360f * integrity,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
                Text(
                    "${(integrity * 100).toInt()}%",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text("SYSTEM INTEGRITY", color = Color.White, fontWeight = FontWeight.ExtraBold)
                Text(
                    "All cores operational. Genesis bridge stable.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { integrity },
                    color = Color.Cyan,
                    trackColor = Color.Gray.copy(alpha = 0.2f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

@Composable
fun HudGaugeCard(
    title: String,
    value: String,
    progress: Float,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(accentColor)
                )
            }
        }
    }
}

