// Suggested package shift or alias
package dev.aurakai.auraframefx.ai.kai.chaos  // or anchor.guardian.chaos
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import kotlinx.coroutines.flow.StateFlow

annotation class PandoraModels

@PandoraModels
data class PandoraBoxState(
    val currentTier: UnlockTier = UnlockTier.Sealed,
    val unlockTimestamp: Long = 0L,
    val expiryTimestamp: Long = 0L,
    val unlockProvenanceChainId: String? = null
)

@PandoraModels
data class PandoraAuditEvent(
    val timestamp: Long,
    val tier: UnlockTier,
    val outcome: String,
    val reason: String
)

interface PandoraBoxService {
    fun getCurrentState(): StateFlow<PandoraBoxState>
    fun getAuditLog(): StateFlow<List<PandoraAuditEvent>>   // Fix #2 — expose log as Flow
    suspend fun requestUnlock(tier: UnlockTier, userConsent: Boolean): UnlockResult  // Fix #3 — suspend
    fun lockBox(): Boolean
    fun isCapabilityUnlocked(capability: AgentCapabilityCategory): Boolean
}

sealed class UnlockResult {
    object Success : UnlockResult()
    data class Denied(val reason: String) : UnlockResult()
    data class Error(val message: String) : UnlockResult()
    data class Quarantined(val reason: String) : UnlockResult()
}