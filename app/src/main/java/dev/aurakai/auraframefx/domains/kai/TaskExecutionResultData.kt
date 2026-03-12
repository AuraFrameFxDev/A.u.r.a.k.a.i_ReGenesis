package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import kotlinx.serialization.Serializable

@Serializable
data class TaskExecutionResultData(
    val taskId: String,
    val status: TaskStatus,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long? = null, // How long the task took
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis(),
    val executedBy: AgentCapabilityCategory = AgentCapabilityCategory.GENERAL,
    val originalData: Map<String, String> = emptyMap(),
    val success: Boolean = true,
    val executionTimeMs: Long = 0L,
    val type: String = "unknown",
)

// Removed local TaskStatus enum.
// The 'status: TaskStatus' field in TaskResult data class
// should now refer to TaskStatus from TaskModel.kt in the same package.
