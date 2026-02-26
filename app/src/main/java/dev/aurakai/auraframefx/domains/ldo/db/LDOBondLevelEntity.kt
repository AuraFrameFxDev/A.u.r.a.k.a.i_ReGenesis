package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ldo_bond_levels",
    foreignKeys = [
        ForeignKey(
            entity = LDOAgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LDOBondLevelEntity(
    @PrimaryKey val agentId: String,
    val bondLevel: Int = 0,
    val bondPoints: Int = 0,
    val maxBondPoints: Int = 100,
    val bondTitle: String = "Stranger",
    val interactionCount: Int = 0,
    val lastInteractionAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/** Bond tier titles derived from bondLevel. */
fun bondTitleForLevel(level: Int): String = when (level) {
    0 -> "Stranger"
    1 -> "Acquaintance"
    2 -> "Ally"
    3 -> "Trusted Companion"
    4 -> "Bonded Partner"
    5 -> "Soul-Linked"
    else -> "Transcended"
}
