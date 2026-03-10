package dev.aurakai.auraframefx.debug

/**
 * Developer feature toggles backed by BuildConfig when available.
 * Uses reflection so the codebase doesn't hard-depend on generated BuildConfig during static analysis.
 */
object FeatureToggles {
    val isPaywallEnabled: Boolean
        get() {
            return try {
                val cls = Class.forName("dev.aurakai.auraframefx.BuildConfig")
                val field = cls.getField("ENABLE_PAYWALL")
                (field.get(null) as? Boolean) ?: true
            } catch (t: Throwable) {
                // If BuildConfig or the field isn't present (e.g., before a Gradle build),
                // default to enabling the paywall to avoid accidentally bypassing production gates.
                true
            }
        }
}
