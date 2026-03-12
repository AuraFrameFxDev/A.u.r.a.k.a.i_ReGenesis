package dev.aurakai.auraframefx.domains.kai.hooks

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.toArgb
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.aurakai.auraframefx.domains.aura.models.NotchBarConfig

/**
 * YukiHook hooker for customizing the Android notch bar (status bar cutout area).
 * Applies visual customizations like color, height, and visibility.
 */
class NotchBarHooker(private val config: NotchBarConfig) : YukiBaseHooker() {

    override fun onHook() {
        // Hook the PhoneStatusBarView to apply customizations
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".toClassOrNull()?.method {
            name = "onFinishInflate"
        }?.hook {
            after {
                val view = instance as View

                // Apply notch bar color if configured
                try {
                    val androidColor = config.backgroundColor.toArgb()
                    view.setBackgroundColor(androidColor)
                } catch (e: Exception) {
                    YLog.error("NotchBarHooker: Failed to set background color: ${e.message}")
                }

                // Apply notch bar height if configured
                try {
                    val layoutParams = view.layoutParams ?: ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        config.height
                    )
                    layoutParams.height = config.height
                    view.layoutParams = layoutParams
                } catch (e: Exception) {
                    YLog.error("NotchBarHooker: Failed to set height: ${e.message}")
                }

                // Apply visibility if configured
                try {
                    view.visibility = if (config.isVisible) View.VISIBLE else View.GONE
                } catch (e: Exception) {
                    YLog.error("NotchBarHooker: Failed to set visibility: ${e.message}")
                }

                YLog.info("NotchBarHooker: Successfully applied customizations to PhoneStatusBarView")
            }
        }
    }
}
