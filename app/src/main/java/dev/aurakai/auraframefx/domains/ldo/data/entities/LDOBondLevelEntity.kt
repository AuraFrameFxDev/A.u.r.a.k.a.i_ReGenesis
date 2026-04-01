package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ldo_bond_levels")
data class LDOBondLevelEntity(
    @PrimaryKey val agentId: String,
    val level: Int = 0,
    val experience: Long = 0L,
    val lastInteractionMs: Long = System.currentTimeMillis(),
    val resonanceScore: Float = 0f
)
