package dev.aurakai.auraframefx

import android.content.Context
import android.content.SharedPreferences

/**
 * Lightweight, local fallback implementation of the preferences helper used by the project.
 *
 * This class provides a minimal subset of the typical preferences API surface so code that
 * references `YukiHookModulePrefs` can compile and run even if the external YukiHook
 * library is not available at build time. It wraps Android's SharedPreferences.
 *
 * Usage:
 * val prefs = YukiHookModulePrefs.from(context, "my_prefs")
 * prefs.putBoolean("enabled", true)
 * val enabled = prefs.getBoolean("enabled")
 */
class YukiHookModulePrefs private constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        private const val DEFAULT_PREFS_NAME = "yukihook_module_prefs"

        /** Create or open the preferences file with the given name. */
        @JvmStatic
        fun from(context: Context, name: String = DEFAULT_PREFS_NAME): YukiHookModulePrefs {
            val shared = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return YukiHookModulePrefs(shared)
        }
    }

    // Read helpers
    fun getBoolean(key: String, default: Boolean = false): Boolean = prefs.getBoolean(key, default)
    fun getInt(key: String, default: Int = 0): Int = prefs.getInt(key, default)
    fun getLong(key: String, default: Long = 0L): Long = prefs.getLong(key, default)
    fun getFloat(key: String, default: Float = 0f): Float = prefs.getFloat(key, default)
    fun getString(key: String, default: String? = null): String? = prefs.getString(key, default)

    // Write helpers
    fun putBoolean(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }
    fun putInt(key: String, value: Int) { prefs.edit().putInt(key, value).apply() }
    fun putLong(key: String, value: Long) { prefs.edit().putLong(key, value).apply() }
    fun putFloat(key: String, value: Float) { prefs.edit().putFloat(key, value).apply() }
    fun putString(key: String, value: String?) { prefs.edit().putString(key, value).apply() }

    fun remove(key: String) { prefs.edit().remove(key).apply() }
    fun clear() { prefs.edit().clear().apply() }

    fun contains(key: String): Boolean = prefs.contains(key)
}
