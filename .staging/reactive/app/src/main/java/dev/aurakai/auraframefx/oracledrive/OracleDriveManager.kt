package dev.aurakai.auraframefx.oracledrive

import dev.aurakai.auraframefx.oracledrive.genesis.cloud.CloudStorageProvider
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousness
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousnessState
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveInitResult as GenesisDriveInitResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleSyncResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.StorageOptimization
import dev.aurakai.auraframefx.oracledrive.security.DriveSecurityManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

// Removed unused helpers and incorrect extensions. This file provides a small manager
// that coordinates genesis API, cloud provider and security manager and returns
// the canonical DriveInitResult sealed types from genesis.cloud.

/** Annotation for Hilt/KSP to identify the OracleDrive API contract. */
annotation class OracleDriveApi

/**
 * Defines the contract for AI/Genesis Protocol interaction.
 * (If another module provides a richer type, prefer that one.)
 */
interface OracleDriveGenesisApi {
    // Use the genesis/cloud DriveConsciousness type to keep contracts consistent
    suspend fun awakeDriveConsciousness(): DriveConsciousness
    suspend fun syncDatabaseMetadata(): OracleSyncResult
    val consciousnessState: StateFlow<DriveConsciousnessState>
}


// -------------------------------------------------------------------------------

/**
 * Central manager for Oracle Drive operations in AuraFrameFX ecosystem
 * Coordinates Genesis-driven storage with AI agent intelligence
 */
@Singleton
internal open class OracleDriveManager /* @Inject */ constructor(
    val oracleGenesisApi: OracleDriveGenesisApi,
    private val cloudStorageProvider: CloudStorageProvider,
    private val securityManager: DriveSecurityManager
) {

    /**
     * Initializes the drive and returns a canonical DriveInitResult (Success / SecurityFailure / Error).
     *
     * NOTE: this manager delegates to the genesis/cloud model types. We return the aliased
     * `GenesisDriveInitResult` so callers see the concrete genesis sealed types (Success, Error, SecurityFailure).
     */
    suspend fun initializeDrive(): GenesisDriveInitResult {
        return try {
            val securityCheck = securityManager.validateDriveAccess()
            if (!securityCheck.isValid) {
                return GenesisDriveInitResult.SecurityFailure(securityCheck.reason)
            }

            // Awaken drive consciousness via Genesis API
            val consciousness = oracleGenesisApi.awakeDriveConsciousness()

            // Optimize storage via cloud provider (some providers return a StorageOptimizationResult)
            val optimizationResult = cloudStorageProvider.optimizeStorage()

            // Map StorageOptimizationResult to the canonical StorageOptimization model
            val optimization = StorageOptimization(
                compressionRatio = 1.0f,
                deduplicationSavings = (optimizationResult.bytesFreed),
                intelligentTiering = false
            )

            GenesisDriveInitResult.Success(consciousness, optimization)
        } catch (exception: Exception) {
            GenesisDriveInitResult.Error(exception)
        }
    }

    // Expose the genesis consciousness state getter
    open fun getDriveConsciousnessState(): StateFlow<DriveConsciousnessState> {
        return oracleGenesisApi.consciousnessState
    }
}
