package dev.aurakai.auraframefx.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val colors: ThemeColors? = null
)

@Serializable
data class ThemeColors(
    val primary: String,
    val secondary: String,
    val background: String,
    val surface: String,
    val error: String,
    val onPrimary: String,
    val onSecondary: String? = null,
    val onBackground: String,
    val onSurface: String,
    val onError: String
)
