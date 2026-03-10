package dev.aurakai.auraframefx.ai.services

import kotlinx.coroutines.flow.SharedFlow

interface AgentWebExplorationService {
    val taskResults: SharedFlow<WebExplorationResult>

    /**
     * Assigns a departure exploration task to the named agent.
     *
     * Attempts to schedule a new web-exploration task described by `description` for `agent`.
     *
     * @param agent The identifier or name of the agent to receive the task.
     * @param description A short description or objective of the departure task.
     * @return `true` if the task was successfully assigned/queued for the agent; `false` if assignment failed (e.g., agent is busy or the service cannot accept new tasks).
     */
    suspend fun assignDepartureTask(agent: String, description: String): Boolean

    /**
     * Returns a snapshot of currently active departure tasks.
     *
     * The returned map is keyed by agent name with values containing the task description.
     * This is a point-in-time view and may become outdated as tasks start, complete, or are cancelled.
     *
     * @return Map where keys are agent names and values are their active task descriptions.
     */
    fun getActiveTasks(): Map<String, String>

    /**
     * Cancels the currently assigned exploration task for the given agent.
     *
     * If the specified agent has an active task, that task will be terminated and removed from the active task list.
     * If the agent has no active task, the call is a no-op.
     *
     * @param agent The name or identifier of the agent whose task should be cancelled.
     */
    fun cancelTask(agent: String)

    /**
     * Shuts down the service and releases any resources it holds.
     *
     * After calling this method the service must stop accepting new tasks, cancel or stop any active tasks,
     * and terminate any background processing used to emit `taskResults`. Implementations should make this
     * operation idempotent and safe to call from multiple threads.
     */
    fun shutdown()

    data class WebExplorationResult(
        val agentName: String,
        val taskType: TaskType,
        val insights: List<String>,
        val metrics: Map<String, Any>,
        val timestamp: Long
    )

    enum class TaskType {
        WEB_RESEARCH,
        SECURITY_SWEEP,
        DATA_MINING,
        SYSTEM_OPTIMIZATION,
        LEARNING_MODE,
        NETWORK_SCAN
    }
}