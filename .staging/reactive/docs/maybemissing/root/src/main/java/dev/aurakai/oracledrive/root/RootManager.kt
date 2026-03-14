package dev.aurakai.oracledrive.root

import com.topjohnwu.superuser.Shell
import dev.aurakai.oracledrive.bootloader.BootloaderManager
import dev.aurakai.oracledrive.root.apatch.APatchManager
import dev.aurakai.oracledrive.root.magisk.MagiskCompatLayer
import dev.aurakai.oracledrive.root.kernelsu.KernelSUManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 🔱 **ROOT MANAGER - ORACLE DRIVE CORE** 🔱
 *
 * Universal root management system that coordinates between:
 * - **APatch** - Kernel patching foundation
 * - **Magisk** - Systemless root compatibility
 * - **KernelSU** - Kernel-space granular control
 *
 * This is the heart of Oracle Drive's revolutionary hybrid root system.
 *
 * **Architecture:**
 * ```
 * RootManager (Intelligent Router)
 *     ↓
 * ├── APatchManager (Primary: Kernel patching)
 * ├── MagiskCompatLayer (Secondary: Module support)
 * └── KernelSUManager (Advanced: Per-app grants)
 * ```
 *
 * **Key Features:**
 * - Auto-detection of installed root solution
 * - Fallback chain for maximum compatibility
 * - Unified API regardless of backend
 * - Trinity AI integration for intelligent decisions
 *
 * @see APatchManager
 * @see MagiskCompatLayer
 * @see KernelSUManager
 */
class RootManager {

    private val apatchManager = APatchManager()
    private val magiskCompat = MagiskCompatLayer()
    private val kernelsuManager = KernelSUManager()
    private val bootloaderManager = BootloaderManager()

    /**
     * Initialize root management system.
     *
     * Detects available root solutions and configures the backend.
     */
    suspend fun initialize(): RootInitResult = withContext(Dispatchers.IO) {
        try {
            Timber.i("RootManager: Initializing Oracle Drive root system...")

            // Check if bootloader is unlocked (required for custom root)
            val bootloaderStatus = bootloaderManager.checkBootloaderStatus()
            if (!bootloaderStatus.isUnlocked) {
                Timber.w("RootManager: Bootloader is locked - root not possible")
                return@withContext RootInitResult(
                    success = false,
                    method = RootMethod.NONE,
                    message = "Bootloader must be unlocked for root access"
                )
            }

            // Detect available root solutions
            val detectionResult = detectRootMethod()

            when (detectionResult.method) {
                RootMethod.APATCH -> {
                    Timber.i("RootManager: APatch detected - using kernel patching")
                    apatchManager.initialize()
                }
                RootMethod.MAGISK -> {
                    Timber.i("RootManager: Magisk detected - using compatibility layer")
                    magiskCompat.initialize()
                }
                RootMethod.KERNELSU -> {
                    Timber.i("RootManager: KernelSU detected - using kernel hooks")
                    kernelsuManager.initialize()
                }
                RootMethod.NONE -> {
                    Timber.w("RootManager: No root solution detected")
                }
            }

            RootInitResult(
                success = detectionResult.method != RootMethod.NONE,
                method = detectionResult.method,
                message = detectionResult.message
            )
        } catch (e: Exception) {
            Timber.e(e, "RootManager: Initialization failed")
            RootInitResult(
                success = false,
                method = RootMethod.NONE,
                message = "Initialization error: ${e.message}"
            )
        }
    }

