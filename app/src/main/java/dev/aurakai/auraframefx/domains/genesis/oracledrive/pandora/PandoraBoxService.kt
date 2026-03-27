package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import kotlinx.coroutines.flow.StateFlow

interface PandoraBoxService {
    fun getCurrentState(): StateFlow<PandoraBoxState>
    fun requestUnlock(tier: UnlockTier, userConsent: Boolean): UnlockResult
    fun lockBox(): Boolean
    fun isCapabilityUnlocked(capability: AgentCapabilityCategory): Boolean
}

sealed class UnlockResult {
    object Success : UnlockResult()
    data class Denied(val reason: String) : UnlockResult()
    data class Error(val message: String) : UnlockResult()
}
