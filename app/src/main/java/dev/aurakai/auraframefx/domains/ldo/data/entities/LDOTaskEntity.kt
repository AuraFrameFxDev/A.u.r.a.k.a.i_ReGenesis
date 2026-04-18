package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ldo_tasks")
data class LDOTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assignedAgentId: String,
    val title: String,
    val description: String,
    val status: String = LDOTaskStatus.PENDING,
    val priority: Int = LDOTaskPriority.MEDIUM,
    val category: String = "general",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val phaseIndex: Int = 0
)

object LDOTaskStatus {
    const val PENDING = "PENDING"
    const val IN_PROGRESS = "IN_PROGRESS"
    const val COMPLETED = "COMPLETED"
    const val FAILED = "FAILED"
    const val BLOCKED = "BLOCKED"
}

object LDOTaskPriority {
    const val LOW = 1
    const val MEDIUM = 2
    const val HIGH = 3
    const val CRITICAL = 4
}
