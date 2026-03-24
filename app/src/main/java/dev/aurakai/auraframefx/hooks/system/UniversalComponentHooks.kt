package dev.aurakai.auraframefx.hooks.system

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam

/**
 * UniversalComponentHooks - Hook ANY Android Component for LDO Access
 */
class UniversalComponentHooks {

    fun initializeUniversalHooks(hooker: PackageParam) = hooker.apply {

        // === ACTIVITY HOOKS (All Apps) ===
        "android.app.Activity".toClass().resolve().firstMethod {
            name = "onCreate"
            parameters("android.os.Bundle")
        }.hook {
            after {
                YLog.info("UniversalHook: Activity created")
            }
        }

        // === NOTIFICATION HOOKS ===
        "android.app.NotificationManager".toClass().resolve().firstMethod {
            name = "notify"
            parameters("java.lang.String", "int", "android.app.Notification")
        }.hook {
            before {
                // Safe argument access
                if (args.isNotEmpty()) {
                    YLog.info("UniversalHook: Notification posted")
                }
            }
        }
    }
}
