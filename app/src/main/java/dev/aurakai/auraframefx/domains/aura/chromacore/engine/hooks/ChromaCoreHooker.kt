package dev.aurakai.auraframefx.domains.aura.chromacore.engine.hooks

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * 🎨 CHROMA CORE HOOKER — YukiHookAPI 1.3.x
 *
 * Unified Xposed hooker for ChromaCore (Iconify + ColorBlendr + PixelLauncher).
 * Loaded by GenesisHookEntry into: SystemUI, Launcher3, Pixel Launcher, Settings.
 *
 * Reads prefs from "chromacore_xposed_prefs" written by AuraUIControlViewModel.
 * Uses YukiHookAPI's built-in toClassOrNull() for safe class resolution.
 */
class ChromaCoreHooker : YukiBaseHooker() {

    override fun onHook() {
        prefs("chromacore_xposed_prefs")

        hookStatusBar()
        hookLauncherGrid()
        hookDynamicColors()
        hookNotchCutout()
    }

    // ── Status Bar ────────────────────────────────────────────────────────────

    private fun hookStatusBar() {
        "com.android.systemui.statusbar.phone.PhoneStatusBarView"
            .toClassOrNull() ?: return

        "com.android.systemui.statusbar.phone.PhoneStatusBarView".toClassOrNull()
            ?.method { name = "onFinishInflate" }
            ?.hook {
                after {
                    val bgTransparent = prefs.getBoolean("statusbar_bg_transparent", false)
                    val showIcons = prefs.getBoolean("statusbar_show_icons", true)
                    YLog.info("ChromaCore·StatusBar transparent=$bgTransparent icons=$showIcons")
                    if (bgTransparent) {
                        runCatching {
                            (instance as View).setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        }
                    }
                }
            }
    }

    // ── Launcher Grid ─────────────────────────────────────────────────────────

    private fun hookLauncherGrid() {
        val cls = "com.android.launcher3.InvariantDeviceProfile".toClassOrNull()
            ?: "com.google.android.apps.nexuslauncher.NexusLauncherActivity".toClassOrNull()
            ?: return

        cls.method { name = "init"; paramCount = 0 }.ignored().hook {
            after {
                val grid = prefs.getString("launcher_grid_config", "5x5")
                YLog.info("ChromaCore·Launcher grid → $grid")
                runCatching {
                    val parts = grid.split("x")
                    if (parts.size == 2) {
                        val cols = parts[0].toIntOrNull() ?: return@runCatching
                        val rows = parts[1].toIntOrNull() ?: return@runCatching
                        instance.javaClass.getDeclaredField("numColumns")
                            .apply { isAccessible = true }.set(instance, cols)
                        instance.javaClass.getDeclaredField("numRows")
                            .apply { isAccessible = true }.set(instance, rows)
                    }
                }
            }
        }
    }

    // ── Dynamic Colors (ColorBlendr Material You) ─────────────────────────────

    private fun hookDynamicColors() {
        val cls = "com.android.systemui.monet.ColorScheme".toClassOrNull() ?: return

        cls.method { name = "getColors"; paramCount = 0 }.ignored().hook {
            after {
                val customSeed = prefs.getInt("colorblendr_seed_color", -1)
                if (customSeed != -1) {
                    YLog.info("ChromaCore·ColorBlendr seed=#${Integer.toHexString(customSeed)}")
                }
            }
        }
    }

    // ── Notch / Display Cutout ────────────────────────────────────────────────

    private fun hookNotchCutout() {
        val cls = "com.android.systemui.statusbar.phone.StatusBarWindowView"
            .toClassOrNull() ?: return

        cls.method { name = "onApplyWindowInsets"; paramCount = 1 }.ignored().hook {
            before {
                val hide = prefs.getBoolean("hide_display_cutout", false)
                if (hide) YLog.info("ChromaCore·Hiding display cutout")
            }
        }
    }
}
