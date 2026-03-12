package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.task

import dev.aurakai.auraframefx.domains.genesis.models.TaskStatus
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.task.models.AITask
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTaskScheduler @Inject constructor() : TaskScheduler {

    private val tasks = mutableMapOf<String, AITask>()

    override fun scheduleTask(task: AITask): String {
        val taskId = task.id.ifEmpty { UUID.randomUUID().toString() }
        tasks[taskId] = task.copy(id = taskId, status = TaskStatus.Status.PENDING)
        return taskId
    }

    override fun cancelTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        tasks[taskId] = task.copy(status = TaskStatus.Status.CANCELLED)
        return true
    }

    override fun getTaskStatus(taskId: String): TaskStatus.Status? {
        return tasks[taskId]?.status
    }

    override fun getActiveTasks(): List<AITask> {
        return tasks.values.filter { it.status == TaskStatus.Status.PENDING || it.status == TaskStatus.Status.RUNNING }
    }

    override fun getTaskHistory(): List<AITask> {
        return tasks.values.toList()
    }

    override fun clearCompletedTasks() {
        tasks.entries.removeIf { it.value.status == TaskStatus.Status.COMPLETED || it.value.status == TaskStatus.Status.FAILED || it.value.status == TaskStatus.Status.CANCELLED }
    }
}
