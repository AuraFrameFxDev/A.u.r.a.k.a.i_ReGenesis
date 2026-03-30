package dev.aurakai.auraframefx.datavein.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.datavein.model.DataVeinNode
import dev.aurakai.auraframefx.datavein.model.GridData
import dev.aurakai.auraframefx.datavein.model.NodeConnection
import dev.aurakai.auraframefx.datavein.model.NodeType
import dev.aurakai.auraframefx.datavein.model.SphereGridConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing DataVein Sphere Grid state
 * Handles node interactions, progression, and real-time updates
 * Now using Hilt dependency injection
 */
@HiltViewModel
class DataVeinSphereGridViewModel @Inject constructor() : ViewModel() {

    private val _selectedNode = MutableStateFlow<DataVeinNode?>(null)
    val selectedNode: StateFlow<DataVeinNode?> = _selectedNode.asStateFlow()

    private val _gridData = MutableStateFlow(generateInitialGrid())
    val gridData: StateFlow<GridData> = _gridData.asStateFlow()

    private val _animatingNodes = MutableStateFlow<Set<String>>(emptySet())
    val animatingNodes: StateFlow<Set<String>> = _animatingNodes.asStateFlow()

    private val _dataFlows = MutableStateFlow<Map<String, Long>>(emptyMap())
    val dataFlows: StateFlow<Map<String, Long>> = _dataFlows.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Computed properties
    val activeNodes: StateFlow<Int> = gridData.map { it.nodes.count { node -> node.activated } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val unlockedNodes: StateFlow<Int> = gridData.map { it.nodes.count { node -> node.isUnlocked } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val totalNodes: StateFlow<Int> = gridData.map { it.nodes.size }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        startDataFlowSimulation()
        startFlowCleanup()
    }

    /**
     * Handle node selection and path highlighting
     */
    fun selectNode(node: DataVeinNode) {
        viewModelScope.launch {
            _selectedNode.value = node

            // Highlight connected nodes
            val currentGrid = _gridData.value
            val connectedNodeIds = currentGrid.connections
                .filter { it.from == node.id || it.to == node.id }
                .map { if (it.from == node.id) it.to else it.from }

            _animatingNodes.value = connectedNodeIds.toSet()

            // Clear animation after delay
            kotlinx.coroutines.delay(1500)
            _animatingNodes.value = emptySet()
        }
    }

    /**
     * Activate a node and update its state
     */
    fun activateNode(nodeId: String) {
        viewModelScope.launch {
            _gridData.update { currentGrid ->
                val updatedNodes = currentGrid.nodes.map { node ->
                    if (node.id == nodeId && node.isUnlocked) {
                        node.copy(
                            activated = true,
                            xp = minOf(node.xp + 100, 1000) // Gain XP for activation
                        )
                    } else {
                        node
                    }
                }
                currentGrid.copy(nodes = updatedNodes)
            }

            // Check for newly unlocked nodes
            checkAndUnlockConnectedNodes(nodeId)
        }
    }

    /**
     * Unlock nodes connected to an activated node (FFX-style progression)
     */
    private fun checkAndUnlockConnectedNodes(activatedNodeId: String) {
        _gridData.update { currentGrid ->
            val activatedNode = currentGrid.nodes.find { it.id == activatedNodeId && it.activated }
            if (activatedNode == null) return@update currentGrid

            // Find connected nodes that should be unlocked
            val connectedNodeIds = currentGrid.connections
                .filter { it.from == activatedNodeId || it.to == activatedNodeId }
                .map { if (it.from == activatedNodeId) it.to else it.from }

            val updatedNodes = currentGrid.nodes.map { node ->
                if (node.id in connectedNodeIds && !node.isUnlocked) {
                    node.copy(
                        isUnlocked = true,
                        connectedPaths = node.connectedPaths + activatedNodeId
                    )
                } else {
                    node
                }
            }
            currentGrid.copy(nodes = updatedNodes)
        }
    }

    /**
     * Reset the grid to initial state
     */
    fun resetGrid() {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedNode.value = null
            _animatingNodes.value = emptySet()
            _dataFlows.value = emptyMap()

            kotlinx.coroutines.delay(500) // Simulate loading
            _gridData.value = generateInitialGrid()
            _isLoading.value = false
        }
    }

    /**
     * Simulate real-time data flows
     */
    private fun startDataFlowSimulation() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(500)

                val currentGrid = _gridData.value
                val newFlows = mutableMapOf<String, Long>()

                // Only active connections can have data flows
                currentGrid.connections
                    .filter { it.active }
                    .forEach { connection ->
                        if (kotlin.random.Random.nextFloat() > 0.85f) {
                            val flowKey = "${connection.from}-${connection.to}"
                            newFlows[flowKey] = System.currentTimeMillis()
                        }
                    }

