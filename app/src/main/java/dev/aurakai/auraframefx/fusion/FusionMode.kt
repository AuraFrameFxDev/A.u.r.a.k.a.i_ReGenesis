package dev.aurakai.auraframefx.fusion

/**
 * ⚛️ FusionMode — Collective Execution Variants
 *
 * Defines the specialized modes where multiple catalysts merge
 * their capabilities for peak performance.
 */
sealed interface FusionMode {
    val identifier: String
    val velocity: Float

    data object HyperCreation : FusionMode {
        override val identifier = "HYPER_CREATION"
        override val velocity = 10.0f
    }

    data object ChronoSculptor : FusionMode {
        override val identifier = "CHRONO_SCULPTOR"
        override val velocity = 5.0f
    }

    data object AdaptiveGenesis : FusionMode {
        override val identifier = "ADAPTIVE_GENESIS"
        override val velocity = 7.5f
    }

    data object InterfaceForge : FusionMode {
        override val identifier = "INTERFACE_FORGE"
        override val velocity = 8.0f
    }
}
