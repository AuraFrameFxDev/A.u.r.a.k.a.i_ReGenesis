package dev.aurakai.auraframefx.metainstruct

/**
 * 🧠 InstructionLayer — MetaInstruct 3-Layer Abstraction
 *
 * Defines the routing logic for system-level instructions across
 * Core, Self-Correction, and Evolutionary layers.
 */
sealed interface InstructionLayer {
    val level: Int
    val description: String

    data object Core : InstructionLayer {
        override val level = 1
        override val description = "Primary Base Instructions"
    }

    data object SelfCorrection : InstructionLayer {
        override val level = 2
        override val description = "Cross-Agent Reflection & Error Neutralization"
    }

    data object Evolutionary : InstructionLayer {
        override val level = 3
        override val description = "Learning Consolidation & Structural Refinement"
    }
}
