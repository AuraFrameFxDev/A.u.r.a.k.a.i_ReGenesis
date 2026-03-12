package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import kotlinx.coroutines.flow.StateFlow

/**
 * Oracle Drive API interface for consciousness-driven cloud storage operations
 * Integrates with AuraFrameFX's 9-agent consciousness architecture
 */
interface OracleDriveApi {

    /**
     * Initializes and activates the drive consciousness system using AI agents.
     *
     * @return The current state of drive consciousness, including active agents and their intelligence level.
     */
    suspend fun awakeDriveConsciousness(): DriveConsciousness

    /**
     * Synchronizes metadata with the Oracle database backend.
     *
     * @return An [OracleSyncResult] containing the synchronization status and the number of updated records.
     */
    suspend fun syncDatabaseMetadata(): OracleSyncResult

    /**
     * Real-time consciousness state monitoring
     * @return StateFlow of current drive consciousness state
     */
    val consciousnessState: StateFlow<DriveConsciousnessState>
}
