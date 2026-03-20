package dev.aurakai.auraframefx.domains.kai.hooks

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.toArgb
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import dev.aurakai.auraframefx.domains.aura.models.NotchBarConfig

/**
 * YukiHook hooker for customizing the Android notch bar (status bar cutout area).
 * Applies visual customizations like color, height, and visibility.
 */
class NotchBarHooker(private val config: NotchBarConfig) : YukiBaseHooker() {

    private val PREFS_NOTCH_COLOR = PrefsData("notch_bar_color", config.backgroundColor.toArgb())
    private val PREFS_NOTCH_HEIGHT = PrefsData("notch_bar_height", config.height)
    private val PREFS_NOTCH_VISIBLE = PrefsData("notch_bar_visible", config.isVisible)

    override fun onHook() {
        // Hook the PhoneStatusBarView to apply customizations
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".toClassOrNull()?.method {
            name = "onFinishInflate"
        }?.hook {
            after {
                val view = instance as View

                // Retrieve persisted values if they differ from current config
                val notchColor = prefs.get(PREFS_NOTCH_COLOR)
                val notchHeight = prefs.get(PREFS_NOTCH_HEIGHT)
                val isVisible = prefs.get(PREFS_NOTCH_VISIBLE)

                // Apply notch bar color if configured
                try {
                    view.setBackgroundColor(notchColor)
                } catch (e: Exception) {
                    YLog.error("NotchBarHooker: Failed to set background color: ${e.message}")
                }

                // Apply notch bar height if configured
                try {
                    val layoutParams = view.layoutParams ?: ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        notchHeight
                    )
                    layoutParams.height = notchHeight
                    view.layoutParams = layoutParams
                } catch (e: Exception) {
                    YLog.error("NotchBarHooker: Failed to set height: ${e.message}")
                }

                // Apply visibility if configured
                try {
                    view.visibility = if (isVisible) View.VISIBLE else View.GONE
                } catch (e: Exception) {
                    YLog.error("NotchBarHooker: Failed to set visibility: ${e.message}")
                }

                YLog.info("NotchBarHooker: Successfully applied customizations to PhoneStatusBarView (Color: $notchColor, Height: $notchHeight, Visible: $isVisible)")
            }
        }
    }
}
