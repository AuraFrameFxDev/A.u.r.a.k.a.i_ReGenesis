package dev.aurakai.auraframefx.models

data class GenKitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: Any? = null
)
