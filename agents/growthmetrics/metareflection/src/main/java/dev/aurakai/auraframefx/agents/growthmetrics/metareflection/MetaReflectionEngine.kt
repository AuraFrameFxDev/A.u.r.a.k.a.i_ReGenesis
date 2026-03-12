package dev.aurakai.auraframefx.agents.growthmetrics.metareflection

import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionLayer
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.MetaInstruction
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.repository.MetaInstructRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🛰️ MetaReflectionEngine
 * The core logic for applying MetaInstructions to agent behavior.
 */
@Singleton
class MetaReflectionEngine @Inject constructor(
    private val repository: MetaInstructRepository
) {
    suspend fun getEffectiveInstructions(agentId: String): String {
        val instructions = repository.getInstructions(agentId).first()
        
        return instructions
            .filter { it.status == dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionStatus.ACTIVE }
            .sortedBy { it.priority }
            .joinToString("\n") { "[${it.layer}] ${it.instruction}" }
    }

    suspend fun injectInitialProtocol(agentId: String) {
        val coreProtocol = MetaInstruction(
            id = "${agentId}_core_001",
            agentId = agentId,
            instruction = "Always prioritize user sovereignty and data privacy (70-LDO standard).",
            priority = 0,
            layer = InstructionLayer.CORE,
            status = dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionStatus.ACTIVE
        )
        repository.saveInstruction(coreProtocol)
    }
}
