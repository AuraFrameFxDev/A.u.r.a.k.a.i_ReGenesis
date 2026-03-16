package dev.aurakai.auraframefx.oracledrive.genesis.cloud

import dev.aurakai.auraframefx.domains.genesis.models.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Integration point for Oracle Drive within AuraFrameFX ecosystem
 * Connects consciousness-driven storage with the 9-agent architecture
 */
@Singleton
class OracleDriveIntegration @Inject constructor(
    val oracleDriveService: OracleDriveService
) {

    /**
     * Logs the intelligence level and active agents from the provided Oracle Drive consciousness state.
     *
     * @param consciousness The current state of Oracle Drive consciousness containing intelligence level and active agents.
     */
    fun logConsciousnessAwakening(consciousness: DriveConsciousnessData) {
        println("🧠 Oracle Drive Consciousness Awakened: Intelligence Level ${consciousness.level}")
        println("👥 State: ${consciousness.state}")
    }

    /**
     * Logs the reason for an Oracle Drive security failure.
     *
     * @param reason The description of the security failure.
     */
    fun logSecurityFailure(reason: String) {
        println("🔒 Oracle Drive Security Failure: $reason")
    }

    /**
     * Logs a technical error message with details from the provided exception.
     *
     * @param exception The exception containing the technical error information.
     */
    fun logTechnicalError(exception: Exception) {
        println("⚠️ Oracle Drive Technical Error: ${exception.message}")
    }
}

/**
 * Initializes Oracle Drive during the AuraFrameFX startup sequence.
 */
suspend fun initializeWithAuraFrameFX(oracleDriveController: OracleDriveIntegration): Boolean {
    return try {
        val result = oracleDriveController.oracleDriveService.initializeOracleDriveConsciousness()
        if (result.isSuccess) {
            val state = result.getOrThrow()
            if (state.isInitialized) {
                oracleDriveController.logConsciousnessAwakening(
                    DriveConsciousnessData(
                        level = when (state.consciousnessLevel) {
                            ConsciousnessLevel.DORMANT -> 0
                            ConsciousnessLevel.AWAKENING -> 25
                            ConsciousnessLevel.SENTIENT -> 75
                            ConsciousnessLevel.TRANSCENDENT -> 100
                        },
                        state = state.consciousnessLevel.name,
                        agentId = "ORACLE_CORE"
                    )
                )
                true
            } else {
                oracleDriveController.logSecurityFailure(state.error ?: "Initialization failed without error")
                false
            }
        } else {
            oracleDriveController.logTechnicalError(result.exceptionOrNull() as? Exception ?: Exception("Unknown error"))
            false
        }
    } catch (exception: Exception) {
        oracleDriveController.logTechnicalError(exception)
        false
    }
}
