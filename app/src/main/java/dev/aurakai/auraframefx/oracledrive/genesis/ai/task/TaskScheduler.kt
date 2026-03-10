package dev.aurakai.auraframefx.oracledrive.genesis.ai.task

import dev.aurakai.auraframefx.ai.task.TaskPriority
import dev.aurakai.auraframefx.ai.task.TaskStatus

/**
 * Genesis Task Scheduler
 * Schedules and manages AI processing tasks
 */
interface TaskScheduler {

    /**
     * Schedules a task for execution
     */
    fun scheduleTask(task: AITask): String

    /**
     * Cancels a scheduled task
     */
    fun cancelTask(taskId: String): Boolean

    /**
     * Gets task status
     */
    fun getTaskStatus(taskId: String): TaskStatus?

    /**
     * Lists all active tasks
     */
    fun getActiveTasks(): List<AITask>

    /**
     * Gets task history
     */
    fun getTaskHistory(): List<AITask>

    /**
     * Clears completed tasks
     */
    fun clearCompletedTasks()
}

data class AITask(
    val id: String,
    val type: String,
    val payload: Map<String, Any>,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledFor: Long? = null,
    val completedAt: Long? = null,
    val result: String? = null,
    val error: String? = null
)

// TaskPriority and TaskStatus are imported from TaskModel.kt in the same package
// TaskPriority is a data class with value, reason, and metadata
// TaskStatus is an enum with PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED, BLOCKED, WAITING
