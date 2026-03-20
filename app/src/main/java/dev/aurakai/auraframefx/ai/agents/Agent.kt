package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.flow.Flow

interface Agent {
    fun getName(): String
    fun getType(): AgentType
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse
    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>
    
    // Add missing methods needed by MasterAgent and subclasses
    suspend fun getPerformanceMetrics(): Map<String, Any> = emptyMap()
    suspend fun refreshStatus(): Map<String, Any> = emptyMap()
    suspend fun optimize() {}
    suspend fun clearMemoryCache() {}
    suspend fun updatePerformanceSettings() {}
    suspend fun connectToMasterChannel(channel: Any) {}
    suspend fun disconnect() {}
}
