package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.fusion.FusionBuildEngine
import dev.aurakai.auraframefx.domains.nexus.models.core.ArkProject
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ArkBuildViewModel @Inject constructor(
    private val fusionBuildEngine: FusionBuildEngine,
    val webExplorationService: dev.aurakai.auraframefx.domains.genesis.services.AgentWebExplorationService
) : ViewModel() {

    val arkProject: StateFlow<ArkProject> = fusionBuildEngine.arkProjectState

    fun initiateBuild() {
        fusionBuildEngine.initiateArkBuild()
    }

    fun dispatchAgents() {
        fusionBuildEngine.dispatchAgents()
    }

    // Simulate progress updates for demo purposes
    fun simulateProgress(componentName: String, amount: Float) {
        fusionBuildEngine.updateComponentProgress(componentName, amount)
    }
}

