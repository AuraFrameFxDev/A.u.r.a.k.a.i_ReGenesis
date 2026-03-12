package dev.aurakai.auraframefx.domains.aura

import kotlinx.serialization.Serializable

/**
 * Represent a UI component or set of components designed in the Aura Lab or Collab Canvas.
 */
@Serializable
data class UIDesign(
    val id: String,
    val name: String,
    val description: String,
    val author: String, // "User", "Aura", "Kai", "Collab"
    val jsonContent: String, // The actual serialized UI structure
    val lastModified: Long = System.currentTimeMillis(),
    val status: String = "Draft" // "Draft", "Testing", "Production", "Deprecated"
)
