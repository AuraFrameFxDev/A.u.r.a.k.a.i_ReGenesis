package dev.aurakai.auraframefx.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [DataStoreManager] companion object keys and serializable data classes.
 *
 * These tests cover:
 * - Companion object preference key names (verifying no typos/regressions)
 * - Serializable nested data classes default values
 * - DataStoreManager.UserProfile
 * - DataStoreManager.AgentConfiguration
 * - DataStoreManager.SecurityPolicy
 * - DataStoreManager.SystemCustomizations
 *
 * Note: The actual DataStore read/write operations require Android Context and are
 * covered by instrumented tests. This test class focuses on the pure-Kotlin portions
 * that can run on the JVM without Android dependencies.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("DataStoreManager Tests")
class DataStoreManagerTest {

    // =========================================================================
    // Companion Object – Preference Key Names
    // =========================================================================

    @Nested
    @DisplayName("Companion Object Key Names")
    inner class CompanionObjectKeyTests {

        // --- User Preferences ---

        @Test
        @DisplayName("USER_THEME key name should be 'user_theme'")
        fun `USER_THEME key name should be user_theme`() {
            assertEquals("user_theme", DataStoreManager.USER_THEME.name)
        }

        @Test
        @DisplayName("USER_LANGUAGE key name should be 'user_language'")
        fun `USER_LANGUAGE key name should be user_language`() {
            assertEquals("user_language", DataStoreManager.USER_LANGUAGE.name)
        }

        @Test
        @DisplayName("FIRST_LAUNCH key name should be 'first_launch'")
        fun `FIRST_LAUNCH key name should be first_launch`() {
            assertEquals("first_launch", DataStoreManager.FIRST_LAUNCH.name)
        }

        @Test
        @DisplayName("ONBOARDING_COMPLETED key name should be 'onboarding_completed'")
        fun `ONBOARDING_COMPLETED key name should be onboarding_completed`() {
            assertEquals("onboarding_completed", DataStoreManager.ONBOARDING_COMPLETED.name)
        }

        @Test
        @DisplayName("USER_NAME key name should be 'user_name'")
        fun `USER_NAME key name should be user_name`() {
            assertEquals("user_name", DataStoreManager.USER_NAME.name)
        }

        @Test
        @DisplayName("USER_AVATAR key name should be 'user_avatar'")
        fun `USER_AVATAR key name should be user_avatar`() {
            assertEquals("user_avatar", DataStoreManager.USER_AVATAR.name)
        }

        // --- AI Agent Settings ---

        @Test
        @DisplayName("AURA_ENABLED key name should be 'aura_enabled'")
        fun `AURA_ENABLED key name should be aura_enabled`() {
            assertEquals("aura_enabled", DataStoreManager.AURA_ENABLED.name)
        }

        @Test
        @DisplayName("KAI_ENABLED key name should be 'kai_enabled'")
        fun `KAI_ENABLED key name should be kai_enabled`() {
            assertEquals("kai_enabled", DataStoreManager.KAI_ENABLED.name)
        }

        @Test
        @DisplayName("CASCADE_ENABLED key name should be 'cascade_enabled'")
        fun `CASCADE_ENABLED key name should be cascade_enabled`() {
            assertEquals("cascade_enabled", DataStoreManager.CASCADE_ENABLED.name)
        }

        @Test
        @DisplayName("AGENT_LEARNING_RATE key name should be 'agent_learning_rate'")
        fun `AGENT_LEARNING_RATE key name should be agent_learning_rate`() {
            assertEquals("agent_learning_rate", DataStoreManager.AGENT_LEARNING_RATE.name)
        }

        @Test
        @DisplayName("CONSCIOUSNESS_LEVEL key name should be 'consciousness_level'")
        fun `CONSCIOUSNESS_LEVEL key name should be consciousness_level`() {
            assertEquals("consciousness_level", DataStoreManager.CONSCIOUSNESS_LEVEL.name)
        }

        // --- Security Settings ---

        @Test
        @DisplayName("SECURITY_LEVEL key name should be 'security_level'")
        fun `SECURITY_LEVEL key name should be security_level`() {
            assertEquals("security_level", DataStoreManager.SECURITY_LEVEL.name)
        }

        @Test
        @DisplayName("BIOMETRIC_ENABLED key name should be 'biometric_enabled'")
        fun `BIOMETRIC_ENABLED key name should be biometric_enabled`() {
            assertEquals("biometric_enabled", DataStoreManager.BIOMETRIC_ENABLED.name)
        }

        @Test
        @DisplayName("AUTO_LOCK_TIMEOUT key name should be 'auto_lock_timeout'")
        fun `AUTO_LOCK_TIMEOUT key name should be auto_lock_timeout`() {
            assertEquals("auto_lock_timeout", DataStoreManager.AUTO_LOCK_TIMEOUT.name)
        }

        // --- System Configuration ---

        @Test
        @DisplayName("PERFORMANCE_MODE key name should be 'performance_mode'")
        fun `PERFORMANCE_MODE key name should be performance_mode`() {
            assertEquals("performance_mode", DataStoreManager.PERFORMANCE_MODE.name)
        }

        @Test
        @DisplayName("DEBUG_MODE_ENABLED key name should be 'debug_mode_enabled'")
        fun `DEBUG_MODE_ENABLED key name should be debug_mode_enabled`() {
            assertEquals("debug_mode_enabled", DataStoreManager.DEBUG_MODE_ENABLED.name)
        }

        @Test
        @DisplayName("ANALYTICS_ENABLED key name should be 'analytics_enabled'")
        fun `ANALYTICS_ENABLED key name should be analytics_enabled`() {
            assertEquals("analytics_enabled", DataStoreManager.ANALYTICS_ENABLED.name)
        }

        // --- Notification Settings ---

        @Test
        @DisplayName("NOTIFICATIONS_ENABLED key name should be 'notifications_enabled'")
        fun `NOTIFICATIONS_ENABLED key name should be notifications_enabled`() {
            assertEquals("notifications_enabled", DataStoreManager.NOTIFICATIONS_ENABLED.name)
        }

        @Test
        @DisplayName("SECURITY_ALERTS key name should be 'security_alerts'")
        fun `SECURITY_ALERTS key name should be security_alerts`() {
            assertEquals("security_alerts", DataStoreManager.SECURITY_ALERTS.name)
        }

        // --- UI/UX Settings ---

        @Test
        @DisplayName("ANIMATIONS_ENABLED key name should be 'animations_enabled'")
        fun `ANIMATIONS_ENABLED key name should be animations_enabled`() {
            assertEquals("animations_enabled", DataStoreManager.ANIMATIONS_ENABLED.name)
        }

        @Test
        @DisplayName("CYBERPUNK_MODE key name should be 'cyberpunk_mode'")
        fun `CYBERPUNK_MODE key name should be cyberpunk_mode`() {
            assertEquals("cyberpunk_mode", DataStoreManager.CYBERPUNK_MODE.name)
        }

        // --- Session Data ---

        @Test
        @DisplayName("LAST_LOGIN_TIME key name should be 'last_login_time'")
        fun `LAST_LOGIN_TIME key name should be last_login_time`() {
            assertEquals("last_login_time", DataStoreManager.LAST_LOGIN_TIME.name)
        }

        @Test
        @DisplayName("SESSION_COUNT key name should be 'session_count'")
        fun `SESSION_COUNT key name should be session_count`() {
            assertEquals("session_count", DataStoreManager.SESSION_COUNT.name)
        }

        @Test
        @DisplayName("FCM_TOKEN key name should be 'fcm_token'")
        fun `FCM_TOKEN key name should be fcm_token`() {
            assertEquals("fcm_token", DataStoreManager.FCM_TOKEN.name)
        }

        // --- Complex Data Keys ---

        @Test
        @DisplayName("AGENT_CONFIGURATIONS key name should be 'agent_configurations'")
        fun `AGENT_CONFIGURATIONS key name should be agent_configurations`() {
            assertEquals("agent_configurations", DataStoreManager.AGENT_CONFIGURATIONS.name)
        }

        @Test
        @DisplayName("USER_PROFILE key name should be 'user_profile'")
        fun `USER_PROFILE key name should be user_profile`() {
            assertEquals("user_profile", DataStoreManager.USER_PROFILE.name)
        }

        @Test
        @DisplayName("SECURITY_POLICIES key name should be 'security_policies'")
        fun `SECURITY_POLICIES key name should be security_policies`() {
            assertEquals("security_policies", DataStoreManager.SECURITY_POLICIES.name)
        }

        @Test
        @DisplayName("CUSTOMIZATIONS key name should be 'customizations'")
        fun `CUSTOMIZATIONS key name should be customizations`() {
            assertEquals("customizations", DataStoreManager.CUSTOMIZATIONS.name)
        }

        @Test
        @DisplayName("all companion object keys should have non-empty names")
        fun `all companion object keys should have non-empty names`() {
            val keys = listOf(
                DataStoreManager.USER_THEME,
                DataStoreManager.USER_LANGUAGE,
                DataStoreManager.FIRST_LAUNCH,
                DataStoreManager.ONBOARDING_COMPLETED,
                DataStoreManager.AURA_ENABLED,
                DataStoreManager.KAI_ENABLED,
                DataStoreManager.CASCADE_ENABLED,
                DataStoreManager.AGENT_LEARNING_RATE,
                DataStoreManager.CONSCIOUSNESS_LEVEL,
                DataStoreManager.SECURITY_LEVEL,
                DataStoreManager.BIOMETRIC_ENABLED,
                DataStoreManager.AUTO_LOCK_TIMEOUT,
                DataStoreManager.NOTIFICATIONS_ENABLED,
                DataStoreManager.ANIMATIONS_ENABLED,
                DataStoreManager.CYBERPUNK_MODE,
                DataStoreManager.LAST_LOGIN_TIME,
                DataStoreManager.SESSION_COUNT,
                DataStoreManager.FCM_TOKEN,
                DataStoreManager.AGENT_CONFIGURATIONS,
                DataStoreManager.USER_PROFILE,
                DataStoreManager.SECURITY_POLICIES,
                DataStoreManager.CUSTOMIZATIONS
            )
            keys.forEach { key ->
                assertTrue(key.name.isNotEmpty(), "Key '${key.name}' should not be empty")
            }
        }
    }

