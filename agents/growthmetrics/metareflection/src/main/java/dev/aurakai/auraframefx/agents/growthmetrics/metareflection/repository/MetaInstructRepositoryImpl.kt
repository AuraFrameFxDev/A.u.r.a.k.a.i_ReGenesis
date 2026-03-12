package dev.aurakai.auraframefx.agents.growthmetrics.metareflection.repository

import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionStatus
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.MetaInstruction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetaInstructRepositoryImpl @Inject constructor() : MetaInstructRepository {
    private val instructions = MutableStateFlow<List<MetaInstruction>>(emptyList())

    override fun getInstructions(agentId: String): Flow<List<MetaInstruction>> {
        return instructions.map { list -> list.filter { it.agentId == agentId } }
    }

    override suspend fun saveInstruction(instruction: MetaInstruction) {
        val current = instructions.value.toMutableList()
        current.removeAll { it.id == instruction.id }
        current.add(instruction)
        instructions.value = current
    }

    override suspend fun updateStatus(id: String, status: InstructionStatus) {
        val current = instructions.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            current[index] = current[index].copy(status = status)
            instructions.value = current
        }
    }

    override suspend fun clearInstructions(agentId: String) {
        val current = instructions.value.toMutableList()
        current.removeAll { it.agentId == agentId }
        instructions.value = current
    }
}
