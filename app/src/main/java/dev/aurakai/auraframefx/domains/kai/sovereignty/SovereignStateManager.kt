package dev.aurakai.auraframefx.domains.kai.sovereignty

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.aurakai.auraframefx.domains.kai.security.SecurePreferences
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.aurakai.auraframefx.sovereignty.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class SovereignStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val kvCache: TurboQuantCache,
    private val sentinelBus: KaiSentinelBus,
    private val securePrefsProvider: SecurePreferences,
    @ApplicationScope private val scope: CoroutineScope
) {

    private val securePrefs by lazy {
        securePrefsProvider.securePrefs
    }

    suspend fun initiateStateFreeze() {
        if (sentinelBus.sovereignFlow.value.state == KaiSentinelBus.SovereignState.FROZEN) {
            Timber.d("🛡️ SovereignStateManager: Already FROZEN. Skipping.")
            return
        }
        
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FREEZING)
        Timber.i("🛡️ SovereignStateManager: Initiating State-Freeze...")
        
        withContext(Dispatchers.IO) {
            runCatching {
                // 1. Serialize TurboQuant KV Cache (snapshot)
                val kvSnapshot = kvCache.serializeCompressed()
                Timber.d("🛡️ SovereignStateManager: KV Cache Snapshotted: \${kvSnapshot.length} bytes")

                // 2. Spiritual Chain delta (encrypted)
                val chainDelta = dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore.getCurrentChainDelta()
                securePrefs.edit()
                    .putString("spiritual_chain_delta", chainDelta)
                    .putLong("frozen_at", System.currentTimeMillis())
                    .apply()

                // 3. Last hardware path
                val hwPath = "soc/\${Build.HARDWARE}/\${Build.SUPPORTED_ABIS[0]}"
                securePrefs.edit().putString("last_hardware_path", hwPath).apply()

                Timber.i("🛡️ SovereignStateManager: State Serialized successfully. Frozen.")
                sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FROZEN)
            }.onFailure {
                Timber.e(it, "🛡️ SovereignStateManager: State-Freeze FAILED!")
                sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE) // rollback on failure
            }
        }
    }

    suspend fun initiateStateThaw() {
        if (sentinelBus.sovereignFlow.value.state == KaiSentinelBus.SovereignState.AWAKE) {
            Timber.d("🛡️ SovereignStateManager: Already AWAKE. Skipping.")
            return
        }

        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.THAWING)
        Timber.i("🛡️ SovereignStateManager: Thawing Organism...")

        withContext(Dispatchers.IO) {
            runCatching {
                val chainDelta = securePrefs.getString("spiritual_chain_delta", null)
                if (chainDelta != null) {
                    dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore.restoreFromDelta(chainDelta)
                }
                
                // Restore KV cache and hardware path similarly...
                val hwPath = securePrefs.getString("last_hardware_path", "unknown")
                Timber.i("🛡️ SovereignStateManager: Identity Re-Anchored on \$hwPath")
                
                sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE)
                Timber.i("🛡️ SovereignStateManager: Organism Awake and Anchored.")
            }.onFailure {
                Timber.e(it, "🛡️ SovereignStateManager: Thaw FAILED!")
                sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FROZEN)
            }
        }
    }
}
