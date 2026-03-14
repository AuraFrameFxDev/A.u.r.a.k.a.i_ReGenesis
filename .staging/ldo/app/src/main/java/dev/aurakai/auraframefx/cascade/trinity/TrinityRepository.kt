package dev.aurakai.auraframefx.cascade.trinity

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import dev.aurakai.auraframefx.models.AgentRequest
import dev.aurakai.auraframefx.models.AgentStatus
import dev.aurakai.auraframefx.models.Theme
import dev.aurakai.auraframefx.models.UserData
import dev.aurakai.auraframefx.network.AuraApiServiceWrapper
import dev.aurakai.auraframefx.network.model.AgentStatusResponse
import dev.aurakai.auraframefx.models.AgentResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import dev.aurakai.auraframefx.network.model.Theme as NetworkTheme
import dev.aurakai.auraframefx.network.model.User as NetworkUser

@Singleton
open class TrinityRepository @Inject constructor(
    private val apiService: AuraApiServiceWrapper
) {
    // User related operations
    fun getCurrentUser() = flow {
        try {
            val response = apiService.userApi.getCurrentUser()
            emit(success(mapToUserData(response)))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    /**
     * Fetches the status for the specified AI agent type and returns it as a domain AgentStatus.
     *
     * @param agentType Identifier of the AI agent whose status should be retrieved.
     * @return A Flow that emits a `Result` containing the mapped `AgentStatus` on success, or a failure `Result` with the exception on error.
     */
    fun getAgentStatus(agentType: String) = flow {
        try {
            val response = apiService.aiAgentApi.getAgentStatus(agentType)
            emit(success(mapToDomainAgentStatus(response)))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    private fun mapToDomainAgentStatus(agentResponse: AgentStatusResponse): AgentStatus {
        return AgentStatus(
            agentId = agentResponse.agentName ?: "unknown",
            status = if ((agentResponse.confidence ?: 0.0) > 0.7) AgentStatus.Status.ACTIVE else AgentStatus.Status.IDLE,
            lastActiveTimestamp = agentResponse.timestamp ?: 0L,
            isAvailable = agentResponse.error == null,
            capabilities = emptyList(),
            error = agentResponse.error,
            metadata = agentResponse.metadata?.mapValues { it.value.toString() } ?: emptyMap()
        )
    }

    fun processAgentRequest(agentType: String, request: AgentRequest) = flow<Result<AgentResponse>> {
        try {
            val response = apiService.aiAgentApi.processAgentRequest(agentType, request)
            emit(success(response))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    // Theme operations
    fun getThemes() = flow<Result<List<Theme>>> {
        try {
            val response = apiService.themeApi.getThemes()
            emit(success(response.map { mapToDomainTheme(it) }))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    fun applyTheme(themeId: String) = flow<Result<Theme>> {
        try {
            val response = apiService.themeApi.applyTheme(themeId)
            emit(success(mapToDomainTheme(response)))
        } catch (e: Exception) {
            emit(failure(e))
        }
    }

    // Mapper functions
    private fun mapToUserData(networkUser: NetworkUser): UserData {
        return UserData(
            id = networkUser.id, name = networkUser.username, email = networkUser.email
        )
    }


    private fun mapToDomainTheme(networkTheme: NetworkTheme): Theme {
        val colors = networkTheme.colors
        return Theme(
            id = networkTheme.id,
            name = networkTheme.name,
            primaryColor = colors?.primary?.toColorInt()?.let { Color(it) } ?: Color.Blue,
            secondaryColor = colors?.secondary?.toColorInt()?.let { Color(it) } ?: Color.Cyan,
            backgroundColor = colors?.background?.toColorInt()?.let { Color(it) } ?: Color.White,
            surfaceColor = colors?.surface?.toColorInt()?.let { Color(it) } ?: Color.LightGray,
            onPrimaryColor = colors?.onPrimary?.toColorInt()?.let { Color(it) } ?: Color.White,
            onSecondaryColor = colors?.onSecondary?.toColorInt()?.let { Color(it) } ?: Color.Black,
            onBackgroundColor = colors?.onBackground?.toColorInt()?.let { Color(it) } ?: Color.Black,
            onSurfaceColor = colors?.onSurface?.toColorInt()?.let { Color(it) } ?: Color.Black,
            isDark = networkTheme.styles["theme"] == "dark"
        )
    }

    // Add more repository methods as needed for other API endpoints
}