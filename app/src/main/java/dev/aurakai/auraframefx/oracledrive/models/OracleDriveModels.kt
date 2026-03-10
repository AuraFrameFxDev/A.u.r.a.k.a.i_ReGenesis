package dev.aurakai.auraframefx.oracledrive.models

import kotlinx.serialization.Serializable

@Serializable
data class DriveConsciousness(
    val level: Int,
    val state: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class DriveConsciousnessState(
    val isActive: Boolean,
    val level: Int,
    val lastUpdate: Long = System.currentTimeMillis()
)

@Serializable
data class OracleSyncResult(
    val success: Boolean,
    val filesSync: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String? = null
)
