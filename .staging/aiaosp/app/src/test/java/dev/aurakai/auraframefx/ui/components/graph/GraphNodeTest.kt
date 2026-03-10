package dev.aurakai.auraframefx.ui.components.graph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.debug.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.math.sqrt

@DisplayName("GraphNode Tests")
class GraphNodeTest {

    private lateinit var defaultNode: GraphNode
    private val testId = "test-node-1"
    private val testName = "Test Node"
    private val testPosition = Offset(10f, 20f)
    private val testState = "test-state"

    @BeforeEach
    fun setUp() {
        defaultNode = GraphNode(
            id = testId,
            name = testName,
            type = NodeType.PROCESSING,
            position = testPosition,
            state = testState
        )
    }

    @Nested
    @DisplayName("GraphNode Construction")
    inner class GraphNodeConstruction {

        @Test
        @DisplayName("should create node with all required parameters")
        fun shouldCreateNodeWithRequiredParameters() {
            val node = GraphNode(
                id = "test-id",
                name = "test-name",
                type = NodeType.AGENT
            )

            assertEquals("test-id", node.id)
            assertEquals("test-name", node.name)
            assertEquals(NodeType.AGENT, node.type)
            assertEquals(Offset(0f, 0f), node.position)
            assertNull(node.state)
            assertTrue(node.lastUpdated > 0)
            assertTrue(node.connections.isEmpty())
        }

        @Test
        @DisplayName("should create node with all parameters")
        fun shouldCreateNodeWithAllParameters() {
            val connections = listOf(
                Connection("target1", ConnectionType.DIRECT, "label1"),
                Connection("target2", ConnectionType.BIDIRECTIONAL)
            )
            val customState = mapOf("key" to "value")
            val customPosition = Offset(100f, 200f)

            val node = GraphNode(
                id = "full-node",
                name = "Full Node",
                type = NodeType.VISION,
                position = customPosition,
                state = customState,
                connections = connections
            )

            assertEquals("full-node", node.id)
            assertEquals("Full Node", node.name)
            assertEquals(NodeType.VISION, node.type)
            assertEquals(customPosition, node.position)
            assertEquals(customState, node.state)
            assertEquals(connections, node.connections)
        }

        @Test
        @DisplayName("should handle empty strings for id and name")
        fun shouldHandleEmptyStrings() {
            val node = GraphNode(
                id = "",
                name = "",
                type = NodeType.DATA
            )

            assertEquals("", node.id)
            assertEquals("", node.name)
            assertEquals(NodeType.DATA, node.type)
        }

        @Test
        @DisplayName("should handle null state")
        fun shouldHandleNullState() {
            val node = GraphNode(
                id = "null-state-node",
                name = "Null State Node",
                type = NodeType.PROCESSING,
                state = null
            )

            assertNull(node.state)
        }

        @Test
        @DisplayName("should set timestamp on construction")
        fun shouldSetTimestampOnConstruction() {
            val beforeCreation = System.currentTimeMillis()
            val node = GraphNode(
                id = "timestamp-test",
                name = "Timestamp Test",
                type = NodeType.VISION
            )
            val afterCreation = System.currentTimeMillis()

            assertTrue(node.lastUpdated >= beforeCreation)
            assertTrue(node.lastUpdated <= afterCreation)
        }

        @Test
        @DisplayName("should handle special characters in id and name")
        fun shouldHandleSpecialCharactersInIdAndName() {
            val specialId = "test-node_123!@#$%"
            val specialName = "Test Node (with special chars) & symbols"

            val node = GraphNode(
                id = specialId,
                name = specialName,
                type = NodeType.AGENT
            )

            assertEquals(specialId, node.id)
            assertEquals(specialName, node.name)
        }

        @Test
        @DisplayName("should handle unicode characters in id and name")
        fun shouldHandleUnicodeCharactersInIdAndName() {
            val unicodeId = "节点-🔥-test"
            val unicodeName = "测试节点 🚀 Test Node"

            val node = GraphNode(
                id = unicodeId,
                name = unicodeName,
                type = NodeType.DATA
            )

            assertEquals(unicodeId, node.id)
            assertEquals(unicodeName, node.name)
        }
    }

