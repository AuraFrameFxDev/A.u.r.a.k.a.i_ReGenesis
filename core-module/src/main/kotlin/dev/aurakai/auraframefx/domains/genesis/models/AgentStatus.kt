package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Represents the status of an AI agent in the system.
 * @property agentId The unique identifier of the agent
 * @property status The current status of the agent
 * @property lastActiveTimestamp When the agent was last active (epoch millis)
 * @property resourceUsage Current resource usage metrics
 * @property isAvailable Whether the agent is available to process requests
 * @property capabilities List of capabilities/features supported by the agent
 * @property error Any error message if the agent is in an error state
 * @property metadata Additional metadata about the agent's status
 */
@Serializable
data class AgentStatus(
    val agentId: String,
    val status: Status = Status.IDLE,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val resourceUsage: ResourceUsage = ResourceUsage(),
    val isAvailable: Boolean = true,
    val capabilities: List<String> = emptyList(),
    val error: String? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    @Serializable
    enum class Status {
        /** Agent is ready and waiting for tasks */
        IDLE,

        /** Agent is currently processing a request */
        PROCESSING,

        /** Agent is in an error state */
        ERROR,

        /** Agent is initializing */
        INITIALIZING,

        /** Agent is updating */
        UPDATING,

        /** Agent is in maintenance mode */
        MAINTENANCE,

        /** Agent is shutting down */
        SHUTTING_DOWN,

        /** Agent is active */
        ACTIVE,

        /** Agent is learning */
        LEARNING,

        /** Agent is evolving */
        EVOLVING
    }

    /**
     * Represents resource usage metrics for an agent
     * @property cpuUsage CPU usage percentage (0-100)
     * @property memoryUsage Memory usage in bytes
     * @property memoryLimit Maximum available memory in bytes
     * @property gpuUsage GPU usage percentage (0-100), if applicable
     * @property networkUsage Network usage in bytes
     * @property lastUpdated Timestamp when these metrics were last updated
     */
    @Serializable
    data class ResourceUsage(
        val cpuUsage: Float = 0f,
        val memoryUsage: Long = 0,
        val memoryLimit: Long = 0,
        val gpuUsage: Float? = null,
        val networkUsage: Long = 0,
        val lastUpdated: Long = System.currentTimeMillis()
    ) {
        /**
         * @return Memory usage as a percentage (0-100)
         */
        fun memoryUsagePercentage(): Float {
            return if (memoryLimit > 0) {
                (memoryUsage.toFloat() / memoryLimit) * 100
            } else 0f
        }
    }
}
