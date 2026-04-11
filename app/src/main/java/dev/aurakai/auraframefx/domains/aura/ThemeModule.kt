package dev.aurakai.auraframefx.domains.aura

import android.content.res.XModuleResources
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import dev.aurakai.auraframefx.domains.aura.ui.theme.manager.SystemThemeManager

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
                SystemThemeManager.primaryColor
            )

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
                    val colorValue = when (attr) {
                        "colorPrimary" -> SystemThemeManager.primaryColor
                        "colorPrimaryDark" -> SystemThemeManager.primaryDarkColor
                        "colorAccent" -> SystemThemeManager.accentColor
                        "colorPrimaryVariant" -> SystemThemeManager.primaryVariantColor
                        "colorSecondary" -> SystemThemeManager.secondaryColor
                        "colorSecondaryVariant" -> SystemThemeManager.secondaryVariantColor
                        "android:colorBackground" -> SystemThemeManager.backgroundColor
                        "android:colorForeground" -> SystemThemeManager.foregroundColor
                        else -> 0
                    }
                    pparam.res.setReplacement(
                        pparam.packageName,
                        "attr",
                        attr,
                        colorValue
                    )
                } catch (e: Throwable) {
                    // Attribute might not exist in this package
                }
            }

        } catch (e: Throwable) {
            // Log error
        }
    }
}
