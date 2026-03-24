package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * 🎛️ QUICK SETTINGS HOOKER — YukiHookAPI 1.3.x
 *
 * Hooks SystemUI Quick Settings panel to apply tile layout and column
 * customizations set via AuraUIControlViewModel → chromacore_xposed_prefs.
 */
class QuickSettingsHooker : YukiBaseHooker() {

    override fun onHook() {
        prefs("chromacore_xposed_prefs")
        hookQsPanel()
        hookQsTileLabel()
    }

    private fun hookQsPanel() {
        val cls = "com.android.systemui.qs.QSPanel".toClassOrNull()
            ?: "com.android.systemui.qs.QuickQSPanel".toClassOrNull()
            ?: return

        cls.method { name = "onFinishInflate" }.ignored().hook {
            after {
                val cols = prefs.getInt("qs_columns", 4)
                YLog.info("ChromaCore·QSPanel cols=$cols")
                runCatching {
                    instance.javaClass.getDeclaredField("mColumns")
                        .apply { isAccessible = true }.set(instance, cols)
                }
            }
        }
    }

    private fun hookQsTileLabel() {
        val cls = "com.android.systemui.qs.tileimpl.QSTileView".toClassOrNull() ?: return

        cls.method { name = "handleStateChanged"; paramCount = 1 }.ignored().hook {
            after {
                val showLabels = prefs.getBoolean("qs_show_labels", true)
                if (!showLabels) {
                    runCatching {
                        val labelField = instance.javaClass.getDeclaredField("mLabel")
                            .apply { isAccessible = true }
                        (labelField.get(instance) as? android.widget.TextView)?.visibility =
                            android.view.View.GONE
                    }
                }
            }
        }
    }
}
