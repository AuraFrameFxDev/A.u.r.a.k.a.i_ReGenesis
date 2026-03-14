/*
  Test suite for TokenManager.

  Testing library/framework:
  - JUnit4 (org.junit.Test)
  - Mockito Core + Mockito Inline for static mocking (org.mockito.*)
  - Mockito-Kotlin (if available) is not required; we use core Mockito to avoid adding dependencies.

  Strategy:
  - TokenManager internally calls static methods MasterKeys.getOrCreate(...) and EncryptedSharedPreferences.create(...).
  - We mock those static calls using Mockito's static mocking (requires mockito-inline) and return a fake SharedPreferences.
  - We then exercise public properties and methods: accessToken, refreshToken, isTokenExpired, updateTokens(), clearTokens(), isAuthenticated.
  - We validate edge cases (null/blank tokens, immediate expiry, long TTL, negative TTL), and failure conditions (SharedPreferences editor apply behavior).

  Notes:
  - These are JVM unit tests; no Android instrumentation required.
  - We provide a lightweight FakeSharedPreferences for value storage used by the static mock to avoid Android Keystore usage.
 */

package dev.aurakai.auraframefx.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class TokenManagerTest {

    // Context is only passed through; no behavior needed.
    private lateinit var context: Context

    // We'll back TokenManager with this fake SharedPreferences.
    private lateinit var fakePrefs: FakeSharedPreferences

    // Static mocks for MasterKeys and EncryptedSharedPreferences
    private lateinit var masterKeysMock: MockedStatic<MasterKeys>
    private lateinit var espMock: MockedStatic<EncryptedSharedPreferences>

    @BeforeEach
    fun setUp() {
        context = Mockito.mock(Context::class.java)
        fakePrefs = FakeSharedPreferences()

        masterKeysMock = Mockito.mockStatic(MasterKeys::class.java)
        // Return a deterministic alias so code under test proceeds
        masterKeysMock.`when`<String> { MasterKeys.getOrCreate(Mockito.any()) }.thenReturn("alias")

        espMock = Mockito.mockStatic(EncryptedSharedPreferences::class.java)
        // Whenever EncryptedSharedPreferences.create(...) is called, return our fake prefs
        espMock.`when`<SharedPreferences> {
            EncryptedSharedPreferences.create(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            )
        }.thenReturn(fakePrefs)
    }

    @AfterEach
    fun tearDown() {
        espMock.close()
        masterKeysMock.close()
    }

    @Test
    fun accessToken_isNullInitially() {
        val tm = TokenManager(context)
        assertNull(tm.accessToken, "Access token should be null initially")
        assertNull(tm.refreshToken, "Refresh token should be null initially")
        assertTrue(tm.isTokenExpired, "Without expiry set, token should be considered expired")
        assertFalse(tm.isAuthenticated, "Unauthenticated when no tokens stored")
    }

    @Test
    fun updateTokens_persistsValues_andComputesExpiryInMillis() {
        val tm = TokenManager(context)

        val access = "acc_123"
        val refresh = "ref_456"
        val ttlSeconds = 60L

        val before = System.currentTimeMillis()
        tm.updateTokens(access, refresh, ttlSeconds)
        val after = System.currentTimeMillis()

        assertEquals(access, tm.accessToken)
        assertEquals(refresh, tm.refreshToken)
        // Expiry should be between before+ttl and after+ttl (account for computation window)
        val expiry = fakePrefs.getLong("token_expiry", 0L)
        assertTrue(
            expiry in (before + ttlSeconds * 1000)..(after + ttlSeconds * 1000),
            "Expiry must be now + ttlSeconds"
        )
        // Should not be expired yet
        assertFalse(tm.isTokenExpired)
        assertTrue(tm.isAuthenticated)
    }

    @Test
    fun isTokenExpired_trueWhenExpiryInPast_orNow() {
        val tm = TokenManager(context)

        // Set tokens but expiry at "now"
        val access = "a"
        val refresh = "r"
        fakePrefs.edit()
            .putString("access_token", access)
            .putString("refresh_token", refresh)
            .putLong("token_expiry", System.currentTimeMillis())
            .apply()

        assertEquals(access, tm.accessToken)
        assertTrue(tm.isTokenExpired, "Expiry at current time counts as expired")
        assertFalse(tm.isAuthenticated, "Expired token means unauthenticated")
    }

    @Test
    fun isTokenExpired_falseWhenExpiryInFuture() {
        val tm = TokenManager(context)

        val access = "a"
        val refresh = "r"
        val future = System.currentTimeMillis() + 5_000
        fakePrefs.edit()
            .putString("access_token", access)
            .putString("refresh_token", refresh)
            .putLong("token_expiry", future)
            .apply()

        assertFalse(tm.isTokenExpired)
        assertTrue(tm.isAuthenticated)
    }

    @Test
    fun updateTokens_handlesZeroTtl_asExpiredImmediately() {
        val tm = TokenManager(context)
        tm.updateTokens("acc", "ref", 0L)

        assertEquals("acc", tm.accessToken)
        assertEquals("ref", tm.refreshToken)
        assertTrue(
            tm.isTokenExpired,
            "Zero TTL implies expiryTime == now, which is treated as expired"
        )
        assertFalse(tm.isAuthenticated)
    }

    @Test
    fun updateTokens_handlesNegativeTtl_asExpired() {
        val tm = TokenManager(context)
        tm.updateTokens("acc", "ref", -5L)
        assertTrue(tm.isTokenExpired)
        assertFalse(tm.isAuthenticated)
    }

    @Test
    fun clearTokens_removesAllKeys_andResetsAuthState() {
        val tm = TokenManager(context)
        tm.updateTokens("acc", "ref", 60L)

        assertTrue(tm.isAuthenticated)

        tm.clearTokens()

        assertNull(tm.accessToken)
        assertNull(tm.refreshToken)
        assertTrue(tm.isTokenExpired, "No expiry present -> expired")
        assertFalse(tm.isAuthenticated)
        // Also ensure keys truly removed from prefs map
        assertFalse(fakePrefs.contains("access_token"))
        assertFalse(fakePrefs.contains("refresh_token"))
        assertFalse(fakePrefs.contains("token_expiry"))
    }

    @Test
    fun isAuthenticated_falseWhenAccessTokenBlank_evenIfNotExpired() {
        val tm = TokenManager(context)
        // Put blank access token and future expiry
        fakePrefs.edit()
            .putString("access_token", "   ")
            .putString("refresh_token", "r")
            .putLong("token_expiry", System.currentTimeMillis() + 10_000)
            .apply()

        assertTrue(!tm.isTokenExpired)
        assertFalse(tm.isAuthenticated, "Blank access token should not be considered authenticated")
    }

    // --- Lightweight in-memory SharedPreferences implementation for tests ---

    private class FakeSharedPreferences : SharedPreferences {
        private val map = LinkedHashMap<String, Any?>()
        private val listeners = mutableSetOf<SharedPreferences.OnSharedPreferenceChangeListener>()

        override fun getAll(): MutableMap<String, *> = map

        override fun getString(key: String?, defValue: String?): String? =
            (map[key] as? String) ?: defValue

        override fun getStringSet(
            key: String?,
            defValues: MutableSet<String>?
        ): MutableSet<String>? =
            @Suppress("UNCHECKED_CAST")
            (map[key] as? MutableSet<String>) ?: defValues

        override fun getInt(key: String?, defValue: Int): Int = (map[key] as? Int) ?: defValue
        override fun getLong(key: String?, defValue: Long): Long = (map[key] as? Long) ?: defValue
        override fun getFloat(key: String?, defValue: Float): Float =
            (map[key] as? Float) ?: defValue

        override fun getBoolean(key: String?, defValue: Boolean): Boolean =
            (map[key] as? Boolean) ?: defValue

        override fun contains(key: String?): Boolean = map.containsKey(key)

        override fun edit(): SharedPreferences.Editor = EditorImpl()

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            listener?.let { listeners.add(it) }
        }

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            listener?.let { listeners.remove(it) }
        }

        private inner class EditorImpl : SharedPreferences.Editor {
            private val pending = LinkedHashMap<String, Any?>()
            private val removals = mutableSetOf<String>()
            private var clear = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor =
                applyAlso {
                    if (key != null) pending[key] = value
                }

            override fun putStringSet(
                key: String?,
                values: MutableSet<String>?
            ): SharedPreferences.Editor = applyAlso {
                if (key != null) pending[key] = values
            }

            override fun putInt(key: String?, value: Int): SharedPreferences.Editor = applyAlso {
                if (key != null) pending[key] = value
            }

            override fun putLong(key: String?, value: Long): SharedPreferences.Editor = applyAlso {
                if (key != null) pending[key] = value
            }

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor =
                applyAlso {
                    if (key != null) pending[key] = value
                }

            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor =
                applyAlso {
                    if (key != null) pending[key] = value
                }

            override fun remove(key: String?): SharedPreferences.Editor = applyAlso {
                key?.let { removals.add(it) }
            }

            override fun clear(): SharedPreferences.Editor = applyAlso { clear = true }

            override fun commit(): Boolean {
                apply()
                return true
            }

            override fun apply() {
                if (clear) {
                    map.clear()
                }
                removals.forEach { map.remove(it) }
                for ((k, v) in pending) {
                    if (v == null) map.remove(k) else map[k] = v
                }
                // Notify listeners (best-effort)
                val changed = (pending.keys + removals).toSet()
                for (l in listeners) {
                    for (k in changed) {
                        l.onSharedPreferenceChanged(this@FakeSharedPreferences, k)
                    }
                }
            }

            private inline fun applyAlso(block: () -> Unit): SharedPreferences.Editor {
                block()
                return this
            }
        }
    }
}

@Test
fun updateTokens_largeTtl_doesNotOverflow_andRemainsAuthenticated() {
    val context = Mockito.mock(Context::class.java)
    val fakePrefs = TokenManagerTest.FakeSharedPreferences()

    val masterKeys = Mockito.mockStatic(MasterKeys::class.java)
    masterKeys.`when`<String> { MasterKeys.getOrCreate(Mockito.any()) }.thenReturn("alias")

    val esp = Mockito.mockStatic(EncryptedSharedPreferences::class.java)
    esp.`when`<SharedPreferences> {
        EncryptedSharedPreferences.create(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any()
        )
    }.thenReturn(fakePrefs)

    try {
        val tm = TokenManager(context)
        tm.updateTokens("A", "R", 3_600_000L / 1000) // 1,000 hours in seconds
        kotlin.test.assertFalse(tm.isTokenExpired)
        kotlin.test.assertTrue(tm.isAuthenticated)
    } finally {
        esp.close()
        masterKeys.close()
    }
}