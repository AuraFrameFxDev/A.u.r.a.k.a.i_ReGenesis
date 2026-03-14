package dev.aurakai.auraframefx.ui.debug.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Immutable
data class GraphNode(
    val id: String,
    val name: String,
    val type: NodeType,
    val position: Offset = Offset(0f, 0f),
    var state: Any? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val connections: List<Connection> = emptyList(),
) {
    /**
     * Returns a copy of this node with the specified state and an updated timestamp.
     *
     * @param newState The new state to associate with the node.
     * @return A new GraphNode instance with the updated state and refreshed lastUpdated value.
     */
    fun withUpdatedState(newState: Any?): GraphNode {
        return copy(state = newState, lastUpdated = System.currentTimeMillis())
    }

    /**
     * Returns a copy of this node with its position updated to the specified coordinates.
     *
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     * @return A new GraphNode instance with the updated position.
     */
    fun withPosition(x: Float, y: Float): GraphNode {
        return copy(position = Offset(x, y))
    }
}

@Immutable
data class Offset(val x: Float, val y: Float) {
    /**
     * Returns a new Offset representing the sum of this offset and another.
     *
     * Adds the x and y components of the two offsets.
     *
     * @param other The offset to add.
     * @return The resulting offset after addition.
     */
    operator fun plus(other: Offset): Offset = Offset(x + other.x, y + other.y)

    /**
     * Returns the vector difference between this offset and another offset.
     *
     * @return A new Offset representing the result of subtracting the given offset from this offset.
     */
    operator fun minus(other: Offset): Offset = Offset(x - other.x, y - other.y)

    /**
     * Returns a new Offset scaled by the given factor.
     *
     * Multiplies both the x and y coordinates by the specified scalar value.
     *
     * @param factor The scalar value to multiply the coordinates by.
     * @return A new Offset with scaled coordinates.
     */
    operator fun times(factor: Float): Offset = Offset(x * factor, y * factor)

    /**
     * Calculates the Euclidean distance between this offset and another offset.
     *
     * @param other The offset to which the distance is measured.
     * @return The straight-line distance between the two offsets.
     */
    fun distanceTo(other: Offset): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }
}

@Immutable
data class Connection(
    val targetId: String,
    val type: ConnectionType = ConnectionType.DIRECT,
    val label: String = "",
)

enum class NodeType(
    val color: Color,
    val icon: ImageVector,
    val defaultSize: Dp = 48.dp,
) {
    VISION(
        color = Color(0xFF03DAC6),
        icon = Icons.Default.Visibility,
        defaultSize = 56.dp
    ),
    PROCESSING(
        color = Color(0xFFBB86FC),
        icon = Icons.Default.Settings,
        defaultSize = 56.dp
    ),
    AGENT(
        color = Color(0xFFCF6679),
        icon = Icons.Default.Person,
        defaultSize = 64.dp
    ),
    DATA(
        color = Color(0xFF018786),
        icon = Icons.Default.Storage,
        defaultSize = 56.dp
    )
}

enum class ConnectionType {
    DIRECT,
    BIDIRECTIONAL,
    DASHED
}