                _dataFlows.value += newFlows
            }
        }
    }

    /**
     * Clean up old data flows
     */
    private fun startFlowCleanup() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(2000)

                val currentTime = System.currentTimeMillis()
                val filteredFlows = _dataFlows.value.filterValues { timestamp ->
                    currentTime - timestamp < 2000
                }

                _dataFlows.value = filteredFlows
            }
        }
    }

    /**
     * Generate initial grid data
     */
    private fun generateInitialGrid(): GridData {
        val config = SphereGridConfig()
        return generateSphereGridData(config)
    }

    /**
     * Export grid state for debugging
     */
    fun exportGridState(): String {
        val grid = _gridData.value
        return buildString {
            appendLine("=== DataVein Sphere Grid State ===")
            appendLine("Total Nodes: ${grid.nodes.size}")
            appendLine("Active Nodes: ${grid.nodes.count { it.activated }}")
            appendLine("Unlocked Nodes: ${grid.nodes.count { it.isUnlocked }}")
            appendLine("Total Connections: ${grid.connections.size}")
            appendLine("Active Connections: ${grid.connections.count { it.active }}")
            appendLine("Current Data Flows: ${_dataFlows.value.size}")
            appendLine()

            appendLine("=== Node Details ===")
            grid.nodes.forEachIndexed { index, node ->
                appendLine("$index. ${node.tag} (${node.type.displayName})")
                appendLine("   Position: (${node.x.toInt()}, ${node.y.toInt()})")
                appendLine("   Status: ${if (node.activated) "Active" else "Inactive"}")
                appendLine("   Unlocked: ${node.isUnlocked}")
                appendLine("   XP: ${node.xp}/1000")
                appendLine()
            }
        }
    }
}

/**
 * Generate sphere grid data with enhanced progression system
 */
private fun generateSphereGridData(config: SphereGridConfig): GridData {
    val nodes = mutableListOf<DataVeinNode>()
    val connections = mutableListOf<NodeConnection>()

    // Generate nodes in spiral pattern
    for (ring in 0 until config.rings) {
        val nodesInRing = 6 + (ring * config.nodesPerRingMultiplier)
        val ringRadius = config.baseRadius * (0.3f + ring * 0.23f)

        repeat(nodesInRing) { i ->
            val angle =
                (i.toFloat() / nodesInRing) * 2 * kotlin.math.PI + (ring * config.spiralOffset)
            val x = config.centerX + kotlin.math.cos(angle).toFloat() * ringRadius
            val y = config.centerY + kotlin.math.sin(angle).toFloat() * ringRadius

            val nodeId = "node_${ring}_$i"
            val type = NodeType.entries.toTypedArray().random()
            val tag = generateNodeTag(type, ring, i)

            // Core nodes (ring 0) start unlocked and some activated
            val isUnlocked = ring == 0 || kotlin.random.Random.nextFloat() > 0.8f
            val isActivated = isUnlocked && kotlin.random.Random.nextFloat() > 0.6f

            nodes.add(
                DataVeinNode(
                    id = nodeId,
                    x = x,
                    y = y,
                    type = type,
                    ring = ring,
                    index = i,
                    activated = isActivated,
                    level = kotlin.random.Random.nextInt(1, 6),
                    data = "Data_${kotlin.random.Random.nextInt(1000, 9999)}",
                    tag = tag,
                    xp = if (isActivated) kotlin.random.Random.nextInt(200, 800) else 0,
                    isUnlocked = isUnlocked
                )
            )
        }
    }

    // Generate connections
    nodes.forEachIndexed { i, node ->
        nodes.forEachIndexed { j, otherNode ->
            if (i != j) {
                val distance = kotlin.math.sqrt(
                    (node.x - otherNode.x) * (node.x - otherNode.x) +
                            (node.y - otherNode.y) * (node.y - otherNode.y)
                )

                if (distance < config.connectionDistance) {
                    val strength =
                        (config.connectionDistance - distance) / config.connectionDistance
                    connections.add(
                        NodeConnection(
                            from = node.id,
                            to = otherNode.id,
                            active = node.isUnlocked && otherNode.isUnlocked && kotlin.random.Random.nextFloat() > 0.5f,
                            strength = strength
                        )
                    )
                }
            }
        }
    }

    return GridData(nodes, connections)
}

/**
 * Generate meaningful node tags
 */
private fun generateNodeTag(type: NodeType, ring: Int, index: Int): String {
    val ringNames = listOf("CORE", "INNER", "MID", "OUTER")
    val ringName = ringNames.getOrElse(ring) { "R$ring" }

    return when (type) {
        NodeType.GENESIS -> "GEN-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.ORACLE -> "ORC-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.AURA -> "AUR-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.KAI -> "KAI-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.NEXUS -> "NEX-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.MEMORY -> "MEM-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.AGENT -> "AGT-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.DATA -> "DAT-$ringName-${index.toString().padStart(2, '0')}"
        NodeType.SECURE -> "SEC-$ringName-${index.toString().padStart(2, '0')}"
    }
}
