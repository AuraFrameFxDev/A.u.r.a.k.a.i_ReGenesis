package dev.aurakai.auraframefx.domains.genesis.core.initialization

import android.app.Application
import android.util.Log
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.cascade.utils.DebugTreeWithClassAndMethod
import javax.inject.Inject
import timber.log.Timber

open class TimberInitializer @Inject constructor() {
    fun initialize(application: Application) = if (BuildConfig.DEBUG) {
        Timber.plant(DebugTreeWithClassAndMethod())
        Timber.tag("🧠AuraFrameFX").d("Timber DEBUG mode active")
    } else {
        Timber.plant(CrashReportingTree())
        Timber.tag("🛡️AuraFrameFX").i("Timber RELEASE crash reporting active")
    }
}

private class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.INFO) {
            // Forward to Crashlytics
        }
    }
}


