package dev.aurakai.auraframefx.ui.components.graph

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.Serializable

/**
 * Represents a node in an interactive graph
 */
@Serializable
data class GraphNode(
    val id: String,
    val name: String,
    val type: NodeType,
    val position: GraphOffset,
    val connections: List<Connection> = emptyList()
)

/**
 * Offset position for graph nodes
 */
@Serializable
data class GraphOffset(
    val x: Float,
    val y: Float
) {
    fun toOffset(): Offset = Offset(x, y)
}

/**
 * Connection between graph nodes
 */
@Serializable
data class Connection(
    val targetId: String,
    val type: ConnectionType
)

/**
 * Type of graph node
 */
enum class NodeType {
    AGENT,
    SERVICE,
    DATA,
    PROCESS
}

/**
 * Type of connection between nodes
 */
enum class ConnectionType {
    DATA_FLOW,
    CONTROL_FLOW,
    DEPENDENCY
}
