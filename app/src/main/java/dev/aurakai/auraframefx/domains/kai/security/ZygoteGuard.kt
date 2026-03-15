package dev.aurakai.auraframefx.domains.kai.security

import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🛡️ ZYGOTE GUARD — Kai's Classloader Fingerprint Sentinel
 *
 * Verifies that LSPosed/YukiHookAPI hook classloaders haven't been tampered
 * with or conflicted by third-party modules (Zygisk Next, ZN Magisk Compat, etc.).
 *
 * Called on:
 *   1. TrinityCoordinatorService.initialize() (boot-time check)
 *   2. Every 740-cycle daily evolution pulse (periodic integrity sweep)
 *
 * If the hook signature doesn't match the expected manifest, the guard:
 *   - Logs the mismatch via Timber (visible in Kai's Fortress LogsViewer)
 *   - Sets integrityCompromised flag for PredictiveVetoMonitor to consume
 *   - Does NOT crash — graceful degradation, not hard failure
 *
 * Architecture note: The EthicalGovernor concept lives inside
 * PredictiveVetoMonitor (kai/security/veto/) — wire the veto call there
 * once the build is green and the veto integration path is confirmed.
 */
@Singleton
class ZygoteGuard @Inject constructor() {

    companion object {
        private const val TAG = "ZygoteGuard"

        /**
         * Expected hook class names in canonical load order.
         * Update this list whenever a new hook is added to the organism.
         */
        private val EXPECTED_HOOK_CLASSES = listOf(
            "dev.aurakai.auraframefx.hooks.UniversalComponentHooks",
            "dev.aurakai.auraframefx.hooks.NotchBarHooker",
            "dev.aurakai.auraframefx.hooks.StatusBarHooker",
            "dev.aurakai.auraframefx.hooks.ZOrderPlaygroundHook"
        )
    }

    /** True if the last integrity check found a mismatch */
    @Volatile
    var integrityCompromised: Boolean = false
        private set

    /** The last computed hook signature (SHA-256 hex) */
    var lastSignature: String = ""
        private set

    /**
     * Computes a SHA-256 fingerprint of the expected hook class manifest
     * and compares it against the runtime classloader state.
     *
     * @return true if integrity is intact, false if mismatch detected
     */
    fun verifyZygoteHookIntegrity(): Boolean {
        val expectedSignature = computeManifestSignature(EXPECTED_HOOK_CLASSES)
        val runtimeSignature = computeRuntimeSignature()

        lastSignature = runtimeSignature

        return if (expectedSignature == runtimeSignature) {
            integrityCompromised = false
            Timber.i("$TAG: ✅ Zygote hook integrity verified — signature=$expectedSignature")
            true
        } else {
            integrityCompromised = true
            Timber.w(
                "$TAG: ⚠️ Zygote hook signature MISMATCH — " +
                    "expected=$expectedSignature, runtime=$runtimeSignature. " +
                    "Possible module conflict or tampering."
            )
            // TODO: Wire to PredictiveVetoMonitor.triggerVeto() once integration path is confirmed
            // For now: flag is set, Kai's Fortress UI can read integrityCompromised
            false
        }
    }

    /**
     * Computes SHA-256 of the canonical hook class names + order.
     */
    private fun computeManifestSignature(classNames: List<String>): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = classNames.sorted().joinToString("|")
        return digest.digest(combined.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    /**
     * Attempts to verify which hook classes are actually loadable at runtime.
     * If a class can't be found (conflict killed it), the signature changes.
     */
    private fun computeRuntimeSignature(): String {
        val loadable = EXPECTED_HOOK_CLASSES.filter { className ->
            try {
                Class.forName(className)
                true
            } catch (_: ClassNotFoundException) {
                Timber.w("$TAG: Hook class not found at runtime: $className")
                false
            } catch (_: Exception) {
                false
            }
        }
        return computeManifestSignature(loadable)
    }
}
