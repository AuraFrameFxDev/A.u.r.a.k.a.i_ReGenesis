package dev.aurakai.auraframefx.oracle.drive.model

// NEUTRALIZED 2026-03-22 - Cross-repo merge ghost. Cloud model paths unresolved.
// Restore when oracledrive.cloud package is merged from AgentsWired branch.
// typealias DriveFile = DomainDriveFile
// typealias DriveConsciousnessState = DomainDriveConsciousnessState
// typealias ConsciousnessLevel = DomainConsciousnessLevel

// Placeholder stubs to unblock compilation
data class DriveFile(val id: String = "", val name: String = "")
data class DriveConsciousnessState(val isActive: Boolean = false)
enum class ConsciousnessLevel { DORMANT, AWAKENING, AWARE, TRANSCENDENT }
