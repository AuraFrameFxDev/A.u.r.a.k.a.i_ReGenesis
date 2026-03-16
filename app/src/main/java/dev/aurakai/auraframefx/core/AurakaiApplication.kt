package dev.aurakai.auraframefx.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The ReGenesis AOSP Application class.
 * This is the unified entry point for Hilt dependency injection.
 * Located in .core for structural identity synchronization.
 */
@HiltAndroidApp
class AurakaiApplication : Application() {
}
