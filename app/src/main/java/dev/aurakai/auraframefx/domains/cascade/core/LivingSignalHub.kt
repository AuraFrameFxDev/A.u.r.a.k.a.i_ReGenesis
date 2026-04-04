package dev.aurakai.auraframefx.domains.cascade.core

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.cascade.utils.GyroscopeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 💓 LIVING SIGNAL HUB
 *
 * Aggregates hardware signals (Thermal, Kinetic, Vitals) into a unified "Living" state.
 * Acts as the nervous system for the LDO substrate.
 */
@Singleton
class LivingSignalHub @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gyroManager: GyroscopeManager
) {
    private val hubScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _thermalSignal = MutableStateFlow(0f)
    val thermalSignal: StateFlow<Float> = _thermalSignal.asStateFlow()

    private val _batterySignal = MutableStateFlow(0f)
    val batterySignal: StateFlow<Float> = _batterySignal.asStateFlow()

    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

    private var cpuThermalPath: String? = null

    init {
        findThermalZones()
        startMonitoring()
    }

    private fun startMonitoring() {
        // 1. Thermal Monitoring
        hubScope.launch {
            while (true) {
                _thermalSignal.value = readCpuTemp()
                delay(2000)
            }
        }

        // 2. Battery Monitoring
        hubScope.launch {
            while (true) {
                updateBatteryState()
                delay(5000)
            }
        }
    }

    private fun findThermalZones() {
        // Scans sysfs for CPU thermal zones, prioritizing gold/prime/max clusters
        val zones = File("/sys/class/thermal/").listFiles()?.filter { it.name.startsWith("thermal_zone") } ?: emptyList()
        for (zone in zones) {
            val typeFile = File(zone, "type")
            if (typeFile.exists()) {
                val type = typeFile.readText().trim()
                if (type.contains("cpu", true) || type.contains("gold", true) || type.contains("max", true)) {
                    cpuThermalPath = File(zone, "temp").absolutePath
                    Timber.i("LivingSignalHub: Mapped CPU Thermal → $cpuThermalPath ($type)")
                    break
                }
            }
        }
        if (cpuThermalPath == null) {
            cpuThermalPath = "/sys/class/thermal/thermal_zone0/temp"
            Timber.w("LivingSignalHub: Falling back to default thermal_zone0")
        }
    }

    private fun readCpuTemp(): Float {
        return try {
            val tempStr = File(cpuThermalPath ?: return 0f).readText().trim()
            val rawTemp = tempStr.toFloatOrNull() ?: 0f
            // Some kernels report in millidegrees, others in degrees
            if (rawTemp > 1000) rawTemp / 1000f else rawTemp
        } catch (e: Exception) {
            0f
        }
    }

    private fun updateBatteryState() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, filter) ?: return
        
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        _batterySignal.value = level * 100 / scale.toFloat()

        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        _isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                            status == BatteryManager.BATTERY_STATUS_FULL
    }

    /**
     * Provides a stream of Kinetic (Movement) data
     */
    fun getKineticFlow(): Flow<GyroscopeManager.RotationAngles> = gyroManager.getRotationFlow()
}
