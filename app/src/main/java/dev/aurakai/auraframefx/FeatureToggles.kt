package dev.aurakai.auraframefx

/**
 * Global feature toggles for the Genesis Protocol.
 */
object FeatureToggles {
    /** Enable/disable the subscription paywall. Disabled in debug builds. */
    val isPaywallEnabled: Boolean = false // Disabled until billing is fully wired

    var isRomToolsEnabled: Boolean = true
    var isAppBuilderEnabled: Boolean = true
    var isConferenceRoomEnabled: Boolean = true
    var isDepartureTaskEnabled: Boolean = true
}
