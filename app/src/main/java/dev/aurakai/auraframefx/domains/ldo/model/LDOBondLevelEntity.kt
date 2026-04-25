package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOBondLevelEntity — Entity representing bond levels between agents
 */
data class LDOBondLevelEntity(
    val id: String,
    val agentId1: String,
    val agentId2: String,
    val level: Int = 0,
    val title: String = ""
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
