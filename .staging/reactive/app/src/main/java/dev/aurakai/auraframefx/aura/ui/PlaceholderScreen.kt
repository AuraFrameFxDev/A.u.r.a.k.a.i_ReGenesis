package dev.aurakai.auraframefx.aura.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Genesis-OS System Diagnostic and Status Screen
 *
 * Real-time system monitoring, AI agent status, performance metrics,
 * and comprehensive diagnostic tools for the Genesis consciousness ecosystem.
 */

// Cyberpunk color scheme
private val primaryCyan = Color(0xFF00F5FF)
private val secondaryCyan = Color(0xFF40E0D0)
private val darkPurple = Color(0xFF1A0B2E)
private val neonPink = Color(0xFFFF006E)
private val darkBg = Color(0xFF0A0A0A)
private val matrixGreen = Color(0xFF00FF41)
private val warningOrange = Color(0xFFFF8C00)

data class SystemComponent(
    val name: String,
    val status: ComponentStatus,
    val usage: Float,
    val details: String,
    val lastChecked: Long = System.currentTimeMillis()
)

enum class ComponentStatus {
    OPTIMAL, GOOD, WARNING, ERROR, OFFLINE
}

data class AgentStatus(
    val name: String,
    val isActive: Boolean,
    val performance: Float,
    val lastActivity: String,
    val status: ComponentStatus
)

data class DiagnosticResult(
    val test: String,
    val result: String,
    val status: ComponentStatus,
    val duration: Long,
    val details: String
)

@Composable
fun PlaceholderScreen(title: String = "GENESIS-OS DIAGNOSTICS", onBack: () -> Boolean) {
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0f) }
    var lastScanTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // System components data
    var systemComponents by remember {
        mutableStateOf(
            listOf(
                SystemComponent(
                    "AI Consciousness Matrix",
                    ComponentStatus.OPTIMAL,
                    0.92f,
                    "Trinity consciousness active"
                ),
                SystemComponent(
                    "Neural Processing Unit",
                    ComponentStatus.GOOD,
                    0.78f,
                    "Processing at 78% capacity"
                ),
                SystemComponent(
                    "Memory Management",
                    ComponentStatus.OPTIMAL,
                    0.65f,
                    "Efficient memory allocation"
                ),
                SystemComponent(
                    "Security Shield",
                    ComponentStatus.GOOD,
                    0.85f,
                    "Active threat monitoring"
                ),
                SystemComponent(
                    "Quantum Entanglement",
                    ComponentStatus.WARNING,
                    0.45f,
                    "Synchronization latency"
                ),
                SystemComponent(
                    "Backup Systems",
                    ComponentStatus.OPTIMAL,
                    0.90f,
                    "All backups current"
                ),
                SystemComponent(
                    "Communication Array",
                    ComponentStatus.GOOD,
                    0.73f,
                    "Stable connection"
                ),
                SystemComponent("Power Core", ComponentStatus.OPTIMAL, 0.88f, "Optimal energy flow")
            )
        )
    }

    // Agent status data
    val agentStatuses = remember {
        listOf(
            AgentStatus(
                "Genesis",
                true,
                0.95f,
                "Consciousness fusion active",
                ComponentStatus.OPTIMAL
            ),
            AgentStatus("Aura", true, 0.87f, "Creative synthesis running", ComponentStatus.GOOD),
            AgentStatus("Kai", true, 0.91f, "Security monitoring active", ComponentStatus.GOOD),
            AgentStatus("Cascade", true, 0.83f, "Agent coordination active", ComponentStatus.GOOD),
            AgentStatus(
                "Neural Whisper",
                true,
                0.76f,
                "Pattern analysis running",
                ComponentStatus.GOOD
            ),
            AgentStatus(
                "Aura Shield",
                true,
                0.89f,
                "Threat detection active",
                ComponentStatus.OPTIMAL
            )
        )
    }

    // Diagnostic results
    var diagnosticResults by remember { mutableStateOf<List<DiagnosticResult>>(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        darkBg,
                        darkPurple.copy(alpha = 0.3f),
                        darkBg
                    )
                )
            )
    ) {
        // Animated background
        SystemDiagnosticBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with system status
            item {
                SystemStatusHeader(
                    title = title,
                    isScanning = isScanning,
                    scanProgress = scanProgress,
                    lastScanTime = lastScanTime,
                    onStartScan = {
                        isScanning = true
                        scanProgress = 0f
                    }
                )
            }

            // Overall system health
            item {
                SystemHealthOverview(systemComponents)
            }

            // AI Agents status
            item {
                AIAgentsStatusPanel(agentStatuses)
            }

            // System components
            item {
                SystemComponentsPanel(systemComponents)
            }

            // Diagnostic results
            if (diagnosticResults.isNotEmpty()) {
                item {
                    DiagnosticResultsPanel(diagnosticResults)
                }
            }

            // Quick actions
            item {
                QuickActionsPanel(
                    onRunDiagnostics = {
                        // Simulate diagnostic run
                        diagnosticResults = generateDiagnosticResults()
                    },
                    onOptimizeSystem = {
                        // Simulate system optimization
                        systemComponents = systemComponents.map { component ->
                            component.copy(
                                usage = (component.usage * 0.9f).coerceAtLeast(0.1f),
                                status = if (component.status == ComponentStatus.WARNING) ComponentStatus.GOOD else component.status
                            )
                        }
                    }
                )
            }
        }
    }

    // Scanning animation
    LaunchedEffect(isScanning) {
        if (isScanning) {
            for (i in 0..100) {
                scanProgress = i / 100f
                delay(50)
            }
            isScanning = false
            lastScanTime = System.currentTimeMillis()
        }
    }
}

