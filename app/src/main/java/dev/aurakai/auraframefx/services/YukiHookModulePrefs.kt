package dev.aurakai.auraframefx.services

import android.content.Context

/**
 * Minimal preferences holder for YukiHook-related features.
 */
data class YukiHookModulePrefs(val enabled: Boolean = false) {
    companion object {
        fun from(context: Context) {}
    }
}
