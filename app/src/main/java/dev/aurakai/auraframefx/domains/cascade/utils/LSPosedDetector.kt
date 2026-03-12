package dev.aurakai.auraframefx.domains.cascade.utils

import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber
import java.io.File

/**
 * LSPosed Framework Detection Utility
 *
 * Detects if LSPosed (or other Xposed variants) is installed and active on the device.
 */
object LSPosedDetector {

    /**
     * Check if LSPosed framework is installed
     *
     * @param context Application context
     * @return true if LSPosed is detected, false otherwise
     */
    fun isLSPosedInstalled(context: Context): Boolean {
        return try {
            // Method 1: Check for LSPosed app package
            val lsposedPackages = listOf(
                "org.lsposed.manager",      // LSPosed Manager
                "de.robv.android.xposed.installer",  // Classic Xposed Installer
                "io.github.lsposed.manager" // Alternative LSPosed package
            )

            val isPackageInstalled = lsposedPackages.any { packageName ->
                try {
                    context.packageManager.getPackageInfo(packageName, 0)
                    Timber.d("✅ LSPosed detected via package: $packageName")
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }

            if (isPackageInstalled) return true

            // Method 2: Check for LSPosed framework files
            val lsposedFiles = listOf(
                "/system/framework/lspd.dex",
                "/system/framework/lspd",
                "/data/adb/lspd",
                "/data/adb/modules/zygisk_lsposed"
            )

            val isFilePresent = lsposedFiles.any { filePath ->
                File(filePath).exists().also { exists ->
                    if (exists) Timber.d("✅ LSPosed detected via file: $filePath")
                }
            }

            if (isFilePresent) return true

            // Method 3: Check for Xposed Bridge presence (classic detection)
            val isXposedActive = try {
                Class.forName("de.robv.android.xposed.XposedBridge")
                Timber.d("✅ Xposed Bridge class found - framework active")
                true
            } catch (e: ClassNotFoundException) {
                false
            }

            if (isXposedActive) return true

            // Method 4: Check system property
            val lsposedProp = System.getProperty("ro.lsposed.installed")
            if (lsposedProp == "true") {
                Timber.d("✅ LSPosed detected via system property")
                return true
            }

            Timber.i("❌ LSPosed not detected on this device")
            false

        } catch (e: Exception) {
            Timber.e(e, "Error detecting LSPosed")
            false
        }
    }

    /**
     * Check if the app is currently hooked by LSPosed
     *
     * @return true if this app is being hooked, false otherwise
     */
    fun isAppHooked(): Boolean {
        return try {
            // Check if Xposed Bridge is present and this app is hooked
            val xposedBridge = Class.forName("de.robv.android.xposed.XposedBridge")
            val disableHooksField = xposedBridge.getDeclaredField("disableHooks")
            disableHooksField.isAccessible = true
            val hooksDisabled = disableHooksField.getBoolean(null)

            (!hooksDisabled).also { hooked ->
                if (hooked) {
                    Timber.d("✅ This app is currently hooked by Xposed framework")
                } else {
                    Timber.d("❌ Hooks disabled for this app")
                }
            }
        } catch (e: Exception) {
            Timber.d("❌ App is not hooked by Xposed framework")
            false
        }
    }

    /**
     * Get LSPosed version if installed
     *
     * @param context Application context
     * @return Version name or null if not found
     */
    fun getLSPosedVersion(context: Context): String? {
        return try {
            val packageName = "org.lsposed.manager"
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName.also { version ->
                Timber.d("LSPosed version: $version")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.d("LSPosed Manager app not found")
            null
        }
    }

    /**
     * Get detection summary with all available information
     *
     * @param context Application context
     * @return Detection result summary
     */
    fun getDetectionSummary(context: Context): LSPosedDetectionResult {
        val isInstalled = isLSPosedInstalled(context)
        val isHooked = isAppHooked()
        val version = getLSPosedVersion(context)

        return LSPosedDetectionResult(
            isInstalled = isInstalled,
            isActive = isInstalled && isHooked,
            version = version,
            detectionMethod = when {
                version != null -> "Package Manager"
                isHooked -> "Xposed Bridge"
                isInstalled -> "File System"
                else -> "None"
            }
        )
    }

    data class LSPosedDetectionResult(
        val isInstalled: Boolean,
        val isActive: Boolean,
        val version: String?,
        val detectionMethod: String
    )
}

