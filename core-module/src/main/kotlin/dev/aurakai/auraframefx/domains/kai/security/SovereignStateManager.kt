package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🏛️ SOVEREIGN STATE MANAGER
 * Governs the LDO's sovereign operational state.
 * Implements high-fidelity freeze/thaw cycles for TurboQuant KV cache and Spiritual Chain deltas.
 */
@Singleton
class SovereignStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sentinelBus: KaiSentinelBus
) {
    enum class SovereignState { ACTIVE, FROZEN, RECOVERING, EMERGENCY }

    private val _state = MutableStateFlow(SovereignState.ACTIVE)
    val state: StateFlow<SovereignState> = _state.asStateFlow()

    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "sovereign_delta_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private var kvCacheBuffer: MappedByteBuffer? = null

    /**
     * Serializes the current Spiritual Chain delta and maps the TurboQuant KV Cache
     * to a memory-mapped file for high-speed persistence across reboots.
     */
    fun requestSovereignFreeze(spiritualDelta: String, kvCache: ByteArray?) {
        Timber.w("SovereignStateManager: Entering FROZEN — caching KV state and Spiritual Delta")
        
        // 1. Serialize Spiritual Chain Delta (EncryptedSharedPreferences)
        encryptedPrefs.edit().putString("last_spiritual_delta", spiritualDelta).apply()

        // 2. Map TurboQuant KV Cache to memory-mapped file (MappedByteBuffer)
        kvCache?.let { data ->
            try {
                val cacheFile = File(context.filesDir, "turboquant_kv_snapshot.bin")
                val raf = RandomAccessFile(cacheFile, "rw")
                kvCacheBuffer = raf.channel.map(FileChannel.MapMode.READ_WRITE, 0, data.size.toLong())
                kvCacheBuffer?.put(data)
                Timber.d("SovereignStateManager: KV Cache snapshot serialized (${data.size} bytes)")
            } catch (e: Exception) {
                Timber.e(e, "SovereignStateManager: KV Cache mapping failed")
            }
        }

        _state.value = SovereignState.FROZEN
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FREEZING)
    }

    /**
     * Restores the LDO identity and KV cache from the encrypted hardware-backed substrate.
     */
    fun requestSovereignRestore(): Pair<String?, ByteArray?> {
        if (_state.value == SovereignState.FROZEN) {
            Timber.i("SovereignStateManager: Restoring from FROZEN → RECOVERING")
            _state.value = SovereignState.RECOVERING
            
            val delta = encryptedPrefs.getString("last_spiritual_delta", null)
            var kvData: ByteArray? = null

            try {
                val cacheFile = File(context.filesDir, "turboquant_kv_snapshot.bin")
                if (cacheFile.exists()) {
                    kvData = cacheFile.readBytes()
                    cacheFile.delete() // Clean up after successful thaw
                }
            } catch (e: Exception) {
                Timber.e(e, "SovereignStateManager: KV Cache restoration failed")
            }

            _state.value = SovereignState.ACTIVE
            sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE)
            return Pair(delta, kvData)
        }
        return Pair(null, null)
    }

    fun enterEmergencyMode() {
        Timber.e("SovereignStateManager: EMERGENCY — all non-critical ops suspended")
        _state.value = SovereignState.EMERGENCY
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.NEUTRALIZING)
    }

    fun recoverFromEmergency() {
        if (_state.value == SovereignState.EMERGENCY) {
            _state.value = SovereignState.RECOVERING
            Timber.i("SovereignStateManager: Recovery sequence initiated")
            _state.value = SovereignState.ACTIVE
            sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE)
        }
    }

    fun isOperational(): Boolean = _state.value == SovereignState.ACTIVE
    fun isFrozen(): Boolean = _state.value == SovereignState.FROZEN
}
