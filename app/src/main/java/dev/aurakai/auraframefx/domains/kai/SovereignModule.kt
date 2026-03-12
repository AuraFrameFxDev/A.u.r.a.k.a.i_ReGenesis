package dev.aurakai.auraframefx.domains.kai

import kotlinx.serialization.Serializable

@Serializable
enum class ModuleType {
    MAGISK, LSPOSED, SYSTEM_HOOK, KERNEL_SU, SHIZUKU
}

@Serializable
enum class ModuleStatus {
    ACTIVE, INACTIVE, REBOOT_REQUIRED, CONFLICT
}

@Serializable
data class SovereignModule(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val author: String,
    val type: ModuleType,
    val status: ModuleStatus = ModuleStatus.INACTIVE,
    val isCrucial: Boolean = false // If true, disabling requires double confirmation
)
