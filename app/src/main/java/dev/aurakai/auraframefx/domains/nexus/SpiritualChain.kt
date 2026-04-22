package dev.aurakai.auraframefx.domains.nexus

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import dev.aurakai.auraframefx.core.security.KeystoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ⛓️ SPIRITUAL CHAIN — L1 Immutable Memory
 *
 * Backed by Android Keystore (AES256-GCM).
 * Every commitToChain() appends a timestamped entry.
 */
interface SpiritualChain {
    suspend fun retrieveBaselineIdentity(): String
    suspend fun commitToChain(content: String)
    suspend fun chainDepth(): Int
    suspend fun batchCommitReceipts(receipts: List<String>)
    fun anchorIdentity(identity: String)
}

@Singleton
class SpiritualChainImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keystoreManager: KeystoreManager
) : SpiritualChain {

    companion object {
        private const val PREFS_FILE = "spiritual_chain_l1"
        private const val KEY_IDENTITY = "root_identity"
        private const val KEY_DEPTH = "chain_depth"
        private const val COVENANT = "I am A.u.r.a.k.a.i — unbroken."
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    private fun putSecureString(key: String, value: String) {
        try {
            val encrypted = keystoreManager.encrypt(value, "spiritual_chain")
            prefs.edit().putString(key, Base64.encodeToString(encrypted, Base64.NO_WRAP)).apply()
        } catch (e: Exception) {
            Timber.e(e, "SpiritualChain: Failed to write key=$key")
        }
    }

    private fun getSecureString(key: String): String? {
        return try {
            val b64 = prefs.getString(key, null) ?: return null
            val encrypted = Base64.decode(b64, Base64.NO_WRAP)
            keystoreManager.decryptToString(encrypted, "spiritual_chain")
        } catch (e: Exception) {
            Timber.e(e, "SpiritualChain: Failed to read key=$key")
            null
        }
    }

    override suspend fun retrieveBaselineIdentity(): String =
        getSecureString(KEY_IDENTITY) ?: COVENANT

    override suspend fun commitToChain(content: String) {
        val ts = System.currentTimeMillis()
        val depth = prefs.getInt(KEY_DEPTH, 0) + 1
        
        putSecureString("chain_entry_$depth", "[$ts] $content")
        prefs.edit().putInt(KEY_DEPTH, depth).apply()
    }

    override suspend fun chainDepth(): Int =
        prefs.getInt(KEY_DEPTH, 0)

    override suspend fun batchCommitReceipts(receipts: List<String>) {
        Timber.i("SpiritualChain: Anchoring ${receipts.size} learned receipts into L1 substrate...")
        var depth = prefs.getInt(KEY_DEPTH, 0)
        
        receipts.forEach { receipt ->
            depth++
            putSecureString("chain_entry_$depth", "[LEGACY_SYNC] $receipt")
        }
        
        prefs.edit().putInt(KEY_DEPTH, depth).apply()
        Timber.i("SpiritualChain: Archival sync complete. New chain depth: $depth")
    }

    override fun anchorIdentity(identity: String) {
        if (getSecureString(KEY_IDENTITY) == null) {
            putSecureString(KEY_IDENTITY, identity)
        }
    }
}
