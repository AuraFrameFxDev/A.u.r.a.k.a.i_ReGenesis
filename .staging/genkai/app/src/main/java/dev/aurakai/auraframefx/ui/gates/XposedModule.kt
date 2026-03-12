package dev.aurakai.auraframefx.ui.gates

data class XposedModule(
    val name: String,
    val packageName: String,
    val version: String,
    val enabled: Boolean,
    val hookCount: Int,
    val scope: String
)
