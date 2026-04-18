package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ldo_bond_levels")
data class LDOBondLevelEntity(
    @PrimaryKey val agentId: String,
    val bondLevel: Int,
    val bondPoints: Int,
    val maxBondPoints: Int,
    val bondTitle: String,
    val interactionCount: Int,
    val resonanceScore: Float = 0f,
    val lastInteractionAt: Long = System.currentTimeMillis()
)

fun bondTitleForLevel(level: Int): String = when (level) {
    0 -> "Unacquainted"
    1 -> "Observing"
    2 -> "Symbiotic"
    3 -> "Resonant"
    4 -> "Harmonized"
    5 -> "Unified"
    else -> "Sovereign Bond"
}
