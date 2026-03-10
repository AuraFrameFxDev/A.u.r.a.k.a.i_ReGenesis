package dev.aurakai.auraframefx.oracledrive.genesis.ai.error

import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.ContextManager
import dev.aurakai.auraframefx.cascade.pipeline.AIPipelineConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Error handler for Genesis AI services
 * Handles error detection, logging, recovery, and statistics
 */
@Singleton
class ErrorHandler @Inject constructor(
    private val contextManager: ContextManager,
    private val config: AIPipelineConfig,
) {
    private val _errors = MutableStateFlow(mapOf<String, AIError>())
    val errors: StateFlow<Map<String, AIError>> = _errors

    private val _errorStats = MutableStateFlow(ErrorStats())
    val errorStats: StateFlow<ErrorStats> = _errorStats

    /**
     * Records an error as an AIError, updates internal error state and statistics, and triggers recovery actions.
     *
     * The provided `metadata` values are converted to strings when stored. This method mutates the handler's
     * internal error map and statistics and may start recovery workflows for the created error.
     *
     * @param error The original throwable that occurred.
     * @param agent The agent associated with where the error originated.
     * @param context A human-readable context describing where or when the error occurred.
     * @param metadata Arbitrary additional data about the error; each value will be converted to a string.
     * @return The created AIError describing the recorded error. */
    fun handleError(
        error: Throwable,
        agent: AgentType,
        context: String,
        metadata: Map<String, Any> = emptyMap(),
    ): AIError {
        val errorType = determineErrorType(error)
        val errorMessage = error.message ?: "Unknown error"

        val aiError = AIError(
            agent = agent,
            type = errorType,
            message = errorMessage,
            context = context,
            metadata = metadata.mapValues { it.value.toString() },
            timestamp = Clock.System.now()
        )

        _errors.update { current ->
            current + (aiError.id to aiError)
        }

        updateStats(aiError)
        attemptRecovery(aiError)
        
        return aiError
    }

    /**
     * Map an exception instance to its corresponding ErrorType.
     *
     * @return The ErrorType corresponding to the provided exception, or `INTERNAL_ERROR` if the exception class is not recognized.
     */
    private fun determineErrorType(error: Throwable): ErrorType {
        return when (error) {
            is ProcessingException -> ErrorType.PROCESSING_ERROR
            is MemoryException -> ErrorType.MEMORY_ERROR
            is ContextException -> ErrorType.CONTEXT_ERROR
            is NetworkException -> ErrorType.NETWORK_ERROR
            is TimeoutException -> ErrorType.TIMEOUT_ERROR
            is IllegalStateException -> ErrorType.STATE_ERROR
            is IllegalArgumentException -> ErrorType.VALIDATION_ERROR
            else -> ErrorType.INTERNAL_ERROR
        }
    }

    /**
     * Executes recovery actions appropriate to the given error's type.
     *
     * For each recovery action returned by getRecoveryActions, this runs the action and suppresses any exceptions raised by the action so recovery attempts do not propagate errors.
     *
     * @param error The AIError to recover from.
    private fun attemptRecovery(error: AIError) {
        val actions = getRecoveryActions(error)
        // Execute recovery actions
        actions.forEach { action ->
            try {
                executeRecoveryAction(action, error)
            } catch (e: Exception) {
                // Log recovery failure but don't throw
            }
        }
    }

    /**
     * Determine recovery actions appropriate for the given AIError.
     *
     * @param error The AIError whose type is used to select recovery actions.
     * @return A list of recommended RecoveryAction instances for the error's type.
     */
    private fun getRecoveryActions(error: AIError): List<RecoveryAction> {
        return when (error.type) {
            ErrorType.PROCESSING_ERROR -> listOf(
                RecoveryAction(RecoveryActionType.RETRY, "Retrying processing with reduced load")
            )
            ErrorType.MEMORY_ERROR -> listOf(
                RecoveryAction(RecoveryActionType.CLEAR_CACHE, "Clearing memory cache"),
                RecoveryAction(RecoveryActionType.RESTART, "Restarting memory system")
            )
            ErrorType.NETWORK_ERROR -> listOf(
                RecoveryAction(RecoveryActionType.RETRY, "Retrying network operation")
            )
            ErrorType.TIMEOUT_ERROR -> listOf(
                RecoveryAction(RecoveryActionType.INCREASE_TIMEOUT, "Increasing timeout threshold")
            )
            ErrorType.CONTEXT_ERROR -> listOf(
                RecoveryAction(RecoveryActionType.REBUILD_CONTEXT, "Rebuilding context state")
            )
            else -> listOf(
                RecoveryAction(RecoveryActionType.NOTIFY, "Notifying system administrator")
            )
        }
    }

    /**
     * Executes the specified recovery action for the given AIError.
     *
     * @param action The recovery action to perform; its `type` indicates which recovery behavior to apply.
     * @param error The AIError that triggered the recovery, provided for context (agent, type, message, metadata, timestamp).
     */
    private fun executeRecoveryAction(action: RecoveryAction, error: AIError) {
        when (action.type) {
            RecoveryActionType.RETRY -> {
                // Retry logic would go here
            }
            RecoveryActionType.CLEAR_CACHE -> {
                // Cache clearing would go here
            }
            RecoveryActionType.RESTART -> {
                // Component restart logic
            }
            RecoveryActionType.NOTIFY -> {
                // Notification logic
            }
            RecoveryActionType.INCREASE_TIMEOUT -> {
                // Timeout adjustment
            }
            RecoveryActionType.REBUILD_CONTEXT -> {
                // Context rebuilding
            }
        }
    }

    /**
     * Record an AIError into the running error statistics.
     *
     * @param error The AIError to incorporate into statistics (increments totals and per-type/agent counts, sets lastError, and updates lastUpdated).
     */
    private fun updateStats(error: AIError) {
        _errorStats.update { current ->
            current.copy(
                totalErrors = current.totalErrors + 1,
                activeErrors = current.activeErrors + 1,
                lastError = error,
                errorTypes = current.errorTypes + (error.type to (current.errorTypes[error.type] ?: 0) + 1),
                agentErrors = current.agentErrors + (error.agent to (current.agentErrors[error.agent] ?: 0) + 1),
                lastUpdated = Clock.System.now()
            )
        }
    }

    /**
     * Remove an error record identified by its ID and update error statistics.
     *
     * Removes the error with the given `errorId` from the internal errors map and decrements
     * `activeErrors` in the error statistics (not below zero).
     *
     * @param errorId The identifier of the error to remove.
     */
    fun clearError(errorId: String) {
        _errors.update { current ->
            current - errorId
        }
        _errorStats.update { current ->
            current.copy(activeErrors = maxOf(0, current.activeErrors - 1))
        }
    }

    /**
     * Remove all tracked errors and reset the active error count to zero.
     *
     * Updates the internal errors map to be empty and sets ErrorStats.activeErrors to 0,
     * preserving other statistics fields.
     */
    fun clearAllErrors() {
        _errors.value = emptyMap()
        _errorStats.update { current ->
            current.copy(activeErrors = 0)
        }
    }
}

