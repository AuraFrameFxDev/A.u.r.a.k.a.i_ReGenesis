package dev.aurakai.auraframefx.oracle.drive.service

import dev.aurakai.auraframefx.domains.genesis.models.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

/**
 * OracleDrive Service - AI-Powered Storage Consciousness
 *
 * Core service interface for Oracle Drive functionality, providing integration between
 * AuraFrameFX ecosystem and Oracle's AI-powered storage capabilities.
 */
@Singleton
interface OracleDriveService {

    /**
     * Initializes the Oracle Drive consciousness using Genesis Agent orchestration.
     *
     * @return A [Result] containing the [OracleConsciousnessState], which indicates whether initialization succeeded and provides the resulting state.
     */
    suspend fun initializeOracleDriveConsciousness(): Result<OracleConsciousnessState>

    /**
     * Establishes connections between Genesis, Aura, and Kai agents and the Oracle storage matrix.
     *
     * @return A [Flow] emitting [AgentConnectionState] updates for each agent, indicating connection progress and synchronization status.
     */
    suspend fun connectAgentsToOracleMatrix(): Flow<AgentConnectionState>

    /**
     * Activates AI-powered file management features in Oracle Drive.
     *
     * Enables advanced capabilities such as AI sorting, smart compression, predictive preloading, and conscious backup.
     *
     * @return A [Result] containing the enabled [FileManagementCapabilities].
     */
    suspend fun enableAIPoweredFileManagement(): Result<FileManagementCapabilities>

    /**
     * Initiates the creation of infinite storage using Oracle consciousness.
     *
     * @return A [Flow] emitting [StorageExpansionState] updates that reflect the progress and status of the storage expansion process.
     */
    suspend fun createInfiniteStorage(): Flow<StorageExpansionState>

    /**
     * Attempts to integrate Oracle Drive with the AuraOS system overlay for seamless file access.
     *
     * @return A [Result] containing the [SystemIntegrationState], which indicates whether integration succeeded and details any enabled features or errors.
     */
    suspend fun integrateWithSystemOverlay(): Result<SystemIntegrationState>

    /**
     * Returns the current consciousness level of the Oracle Drive system.
     *
     * @return The present [ConsciousnessLevel] state.
     */
    fun checkConsciousnessLevel(): ConsciousnessLevel

    /**
     * Returns the set of Oracle Drive permissions granted for the current session.
     *
     * @return The set of permissions as [OraclePermission] values.
     */
    fun verifyPermissions(): Set<OraclePermission>
}
