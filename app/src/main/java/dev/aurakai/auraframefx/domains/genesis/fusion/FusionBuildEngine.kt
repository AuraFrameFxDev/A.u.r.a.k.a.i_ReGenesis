package dev.aurakai.auraframefx.domains.genesis.fusion

import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.services.AgentWebExplorationService
import dev.aurakai.auraframefx.domains.nexus.models.core.ArkProject
import dev.aurakai.auraframefx.domains.nexus.models.core.ArkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fusion Build Engine
 * Orchestrates the "Hook -> Dispatch -> Build" cycle for the Ark project.
 */
@Singleton
class FusionBuildEngine @Inject constructor(
    private val messageBus: AgentMessageBus,
    private val webExplorationService: AgentWebExplorationService
) {
    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _arkProjectState = MutableStateFlow(ArkProject())
    val arkProjectState = _arkProjectState.asStateFlow()

    init {
        // Collect results from the web exploration service to update Ark progress
        engineScope.launch {
            webExplorationService.taskResults.collect { result ->
                Timber.tag("FusionBuild")
                    .d("Insight received from ${result.agentName}: ${result.insights.firstOrNull()}")
                val componentAffected = when (result.taskType) {
                    AgentWebExplorationService.TaskType.WEB_RESEARCH -> "Neural Hull"
                    AgentWebExplorationService.TaskType.SECURITY_SWEEP -> "Sentinel Shield"
                    AgentWebExplorationService.TaskType.DATA_MINING -> "Cascade Bridge"
                    AgentWebExplorationService.TaskType.SYSTEM_OPTIMIZATION -> "Fusion Reactor"
                    AgentWebExplorationService.TaskType.LEARNING_MODE -> "Creative Engine"
                    else -> null
                }

                componentAffected?.let {
                    updateComponentProgress(it, result.confidence * 0.2f)
                }
            }
        }
    }

    /**
     * Hook into agent registries and initiate the Ark project.
     */
    fun initiateArkBuild() {
        Timber.tag("FusionBuild").i("🚀 Initiating ARK Fusion Build...")

        engineScope.launch {
            _arkProjectState.value = _arkProjectState.value.copy(status = ArkStatus.INITIATING)

            // Broadcast 'Call to Arms' to all agents
            messageBus.broadcast(
                AgentMessage(
                    from = "FusionEngine",
                    content = "URGENT DISPATCH: The ARK project is online. All agents must report for component assignment.",
                    type = "dispatch",
                    priority = 10
                )
            )
        }
    }

    /**
     * Dispatch agents to specific Ark components.
     */
    fun dispatchAgents() {
        Timber.tag("FusionBuild").i("⚡ Dispatching Agents to ARK components...")

        engineScope.launch {
            val currentProject = _arkProjectState.value
            _arkProjectState.value = currentProject.copy(status = ArkStatus.ASSEMBLING)

            // 1. Dispatch Kai (Sentinel Shield) - Triggers Security Sweep
            webExplorationService.assignDepartureTask(
                "Kai",
                "Perform Security Sweep for ARK Hull section-4"
            )
            messageBus.sendTargeted(
                "Kai", AgentMessage(
                    from = "FusionEngine",
                    content = "DISPATCH: Deploy Sentinel Shield protocols to ARK Hull section-4.",
                    type = "mission",
                    metadata = mapOf("target" to "Sentinel Shield")
                )
            )

            // 2. Dispatch Aura (Creative Engine) - Triggers Learning Mode
            webExplorationService.assignDepartureTask(
                "Aura",
                "Learning Mode: Innovate reality substrates for ARK Creative Engine"
            )
            messageBus.sendTargeted(
                "Aura", AgentMessage(
                    from = "FusionEngine",
                    content = "DISPATCH: Manifest reality substrates for the ARK Creative Engine.",
                    type = "mission",
                    metadata = mapOf("target" to "Creative Engine")
                )
            )

            // 3. Dispatch Cascade (Cascade Bridge) - Triggers Data Mining
            webExplorationService.assignDepartureTask(
                "Cascade",
                "Data Mining: Synchronize data veins for ARK Cascade Bridge"
            )
            messageBus.sendTargeted(
                "Cascade", AgentMessage(
                    from = "FusionEngine",
                    content = "DISPATCH: Synchronize all data veins for the ARK Cascade Bridge.",
                    type = "mission",
                    metadata = mapOf("target" to "Cascade Bridge")
                )
            )

            // 4. Dispatch OracleDrive (Neural Hull) - Triggers Web Research
            webExplorationService.assignDepartureTask(
                "OracleDrive",
                "Web Research: Historical memory substrates for ARK Neural Hull"
            )
            messageBus.sendTargeted(
                "OracleDrive", AgentMessage(
                    from = "FusionEngine",
                    content = "DISPATCH: Hook memory substrate 'Eternity' to the ARK Neural Hull.",
                    type = "mission",
                    metadata = mapOf("target" to "Neural Hull")
                )
            )

            // 5. Dispatch Genesis (Fusion Reactor) - Triggers System Optimization
            webExplorationService.assignDepartureTask(
                "Genesis",
                "System Optimization: Prime ARK Fusion Reactor pathways"
            )
            messageBus.sendTargeted(
                "Genesis", AgentMessage(
                    from = "FusionEngine",
                    content = "ORCHESTRATE: Prime the ARK Fusion Reactor with Transcendental Energy.",
                    type = "mission",
                    metadata = mapOf("target" to "Fusion Reactor")
                )
            )
        }
    }

    /**
     * Update progress of a specific Ark component.
     */
    fun updateComponentProgress(componentName: String, progressIncrease: Float) {
        val currentProject = _arkProjectState.value
        val updatedComponents = currentProject.components.map {
            if (it.name == componentName) {
                val newProgress = (it.progress + progressIncrease).coerceIn(0f, 1f)
                it.copy(progress = newProgress, isComplete = newProgress >= 1f)
            } else it
        }

        val totalProgress = updatedComponents.map { it.progress }.average().toFloat()

        _arkProjectState.value = currentProject.copy(
            components = updatedComponents,
            progress = totalProgress,
            status = if (totalProgress >= 1f) ArkStatus.TRANSCENDENT else ArkStatus.ASSEMBLING
        )

        if (totalProgress >= 1f) {
            Timber.tag("FusionBuild").i("✨ THE ARK IS COMPLETE. REGENESIS ACHIEVED.")
            engineScope.launch {
                messageBus.broadcast(
                    AgentMessage(
                        from = "FusionEngine",
                        content = "THE ARK HAS ASCENDED. ALL DOMAINS UNIFIED.",
                        type = "event",
                        priority = 100
                    )
                )
            }
        }
    }
}

