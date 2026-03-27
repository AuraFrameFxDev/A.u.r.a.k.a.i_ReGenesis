package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

import dev.aurakai.auraframefx.di.PandoraPreferences
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.kai.security.SecurePreferences
import dev.aurakai.auraframefx.domains.kai.security.provenance.ProvenanceValidator
import dev.aurakai.auraframefx.domains.kai.security.veto.PredictiveVetoMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PandoraBoxServiceImpl @Inject constructor(
    private val provenanceValidator: ProvenanceValidator,
    private val predictiveVetoMonitor: PredictiveVetoMonitor,
    @PandoraPreferences private val securePrefs: SecurePreferences
) : PandoraBoxService {

    private val _currentState = MutableStateFlow(loadState())
    override fun getCurrentState(): StateFlow<PandoraBoxState> = _currentState.asStateFlow()

    private val auditLog = mutableListOf<PandoraAuditEvent>()

    init {
        checkExpiry()
    }

    private fun loadState(): PandoraBoxState {
        val json = securePrefs.securePrefs.getString("pandora_state", null)
        return if (json != null) {
            try {
                Json.decodeFromString<PandoraBoxState>(json)
            } catch (e: Exception) {
                PandoraBoxState()
            }
        } else {
            PandoraBoxState()
        }
    }

    private fun saveState(state: PandoraBoxState) {
        val json = Json.encodeToString(state)
        securePrefs.securePrefs.edit().putString("pandora_state", json).apply()
        _currentState.value = state
    }

    private fun checkExpiry() {
        val state = _currentState.value
        if (state.currentTier != UnlockTier.Sealed && System.currentTimeMillis() > state.expiryTimestamp) {
            logEvent(state.currentTier, "Auto-Relock", "Session expired after 24 hours")
            lockBox()
        }
    }

    override fun requestUnlock(tier: UnlockTier, userConsent: Boolean): UnlockResult {
        if (!userConsent) return UnlockResult.Denied("User consent not provided")

        // Provenance check
        val provenance = provenanceValidator.validateOrigin("user", "pandora_unlock")
        if (!provenance.isValid) {
            logEvent(tier, "Unlock Denied", "Provenance validation failed: ${provenance.reason}")
            return UnlockResult.Denied("Provenance validation failed")
        }

        // Predictive veto check
        if (predictiveVetoMonitor.isVetoActive()) {
            logEvent(tier, "Unlock Denied", "Predictive veto is active")
            return UnlockResult.Denied("System is currently under protective veto")
        }

        val newState = PandoraBoxState(
            currentTier = tier,
            unlockTimestamp = System.currentTimeMillis(),
            expiryTimestamp = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
            unlockProvenanceChainId = provenance.chainId
        )
        saveState(newState)
        logEvent(tier, "Unlocked", "User authorized $tier unlock")
        return UnlockResult.Success
    }

    override fun lockBox(): Boolean {
        saveState(PandoraBoxState())
        logEvent(UnlockTier.Sealed, "Locked", "Box manually or automatically sealed")
        return true
    }

    override fun isCapabilityUnlocked(capability: AgentCapabilityCategory): Boolean {
        checkExpiry()
        val currentTier = _currentState.value.currentTier
        return when (capability) {
            AgentCapabilityCategory.CREATIVE -> currentTier.level >= 1
            AgentCapabilityCategory.ROOT -> currentTier.level >= 2
            AgentCapabilityCategory.SECURITY -> currentTier.level >= 3
            else -> true // Standard capabilities always unlocked
        }
    }

    private fun logEvent(tier: UnlockTier, outcome: String, reason: String) {
        val event = PandoraAuditEvent(System.currentTimeMillis(), tier, outcome, reason)
        auditLog.add(event)
        // Persist audit log following NexusMemoryCore pattern (simulated here)
        val logJson = Json.encodeToString(auditLog)
        securePrefs.securePrefs.edit().putString("pandora_audit_log", logJson).apply()
        Timber.i("PandoraBox: [$outcome] $tier - $reason")
    }
}