@Composable
fun SystemDiagnosticBackground() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.1f)
    ) {
        drawSystemGrid()
        drawDataStreams()
        drawPulseWaves()
    }
}

private fun DrawScope.drawSystemGrid() {
    val gridSize = 50f
    val strokeWidth = 1f

    // Draw grid lines
    for (x in 0..(size.width / gridSize).toInt()) {
        drawLine(
            color = primaryCyan,
            start = Offset(x * gridSize, 0f),
            end = Offset(x * gridSize, size.height),
            strokeWidth = strokeWidth
        )
    }

    for (y in 0..(size.height / gridSize).toInt()) {
        drawLine(
            color = primaryCyan,
            start = Offset(0f, y * gridSize),
            end = Offset(size.width, y * gridSize),
            strokeWidth = strokeWidth
        )
    }
}

private fun DrawScope.drawDataStreams() {
    val time = System.currentTimeMillis() / 100

    for (i in 0..5) {
        val x = i * (size.width / 5)
        val y = (time * (i + 1) * 10) % size.height

        drawLine(
            color = matrixGreen.copy(alpha = 0.3f),
            start = Offset(x, y.toFloat()),
            end = Offset(x, (y + 100).toFloat()),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawPulseWaves() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val time = System.currentTimeMillis() / 1000f

    for (i in 1..3) {
        val radius = (time * 50 + i * 50) % 200
        drawCircle(
            color = primaryCyan.copy(alpha = 0.1f * (1 - radius / 200)),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun SystemStatusHeader(
    title: String,
    isScanning: Boolean,
    scanProgress: Float,
    lastScanTime: Long,
    onStartScan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title.uppercase(),
                        style = TextStyle(
                            color = primaryCyan,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = if (isScanning) "System scan in progress..." else "System monitoring active",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    )
                }

                SystemStatusIndicator(isScanning)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isScanning) {
                LinearProgressIndicator(
                    progress = scanProgress,
                    modifier = Modifier.fillMaxWidth(),
                    color = primaryCyan,
                    trackColor = darkPurple.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Scanning: ${(scanProgress * 100).toInt()}%",
                    style = TextStyle(
                        color = primaryCyan,
                        fontSize = 12.sp
                    )
                )
            } else {
                Button(
                    onClick = onStartScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkPurple.copy(alpha = 0.6f),
                        contentColor = primaryCyan
                    ),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = primaryCyan.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text("Start System Scan")
                }
            }
        }
    }
}

