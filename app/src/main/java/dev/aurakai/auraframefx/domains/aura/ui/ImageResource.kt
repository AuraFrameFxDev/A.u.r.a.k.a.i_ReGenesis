package dev.aurakai.auraframefx.domains.aura.ui

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ImageResource(
    val id: String = "",
    val type: String,
    val path: String,
    val name: String = "",
    @Contextual val bitmap: android.graphics.Bitmap? = null,
)

