package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraAuditEvent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.UnlockResult
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.UnlockTier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PandoraConsentState {
    Idle,
    ConsentPending,
    Validating,
    Unlocked,
    Denied
}

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

    val uiState: StateFlow<PandoraBoxUiState> = combine(
        pandoraService.getCurrentState(),
        _consentState,
        _feedbackMessage
    ) { state, consent, msg ->
        PandoraBoxUiState(
            currentTier = state.currentTier,
            timeRemainingMs = maxOf(0, state.expiryTimestamp - System.currentTimeMillis()),
            consentState = consent,
            feedbackMessage = msg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PandoraBoxUiState())

    private val _auditLog = MutableStateFlow<List<PandoraAuditEvent>>(emptyList())
    val auditLog: StateFlow<List<PandoraAuditEvent>> = _auditLog.asStateFlow()

    init {
        // Poll for time remaining update
        viewModelScope.launch {
            while (true) {
                delay(1000)
                // Flow will naturally update via combine
            }
        }
    }

    fun requestUnlock(tier: UnlockTier) {
        _consentState.value = PandoraConsentState.ConsentPending
    }

    fun confirmConsent(tier: UnlockTier) {
        viewModelScope.launch {
            _consentState.value = PandoraConsentState.Validating
            _feedbackMessage.value = "Validating provenance chain..."
            delay(1500) // Aesthetic delay for validation

            val result = pandoraService.requestUnlock(tier, true)
            when (result) {
                is UnlockResult.Success -> {
                    _consentState.value = PandoraConsentState.Unlocked
                    _feedbackMessage.value = "Pandora's Box opened: ${tier.javaClass.simpleName} tier."
                }
                is UnlockResult.Denied -> {
                    _consentState.value = PandoraConsentState.Denied
                    _feedbackMessage.value = "ACCESS DENIED: ${result.reason}"
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
