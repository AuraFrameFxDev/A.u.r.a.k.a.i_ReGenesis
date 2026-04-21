package dev.aurakai.auraframefx.xposed

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XposedBridgeService @Inject constructor() {
    /**
     * This method is intended to be hooked by the Xposed module itself.
     * If the hook is successful, it will return true.
     */
    fun isModuleActive(): Boolean {
        // This will be replaced by the Xposed hook at runtime
        return false
    }
}