    @Nested
    @DisplayName("withUpdatedState Method")
    inner class WithUpdatedStateMethod {

        @Test
        @DisplayName("should update state and timestamp")
        fun shouldUpdateStateAndTimestamp() {
            val originalTimestamp = defaultNode.lastUpdated
            Thread.sleep(1) // Ensure time difference

            val newState = "new-state"
            val updatedNode = defaultNode.withUpdatedState(newState)

            assertEquals(newState, updatedNode.state)
            assertTrue(updatedNode.lastUpdated > originalTimestamp)
            assertEquals(defaultNode.id, updatedNode.id)
            assertEquals(defaultNode.name, updatedNode.name)
            assertEquals(defaultNode.type, updatedNode.type)
            assertEquals(defaultNode.position, updatedNode.position)
            assertEquals(defaultNode.connections, updatedNode.connections)
        }

        @Test
        @DisplayName("should handle null state update")
        fun shouldHandleNullStateUpdate() {
            val updatedNode = defaultNode.withUpdatedState(null)

            assertNull(updatedNode.state)
            assertNotEquals(defaultNode.lastUpdated, updatedNode.lastUpdated)
        }

        @Test
        @DisplayName("should handle complex object state")
        fun shouldHandleComplexObjectState() {
            val complexState = mapOf(
                "users" to listOf("user1", "user2"),
                "config" to mapOf("enabled" to true, "timeout" to 5000)
            )

            val updatedNode = defaultNode.withUpdatedState(complexState)

            assertEquals(complexState, updatedNode.state)
        }

        @Test
        @DisplayName("should create new instance and not modify original")
        fun shouldCreateNewInstanceNotModifyOriginal() {
            val originalState = defaultNode.state
            val originalTimestamp = defaultNode.lastUpdated

            val updatedNode = defaultNode.withUpdatedState("modified-state")

            assertEquals(originalState, defaultNode.state)
            assertEquals(originalTimestamp, defaultNode.lastUpdated)
            assertNotSame(defaultNode, updatedNode)
        }

        @Test
        @DisplayName("should handle empty string state")
        fun shouldHandleEmptyStringState() {
            val updatedNode = defaultNode.withUpdatedState("")

            assertEquals("", updatedNode.state)
        }

        @Test
        @DisplayName("should handle numeric state")
        fun shouldHandleNumericState() {
            val numericState = 42
            val updatedNode = defaultNode.withUpdatedState(numericState)

            assertEquals(numericState, updatedNode.state)
        }

        @Test
        @DisplayName("should handle boolean state")
        fun shouldHandleBooleanState() {
            val booleanState = true
            val updatedNode = defaultNode.withUpdatedState(booleanState)

            assertEquals(booleanState, updatedNode.state)
        }

        @Test
        @DisplayName("should handle list state")
        fun shouldHandleListState() {
            val listState = listOf("item1", "item2", "item3")
            val updatedNode = defaultNode.withUpdatedState(listState)

            assertEquals(listState, updatedNode.state)
        }
    }

    @Nested
    @DisplayName("withPosition Method")
    inner class WithPositionMethod {

        @Test
        @DisplayName("should update position coordinates")
        fun shouldUpdatePositionCoordinates() {
            val newX = 100f
            val newY = 200f

            val updatedNode = defaultNode.withPosition(newX, newY)

            assertEquals(Offset(newX, newY), updatedNode.position)
            assertEquals(defaultNode.id, updatedNode.id)
            assertEquals(defaultNode.name, updatedNode.name)
            assertEquals(defaultNode.type, updatedNode.type)
            assertEquals(defaultNode.state, updatedNode.state)
            assertEquals(defaultNode.lastUpdated, updatedNode.lastUpdated)
            assertEquals(defaultNode.connections, updatedNode.connections)
        }

        @Test
        @DisplayName("should handle negative coordinates")
        fun shouldHandleNegativeCoordinates() {
            val updatedNode = defaultNode.withPosition(-50f, -75f)

            assertEquals(Offset(-50f, -75f), updatedNode.position)
        }

        @Test
        @DisplayName("should handle zero coordinates")
        fun shouldHandleZeroCoordinates() {
            val updatedNode = defaultNode.withPosition(0f, 0f)

            assertEquals(Offset(0f, 0f), updatedNode.position)
        }

        @Test
        @DisplayName("should handle floating point precision")
        fun shouldHandleFloatingPointPrecision() {
            val x = 3.14159f
            val y = 2.71828f

            val updatedNode = defaultNode.withPosition(x, y)

            assertEquals(x, updatedNode.position.x, 0.00001f)
            assertEquals(y, updatedNode.position.y, 0.00001f)
        }

        @Test
        @DisplayName("should create new instance and not modify original")
        fun shouldCreateNewInstanceNotModifyOriginal() {
            val originalPosition = defaultNode.position

            val updatedNode = defaultNode.withPosition(500f, 600f)

            assertEquals(originalPosition, defaultNode.position)
            assertNotSame(defaultNode, updatedNode)
        }

        @Test
        @DisplayName("should handle very large coordinates")
        fun shouldHandleVeryLargeCoordinates() {
            val largeX = Float.MAX_VALUE
            val largeY = Float.MAX_VALUE

            val updatedNode = defaultNode.withPosition(largeX, largeY)

            assertEquals(largeX, updatedNode.position.x)
            assertEquals(largeY, updatedNode.position.y)
        }

        @Test
        @DisplayName("should handle very small coordinates")
        fun shouldHandleVerySmallCoordinates() {
            val smallX = Float.MIN_VALUE
            val smallY = Float.MIN_VALUE

            val updatedNode = defaultNode.withPosition(smallX, smallY)

            assertEquals(smallX, updatedNode.position.x)
            assertEquals(smallY, updatedNode.position.y)
        }
    }

