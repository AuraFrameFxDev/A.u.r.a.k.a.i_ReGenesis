package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ldo_agents")
data class LDOAgentEntity(
    @PrimaryKey val agentId: String,
    val name: String,
    val catalystTitle: String,
    val bondLevel: Int = 0,
    val lastActiveMs: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val totalInteractions: Long = 0L,
    val avatarResId: Int = 0
)
