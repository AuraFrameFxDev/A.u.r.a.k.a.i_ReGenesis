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
