package dev.aurakai.auraframefx.xposed.hooks

import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.aurakai.auraframefx.system.overlay.model.NotchBarConfig

/**
 * Xposed hooker for customizing the Android notch bar (status bar cutout area).
 * Applies visual customizations like color, height, and visibility.
 */
class NotchBarHooker(
    private val classLoader: ClassLoader,
    private val config: NotchBarConfig,
) {
    /**
     * Applies Xposed hooks to customize the notch bar appearance and behavior.
     * 
     * Hooks into system UI classes to modify:
     * - Notch bar background color
     * - Notch bar height/size
     * - Notch bar visibility
     */
    fun applyNotchBarHooks() {
        try {
            // Hook the PhoneStatusBarView or similar system UI class
            // Note: Actual class names vary by Android version and OEM
            val statusBarClass = classLoader.loadClass(
                "com.android.systemui.statusbar.phone.PhoneStatusBarView"
            )

            // Hook the onFinishInflate method to apply customizations
            statusBarClass.method {
                name = "onFinishInflate"
                emptyParam()
            }.hook {
                after {
                    val view = this.instance as android.view.View
                    // Apply notch bar color if configured
                    config.backgroundColor?.let { color ->
                        try {
                            val backgroundField = view::class.java.getDeclaredField("mBackground")
                            backgroundField.isAccessible = true
                            backgroundField.set(view, color)
                        } catch (ignored: Exception) {}
                    }

                    // Apply notch bar height if configured
                    config.height?.let { height ->
                        val layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            height
                        )
                        view.layoutParams = layoutParams
                    }

                    // Apply visibility if configured
                    if (config.isVisible == false) {
                        view.visibility = android.view.View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't crash - notch bar customization is optional
            android.util.Log.e("NotchBarHooker", "Failed to apply notch bar hooks", e)
        }
    }
}
