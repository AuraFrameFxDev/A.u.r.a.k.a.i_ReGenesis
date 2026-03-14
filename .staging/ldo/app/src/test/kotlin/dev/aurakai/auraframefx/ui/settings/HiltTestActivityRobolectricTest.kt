package dev.aurakai.auraframefx.ui.settings

/*
Test stack:
- Framework: JUnit 4
- Runner: RobolectricTestRunner
- DI testing: Hilt Android Testing (@HiltAndroidTest, HiltAndroidRule, @BindValue)
- Utilities: Robolectric ShadowLog to capture android.util.Log

These tests validate that:
- onCreate logs the greeting from the injected GreetingProvider (happy path)
- An empty greeting is still logged (edge case)
- Exceptions thrown by GreetingProvider.getGreeting() propagate during onCreate (failure path)
- Recreating the activity logs again (lifecycle edge case)
*/

import android.util.Log
import androidx.test.core.app.ActivityScenario
import com.google.dagger.hilt.android.testing.BindValue
import com.google.dagger.hilt.android.testing.HiltAndroidRule
import com.google.dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [34])
class HiltTestActivityRobolectricTest {

    // Provide/override GreetingProvider for tests
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @JvmField
    @BindValue
    var greetingProvider: GreetingProvider = FakeGreetingProvider("init-not-used")

    @BeforeEach
    fun setUp() {
        // Clear any stale logs and ensure DI is ready before launching Activities
        ShadowLog.clear()
        hiltRule.inject()
    }

    @Test
    fun logsGreetingOnCreate_happyPath() {
        // Arrange
        greetingProvider = FakeGreetingProvider("Hello from Test")
        hiltRule.inject()
        ShadowLog.clear()

        // Act
        ActivityScenario.launch(HiltTestActivity::class.java).use {
            // Assert
            val logItems = ShadowLog.getLogsForTag("HiltTestActivity")
            assertTrue(
                "Expected a DEBUG log with the greeting message",
                logItems.any { it.type == Log.DEBUG && it.msg == "Hello from Test" }
            )
            // Also ensure only one greeting log per create
            val count = logItems.count { it.type == Log.DEBUG && it.msg == "Hello from Test" }
            assertEquals("Expected exactly one greeting log on initial create", 1, count)
        }
    }

    @Test
    fun logsEmptyGreeting_whenProviderReturnsEmpty_edgeCase() {
        // Arrange
        greetingProvider = FakeGreetingProvider("")
        hiltRule.inject()
        ShadowLog.clear()

        // Act
        ActivityScenario.launch(HiltTestActivity::class.java).use {
            // Assert
            val logItems = ShadowLog.getLogsForTag("HiltTestActivity")
            assertTrue(
                "Expected a DEBUG log even for an empty greeting",
                logItems.any { it.type == Log.DEBUG && it.msg == "" }
            )
        }
    }

    @Test
    fun onCreate_propagatesException_whenProviderThrows_failurePath() {
        // Arrange
        greetingProvider = object : GreetingProvider {
            override fun getGreeting(): String = throw IllegalStateException("boom")
        }
        hiltRule.inject()
        ShadowLog.clear()

        // Act + Assert
        // Use ActivityController to surface the exception thrown during onCreate.
        val controller = Robolectric.buildActivity(HiltTestActivity::class.java)
        assertThrows(
            "Expected an exception from GreetingProvider to propagate during onCreate",
            IllegalStateException::class.java
        ) {
            controller.setup() // Triggers onCreate
        }
    }

    @Test
    fun logsGreetingAgain_afterRecreation_lifecycleEdgeCase() {
        // Arrange
        greetingProvider = FakeGreetingProvider("Recreated Hello")
        hiltRule.inject()
        ShadowLog.clear()

        // Act
        ActivityScenario.launch(HiltTestActivity::class.java).use { scenario ->
            // First create happened; now recreate (simulates configuration change)
            scenario.recreate()

            // Assert
            val logItems = ShadowLog.getLogsForTag("HiltTestActivity")
            val count = logItems.count { it.type == Log.DEBUG && it.msg == "Recreated Hello" }
            assertEquals(
                "Expected greeting to be logged on each create (initial + recreation)",
                2,
                count
            )
        }
    }

    // Simple fake for deterministic behavior across tests
    private class FakeGreetingProvider(
        private val message: String
    ) : GreetingProvider {
        override fun getGreeting(): String = message
    }
}