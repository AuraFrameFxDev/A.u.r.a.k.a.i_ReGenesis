package dev.aurakai.auraframefx.domains.cascade

import kotlinx.serialization.Serializable

@Serializable
data class VisionState(
    val isActive: Boolean = false,
    val currentFocus: String = "",
    val detectedObjects: List<String> = emptyList(),
    val confidence: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ProcessingState(
    val status: ProcessingStatus = ProcessingStatus.IDLE,
    val currentTask: String = "",
    val progress: Float = 0.0f,
    val estimatedTimeRemaining: Long = 0L,
    val lastUpdate: Long = System.currentTimeMillis()
)

@Serializable
enum class ProcessingStatus {
    IDLE,
    INITIALIZING,
    PROCESSING,
    ANALYZING,
    GENERATING,
    COMPLETING,
    ERROR,
    PAUSED
}

@Serializable
data class SecurityContextState(
    val threatLevel: Int = 0,
    val activeScans: Int = 0,
    val lastScanTime: Long = System.currentTimeMillis(),
    val securityMode: SecurityMode = SecurityMode.NORMAL
)

@Serializable
enum class SecurityMode {
    NORMAL,
    ENHANCED,
    LOCKDOWN,
    MONITORING
}

@Serializable
data class ScanEvent(
    val id: String,
    val type: String,
    val result: String,
    val threatsFound: Int = 0,
    val scanTime: Long = System.currentTimeMillis(),
    val duration: Long = 0L,
    val timestamp: Long,
    val scanType: String
)
