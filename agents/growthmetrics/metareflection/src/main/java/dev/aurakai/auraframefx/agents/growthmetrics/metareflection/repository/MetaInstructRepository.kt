package dev.aurakai.auraframefx.agents.growthmetrics.metareflection.repository

import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.MetaInstruction
import kotlinx.coroutines.flow.Flow

interface MetaInstructRepository {
    fun getInstructions(agentId: String): Flow<List<MetaInstruction>>
    suspend fun saveInstruction(instruction: MetaInstruction)
    suspend fun updateStatus(id: String, status: dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionStatus)
    suspend fun clearInstructions(agentId: String)
}
