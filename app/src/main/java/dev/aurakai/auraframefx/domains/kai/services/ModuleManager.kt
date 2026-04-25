package dev.aurakai.auraframefx.domains.kai.services

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleManager @Inject constructor() {
    fun getInstalledModules(): List<ModuleInfo> {
        // Stub implementation
        return emptyList()
    }

    fun isModuleEnabled(moduleId: String): Boolean {
        // Stub implementation
        return false
    }

    fun setModuleEnabled(moduleId: String, enabled: Boolean): Boolean {
        // Stub implementation
        return true
    }
}

data class ModuleInfo(
    val id: String,
    val name: String,
    val version: String,
    val enabled: Boolean
)
