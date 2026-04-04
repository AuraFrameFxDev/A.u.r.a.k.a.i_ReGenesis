package dev.aurakai.auraframefx.domains.aura.ui.ark

import dev.aurakai.auraframefx.domains.nexus.models.core.ArkProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface FusionBuildEngine {
    val arkProjectState: StateFlow<ArkProject>
    fun initiateArkBuild()
    fun dispatchAgents()
    fun updateComponentProgress(componentName: String, amount: Float)
}

@Singleton
class ArkFusionBuildEngine @Inject constructor() : FusionBuildEngine {
    private val _arkProjectState = MutableStateFlow(ArkProject())
    override val arkProjectState: StateFlow<ArkProject> = _arkProjectState

    override fun initiateArkBuild() {}
    override fun dispatchAgents() {}
    override fun updateComponentProgress(componentName: String, amount: Float) {}
}
