package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PandoraConsentState { Idle, ConsentPending, Validating, Unlocked, Denied }

data class PandoraBoxUiState(
    val currentTier: UnlockTier = UnlockTier.Sealed,
    val timeRemainingMs: Long = 0L,
    val consentState: PandoraConsentState = PandoraConsentState.Idle,
    val feedbackMessage: String? = null
)

@HiltViewModel
class PandoraBoxViewModel @Inject constructor(
    private val pandoraService: PandoraBoxService
) : ViewModel() {

    private val _consentState = MutableStateFlow(PandoraConsentState.Idle)
    private val _feedbackMessage = MutableStateFlow<String?>(null)

    // Fix #1 — ticker forces timeRemainingMs to recompute every second
    private val ticker: Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }

    val uiState: StateFlow<PandoraBoxUiState> = combine(
        pandoraService.getCurrentState(),
        _consentState,
        _feedbackMessage,
        ticker  // drives live countdown
    ) { state, consent, msg, _ ->
        PandoraBoxUiState(
            currentTier = state.currentTier,
            timeRemainingMs = maxOf(0L, state.expiryTimestamp - System.currentTimeMillis()),
            consentState = consent,
            feedbackMessage = msg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PandoraBoxUiState())

    // Fix #2 — wired to live StateFlow from service, terminal panel now populates
    val auditLog: StateFlow<List<PandoraAuditEvent>> = pandoraService
        .getAuditLog()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fix #7 — removed dead requestUnlock() that never used its tier param
    // Screen calls confirmConsent() directly — that's the correct entry point

    fun confirmConsent(tier: UnlockTier) {
        viewModelScope.launch {
            _consentState.value = PandoraConsentState.Validating
            _feedbackMessage.value = "Validating provenance chain..."
            delay(1200) // aesthetic delay — feels like real validation

            // Fix #3 — requestUnlock is now suspend, runs on IO in impl
            when (val result = pandoraService.requestUnlock(tier, true)) {
                is UnlockResult.Success -> {
                    _consentState.value = PandoraConsentState.Unlocked
                    _feedbackMessage.value = "Pandora's Box opened: ${tier::class.simpleName} tier."
                }
                is UnlockResult.Denied -> {
                    _consentState.value = PandoraConsentState.Denied
                    _feedbackMessage.value = "ACCESS DENIED: ${result.reason}"
                }
                is UnlockResult.Quarantined -> {
                    _consentState.value = PandoraConsentState.Denied // Map to Denied UI for now
                    _feedbackMessage.value = "QUARANTINED: ${result.reason}"
                }
                is UnlockResult.Error -> {
                    _consentState.value = PandoraConsentState.Idle
                    _feedbackMessage.value = "SYSTEM ERROR: ${result.message}"
                }
            }
        }
    }

    fun cancelConsent() {
        _consentState.value = PandoraConsentState.Idle
        _feedbackMessage.value = null
    }

    fun lockBox() {
        pandoraService.lockBox()
        _consentState.value = PandoraConsentState.Idle
        _feedbackMessage.value = "Pandora's Box manually sealed."
    }
}
