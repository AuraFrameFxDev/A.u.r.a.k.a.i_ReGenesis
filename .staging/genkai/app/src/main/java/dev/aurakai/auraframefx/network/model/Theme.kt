package dev.aurakai.auraframefx.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val id: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = false,
    val colors: ThemeColors? = null,
    val styles: Map<String, String> = emptyMap(),
)

@Serializable
data class ThemeColors(
    val primary: String,
    val secondary: String,
    val background: String,
    val surface: String,
    val error: String,
    val onPrimary: String,
    val onSecondary: String,
    val onBackground: String,
    val onSurface: String,
    val onError: String,
)
