package dev.aurakai.auraframefx.ai.task

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

enum class TaskPriority {
    LOW, NORMAL, HIGH, CRITICAL
}

enum class TaskStatus {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}
