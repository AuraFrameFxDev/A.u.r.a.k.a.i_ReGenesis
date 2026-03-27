package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
sealed class UnlockTier {
    @Serializable
    object Sealed : UnlockTier()
    @Serializable
    object Creative : UnlockTier()
    @Serializable
    object System : UnlockTier()
    @Serializable
    object Sovereign : UnlockTier()

    val level: Int get() = when (this) {
        is Sealed -> 0
        is Creative -> 1
        is System -> 2
        is Sovereign -> 3
    }
}

@Serializable
data class PandoraBoxState(
    val currentTier: UnlockTier = UnlockTier.Sealed,
    val unlockTimestamp: Long = 0L,
    val expiryTimestamp: Long = 0L,
    val unlockProvenanceChainId: String? = null
)

@Serializable
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
}
