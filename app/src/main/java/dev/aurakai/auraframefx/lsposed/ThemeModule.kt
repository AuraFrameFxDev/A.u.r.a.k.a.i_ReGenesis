package dev.aurakai.auraframefx.lsposed

import android.content.res.XModuleResources
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources

/**
 * Xposed module for application of custom themes via LSPosed.
 */
class ThemeModule : IXposedHookZygoteInit, IXposedHookInitPackageResources {

    private var modulePath: String? = null
    private var modRes: XModuleResources? = null

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        modRes = XModuleResources.createInstance(modulePath, null)
    }

    override fun handleInitPackageResources(pparam: XC_InitPackageResources.InitPackageResourcesParam) {
        // We'll target all packages for system-wide theming
        applyThemes(pparam)
    }

    private fun applyThemes(pparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            // Hook into resource system to override colors
            pparam.res.setReplacement(
                pparam.packageName,
                "color",
                "colorPrimary",
                object : XC_InitPackageResources.DynamicResource(pparam.packageName) {
                    override fun getValue(): Int {
                        // Return the dynamic color value from our theme
                        return ThemeManager.primaryColor
                    }
                })

            // Add more resource replacements as needed
            // Example for common Material color attributes
            val colorAttrs = arrayOf(
                "colorPrimary",
                "colorPrimaryDark",
                "colorAccent",
                "colorPrimaryVariant",
                "colorSecondary",
                "colorSecondaryVariant",
                "android:colorBackground",
                "android:colorForeground"
            )

            colorAttrs.forEach { attr ->
                try {
                    pparam.res.setReplacement(
                        pparam.packageName,
                        "attr",
                        attr,
                        object : XC_InitPackageResources.DynamicResource(pparam.packageName) {
                            override fun getValue(): Int {
                                return when (attr) {
                                    "colorPrimary" -> ThemeManager.primaryColor
                                    "colorPrimaryDark" -> ThemeManager.primaryDarkColor
                                    "colorAccent" -> ThemeManager.accentColor
                                    "colorPrimaryVariant" -> ThemeManager.primaryVariantColor
                                    "colorSecondary" -> ThemeManager.secondaryColor
                                    "colorSecondaryVariant" -> ThemeManager.secondaryVariantColor
                                    "android:colorBackground" -> ThemeManager.backgroundColor
                                    "android:colorForeground" -> ThemeManager.foregroundColor
                                    else -> 0
                                }
                            }
                        })
                } catch (e: Throwable) {
                    // Attribute might not exist in this package
                }
            }

        } catch (e: Throwable) {
            // Log error
        }
    }
}
