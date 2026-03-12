package dev.aurakai.auraframefx.core.initialization

import android.app.Application

class TimberInitializerImpl : TimberInitializer {
    override fun init(app: Application) {
        // No-op for release builds
    }
}
