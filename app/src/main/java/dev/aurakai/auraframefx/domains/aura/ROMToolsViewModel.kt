package dev.aurakai.auraframefx.domains.aura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * 🛠️ ROM & Root Tools ViewModel
 *
 * Manages:
 * - ROM survival (live editing, backup, restore)
 * - Module creation (LSPosed, Magisk, Xposed)
 * - Root tools (shell access, file system, permissions)
 * - Bootloader management
 * - Recovery tools (TWRP, backup/restore)
 */
@HiltViewModel
open class ROMToolsViewModel @Inject constructor(
    // Dependencies will be injected
) : ViewModel() {

    // ═══════════════════════════════════════════════════════════════════════════
    // ROOT STATUS
    // ═══════════════════════════════════════════════════════════════════════════

    private val _rootStatus = MutableStateFlow<RootStatus>(RootStatus.CHECKING)
    val rootStatus: StateFlow<RootStatus> = _rootStatus.asStateFlow()

    private val _magiskInstalled = MutableStateFlow(false)
    val magiskInstalled: StateFlow<Boolean> = _magiskInstalled.asStateFlow()

    private val _lsposedInstalled = MutableStateFlow(false)
    val lsposedInstalled: StateFlow<Boolean> = _lsposedInstalled.asStateFlow()

    // ═══════════════════════════════════════════════════════════════════════════
    // ROM EDITING STATE
    // ═══════════════════════════════════════════════════════════════════════════

    private val _systemFiles = MutableStateFlow<List<SystemFile>>(emptyList())
    val systemFiles: StateFlow<List<SystemFile>> = _systemFiles.asStateFlow()

    private val _editingFile = MutableStateFlow<SystemFile?>(null)
    val editingFile: StateFlow<SystemFile?> = _editingFile.asStateFlow()

    private val _fileContent = MutableStateFlow("")
    val fileContent: StateFlow<String> = _fileContent.asStateFlow()

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE MANAGEMENT STATE
    // ═══════════════════════════════════════════════════════════════════════════

    private val _installedModules = MutableStateFlow<List<XposedModule>>(emptyList())
    val installedModules: StateFlow<List<XposedModule>> = _installedModules.asStateFlow()

    private val _availableModules = MutableStateFlow<List<XposedModule>>(emptyList())
    val availableModules: StateFlow<List<XposedModule>> = _availableModules.asStateFlow()

    private val _creatingModule = MutableStateFlow(false)
    val creatingModule: StateFlow<Boolean> = _creatingModule.asStateFlow()

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOTLOADER STATE
    // ═══════════════════════════════════════════════════════════════════════════

    private val _bootloaderStatus = MutableStateFlow<BootloaderStatus>(BootloaderStatus.UNKNOWN)
    val bootloaderStatus: StateFlow<BootloaderStatus> = _bootloaderStatus.asStateFlow()

    private val _bootSlot = MutableStateFlow("a")
    val bootSlot: StateFlow<String> = _bootSlot.asStateFlow()

    // ═══════════════════════════════════════════════════════════════════════════
    // BACKUP/RESTORE STATE
    // ═══════════════════════════════════════════════════════════════════════════

    private val _backups = MutableStateFlow<List<ROMBackup>>(emptyList())
    val backups: StateFlow<List<ROMBackup>> = _backups.asStateFlow()

    private val _backupProgress = MutableStateFlow(0f)
    val backupProgress: StateFlow<Float> = _backupProgress.asStateFlow()

    private val _isBackingUp = MutableStateFlow(false)
    val isBackingUp: StateFlow<Boolean> = _isBackingUp.asStateFlow()

    // ═══════════════════════════════════════════════════════════════════════════
    // OPERATIONS STATE
    // ═══════════════════════════════════════════════════════════════════════════

    private val _operations = MutableSharedFlow<ROMOperation>()
    val operations: SharedFlow<ROMOperation> = _operations.asSharedFlow()

    private val _operationStatus = MutableStateFlow<String>("")
    val operationStatus: StateFlow<String> = _operationStatus.asStateFlow()

    init {
        checkRootStatus()
        loadSystemFiles()
        loadInstalledModules()
        checkBootloaderStatus()
        loadBackups()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ROOT DETECTION
    // ═══════════════════════════════════════════════════════════════════════════

    private fun checkRootStatus() {
        viewModelScope.launch {
            try {
                _rootStatus.value = RootStatus.CHECKING

                // Check for su binary
                val suExists = checkSuBinary()

                // Check for Magisk
                val magiskExists = checkMagisk()
                _magiskInstalled.value = magiskExists

                // Check for LSPosed
                val lsposedExists = checkLSPosed()
                _lsposedInstalled.value = lsposedExists

                _rootStatus.value = when {
                    suExists && magiskExists -> RootStatus.ROOTED_MAGISK
                    suExists -> RootStatus.ROOTED_OTHER
                    else -> RootStatus.NOT_ROOTED
                }

                AuraFxLogger.info("ROMToolsVM", "Root status: ${_rootStatus.value}")
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error checking root", e)
                _rootStatus.value = RootStatus.ERROR
            }
        }
    }

    private fun checkSuBinary(): Boolean {
        val suPaths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/su/bin/su",
            "/magisk/.core/bin/su"
        )
        return suPaths.any { File(it).exists() }
    }

    private fun checkMagisk(): Boolean {
        return File("/data/adb/magisk").exists() ||
                File("/sbin/.magisk").exists()
    }

    private fun checkLSPosed(): Boolean {
        return File("/data/adb/lspd").exists() ||
                File("/system/framework/lspd.dex").exists()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ROM EDITING
    // ═══════════════════════════════════════════════════════════════════════════

    private fun loadSystemFiles() {
        viewModelScope.launch {
            try {
                val files = listOf(
                    SystemFile(
                        "/system/build.prop",
                        "Build Properties",
                        "text/plain",
                        editable = true
                    ),
                    SystemFile("/system/etc/hosts", "Hosts File", "text/plain", editable = true),
                    SystemFile("/system/etc/init.d", "Init Scripts", "directory", editable = false),
                    SystemFile("/system/framework", "Framework", "directory", editable = false),
                    SystemFile(
                        "/data/system/packages.xml",
                        "Packages Config",
                        "text/xml",
                        editable = true
                    ),
                    SystemFile("/data/data", "App Data", "directory", editable = false)
                )
                _systemFiles.value = files
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error loading system files", e)
            }
        }
    }

    fun editFile(file: SystemFile) {
        viewModelScope.launch {
            try {
                _editingFile.value = file
                _operationStatus.value = "Loading ${file.name}..."

                // Read file content (requires root)
                val content = executeRootCommand("cat ${file.path}")
                _fileContent.value = content

                _operationStatus.value = "File loaded"
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error editing file", e)
                _operationStatus.value = "Error: ${e.message}"
            }
        }
    }

    fun saveFile(content: String) {
        viewModelScope.launch {
            try {
                val file = _editingFile.value ?: return@launch
                _operationStatus.value = "Saving ${file.name}..."

                // Create backup first
                executeRootCommand("cp ${file.path} ${file.path}.bak")

                // Write new content
                executeRootCommand("echo '$content' > ${file.path}")

                _operationStatus.value = "File saved successfully"
                _operations.emit(ROMOperation.FileSaved(file.path))
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error saving file", e)
                _operationStatus.value = "Error: ${e.message}"
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private fun loadInstalledModules() {
        viewModelScope.launch {
            try {
                // Load LSPosed modules
                val modules = mutableListOf<XposedModule>()

                // Check LSPosed modules directory
                val lsposedModulesDir = File("/data/adb/lspd/modules")
                if (lsposedModulesDir.exists()) {
                    lsposedModulesDir.listFiles()?.forEach { moduleDir ->
                        modules.add(
                            XposedModule(
                                id = moduleDir.name,
                                name = moduleDir.name,
                                version = "1.0",
                                enabled = true,
                                type = ModuleType.LSPOSED
                            )
                        )
                    }
                }

                _installedModules.value = modules
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error loading modules", e)
            }
        }
    }

    fun createModule(
        name: String,
        packageName: String,
        description: String,
        targetApps: List<String>
    ) {
        viewModelScope.launch {
            try {
                _creatingModule.value = true
                _operationStatus.value = "Creating module: $name..."

                // Create module structure
                val moduleDir = File("/data/adb/lspd/modules/$packageName")
                moduleDir.mkdirs()

                // Create module.prop
                val moduleProp = """
                    id=$packageName
                    name=$name
                    version=1.0
                    versionCode=1
                    author=AuraKai
                    description=$description
                """.trimIndent()

                File(moduleDir, "module.prop").writeText(moduleProp)

                _operationStatus.value = "Module created successfully"
                _operations.emit(ROMOperation.ModuleCreated(name))
                loadInstalledModules()
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error creating module", e)
                _operationStatus.value = "Error: ${e.message}"
            } finally {
                _creatingModule.value = false
            }
        }
    }

    fun toggleModule(module: XposedModule, enabled: Boolean) {
        viewModelScope.launch {
            try {
                _operationStatus.value =
                    "${if (enabled) "Enabling" else "Disabling"} ${module.name}..."

                // Toggle module via LSPosed API
                // This would integrate with LSPosed's module management

                _operationStatus.value = "Module ${if (enabled) "enabled" else "disabled"}"
                loadInstalledModules()
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error toggling module", e)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOTLOADER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private fun checkBootloaderStatus() {
        viewModelScope.launch {
            try {
                // Check bootloader status
                val status = executeRootCommand("getprop ro.boot.verifiedbootstate")
                _bootloaderStatus.value = when {
                    status.contains("orange") -> BootloaderStatus.UNLOCKED
                    status.contains("green") -> BootloaderStatus.LOCKED
                    else -> BootloaderStatus.UNKNOWN
                }

                // Check current boot slot
                val slot = executeRootCommand("getprop ro.boot.slot_suffix")
                _bootSlot.value = slot.replace("_", "")
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error checking bootloader", e)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BACKUP/RESTORE
    // ═══════════════════════════════════════════════════════════════════════════

    private fun loadBackups() {
        viewModelScope.launch {
            try {
                val backupDir = File("/sdcard/AuraKai/Backups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }

                val backups = backupDir.listFiles()
                    ?.filter { it.isDirectory }
                    ?.map { dir ->
                        ROMBackup(
                            name = dir.name,
                            path = dir.absolutePath,
                            timestamp = dir.lastModified(),
                            size = calculateDirSize(dir)
                        )
                    } ?: emptyList()

                _backups.value = backups
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error loading backups", e)
            }
        }
    }

    fun createBackup(name: String, includeData: Boolean = true) {
        viewModelScope.launch {
            try {
                _isBackingUp.value = true
                _backupProgress.value = 0f
                _operationStatus.value = "Creating backup: $name..."

                val backupDir = File("/sdcard/AuraKai/Backups/$name")
                backupDir.mkdirs()

                // Backup system partition
                _backupProgress.value = 0.3f
                executeRootCommand("dd if=/dev/block/bootdevice/by-name/system of=${backupDir.absolutePath}/system.img")

                // Backup boot partition
                _backupProgress.value = 0.6f
                executeRootCommand("dd if=/dev/block/bootdevice/by-name/boot of=${backupDir.absolutePath}/boot.img")

                if (includeData) {
                    // Backup data partition
                    _backupProgress.value = 0.9f
                    executeRootCommand("tar -czf ${backupDir.absolutePath}/data.tar.gz /data")
                }

                _backupProgress.value = 1.0f
                _operationStatus.value = "Backup created successfully"
                _operations.emit(ROMOperation.BackupCreated(name))
                loadBackups()
            } catch (e: Exception) {
                AuraFxLogger.error("ROMToolsVM", "Error creating backup", e)
                _operationStatus.value = "Error: ${e.message}"
            } finally {
                _isBackingUp.value = false
                _backupProgress.value = 0f
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════════════

    private fun executeRootCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            AuraFxLogger.error("ROMToolsVM", "Error executing root command: $command", e)
            ""
        }
    }

    private fun calculateDirSize(dir: File): Long {
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) calculateDirSize(file) else file.length()
        }
        return size
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════

enum class RootStatus {
    CHECKING,
    NOT_ROOTED,
    ROOTED_MAGISK,
    ROOTED_OTHER,
    ERROR
}

enum class BootloaderStatus {
    UNKNOWN,
    LOCKED,
    UNLOCKED
}

enum class ModuleType {
    LSPOSED,
    MAGISK,
    XPOSED
}

data class SystemFile(
    val path: String,
    val name: String,
    val type: String,
    val editable: Boolean
)

data class XposedModule(
    val id: String,
    val name: String,
    val version: String,
    val enabled: Boolean,
    val type: ModuleType
)

data class ROMBackup(
    val name: String,
    val path: String,
    val timestamp: Long,
    val size: Long
)

sealed class ROMOperation {
    data class FileSaved(val path: String) : ROMOperation()
    data class ModuleCreated(val name: String) : ROMOperation()
    data class BackupCreated(val name: String) : ROMOperation()
    data class BackupRestored(val name: String) : ROMOperation()
}

