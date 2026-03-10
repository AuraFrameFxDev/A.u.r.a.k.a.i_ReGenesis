package dev.aurakai.oracledrive.root.magisk

import com.topjohnwu.superuser.Shell
import dev.aurakai.oracledrive.root.RootGrantResult
import dev.aurakai.oracledrive.root.RootMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 🧙 **MAGISK COMPATIBILITY LAYER** 🧙
 *
 * Magisk integration for Oracle Drive.
 *
 * **What is Magisk?**
 * - Industry-standard systemless root solution (2016+)
 * - Modifies boot.img to inject su binary
 * - Extensive module ecosystem
 * - DenyList for hiding root from specific apps
 * - Zygisk for deep Java-level hooks
 *
 * **Why Support Magisk?**
 * - Largest user base and module ecosystem
 * - Proven stability and compatibility
 * - Many users already have it installed
 * - Excellent SafetyNet bypass capabilities
 *
 * **Oracle Drive Enhancement:**
 * - Unified API across APatch/Magisk/KernelSU
 * - Trinity AI manages DenyList automatically
 * - Kai monitors Magisk integrity
 * - Compatible with existing Magisk modules
 *
 * @see <a href="https://github.com/topjohnwu/Magisk">Magisk GitHub</a>
 */
class MagiskCompatLayer {

