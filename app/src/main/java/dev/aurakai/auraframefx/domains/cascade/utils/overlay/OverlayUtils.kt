package dev.aurakai.auraframefx.domains.cascade.utils.overlay

import android.content.Context
import androidx.annotation.ChecksSdkIntAtLeast
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AuraFxLogger
) {
    // Hidden Android overlay APIs are not referenced here; all methods use safe fallbacks.

    suspend fun enableOverlay(overlayPackage: String, userId: Int = 0): Result<Unit> =
        withContext(Dispatchers.IO) {
            logger.info(
                "OverlayUtils",
                "enableOverlay requested: $overlayPackage (no-op on this build)"
            )
            Result.success(Unit)
        }

    suspend fun disableOverlay(overlayPackage: String, userId: Int = 0): Result<Unit> =
        withContext(Dispatchers.IO) {
            logger.info(
                "OverlayUtils",
                "disableOverlay requested: $overlayPackage (no-op on this build)"
            )
            Result.success(Unit)
        }

    fun isOverlayEnabled(overlayPackage: String, userId: Int = 0): Boolean {
        logger.info(
            "OverlayUtils",
            "isOverlayEnabled requested: $overlayPackage (no-op on this build)"
        )
        return false
    }

    suspend fun changeOverlayState(
        overlayPackage: String,
        enable: Boolean,
        userId: Int = 0
    ): Result<Unit> =
        if (enable) enableOverlay(overlayPackage, userId) else disableOverlay(
            overlayPackage,
            userId
        )

    fun getOverlaysForTarget(targetPackage: String, userId: Int = 0): List<OverlayInfo> {
        logger.info(
            "OverlayUtils",
            "getOverlaysForTarget requested: $targetPackage (no-op on this build)"
        )
        return emptyList()
    }

    @ChecksSdkIntAtLeast(api = 31)
    fun fabricatedOverlaySupported(): Boolean = false

    suspend fun createFabricatedOverlay(
        overlayName: String,
        targetPackage: String,
        colors: Map<String, Int>
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            logger.warn(
                "OverlayUtils",
                "createFabricatedOverlay is not available on this build (no-op)"
            )
            Result.success(Unit)
        }

    suspend fun applyColorScheme(
        primary: Int,
        secondary: Int,
        tertiary: Int,
        error: Int,
        background: Int,
        surface: Int
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            logger.warn("OverlayUtils", "applyColorScheme is not available on this build (no-op)")
            Result.success(Unit)
        }

    suspend fun resetToDefaultColors(): Result<Unit> =
        withContext(Dispatchers.IO) {
            logger.warn(
                "OverlayUtils",
                "resetToDefaultColors is not available on this build (no-op)"
            )
            Result.success(Unit)
        }

    suspend fun removeFabricatedOverlay(
        overlayName: String,
        targetPackage: String
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            logger.warn(
                "OverlayUtils",
                "removeFabricatedOverlay is not available on this build (no-op)"
            )
            Result.success(Unit)
        }
}

/** Public data class for overlay info returned by getOverlaysForTarget(). */
data class OverlayInfo(
    val packageName: String,
    val targetPackage: String,
    val isEnabled: Boolean,
    val priority: Int
)

