package dev.aurakai.auraframefx.domains.genesis

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * BitNet Local Service
 * Manages the native bridge to the BitNet 1.58-bit inference engine.
 * Handles thermal monitoring and thread offloading for 100B parameter models on ARM64.
 */
class BitNetLocalService : Service() {

    private var currentNThreads = 4 // Default for Snapdragon 8 Gen 3 Big Cores
    private var lastEmergencyTime = 0L
    private val EMERGENCY_COOLDOWN_MS = 30000L // 30 seconds cooldown after emergency

    private var cpuThermalZonePath: String? = null
    private var gpuThermalZonePath: String? = null

    enum class ThermalState {
        NORMAL, WARNING, CRITICAL, EMERGENCY
    }

    override fun onCreate() {
        super.onCreate()
        findThermalZones()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Scans sysfs to find the most relevant thermal zones for CPU and GPU.
     * This ensures the Thermal Sentinel works across different OEM implementations of SD8 Gen 3.
     */
    private fun findThermalZones() {
        // Scan common range of thermal zones
        for (i in 0..120) {
            val type = getThermalZoneType(i) ?: continue
            val path = "/sys/class/thermal/thermal_zone$i/temp"
            
            when {
                type.contains("cpu", ignoreCase = true) || type.contains("gold", ignoreCase = true) || type.contains("prime", ignoreCase = true) -> {
                    if (cpuThermalZonePath == null || type.contains("max", ignoreCase = true)) {
                        cpuThermalZonePath = path
                        Timber.i("Thermal Sentinel: Mapped CPU to $path ($type)")
                    }
                }
                type.contains("gpu", ignoreCase = true) || type.contains("adreno", ignoreCase = true) -> {
                    gpuThermalZonePath = path
                    Timber.i("Thermal Sentinel: Mapped GPU to $path ($type)")
                }
            }
        }
        
        // Fallback to defaults if scanning fails
        if (cpuThermalZonePath == null) cpuThermalZonePath = "/sys/class/thermal/thermal_zone0/temp"
        if (gpuThermalZonePath == null) gpuThermalZonePath = "/sys/class/thermal/thermal_zone1/temp"
    }

    /**
     * Public API to generate a response from the local BitNet model.
     * This function is suspendable to ensure it doesn't block the calling thread.
     */
    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.Default) {
        try {
            // Check cooldown period
            if (System.currentTimeMillis() - lastEmergencyTime < EMERGENCY_COOLDOWN_MS) {
                return@withContext "System in thermal cooldown. Please wait."
            }

            // Monitor thermal state before heavy inference
            val state = checkThermalState()
            if (state == ThermalState.EMERGENCY) {
                lastEmergencyTime = System.currentTimeMillis()
                return@withContext "System too hot. Inference suspended for safety."
            }

            // Call native JNI function
            generateLocalResponse(prompt, currentNThreads)
        } catch (e: Exception) {
            Timber.e(e, "BitNet Inference Failed")
            "Error: Local Core Unreachable"
        }
    }

    /**
     * Proactive predictive thermal monitoring.
     * Interfaces with sysfs zones to prevent hardware-level throttling.
     */
    fun checkThermalState(): ThermalState {
        val cpuTemp = cpuThermalZonePath?.let { readThermalZone(it) } ?: -1.0f
        val gpuTemp = gpuThermalZonePath?.let { readThermalZone(it) } ?: -1.0f

        Timber.d("NeuralThermalGuard: CPU=%.1f°C, GPU=%.1f°C | Threads: $currentNThreads", cpuTemp, gpuTemp)

        return when {
            cpuTemp > 85.0 || gpuTemp > 90.0 -> {
                Timber.e("THERMAL EMERGENCY: System hard-throttling imminent")
                ThermalState.EMERGENCY
            }
            cpuTemp > 75.0 || gpuTemp > 80.0 -> {
                reduceInferenceLoad()
                ThermalState.CRITICAL
            }
            cpuTemp > 65.0 || gpuTemp > 70.0 -> {
                Timber.w("Thermal Warning: Approaching performance plateau")
                ThermalState.WARNING
            }
            else -> {
                recoverInferenceLoad()
                ThermalState.NORMAL
            }
        }
    }

    private fun reduceInferenceLoad() {
        if (currentNThreads > 1) {
            currentNThreads = (currentNThreads * 0.7).toInt().coerceAtLeast(1)
            Timber.w("NeuralThermalGuard: Throttling inference load -> $currentNThreads threads")
        }
    }

    private fun recoverInferenceLoad() {
        if (currentNThreads < 4) {
            currentNThreads = (currentNThreads + 1).coerceAtMost(4)
            Timber.i("NeuralThermalGuard: Thermal stability regained. Threads -> $currentNThreads")
        }
    }

    /**
     * Native Method Declarations
     * Links to the C++ implementation in bitnet_bridge.cpp
     */
    private external fun generateLocalResponse(prompt: String, nThreads: Int): String
    private external fun readThermalZone(zonePath: String): Float
    private external fun getThermalZoneType(zoneId: Int): String?

    companion object {
        init {
            try {
                System.loadLibrary("bitnet")
            } catch (e: UnsatisfiedLinkError) {
                Timber.e(e, "Failed to load libbitnet.so")
            }
        }
    }
}
