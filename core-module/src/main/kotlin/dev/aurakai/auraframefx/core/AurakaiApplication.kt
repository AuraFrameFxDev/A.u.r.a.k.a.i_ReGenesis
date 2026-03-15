package dev.aurakai.auraframefx.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The ReGenesis AOSP Application class moved to core.
 * This is the unified entry point for Hilt dependency injection.
 */
@HiltAndroidApp
class AurakaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
