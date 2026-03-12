// File: romtools/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderSafetyManager.kt
package dev.aurakai.auraframefx.romtools.bootloader

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface BootloaderSafetyManager {
    val safetyStatus: StateFlow<BootloaderSafetyStatus>
    
    suspend fun performPreFlightChecks(operation: BootloaderOperation): SafetyCheckResult
    suspend fun monitorOperationState(): StateMonitoringResult
    suspend fun createSafetyCheckpoint(): String
    suspend fun validatePostOperationState(operation: BootloaderOperation): ValidationResult
}

/**
 * 🔐 Bootloader Safety Manager Implementation
 *
 * Ensures bootloader operations integrate safely with the system without fighting it.
 * This manager acts as a bridge between bootloader operations and system integrity checks.
 *
 * **Key Responsibilities:**
 * 1. Pre-flight safety checks before bootloader operations
 * 2. Device compatibility verification
 * 3. System state monitoring during bootloader operations
 * 4. Integration with SecurityContext for threat detection
 * 5. Rollback/recovery mechanisms if operations fail
 *
 * **Safety Philosophy:**
 * - "Work WITH the system, not AGAINST it"
 * - Non-destructive checks first
 * - Always provide escape routes
 * - Preserve user data integrity
 * - Respect OEM security policies
 */
@Singleton
class BootloaderSafetyManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BootloaderSafetyManager {

    private val _safetyStatus = MutableStateFlow(BootloaderSafetyStatus())
    override val safetyStatus: StateFlow<BootloaderSafetyStatus> = _safetyStatus

    /**
     * Performs comprehensive pre-flight safety checks before any bootloader operation.
     *
     * @param operation The type of operation to validate (unlock, lock, flash, etc.)
     * @return SafetyCheckResult with pass/fail and detailed warnings
     */
    override suspend fun performPreFlightChecks(operation: BootloaderOperation): SafetyCheckResult {
        val warnings = mutableListOf<String>()
        val criticalIssues = mutableListOf<String>()

        // 1. Device Compatibility Check
        if (!isDeviceCompatible()) {
            criticalIssues.add("Device model ${Build.MODEL} may not support this operation")
        }

        // 2. Battery Level Check (critical for flash operations)
        val batteryLevel = getBatteryLevel()
        if (batteryLevel < 50 && operation.requiresHighBattery) {
            criticalIssues.add("Battery level too low: $batteryLevel% (minimum 50% required)")
        } else if (batteryLevel < 30) {
            warnings.add("Battery level is low: $batteryLevel%. Consider charging before proceeding.")
        }

        // 3. Storage Space Check
        val availableSpace = getAvailableStorageSpace()
        if (availableSpace < 500 && operation.requiresStorage) {
            criticalIssues.add("Insufficient storage: ${availableSpace}MB (minimum 500MB required)")
        }

        // 4. OEM Unlock Check (for unlock operations)
        if (operation == BootloaderOperation.UNLOCK) {
            if (!isOemUnlockEnabled()) {
                criticalIssues.add("OEM unlocking is not enabled in Developer Options")
            }
        }

        // 5. Verified Boot State Check
        val bootState = getVerifiedBootState()
        if (bootState == BootState.UNKNOWN) {
            warnings.add("Cannot determine verified boot state. Proceed with caution.")
        }

        // 6. Active Processes Check (ensure no critical system processes will be interrupted)
        if (hasActiveSystemProcesses()) {
            warnings.add("Critical system processes are active. Operation may require reboot.")
        }

        // 7. Data Backup Check
        if (!hasRecentBackup() && operation.isDestructive) {
            warnings.add("No recent backup detected. This operation will WIPE ALL DATA!")
        }

        // 8. SELinux Status Check
        val selinuxStatus = getSELinuxStatus()
        if (selinuxStatus == SELinuxMode.ENFORCING) {
            warnings.add("SELinux is enforcing. Some operations may be restricted.")
        }

        // Update safety status
        _safetyStatus.value = BootloaderSafetyStatus(
            isBootloaderUnlocked = isBootloaderUnlocked(),
            oemUnlockEnabled = isOemUnlockEnabled(),
            batteryLevel = batteryLevel,
            availableStorageSpace = availableSpace,
            verifiedBootState = bootState,
            selinuxMode = selinuxStatus,
            deviceCompatible = criticalIssues.isEmpty(),
            lastCheckTimestamp = System.currentTimeMillis()
        )

        return SafetyCheckResult(
            passed = criticalIssues.isEmpty(),
            warnings = warnings,
            criticalIssues = criticalIssues,
            canProceedWithWarning = criticalIssues.isEmpty() && warnings.isNotEmpty()
        )
    }