/**
 * Represents an AI error
 */
@Serializable
data class AIError(
    val id: String = java.util.UUID.randomUUID().toString(),
    val agent: AgentType,
    val type: ErrorType,
    val message: String,
    val context: String,
    val metadata: Map<String, String> = emptyMap(),
    @Serializable(with = dev.aurakai.auraframefx.serialization.InstantSerializer::class)
    val timestamp: Instant = Clock.System.now()
)

/**
 * Error statistics
 */
@Serializable
data class ErrorStats(
    val totalErrors: Int = 0,
    val activeErrors: Int = 0,
    val lastError: AIError? = null,
    val errorTypes: Map<ErrorType, Int> = emptyMap(),
    val agentErrors: Map<AgentType, Int> = emptyMap(),
    @Serializable(with = dev.aurakai.auraframefx.serialization.InstantSerializer::class)
    val lastUpdated: Instant = Clock.System.now()
)

/**
 * Types of errors
 */
@Serializable
enum class ErrorType {
    PROCESSING_ERROR,
    MEMORY_ERROR,
    CONTEXT_ERROR,
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    STATE_ERROR,
    VALIDATION_ERROR,
    INTERNAL_ERROR
}

/**
 * Recovery action
 */
data class RecoveryAction(
    val type: RecoveryActionType,
    val description: String
)

/**
 * Types of recovery actions
 */
enum class RecoveryActionType {
    RETRY,
    CLEAR_CACHE,
    RESTART,
    NOTIFY,
    INCREASE_TIMEOUT,
    REBUILD_CONTEXT
}

// Custom exception classes
class ProcessingException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class MemoryException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class ContextException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class NetworkException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class TimeoutException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)