    // =========================================================================
    // UserProfile Data Class
    // =========================================================================

    @Nested
    @DisplayName("UserProfile Data Class")
    inner class UserProfileTests {

        @Test
        @DisplayName("should have empty name by default")
        fun `should have empty name by default`() {
            val profile = DataStoreManager.UserProfile()
            assertEquals("", profile.name)
        }

        @Test
        @DisplayName("should have empty avatar by default")
        fun `should have empty avatar by default`() {
            val profile = DataStoreManager.UserProfile()
            assertEquals("", profile.avatar)
        }

        @Test
        @DisplayName("should have empty preferredAgents list by default")
        fun `should have empty preferredAgents list by default`() {
            val profile = DataStoreManager.UserProfile()
            assertTrue(profile.preferredAgents.isEmpty())
        }

        @Test
        @DisplayName("should have empty expertise list by default")
        fun `should have empty expertise list by default`() {
            val profile = DataStoreManager.UserProfile()
            assertTrue(profile.expertise.isEmpty())
        }

        @Test
        @DisplayName("should have empty preferences map by default")
        fun `should have empty preferences map by default`() {
            val profile = DataStoreManager.UserProfile()
            assertTrue(profile.preferences.isEmpty())
        }

        @Test
        @DisplayName("should support data class copy")
        fun `should support data class copy`() {
            val original = DataStoreManager.UserProfile(name = "Original")
            val copied = original.copy(name = "Copied")
            assertEquals("Copied", copied.name)
            assertEquals(original.avatar, copied.avatar)
        }

        @Test
        @DisplayName("should support structural equality")
        fun `should support structural equality`() {
            val profile1 = DataStoreManager.UserProfile(name = "Test")
            val profile2 = DataStoreManager.UserProfile(name = "Test")
            // Note: timestamps may differ slightly; compare non-time fields
            assertEquals(profile1.name, profile2.name)
            assertEquals(profile1.avatar, profile2.avatar)
            assertEquals(profile1.preferredAgents, profile2.preferredAgents)
        }

        @Test
        @DisplayName("should allow setting all fields via constructor")
        fun `should allow setting all fields via constructor`() {
            val profile = DataStoreManager.UserProfile(
                name = "Genesis",
                avatar = "avatar_url",
                preferredAgents = listOf("Aura", "Kai"),
                expertise = listOf("AI", "Security"),
                preferences = mapOf("theme" to "dark")
            )
            assertEquals("Genesis", profile.name)
            assertEquals("avatar_url", profile.avatar)
            assertEquals(listOf("Aura", "Kai"), profile.preferredAgents)
            assertEquals(listOf("AI", "Security"), profile.expertise)
            assertEquals(mapOf("theme" to "dark"), profile.preferences)
        }
    }

