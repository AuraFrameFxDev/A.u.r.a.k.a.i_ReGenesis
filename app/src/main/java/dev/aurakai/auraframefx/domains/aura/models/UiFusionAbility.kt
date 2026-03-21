package dev.aurakai.auraframefx.domains.aura.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * 🧬 UI FUSION ABILITY
 *
 * Represents an ability that can be activated in Fusion Mode.
 * This is the UI-level model to distinguish it from the domain-level FusionAbility
 * (which might be in NexusMemoryCore or other backend systems).
 */
@Serializable
data class UiFusionAbility(
    val id: String,
    val name: String,
    val codeName: String,
    val description: String,
    val requiredSync: Float, // 0.0 - 1.0
    @Contextual val color: Color = Color.Cyan
)