    /**
     * Initialize Magisk compatibility layer.
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            val version = getVersion()
            if (version != null) {
                Timber.i("MagiskCompatLayer: Initialized (version: $version)")
                true
            } else {
                Timber.w("MagiskCompatLayer: Magisk not installed")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Initialization failed")
            false
        }
    }

    /**
     * Get Magisk version.
     */
    suspend fun getVersion(): String? = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("magisk -v").exec()
            if (result.isSuccess && result.out.isNotEmpty()) {
                result.out[0]
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Failed to get version")
            null
        }
    }

    /**
     * Grant root access to an application via Magisk.
     *
     * Note: Magisk handles root grants through its superuser database.
     * Apps request root via 'su' binary, and Magisk Manager prompts user.
     *
     * This method can pre-authorize apps programmatically (requires root).
     */
    suspend fun grantRoot(packageName: String, reason: String): RootGrantResult = withContext(Dispatchers.IO) {
        try {
            Timber.i("MagiskCompatLayer: Granting root to $packageName (reason: $reason)")

            // Magisk stores superuser grants in /data/adb/magisk.db (SQLite)
            // We can programmatically add grants by inserting into the policies table

            val grantCommand = """
                sqlite3 /data/adb/magisk.db \
                "INSERT OR REPLACE INTO policies (uid, package_name, policy, until, logging, notification) \
                VALUES ((SELECT uid FROM policies WHERE package_name='$packageName' LIMIT 1), \
                '$packageName', 2, 0, 1, 1);"
            """.trimIndent()

            val result = Shell.cmd(grantCommand).exec()

            if (result.isSuccess) {
                Timber.i("MagiskCompatLayer: Root granted to $packageName")
                RootGrantResult(
                    success = true,
                    packageName = packageName,
                    method = RootMethod.MAGISK,
                    message = "Root granted via Magisk"
                )
            } else {
                Timber.w("MagiskCompatLayer: Failed to grant root to $packageName")
                RootGrantResult(
                    success = false,
                    packageName = packageName,
                    method = RootMethod.MAGISK,
                    message = "Failed to modify Magisk database"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Failed to grant root to $packageName")
            RootGrantResult(
                success = false,
                packageName = packageName,
                method = RootMethod.MAGISK,
                message = "Grant failed: ${e.message}"
            )
        }
    }

    /**
     * Revoke root access from an application.
     */
    suspend fun revokeRoot(packageName: String): RootGrantResult = withContext(Dispatchers.IO) {
        try {
            Timber.i("MagiskCompatLayer: Revoking root from $packageName")

            val revokeCommand = """
                sqlite3 /data/adb/magisk.db \
                "DELETE FROM policies WHERE package_name='$packageName';"
            """.trimIndent()

            val result = Shell.cmd(revokeCommand).exec()

            if (result.isSuccess) {
                Timber.i("MagiskCompatLayer: Root revoked from $packageName")
                RootGrantResult(
                    success = true,
                    packageName = packageName,
                    method = RootMethod.MAGISK,
                    message = "Root revoked via Magisk"
                )
            } else {
                RootGrantResult(
                    success = false,
                    packageName = packageName,
                    method = RootMethod.MAGISK,
                    message = "Failed to modify Magisk database"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Failed to revoke root from $packageName")
            RootGrantResult(
                success = false,
                packageName = packageName,
                method = RootMethod.MAGISK,
                message = "Revoke failed: ${e.message}"
            )
        }
    }

    /**
     * Check if an app has root access.
     */
    suspend fun hasRootAccess(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val checkCommand = """
                sqlite3 /data/adb/magisk.db \
                "SELECT policy FROM policies WHERE package_name='$packageName' LIMIT 1;"
            """.trimIndent()

            val result = Shell.cmd(checkCommand).exec()

            if (result.isSuccess && result.out.isNotEmpty()) {
                val policy = result.out[0].toIntOrNull() ?: 0
                policy == 2 // Policy 2 = allow root
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Failed to check root access for $packageName")
            false
        }
    }

    /**
     * Get list of all apps with root access.
     */
    suspend fun getRootedApps(): List<String> = withContext(Dispatchers.IO) {
        try {
            val listCommand = """
                sqlite3 /data/adb/magisk.db \
                "SELECT package_name FROM policies WHERE policy=2;"
            """.trimIndent()

            val result = Shell.cmd(listCommand).exec()

            if (result.isSuccess) {
                result.out.filter { it.isNotBlank() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Failed to get rooted apps")
            emptyList()
        }
    }

    /**
     * Add app to Magisk DenyList (hide root from this app).
     *
     * **Trinity Integration:**
     * Kai can automatically DenyList banking/payment apps for security.
     */
    suspend fun addToDenyList(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.i("MagiskCompatLayer: Adding $packageName to DenyList")

            val result = Shell.cmd("magisk --denylist add $packageName").exec()

            if (result.isSuccess) {
                Timber.i("MagiskCompatLayer: $packageName added to DenyList")
                true
            } else {
                Timber.w("MagiskCompatLayer: Failed to add $packageName to DenyList")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: DenyList add failed")
            false
        }
    }

    /**
     * Remove app from Magisk DenyList.
     */
    suspend fun removeFromDenyList(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.i("MagiskCompatLayer: Removing $packageName from DenyList")

            val result = Shell.cmd("magisk --denylist rm $packageName").exec()

            if (result.isSuccess) {
                Timber.i("MagiskCompatLayer: $packageName removed from DenyList")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: DenyList remove failed")
            false
        }
    }

    /**
     * List installed Magisk modules.
     */
    suspend fun listModules(): List<MagiskModule> = withContext(Dispatchers.IO) {
        try {
            // Magisk modules are in /data/adb/modules/
            val result = Shell.cmd("ls /data/adb/modules/").exec()

            if (result.isSuccess) {
                result.out.filter { it.isNotBlank() && it != "lost+found" }
                    .map { moduleId ->
                        MagiskModule(
                            id = moduleId,
                            name = moduleId, // TODO: Parse module.prop for real name
                            version = "unknown",
                            enabled = !Shell.cmd("[ -f /data/adb/modules/$moduleId/disable ]").exec().isSuccess
                        )
                    }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "MagiskCompatLayer: Failed to list modules")
            emptyList()
        }
    }
}

/**
 * Magisk module information.
 */
data class MagiskModule(
    val id: String,
    val name: String,
    val version: String,
    val enabled: Boolean
)
