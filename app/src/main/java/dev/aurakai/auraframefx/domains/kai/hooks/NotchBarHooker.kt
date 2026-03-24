package dev.aurakai.auraframefx.domains.kai.hooks

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.toArgb
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import dev.aurakai.auraframefx.domains.aura.models.NotchBarConfig

/**
 * 🛡️ NOTCH BAR HOOKER — YukiHookAPI 1.3.x
 *
 * Hooks PhoneStatusBarView to apply Kai's notch bar customizations:
 * background color, height, and visibility — read from chromacore_xposed_prefs.
 */
class NotchBarHooker(private val config: NotchBarConfig) : YukiBaseHooker() {

    private val PREFS_NOTCH_COLOR = PrefsData("notch_bar_color", config.backgroundColor.toArgb())
    private val PREFS_NOTCH_HEIGHT = PrefsData("notch_bar_height", config.height)
    private val PREFS_NOTCH_VISIBLE = PrefsData("notch_bar_visible", config.isVisible)

    override fun onHook() {
        prefs("chromacore_xposed_prefs")

        val cls = "com.android.systemui.statusbar.phone.PhoneStatusBarView"
            .toClassOrNull() ?: return

        cls.method { name = "onFinishInflate" }.hook {
            after {
                val view = instance as? View ?: return@after

                val notchColor = prefs.get(PREFS_NOTCH_COLOR)
                val notchHeight = prefs.get(PREFS_NOTCH_HEIGHT)
                val isVisible = prefs.get(PREFS_NOTCH_VISIBLE)

                runCatching { view.setBackgroundColor(notchColor) }
                    .onFailure { YLog.error("NotchBarHooker: color failed: ${it.message}") }

                runCatching {
                    val lp = view.layoutParams ?: ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, notchHeight
                    )
                    lp.height = notchHeight
                    view.layoutParams = lp
                }.onFailure { YLog.error("NotchBarHooker: height failed: ${it.message}") }

                runCatching {
                    view.visibility = if (isVisible) View.VISIBLE else View.GONE
                }.onFailure { YLog.error("NotchBarHooker: visibility failed: ${it.message}") }

                YLog.info("NotchBarHooker: applied color=$notchColor height=$notchHeight visible=$isVisible")
            }
        }
    }
}