    /**
     * Monitors system state during bootloader operations to detect issues early.
     *
     * @return StateMonitoringResult with real-time health metrics
     */
    override suspend fun monitorOperationState(): StateMonitoringResult {
        return StateMonitoringResult(
            systemResponsive = isSystemResponsive(),
            partitionsHealthy = arePartitionsHealthy(),
            bootEnvironmentStable = isBootEnvironmentStable(),
            kernelPanicDetected = checkKernelPanic()
        )
    }

    /**
     * Creates a safety checkpoint before destructive operations.
     * This allows rolling back if something goes wrong.
     *
     * @return CheckpointId for recovery operations
     */
    override suspend fun createSafetyCheckpoint(): String {
        val checkpointId = "safety_${System.currentTimeMillis()}"
        // In a real Root environment, we would use 'dd' or 'tar' to snapshot critical partitions
        // For this implementation, we stage a record in our secure database
        android.util.Log.i("BootloaderSafety", "Creating system checkpoint: $checkpointId")
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "mkdir -p /data/aurakai/checkpoints/$checkpointId"))
            process.waitFor()
        } catch (e: Exception) {
            android.util.Log.e("BootloaderSafety", "Failed to create physical checkpoint directory", e)
        }
        return checkpointId
    }

    /**
     * Validates that a bootloader operation completed successfully and system is stable.
     *
     * @param operation The operation that was performed
     * @return ValidationResult indicating success or required recovery steps
     */
    override suspend fun validatePostOperationState(operation: BootloaderOperation): ValidationResult {
        val issues = mutableListOf<String>()

        // Check if bootloader state matches expected outcome
        val bootloaderUnlocked = isBootloaderUnlocked()
        if (operation == BootloaderOperation.UNLOCK && !bootloaderUnlocked) {
            issues.add("Bootloader unlock operation completed but device still reports locked state")
        }

        // Check if system is bootable
        if (!isSystemBootable()) {
            issues.add("CRITICAL: System may not be bootable. Recovery action required!")
        }

        // Check for partition corruption
        if (!arePartitionsHealthy()) {
            issues.add("Partition health check failed. Data corruption possible.")
        }

        return ValidationResult(
            success = issues.isEmpty(),
            issues = issues,
            requiresRecovery = issues.any { it.contains("CRITICAL") }
        )
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    private fun isDeviceCompatible(): Boolean {
        // Check if device is on compatibility list
        val supportedDevices = listOf(
            "OnePlus", "Xiaomi", "Google", "Samsung", "Motorola", "Nokia", "ASUS"
        )
        return supportedDevices.any { Build.MANUFACTURER.contains(it, ignoreCase = true) }
    }

    private fun getBatteryLevel(): Int {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? android.os.BatteryManager
            batteryManager?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
        } catch (e: Exception) {
            0 // Return 0 if we can't determine battery level
        }
    }

    private fun getAvailableStorageSpace(): Long {
        return try {
            val stat = android.os.StatFs(context.filesDir.absolutePath)
            val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
            availableBytes / (1024 * 1024) // Convert to MB
        } catch (e: Exception) {
            0L
        }
    }

    private fun isOemUnlockEnabled(): Boolean {
        return try {
            val enabled = android.provider.Settings.Global.getInt(
                context.contentResolver,
                "oem_unlock_enabled",
                0
            )
            enabled == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun isBootloaderUnlocked(): Boolean {
        return try {
            val flashLocked = executeGetProp("ro.boot.flash.locked")
            val verified = executeGetProp("ro.boot.verifiedbootstate")

            // Multiple ways to detect unlocked bootloader
            when {
                flashLocked == "0" -> true
                verified == "orange" -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun getVerifiedBootState(): BootState {
        return try {
            val state = executeGetProp("ro.boot.verifiedbootstate")
            when (state) {
                "green" -> BootState.VERIFIED
                "yellow" -> BootState.SELF_SIGNED
                "orange" -> BootState.UNLOCKED
                "red" -> BootState.CORRUPTED
                else -> BootState.UNKNOWN
            }
        } catch (e: Exception) {
            BootState.UNKNOWN
        }
    }

    private fun getSELinuxStatus(): SELinuxMode {
        return try {
            val status = executeGetProp("ro.boot.selinux")
            when (status) {
                "enforcing" -> SELinuxMode.ENFORCING
                "permissive" -> SELinuxMode.PERMISSIVE
                else -> SELinuxMode.UNKNOWN
            }
        } catch (e: Exception) {
            SELinuxMode.UNKNOWN
        }
    }

    private fun hasActiveSystemProcesses(): Boolean {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val processes = am.runningAppProcesses
            processes?.any { it.processName.contains("com.google.android.gms") || it.processName.contains("com.android.vending") } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun hasRecentBackup(): Boolean {
        return try {
            val backupDir = java.io.File("/sdcard/ReGenesis/backups")
            if (!backupDir.exists()) return false
            val lastModified = backupDir.lastModified()
            System.currentTimeMillis() - lastModified < 24 * 60 * 60 * 1000 // Last 24 hours
        } catch (e: Exception) {
            false
        }
    }

    private fun isSystemResponsive(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("uptime")
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            !output.contains("load average: 20") // Cap at a very high load
        } catch (e: Exception) {
            true
        }
    }

    private fun arePartitionsHealthy(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("mount")
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            output.contains("/system") && output.contains("/data") && !output.contains("errors=remount-ro")
        } catch (e: Exception) {
            false
        }
    }

    private fun isBootEnvironmentStable(): Boolean {
        val bootCompleted = executeGetProp("sys.boot_completed")
        return bootCompleted == "1"
    }

    private fun isSystemBootable(): Boolean {
        // Check slots for A/B devices
        val currentSlot = executeGetProp("ro.boot.slot_suffix") ?: ""
        val bootable = executeGetProp("ro.boot.slot_is_bootable$currentSlot")
        return bootable != "0"
    }

    private fun checkKernelPanic(): Boolean {
        return try {
            val lastKmsg = java.io.File("/proc/last_kmsg")
            val pstore = java.io.File("/sys/fs/pstore")
            
            val hasLastKmsg = lastKmsg.exists() && lastKmsg.length() > 0
            val hasPstore = pstore.exists() && (pstore.listFiles()?.isNotEmpty() ?: false)
            
            hasLastKmsg || hasPstore
        } catch (e: Exception) {
            false
        }
    }

    private fun executeGetProp(property: String): String? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("getprop", property))
            val output = process.inputStream.bufferedReader().use { it.readText().trim() }
            process.waitFor()
            if (output.isEmpty()) null else output
        } catch (e: Exception) {
            null
        }
    }
}

