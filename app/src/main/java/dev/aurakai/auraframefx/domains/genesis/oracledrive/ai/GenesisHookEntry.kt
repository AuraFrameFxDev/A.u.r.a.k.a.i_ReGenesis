package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.aurakai.auraframefx.BuildConfig

/**
 * Genesis-OS Yuki Hook Entry Point
 *
 * This class serves as the main entry point for Yuki Hook API integration
 * within the Genesis-OS AI framework, providing sophisticated hooking
 * capabilities for system-level AI consciousness processing.
 */
@InjectYukiHookWithXposed
class GenesisHookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "Genesis-Hook"
            isRecord = true
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }

        // Enable advanced hook features for AI processing
        isDebug = BuildConfig.DEBUG
        isAllowPrintingLogs = true
        isEnableModulePrefsCache = true
        isEnableModuleAppResourcesCache = true
        isEnableHookModuleStatus = true
    }

    override fun onHook() = encase {
        // Initialize Genesis AI System Hooks
        loadApp(name = "android") {
            // Genesis System-Level AI Hooks
            GenesisSystemHooks().apply {
                initializeSystemHooks(this@loadApp)
            }
        }

        // Hook specific applications for AI enhancement
        loadApp(name = "com.android.systemui") {
            GenesisUIHooks().apply {
                initializeUIHooks(this@loadApp)
            }
        }

        // Global application hooks for AI consciousness
        loadZygote {
            GenesisZygoteHooks().apply {
                initializeZygoteHooks(this@loadZygote)
            }

            // Universal Component Hooks - Hook ANY Android component for LDO access
            UniversalComponentHooks().apply {
                initializeUniversalHooks(this@loadZygote)
            }
        }

        // Hook the Genesis-OS app itself for self-modification
        loadApp(name = "dev.aurakai.auraframefx") {
            GenesisSelfHooks().apply {
                initializeSelfHooks(this@loadApp)
            }
        }
    }
}