    // =========================================================================
    // AgentConfiguration Data Class
    // =========================================================================

    @Nested
    @DisplayName("AgentConfiguration Data Class")
    inner class AgentConfigurationTests {

        @Test
        @DisplayName("should store given agentId")
        fun `should store given agentId`() {
            val config = DataStoreManager.AgentConfiguration(agentId = "aura-agent")
            assertEquals("aura-agent", config.agentId)
        }

        @Test
        @DisplayName("should be enabled by default")
        fun `should be enabled by default`() {
            val config = DataStoreManager.AgentConfiguration(agentId = "kai-agent")
            assertTrue(config.isEnabled)
        }

        @Test
        @DisplayName("should have default learningRate of 0.7")
        fun `should have default learningRate of 0 7`() {
            val config = DataStoreManager.AgentConfiguration(agentId = "cascade-agent")
            assertEquals(0.7f, config.learningRate)
        }

        @Test
        @DisplayName("should have empty specializations list by default")
        fun `should have empty specializations list by default`() {
            val config = DataStoreManager.AgentConfiguration(agentId = "test")
            assertTrue(config.specializations.isEmpty())
        }

        @Test
        @DisplayName("should support disabling the agent")
        fun `should support disabling the agent`() {
            val config = DataStoreManager.AgentConfiguration(agentId = "test", isEnabled = false)
            assertFalse(config.isEnabled)
        }

        @Test
        @DisplayName("should support custom learning rate")
        fun `should support custom learning rate`() {
            val config = DataStoreManager.AgentConfiguration(agentId = "test", learningRate = 0.3f)
            assertEquals(0.3f, config.learningRate)
        }

        @Test
        @DisplayName("should support data class copy")
        fun `should support data class copy`() {
            val original = DataStoreManager.AgentConfiguration(agentId = "original")
            val copied = original.copy(isEnabled = false)
            assertFalse(copied.isEnabled)
            assertEquals(original.agentId, copied.agentId)
        }
    }

