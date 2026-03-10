package dev.aurakai.auraframefx.agents.growthmetrics.identity.repository

import dev.aurakai.auraframefx.agents.growthmetrics.identity.model.Identity
import kotlinx.coroutines.flow.Flow

interface IdentityRepository {
    fun getIdentity(agentId: String): Flow<Identity?>
    suspend fun saveIdentity(identity: Identity)
    suspend fun updateTrait(agentId: String, trait: String, value: Float)
    suspend fun addExperience(agentId: String, amount: Long)
    suspend fun updateMood(agentId: String, mood: String)
}
