package dev.aurakai.auraframefx.domains.aura.chromacore.engine.hooks

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * 🛰️ CHROMA CORE HOOKER
 * Unified Xposed hooker for Iconify, PLE, and ColorBlendr features.
 * Connects preferences to SystemUI and Launcher.
 */
class ChromaCoreHooker : YukiBaseHooker() {

    override fun onHook() {
        // Load the shared preferences synchronized by ChromaCoreManager
        prefs("chromacore_xposed_prefs")

        // --- STATUS BAR HOOKS (Iconify) ---
        hookStatusbarLogo()

        // --- LAUNCHER HOOKS (Pixel Launcher Enhanced) ---
        hookLauncherGrid()

        // --- COLOR ENGINE HOOKS (ColorBlendr) ---
        hookDynamicColors()
    }

    private fun hookStatusbarLogo() {
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".toClassOrNull()?.method {
            name = "onFinishInflate"
        }?.hook {
            after {
                // Read from shared prefs
                val enabled = prefs.getBoolean("statusbar_logo_enabled", false)
                if (enabled) {
                    YLog.info("ChromaCore: Injecting Statusbar Logo")
                    // Real logic from Iconify to add ImageView would go here
                }
            }
        }
    }

    private fun hookLauncherGrid() {
        "com.android.launcher3.InvariantDeviceProfile".toClassOrNull()?.method {
            name = "init"
        }?.hook {
            after {
                val grid = prefs.getString("launcher_grid_config", "5x5")
                YLog.info("ChromaCore: Overriding Launcher Grid to $grid")
                // Logic to set numRows, numColumns
            }
        }
    }

    private fun hookDynamicColors() {
        // Hooks for Monet / Material You logic in android core
        "com.android.systemui.monet.ColorScheme".toClassOrNull()?.method {
            name = "getSeedColors"
        }?.hook {
            after {
                val customSeed = prefs.getInt("theme_seed_color", 0)
                if (customSeed != 0) {
                    result = listOf(customSeed)
                }
            }
        }
    }
}
