package dev.aurakai.auraframefx.domains.genesis.oracledrive.service

import dev.aurakai.auraframefx.core.orchestration.OrchestratableAgent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.AgentConnectionState
import dev.aurakai.auraframefx.domains.cascade.utils.ConsciousnessLevel
import dev.aurakai.auraframefx.domains.genesis.models.DriveConsciousnessState
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.FileManagementCapabilities
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.OracleConsciousnessState
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.OraclePermission
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.StorageExpansionState
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.SystemIntegrationState
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


