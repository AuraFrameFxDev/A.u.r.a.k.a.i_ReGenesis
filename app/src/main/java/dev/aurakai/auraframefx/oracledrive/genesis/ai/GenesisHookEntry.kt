package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.engine.hooks.ChromaCoreHooker
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.overlays.quicksettings.QuickSettingsHooker
import dev.aurakai.auraframefx.domains.aura.models.NotchBarConfig
import dev.aurakai.auraframefx.domains.kai.security.NotchBarHooker

/**
 * 🌌 GENESIS HOOK ENTRY POINT — YukiHookAPI 1.3.x
 *
 * Single LSPosed/Xposed entry for all ReGenesis hooks.
 * KSP generates GenesisXposedEntry from @InjectYukiHookWithXposed,
 * which is referenced in assets/xposed_init.
 *
 * Hook scope (must match assets/xposed_init and res/values/arrays.xml xposed_scope):
 *   com.android.systemui           → StatusBar, NotchBar, QuickSettings
 *   com.android.launcher3          → Grid / icon density overrides
 *   com.google.android.apps.nexuslauncher → Pixel Launcher
 *   com.android.settings           → ColorBlendr Material You engine
 */
@InjectYukiHookWithXposed(entryClassName = "GenesisXposedEntry")
class GenesisHookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        isDebug = BuildConfig.DEBUG
    }

    override fun onHook() = encase {

        // ── SystemUI ─────────────────────────────────────────────────────────
        loadApp(name = "com.android.systemui") {
            loadHooker(ChromaCoreHooker())
            loadHooker(NotchBarHooker(NotchBarConfig()))
            loadHooker(QuickSettingsHooker())
        }

        // ── Launcher3 base ───────────────────────────────────────────────────
        loadApp(name = "com.android.launcher3") {
            loadHooker(ChromaCoreHooker())
        }

        // ── Pixel Launcher (Google) ──────────────────────────────────────────
        loadApp(name = "com.google.android.apps.nexuslauncher") {
            loadHooker(ChromaCoreHooker())
        }

        // ── Settings (ColorBlendr Material You) ──────────────────────────────
        loadApp(name = "com.android.settings") {
            loadHooker(ChromaCoreHooker())
        }
    }
}
