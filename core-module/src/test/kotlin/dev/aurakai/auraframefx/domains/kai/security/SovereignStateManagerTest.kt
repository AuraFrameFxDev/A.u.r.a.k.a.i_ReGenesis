package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("SovereignStateManager")
class SovereignStateManagerTest {

    private lateinit var sentinelBus: KaiSentinelBus
    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var manager: SovereignStateManager

    @BeforeEach
    fun setUp() {
        sentinelBus = mockk(relaxed = true)
        context = mockk(relaxed = true)
        sharedPrefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { context.filesDir } returns File("/tmp")

        mockkStatic(MasterKeys::class)
        every { MasterKeys.getOrCreate(any()) } returns "master_key"

        mockkStatic(EncryptedSharedPreferences::class)
        every {
            EncryptedSharedPreferences.create(
                any(), any(), any(), any(), any()
            )
        } returns sharedPrefs

        manager = SovereignStateManager(context, sentinelBus)
    }

    @Nested
    @DisplayName("Sovereign Freeze")
    inner class Freeze {
        @Test
        fun `transitions to FROZEN and serializes delta`() = runTest {
            manager.requestSovereignFreeze("test_delta", null)
            assertEquals(SovereignStateManager.SovereignState.FROZEN, manager.state.first())
            verify { editor.putString("last_spiritual_delta", "test_delta") }
            verify { sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FREEZING) }
        }
    }

    @Nested
    @DisplayName("Sovereign Restore")
    inner class Restore {
        @Test
        fun `restores state from substrate`() = runTest {
            every { sharedPrefs.getString("last_spiritual_delta", null) } returns "restored_delta"
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
