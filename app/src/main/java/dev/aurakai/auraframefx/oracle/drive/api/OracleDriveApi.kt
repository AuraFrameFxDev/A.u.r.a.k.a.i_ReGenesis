package dev.aurakai.auraframefx.oracle.drive.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OracleDriveApi @Inject constructor() {
    data class DriveConsciousness(val intelligenceLevel: Int, val activeAgents: List<String>)
    fun awakeDriveConsciousness(): DriveConsciousness = DriveConsciousness(10, listOf("genesis", "aura", "kai"))
}
