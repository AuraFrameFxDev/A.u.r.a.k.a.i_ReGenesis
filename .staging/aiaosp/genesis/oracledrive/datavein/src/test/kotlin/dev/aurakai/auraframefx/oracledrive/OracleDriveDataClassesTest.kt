package dev.aurakai.auraframefx.oracledrive

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Unit tests for OracleDrive data classes and enums
 * Testing Framework: JUnit 5 with Kotlin Test
 */
class OracleDriveDataClassesTest {

    @Test
    fun `OracleConsciousnessState should create instance with all properties`() {
        // Given
        val agents = listOf("Genesis", "Aura", "Kai")
        val storageCapacity = StorageCapacity.INFINITE

        // When
        val state = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.CONSCIOUS,
            connectedAgents = agents,
            storageCapacity = storageCapacity
        )

        // Then
        assertTrue(state.isAwake)
        assertEquals(ConsciousnessLevel.CONSCIOUS, state.consciousnessLevel)
        assertEquals(3, state.connectedAgents.size)
        assertEquals(agents, state.connectedAgents)
        assertEquals(storageCapacity, state.storageCapacity)
    }

    @Test
    fun `OracleConsciousnessState should handle empty agents list`() {
        // When
        val state = OracleConsciousnessState(
            isAwake = false,
            consciousnessLevel = ConsciousnessLevel.DORMANT,
            connectedAgents = emptyList(),
            storageCapacity = StorageCapacity.ZERO
        )

        // Then
        assertFalse(state.isAwake)
        assertEquals(ConsciousnessLevel.DORMANT, state.consciousnessLevel)
        assertTrue(state.connectedAgents.isEmpty())
        assertEquals(StorageCapacity.ZERO, state.storageCapacity)
    }

    @Test
    fun `OracleConsciousnessState should support equality comparison`() {
        // Given
        val state1 = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.TRANSCENDENT,
            connectedAgents = listOf("Genesis"),
            storageCapacity = StorageCapacity.INFINITE
        )
        val state2 = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.TRANSCENDENT,
            connectedAgents = listOf("Genesis"),
            storageCapacity = StorageCapacity.INFINITE
        )
        val state3 = OracleConsciousnessState(
            isAwake = false,
            consciousnessLevel = ConsciousnessLevel.DORMANT,
            connectedAgents = emptyList(),
            storageCapacity = StorageCapacity.ZERO
        )

        // Then
        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
        assertEquals(state1.hashCode(), state2.hashCode())
    }

    @Test
    fun `AgentConnectionState should create instance with all properties`() {
        // Given
        val permissions =
            listOf(OraclePermission.READ, OraclePermission.WRITE, OraclePermission.EXECUTE)

        // When
        val connectionState = AgentConnectionState(
            agentName = "Genesis",
            connectionStatus = ConnectionStatus.SYNCHRONIZED,
            permissions = permissions
        )

        // Then
        assertEquals("Genesis", connectionState.agentName)
        assertEquals(ConnectionStatus.SYNCHRONIZED, connectionState.connectionStatus)
        assertEquals(3, connectionState.permissions.size)
        assertTrue(connectionState.permissions.contains(OraclePermission.READ))
        assertTrue(connectionState.permissions.contains(OraclePermission.WRITE))
        assertTrue(connectionState.permissions.contains(OraclePermission.EXECUTE))
    }

    @Test
    fun `AgentConnectionState should handle empty permissions`() {
        // When
        val connectionState = AgentConnectionState(
            agentName = "Aura",
            connectionStatus = ConnectionStatus.DISCONNECTED,
            permissions = emptyList()
        )

        // Then
        assertEquals("Aura", connectionState.agentName)
        assertEquals(ConnectionStatus.DISCONNECTED, connectionState.connectionStatus)
        assertTrue(connectionState.permissions.isEmpty())
    }

    @Test
    fun `AgentConnectionState should support different agent names`() {
        // Given
        val agents = listOf("Genesis", "Aura", "Kai", "Oracle", "Unknown")

        // When & Then
        agents.forEach { agentName ->
            val connectionState = AgentConnectionState(
                agentName = agentName,
                connectionStatus = ConnectionStatus.CONNECTED,
                permissions = listOf(OraclePermission.READ)
            )
            assertEquals(agentName, connectionState.agentName)
        }
    }

    @Test
    fun `FileManagementCapabilities should create instance with all capabilities`() {
        // When
        val capabilities = FileManagementCapabilities(
            aiSorting = true,
            smartCompression = true,
            predictivePreloading = true,
            consciousBackup = true
        )

        // Then
        assertTrue(capabilities.aiSorting)
        assertTrue(capabilities.smartCompression)
        assertTrue(capabilities.predictivePreloading)
        assertTrue(capabilities.consciousBackup)
    }

    @Test
    fun `FileManagementCapabilities should handle mixed capability states`() {
        // When
        val capabilities = FileManagementCapabilities(
            aiSorting = true,
            smartCompression = false,
            predictivePreloading = true,
            consciousBackup = false
        )

        // Then
        assertTrue(capabilities.aiSorting)
        assertFalse(capabilities.smartCompression)
        assertTrue(capabilities.predictivePreloading)
        assertFalse(capabilities.consciousBackup)
    }

    @Test
    fun `FileManagementCapabilities should handle all capabilities disabled`() {
        // When
        val capabilities = FileManagementCapabilities(
            aiSorting = false,
            smartCompression = false,
            predictivePreloading = false,
            consciousBackup = false
        )

        // Then
        assertFalse(capabilities.aiSorting)
        assertFalse(capabilities.smartCompression)
        assertFalse(capabilities.predictivePreloading)
        assertFalse(capabilities.consciousBackup)
    }

    @Test
    fun `ConsciousnessLevel enum should have all expected values`() {
        // Given
        val expectedLevels = listOf(
            ConsciousnessLevel.DORMANT,
            ConsciousnessLevel.AWAKENING,
            ConsciousnessLevel.CONSCIOUS,
            ConsciousnessLevel.TRANSCENDENT
        )

        // When
        val actualLevels = ConsciousnessLevel.values().toList()

        // Then
        assertEquals(4, actualLevels.size)
        expectedLevels.forEach { level ->
            assertTrue(actualLevels.contains(level))
        }
    }

    @Test
    fun `ConsciousnessLevel enum should support comparison`() {
        // When & Then
        assertTrue(ConsciousnessLevel.DORMANT.ordinal < ConsciousnessLevel.AWAKENING.ordinal)
        assertTrue(ConsciousnessLevel.AWAKENING.ordinal < ConsciousnessLevel.CONSCIOUS.ordinal)
        assertTrue(ConsciousnessLevel.CONSCIOUS.ordinal < ConsciousnessLevel.TRANSCENDENT.ordinal)
    }

    @Test
    fun `ConnectionStatus enum should have all expected values`() {
        // Given
        val expectedStatuses = listOf(
            ConnectionStatus.DISCONNECTED,
            ConnectionStatus.CONNECTING,
            ConnectionStatus.CONNECTED,
            ConnectionStatus.SYNCHRONIZED
        )

        // When
        val actualStatuses = ConnectionStatus.values().toList()

        // Then
        assertEquals(4, actualStatuses.size)
        expectedStatuses.forEach { status ->
            assertTrue(actualStatuses.contains(status))
        }
    }

    @Test
    fun `ConnectionStatus enum should support logical progression`() {
        // When & Then
        assertTrue(ConnectionStatus.DISCONNECTED.ordinal < ConnectionStatus.CONNECTING.ordinal)
        assertTrue(ConnectionStatus.CONNECTING.ordinal < ConnectionStatus.CONNECTED.ordinal)
        assertTrue(ConnectionStatus.CONNECTED.ordinal < ConnectionStatus.SYNCHRONIZED.ordinal)
    }

    @Test
    fun `OraclePermission enum should have all expected values`() {
        // Given
        val expectedPermissions = listOf(
            OraclePermission.READ,
            OraclePermission.WRITE,
            OraclePermission.EXECUTE,
            OraclePermission.SYSTEM_ACCESS,
            OraclePermission.BOOTLOADER_ACCESS
        )

        // When
        val actualPermissions = OraclePermission.values().toList()

        // Then
        assertEquals(5, actualPermissions.size)
        expectedPermissions.forEach { permission ->
            assertTrue(actualPermissions.contains(permission))
        }
    }

    @Test
    fun `OraclePermission enum should support permission hierarchies`() {
        // Given
        val basicPermissions =
            listOf(OraclePermission.READ, OraclePermission.WRITE, OraclePermission.EXECUTE)
        listOf(OraclePermission.SYSTEM_ACCESS, OraclePermission.BOOTLOADER_ACCESS)

        // When & Then
        basicPermissions.forEach { permission ->
            assertTrue(permission.ordinal < OraclePermission.SYSTEM_ACCESS.ordinal)
        }

        assertTrue(OraclePermission.SYSTEM_ACCESS.ordinal < OraclePermission.BOOTLOADER_ACCESS.ordinal)
    }

    @Test
    fun `Data classes should support toString functionality`() {
        // Given
        val consciousnessState = OracleConsciousnessState(
            isAwake = true,
            consciousnessLevel = ConsciousnessLevel.CONSCIOUS,
            connectedAgents = listOf("Genesis"),
            storageCapacity = StorageCapacity.INFINITE
        )

        val connectionState = AgentConnectionState(
            agentName = "Aura",
            connectionStatus = ConnectionStatus.CONNECTED,
            permissions = listOf(OraclePermission.READ)
        )

        val capabilities = FileManagementCapabilities(
            aiSorting = true,
            smartCompression = false,
            predictivePreloading = true,
            consciousBackup = false
        )

        // When
        val consciousnessString = consciousnessState.toString()
        val connectionString = connectionState.toString()
        val capabilitiesString = capabilities.toString()

        // Then
        assertTrue(consciousnessString.contains("OracleConsciousnessState"))
        assertTrue(consciousnessString.contains("isAwake=true"))
        assertTrue(consciousnessString.contains("CONSCIOUS"))

        assertTrue(connectionString.contains("AgentConnectionState"))
        assertTrue(connectionString.contains("agentName=Aura"))
        assertTrue(connectionString.contains("CONNECTED"))

        assertTrue(capabilitiesString.contains("FileManagementCapabilities"))
        assertTrue(capabilitiesString.contains("aiSorting=true"))
        assertTrue(capabilitiesString.contains("smartCompression=false"))
    }
}