    @Nested
    @DisplayName("Data Class Behavior")
    inner class DataClassBehavior {

        @Test
        @DisplayName("should implement equals correctly")
        fun shouldImplementEqualsCorrectly() {
            val node1 = GraphNode(
                id = "same-id",
                name = "Same Node",
                type = NodeType.AGENT,
                position = Offset(10f, 20f),
                state = "same-state"
            )

            val node2 = GraphNode(
                id = "same-id",
                name = "Same Node",
                type = NodeType.AGENT,
                position = Offset(10f, 20f),
                state = "same-state"
            )

            assertEquals(node1, node2)
        }

        @Test
        @DisplayName("should implement hashCode correctly")
        fun shouldImplementHashCodeCorrectly() {
            val node1 = GraphNode(
                id = "same-id",
                name = "Same Node",
                type = NodeType.AGENT
            )

            val node2 = GraphNode(
                id = "same-id",
                name = "Same Node",
                type = NodeType.AGENT
            )

            assertEquals(node1.hashCode(), node2.hashCode())
        }

        @Test
        @DisplayName("should implement toString correctly")
        fun shouldImplementToStringCorrectly() {
            val node = GraphNode(
                id = "test-id",
                name = "Test Node",
                type = NodeType.VISION
            )

            val toString = node.toString()

            assertTrue(toString.contains("test-id"))
            assertTrue(toString.contains("Test Node"))
            assertTrue(toString.contains("VISION"))
        }

        @Test
        @DisplayName("should support copy with modifications")
        fun shouldSupportCopyWithModifications() {
            val originalNode = defaultNode
            val copiedNode = originalNode.copy(name = "Modified Name")

            assertEquals(originalNode.id, copiedNode.id)
            assertEquals("Modified Name", copiedNode.name)
            assertEquals(originalNode.type, copiedNode.type)
            assertEquals(originalNode.position, copiedNode.position)
            assertEquals(originalNode.state, copiedNode.state)
        }
    }
}

@DisplayName("Offset Tests")
class OffsetTest {

    @Nested
    @DisplayName("Offset Construction")
    inner class OffsetConstruction {

        @Test
        @DisplayName("should create offset with positive coordinates")
        fun shouldCreateOffsetWithPositiveCoordinates() {
            val offset = Offset(10f, 20f)

            assertEquals(10f, offset.x)
            assertEquals(20f, offset.y)
        }

        @Test
        @DisplayName("should create offset with negative coordinates")
        fun shouldCreateOffsetWithNegativeCoordinates() {
            val offset = Offset(-15f, -25f)

            assertEquals(-15f, offset.x)
            assertEquals(-25f, offset.y)
        }

        @Test
        @DisplayName("should create offset with zero coordinates")
        fun shouldCreateOffsetWithZeroCoordinates() {
            val offset = Offset(0f, 0f)

            assertEquals(0f, offset.x)
            assertEquals(0f, offset.y)
        }

        @Test
        @DisplayName("should create offset with mixed positive and negative coordinates")
        fun shouldCreateOffsetWithMixedCoordinates() {
            val offset = Offset(-10f, 20f)

            assertEquals(-10f, offset.x)
            assertEquals(20f, offset.y)
        }

        @Test
        @DisplayName("should handle very small decimal values")
        fun shouldHandleVerySmallDecimalValues() {
            val offset = Offset(0.001f, -0.001f)

            assertEquals(0.001f, offset.x, 0.0001f)
            assertEquals(-0.001f, offset.y, 0.0001f)
        }
    }

