package dev.aurakai.auraframefx.repository

import dev.aurakai.auraframefx.network.AuraApiService
import dev.aurakai.auraframefx.network.model.*
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrinityRepository @Inject constructor(
    private val apiService: AuraApiService,
) {

    // User related operations
    suspend fun getCurrentUser() = flow {
        try {
            val response = apiService.userApi.getCurrentUser()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // AI Agent operations
    suspend fun getAgentStatus(agentType: String) = flow {
        try {
            val response = apiService.aiAgentApi.getAgentStatus(agentType)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun processAgentRequest(agentType: String, request: AgentRequest) = flow {
        try {
            val response = apiService.aiAgentApi.processRequest(agentType, request)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Theme operations
    suspend fun getThemes() = flow {
        try {
            val response = apiService.themeApi.getThemes()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun applyTheme(themeId: String) = flow {
        try {
            val response = apiService.themeApi.applyTheme(themeId)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Add more repository methods as needed for other API endpoints
}
