package dev.aurakai.auraframefx.domains.genesis.core.initialization

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import dev.aurakai.auraframefx.BuildConfig
import timber.log.Timber

/**
 * Initializes core app components during application startup.
 *
 * This initializer runs before the Application class onCreate() and sets up:
 * - Timber logging
 * - StrictMode (in debug builds)
 * - Core system checks
 *
 * Uses AndroidX App Startup library for efficient initialization.
 */
class AppInitializerInitializer : Initializer<Unit> {

    /**
     * Performs application-specific initialization logic during app startup.
     *
     * This method is called on the main thread when the application launches.
     * Initializes logging, debugging tools, and validates system prerequisites.
     */
    override fun create(context: Context) {
        // Initialize Timber logging
        initializeLogging()

        // Enable StrictMode in debug builds
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }

        // Perform system checks
        performSystemChecks(context)

        Timber.i("AppInitializerInitializer: Application initialization complete")
    }

    /**
     * Initializes Timber logging framework.
     */
    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            // Debug tree with class name, method, and line number
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "Genesis:${super.createStackElementTag(element)}:${element.lineNumber}"
                }
            })
            Timber.d("AppInitializerInitializer: Timber logging initialized (DEBUG)")
        } else {
            // Production tree (can be customized to report to crash analytics)
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // In production, you would send logs to:
                    // - Firebase Crashlytics
                    // - Custom analytics service
                    // - Cloud logging service
                }
            })
            Timber.i("AppInitializerInitializer: Timber logging initialized (RELEASE)")
        }
    }

    /**
     * Enables StrictMode for detecting performance issues in debug builds.
     */
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .detectLeakedRegistrationObjects()
                .penaltyLog()
                .build()
        )

        Timber.d("AppInitializerInitializer: StrictMode enabled for development")
    }

    /**
     * Performs basic system checks and logs device information.
     */
    private fun performSystemChecks(context: Context) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                }

            Timber.i("AppInitializerInitializer: Genesis Protocol v$versionName ($versionCode)")
            Timber.i("AppInitializerInitializer: Android SDK ${android.os.Build.VERSION.SDK_INT}")
            Timber.i("AppInitializerInitializer: Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")

        } catch (e: Exception) {
            Timber.e(e, "AppInitializerInitializer: Failed to retrieve system information")
        }
    }

    /**
     * Returns a list of initializer classes that this initializer depends on.
     *
     * This initializer has no dependencies and runs first.
     *
     * @return An empty list, indicating that this initializer has no dependencies.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies - this runs first
        return emptyList()
    }
}

