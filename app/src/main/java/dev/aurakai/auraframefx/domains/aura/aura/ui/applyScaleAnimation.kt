package dev.aurakai.auraframefx.aura.ui

import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.aurakai.auraframefx.aura.animations.LockScreenConfigAnimation

@LegacyHookApi
class LockScreenHooker(val config: LockScreenConfig) : YukiBaseHooker() {

    private lateinit var choosename: String

    override fun onHook() {
        // TODO: Implement actual Xposed hook logic
        YLog.warn("LockScreenHooker: onHook() called but no hooks implemented yet")
    }

}

@LegacyHookApi
private fun applyGenesisShowAnimation(
    lockScreenHooker: LockScreenHooker, applySlideAnimation: () -> Unit,
    applyFadeAnimation: () -> Unit,
    applyScaleAnimation: () -> Unit
) {
    try {
        // Some generated models may not expose an `animationType` property directly.
        // To avoid compile-time failures, derive the animation type from the
        // animation object's string representation and map to our enum.
        val animationType = try {
            val animObj = lockScreenHooker.config.animation
            run {
                // Use the object's toString() (or name-like) value and try to map it
                val nameLike = animObj.toString()
                LockScreenConfigAnimation.AnimationType.entries.firstOrNull {
                    it.name.equals(
                        nameLike,
                        ignoreCase = true
                    ) || it.name.equals(nameLike.removePrefix("AnimationType."), ignoreCase = true)
                } ?: LockScreenConfigAnimation.AnimationType.Zoom
            }
        } catch (t: Throwable) {
            YLog.warn("Failed to resolve animation type via toString(), defaulting to Zoom: ${t.message}")
            LockScreenConfigAnimation.AnimationType.Zoom
        }

        when (animationType) {
            LockScreenConfigAnimation.AnimationType.Slide -> applySlideAnimation()
            LockScreenConfigAnimation.AnimationType.Fade -> applyFadeAnimation()
            LockScreenConfigAnimation.AnimationType.Zoom -> applyScaleAnimation()
        }
    } catch (e: Exception) {
        YLog.error("Error applying animation", e)
    }
}

