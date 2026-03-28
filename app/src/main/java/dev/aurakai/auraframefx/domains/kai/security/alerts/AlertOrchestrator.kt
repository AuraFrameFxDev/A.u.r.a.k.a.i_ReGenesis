package dev.aurakai.auraframefx.domains.kai.security.alerts

import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.sovereignty.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class AlertOrchestrator @Inject constructor(
    private val sentinelBus: KaiSentinelBus,
    private val emailNotifier: EmailIntentNotifier,
    @ApplicationScope private val targetScope: CoroutineScope
) {
    // Basic Cooldown + Thresholds to avoid overwhelming user Intent stack
    private var lastAlertTime = 0L
    private val COOLDOWN_MS = 60_000L // 1 min per alert default, adjust as needed
    private var dailyAlertCount = 0
    private val DAILY_CAP = 10

    fun startOrchestrator() {
        Timber.i("🛡️ System Alert Orchestrator Spin Up.")
        observeSovereignState()
        // Could technically add heartbeat observation here or thermal observing
    }

    private fun observeSovereignState() {
        sentinelBus.sovereignFlow.onEach { statePacket ->
            // if it enters a frozen / freezing state
            if (statePacket.state == KaiSentinelBus.SovereignState.FROZEN || 
                statePacket.state == KaiSentinelBus.SovereignState.FREEZING) {
                dispatch(AlertEvent.SovereignFreeze("Automated Network Disconnect - Shields Raised."))
            }
        }.launchIn(targetScope)
    }

    // Externally callable for manual triggers (e.g. from Quarantine)
    fun dispatch(event: AlertEvent, extraContext: Map<String, String> = emptyMap()) {
        val now = System.currentTimeMillis()
        if (now - lastAlertTime < COOLDOWN_MS && event.priority != AlertPriority.CRITICAL) {
            Timber.d("Rate limited. Skipping event \${event.name}")
            return
        }
        
        if (dailyAlertCount >= DAILY_CAP && event.priority != AlertPriority.CRITICAL) {
            Timber.d("Daily Cap Hit. Skiping event \${event.name}")
            return
        }

        lastAlertTime = now
        dailyAlertCount++

        val defaultMetadata = mapOf(
            "Timestamp" to now.toString(),
            "Sentinel Node" to "KAI-ORCHESTRATOR"
        )

        emailNotifier.notifyViaEmail(event, defaultMetadata + extraContext)
    }
}