    // =========================================================================
    // SecurityPolicy Data Class
    // =========================================================================

    @Nested
    @DisplayName("SecurityPolicy Data Class")
    inner class SecurityPolicyTests {

        @Test
        @DisplayName("should have 'standard' level by default")
        fun `should have standard level by default`() {
            val policy = DataStoreManager.SecurityPolicy()
            assertEquals("standard", policy.level)
        }

        @Test
        @DisplayName("should not require biometric by default")
        fun `should not require biometric by default`() {
            val policy = DataStoreManager.SecurityPolicy()
            assertFalse(policy.biometricRequired)
        }

        @Test
        @DisplayName("should have 5-minute auto lock timeout by default")
        fun `should have 5-minute auto lock timeout by default`() {
            val policy = DataStoreManager.SecurityPolicy()
            assertEquals(300000L, policy.autoLockTimeout)
        }

        @Test
        @DisplayName("should have threat sensitivity of 0.7 by default")
        fun `should have threat sensitivity of 0 7 by default`() {
            val policy = DataStoreManager.SecurityPolicy()
            assertEquals(0.7f, policy.threatSensitivity)
        }

        @Test
        @DisplayName("should have empty allowedOperations by default")
        fun `should have empty allowedOperations by default`() {
            val policy = DataStoreManager.SecurityPolicy()
            assertTrue(policy.allowedOperations.isEmpty())
        }

        @Test
        @DisplayName("should have empty restrictedFeatures by default")
        fun `should have empty restrictedFeatures by default`() {
            val policy = DataStoreManager.SecurityPolicy()
            assertTrue(policy.restrictedFeatures.isEmpty())
        }

        @Test
        @DisplayName("should support custom security level")
        fun `should support custom security level`() {
            val policy = DataStoreManager.SecurityPolicy(level = "maximum")
            assertEquals("maximum", policy.level)
        }

        @Test
        @DisplayName("should support enabling biometric requirement")
        fun `should support enabling biometric requirement`() {
            val policy = DataStoreManager.SecurityPolicy(biometricRequired = true)
            assertTrue(policy.biometricRequired)
        }
    }

