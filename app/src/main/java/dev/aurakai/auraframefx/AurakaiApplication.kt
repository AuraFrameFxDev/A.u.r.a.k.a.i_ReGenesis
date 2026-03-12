package dev.aurakai.auraframefx

import android.app.Application
import android.util.Log
import com.highcapable.yukihookapi.YukiHookAPI
import dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles

private const val TAG = "AurakaiApplication"

class AurakaiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "AurakaiApplication: Application started.")

        if (FeatureToggles.XPOSED_ENABLED) {
            YukiHookAPI.encapsulate(this)
            checkHookEnvironment()
        }
    }

    private fun checkHookEnvironment() {
        try {
            // This check is a common way to detect if an Xposed/LSPosed environment is active
            // by attempting to access a specific XposedBridge method.
            Class.forName("de.robv.android.xposed.XposedBridge")
            Log.i(TAG, "AurakaiApplication: Xposed/LSPosed environment detected!")
        } catch (e: ClassNotFoundException) {
            Log.i(
                TAG,
                "AurakaiApplication: Xposed/LSPosed environment NOT detected. Running in normal mode."
            )
        }
    }
}
