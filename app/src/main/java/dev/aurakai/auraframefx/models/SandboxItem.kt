package dev.aurakai.auraframefx.models

data class SandboxItem(
    val id: String,
    val name: String,
    val description: String = "",
    val assetPath: String = ""
)
