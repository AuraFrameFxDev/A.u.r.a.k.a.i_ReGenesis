package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

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

    val level: Int get() = when(this) {
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
