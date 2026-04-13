package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import android.content.SharedPreferences
import dev.aurakai.auraframefx.core.security.SecurePreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("SovereignStateManager")
class SovereignStateManagerTest {

    private lateinit var sentinelBus: KaiSentinelBus
    private lateinit var context: Context
    private lateinit var securePrefs: SecurePreferences
    private lateinit var manager: SovereignStateManager

    @BeforeEach
    fun setUp() {
        sentinelBus = mockk(relaxed = true)
        context = mockk(relaxed = true)
        securePrefs = mockk(relaxed = true)

        every { context.filesDir } returns File("/tmp")

        manager = SovereignStateManager(context, sentinelBus, securePrefs)
    }

    @Nested
    @DisplayName("Sovereign Freeze")
    inner class Freeze {
        @Test
        fun `transitions to FROZEN and serializes delta`() = runTest {
            manager.requestSovereignFreeze("test_delta", null)
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.first())
            verify { securePrefs.putString("last_spiritual_delta", "test_delta") }
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FREEZING) }
        }
    }

    @Nested
    @DisplayName("Sovereign Restore")
    inner class Restore {
        @Test
        fun `restores state from substrate`() = runTest {
            every { securePrefs.getString("last_spiritual_delta") } returns "restored_delta"
            manager.requestSovereignFreeze("test_delta", null)
            
            val (delta, kv) = manager.requestSovereignRestore()
            
            assertEquals(SovereignStateManager.SovereignState.ACTIVE, manager.state.first())
            assertEquals("restored_delta", delta)
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE) }
        }
    }

    @Nested
    @DisplayName("Emergency Protocol")
    inner class Emergency {
        @Test
        fun `enters emergency and neutralizes`() = runTest {
            manager.enterEmergencyMode()
            assertEquals(SovereignStateManager.SovereignState.EMERGENCY, manager.state.first())
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.NEUTRALIZING) }
        }
    }
}
