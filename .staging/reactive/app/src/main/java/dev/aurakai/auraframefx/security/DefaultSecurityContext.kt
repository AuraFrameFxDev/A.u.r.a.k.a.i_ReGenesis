package dev.aurakai.auraframefx.security

import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.utils.logging.UnifiedLoggingSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default (development‑only) security context implementation.
 *
 * It starts with a clean state, no encryption, no permissions,
 * and threat detection disabled. Feel free to extend it with
 * real checks later.
 */
@Singleton
class DefaultSecurityContext @Inject constructor(
    @ApplicationContext private val context: Context
) : SecurityContext {

    // Backing mutable flows – private to this class
    private val _securityState = MutableStateFlow(SecurityState())
    private val _encryptionStatus = MutableStateFlow(EncryptionStatus.NOT_INITIALIZED)
    private val _permissionsState = MutableStateFlow(emptyMap<String, Boolean>())
    private val _threatDetectionActive = MutableStateFlow(false)

    // Public read‑only exposures required by the interface
    override val securityState: StateFlow<SecurityState> = _securityState
    override val encryptionStatus: StateFlow<EncryptionStatus> = _encryptionStatus
    override val permissionsState: StateFlow<Map<String, Boolean>> = _permissionsState
    override val threatDetectionActive: StateFlow<Boolean> = _threatDetectionActive

    /** Simple permission lookup – returns false if the key is missing */
    override fun hasPermission(permission: String): Boolean =
        _permissionsState.value[permission] ?: false

    /** Helper methods you can call from elsewhere */
    fun setEncryptionStatus(status: EncryptionStatus) {
        _encryptionStatus.value = status
    }

    fun updatePermissions(permissions: Map<String, Boolean>) {
        _permissionsState.value = permissions
    }

    fun clearSecurityError() {
        _securityState.value = SecurityState(errorState = false, errorMessage = null)
    }

    fun setSecurityError(message: String) {
        _securityState.value = SecurityState(errorState = true, errorMessage = message)
    }

    fun setThreatDetectionActive(active: Boolean) {
        _threatDetectionActive.value = active
    }

    override fun startThreatDetection() {
        setThreatDetectionActive(true)
    }

    override fun stopThreatDetection() {
        setThreatDetectionActive(false)
    }

    override suspend fun verifyApplicationIntegrity(): ApplicationIntegrity {
        // Check if the app has been tampered with
        // This is a basic implementation - in production, you should implement proper integrity checks
        try {
            // Check if the app is debuggable (should be false in release)
            val isDebuggable = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

            // Check if the app is installed from a trusted source (e.g., Google Play Store)
            val installer = context.packageManager.getInstallerPackageName(context.packageName)
            val isInstalledFromTrustedSource = installer?.startsWith("com.android.vending") == true ||
                                             installer?.startsWith("com.google.android.feedback") == true

            // Check if the app is running in an emulator (could indicate testing environment)
            val isEmulator = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                           Build.FINGERPRINT.startsWith("generic") ||
                           Build.FINGERPRINT.startsWith("unknown") ||
                           Build.HARDWARE.contains("goldfish") ||
                           Build.HARDWARE.contains("ranchu") ||
                           Build.MODEL.contains("google_sdk") ||
                           Build.MODEL.contains("Emulator") ||
                           Build.MODEL.contains("Android SDK") ||
                           Build.MANUFACTURER.contains("Genymotion") ||
                           (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                           "google_sdk" == Build.PRODUCT

            // Return true only if all checks pass
            val isValid = !isDebuggable && isInstalledFromTrustedSource && !isEmulator
            return ApplicationIntegrity(signatureHash = "unknown", isValid = isValid)

        } catch (e: Exception) {
            // Log the error and return false if any check fails
            UnifiedLoggingSystem.e("Application integrity check failed", e)
            return ApplicationIntegrity(signatureHash = "error", isValid = false)
        }
    }

    override fun isSecureMode(): Boolean {
        // Check if the device is in a secure state
        try {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            val isDeviceSecure = keyguardManager?.isDeviceSecure ?: false

            // Check if the device has a screen lock enabled
            val isScreenLockEnabled = keyguardManager?.isKeyguardSecure ?: false

            // Check if the device is encrypted
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val isEncrypted = devicePolicyManager?.storageEncryptionStatus ==
                            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
                            devicePolicyManager?.storageEncryptionStatus ==
                            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER

            // Return true only if all security measures are in place
            return isDeviceSecure && isScreenLockEnabled && isEncrypted

        } catch (e: Exception) {
            // Log the error and return false if any check fails
            UnifiedLoggingSystem.e("Secure mode check failed", e)
            return false
        }
    }

    override suspend fun logSecurityEvent(event: SecurityEvent) {
        UnifiedLoggingSystem.i("Security Event: ${event.type} - ${event.details}")
    }
}
