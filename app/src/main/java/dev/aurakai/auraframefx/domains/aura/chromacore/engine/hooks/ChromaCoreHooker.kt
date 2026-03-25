package dev.aurakai.auraframefx.domains.aura.chromacore.engine.hooks

import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * 🎨 CHROMA CORE HOOKER — Fully Migrated to KavaRef
 *
 * Uses KavaRef for all reflection operations (class, method, field).
 * Loaded by GenesisHookEntry into SystemUI, Launcher3, Pixel Launcher, Settings.
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
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".resolve().method("onFinishInflate").hook {
            after {
                val bgTransparent = prefs.getBoolean("statusbar_bg_transparent", false)
                val showIcons = prefs.getBoolean("statusbar_show_icons", true)
                YLog.info("ChromaCore·StatusBar transparent=$bgTransparent icons=$showIcons")

                if (bgTransparent) {
                    runCatching {
                        (instance as? View)?.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                }
            }
        }
    }

    // ── Launcher Grid ─────────────────────────────────────────────────────────
    private fun hookLauncherGrid() {
        val cls = "com.android.launcher3.InvariantDeviceProfile".resolve()
            ?: "com.google.android.apps.nexuslauncher.NexusLauncherActivity".resolve()
            ?: return

        cls.method("init").hook {
            after {
                val grid = prefs.getString("launcher_grid_config", "5x5")
                YLog.info("ChromaCore·Launcher grid → $grid")

                runCatching {
                    val parts = grid.split("x")
                    if (parts.size == 2) {
                        val cols = parts[0].toIntOrNull() ?: return@runCatching
                        val rows = parts[1].toIntOrNull() ?: return@runCatching

                        // KavaRef field access (cleaner than raw reflection)
                        instance.resolve().field("numColumns").set(cols)
                        instance.resolve().field("numRows").set(rows)
                    }
                }
            }
        }
    }

    // ── Dynamic Colors (ColorBlendr Material You) ─────────────────────────────
    private fun hookDynamicColors() {
        "com.android.systemui.monet.ColorScheme".resolve().method("getColors").hook {
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
        "com.android.systemui.statusbar.phone.StatusBarWindowView".resolve().method("onApplyWindowInsets").hook {
            before {
                val hide = prefs.getBoolean("hide_display_cutout", false)
                if (hide) YLog.info("ChromaCore·Hiding display cutout")
            }
        }
    }
}
