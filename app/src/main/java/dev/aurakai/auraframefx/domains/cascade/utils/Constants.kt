package dev.aurakai.auraframefx.domains.cascade.utils

/**
 * Common constants used throughout the app
 */
object Constants {
    // Logging tags
    const val TAG_GENESIS = "Genesis"
    const val TAG_AURA = "Aura"
    const val TAG_KAI = "Kai"
    const val TAG_CASCADE = "Cascade"
    const val TAG_SECURITY = "Security"
    const val TAG_NETWORK = "Network"
    const val TAG_UI = "UI"

    // Default TAG for classes without specific tag
    const val TAG = "AuraKai"
}

/**
 * Extension function to get TAG for any class
 */
val Any.TAG: String
    get() = this::class.simpleName ?: Constants.TAG

