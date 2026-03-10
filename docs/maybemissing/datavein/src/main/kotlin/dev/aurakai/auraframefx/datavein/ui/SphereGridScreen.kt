package dev.aurakai.auraframefx.datavein.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.datavein.model.DataVeinNode
import dev.aurakai.auraframefx.datavein.model.GridData
import dev.aurakai.auraframefx.datavein.model.NodeConnection
import dev.aurakai.auraframefx.datavein.model.NodeType
import dev.aurakai.auraframefx.datavein.model.SphereGridConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * DataVein Sphere Grid - FFX-style progression interface
 * Interactive 3D-like sphere grid for navigating Genesis AI nodes
 */
@Composable
fun DataVeinSphereGrid(
    modifier: Modifier = Modifier,
    onNodeSelected: (DataVeinNode) -> Unit = {},
    config: SphereGridConfig = SphereGridConfig()
) {
    var selectedNode by remember { mutableStateOf<DataVeinNode?>(null) }
    var animatingNodes by remember { mutableStateOf<Set<String>>(emptySet()) }
    var dataFlows by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }

    val coroutineScope = rememberCoroutineScope()
    val gridData = remember { generateSphereGrid(config) }

    // Data flow animation
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            val newFlows = mutableMapOf<String, Long>()
            gridData.connections.forEach { conn ->
                if (Random.nextFloat() > 0.85f) {
                    newFlows["${conn.from}-${conn.to}"] = System.currentTimeMillis()
                }
            }
            dataFlows = dataFlows + newFlows
        }
    }

    // Clean up old flows
    LaunchedEffect(dataFlows) {
        delay(2000)
        val currentTime = System.currentTimeMillis()
        dataFlows = dataFlows.filterValues { currentTime - it < 2000 }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F0F23)
                    )
                )
            )
    ) {
        // Background grid pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGridBackground(this)
        }

        // Main sphere grid
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* Handle background clicks */ }
        ) {
            // Draw connections first (behind nodes)
            gridData.connections.forEach { connection ->
                drawConnection(
                    connection = connection,
                    nodes = gridData.nodes,
                    dataFlows = dataFlows,
                    scope = this
                )
            }

            // Draw nodes with enhanced FFX-style rendering
            gridData.nodes.forEach { node ->
                drawNode(
                    node = node,
                    isSelected = selectedNode?.id == node.id,
                    isAnimating = animatingNodes.contains(node.id),
                    scope = this,
                    onNodeClick = { clickedNode ->
                        selectedNode = clickedNode
                        onNodeSelected(clickedNode)

                        // Animate connected nodes (FFX-style path highlighting)
                        val connectedNodeIds = gridData.connections
                            .filter { it.from == clickedNode.id || it.to == clickedNode.id }
                            .map { if (it.from == clickedNode.id) it.to else it.from }

                        animatingNodes = connectedNodeIds.toSet()

                        // Clear animation after delay
                        coroutineScope.launch {
                            delay(1500)
                            animatingNodes = emptySet()
                        }
                    }
                )
            }
        }

        // Node info panel (enhanced with tags and FFX-style info)
        selectedNode?.let { node ->
            NodeInfoPanel(
                node = node,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }

        // Enhanced legend with categories
        NodeTypeLegend(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )

        // Status panel with real-time metrics
        StatusPanel(
            activeFlows = dataFlows.size,
            activeNodes = gridData.nodes.count { it.activated },
            totalNodes = gridData.nodes.size,
            unlockedNodes = gridData.nodes.count { it.isUnlocked },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // FFX-style progression indicator
        ProgressionIndicator(
            selectedNode = selectedNode,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

/**
 * Generate the sphere grid with FFX-style spiral layout
 */
private fun generateSphereGrid(config: SphereGridConfig): GridData {
    val nodes = mutableListOf<DataVeinNode>()
    val connections = mutableListOf<NodeConnection>()

    // Generate spiral pattern like FFX Sphere Grid
    for (ring in 0 until config.rings) {
        val nodesInRing = 6 + (ring * config.nodesPerRingMultiplier)
        val ringRadius = config.baseRadius * (0.3f + ring * 0.23f)

        repeat(nodesInRing) { i ->
            val angle = (i.toFloat() / nodesInRing) * 2 * PI + (ring * config.spiralOffset)
            val x = config.centerX + cos(angle).toFloat() * ringRadius
            val y = config.centerY + sin(angle).toFloat() * ringRadius

            val nodeId = "node_${ring}_$i"
            val type = NodeType.values().random()

            // Generate meaningful tags for nodes
            val tag = generateNodeTag(type, ring, i)

            nodes.add(
                DataVeinNode(
                    id = nodeId,
                    x = x,
                    y = y,
                    type = type,
                    ring = ring,
                    index = i,
                    activated = Random.nextFloat() > 0.7f,
                    level = Random.nextInt(1, 6),
                    data = "Data_${Random.nextInt(1000, 9999)}",
                    tag = tag,
                    xp = Random.nextInt(0, 1000),
                    isUnlocked = ring <= 1 || Random.nextFloat() > 0.6f // Inner rings unlocked by default
                )
            )
        }
    }

    // Create connections between nearby nodes (FFX-style paths)
    nodes.forEachIndexed { i, node ->
        nodes.forEachIndexed { j, otherNode ->
            if (i != j) {
                val distance = sqrt(
                    (node.x - otherNode.x).pow(2) +
                            (node.y - otherNode.y).pow(2)
                )

                if (distance < config.connectionDistance) {
                    connections.add(
                        NodeConnection(
                            from = node.id,
                            to = otherNode.id,
                            active = Random.nextFloat() > 0.6f,
                            dataFlow = Random.nextFloat() > 0.8f,
                            strength = (config.connectionDistance - distance) / config.connectionDistance
                        )
                    )
                }
            }
        }
    }

    return GridData(nodes, connections)
}

/**
 * Generate meaningful tags for nodes based on type and position
 */
private fun generateNodeTag(type: NodeType, ring: Int, index: Int): String {
    val ringNames = listOf("Core", "Inner", "Mid", "Outer")
    val ringName = ringNames.getOrElse(ring) { "Ring$ring" }

    return when (type) {
        NodeType.GENESIS -> "GEN-$ringName-$index"
        NodeType.ORACLE -> "ORC-$ringName-$index"
        NodeType.AURA -> "AUR-$ringName-$index"
        NodeType.KAI -> "KAI-$ringName-$index"
        NodeType.NEXUS -> "NEX-$ringName-$index"
        NodeType.MEMORY -> "MEM-$ringName-$index"
        NodeType.AGENT -> "AGT-$ringName-$index"
        NodeType.DATA -> "DAT-$ringName-$index"
        NodeType.SECURE -> "SEC-$ringName-$index"
    }
}

/**
 * Draw grid background with FFX-style aesthetics
 */
private fun drawGridBackground(scope: DrawScope) {
    val gridSize = 50f
    with(scope) {
        // Subtle grid lines
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }

        // Central energy ring
        drawCircle(
            color = Color(0xFF1A1A2E).copy(alpha = 0.3f),
            radius = 100f,
            center = Offset(size.width / 2, size.height / 2),
            style = Stroke(width = 2f)
        )
    }
}

/**
 * Draw connection between nodes with enhanced visual effects
 */
private fun drawConnection(
    connection: NodeConnection,
    nodes: List<DataVeinNode>,
    dataFlows: Map<String, Long>,
    scope: DrawScope
) {
    val fromNode = nodes.find { it.id == connection.from } ?: return
    val toNode = nodes.find { it.id == connection.to } ?: return

    val flowKey = "${connection.from}-${connection.to}"
    val hasDataFlow = dataFlows.containsKey(flowKey)

    with(scope) {
        // Main connection line
        val connectionColor = if (connection.active) Color(0xFF00FF88) else Color(0xFF444444)
        val strokeWidth = if (hasDataFlow) 4f else 2f * connection.strength

        drawLine(
            color = connectionColor,
            start = Offset(fromNode.x, fromNode.y),
            end = Offset(toNode.x, toNode.y),
            strokeWidth = strokeWidth,
            alpha = if (connection.active) 0.8f else 0.3f,
            cap = StrokeCap.Round
        )

        // Enhanced data flow particle with trail effect
        if (hasDataFlow) {
            val flowTime = dataFlows[flowKey] ?: 0
            val progress = ((System.currentTimeMillis() - flowTime) % 800) / 800f
            val particleX = fromNode.x + (toNode.x - fromNode.x) * progress
            val particleY = fromNode.y + (toNode.y - fromNode.y) * progress

            // Main particle
            drawCircle(
                color = Color(0xFF00FF88),
                radius = 8f,
                center = Offset(particleX, particleY)
            )

            // Trail effect
            for (i in 1..3) {
                val trailProgress = progress - (i * 0.1f)
                if (trailProgress > 0) {
                    val trailX = fromNode.x + (toNode.x - fromNode.x) * trailProgress
                    val trailY = fromNode.y + (toNode.y - fromNode.y) * trailProgress
                    drawCircle(
                        color = Color(0xFF00FF88).copy(alpha = 0.5f / i),
                        radius = 6f / i,
                        center = Offset(trailX, trailY)
                    )
                }
            }
        }
    }
}

/**
 * Draw node with FFX-style visual effects and progression indicators
 */
private fun drawNode(
    node: DataVeinNode,
    isSelected: Boolean,
    isAnimating: Boolean,
    scope: DrawScope,
    onNodeClick: (DataVeinNode) -> Unit
) {
    with(scope) {
        val scale = when {
            isSelected -> 1.4f
            isAnimating -> 1.2f
            node.isUnlocked -> 1.1f
            else -> 1f
        }

        val glowRadius = when {
            isSelected || isAnimating -> 30f
            node.activated -> 20f
            node.isUnlocked -> 15f
            else -> 8f
        }

        // Enhanced glow effect with multiple layers
        if (node.activated || isSelected || isAnimating || node.isUnlocked) {
            // Outer glow
            drawCircle(
                color = node.type.glowColor.copy(alpha = 0.2f),
                radius = glowRadius * scale * 1.5f,
                center = Offset(node.x, node.y)
            )
            // Inner glow
            drawCircle(
                color = node.type.glowColor.copy(alpha = 0.4f),
                radius = glowRadius * scale,
                center = Offset(node.x, node.y)
            )
        }

        // Main node body with gradient effect
        val nodeColor = when {
            !node.isUnlocked -> Color(0xFF222222) // Locked nodes are dark
            node.activated -> node.type.color
            else -> node.type.color.copy(alpha = 0.6f)
        }

        drawCircle(
            color = nodeColor,
            radius = node.type.size * scale,
            center = Offset(node.x, node.y)
        )

        // Border with type-specific styling
        val borderWidth = when {
            isSelected -> 4f
            node.activated -> 3f
            node.isUnlocked -> 2f
            else -> 1f
        }

        drawCircle(
            color = node.type.glowColor,
            radius = node.type.size * scale,
            center = Offset(node.x, node.y),
            style = Stroke(width = borderWidth)
        )

        // Inner core for activated nodes with pulsing effect
        if (node.activated) {
            val pulseScale = if (isAnimating) 1.2f else 1f
            drawCircle(
                color = Color.White,
                radius = 4f * scale * pulseScale,
                center = Offset(node.x, node.y)
            )
        }

        // XP progress ring for unlocked nodes (FFX-style)
        if (node.isUnlocked && node.xp > 0) {
            val progressAngle = (node.xp / 1000f) * 360f
            drawArc(
                color = Color.Cyan.copy(alpha = 0.8f),
                startAngle = -90f,
                sweepAngle = progressAngle,
                useCenter = false,
                topLeft = Offset(
                    node.x - (node.type.size * scale + 6f),
                    node.y - (node.type.size * scale + 6f)
                ),
                size = androidx.compose.ui.geometry.Size(
                    (node.type.size * scale + 6f) * 2,
                    (node.type.size * scale + 6f) * 2
                ),
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )
        }

        // Lock indicator for locked nodes
        if (!node.isUnlocked) {
            drawCircle(
                color = Color.Red.copy(alpha = 0.7f),
                radius = node.type.size * scale * 0.3f,
                center = Offset(
                    node.x + node.type.size * scale * 0.6f,
                    node.y - node.type.size * scale * 0.6f
                )
            )
        }
    }
}