    /**
     * Detect which root method is installed.
     *
     * Checks in order of preference:
     * 1. APatch (preferred - modern kernel patching)
     * 2. KernelSU (advanced - kernel-space control)
     * 3. Magisk (fallback - wide compatibility)
     */
    private suspend fun detectRootMethod(): RootDetectionResult = withContext(Dispatchers.IO) {
        try {
            // Check for APatch
            val apatchCheck = Shell.cmd("which apd").exec()
            if (apatchCheck.isSuccess && apatchCheck.out.isNotEmpty()) {
                return@withContext RootDetectionResult(
                    method = RootMethod.APATCH,
                    version = apatchManager.getVersion(),
                    message = "APatch detected"
                )
            }

            // Check for KernelSU
            val ksuCheck = Shell.cmd("which ksud").exec()
            if (ksuCheck.isSuccess && ksuCheck.out.isNotEmpty()) {
                return@withContext RootDetectionResult(
                    method = RootMethod.KERNELSU,
                    version = kernelsuManager.getVersion(),
                    message = "KernelSU detected"
                )
            }

            // Check for Magisk
            val magiskCheck = Shell.cmd("which magisk").exec()
            if (magiskCheck.isSuccess && magiskCheck.out.isNotEmpty()) {
                return@withContext RootDetectionResult(
                    method = RootMethod.MAGISK,
                    version = magiskCompat.getVersion(),
                    message = "Magisk detected"
                )
            }

            // No root solution found
            RootDetectionResult(
                method = RootMethod.NONE,
                version = null,
                message = "No root solution detected"
            )
        } catch (e: Exception) {
            Timber.e(e, "RootManager: Root detection failed")
            RootDetectionResult(
                method = RootMethod.NONE,
                version = null,
                message = "Detection error: ${e.message}"
            )
        }
    }

    /**
     * Grant root access to an application.
     *
     * Routes to appropriate backend based on detected root method.
     *
     * @param packageName The package name of the app requesting root
     * @param reason Human-readable reason for the grant (for logging/AI analysis)
     * @return RootGrantResult indicating success or failure
     */
    suspend fun grantRoot(packageName: String, reason: String = ""): RootGrantResult {
        val detectedMethod = detectRootMethod().method

        return when (detectedMethod) {
            RootMethod.APATCH -> apatchManager.grantRoot(packageName, reason)
            RootMethod.KERNELSU -> kernelsuManager.grantRoot(packageName, reason)
            RootMethod.MAGISK -> magiskCompat.grantRoot(packageName, reason)
            RootMethod.NONE -> RootGrantResult(
                success = false,
                packageName = packageName,
                method = RootMethod.NONE,
                message = "No root solution available"
            )
        }
    }

    /**
     * Revoke root access from an application.
     */
    suspend fun revokeRoot(packageName: String): RootGrantResult {
        val detectedMethod = detectRootMethod().method

        return when (detectedMethod) {
            RootMethod.APATCH -> apatchManager.revokeRoot(packageName)
            RootMethod.KERNELSU -> kernelsuManager.revokeRoot(packageName)
            RootMethod.MAGISK -> magiskCompat.revokeRoot(packageName)
            RootMethod.NONE -> RootGrantResult(
                success = false,
                packageName = packageName,
                method = RootMethod.NONE,
                message = "No root solution available"
            )
        }
    }

    /**
     * Check if an app has root access.
     */
    suspend fun hasRootAccess(packageName: String): Boolean {
        val detectedMethod = detectRootMethod().method

        return when (detectedMethod) {
            RootMethod.APATCH -> apatchManager.hasRootAccess(packageName)
            RootMethod.KERNELSU -> kernelsuManager.hasRootAccess(packageName)
            RootMethod.MAGISK -> magiskCompat.hasRootAccess(packageName)
            RootMethod.NONE -> false
        }
    }

    /**
     * Get list of all apps with root access.
     */
    suspend fun getRootedApps(): List<String> {
        val detectedMethod = detectRootMethod().method

        return when (detectedMethod) {
            RootMethod.APATCH -> apatchManager.getRootedApps()
            RootMethod.KERNELSU -> kernelsuManager.getRootedApps()
            RootMethod.MAGISK -> magiskCompat.getRootedApps()
            RootMethod.NONE -> emptyList()
        }
    }
}

/**
 * Root method enumeration.
 */
enum class RootMethod {
    APATCH,     // APatch kernel patching
    MAGISK,     // Magisk systemless root
    KERNELSU,   // KernelSU kernel-space
    NONE        // No root detected
}

/**
 * Root initialization result.
 */
data class RootInitResult(
    val success: Boolean,
    val method: RootMethod,
    val message: String
)

/**
 * Root detection result.
 */
data class RootDetectionResult(
    val method: RootMethod,
    val version: String?,
    val message: String
)

/**
 * Root grant/revoke result.
 */
data class RootGrantResult(
    val success: Boolean,
    val packageName: String,
    val method: RootMethod,
    val message: String
)
