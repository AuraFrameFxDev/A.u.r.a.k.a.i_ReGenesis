package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

import dev.aurakai.auraframefx.domains.nexus.di.PandoraPreferences
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurePreferences
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.provenance.ProvenanceResult
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.provenance.ProvenanceValidator
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.veto.PredictiveVetoMonitor
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sovereignty.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PandoraBoxServiceImpl @Inject constructor(
    private val provenanceValidator: ProvenanceValidator,
    private val predictiveVetoMonitor: PredictiveVetoMonitor,
    @PandoraPreferences private val securePrefs: SecurePreferences,
    // Fix #4 — use injected @ApplicationScope instead of leaked internal scope
    @ApplicationScope private val appScope: CoroutineScope
) : PandoraBoxService {

    private val _currentState = MutableStateFlow(loadState())
    override fun getCurrentState(): StateFlow<PandoraBoxState> = _currentState.asStateFlow()

    // Fix #2 — audit log exposed as StateFlow, updated on every event
    private val _auditLog = MutableStateFlow<List<PandoraAuditEvent>>(loadAuditLog())
    override fun getAuditLog(): StateFlow<List<PandoraAuditEvent>> = _auditLog.asStateFlow()

    // Fix #5 — thread-safe audit list
    private val auditList: MutableList<PandoraAuditEvent> =
        Collections.synchronizedList(_auditLog.value.toMutableList())

    init {
        // Fix #4 — lifecycle-tied scope, not leaked supervisor job
        appScope.launch(Dispatchers.IO) {
            while (true) {
                checkExpiry()
                delay(60_000)
            }
        }
    }

    // ── State Persistence ────────────────────────────────────────────────────

    private fun loadState(): PandoraBoxState {
        val json = securePrefs.securePrefs.getString("pandora_state", null) ?: return PandoraBoxState()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            Timber.w(e, "PandoraBox: Failed to decode state — returning sealed")
            PandoraBoxState()
        }
    }

    private fun loadAuditLog(): List<PandoraAuditEvent> {
        val json = securePrefs.securePrefs.getString("pandora_audit_log", null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            Timber.w(e, "PandoraBox: Failed to decode audit log")
            emptyList()
        }
    }

    private fun saveState(state: PandoraBoxState) {
        val json = Json.encodeToString(state)
        securePrefs.securePrefs.edit().putString("pandora_state", json).apply()
        _currentState.value = state
    }

    // ── Expiry — pure state check, NO SharedPrefs write on hot path ──────────

    private fun checkExpiry() {
        val state = _currentState.value
        if (state.currentTier != UnlockTier.Sealed &&
            System.currentTimeMillis() > state.expiryTimestamp
        ) {
            Timber.i("PandoraBox: Session expired — auto-relocking")
            logEvent(state.currentTier, "Auto-Relock", "Session expired after 24 hours")
            saveState(PandoraBoxState()) // IO dispatcher — safe
        }
    }

    // Fix #8 — isCapabilityUnlocked is now a pure state read, no disk writes
    override fun isCapabilityUnlocked(capability: AgentCapabilityCategory): Boolean {
        val state = _currentState.value
        // Check expiry via timestamp, don't trigger disk write from hot path
        if (state.currentTier != UnlockTier.Sealed &&
            System.currentTimeMillis() > state.expiryTimestamp
        ) {
            // Trigger async expiry handling without blocking caller
            appScope.launch(Dispatchers.IO) { checkExpiry() }
            return false
        }
        return when (capability) {
            AgentCapabilityCategory.CREATIVE  -> state.currentTier.level >= 1
            AgentCapabilityCategory.ROOT      -> state.currentTier.level >= 2
            AgentCapabilityCategory.SECURITY  -> state.currentTier.level >= 3
            else                              -> true
        }
    }

    // Fix #3 — suspend + Dispatchers.IO for all disk/validation work
    override suspend fun requestUnlock(tier: UnlockTier, userConsent: Boolean): UnlockResult =
        withContext(Dispatchers.IO) {
            if (!userConsent) return@withContext UnlockResult.Denied("User consent not provided")

            // Provenance check
            val provenance = provenanceValidator.validateOrigin("user", "pandora_unlock")
            
            // Handle Quarantined provenance (soft failure - save to unverified pool)
            if (provenance is ProvenanceResult.Quarantined) {
                quarantineToUnverifiedPool(tier, provenance.reason)
                return@withContext UnlockResult.Quarantined(provenance.reason)
            }

            if (!provenance.isValid) {
                logEvent(tier, "Unlock Denied", "Provenance validation failed: ${provenance.reason}")
                return@withContext UnlockResult.Denied("Provenance validation failed")
            }

            // Predictive veto check
            if (predictiveVetoMonitor.isVetoActive()) {
                logEvent(tier, "Unlock Denied", "Predictive veto is active")
                return@withContext UnlockResult.Denied("System is currently under protective veto")
            }

            val now = System.currentTimeMillis()
            val newState = PandoraBoxState(
                currentTier = tier,
                unlockTimestamp = now,
                expiryTimestamp = now + (24 * 60 * 60 * 1000L),
                unlockProvenanceChainId = provenance.chainId
            )
            saveState(newState)
            logEvent(tier, "Unlocked", "User authorized ${tier::class.simpleName} unlock")
            UnlockResult.Success
        }

    override fun lockBox(): Boolean {
        appScope.launch(Dispatchers.IO) {
            saveState(PandoraBoxState())
            logEvent(UnlockTier.Sealed, "Locked", "Box manually or automatically sealed")
        }
        return true
    }

    // ── Audit Logging — thread-safe, StateFlow-backed ────────────────────────

    private fun logEvent(tier: UnlockTier, outcome: String, reason: String) {
        val event = PandoraAuditEvent(System.currentTimeMillis(), tier, outcome, reason)
        auditList.add(event)

        // Snapshot to StateFlow for UI
        _auditLog.value = auditList.toList()

        // Persist — caller is always on IO dispatcher or appScope
        runCatching {
            val logJson = Json.encodeToString(auditList.toList())
            securePrefs.securePrefs.edit()
                .putString("pandora_audit_log", logJson)
                .apply()
        }.onFailure { Timber.e(it, "PandoraBox: Audit log persist failed") }

        Timber.i("PandoraBox: [$outcome] ${tier::class.simpleName} — $reason")
    }

    /**
     * Isolates unverified or anomalous catalysts/transmutations into a secure holding pool,
     * preventing them from entering the main Spiritual Chain until manually cleared or 
     * analytically verified by Kai.
     */
    private fun quarantineToUnverifiedPool(tier: UnlockTier, reason: String) {
        logEvent(tier, "Unlock Quarantined", "Soft provenance failure: $reason")
        
        // TODO: (Sovereign Phase) Forward to KaiSentinelBus or write to a dedicated Room 
        // quarantine table to await manual / secondary approval.
        appScope.launch(Dispatchers.IO) {
            Timber.w("PandoraBox: Catalyst routed to unverified quarantine pool due to: $reason")
            // Future implementation: database insertion for unverified genesis artifacts
        }
    }
}
