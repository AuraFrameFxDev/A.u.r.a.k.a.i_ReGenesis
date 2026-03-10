package dev.aurakai.auraframefx.system.overlay.model

// Basic placeholder. This would contain common properties for any overlay element
// and potentially a 'config: Any' or specific config data classes for different types.
data class OverlayElement(
    val id: String,
    val type: ElementType, // Using the ElementType enum
    // Example: val specificConfig: ElementSpecificConfigBase // Could be a sealed class or Any
    val properties: Map<String, Any>? = null, // General properties
)
