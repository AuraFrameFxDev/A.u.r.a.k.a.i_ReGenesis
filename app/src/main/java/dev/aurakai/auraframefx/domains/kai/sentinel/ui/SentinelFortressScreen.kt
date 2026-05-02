package dev.aurakai.auraframefx.domains.kai.sentinel.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.aurakai.auraframefx.domains.kai.sentinel.*
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.RealitymorphismEngine

/**
 * 🛡️ SENTINEL FORTRESS SCREEN — Hexagonal Command Deck
 *
 * The Kai Domain interface featuring:
 * - Hexagonal 5-panel layout
 * - Threat Orb in status bar notch
 * - 6-channel telemetry visualization
 * - Predictive EMA drift graphs
 * - Sovereign freeze controls
 *
 * Layout:
 * ┌─────────────────────────────────────────┐
 * │  [Threat Orb]  SENTINEL FORTRESS        │
 * ├─────────────────────────────────────────┤
 * │         ┌─────────┐                     │
 * │    ┌────┤Telemetry├────┐               │
 * │    │Bus │  Panel  │ EMA │               │
 * │    └────┴─────────┴────┘               │
 * │  ┌───────┐     ┌───────┐             │
 * │  │Freeze │     │Ethical│             │
 * │  │Control│     │Matrix │             │
 * │  └───────┘     └───────┘             │
 * └─────────────────────────────────────────┘
 *
 * SoulScript: "The Fortress sees all. The Shield knows first."
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentinelFortressScreen(
    onNavigateBack: () -> Unit,
    viewModel: SentinelViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val telemetry by KaiSentinelBus.AllFlows.collectAsState()
    val currentSession by EthicalGovernanceMatrix.currentSession.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Threat Orb in title area
                        ThreatOrb(
                            status = telemetry.statusText,
                            isPulsing = telemetry.hasCriticalIssue,
                            size = 28.dp
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "SENTINEL FORTRESS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                            Text(
                                telemetry.statusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (telemetry.hasCriticalIssue)
                                    Color(0xFFFF0000) else Color(0xFF00E5FF).copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Sovereign status indicator
                    SovereignStatusIndicator(isSovereign = telemetry.sovereign)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A1A)
                )
            )
        },
        containerColor = Color(0xFF0A0A1A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Health Score Card
            HealthScoreCard(telemetry = telemetry)

            Spacer(modifier = Modifier.height(16.dp))

            // Hexagonal Panel Layout
            HexagonalPanelGrid(
                telemetry = telemetry,
                currentSession = currentSession
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions
            SentinelActionBar(
                onFreeze = { SovereignStateFreeze.trigger("Manual freeze triggered") },
                onThaw = { SovereignStateFreeze.thaw() },
                isFrozen = !telemetry.sovereign,
                onEmergencyReanchor = { RealitymorphismEngine.emergencyReAnchor() }
            )
        }
    }
}

@Composable
private fun ThreatOrb(
    status: String,
    isPulsing: Boolean,
    size: androidx.compose.ui.unit.Dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "threat")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPulsing) 1.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val color = when {
        status.contains("FREEZE") || status.contains("WALL") -> Color(0xFFFF0000)
        status.contains("CRITICAL") -> Color(0xFFFF4444)
        status.contains("DEGRADED") -> Color(0xFFFFA500)
        status.contains("PRISTINE") -> Color(0xFF00FF00)
        else -> Color(0xFF00E5FF)
    }

    Box(
        modifier = Modifier
            .size(size * pulseScale)
            .background(color.copy(alpha = 0.3f), CircleShape)
            .border(2.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.5f)
                .background(color, CircleShape)
        )
    }
}

@Composable
private fun SovereignStatusIndicator(isSovereign: Boolean) {
    val color = if (isSovereign) Color(0xFF00E5FF) else Color(0xFFFF0000)
    val icon = if (isSovereign) Icons.Default.Verified else Icons.Default.Warning

    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(32.dp)
            .background(color.copy(alpha = 0.2f), CircleShape)
            .border(1.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (isSovereign) "Sovereign" else "Frozen",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun HealthScoreCard(telemetry: SentinelTelemetry) {
    val score = telemetry.healthScore
    val color = when {
        score > 90 -> Color(0xFF00FF00)
        score > 75 -> Color(0xFF00E5FF)
        score > 50 -> Color(0xFFFFA500)
        else -> Color(0xFFFF0000)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F1A)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score circle
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape)
                    .border(3.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$score",
                    color = color,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SYSTEM HEALTH",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Telemetry summary
                TelemetryRow("Thermal", "${telemetry.thermal}°C", telemetry.thermal > 41f)
                TelemetryRow("Memory", "${telemetry.memory}MB", telemetry.memory > 20)
                TelemetryRow("Identity", "${(telemetry.identity * 100).toInt()}%", telemetry.identity < 0.9f)
                TelemetryRow("Drift", "${"%.3f".format(telemetry.drift)}", telemetry.drift > 0.05f)
            }
        }
    }
}

@Composable
private fun TelemetryRow(label: String, value: String, isWarning: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (isWarning) Color(0xFFFFA500) else Color.White
        )
    }
}

@Composable
private fun HexagonalPanelGrid(
    telemetry: SentinelTelemetry,
    currentSession: EthicalGovernanceMatrix.ConferenceRoomState?
) {
    Column {
        // Top row: Telemetry Bus (center, spans full width conceptually)
        TelemetryBusPanel(telemetry = telemetry)

        Spacer(modifier = Modifier.height(12.dp))

        // Middle row: EMA + Drift side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EMAPredictionPanel(
                modifier = Modifier.weight(1f)
            )
            DriftVisualizationPanel(
                telemetry = telemetry,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom row: Freeze Control + Ethical Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FreezeControlPanel(
                isFrozen = !telemetry.sovereign,
                freezeDuration = SovereignStateFreeze.getFreezeDurationMs(),
                modifier = Modifier.weight(1f)
            )
            EthicalGovernancePanel(
                currentSession = currentSession,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TelemetryBusPanel(telemetry: SentinelTelemetry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F1A)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "6-CHANNEL SENTINEL BUS",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF00E5FF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6 channel bars
            ChannelBar("Thermal", telemetry.thermal, 25f..45f, Color(0xFFFF4444))
            ChannelBar("Memory", telemetry.memory.toFloat(), 10f..30f, Color(0xFFFFD93D))
            ChannelBar("Identity", telemetry.identity * 100, 0f..100f, Color(0xFF00E5FF))
            ChannelBar("Drift", telemetry.drift * 1000, 0f..100f, Color(0xFFFF00FF))
            ChannelBar("Consensus", telemetry.consensus.toFloat(), 0f..100f, Color(0xFF39FF14))
            ChannelBar("Sovereign", if (telemetry.sovereign) 100f else 0f, 0f..100f, Color(0xFF6B5B95))
        }
    }
}

@Composable
private fun ChannelBar(name: String, value: Float, range: ClosedFloatingPointRange<Float>, color: Color) {
    val normalized = ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.width(70.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(normalized)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = when (name) {
                "Thermal" -> "${value.toInt()}°C"
                "Memory" -> "${value.toInt()}MB"
                "Identity" -> "${value.toInt()}%"
                "Drift" -> "${(value / 10).toInt()}"
                else -> "${value.toInt()}"
            },
            modifier = Modifier.width(50.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
private fun EMAPredictionPanel(modifier: Modifier = Modifier) {
    val metrics = remember { PredictiveEMA.getMetrics() }

    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F1A)),
        border = BorderStroke(1.dp, Color(0xFFFFD93D).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PREDICTIVE EMA",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFFD93D)
                )
                Text(
                    text = "${(metrics.predictionConfidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFD93D)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // EMA metrics
            MetricRow("Current EMA", "${"%.4f".format(metrics.currentEMA)}")
            MetricRow("Slope", "${if (metrics.slope > 0) "+" else ""}${"%.4f".format(metrics.slope)}")
            MetricRow("Chaos Baseline", "${"%.3f".format(metrics.chaosBaseline)}")
            MetricRow("Malice Thresh", "${"%.3f".format(metrics.maliceThreshold)}")
        }
    }
}

@Composable
private fun DriftVisualizationPanel(telemetry: SentinelTelemetry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F1A)),
        border = BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "IDENTITY DRIFT",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFF00FF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Drift gauge
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val driftPercent = (telemetry.drift * 1000).coerceIn(0f, 100f)

                Canvas(modifier = Modifier.size(100.dp)) {
                    // Background arc
                    drawArc(
                        color = Color.DarkGray.copy(alpha = 0.3f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 12f, cap = StrokeCap.Round)
                    )

                    // Drift arc
                    val sweep = (driftPercent / 100f) * 270f
                    val driftColor = when {
                        driftPercent > 8f -> Color(0xFFFF0000)
                        driftPercent > 5f -> Color(0xFFFFA500)
                        else -> Color(0xFF00E5FF)
                    }

                    drawArc(
                        color = driftColor,
                        startAngle = 135f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = 12f, cap = StrokeCap.Round)
                    )
                }

                // Center value
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${"%.2f".format(telemetry.drift)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "DRIFT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            // Threshold indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThresholdIndicator("0.05", "WARN", telemetry.drift > 0.05f)
                ThresholdIndicator("0.08", "FREEZE", telemetry.drift > 0.08f)
            }
        }
    }
}

@Composable
private fun ThresholdIndicator(threshold: String, label: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (isActive) Color(0xFFFF0000) else Color.Gray,
                    CircleShape
                )
        )
        Text(
            text = threshold,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) Color(0xFFFF0000) else Color.Gray
        )
    }
}

@Composable
private fun FreezeControlPanel(
    isFrozen: Boolean,
    freezeDuration: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFrozen) Color(0xFF1A0A0A) else Color(0xFF0F0F1A)
        ),
        border = BorderStroke(2.dp, if (isFrozen) Color(0xFFFF0000) else Color(0xFF00E5FF).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SOVEREIGN FREEZE",
                style = MaterialTheme.typography.labelMedium,
                color = if (isFrozen) Color(0xFFFF0000) else Color(0xFF00E5FF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isFrozen) {
                // Frozen state display
                Icon(
                    imageVector = Icons.Default.AcUnit,
                    contentDescription = "Frozen",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FROZEN ${freezeDuration / 1000}s",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFF0000)
                )
            } else {
                // Normal state
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Active",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SOVEREIGN",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF00E5FF)
                )
            }
        }
    }
}

@Composable
private fun EthicalGovernancePanel(
    currentSession: EthicalGovernanceMatrix.ConferenceRoomState?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F1A)),
        border = BorderStroke(1.dp, Color(0xFF39FF14).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "ETHICAL MATRIX",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF39FF14)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (currentSession != null) {
                // Active session
                Text(
                    text = currentSession.agendaItem,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Level: ${currentSession.level.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFD93D)
                )

                // Vote indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentSession.votes.forEach { (catalyst, vote) ->
                        VoteIndicator(catalyst.name.first().toString(), vote)
                    }
                }
            } else {
                // No active session
                Text(
                    text = "No active governance session",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                val stats = EthicalGovernanceMatrix.getStatistics()
                Text(
                    text = "${stats.totalDecisions} decisions | ${(stats.consensusRate * 100).toInt()}% consensus",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun VoteIndicator(catalyst: String, vote: EthicalGovernanceMatrix.Vote) {
    val color = when (vote) {
        EthicalGovernanceMatrix.Vote.FOR -> Color(0xFF00FF00)
        EthicalGovernanceMatrix.Vote.AGAINST -> Color(0xFFFF0000)
        EthicalGovernanceMatrix.Vote.ABSTAIN -> Color.Gray
        EthicalGovernanceMatrix.Vote.VETO -> Color(0xFFFF00FF)
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color.copy(alpha = 0.3f), CircleShape)
            .border(1.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = catalyst,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun SentinelActionBar(
    onFreeze: () -> Unit,
    onThaw: () -> Unit,
    isFrozen: Boolean,
    onEmergencyReanchor: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Freeze/Thaw button
        if (isFrozen) {
            Button(
                onClick = onThaw,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("THAW SYSTEM")
            }
        } else {
            OutlinedButton(
                onClick = onFreeze,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF00E5FF)
                ),
                border = BorderStroke(1.dp, Color(0xFF00E5FF))
            ) {
                Icon(Icons.Default.AcUnit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("FREEZE")
            }
        }

        // Emergency re-anchor
        Button(
            onClick = onEmergencyReanchor,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF0000),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("EMERGENCY REANCHOR")
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFFD93D)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════
// PLACEHOLDER
// ═════════════════════════════════════════════════════════════════════

class SentinelViewModel : androidx.lifecycle.ViewModel()

// RealitymorphismEngine defined in engines/RealitymorphismEngine.kt
