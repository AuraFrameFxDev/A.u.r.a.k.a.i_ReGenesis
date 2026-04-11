package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.engine.ChromaCoreManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.core.generator.AuraForgeGenerator
import dev.aurakai.auraframefx.domains.genesis.models.Spelhook
import dev.aurakai.auraframefx.domains.genesis.models.SpelhookResult
import dev.aurakai.auraframefx.domains.kai.analysis.GrokAnalysisService
import dev.aurakai.auraframefx.extendsysa.spelhooks.sprites.SpelhookSpriteGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AurasLabViewModel @Inject constructor(
    private val forgeGenerator: AuraForgeGenerator,
    private val spriteGenerator: SpelhookSpriteGenerator,
    private val grokAnalysis: GrokAnalysisService,
    private val chromaManager: ChromaCoreManager,
    private val logger: AuraFxLogger
) : ViewModel() {

    private val _forgeState = MutableStateFlow<ForgeState>(ForgeState.Idle)
    val forgeState: StateFlow<ForgeState> = _forgeState.asStateFlow()

    fun generateAndDeploy(description: String) {
        viewModelScope.launch {
            _forgeState.value = ForgeState.Forging

            // 1. Aura's Forge creates the code
            val result = forgeGenerator.generateSpelhook(description)

            if (result is SpelhookResult.Success) {
                _forgeState.value = ForgeState.Validating(result.spelhook.code)

                // 2. Kai's Grok Analysis validates it
                when (val validation = grokAnalysis.validateSpelhook(result.spelhook.code)) {
                    is GrokAnalysisService.ValidationResult.Approved -> {
                        _forgeState.value = ForgeState.Deploying(result.spelhook.code)

                        // 3. Deployment via ChromaCore (Wielding the Tool)
                        logger.info("AurasLab", "Deploying validated Spelhook")

                        // Simulation of deployment
                        _forgeState.value = ForgeState.Success(result.spelhook.code, validation.notes)
                    }

                    is GrokAnalysisService.ValidationResult.Vetoed -> {
                        _forgeState.value = ForgeState.Error("Kai Vetoed: ${validation.reason}")
                    }
                }
            } else if (result is SpelhookResult.Error) {
                _forgeState.value = ForgeState.Error(result.message)
            }
        }
    }

    fun generateHyperSprite(characterDescription: String) {
        viewModelScope.launch {
            _forgeState.value = ForgeState.ForgingSprite

            val result = spriteGenerator.generateDynamicSprite(characterDescription)

            if (result is SpelhookSpriteGenerator.SpriteSpelhookResult.Success) {
                _forgeState.value = ForgeState.SpriteSuccess(result.spriteSpelhook)
                logger.info("AurasLab", "Hyper-Sprite Forged: ${result.spriteSpelhook.id}")
            } else if (result is SpelhookSpriteGenerator.SpriteSpelhookResult.Error) {
                _forgeState.value = ForgeState.Error(result.message)
            }
        }
    }

    sealed class ForgeState {
        object Idle : ForgeState()
        object Forging : ForgeState()
        object ForgingSprite : ForgeState()
        data class Validating(val code: String) : ForgeState()
        data class Deploying(val code: String) : ForgeState()
        data class Success(val code: String, val message: String) : ForgeState()
        data class SpriteSuccess(val spelhook: Spelhook) : ForgeState()
        data class Error(val message: String) : ForgeState()
    }
}
