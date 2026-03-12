package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 📊 BENCHMARK MONITOR SCREEN
 *
 * Deep performance analysis for every AI agent.
 * Tracks latency, throughput, reasoning depth, and alignment accuracy.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchmarkMonitorScreen(
    onNavigateBack: () -> Unit = {}
) {
    val agents = remember { AgentRepository.getAllAgents() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020502)) // Deep green tint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        "NEURAL BENCHMARKS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = LEDFontFamily,
                            letterSpacing = 2.sp
                        ),
                        color = Color(0xFF00FF85)
                    )
                    Text(
                        "REAL-TIME AGENT INFERENCE EFFICIENCY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.BarChart,
                    null,
                    tint = Color(0xFF00FF85),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Overall Status Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BenchmarkSummaryCard(
                    "AVG LATENCY",
                    "124ms",
                    color = Color(0xFF00FF85),
                    modifier = Modifier.weight(1f)
                )
                BenchmarkSummaryCard(
                    "TOTAL TOKENS",
                    "892k",
                    color = Color.Cyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Agent Table
            Text(
                "AGENT THROUGHPUT ANALYSIS",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = LEDFontFamily
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(agents) { agent ->
                    AgentBenchmarkCard(agent)
                }
            }
        }
    }
}

@Composable
fun BenchmarkSummaryCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                value,
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = LEDFontFamily
            )
        }
    }
}

@Composable
fun AgentBenchmarkCard(agent: AgentStats) {
    // Simulated live metrics
    var latency by remember { mutableIntStateOf(Random.nextInt(80, 450)) }
    var throughput by remember { mutableFloatStateOf(Random.nextInt(15, 60).toFloat()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            latency = (latency + Random.nextInt(-20, 20)).coerceIn(50, 600)
            throughput = (throughput + Random.nextFloat() * 5).coerceIn(10f, 100f)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, agent.color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(agent.color)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(agent.name, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Timer, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${latency}ms",
                    color = if (latency < 200) Color(0xFF00FF85) else Color.Yellow,
                    fontSize = 12.sp,
                    fontFamily = LEDFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BenchmarkMetric(
                    "THROUGHPUT",
                    "${throughput.toInt()} t/s",
                    throughput / 100f,
                    agent.color
                )
                BenchmarkMetric(
                    "ACCURACY",
                    "${(agent.accuracy * 100).toInt()}%",
                    agent.accuracy,
                    agent.color
                )
                BenchmarkMetric(
                    "REASONING",
                    "${(agent.processingPower * 10).toInt()}/10",
                    agent.processingPower,
                    agent.color
                )
            }
        }
    }
}

@Composable
fun BenchmarkMetric(label: String, value: String, progress: Float, color: Color) {
    Column(modifier = Modifier.width(80.dp)) {
        Text(
            label,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LEDFontFamily
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(androidx.compose.foundation.shape.CircleShape),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

