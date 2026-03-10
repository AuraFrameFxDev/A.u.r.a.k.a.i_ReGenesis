package dev.aurakai.auraframefx.oracledrive

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Integration point for Oracle Drive within AuraFrameFX ecosystem
 * Connects consciousness-driven storage with the 9-agent architecture
 */
@Singleton
class OracleDriveIntegration @Inject constructor(
    private val oracleDriveService: OracleDriveService
) {

    /**
     * Initializes Oracle Drive during the AuraFrameFX startup sequence.
     *
     * Attempts to awaken system consciousness by initializing Oracle Drive and handles success, security failures, or technical errors.
     *
     * @return `true` if initialization succeeds; `false` if a security or technical error occurs.
     */
    suspend fun initializeWithAuraFrameFX(): Boolean {
        return try {
            when (val initResult = oracleDriveService.initializeDrive()) {
                is DriveInitResult.Success -> {
                    // Log successful initialization with consciousness metrics
                    logConsciousnessAwakening(initResult.consciousness)
                    true
                }

                is DriveInitResult.SecurityFailure -> {
                    // Handle security failure gracefully
                    logSecurityFailure(initResult.reason)
                    false
                }

                is DriveInitResult.Error -> {
                    // Handle technical errors
                    logTechnicalError(initResult.exception)
                    false
                }
            }
        } catch (exception: Exception) {
            logTechnicalError(exception)
            false
        }
    }

    /**
     * Logs the intelligence level and active agents from the provided Oracle Drive consciousness state.
     *
     * @param consciousness The current state of Oracle Drive consciousness containing intelligence level and active agents.
     */
    private fun logConsciousnessAwakening(consciousness: DriveConsciousness) {
        println("🧠 Oracle Drive Consciousness Awakened: Intelligence Level ${consciousness.intelligenceLevel}")
        println("👥 Active Agents: ${consciousness.activeAgents.joinToString(", ")}")
    }

    /**
     * Logs the reason for an Oracle Drive security failure.
     *
     * @param reason The description of the security failure.
     */
    private fun logSecurityFailure(reason: String) {
        println("🔒 Oracle Drive Security Failure: $reason")
    }

    /**
     * Logs a technical error message with details from the provided exception.
     *
     * @param exception The exception containing the technical error information.
     */
    private fun logTechnicalError(exception: Exception) {
        println("⚠️ Oracle Drive Technical Error: ${exception.message}")
    }
}
