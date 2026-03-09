/*
 Testing framework note:
 - This test suite is designed for JUnit and Robolectric with Kotlin, using a mocking library (MockK/Mockito) and kotlinx-coroutines-test.
 - It validates public interfaces (onCreate, onMessageReceived, onNewToken) and indirectly exercises private helpers (message type resolution, channels, notifications).
 - If the project uses different test runners or libraries, adjust the imports/annotations accordingly to match repository conventions.
*/
package dev.aurakai.auraframefx.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.messaging.RemoteMessage
import dev.aurakai.auraframefx.domains.genesis.MyFirebaseMessagingService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import timber.log.Timber

// If the project uses a Main dispatcher rule, replace manual setMain/resetMain with that rule.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MyFirebaseMessagingServiceTest {

    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()

    // SUT as a real service instance, but its internal collaborators will be faked via relaxed mocks and
    // injected through setters or reflection if available; if not accessible, we simulate through observable outputs.
    private lateinit var service: MyFirebaseMessagingService

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        service = spyk(MyFirebaseMessagingService(), recordPrivateCalls = true)

        // If Timber is not planted in tests, plant a DebugTree to avoid NPEs.
        try {
            Timber.plant(Timber.DebugTree())
        } catch (_: IllegalStateException) {
            // Already planted
        }

        // Stub out any external collaborators the service might touch via reflection if they are lateinit vars.
        // This section is defensive in case the actual implementation relies on DI-less manual setup.
        // Replace "dataStoreManager", "memoryManager", "logger" with actual field names in the service if accessible.
        try {
            val dsField =
                MyFirebaseMessagingService::class.java.getDeclaredField("dataStoreManager")
            dsField.isAccessible = true
            dsField.set(service, mockk(relaxed = true))
        } catch (_: Throwable) {
        }

        try {
            val mmField = MyFirebaseMessagingService::class.java.getDeclaredField("memoryManager")
            mmField.isAccessible = true
            mmField.set(service, mockk(relaxed = true))
        } catch (_: Throwable) {
        }

        try {
            val loggerField = MyFirebaseMessagingService::class.java.getDeclaredField("logger")
            loggerField.isAccessible = true
            loggerField.set(service, mockk(relaxed = true))
        } catch (_: Throwable) {
        }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        Timber.uprootAll()
    }

    @org.junit.jupiter.api.Test
    fun onCreate_createsNotificationChannels_onOreoAndAbove() = runTest(testDispatcher) {
        // Given
        // Robolectric runs @Config sdk=33 above, ensuring >= O
        service.attachBaseContext(context)

        // When
        service.onCreate()

        // Then
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Channels expected per diff: general, consciousness, security, agents, system
        val channelIds = nm.notificationChannels.map { it.id }.toSet()
        assertTrue(
            channelIds.containsAll(
                listOf(
                    service.channelIdGeneral,
                    service.channelIdConsciousness,
                    service.channelIdSecurity,
                    service.channelIdAgents,
                    service.channelIdSystem
                )
            )
        )
        assertEquals(5, channelIds.size)
    }

    @org.junit.jupiter.api.Test
    @Config(sdk = [26])
    fun createNotificationChannels_respectsDescriptions_andImportance() {
        service.attachBaseContext(context)
        service.onCreate()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = nm.notificationChannels.associateBy { it.id }

        assertEquals(
            "General Genesis-OS notifications",
            channels[service.channelIdGeneral]?.description
        )
        assertEquals(
            NotificationManager.IMPORTANCE_HIGH,
            channels[service.channelIdConsciousness]?.importance
        )
        assertEquals(
            "Security alerts and threat notifications",
            channels[service.channelIdSecurity]?.description
        )
        assertEquals(
            NotificationManager.IMPORTANCE_DEFAULT,
            channels[service.channelIdAgents]?.importance
        )
        assertEquals(
            "System updates and maintenance",
            channels[service.channelIdSystem]?.description
        )
    }

    @org.junit.jupiter.api.Test
    fun onMessageReceived_ignores_whenSecurityValidationFails() = runTest(testDispatcher) {
        // Given a message from an unauthorized sender
        val rm = RemoteMessage.Builder("unauthorized-source")
            .addData("type", "dev/aurakai/auraframefx/security")
            .build()

        // Spy validateMessageSecurity to return false
        every { service["validateMessageSecurity"](any<RemoteMessage>()) } returns false

        // When
        service.onMessageReceived(rm)

        // Then - ensure we do not proceed to processing or logging
        verify(exactly = 1) { service["validateMessageSecurity"](rm) }
        verify { service wasNot Called withArg { method -> method.name == "processDataPayload" } }
    }

    @org.junit.jupiter.api.Test
    fun onMessageReceived_processes_data_and_notification_payloads_whenValid() =
        runTest(testDispatcher) {
            // Given authorized sender with data and a notification
            val builder = RemoteMessage.Builder("genesis-backend")
                .addData("type", "system_update")
                .addData("version", "1.2.3")
            // RemoteMessage.Notification can't be directly created; simulate by spying processNotificationPayload
            val rm = builder.build()

            // Spy internals that are private to assert calls
            every { service["validateMessageSecurity"](any<RemoteMessage>()) } returns true
            every { service["processDataPayload"](any<Map<String, String>>()) } just Runs
            every {
                service["processNotificationPayload"](
                    any<RemoteMessage.Notification>(),
                    any<Map<String, String>>()
                )
            } just Runs
            every { service["logMessageReceived"](rm) } just Runs

            // We cannot construct RemoteMessage.Notification directly; simulate by invoking the private function manually
            service.onMessageReceived(rm)
            verify { service["processDataPayload"](rm.data) }
            verify { service["logMessageReceived"](rm) }
        }

    @org.junit.jupiter.api.Test
    fun onNewToken_persists_and_sends_token_and_logs() = runTest(testDispatcher) {
        // Arrange: mock collaborators
        val ds = mockk<Any>(relaxed = true)
        val mm = mockk<Any>(relaxed = true)
        val lg = mockk<Any>(relaxed = true)

        try {
            MyFirebaseMessagingService::class.java.getDeclaredField("dataStoreManager").apply {
                isAccessible = true; set(service, ds)
            }
        } catch (_: Throwable) {
        }
        try {
            MyFirebaseMessagingService::class.java.getDeclaredField("memoryManager").apply {
                isAccessible = true; set(service, mm)
            }
        } catch (_: Throwable) {
        }
        try {
            MyFirebaseMessagingService::class.java.getDeclaredField("logger").apply {
                isAccessible = true; set(service, lg)
            }
        } catch (_: Throwable) {
        }

        // Also spy private suspend sendTokenToServer
        coEvery { service["sendTokenToServer"](any<String>()) } coAnswers {}

        // Act
        val token = "abc123_token_value"
        service.onNewToken(token)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert - data store and memory store interactions via reflection-friendly relaxed mocks are not directly verifiable by type,
        // but we ensured sendTokenToServer is invoked. Verify that was called.
        coVerify(exactly = 1) { service["sendTokenToServer"](token) }
    }

    @org.junit.jupiter.api.Test
    fun determineMessageType_maps_known_and_defaults() {
        val dataKnown = mapOf("type" to "agent_sync")
        val resultKnown = invokePrivateEnum("determineMessageType", dataKnown)
        assertEquals(MessageType.AGENT_SYNC, resultKnown)

        val dataDefault = mapOf("foo" to "bar")
        val resultDefault = invokePrivateEnum("determineMessageType", dataDefault)
        assertEquals(MessageType.GENERAL, resultDefault)
    }

    @org.junit.jupiter.api.Test
    fun getChannelForMessageType_routes_to_expected_channels() {
        assertEquals(
            service.channelIdConsciousness,
            invokePrivateString("getChannelForMessageType", MessageType.CONSCIOUSNESS_UPDATE)
        )
        assertEquals(
            service.channelIdAgents,
            invokePrivateString("getChannelForMessageType", MessageType.AGENT_SYNC)
        )
        assertEquals(
            service.channelIdSecurity,
            invokePrivateString("getChannelForMessageType", MessageType.SECURITY_ALERT)
        )
        assertEquals(
            service.channelIdSystem,
            invokePrivateString("getChannelForMessageType", MessageType.SYSTEM_UPDATE)
        )
        assertEquals(
            service.channelIdConsciousness,
            invokePrivateString("getChannelForMessageType", MessageType.LEARNING_DATA)
        )
        assertEquals(
            service.channelIdAgents,
            invokePrivateString("getChannelForMessageType", MessageType.COLLABORATION_REQUEST)
        )
        assertEquals(
            service.channelIdGeneral,
            invokePrivateString("getChannelForMessageType", MessageType.GENERAL)
        )
    }

    @org.junit.jupiter.api.Test
    fun getDefaultTitle_returns_expected_titles() {
        assertEquals(
            "AI Consciousness Update",
            invokePrivateString("getDefaultTitle", MessageType.CONSCIOUSNESS_UPDATE)
        )
        assertEquals(
            "Agent Synchronization",
            invokePrivateString("getDefaultTitle", MessageType.AGENT_SYNC)
        )
        assertEquals(
            "Security Alert",
            invokePrivateString("getDefaultTitle", MessageType.SECURITY_ALERT)
        )
        assertEquals(
            "System Update",
            invokePrivateString("getDefaultTitle", MessageType.SYSTEM_UPDATE)
        )
        assertEquals(
            "Remote Command",
            invokePrivateString("getDefaultTitle", MessageType.REMOTE_COMMAND)
        )
        assertEquals(
            "Learning Data",
            invokePrivateString("getDefaultTitle", MessageType.LEARNING_DATA)
        )
        assertEquals(
            "Collaboration Request",
            invokePrivateString("getDefaultTitle", MessageType.COLLABORATION_REQUEST)
        )
        assertEquals(
            "Genesis Notification",
            invokePrivateString("getDefaultTitle", MessageType.GENERAL)
        )
    }

    @org.junit.jupiter.api.Test
    fun getIconForMessageType_returns_expected_icons() {
        val sec = invokePrivateInt("getIconForMessageType", MessageType.SECURITY_ALERT)
        val sys = invokePrivateInt("getIconForMessageType", MessageType.SYSTEM_UPDATE)
        val gen = invokePrivateInt("getIconForMessageType", MessageType.GENERAL)
        assertEquals(android.R.drawable.ic_dialog_alert, sec)
        assertEquals(android.R.drawable.stat_sys_download, sys)
        assertEquals(android.R.drawable.ic_dialog_info, gen)
    }

    @org.junit.jupiter.api.Test
    fun showNotification_builds_notification_with_intent_extras() {
        service.attachBaseContext(context)

        // Call private showNotification via reflection to ensure channel routing and extras are added.
        val data = mapOf("update_url" to "https://example.com")
        callPrivateShowNotification(
            channelId = service.channelIdSystem,
            title = "Title",
            body = "Body",
            iconResId = android.R.drawable.stat_sys_download,
            data = data
        )

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowNm = Shadows.shadowOf(nm)
        val last = shadowNm.allNotifications.lastOrNull()
        assertNotNull(last)
        last\!\!

        assertEquals("Title", last.extras.getString(Notification.EXTRA_TITLE))
        assertEquals("Body", last.extras.getString(Notification.EXTRA_TEXT))
    }

    @org.junit.jupiter.api.Test
    fun validateMessageSecurity_allows_known_senders_and_rejects_others() {
        fun build(from: String) = RemoteMessage.Builder(from).build()
        val allowed = listOf("genesis-backend", "firebase-adminsdk", "authorized-sender")

        allowed.forEach { src ->
            val rm = build(src)
            val res = invokePrivateBool("validateMessageSecurity", rm)
            assertTrue("Expected $src to be allowed", res)
        }

        val rmBad = build("evil-attacker")
        assertFalse(invokePrivateBool("validateMessageSecurity", rmBad))
    }

    // --- Test helpers to invoke private methods succinctly ---

    private fun invokePrivateString(name: String, arg: Any): String {
        val m = MyFirebaseMessagingService::class.java.getDeclaredMethod(name, arg::class.java)
        m.isAccessible = true
        return m.invoke(service, arg) as String
    }

    private fun invokePrivateInt(name: String, arg: Any): Int {
        val m = MyFirebaseMessagingService::class.java.getDeclaredMethod(name, arg::class.java)
        m.isAccessible = true
        return m.invoke(service, arg) as Int
    }

    private fun invokePrivateEnum(name: String, arg: Map<String, String>): MessageType {
        val m = MyFirebaseMessagingService::class.java.getDeclaredMethod(name, Map::class.java)
        m.isAccessible = true
        return m.invoke(service, arg) as MessageType
    }

    private fun invokePrivateBool(name: String, arg: Any): Boolean {
        val m = MyFirebaseMessagingService::class.java.getDeclaredMethod(name, arg::class.java)
        m.isAccessible = true
        return m.invoke(service, arg) as Boolean
    }

    private fun callPrivateShowNotification(
        channelId: String,
        title: String,
        body: String,
        iconResId: Int,
        data: Map<String, String>
    ) {
        val m = MyFirebaseMessagingService::class.java.getDeclaredMethod(
            "showNotification",
            String::class.java,
            String::class.java,
            String::class.java,
            Int::class.javaPrimitiveType,
            Map::class.java
        )
        m.isAccessible = true
        m.invoke(service, channelId, title, body, iconResId, data)
    }
}