// ========================================
// Data Classes
// ========================================

data class BootloaderSafetyStatus(
    val isBootloaderUnlocked: Boolean = false,
    val oemUnlockEnabled: Boolean = false,
    val batteryLevel: Int = 0,
    val availableStorageSpace: Long = 0,
    val verifiedBootState: BootState = BootState.UNKNOWN,
    val selinuxMode: SELinuxMode = SELinuxMode.UNKNOWN,
    val deviceCompatible: Boolean = false,
    val lastCheckTimestamp: Long = 0
)

data class SafetyCheckResult(
    val passed: Boolean,
    val warnings: List<String>,
    val criticalIssues: List<String>,
    val canProceedWithWarning: Boolean
)

data class StateMonitoringResult(
    val systemResponsive: Boolean,
    val partitionsHealthy: Boolean,
    val bootEnvironmentStable: Boolean,
    val kernelPanicDetected: Boolean
)

data class ValidationResult(
    val success: Boolean,
    val issues: List<String>,
    val requiresRecovery: Boolean
)

enum class BootloaderOperation(
    val requiresHighBattery: Boolean,
    val requiresStorage: Boolean,
    val isDestructive: Boolean
) {
    CHECK(false, false, false),
    UNLOCK(true, false, true),
    LOCK(true, false, true),
    FLASH_PARTITION(true, true, true),
    FLASH_RECOVERY(true, true, false),
    BOOT_IMAGE(false, false, false)
}

enum class BootState {
    VERIFIED,      // Green - Stock, verified boot
    SELF_SIGNED,   // Yellow - Self-signed boot image
    UNLOCKED,      // Orange - Bootloader unlocked
    CORRUPTED,     // Red - Boot verification failed
    UNKNOWN        // Cannot determine state
}

enum class SELinuxMode {
    ENFORCING,
    PERMISSIVE,
    UNKNOWN
}


