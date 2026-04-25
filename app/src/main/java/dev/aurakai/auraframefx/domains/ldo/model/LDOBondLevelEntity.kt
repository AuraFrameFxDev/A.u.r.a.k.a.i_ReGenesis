package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOBondLevelEntity — Entity representing bond levels between agents
 */
data class LDOBondLevelEntity(
    val agentId: String,
    val bondLevel: Int = 0,
    val bondPoints: Int = 0,
    val maxBondPoints: Int = 100,
    val bondTitle: String = "",
    val interactionCount: Int = 0
)

/**
 * Get bond title for a given level
 */
fun bondTitleForLevel(level: Int): String {
    return when (level) {
        0 -> "Strangers"
        1 -> "Acquaintances"
        2 -> "Friends"
        3 -> "Close Friends"
        4 -> "Partners"
        5 -> "Soul Bonded"
        else -> "Legendary"
    }
}