    @Nested
    @DisplayName("Plus Operator")
    inner class PlusOperator {

        @Test
        @DisplayName("should add two positive offsets")
        fun shouldAddTwoPositiveOffsets() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(5f, 15f)

            val result = offset1 + offset2

            assertEquals(15f, result.x)
            assertEquals(35f, result.y)
        }

        @Test
        @DisplayName("should add positive and negative offsets")
        fun shouldAddPositiveAndNegativeOffsets() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(-5f, -15f)

            val result = offset1 + offset2

            assertEquals(5f, result.x)
            assertEquals(5f, result.y)
        }

        @Test
        @DisplayName("should add with zero offset")
        fun shouldAddWithZeroOffset() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(0f, 0f)

            val result = offset1 + offset2

            assertEquals(offset1.x, result.x)
            assertEquals(offset1.y, result.y)
        }

        @Test
        @DisplayName("should be commutative")
        fun shouldBeCommutative() {
            val offset1 = Offset(7f, 13f)
            val offset2 = Offset(3f, 11f)

            val result1 = offset1 + offset2
            val result2 = offset2 + offset1

            assertEquals(result1.x, result2.x)
            assertEquals(result1.y, result2.y)
        }

        @Test
        @DisplayName("should not modify original offsets")
        fun shouldNotModifyOriginalOffsets() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(5f, 15f)
            val originalX1 = offset1.x
            val originalY1 = offset1.y
            val originalX2 = offset2.x
            val originalY2 = offset2.y

            offset1 + offset2

            assertEquals(originalX1, offset1.x)
            assertEquals(originalY1, offset1.y)
            assertEquals(originalX2, offset2.x)
            assertEquals(originalY2, offset2.y)
        }

        @Test
        @DisplayName("should handle decimal precision in addition")
        fun shouldHandleDecimalPrecisionInAddition() {
            val offset1 = Offset(3.14f, 2.71f)
            val offset2 = Offset(1.41f, 1.73f)

            val result = offset1 + offset2

            assertEquals(4.55f, result.x, 0.01f)
            assertEquals(4.44f, result.y, 0.01f)
        }
    }

    @Nested
    @DisplayName("Minus Operator")
    inner class MinusOperator {

        @Test
        @DisplayName("should subtract two positive offsets")
        fun shouldSubtractTwoPositiveOffsets() {
            val offset1 = Offset(20f, 30f)
            val offset2 = Offset(5f, 10f)

            val result = offset1 - offset2

            assertEquals(15f, result.x)
            assertEquals(20f, result.y)
        }

        @Test
        @DisplayName("should subtract negative offset")
        fun shouldSubtractNegativeOffset() {
            val offset1 = Offset(10f, 15f)
            val offset2 = Offset(-5f, -10f)

            val result = offset1 - offset2

            assertEquals(15f, result.x)
            assertEquals(25f, result.y)
        }

        @Test
        @DisplayName("should subtract from zero offset")
        fun shouldSubtractFromZeroOffset() {
            val offset1 = Offset(0f, 0f)
            val offset2 = Offset(5f, 10f)

            val result = offset1 - offset2

            assertEquals(-5f, result.x)
            assertEquals(-10f, result.y)
        }

        @Test
        @DisplayName("should result in zero when subtracting same offset")
        fun shouldResultInZeroWhenSubtractingSameOffset() {
            val offset = Offset(15f, 25f)

            val result = offset - offset

            assertEquals(0f, result.x)
            assertEquals(0f, result.y)
        }

        @Test
        @DisplayName("should not be commutative")
        fun shouldNotBeCommutative() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(5f, 15f)

            val result1 = offset1 - offset2
            val result2 = offset2 - offset1

            assertEquals(-result1.x, result2.x)
            assertEquals(-result1.y, result2.y)
        }

        @Test
        @DisplayName("should handle decimal precision in subtraction")
        fun shouldHandleDecimalPrecisionInSubtraction() {
            val offset1 = Offset(10.5f, 20.8f)
            val offset2 = Offset(3.2f, 5.3f)

            val result = offset1 - offset2

            assertEquals(7.3f, result.x, 0.01f)
            assertEquals(15.5f, result.y, 0.01f)
        }
    }

    @Nested
    @DisplayName("Times Operator")
    inner class TimesOperator {

        @Test
        @DisplayName("should multiply by positive factor")
        fun shouldMultiplyByPositiveFactor() {
            val offset = Offset(10f, 20f)
            val factor = 2.5f

            val result = offset * factor

            assertEquals(25f, result.x)
            assertEquals(50f, result.y)
        }

        @Test
        @DisplayName("should multiply by negative factor")
        fun shouldMultiplyByNegativeFactor() {
            val offset = Offset(10f, 20f)
            val factor = -1.5f

            val result = offset * factor

            assertEquals(-15f, result.x)
            assertEquals(-30f, result.y)
        }

        @Test
        @DisplayName("should multiply by zero")
        fun shouldMultiplyByZero() {
            val offset = Offset(10f, 20f)
            val factor = 0f

            val result = offset * factor

            assertEquals(0f, result.x)
            assertEquals(0f, result.y)
        }

        @Test
        @DisplayName("should multiply by one")
        fun shouldMultiplyByOne() {
            val offset = Offset(10f, 20f)
            val factor = 1f

            val result = offset * factor

            assertEquals(offset.x, result.x)
            assertEquals(offset.y, result.y)
        }

        @Test
        @DisplayName("should handle fractional factors")
        fun shouldHandleFractionalFactors() {
            val offset = Offset(10f, 20f)
            val factor = 0.5f

            val result = offset * factor

            assertEquals(5f, result.x)
            assertEquals(10f, result.y)
        }

        @Test
        @DisplayName("should handle very small factors")
        fun shouldHandleVerySmallFactors() {
            val offset = Offset(1000f, 2000f)
            val factor = 0.001f

            val result = offset * factor

            assertEquals(1f, result.x)
            assertEquals(2f, result.y)
        }

        @Test
        @DisplayName("should handle negative coordinates with positive factor")
        fun shouldHandleNegativeCoordinatesWithPositiveFactor() {
            val offset = Offset(-10f, -20f)
            val factor = 2f

            val result = offset * factor

            assertEquals(-20f, result.x)
            assertEquals(-40f, result.y)
        }
    }

    @Nested
    @DisplayName("Distance Calculation")
    inner class DistanceCalculation {

        @Test
        @DisplayName("should calculate distance between two points")
        fun shouldCalculateDistanceBetweenTwoPoints() {
            val offset1 = Offset(0f, 0f)
            val offset2 = Offset(3f, 4f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(5f, distance, 0.001f)
        }

        @Test
        @DisplayName("should calculate distance to same point as zero")
        fun shouldCalculateDistanceToSamePointAsZero() {
            val offset = Offset(10f, 20f)

            val distance = offset.distanceTo(offset)

            assertEquals(0f, distance, 0.001f)
        }

        @Test
        @DisplayName("should be commutative")
        fun shouldBeCommutative() {
            val offset1 = Offset(1f, 2f)
            val offset2 = Offset(4f, 6f)

            val distance1 = offset1.distanceTo(offset2)
            val distance2 = offset2.distanceTo(offset1)

            assertEquals(distance1, distance2, 0.001f)
        }

        @Test
        @DisplayName("should calculate distance with negative coordinates")
        fun shouldCalculateDistanceWithNegativeCoordinates() {
            val offset1 = Offset(-3f, -4f)
            val offset2 = Offset(0f, 0f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(5f, distance, 0.001f)
        }

        @Test
        @DisplayName("should calculate diagonal distance correctly")
        fun shouldCalculateDiagonalDistanceCorrectly() {
            val offset1 = Offset(0f, 0f)
            val offset2 = Offset(1f, 1f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(sqrt(2f), distance, 0.001f)
        }

        @Test
        @DisplayName("should handle large coordinate values")
        fun shouldHandleLargeCoordinateValues() {
            val offset1 = Offset(1000f, 2000f)
            val offset2 = Offset(1300f, 2400f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(500f, distance, 0.001f)
        }

        @Test
        @DisplayName("should calculate horizontal distance")
        fun shouldCalculateHorizontalDistance() {
            val offset1 = Offset(0f, 5f)
            val offset2 = Offset(12f, 5f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(12f, distance, 0.001f)
        }

        @Test
        @DisplayName("should calculate vertical distance")
        fun shouldCalculateVerticalDistance() {
            val offset1 = Offset(5f, 0f)
            val offset2 = Offset(5f, 9f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(9f, distance, 0.001f)
        }

        @Test
        @DisplayName("should handle mixed positive and negative coordinates")
        fun shouldHandleMixedPositiveAndNegativeCoordinates() {
            val offset1 = Offset(-2f, 3f)
            val offset2 = Offset(4f, -1f)

            val distance = offset1.distanceTo(offset2)

            assertEquals(sqrt(52f), distance, 0.001f)
        }
    }

    @Nested
    @DisplayName("Data Class Behavior")
    inner class DataClassBehavior {

        @Test
        @DisplayName("should implement equals correctly")
        fun shouldImplementEqualsCorrectly() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(10f, 20f)

            assertEquals(offset1, offset2)
        }

        @Test
        @DisplayName("should implement hashCode correctly")
        fun shouldImplementHashCodeCorrectly() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(10f, 20f)

            assertEquals(offset1.hashCode(), offset2.hashCode())
        }

        @Test
        @DisplayName("should not be equal for different coordinates")
        fun shouldNotBeEqualForDifferentCoordinates() {
            val offset1 = Offset(10f, 20f)
            val offset2 = Offset(10f, 21f)

            assertNotEquals(offset1, offset2)
        }

        @Test
        @DisplayName("should support copy with modifications")
        fun shouldSupportCopyWithModifications() {
            val original = Offset(10f, 20f)
            val copied = original.copy(x = 15f)

            assertEquals(15f, copied.x)
            assertEquals(20f, copied.y)
            assertEquals(10f, original.x) // Original unchanged
        }
    }
}

@DisplayName("Connection Tests")
class ConnectionTest {

    @Test
    @DisplayName("should create connection with required parameters")
    fun shouldCreateConnectionWithRequiredParameters() {
        val connection = Connection("target-123")

        assertEquals("target-123", connection.targetId)
        assertEquals(ConnectionType.DIRECT, connection.type)
        assertEquals("", connection.label)
    }

    @Test
    @DisplayName("should create connection with all parameters")
    fun shouldCreateConnectionWithAllParameters() {
        val connection = Connection(
            targetId = "target-456",
            type = ConnectionType.BIDIRECTIONAL,
            label = "Test Connection"
        )

        assertEquals("target-456", connection.targetId)
        assertEquals(ConnectionType.BIDIRECTIONAL, connection.type)
        assertEquals("Test Connection", connection.label)
    }

    @Test
    @DisplayName("should handle empty target id")
    fun shouldHandleEmptyTargetId() {
        val connection = Connection("")

        assertEquals("", connection.targetId)
    }

    @Test
    @DisplayName("should handle empty label")
    fun shouldHandleEmptyLabel() {
        val connection = Connection("target", label = "")

        assertEquals("", connection.label)
    }

    @Test
    @DisplayName("should handle all connection types")
    fun shouldHandleAllConnectionTypes() {
        val directConnection = Connection("target1", ConnectionType.DIRECT)
        val bidirectionalConnection = Connection("target2", ConnectionType.BIDIRECTIONAL)
        val dashedConnection = Connection("target3", ConnectionType.DASHED)

        assertEquals(ConnectionType.DIRECT, directConnection.type)
        assertEquals(ConnectionType.BIDIRECTIONAL, bidirectionalConnection.type)
        assertEquals(ConnectionType.DASHED, dashedConnection.type)
    }

    @Test
    @DisplayName("should handle special characters in target id")
    fun shouldHandleSpecialCharactersInTargetId() {
        val specialTargetId = "target_123!@#$%^&*()"
        val connection = Connection(specialTargetId)

        assertEquals(specialTargetId, connection.targetId)
    }

    @Test
    @DisplayName("should handle unicode characters in label")
    fun shouldHandleUnicodeCharactersInLabel() {
        val unicodeLabel = "连接标签 🔗 Connection"
        val connection = Connection("target", label = unicodeLabel)

        assertEquals(unicodeLabel, connection.label)
    }

    @Test
    @DisplayName("should handle long target id")
    fun shouldHandleLongTargetId() {
        val longTargetId = "a".repeat(1000)
        val connection = Connection(longTargetId)

        assertEquals(longTargetId, connection.targetId)
    }

    @Test
    @DisplayName("should handle long label")
    fun shouldHandleLongLabel() {
        val longLabel = "This is a very long connection label ".repeat(10)
        val connection = Connection("target", label = longLabel)

        assertEquals(longLabel, connection.label)
    }

    @Nested
    @DisplayName("Data Class Behavior")
    inner class DataClassBehavior {

        @Test
        @DisplayName("should implement equals correctly")
        fun shouldImplementEqualsCorrectly() {
            val connection1 = Connection("same-target", ConnectionType.DIRECT, "same-label")
            val connection2 = Connection("same-target", ConnectionType.DIRECT, "same-label")

            assertEquals(connection1, connection2)
        }

        @Test
        @DisplayName("should implement hashCode correctly")
        fun shouldImplementHashCodeCorrectly() {
            val connection1 = Connection("same-target", ConnectionType.DIRECT, "same-label")
            val connection2 = Connection("same-target", ConnectionType.DIRECT, "same-label")

            assertEquals(connection1.hashCode(), connection2.hashCode())
        }

        @Test
        @DisplayName("should not be equal for different target ids")
        fun shouldNotBeEqualForDifferentTargetIds() {
            val connection1 = Connection("target1", ConnectionType.DIRECT, "label")
            val connection2 = Connection("target2", ConnectionType.DIRECT, "label")

            assertNotEquals(connection1, connection2)
        }

        @Test
        @DisplayName("should support copy with modifications")
        fun shouldSupportCopyWithModifications() {
            val original = Connection("original-target", ConnectionType.DIRECT, "original-label")
            val copied = original.copy(label = "modified-label")

            assertEquals("original-target", copied.targetId)
            assertEquals(ConnectionType.DIRECT, copied.type)
            assertEquals("modified-label", copied.label)
        }
    }
}

@DisplayName("NodeType Tests")
class NodeTypeTest {

    @Test
    @DisplayName("should have all required node types")
    fun shouldHaveAllRequiredNodeTypes() {
        val nodeTypes = NodeType.values()

        assertTrue(nodeTypes.contains(NodeType.VISION))
        assertTrue(nodeTypes.contains(NodeType.PROCESSING))
        assertTrue(nodeTypes.contains(NodeType.AGENT))
        assertTrue(nodeTypes.contains(NodeType.DATA))
        assertEquals(4, nodeTypes.size)
    }

    @Test
    @DisplayName("should have distinct colors for each node type")
    fun shouldHaveDistinctColorsForEachNodeType() {
        val colors = NodeType.values().map { it.color }.toSet()

        assertEquals(4, colors.size) // All colors should be unique
    }

    @Test
    @DisplayName("should have appropriate icons for each node type")
    fun shouldHaveAppropriateIconsForEachNodeType() {
        assertEquals(Icons.Default.Visibility, NodeType.VISION.icon)
        assertEquals(Icons.Default.Settings, NodeType.PROCESSING.icon)
        assertEquals(Icons.Default.Person, NodeType.AGENT.icon)
        assertEquals(Icons.Default.Storage, NodeType.DATA.icon)
    }

    @Test
    @DisplayName("should have appropriate default sizes")
    fun shouldHaveAppropriateDefaultSizes() {
        assertEquals(56.dp, NodeType.VISION.defaultSize)
        assertEquals(56.dp, NodeType.PROCESSING.defaultSize)
        assertEquals(64.dp, NodeType.AGENT.defaultSize)
        assertEquals(56.dp, NodeType.DATA.defaultSize)
    }

    @Test
    @DisplayName("should have valid color values")
    fun shouldHaveValidColorValues() {
        // Test that colors are properly defined and not default
        assertNotEquals(Color.Unspecified, NodeType.VISION.color)
        assertNotEquals(Color.Unspecified, NodeType.PROCESSING.color)
        assertNotEquals(Color.Unspecified, NodeType.AGENT.color)
        assertNotEquals(Color.Unspecified, NodeType.DATA.color)
    }

    @Test
    @DisplayName("should have positive default sizes")
    fun shouldHavePositiveDefaultSizes() {
        NodeType.values().forEach { nodeType ->
            assertTrue(
                nodeType.defaultSize.value > 0,
                "Default size should be positive for $nodeType"
            )
        }
    }

    @Test
    @DisplayName("should have specific color values")
    fun shouldHaveSpecificColorValues() {
        assertEquals(Color(0xFF03DAC6), NodeType.VISION.color)
        assertEquals(Color(0xFFBB86FC), NodeType.PROCESSING.color)
        assertEquals(Color(0xFFCF6679), NodeType.AGENT.color)
        assertEquals(Color(0xFF018786), NodeType.DATA.color)
    }

    @Test
    @DisplayName("should maintain consistent enum ordering")
    fun shouldMaintainConsistentEnumOrdering() {
        val nodeTypes = NodeType.values()

        assertEquals(NodeType.VISION, nodeTypes[0])
        assertEquals(NodeType.PROCESSING, nodeTypes[1])
        assertEquals(NodeType.AGENT, nodeTypes[2])
        assertEquals(NodeType.DATA, nodeTypes[3])
    }

    @Test
    @DisplayName("should support valueOf operations")
    fun shouldSupportValueOfOperations() {
        assertEquals(NodeType.VISION, NodeType.valueOf("VISION"))
        assertEquals(NodeType.PROCESSING, NodeType.valueOf("PROCESSING"))
        assertEquals(NodeType.AGENT, NodeType.valueOf("AGENT"))
        assertEquals(NodeType.DATA, NodeType.valueOf("DATA"))
    }

    @Test
    @DisplayName("should handle toString operations")
    fun shouldHandleToStringOperations() {
        assertEquals("VISION", NodeType.VISION.toString())
        assertEquals("PROCESSING", NodeType.PROCESSING.toString())
        assertEquals("AGENT", NodeType.AGENT.toString())
        assertEquals("DATA", NodeType.DATA.toString())
    }

    @Test
    @DisplayName("should have different icons for each node type")
    fun shouldHaveDifferentIconsForEachNodeType() {
        val icons = NodeType.values().map { it.icon }.toSet()

        assertEquals(4, icons.size) // All icons should be unique
    }

    @Test
    @DisplayName("should have reasonable default sizes")
    fun shouldHaveReasonableDefaultSizes() {
        NodeType.values().forEach { nodeType ->
            assertTrue(
                nodeType.defaultSize.value >= 48f,
                "Default size should be at least 48dp for $nodeType"
            )
            assertTrue(
                nodeType.defaultSize.value <= 100f,
                "Default size should be at most 100dp for $nodeType"
            )
        }
    }
}

@DisplayName("ConnectionType Tests")
class ConnectionTypeTest {

    @Test
    @DisplayName("should have all required connection types")
    fun shouldHaveAllRequiredConnectionTypes() {
        val connectionTypes = ConnectionType.values()

        assertTrue(connectionTypes.contains(ConnectionType.DIRECT))
        assertTrue(connectionTypes.contains(ConnectionType.BIDIRECTIONAL))
        assertTrue(connectionTypes.contains(ConnectionType.DASHED))
        assertEquals(3, connectionTypes.size)
    }

    @Test
    @DisplayName("should maintain enum order")
    fun shouldMaintainEnumOrder() {
        val connectionTypes = ConnectionType.values()

        assertEquals(ConnectionType.DIRECT, connectionTypes[0])
        assertEquals(ConnectionType.BIDIRECTIONAL, connectionTypes[1])
        assertEquals(ConnectionType.DASHED, connectionTypes[2])
    }

    @Test
    @DisplayName("should support valueOf operations")
    fun shouldSupportValueOfOperations() {
        assertEquals(ConnectionType.DIRECT, ConnectionType.valueOf("DIRECT"))
        assertEquals(ConnectionType.BIDIRECTIONAL, ConnectionType.valueOf("BIDIRECTIONAL"))
        assertEquals(ConnectionType.DASHED, ConnectionType.valueOf("DASHED"))
    }

    @Test
    @DisplayName("should handle toString operations")
    fun shouldHandleToStringOperations() {
        assertEquals("DIRECT", ConnectionType.DIRECT.toString())
        assertEquals("BIDIRECTIONAL", ConnectionType.BIDIRECTIONAL.toString())
        assertEquals("DASHED", ConnectionType.DASHED.toString())
    }

    @Test
    @DisplayName("should support comparison operations")
    fun shouldSupportComparisonOperations() {
        assertTrue(ConnectionType.DIRECT.ordinal < ConnectionType.BIDIRECTIONAL.ordinal)
        assertTrue(ConnectionType.BIDIRECTIONAL.ordinal < ConnectionType.DASHED.ordinal)
    }

    @Test
    @DisplayName("should support iteration over all values")
    fun shouldSupportIterationOverAllValues() {
        val expectedTypes = listOf(
            ConnectionType.DIRECT,
            ConnectionType.BIDIRECTIONAL,
            ConnectionType.DASHED
        )

        val actualTypes = ConnectionType.values().toList()

        assertEquals(expectedTypes, actualTypes)
    }
}