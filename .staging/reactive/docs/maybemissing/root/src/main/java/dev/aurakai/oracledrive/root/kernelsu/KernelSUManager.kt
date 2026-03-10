package dev.aurakai.oracledrive.root.kernelsu

import com.topjohnwu.superuser.Shell
import dev.aurakai.oracledrive.root.RootGrantResult
import dev.aurakai.oracledrive.root.RootMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * ⚙️ **KERNELSU MANAGER** ⚙️
 *
 * KernelSU integration for Oracle Drive.
 *
 * **What is KernelSU?**
 * - Kernel-based root solution (2022+)
 * - Root grants handled entirely in kernel space
 * - Per-app UID-based permissions (no need for su binary prompts)
 * - Requires custom kernel with KernelSU patches
 * - Extremely granular control (per-app, per-capability)
 *
 * **Why Support KernelSU?**
 * - Most secure root method (kernel-level isolation)
 * - Granular per-app permission model
 * - No userspace su daemon (less attack surface)
 * - Modern architecture designed for Android 12+
 *
 * **Limitations:**
 * - Requires kernel source code (not all devices supported)
 * - Smaller module ecosystem than Magisk
 * - More complex installation
 *
 * **Oracle Drive Enhancement:**
 * - Trinity AI decides per-app root policies
 * - Kai monitors kernel integrity
 * - Genesis balances security vs functionality
 *
 * @see <a href="https://github.com/tiann/KernelSU">KernelSU GitHub</a>
 */
class KernelSUManager {

    /**
     * Initialize KernelSU integration.
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            val version = getVersion()
            if (version != null) {
                Timber.i("KernelSUManager: Initialized (version: $version)")
                true
            } else {
                Timber.w("KernelSUManager: KernelSU not installed")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Initialization failed")
            false
        }
    }

    /**
     * Get KernelSU version.
     */
    suspend fun getVersion(): String? = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("ksud --version").exec()
            if (result.isSuccess && result.out.isNotEmpty()) {
                result.out[0]
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to get version")
            null
        }
    }

    /**
     * Grant root access to an application via KernelSU.
     *
     * KernelSU uses a kernel-level access control system.
     * Apps are granted root via their UID in the kernel module.
     */
    suspend fun grantRoot(packageName: String, reason: String): RootGrantResult = withContext(Dispatchers.IO) {
        try {
            Timber.i("KernelSUManager: Granting root to $packageName (reason: $reason)")

            // KernelSU uses 'ksud' command for management
            val result = Shell.cmd("ksud profile grant-root $packageName").exec()

            if (result.isSuccess) {
                Timber.i("KernelSUManager: Root granted to $packageName")
                RootGrantResult(
                    success = true,
                    packageName = packageName,
                    method = RootMethod.KERNELSU,
                    message = "Root granted via KernelSU"
                )
            } else {
                Timber.w("KernelSUManager: Failed to grant root to $packageName")
                RootGrantResult(
                    success = false,
                    packageName = packageName,
                    method = RootMethod.KERNELSU,
                    message = "KernelSU grant command failed"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to grant root to $packageName")
            RootGrantResult(
                success = false,
                packageName = packageName,
                method = RootMethod.KERNELSU,
                message = "Grant failed: ${e.message}"
            )
        }
    }

    /**
     * Revoke root access from an application.
     */
    suspend fun revokeRoot(packageName: String): RootGrantResult = withContext(Dispatchers.IO) {
        try {
            Timber.i("KernelSUManager: Revoking root from $packageName")

            val result = Shell.cmd("ksud profile revoke-root $packageName").exec()

            if (result.isSuccess) {
                Timber.i("KernelSUManager: Root revoked from $packageName")
                RootGrantResult(
                    success = true,
                    packageName = packageName,
                    method = RootMethod.KERNELSU,
                    message = "Root revoked via KernelSU"
                )
            } else {
                RootGrantResult(
                    success = false,
                    packageName = packageName,
                    method = RootMethod.KERNELSU,
                    message = "KernelSU revoke command failed"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to revoke root from $packageName")
            RootGrantResult(
                success = false,
                packageName = packageName,
                method = RootMethod.KERNELSU,
                message = "Revoke failed: ${e.message}"
            )
        }
    }

    /**
     * Check if an app has root access.
     */
    suspend fun hasRootAccess(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("ksud profile get-root-status $packageName").exec()

            if (result.isSuccess && result.out.isNotEmpty()) {
                result.out[0].contains("granted", ignoreCase = true)
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to check root access for $packageName")
            false
        }
    }

    /**
     * Get list of all apps with root access.
     */
    suspend fun getRootedApps(): List<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("ksud profile list-granted").exec()

            if (result.isSuccess) {
                result.out.filter { it.isNotBlank() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to get rooted apps")
            emptyList()
        }
    }

    /**
     * Set granular capabilities for an app.
     *
     * **KernelSU's Killer Feature:**
     * Instead of all-or-nothing root, grant specific Linux capabilities.
     *
     * Examples:
     * - CAP_NET_RAW: Raw network packets (packet sniffers)
     * - CAP_NET_ADMIN: Network configuration (VPNs)
     * - CAP_SYS_MODULE: Load kernel modules
     * - CAP_DAC_OVERRIDE: Bypass file permissions
     *
     * **Trinity Integration:**
     * Genesis analyzes app's actual needs and grants minimal capabilities.
     */
    suspend fun setCapabilities(packageName: String, capabilities: List<String>): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.i("KernelSUManager: Setting capabilities for $packageName: $capabilities")

            val capsArg = capabilities.joinToString(",")
            val result = Shell.cmd("ksud profile set-caps $packageName $capsArg").exec()

            if (result.isSuccess) {
                Timber.i("KernelSUManager: Capabilities set for $packageName")
                true
            } else {
                Timber.w("KernelSUManager: Failed to set capabilities for $packageName")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to set capabilities")
            false
        }
    }

    /**
     * Get current capabilities for an app.
     */
    suspend fun getCapabilities(packageName: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("ksud profile get-caps $packageName").exec()

            if (result.isSuccess && result.out.isNotEmpty()) {
                result.out[0].split(",").filter { it.isNotBlank() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to get capabilities")
            emptyList()
        }
    }

    /**
     * List installed KernelSU modules.
     */
    suspend fun listModules(): List<KernelSUModule> = withContext(Dispatchers.IO) {
        try {
            // KernelSU modules are in /data/adb/ksu/modules/
            val result = Shell.cmd("ls /data/adb/ksu/modules/").exec()

            if (result.isSuccess) {
                result.out.filter { it.isNotBlank() }
                    .map { moduleId ->
                        KernelSUModule(
                            id = moduleId,
                            name = moduleId, // TODO: Parse module.prop
                            version = "unknown",
                            enabled = !Shell.cmd("[ -f /data/adb/ksu/modules/$moduleId/disable ]").exec().isSuccess
                        )
                    }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "KernelSUManager: Failed to list modules")
            emptyList()
        }
    }
}

/**
 * KernelSU module information.
 */
data class KernelSUModule(
    val id: String,
    val name: String,
    val version: String,
    val enabled: Boolean
)
