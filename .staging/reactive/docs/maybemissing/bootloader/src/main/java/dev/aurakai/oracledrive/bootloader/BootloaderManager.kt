package dev.aurakai.oracledrive.bootloader

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 🔓 **BOOTLOADER MANAGER** 🔓
 *
 * Manages bootloader unlock status detection and verification.
 * Part of the Oracle Drive universal root management system.
 *
 * **Features:**
 * - Bootloader unlock status detection
 * - Device verification state checking
 * - SafetyNet/Play Integrity status
 * - Secure boot state monitoring
 *
 * **Safety:**
 * - Read-only operations (no actual unlock attempts)
 * - Informational only - user must unlock via OEM methods
 * - Provides guidance for unlock procedures
 */
class BootloaderManager {

    /**
     * Check if the bootloader is unlocked.
     *
     * Different OEMs use different methods to report bootloader status:
     * - Google Pixels: ro.boot.verifiedbootstate = orange/red (unlocked) vs green (locked)
     * - Samsung: ro.boot.warranty_bit = 1 (unlocked) vs 0 (locked)
     * - OnePlus: ro.secureboot.lockstate = locked/unlocked
     * - Xiaomi: ro.secureboot.enable = 0 (unlocked) vs 1 (locked)
     *
     * @return BootloaderStatus containing unlock state and details
     */
    suspend fun checkBootloaderStatus(): BootloaderStatus = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(
                "getprop ro.boot.verifiedbootstate",
                "getprop ro.boot.warranty_bit",
                "getprop ro.secureboot.lockstate",
                "getprop ro.secureboot.enable",
                "getprop ro.boot.flash.locked",
                "getprop ro.boot.veritymode"
            ).exec()

            val outputs = result.out
            Timber.d("Bootloader check outputs: $outputs")

            val isUnlocked = when {
                // Google Pixel detection
                outputs.any { it.contains("orange") || it.contains("red") } -> true
                // Samsung detection
                outputs.any { it == "1" && outputs.indexOf(it) == 1 } -> true
                // OnePlus detection
                outputs.any { it.contains("unlocked") } -> true
                // Xiaomi detection
                outputs.any { it == "0" && outputs.indexOf(it) == 3 } -> true
                // Generic flash.locked detection
                outputs.any { it == "0" && outputs.indexOf(it) == 4 } -> true
                else -> false
            }

            val verifiedBootState = outputs.getOrNull(0) ?: "unknown"
            val warrantyBit = outputs.getOrNull(1) ?: "unknown"
            val lockState = outputs.getOrNull(2) ?: "unknown"

            BootloaderStatus(
                isUnlocked = isUnlocked,
                verifiedBootState = verifiedBootState,
                warrantyBit = warrantyBit,
                lockState = lockState,
                detectionMethod = detectOEM()
            )
        } catch (e: Exception) {
            Timber.e(e, "BootloaderManager: Failed to check bootloader status")
            BootloaderStatus(
                isUnlocked = false,
                verifiedBootState = "error",
                warrantyBit = "error",
                lockState = "error",
                detectionMethod = "unknown"
            )
        }
    }

    /**
     * Detect the device OEM/manufacturer for OEM-specific bootloader checks.
     */
    private suspend fun detectOEM(): String = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(
                "getprop ro.product.manufacturer",
                "getprop ro.product.brand"
            ).exec()

            val manufacturer = result.out.getOrNull(0)?.lowercase() ?: "unknown"

            when {
                manufacturer.contains("google") -> "pixel"
                manufacturer.contains("samsung") -> "samsung"
                manufacturer.contains("oneplus") -> "oneplus"
                manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> "xiaomi"
                manufacturer.contains("motorola") -> "motorola"
                manufacturer.contains("sony") -> "sony"
                else -> manufacturer
            }
        } catch (e: Exception) {
            Timber.e(e, "BootloaderManager: Failed to detect OEM")
            "unknown"
        }
    }

    /**
     * Get bootloader unlock instructions for the detected device.
     */
    suspend fun getUnlockInstructions(): UnlockInstructions = withContext(Dispatchers.IO) {
        val oem = detectOEM()
        val status = checkBootloaderStatus()

        when {
            status.isUnlocked -> UnlockInstructions(
                isRequired = false,
                oem = oem,
                steps = emptyList(),
                warning = "Bootloader is already unlocked."
            )
            oem == "pixel" -> UnlockInstructions(
                isRequired = true,
                oem = oem,
                steps = listOf(
                    "Enable Developer Options (Settings → About phone → Tap build number 7 times)",
                    "Enable OEM unlocking (Settings → System → Developer options)",
                    "Reboot to bootloader (adb reboot bootloader)",
                    "Unlock bootloader (fastboot flashing unlock)",
                    "Confirm unlock on device (WARNING: Wipes all data)"
                ),
                warning = "Unlocking bootloader will WIPE ALL DATA and void warranty."
            )
            oem == "samsung" -> UnlockInstructions(
                isRequired = true,
                oem = oem,
                steps = listOf(
                    "Samsung bootloader unlock varies by region and model",
                    "US/Canada models often have locked bootloaders (check XDA forums)",
                    "International models: Enable Developer Options",
                    "Enable OEM unlocking (Settings → Developer options)",
                    "Reboot to download mode (Vol Down + Bixby + Power)",
                    "Long press Vol Up to unlock bootloader"
                ),
                warning = "Knox will be permanently tripped. Samsung Pay/Pass will stop working."
            )
            oem == "oneplus" -> UnlockInstructions(
                isRequired = true,
                oem = oem,
                steps = listOf(
                    "Enable Developer Options",
                    "Enable OEM unlocking and Advanced reboot",
                    "Reboot to bootloader (adb reboot bootloader)",
                    "Unlock bootloader (fastboot oem unlock)",
                    "Confirm on device"
                ),
                warning = "Unlocking will wipe all data."
            )
            oem == "xiaomi" -> UnlockInstructions(
                isRequired = true,
                oem = oem,
                steps = listOf(
                    "Create Mi account and link device for 7 days (Xiaomi waiting period)",
                    "Download Mi Unlock Tool on PC",
                    "Enable Developer Options → Enable OEM unlocking and USB debugging",
                    "Reboot to fastboot (Power + Vol Down)",
                    "Run Mi Unlock Tool and follow on-screen instructions"
                ),
                warning = "Xiaomi requires 7-day waiting period. Unlocking wipes all data."
            )
            else -> UnlockInstructions(
                isRequired = true,
                oem = oem,
                steps = listOf(
                    "Check XDA Developers forum for your specific device model",
                    "General steps: Enable Developer Options and OEM unlocking",
                    "Use 'adb reboot bootloader' to enter fastboot",
                    "Try 'fastboot oem unlock' or 'fastboot flashing unlock'"
                ),
                warning = "Bootloader unlock process varies by manufacturer. Research your specific device."
            )
        }
    }
}

/**
 * Bootloader status information.
 */
data class BootloaderStatus(
    val isUnlocked: Boolean,
    val verifiedBootState: String,
    val warrantyBit: String,
    val lockState: String,
    val detectionMethod: String
)

/**
 * Bootloader unlock instructions for specific OEM.
 */
data class UnlockInstructions(
    val isRequired: Boolean,
    val oem: String,
    val steps: List<String>,
    val warning: String
)
