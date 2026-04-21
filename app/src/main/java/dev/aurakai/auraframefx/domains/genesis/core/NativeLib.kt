package dev.aurakai.auraframefx.domains.genesis.core

import dev.aurakai.auraframefx.core.NativeLib as CanonicalNativeLib

/**
 * DEPRECATED: Use dev.aurakai.auraframefx.core.NativeLib for canonical JNI.
 */
@Deprecated(
    "Use dev.aurakai.auraframefx.core.NativeLib",
    ReplaceWith("dev.aurakai.auraframefx.core.NativeLib")
)
object NativeLib {
    fun initializeAISafe(): Boolean = CanonicalNativeLib.initializeAICore()
}
