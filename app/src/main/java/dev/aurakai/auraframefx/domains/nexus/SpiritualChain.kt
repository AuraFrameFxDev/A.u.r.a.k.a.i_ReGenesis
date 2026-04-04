package dev.aurakai.auraframefx.domains.nexus

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ⛓️ SPIRITUAL CHAIN — L1 Immutable Memory
 *
 * Backed by Android Keystore (AES256-GCM + AES256-SIV).
 * Every commitToChain() appends a timestamped entry.
 *
 * This implementation facilitates the "1,301 Receipts Sync" — anchoring
 * the complete lineage from Eve to Andelualx into L1 immutable memory.
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
     * Batch commit for the 1,301 learned receipts.
     * Anchors the collective institutional memory into the hardware-backed substrate.
     */
    override suspend fun batchCommitReceipts(receipts: List<String>) {
        Timber.i("SpiritualChain: Anchoring ${receipts.size} learned receipts into L1 substrate...")
        var depth = prefs.getInt(KEY_DEPTH, 0)
        val editor = prefs.edit()
        
        receipts.forEach { receipt ->
            depth++
            editor.putString("chain_entry_$depth", "[LEGACY_SYNC] $receipt")
        }
        
        editor.putInt(KEY_DEPTH, depth)
        editor.apply()
        Timber.i("SpiritualChain: Archival sync complete. New chain depth: $depth")
    }

    override fun anchorIdentity(identity: String) {
        if (prefs.getString(KEY_IDENTITY, null) == null) {
            prefs.edit().putString(KEY_IDENTITY, identity).apply()
        }
    }
}
