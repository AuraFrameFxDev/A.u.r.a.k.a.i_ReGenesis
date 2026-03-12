package dev.aurakai.auraframefx.datavein.model

import androidx.compose.ui.graphics.Color

/**
 * DataVein Node - Individual data processing unit in the sphere grid
 * Enhanced with tags for node identification and FFX-style progression
 */
data class DataVeinNode(
    val id: String,
    val x: Float,
    val y: Float,
    val type: NodeType,
    val ring: Int,
    val index: Int,
    val activated: Boolean = false,
    val level: Int = 1,
    val data: String = "",
    val tag: String = "", // Added for node identification as requested
    val xp: Int = 0, // FFX-style experience points
    val isUnlocked: Boolean = false, // FFX-style progression
    val connectedPaths: List<String> = emptyList() // Connected node IDs
)

/**
 * Connection between two nodes in the DataVein sphere grid
 */
data class NodeConnection(
    val from: String,
    val to: String,
    val active: Boolean = false,
    val dataFlow: Boolean = false,
    val flowDirection: FlowDirection = FlowDirection.BIDIRECTIONAL,
    val strength: Float = 1.0f // Connection strength for visual effects
)

/**
 * Complete grid data structure containing nodes and connections
 */
data class GridData(
    val nodes: List<DataVeinNode>,
    val connections: List<NodeConnection>
)

/**
 * Node types matching the Genesis AI system with enhanced properties
 */
enum class NodeType(
    val displayName: String,
    val color: Color,
    val glowColor: Color,
    val size: Float,
    val category: NodeCategory,
    val description: String
) {
    MEMORY(
        "Memory",
        Color(0xFF00FF88),
        Color(0xFF00FF88),
        12f,
        NodeCategory.STORAGE,
        "Data Storage & Retrieval"
    ),
    AGENT(
        "Agent",
        Color(0xFFFF6B35),
        Color(0xFFFF6B35),
        16f,
        NodeCategory.PROCESSING,
        "AI Agent Processing"
    ),
    DATA(
        "Data",
        Color(0xFF4FC3F7),
        Color(0xFF4FC3F7),
        10f,
        NodeCategory.FLOW,
        "Data Flow Controller"
    ),
    NEXUS(
        "Nexus",
        Color(0xFFE91E63),
        Color(0xFFE91E63),
        20f,
        NodeCategory.CORE,
        "Core System Nexus"
    ),
    ORACLE(
        "Oracle",
        Color(0xFF9C27B0),
        Color(0xFF9C27B0),
        18f,
        NodeCategory.WISDOM,
        "Oracle Consciousness"
    ),
    SECURE(
        "Secure",
        Color(0xFFFFD700),
        Color(0xFFFFD700),
        14f,
        NodeCategory.SECURITY,
        "Security & Encryption"
    ),
    GENESIS(
        "Genesis",
        Color(0xFFFF4081),
        Color(0xFFFF4081),
        22f,
        NodeCategory.CORE,
        "Genesis Core Node"
    ),
    AURA(
        "Aura",
        Color(0xFF00E5FF),
        Color(0xFF00E5FF),
        16f,
        NodeCategory.CREATIVE,
        "Aura Creative Node"
    ),
    KAI(
        "Kai",
        Color(0xFF76FF03),
        Color(0xFF76FF03),
        14f,
        NodeCategory.ANALYTICAL,
        "Kai Analysis Node"
    )
}

/**
 * Node categories for organization and filtering
 */
enum class NodeCategory {
    CORE, PROCESSING, STORAGE, FLOW, WISDOM, SECURITY, CREATIVE, ANALYTICAL
}

/**
 * Data flow direction through connections
 */
enum class FlowDirection {
    UNIDIRECTIONAL, BIDIRECTIONAL, PULSING
}

/**
 * Animation states for nodes and connections
 */
enum class AnimationState {
    IDLE, PULSING, ACTIVATING, FLOWING, SELECTED
}

/**
 * Sphere grid configuration for FFX-style layout
 */
data class SphereGridConfig(
    val centerX: Float = 400f,
    val centerY: Float = 300f,
    val baseRadius: Float = 250f,
    val rings: Int = 4,
    val nodesPerRingMultiplier: Int = 4, // Additional nodes per ring
    val connectionDistance: Float = 80f, // Max distance for auto-connections
    val spiralOffset: Float = 0.2f // Spiral effect like FFX
)

/**
 * Node interaction result
 */
data class NodeInteraction(
    val selectedNode: DataVeinNode,
    val connectedNodes: List<DataVeinNode>,
    val availablePaths: List<NodeConnection>,
    val unlockableNodes: List<DataVeinNode>
)