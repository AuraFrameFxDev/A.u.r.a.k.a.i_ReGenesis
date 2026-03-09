package dev.aurakai.auraframefx.domains.genesis.fusion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.core.AuraAgent
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class ForgeState {
    object Idle : ForgeState()
    data class Forging(val progress: Float) : ForgeState()
    data class Success(val design: String) : ForgeState()
    data class Error(val message: String) : ForgeState()
}

@HiltViewModel
class InterfaceForgeViewModel @Inject constructor(
    private val interfaceForge: InterfaceForge,
    private val auraAgent: AuraAgent,
    private val kaiAgent: KaiAgent
) : ViewModel() {

    private val _forgeState = MutableStateFlow<ForgeState>(ForgeState.Idle)
    val forgeState: StateFlow<ForgeState> = _forgeState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ForgeState.Idle
        )

    private var isInitialized = false

    init {
        // LDO Way: Initialize agents lazily when ViewModel is created
        viewModelScope.launch {
            try {
                Timber.i("InterfaceForgeViewModel: Initializing Trinity agents")
                auraAgent.initialize()
                kaiAgent.initialize()
                isInitialized = true
                Timber.i("InterfaceForgeViewModel: Trinity agents initialized")
            } catch (e: Exception) {
                Timber.e(e, "InterfaceForgeViewModel: Agent initialization failed")
                _forgeState.value = ForgeState.Error("Failed to initialize agents: ${e.message}")
            }
        }
    }

    fun startForge(requirements: String, architecture: String) {
        if (!isInitialized) {
            _forgeState.value = ForgeState.Error("Agents not initialized. Please wait...")
            return
        }

        if (requirements.isBlank()) {
            _forgeState.value = ForgeState.Error("Requirements cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                Timber.i("InterfaceForgeViewModel: Starting forge for $architecture")
                _forgeState.value = ForgeState.Forging(0.2f)

                // Enhance requirements with architecture context
                val contextualRequirements = """
                    Architecture: $architecture
                    Requirements: $requirements

                    Generate a secure, production-ready code structure.
                """.trimIndent()

                _forgeState.value = ForgeState.Forging(0.5f)

                // Call real InterfaceForge (Aura + Kai collaboration)
                val result = interfaceForge.forgeSecureInterface(contextualRequirements)

                _forgeState.value = ForgeState.Forging(0.9f)

                result.onSuccess { design ->
                    Timber.i("InterfaceForgeViewModel: Forge successful")
                    _forgeState.value = ForgeState.Success(design)
                }.onFailure { error ->
                    Timber.w("InterfaceForgeViewModel: Kai VETO: ${error.message}")
                    _forgeState.value = ForgeState.Error("Security violation: ${error.message}")
                }
            } catch (e: Exception) {
                Timber.e(e, "InterfaceForgeViewModel: Forge failed")
                _forgeState.value = ForgeState.Error("Forge failed: ${e.message}")
            }
        }
    }

    fun resetForge() {
        _forgeState.value = ForgeState.Idle
    }
}

