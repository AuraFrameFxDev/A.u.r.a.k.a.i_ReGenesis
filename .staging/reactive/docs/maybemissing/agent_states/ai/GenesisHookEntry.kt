package dev.aurakai.auraframefx.xposed

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.aurakai.auraframefx.BuildConfig

/**
 * Genesis-OS Yuki Hook Entry Point
 *
 * Single @InjectYukiHookWithXposed entry for all Xposed hooks.
 * Every section is gated behind FeatureToggles.XPOSED_HOOKS_ENABLED
 * (master switch) plus its individual per-feature flag.
 *
 * To activate on a rooted/AOSP build: set XPOSED_HOOKS_ENABLED = true.
 * All individual flags default true, so the master switch is the only
 * change needed for a full activation.
 */
@InjectYukiHookWithXposed
class GenesisHookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "Genesis-Hook"
            isRecord = true
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }
        isDebug = BuildConfig.DEBUG
        isAllowPrintingLogs = true
        isEnableModulePrefsCache = true
        isEnableModuleAppResourcesCache = true
        isEnableHookModuleStatus = true
    }

    override fun onHook() = encase {
        // ── Master gate ──────────────────────────────────────────────────────
        if (!FeatureToggles.XPOSED_HOOKS_ENABLED) return@encase

        // ── android process: system-level AI hooks ───────────────────────────
        if (FeatureToggles.XPOSED_GENESIS_SYSTEM_HOOKS) {
            loadApp(name = "android") {
                GenesisSystemHooks().initializeSystemHooks(this@loadApp)
            }
        }

        // ── com.android.systemui: StatusBar / Notch / QS / Chroma hooks ──────
        loadApp(name = "com.android.systemui") {
            if (FeatureToggles.XPOSED_GENESIS_UI_HOOKS) {
                GenesisUIHooks().initializeUIHooks(this@loadApp)
            }

            if (FeatureToggles.XPOSED_NOTCH_BAR_HOOKER) {
                loadHooker(NotchBarHooker(NotchBarConfig()))
            }

            if (FeatureToggles.XPOSED_QS_HOOKER) {
                loadHooker(QuickSettingsHooker(QuickSettingsConfig.DEFAULT))
            }

            if (FeatureToggles.XPOSED_CHROMA_CORE_HOOKER) {
                loadHooker(ChromaCoreHooker())
            }

            if (FeatureToggles.XPOSED_LOCKSCREEN_HOOKS) {
                loadHooker(LockScreenAnimationHooker())
            }
        }

        // ── Zygote: early-stage global hooks ─────────────────────────────────
        loadZygote {
            if (FeatureToggles.XPOSED_GENESIS_ZYGOTE_HOOKS) {
                GenesisZygoteHooks().initializeZygoteHooks(this@loadZygote)
            }

            // Universal Component Hooks - Hook ANY Android component for LDO access
            UniversalComponentHooks().apply {
                initializeUniversalHooks(this@loadZygote)
            }
        }

        // ── Genesis-OS self hooks ─────────────────────────────────────────────
        if (FeatureToggles.XPOSED_GENESIS_SELF_HOOKS) {
            loadApp(name = "dev.aurakai.auraframefx") {
                GenesisSelfHooks().initializeSelfHooks(this@loadApp)
            }
        }
    }
}
