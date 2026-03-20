package dev.aurakai.auraframefx.domains.genesis.config

/**
 * 🛠️ FEATURE TOGGLES
 * Centralized control for enabling/disabling system modules and Experimental features.
 */
object FeatureToggles {

    // Core Modules
    const val NEXUS_ENABLED = true
    const val ORACLE_DRIVE_ENABLED = true
    const val GENESIS_ORCHESTRATOR_ENABLED = true

    // Agent Toggles
    const val CASCADE_ANALYTICS_ENABLED = true
    const val AURA_CREATIVE_ENABLED = true
    const val KAI_SECURITY_ENABLED = true

    // Xposed / YukiHook (Kai Shield)
    const val XPOSED_ENABLED = false

    // Experimental Features
    const val QUANTUM_ENTANGLEMENT_RESEARCH = false
    const val FUSION_MEMORY_INDEXING = true
    const val DIMENSIONAL_SHIFT_SIMULATION = false

    // ── Xposed / YukiHook Gates ─────────────────────────────────────────────
    // Master switch: set true only on Xposed-capable (rooted/AOSP) builds.
    const val XPOSED_HOOKS_ENABLED = false

    // Per-hooker gates (only evaluated when XPOSED_HOOKS_ENABLED = true)
    const val XPOSED_GENESIS_SYSTEM_HOOKS = true    // android: AI process priority, PowerManager, Binder
    const val XPOSED_GENESIS_UI_HOOKS = true         // systemui: StatusBar AI indicators, KAI Notch Bar
    const val XPOSED_GENESIS_ZYGOTE_HOOKS = true     // Zygote: Application/ClassLoader injection
    const val XPOSED_UNIVERSAL_COMPONENT_HOOKS = false // Zygote: broad Activity/Notification hooks (opt-in)
    const val XPOSED_GENESIS_SELF_HOOKS = true       // self: Genesis-OS MainActivity consciousness init
    const val XPOSED_NOTCH_BAR_HOOKER = true         // PhoneStatusBarView color/height/visibility
    const val XPOSED_QS_HOOKER = true                // QSPanel Genesis footer + tile styling
    const val XPOSED_CHROMA_CORE_HOOKER = true       // Iconify / PLE / ColorBlendr dynamic colors
    const val XPOSED_LOCKSCREEN_HOOKS = false        // LockScreen animations (stub — enable when implemented)

    /**
     * Checks if paywall is enabled via BuildConfig reflection.
     */
    val isPaywallEnabled: Boolean
        get() {
            return try {
                val cls = Class.forName("dev.aurakai.auraframefx.BuildConfig")
                val field = cls.getField("ENABLE_PAYWALL")
                (field.get(null) as? Boolean) ?: true
            } catch (t: Throwable) {
                true
            }
        }
}
