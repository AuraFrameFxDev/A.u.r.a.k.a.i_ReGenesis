package dev.aurakai.auraframefx.domains.kai.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.SystemMonitor
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.core.security.SecurityContext
import dev.aurakai.auraframefx.domains.kai.security.SovereignPerimeter
import dev.aurakai.auraframefx.domains.kai.security.SovereignStateManager
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SystemStatusState(
    val hasRoot: Boolean = false,
    val isShizukuAvailable: Boolean = false,
    val bootloaderUnlocked: Boolean = false,
    val oemUnlockSupported: Boolean = false,
    val verifiedBootState: String = "unknown",
    val batteryLevel: Int = 0,
    val developerOptionsEnabled: Boolean = false,
    val deviceFingerprint: String = "",
    val cpuUsage: Float = 0f,
    val memoryUsedMb: Long = 0L,
    val memoryAvailableMb: Long = 0L,
    val batteryCurrentMa: Int = 0,
    val isLoading: Boolean = true,
    val threatLevel: KaiSentinelBus.ThreatLevel = KaiSentinelBus.ThreatLevel.NOMINAL,
    val detectedThreats: Int = 0,
    val lastScanTime: Long = 0L
)

data class LogEntry(
    val level: String,
    val tag: String,
    val message: String,
    val timestamp: String,
    val color: Long = 0xFF00E5FF
)

data class SystemLogsState(
    val entries: List<LogEntry> = emptyList(),
    val filter: String = "",
    val isStreaming: Boolean = false
)

@HiltViewModel
class KaiSystemViewModel @Inject constructor(
    val sentinelBus: KaiSentinelBus,
    val sovereignPerimeter: SovereignPerimeter,
    private val sovereignStateManager: SovereignStateManager,
    private val kaiAgent: KaiAgent,
    private val securityContext: SecurityContext,
    private val systemMonitor: SystemMonitor,
    private val bootloaderManager: BootloaderManager
) : ViewModel() {

    private val _systemStatus = MutableStateFlow(SystemStatusState())
    val systemStatus: StateFlow<SystemStatusState> = _systemStatus.asStateFlow()

    private val _logsState = MutableStateFlow(SystemLogsState())
    val logsState: StateFlow<SystemLogsState> = _logsState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        collectSecurityContext()
        collectSystemMetrics()
        loadSystemStatus()
        startMonitoring()
    }

    private fun loadSystemStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hasRoot = Shell.isAppGrantedRoot() == true
                val shizuku = ShizukuManager.isShizukuAvailable()
                val preflight = bootloaderManager.collectPreflightSignals()

                _systemStatus.value = _systemStatus.value.copy(
                    hasRoot = hasRoot,
                    isShizukuAvailable = shizuku,
                    bootloaderUnlocked = preflight.isBootloaderUnlocked,
                    oemUnlockSupported = preflight.oemUnlockSupported,
                    verifiedBootState = preflight.verifiedBootState,
                    batteryLevel = preflight.batteryLevel,
                    developerOptionsEnabled = preflight.developerOptionsEnabled,
                    deviceFingerprint = preflight.deviceFingerprint,
                    isLoading = false
                )
            } catch (e: Exception) {
                _systemStatus.value = _systemStatus.value.copy(isLoading = false)
                _error.value = "System status load failed: ${e.message}"
            }
        }
    }

    private fun collectSecurityContext() {
        viewModelScope.launch {
            combine(
                securityContext.securityState,
                securityContext.threatDetectionActive
            ) { secState, _ ->
                secState
            }.collect { secState ->
                _systemStatus.value = _systemStatus.value.copy(
                    threatLevel = secState.threatLevel,
                    detectedThreats = secState.detectedThreats.size,
                    lastScanTime = secState.lastScanTime
                )
            }
        }
    }

    private fun collectSystemMetrics() {
        viewModelScope.launch {
            combine(
                systemMonitor.cpuUsage,
                systemMonitor.memoryUsage,
                systemMonitor.availableMemory,
                systemMonitor.batteryCurrentMa
            ) { cpu, mem, avail, battery ->
                _systemStatus.value = _systemStatus.value.copy(
                    cpuUsage = cpu,
                    memoryUsedMb = mem / 1_048_576L,
                    memoryAvailableMb = avail / 1_048_576L,
                    batteryCurrentMa = battery
                )
            }.collect { }
        }
    }

    private fun startMonitoring() {
        systemMonitor.startMonitoring(intervalMs = 5_000)
        securityContext.startThreatDetection()
    }

    fun refreshStatus() {
        _systemStatus.value = _systemStatus.value.copy(isLoading = true)
        loadSystemStatus()
    }

    fun startLogStream(filter: String = "") {
        if (_logsState.value.isStreaming) return
        _logsState.value = _logsState.value.copy(isStreaming = true, filter = filter)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val args = arrayOf("logcat", "-d", "-v", "time", "-t", "200")
                val result = Shell.cmd(*args).exec()
                val entries = result.out.mapNotNull { line -> parseLogLine(line) }.takeLast(200)
                _logsState.value = _logsState.value.copy(entries = entries, isStreaming = false)
            } catch (e: Exception) {
                _logsState.value = _logsState.value.copy(isStreaming = false)
                _error.value = "Logcat failed"
            }
        }
    }

    fun triggerSecurityScan() {
        viewModelScope.launch {
            securityContext.startThreatDetection()
        }
    }

    fun triggerFreeze() {
        sovereignStateManager.requestSovereignFreeze("MANUAL_TRIGGER", null)
    }

    fun softReboot() {
        viewModelScope.launch(Dispatchers.IO) {
            Shell.cmd("reboot soft").exec()
        }
    }

    fun killGhosts() {
        viewModelScope.launch(Dispatchers.IO) {
            Shell.cmd("am kill-all").exec()
        }
    }

    private fun parseLogLine(line: String): LogEntry? {
        if (line.isBlank()) return null
        
        // High-importance filter for the Monitoring HUD
        if (line.contains("AOC", ignoreCase = true) || 
            line.contains("CHRE", ignoreCase = true) ||
            line.contains("USF", ignoreCase = true) ||
            line.contains("Calculated CCT", ignoreCase = true)) {
            return null // Quiet the sensor spam
        }

        return try {
            // Simplified parsing for 04-04 15:04:00.599 format
            val parts = line.split(" ").filter { it.isNotBlank() }
            if (parts.size < 5) return null
            
            val level = parts[4]
            val tag = if (parts.size > 5) parts[5] else "Unknown"
            val message = parts.drop(6).joinToString(" ")

            LogEntry(
                level = level,
                tag = tag,
                message = message,
                timestamp = parts[1],
                color = when(level) {
                    "E", "F" -> 0xFFFF4444
                    "W" -> 0xFFFFD700
                    "I" -> 0xFF00FF41
                    else -> 0xFF00E5FF
                }
            )
        } catch (e: Exception) {
            null
        }
    }
}
