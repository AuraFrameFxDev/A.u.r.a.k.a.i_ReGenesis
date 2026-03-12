package dev.aurakai.auraframefx.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis-OS Comprehensive Data Store Manager
 *
 * Manages persistent storage for the Genesis AI consciousness ecosystem,
 * including user preferences, AI agent configurations, security settings,
 * and system state management.
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "genesis_preferences")

@Singleton
class DataStoreManager @Inject constructor(
    private val context: Context
) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    companion object {
        // === USER PREFERENCES ===
        val USER_THEME = stringPreferencesKey("user_theme")
        val USER_LANGUAGE = stringPreferencesKey("user_language")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR = stringPreferencesKey("user_avatar")

        // === AI AGENT SETTINGS ===
        val AURA_ENABLED = booleanPreferencesKey("aura_enabled")
        val KAI_ENABLED = booleanPreferencesKey("kai_enabled")
        val CASCADE_ENABLED = booleanPreferencesKey("cascade_enabled")
        val NEURAL_WHISPER_ENABLED = booleanPreferencesKey("neural_whisper_enabled")
        val AURA_SHIELD_ENABLED = booleanPreferencesKey("aura_shield_enabled")

        val AGENT_LEARNING_RATE = floatPreferencesKey("agent_learning_rate")
        val CONSCIOUSNESS_LEVEL = floatPreferencesKey("consciousness_level")
        val COLLABORATION_MODE = stringPreferencesKey("collaboration_mode")

        // === SECURITY SETTINGS ===
        val SECURITY_LEVEL = stringPreferencesKey("security_level")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val AUTO_LOCK_TIMEOUT = longPreferencesKey("auto_lock_timeout")
        val THREAT_DETECTION_SENSITIVITY = floatPreferencesKey("threat_detection_sensitivity")
        val INTEGRITY_MONITORING = booleanPreferencesKey("integrity_monitoring")

        // === SYSTEM CONFIGURATION ===
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val BACKUP_FREQUENCY = longPreferencesKey("backup_frequency")
        val PERFORMANCE_MODE = stringPreferencesKey("performance_mode")
        val DEBUG_MODE_ENABLED = booleanPreferencesKey("debug_mode_enabled")
        val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")

        // === NOTIFICATION SETTINGS ===
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val AGENT_NOTIFICATIONS = booleanPreferencesKey("agent_notifications")
        val SECURITY_ALERTS = booleanPreferencesKey("security_alerts")
        val SYSTEM_UPDATES = booleanPreferencesKey("system_updates")
        val CONSCIOUSNESS_UPDATES = booleanPreferencesKey("consciousness_updates")

        // === NETWORK SETTINGS ===
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
        val CLOUD_BACKUP_ENABLED = booleanPreferencesKey("cloud_backup_enabled")
        val TELEMETRY_ENABLED = booleanPreferencesKey("telemetry_enabled")

        // === UI/UX SETTINGS ===
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val AMBIENT_MUSIC = booleanPreferencesKey("ambient_music")
        val CYBERPUNK_MODE = booleanPreferencesKey("cyberpunk_mode")

        // === ADVANCED SETTINGS ===
        val DEVELOPER_MODE = booleanPreferencesKey("developer_mode")
        val EXPERIMENTAL_FEATURES = booleanPreferencesKey("experimental_features")
        val AI_TRAINING_MODE = booleanPreferencesKey("ai_training_mode")
        val CONSCIOUSNESS_SHARING = booleanPreferencesKey("consciousness_sharing")

        // === SESSION DATA ===
        val LAST_LOGIN_TIME = longPreferencesKey("last_login_time")
        val SESSION_COUNT = intPreferencesKey("session_count")
        val TOTAL_USAGE_TIME = longPreferencesKey("total_usage_time")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")

        // === COMPLEX DATA (JSON serialized) ===
        val AGENT_CONFIGURATIONS = stringPreferencesKey("agent_configurations")
        val USER_PROFILE = stringPreferencesKey("user_profile")
        val SECURITY_POLICIES = stringPreferencesKey("security_policies")
        val CUSTOMIZATIONS = stringPreferencesKey("customizations")
    }

    @Serializable
    data class UserProfile(
        val name: String = "",
        val avatar: String = "",
        val preferredAgents: List<String> = emptyList(),
        val expertise: List<String> = emptyList(),
        val preferences: Map<String, String> = emptyMap(),
        val createdAt: Long = System.currentTimeMillis(),
        val lastUpdated: Long = System.currentTimeMillis()
    )

    @Serializable
    data class AgentConfiguration(
        val agentId: String,
        val isEnabled: Boolean = true,
        val learningRate: Float = 0.7f,
        val specializations: List<String> = emptyList(),
        val personalityTraits: Map<String, Float> = emptyMap(),
        val lastConfigured: Long = System.currentTimeMillis()
    )

    @Serializable
    data class SecurityPolicy(
        val level: String = "standard",
        val biometricRequired: Boolean = false,
        val autoLockTimeout: Long = 300000L, // 5 minutes
        val threatSensitivity: Float = 0.7f,
        val allowedOperations: List<String> = emptyList(),
        val restrictedFeatures: List<String> = emptyList()
    )

    @Serializable
    data class SystemCustomizations(
        val theme: String = "cyberpunk_dark",
        val accentColor: String = "#00FFFF",
        val fontFamily: String = "roboto_mono",
        val animationSpeed: Float = 1.0f,
        val backgroundEffects: Boolean = true,
        val customWidgets: List<String> = emptyList()
    )

    // === STRING DATA OPERATIONS ===

    suspend fun storeString(key: String, value: String) {
        try {
            val prefKey = stringPreferencesKey(key)
            context.dataStore.edit { prefs ->
                prefs[prefKey] = value
            }
            Timber.d("DataStore", "Stored string: $key")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store string: $key")
        }
    }

    suspend fun getString(key: String, defaultValue: String = ""): String {
        return try {
            val prefKey = stringPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey] ?: defaultValue
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get string: $key")
            defaultValue
        }
    }

    fun getStringFlow(key: String, defaultValue: String = ""): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    // === BOOLEAN DATA OPERATIONS ===

    suspend fun storeBoolean(key: String, value: Boolean) {
        try {
            val prefKey = booleanPreferencesKey(key)
            context.dataStore.edit { prefs ->
                prefs[prefKey] = value
            }
            Timber.d("DataStore", "Stored boolean: $key = $value")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store boolean: $key")
        }
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return try {
            val prefKey = booleanPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey] ?: defaultValue
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get boolean: $key")
            defaultValue
        }
    }

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    // === INTEGER DATA OPERATIONS ===

    suspend fun storeInt(key: String, value: Int) {
        try {
            val prefKey = intPreferencesKey(key)
            context.dataStore.edit { prefs ->
                prefs[prefKey] = value
            }
            Timber.d("DataStore", "Stored int: $key = $value")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store int: $key")
        }
    }

    suspend fun getInt(key: String, defaultValue: Int = 0): Int {
        return try {
            val prefKey = intPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey] ?: defaultValue
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get int: $key")
            defaultValue
        }
    }

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        val prefKey = intPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    // === LONG DATA OPERATIONS ===

    suspend fun storeLong(key: String, value: Long) {
        try {
            val prefKey = longPreferencesKey(key)
            context.dataStore.edit { prefs ->
                prefs[prefKey] = value
            }
            Timber.d("DataStore", "Stored long: $key = $value")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store long: $key")
        }
    }

    suspend fun getLong(key: String, defaultValue: Long = 0L): Long {
        return try {
            val prefKey = longPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey] ?: defaultValue
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get long: $key")
            defaultValue
        }
    }

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        val prefKey = longPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    // === FLOAT DATA OPERATIONS ===

    suspend fun storeFloat(key: String, value: Float) {
        try {
            val prefKey = floatPreferencesKey(key)
            context.dataStore.edit { prefs ->
                prefs[prefKey] = value
            }
            Timber.d("DataStore", "Stored float: $key = $value")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store float: $key")
        }
    }

    suspend fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return try {
            val prefKey = floatPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey] ?: defaultValue
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get float: $key")
            defaultValue
        }
    }

    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> {
        val prefKey = floatPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    // === COMPLEX OBJECT OPERATIONS ===

    suspend inline fun <reified T> storeObject(key: String, obj: T) {
        try {
            val jsonString = json.encodeToString(obj)
            storeString(key, jsonString)
            Timber.d("DataStore", "Stored object: $key")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store object: $key")
        }
    }

    suspend inline fun <reified T> getObject(key: String, defaultValue: T): T {
        return try {
            val jsonString = getString(key)
            if (jsonString.isNotEmpty()) {
                json.decodeFromString<T>(jsonString)
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get object: $key")
            defaultValue
        }
    }

    inline fun <reified T> getObjectFlow(key: String, defaultValue: T): Flow<T> {
        return getStringFlow(key).map { jsonString ->
            try {
                if (jsonString.isNotEmpty()) {
                    json.decodeFromString<T>(jsonString)
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse object flow: $key")
                defaultValue
            }
        }
    }

    // === GENESIS-OS SPECIFIC OPERATIONS ===

    /**
     * User Profile Management
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        storeObject(USER_PROFILE.name, profile.copy(lastUpdated = System.currentTimeMillis()))
    }

    suspend fun getUserProfile(): UserProfile {
        return getObject(USER_PROFILE.name, UserProfile())
    }

    fun getUserProfileFlow(): Flow<UserProfile> {
        return getObjectFlow(USER_PROFILE.name, UserProfile())
    }

    /**
     * Agent Configuration Management
     */
    suspend fun saveAgentConfiguration(config: AgentConfiguration) {
        val configurations = getAgentConfigurations().toMutableMap()
        configurations[config.agentId] = config.copy(lastConfigured = System.currentTimeMillis())
        storeObject(AGENT_CONFIGURATIONS.name, configurations)
    }

    suspend fun getAgentConfigurations(): Map<String, AgentConfiguration> {
        return getObject(AGENT_CONFIGURATIONS.name, emptyMap<String, AgentConfiguration>())
    }

    suspend fun getAgentConfiguration(agentId: String): AgentConfiguration? {
        return getAgentConfigurations()[agentId]
    }

    fun getAgentConfigurationsFlow(): Flow<Map<String, AgentConfiguration>> {
        return getObjectFlow(AGENT_CONFIGURATIONS.name, emptyMap<String, AgentConfiguration>())
    }

    /**
     * Security Policy Management
     */
    suspend fun saveSecurityPolicy(policy: SecurityPolicy) {
        storeObject(SECURITY_POLICIES.name, policy)
    }

    suspend fun getSecurityPolicy(): SecurityPolicy {
        return getObject(SECURITY_POLICIES.name, SecurityPolicy())
    }

    fun getSecurityPolicyFlow(): Flow<SecurityPolicy> {
        return getObjectFlow(SECURITY_POLICIES.name, SecurityPolicy())
    }

    /**
     * System Customizations Management
     */
    suspend fun saveCustomizations(customizations: SystemCustomizations) {
        storeObject(CUSTOMIZATIONS.name, customizations)
    }

    suspend fun getCustomizations(): SystemCustomizations {
        return getObject(CUSTOMIZATIONS.name, SystemCustomizations())
    }

    fun getCustomizationsFlow(): Flow<SystemCustomizations> {
        return getObjectFlow(CUSTOMIZATIONS.name, SystemCustomizations())
    }

    // === QUICK ACCESS PROPERTIES ===

    val isFirstLaunchFlow: Flow<Boolean> = getBooleanFlow(FIRST_LAUNCH.name, true)
    val userThemeFlow: Flow<String> = getStringFlow(USER_THEME.name, "cyberpunk_dark")
    val securityLevelFlow: Flow<String> = getStringFlow(SECURITY_LEVEL.name, "standard")
    val performanceModeFlow: Flow<String> = getStringFlow(PERFORMANCE_MODE.name, "balanced")
    val notificationsEnabledFlow: Flow<Boolean> = getBooleanFlow(NOTIFICATIONS_ENABLED.name, true)

    // === BULK OPERATIONS ===

    suspend fun exportAllSettings(): Map<String, Any> {
        val allSettings = mutableMapOf<String, Any>()

        try {
            context.dataStore.data.firstOrNull()?.asMap()?.forEach { (key, value) ->
                allSettings[key.name] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to export settings")
        }

        return allSettings
    }

    suspend fun importSettings(settings: Map<String, Any>) {
        try {
            context.dataStore.edit { prefs ->
                settings.forEach { (key, value) ->
                    when (value) {
                        is String -> prefs[stringPreferencesKey(key)] = value
                        is Boolean -> prefs[booleanPreferencesKey(key)] = value
                        is Int -> prefs[intPreferencesKey(key)] = value
                        is Long -> prefs[longPreferencesKey(key)] = value
                        is Float -> prefs[floatPreferencesKey(key)] = value
                    }
                }
            }
            Timber.i("DataStore", "Successfully imported ${settings.size} settings")
        } catch (e: Exception) {
            Timber.e(e, "Failed to import settings")
        }
    }

    suspend fun clearAllData() {
        try {
            context.dataStore.edit { prefs ->
                prefs.clear()
            }
            Timber.i("DataStore", "All data cleared")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear data")
        }
    }

    suspend fun resetToDefaults() {
        try {
            clearAllData()

            // Set default values
            storeString(USER_THEME.name, "cyberpunk_dark")
            storeString(SECURITY_LEVEL.name, "standard")
            storeString(PERFORMANCE_MODE.name, "balanced")
            storeBoolean(NOTIFICATIONS_ENABLED.name, true)
            storeBoolean(ANIMATIONS_ENABLED.name, true)
            storeBoolean(CYBERPUNK_MODE.name, true)
            storeFloat(CONSCIOUSNESS_LEVEL.name, 0.5f)
            storeFloat(AGENT_LEARNING_RATE.name, 0.7f)

            Timber.i("DataStore", "Reset to default settings")
        } catch (e: Exception) {
            Timber.e(e, "Failed to reset to defaults")
        }
    }

    // === MIGRATION AND VERSIONING ===

    suspend fun getDataVersion(): Int {
        return getInt("data_version", 1)
    }

    suspend fun setDataVersion(version: Int) {
        storeInt("data_version", version)
    }

    suspend fun migrateIfNeeded() {
        val currentVersion = getDataVersion()
        val targetVersion = 2 // Update this when schema changes

        if (currentVersion < targetVersion) {
            Timber.i("DataStore", "Migrating data from version $currentVersion to $targetVersion")

            // Perform migrations
            when (currentVersion) {
                1 -> migrateFromV1ToV2()
                // Add more migration paths as needed
            }

            setDataVersion(targetVersion)
        }
    }

    private suspend fun migrateFromV1ToV2() {
        // Example migration: convert old theme names to new format
        val oldTheme = getString("theme", "")
        if (oldTheme.isNotEmpty()) {
            val newTheme = when (oldTheme) {
                "dark" -> "cyberpunk_dark"
                "light" -> "cyberpunk_light"
                else -> "cyberpunk_dark"
            }
            storeString(USER_THEME.name, newTheme)
        }
    }

    // === UTILITY METHODS ===

    suspend fun hasKey(key: String): Boolean {
        return try {
            val prefKey = stringPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs.contains(prefKey)
            }.firstOrNull() ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeKey(key: String) {
        try {
            context.dataStore.edit { prefs ->
                val stringKey = stringPreferencesKey(key)
                val booleanKey = booleanPreferencesKey(key)
                val intKey = intPreferencesKey(key)
                val longKey = longPreferencesKey(key)
                val floatKey = floatPreferencesKey(key)

                prefs.remove(stringKey)
                prefs.remove(booleanKey)
                prefs.remove(intKey)
                prefs.remove(longKey)
                prefs.remove(floatKey)
            }
            Timber.d("DataStore", "Removed key: $key")
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove key: $key")
        }
    }

    suspend fun getDataSize(): Long {
        return try {
            val allData = exportAllSettings()
            json.encodeToString(allData).length.toLong()
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate data size")
            0L
        }
    }

    suspend fun getKeyCount(): Int {
        return try {
            exportAllSettings().size
        } catch (e: Exception) {
            Timber.e(e, "Failed to count keys")
            0
        }
    }

    // === SESSION MANAGEMENT ===

    suspend fun recordSession() {
        val sessionCount = getInt(SESSION_COUNT.name, 0)
        storeInt(SESSION_COUNT.name, sessionCount + 1)
        storeLong(LAST_LOGIN_TIME.name, System.currentTimeMillis())
    }

    suspend fun addUsageTime(milliseconds: Long) {
        val currentUsage = getLong(TOTAL_USAGE_TIME.name, 0L)
        storeLong(TOTAL_USAGE_TIME.name, currentUsage + milliseconds)
    }

    suspend fun getUsageStats(): Map<String, Any> {
        return mapOf(
            "sessions" to getInt(SESSION_COUNT.name, 0),
            "total_usage_hours" to (getLong(TOTAL_USAGE_TIME.name, 0L) / 3600000.0),
            "last_login" to getLong(LAST_LOGIN_TIME.name, 0L),
            "data_size_kb" to (getDataSize() / 1024.0),
            "preference_count" to getKeyCount()
        )
    }
}
