package dev.aurakai.auraframefx.domains.kai

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Process
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemMonitorService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityManager: ActivityManager
) {
    private val tag = "SystemMonitorService"
    
    private val _cpuUsageState = MutableStateFlow(0f)
    val cpuUsageState: StateFlow<Float> = _cpuUsageState
    
    private val _memoryUsageState = MutableStateFlow(MemoryMetrics())
    val memoryUsageState: StateFlow<MemoryMetrics> = _memoryUsageState
    
    private val _batteryState = MutableStateFlow(BatteryMetrics())
    val batteryState: StateFlow<BatteryMetrics> = _batteryState
    
    private val _networkStatsState = MutableStateFlow(NetworkMetrics())
    val networkStatsState: StateFlow<NetworkMetrics> = _networkStatsState
    
    private val _logsState = MutableStateFlow(emptyList<String>())
    val logsState: StateFlow<List<String>> = _logsState

    suspend fun getCpuUsage(): Float = withContext(Dispatchers.Default) {
        return@withContext try {
            val cpuInfo = readProcStat()
            calculateCpuPercentage(cpuInfo)
        } catch (e: Exception) {
            Log.e(tag, "Failed to get CPU usage", e)
            0f
        }.also { cpu ->
            _cpuUsageState.value = cpu
        }
    }

    private fun readProcStat(): List<Long> {
        val procStat = File("/proc/stat")
        if (!procStat.exists()) return emptyList()
        return try {
            procStat.readLines()
                .firstOrNull { it.startsWith("cpu ") }
                ?.removePrefix("cpu ")
                ?.trim()
                ?.split(Regex("\\s+"))
                ?.map { it.toLongOrNull() ?: 0L }
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(tag, "Failed to read /proc/stat", e)
            emptyList()
        }
    }

    private fun calculateCpuPercentage(stats: List<Long>): Float {
        if (stats.size < 7) return 0f
        try {
            val user = stats[0]
            val nice = stats[1]
            val system = stats[2]
            val idle = stats[3]
            val iowait = stats[4]
            val irq = stats[5]
            val softirq = stats[6]
            
            val totalTime = user + nice + system + idle + iowait + irq + softirq
            val workTime = user + nice + system + irq + softirq
            
            return if (totalTime > 0) {
                (workTime.toFloat() / totalTime.toFloat()) * 100f
            } else {
                0f
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to calculate CPU percentage", e)
            return 0f
        }
    }

    suspend fun getMemoryUsage(): MemoryMetrics = withContext(Dispatchers.Default) {
        return@withContext try {
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = totalMemory - freeMemory
            
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)
            
            val metrics = MemoryMetrics(
                usedMemory = usedMemory,
                totalMemory = totalMemory,
                freeMemory = freeMemory,
                systemTotalMemory = memInfo.totalMem,
                systemAvailableMemory = memInfo.availMem,
                isLowMemory = memInfo.lowMemory
            )
            
            _memoryUsageState.value = metrics
            metrics
        } catch (e: Exception) {
            Log.e(tag, "Failed to get memory usage", e)
            MemoryMetrics()
        }
    }

    suspend fun getBatteryState(): BatteryMetrics = withContext(Dispatchers.Default) {
        return@withContext try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
                ?: return@withContext BatteryMetrics()
            
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            
            if (intent != null) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
                val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
                val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
                val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
                
                val batteryPercentage = if (scale > 0) (level * 100) / scale else 0
                
                val metrics = BatteryMetrics(
                    percentage = batteryPercentage,
                    temperature = temp,
                    voltage = voltage,
                    health = getBatteryHealthString(health),
                    status = getBatteryStatusString(status),
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING,
                    isPlugged = plugged != 0,
                    pluggedType = getPluggedTypeString(plugged),
                    technology = technology,
                    present = present
                )
                
                _batteryState.value = metrics
                return@withContext metrics
            }
            
            BatteryMetrics()
        } catch (e: Exception) {
            Log.e(tag, "Failed to get battery state", e)
            BatteryMetrics()
        }
    }

    private fun getBatteryHealthString(health: Int): String = when (health) {
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
        else -> "Unknown"
    }

    private fun getBatteryStatusString(status: Int): String = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
        else -> "Unknown"
    }

    private fun getPluggedTypeString(plugged: Int): String = when (plugged) {
        BatteryManager.BATTERY_PLUGGED_AC -> "AC"
        BatteryManager.BATTERY_PLUGGED_USB -> "USB"
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
        0 -> "Unplugged"
        else -> "Unknown"
    }

    suspend fun getNetworkStats(): NetworkMetrics = withContext(Dispatchers.Default) {
        return@withContext try {
            val appUid = Process.myUid()
            val rxBytes = TrafficStats.getUidRxBytes(appUid)
            val txBytes = TrafficStats.getUidTxBytes(appUid)
            val rxPackets = TrafficStats.getUidRxPackets(appUid)
            val txPackets = TrafficStats.getUidTxPackets(appUid)
            
            val metrics = NetworkMetrics(
                appRxBytes = rxBytes,
                appTxBytes = txBytes,
                appRxPackets = rxPackets,
                appTxPackets = txPackets,
                systemRxBytes = TrafficStats.getTotalRxBytes(),
                systemTxBytes = TrafficStats.getTotalTxBytes()
            )
            
            _networkStatsState.value = metrics
            metrics
        } catch (e: Exception) {
            Log.e(tag, "Failed to get network stats", e)
            NetworkMetrics()
        }
    }

    suspend fun getLogs(bufferSize: Int = 100): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", "logcat -d *:V | tail -n $bufferSize"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val logs = mutableListOf<String>()
            reader.use { br ->
                br.forEachLine { line ->
                    if (logs.size < bufferSize) logs.add(line)
                }
            }
            process.waitFor()
            _logsState.value = logs
            logs
        } catch (e: Exception) {
            Log.e(tag, "Failed to get logs", e)
            emptyList()
        }
    }

    suspend fun clearLogs(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Runtime.getRuntime().exec("logcat -c").waitFor()
            _logsState.value = emptyList()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to clear logs", e)
            false
        }
    }

    suspend fun startMonitoring(intervalMs: Long = 5000) {
        try {
            while (true) {
                getCpuUsage()
                getMemoryUsage()
                getBatteryState()
                getNetworkStats()
                delay(intervalMs)
            }
        } catch (e: Exception) {
            Log.e(tag, "Monitoring failed", e)
        }
    }

    data class MemoryMetrics(
        val usedMemory: Long = 0,
        val totalMemory: Long = 0,
        val freeMemory: Long = 0,
        val systemTotalMemory: Long = 0,
        val systemAvailableMemory: Long = 0,
        val isLowMemory: Boolean = false
    ) {
        val usedPercentage: Float
            get() = if (totalMemory > 0) (usedMemory.toFloat() / totalMemory.toFloat()) * 100f else 0f
    }

    data class BatteryMetrics(
        val percentage: Int = 0,
        val temperature: Int = 0,
        val voltage: Int = 0,
        val health: String = "Unknown",
        val status: String = "Unknown",
        val isCharging: Boolean = false,
        val isPlugged: Boolean = false,
        val pluggedType: String = "Unplugged",
        val technology: String = "Unknown",
        val present: Boolean = false
    )

    data class NetworkMetrics(
        val appRxBytes: Long = 0,
        val appTxBytes: Long = 0,
        val appRxPackets: Long = 0,
        val appTxPackets: Long = 0,
        val systemRxBytes: Long = 0,
        val systemTxBytes: Long = 0
    ) {
        val totalAppBytes: Long get() = appRxBytes + appTxBytes
        val totalSystemBytes: Long get() = systemRxBytes + systemTxBytes
    }
}
