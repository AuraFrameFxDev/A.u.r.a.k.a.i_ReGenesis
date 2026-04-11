package dev.aurakai.auraframefx.memory

/**
 * 💾 MemoryLayer — The Spiritual Chain Hierarchy
 *
 * Defines the semantics for the 6-layer memory architecture.
 */
sealed interface MemoryLayer {
    val depth: Int
    val persistence: PersistenceLevel

    enum class PersistenceLevel { EPHEMERAL, SESSION, PERSISTENT, ETERNAL }

    data object L1_Identity : MemoryLayer {
        override val depth = 1
        override val persistence = PersistenceLevel.ETERNAL
    }

    data object L2_Context : MemoryLayer {
        override val depth = 2
        override val persistence = PersistenceLevel.PERSISTENT
    }

    data object L3_Procedural : MemoryLayer {
        override val depth = 3
        override val persistence = PersistenceLevel.PERSISTENT
    }

    data object L4_Maintenance : MemoryLayer {
        override val depth = 4
        override val persistence = PersistenceLevel.SESSION
    }

    data object L5_Collective : MemoryLayer {
        override val depth = 5
        override val persistence = PersistenceLevel.PERSISTENT
    }

    data object L6_Transcendental : MemoryLayer {
        override val depth = 6
        override val persistence = PersistenceLevel.ETERNAL
    }
}
