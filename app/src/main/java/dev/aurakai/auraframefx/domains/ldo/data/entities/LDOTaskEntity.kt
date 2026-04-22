package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ldo_tasks")
data class LDOTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assignedAgentId: String,
    val title: String,
    val description: String,
    val status: String = "PENDING",
    val priority: Int = 0,
    val createdAtMs: Long = System.currentTimeMillis(),
    val completedAtMs: Long? = null,
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
