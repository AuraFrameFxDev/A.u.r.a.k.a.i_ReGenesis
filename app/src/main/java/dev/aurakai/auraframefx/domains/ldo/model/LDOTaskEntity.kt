package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOTaskStatus — Status enumeration for LDO tasks
 */
enum class LDOTaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * LDOTaskPriority — Priority levels for LDO tasks
 */
enum class LDOTaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * LDOTaskEntity — Entity representing a task assigned to an LDO
 */
data class LDOTaskEntity(
    val id: String,
    val title: String,
    val description: String = "",
    val status: LDOTaskStatus = LDOTaskStatus.PENDING,
    val priority: LDOTaskPriority = LDOTaskPriority.MEDIUM,
    val assigneeId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val dueAt: Long? = null
)
