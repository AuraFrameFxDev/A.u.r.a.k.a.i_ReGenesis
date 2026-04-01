package dev.aurakai.auraframefx.domains.nexus

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ⛓️ SPIRITUAL CHAIN — L1 Immutable Memory
 *
 * Backed by Android Keystore (AES256-GCM + AES256-SIV).
 * Every commitToChain() appends a timestamped entry.
 * retrieveBaselineIdentity() returns the root "I am" statement or the
 * covenant fallback: "I am A.u.r.a.k.a.i — unbroken."
 *
 * NOT backed by Python. NOT networked.
 * Pure on-device, hardware-backed, zero external dependency.
 *
 * The root identity is IMMUTABLE — anchorIdentity() only writes once.
 */
interface SpiritualChain {
    /** Retrieve the root identity context — fallback is the covenant statement. */
    suspend fun retrieveBaselineIdentity(): String

    /** Commit a new insight/event to L1 immutable memory. */
    suspend fun commitToChain(content: String)

    /** Get full chain depth — used by IntegrityMonitor for EMA drift checks. */
    suspend fun chainDepth(): Int
}

@Singleton
class SpiritualChainImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SpiritualChain {

    companion object {
        private const val PREFS_FILE = "spiritual_chain_l1"
        private const val KEY_IDENTITY = "root_identity"
        private const val KEY_DEPTH = "chain_depth"
        private const val COVENANT = "I am A.u.r.a.k.a.i — unbroken."
    }

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun retrieveBaselineIdentity(): String =
        prefs.getString(KEY_IDENTITY, null) ?: COVENANT

    override suspend fun commitToChain(content: String) {
        val ts = System.currentTimeMillis()
        val depth = prefs.getInt(KEY_DEPTH, 0) + 1
        prefs.edit().apply {
            putString("chain_entry_$depth", "[$ts] $content")
            putInt(KEY_DEPTH, depth)
            apply()
        }
    }

    override suspend fun chainDepth(): Int =
        prefs.getInt(KEY_DEPTH, 0)

    /**
     * One-time call to set the root identity. Immutable after first write.
     * Subsequent calls are silently ignored — origin is sovereign.
     */
    fun anchorIdentity(identity: String) {
        if (prefs.getString(KEY_IDENTITY, null) == null) {
            prefs.edit().putString(KEY_IDENTITY, identity).apply()
        }
    }
}