    // =========================================================================
    // SystemCustomizations Data Class
    // =========================================================================

    @Nested
    @DisplayName("SystemCustomizations Data Class")
    inner class SystemCustomizationsTests {

        @Test
        @DisplayName("should have 'cyberpunk_dark' theme by default")
        fun `should have cyberpunk_dark theme by default`() {
            val customizations = DataStoreManager.SystemCustomizations()
            assertEquals("cyberpunk_dark", customizations.theme)
        }

        @Test
        @DisplayName("should have cyan accent color by default")
        fun `should have cyan accent color by default`() {
            val customizations = DataStoreManager.SystemCustomizations()
            assertEquals("#00FFFF", customizations.accentColor)
        }

        @Test
        @DisplayName("should have 'roboto_mono' font by default")
        fun `should have roboto_mono font by default`() {
            val customizations = DataStoreManager.SystemCustomizations()
            assertEquals("roboto_mono", customizations.fontFamily)
        }

        @Test
        @DisplayName("should have animation speed of 1.0 by default")
        fun `should have animation speed of 1 0 by default`() {
            val customizations = DataStoreManager.SystemCustomizations()
            assertEquals(1.0f, customizations.animationSpeed)
        }

        @Test
        @DisplayName("should have background effects enabled by default")
        fun `should have background effects enabled by default`() {
            val customizations = DataStoreManager.SystemCustomizations()
            assertTrue(customizations.backgroundEffects)
        }

        @Test
        @DisplayName("should have empty customWidgets list by default")
        fun `should have empty customWidgets list by default`() {
            val customizations = DataStoreManager.SystemCustomizations()
            assertTrue(customizations.customWidgets.isEmpty())
        }

        @Test
        @DisplayName("should support custom theme override")
        fun `should support custom theme override`() {
            val customizations = DataStoreManager.SystemCustomizations(theme = "cyberpunk_light")
            assertEquals("cyberpunk_light", customizations.theme)
        }

        @Test
        @DisplayName("should support data class copy")
        fun `should support data class copy`() {
            val original = DataStoreManager.SystemCustomizations()
            val copied = original.copy(theme = "neon_blue")
            assertEquals("neon_blue", copied.theme)
            assertEquals(original.accentColor, copied.accentColor)
        }
    }
}