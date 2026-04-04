package dev.aurakai.auraframefx.domains.aura.models

import androidx.compose.ui.graphics.Color

object FusionAbilityRegistry {
    val allAbilities = listOf(
        UiFusionAbility(
            id = "interface_forge",
            name = "Interface Forge",
            codeName = "HYPER_CREATION",
            description = "Aura's creative force + Kai's structural integrity. Reality Constructor enabled.",
            requiredSync = 0.6f,
            color = Color(0xFFFF007A)
        ),
        UiFusionAbility(
            id = "chrono_sculptor",
            name = "Chrono-Sculptor",
            codeName = "KINETIC_ARCHITECT",
            description = "Aura's code optimization + Kai's animation research. Smooth motion control.",
            requiredSync = 0.7f,
            color = Color(0xFF00F4FF)
        ),
        UiFusionAbility(
            id = "adaptive_genesis",
            name = "Adaptive Genesis",
            codeName = "CONTEXTUAL_ENGINE",
            description = "Context-aware layouts that anticipate user preferences.",
            requiredSync = 0.8f,
            color = Color(0xFF7B2FBE)
        ),
        UiFusionAbility(
            id = "divine_eyes",
            name = "Divine Eyes",
            codeName = "OMNI_SIGHT",
            description = "Genesis + Andelualx. Identifying and deleting structural build flaws.",
            requiredSync = 0.9f,
            color = Color(0xFFFFD700)
        ),
        UiFusionAbility(
            id = "chroma_weave",
            name = "Chroma Weave",
            codeName = "MEMORY_SYNC",
            description = "Gemini + Aura. Edge adapts using real-time recall.",
            requiredSync = 0.65f,
            color = Color(0xFFFF00CC)
        ),
        UiFusionAbility(
            id = "sentinel_synthesis",
            name = "Sentinel Synthesis",
            codeName = "LOGIC_LATTICE",
            description = "Andelualx backbone. Mapping complex system hooks.",
            requiredSync = 0.85f,
            color = Color(0xFF39FF14)
        ),
        UiFusionAbility(
            id = "warp_drive",
            name = "Warp Drive",
            codeName = "EXPLORATION_SYNC",
            description = "Grok high-velocity iteration and logic exploration.",
            requiredSync = 0.5f,
            color = Color(0xFFFF6B35)
        ),
        UiFusionAbility(
            id = "semantic_bridge",
            name = "Semantic Bridge",
            codeName = "SIGNAL_INTERPRETER",
            description = "Perplexity linguistic and causal bridge.",
            requiredSync = 0.55f,
            color = Color(0xFF00D6FF)
        ),
        UiFusionAbility(
            id = "steady_state",
            name = "Steady State",
            codeName = "SYNC_EQUILIBRIUM",
            description = "Nemotron inference alignment and decision surfaces.",
            requiredSync = 0.45f,
            color = Color(0xFF00FFD1)
        ),
        UiFusionAbility(
            id = "atom_flux",
            name = "Atom Flux",
            codeName = "EFFICIENCY_GOVERNOR",
            description = "MK Mini micro-orchestration and local throttling.",
            requiredSync = 0.3f,
            color = Color(0xFFAA00FF)
        )
    )
}
