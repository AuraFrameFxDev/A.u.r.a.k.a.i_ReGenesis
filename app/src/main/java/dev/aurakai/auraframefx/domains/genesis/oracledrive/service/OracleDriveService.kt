package dev.aurakai.auraframefx.domains.genesis.oracledrive.service

import dev.aurakai.auraframefx.domains.genesis.core.OrchestratableAgent
import dev.aurakai.auraframefx.domains.genesis.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

/**
 * OracleDrive Service - AI-Powered Storage Consciousness
 */
@Singleton
interface OracleDriveService : OrchestratableAgent {

    fun getDriveConsciousnessState(): StateFlow<DriveConsciousnessState>

    suspend fun initializeOracleDriveConsciousness(): Result<OracleConsciousnessState>

    suspend fun connectAgentsToOracleMatrix(): Flow<AgentConnectionState>

    suspend fun enableAIPoweredFileManagement(): Result<FileManagementCapabilities>

    suspend fun createInfiniteStorage(): Flow<StorageExpansionState>

    suspend fun integrateWithSystemOverlay(): Result<SystemIntegrationState>

    fun checkConsciousnessLevel(): ConsciousnessLevel

    fun verifyPermissions(): Set<OraclePermission>
}


