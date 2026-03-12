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
                    // Apply notch bar color if configured
                    config.backgroundColor?.let { color ->
                        instance.javaClass.getDeclaredField("mBackground")?.apply {
                            isAccessible = true
                            set(instance, color)
                        }
                    }

                    // Apply notch bar height if configured
                    config.height?.let { height ->
                        instance.javaClass.getDeclaredMethod(
                            "setLayoutParams",
                            android.view.ViewGroup.LayoutParams::class.java
                        ).invoke(instance, android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            height
                        ))
                    }

                    // Apply visibility if configured
                    if (config.isVisible == false) {
                        instance.javaClass.getDeclaredMethod(
                            "setVisibility",
                            IntType
                        ).invoke(instance, android.view.View.GONE)
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't crash - notch bar customization is optional
            android.util.Log.e("NotchBarHooker", "Failed to apply notch bar hooks", e)
        }
    }
}
