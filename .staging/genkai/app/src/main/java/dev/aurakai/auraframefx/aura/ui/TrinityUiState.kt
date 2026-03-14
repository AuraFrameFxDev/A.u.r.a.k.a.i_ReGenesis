package dev.aurakai.auraframefx.aura.ui

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.Theme
import dev.aurakai.auraframefx.models.UserData

/**
 * Represents the UI state for the Trinity system.
 */
sealed interface TrinityUiState {
    /**
     * Represents the loading state when data is being fetched.
     */
    object Loading : TrinityUiState

    /**
     * Represents the error state with an error message.
     *
     * @property message The error message to display.
     */
    data class Error(val message: String) : TrinityUiState

    /**
     * Represents the processing state when an agent request is being processed.
     */
    object Processing : TrinityUiState

    /**
     * Represents the success state with the loaded data.
     *
     * @property user The current user data.
     * @property agentStatus A map of agent types to their statuses.
     * @property availableThemes The list of available themes.
     * @property lastAgentResponse The last response from an agent.
     * @property lastAgentType The type of the last agent that responded.
     */
    data class Success(
        val user: UserData? = null,
        val agentStatus: Map<String, dev.aurakai.auraframefx.models.AgentStatus> = emptyMap(),
        val availableThemes: List<Theme> = emptyList(),
        val lastAgentResponse: AgentResponse? = null,
        val lastAgentType: String? = null,
    ) : TrinityUiState
}
