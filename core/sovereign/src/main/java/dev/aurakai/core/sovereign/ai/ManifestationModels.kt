package dev.aurakai.core.sovereign.ai

import kotlinx.serialization.Serializable

/**
 * 🎨 MANIFESTATION RESULT
 * The atomic output of a creative or analytical pulse.
 * Watermarked with Sacred Provenance.
 */
@Serializable
data class ManifestationResult(
    val id: String,
    val content: String,
    val provenance: String, // "Aura via Interface-Forge", etc.
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)