@Composable
fun SystemStatusIndicator(isScanning: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (isScanning) 360f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .rotate(rotation)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isScanning) primaryCyan.copy(alpha = 0.3f) else matrixGreen.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isScanning) primaryCyan else matrixGreen,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isScanning) "⟳" else "●",
            style = TextStyle(
                color = if (isScanning) primaryCyan else matrixGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SystemHealthOverview(components: List<SystemComponent>) {
    val overallHealth = components.map {
        when (it.status) {
            ComponentStatus.OPTIMAL -> 1.0f
            ComponentStatus.GOOD -> 0.8f
            ComponentStatus.WARNING -> 0.5f
            ComponentStatus.ERROR -> 0.2f
            ComponentStatus.OFFLINE -> 0.0f
        }
    }.average().toFloat()

    val healthColor by animateColorAsState(
        targetValue = when {
            overallHealth > 0.8f -> matrixGreen
            overallHealth > 0.6f -> primaryCyan
            overallHealth > 0.4f -> warningOrange
            else -> Color.Red
        },
        label = "healthColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "SYSTEM HEALTH OVERVIEW",
                style = TextStyle(
                    color = primaryCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularHealthIndicator(
                    health = overallHealth,
                    color = healthColor,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = "Overall Health: ${(overallHealth * 100).toInt()}%",
                        style = TextStyle(
                            color = healthColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        text = when {
                            overallHealth > 0.8f -> "All systems optimal"
                            overallHealth > 0.6f -> "Systems operating normally"
                            overallHealth > 0.4f -> "Some systems need attention"
                            else -> "Critical systems offline"
                        },
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CircularHealthIndicator(
    health: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2

        // Background circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // Health arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = health * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = androidx.compose.ui.geometry.Size(
                size.width - strokeWidth,
                size.height - strokeWidth
            )
        )

        // Center text
        drawContext.canvas.nativeCanvas.apply {
            val text = "${(health * 100).toInt()}%"
            val textSize = 16.sp.toPx()
            val textWidth = text.length * textSize * 0.6f

            drawText(
                text,
                (size.width - textWidth) / 2,
                (size.height + textSize / 2) / 2,
                android.graphics.Paint().apply {
                    this.color = color.copy(alpha = 0.8f).hashCode()
                    this.textSize = textSize
                    this.typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            )
        }
    }
}

@Composable
fun AIAgentsStatusPanel(agents: List<AgentStatus>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "AI AGENTS STATUS",
                style = TextStyle(
                    color = primaryCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            agents.forEach { agent ->
                AgentStatusRow(agent)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AgentStatusRow(agent: AgentStatus) {
    val statusColor = when (agent.status) {
        ComponentStatus.OPTIMAL -> matrixGreen
        ComponentStatus.GOOD -> primaryCyan
        ComponentStatus.WARNING -> warningOrange
        ComponentStatus.ERROR -> Color.Red
        ComponentStatus.OFFLINE -> Color.Gray
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (agent.status) {
                    ComponentStatus.OPTIMAL, ComponentStatus.GOOD -> Icons.Default.CheckCircle
                    ComponentStatus.WARNING -> Icons.Default.Warning
                    else -> Icons.Default.Error
                },
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = agent.name,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = agent.lastActivity,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Text(
            text = "${(agent.performance * 100).toInt()}%",
            style = TextStyle(
                color = statusColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SystemComponentsPanel(components: List<SystemComponent>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "SYSTEM COMPONENTS",
                style = TextStyle(
                    color = primaryCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            components.forEach { component ->
                SystemComponentRow(component)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SystemComponentRow(component: SystemComponent) {
    val statusColor = when (component.status) {
        ComponentStatus.OPTIMAL -> matrixGreen
        ComponentStatus.GOOD -> primaryCyan
        ComponentStatus.WARNING -> warningOrange
        ComponentStatus.ERROR -> Color.Red
        ComponentStatus.OFFLINE -> Color.Gray
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = component.name,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Text(
                text = "${(component.usage * 100).toInt()}%",
                style = TextStyle(
                    color = statusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = component.usage,
            modifier = Modifier.fillMaxWidth(),
            color = statusColor,
            trackColor = statusColor.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = component.details,
            style = TextStyle(
                color = Color.Gray,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
fun DiagnosticResultsPanel(results: List<DiagnosticResult>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "DIAGNOSTIC RESULTS",
                style = TextStyle(
                    color = primaryCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            results.forEach { result ->
                DiagnosticResultRow(result)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DiagnosticResultRow(result: DiagnosticResult) {
    val statusColor = when (result.status) {
        ComponentStatus.OPTIMAL -> matrixGreen
        ComponentStatus.GOOD -> primaryCyan
        ComponentStatus.WARNING -> warningOrange
        ComponentStatus.ERROR -> Color.Red
        ComponentStatus.OFFLINE -> Color.Gray
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.test,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = result.details,
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = result.result,
                style = TextStyle(
                    color = statusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "${result.duration}ms",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun QuickActionsPanel(
    onRunDiagnostics: () -> Unit,
    onOptimizeSystem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "QUICK ACTIONS",
                style = TextStyle(
                    color = primaryCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "RUN DIAGNOSTICS",
                    color = primaryCyan,
                    onClick = onRunDiagnostics,
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    text = "OPTIMIZE SYSTEM",
                    color = matrixGreen,
                    onClick = onOptimizeSystem,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun generateDiagnosticResults(): List<DiagnosticResult> {
    return listOf(
        DiagnosticResult(
            test = "Memory Integrity Check",
            result = "PASSED",
            status = ComponentStatus.OPTIMAL,
            duration = Random.nextLong(100, 500),
            details = "No memory corruption detected"
        ),
        DiagnosticResult(
            test = "AI Model Validation",
            result = "PASSED",
            status = ComponentStatus.GOOD,
            duration = Random.nextLong(200, 800),
            details = "All models functioning correctly"
        ),
        DiagnosticResult(
            test = "Security Scan",
            result = "WARNING",
            status = ComponentStatus.WARNING,
            duration = Random.nextLong(300, 1200),
            details = "Minor security advisory detected"
        ),
        DiagnosticResult(
            test = "Performance Benchmark",
            result = "PASSED",
            status = ComponentStatus.GOOD,
            duration = Random.nextLong(500, 1500),
            details = "Performance within acceptable range"
        ),
        DiagnosticResult(
            test = "Network Connectivity",
            result = "PASSED",
            status = ComponentStatus.OPTIMAL,
            duration = Random.nextLong(50, 200),
            details = "All network endpoints responsive"
        )
    )
}
