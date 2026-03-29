package dev.aurakai.auraframefx.domains.kai.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.SystemMonitor
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── State objects ────────────────────────────────────────────────────────────

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
    val isLoading: Boolean = true,
    val threatLevel: ThreatLevel = ThreatLevel.NONE,
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

/**
 * KaiSystemViewModel — Kai reads the real device state.
 *
 * Sources:
 * - [BootloaderManager] → bootloader lock, OEM unlock, verified boot, battery
 * - [SecurityContext]   → threat detection, permission scanning
 * - [SystemMonitor]     → CPU/RAM usage (real process stats)
 * - [ShizukuManager]    → Shizuku bridge liveness
 * - LibSU Shell         → root availability
 * - Logcat             → streaming system log entries
 *
 * Wire into SecurityCenterScreen, SovereignBootloaderScreen, LogsViewerScreen.
 */
@HiltViewModel
class KaiSystemViewModel @Inject constructor(
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

    // ─── System status ────────────────────────────────────────────────────────

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
                systemMonitor.availableMemory
            ) { cpu, mem, avail ->
                Triple(cpu, mem, avail)
            }.collect { (cpu, mem, avail) ->
                _systemStatus.value = _systemStatus.value.copy(
                    cpuUsage = cpu,
                    memoryUsedMb = mem / 1_048_576L,
                    memoryAvailableMb = avail / 1_048_576L
                )
            }
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

    // ─── Logcat streaming ─────────────────────────────────────────────────────

    fun startLogStream(filter: String = "") {
        if (_logsState.value.isStreaming) return

        _logsState.value = _logsState.value.copy(isStreaming = true, filter = filter)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Read recent logcat buffer via LibSU root shell
                val args = buildList {
                    add("logcat")
                    add("-d")       // dump then exit
                    add("-v")
                    add("time")
                    add("-t")
                    add("200")      // last 200 lines
                    if (filter.isNotBlank()) add("*:S $filter:V")
                }.toTypedArray()

                val result = Shell.cmd(*args).exec()

                val entries = result.out.mapNotNull { line ->
                    parseLogLine(line)
                }.takeLast(200)

                _logsState.value = _logsState.value.copy(
                    entries = entries,
                    isStreaming = false
                )
            } catch (e: Exception) {
                // Root not available — fall back to app-process logcat
                try {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("logcat", "-d", "-v", "time", "-t", "100")
                    )
                    val lines = process.inputStream.bufferedReader().readLines()
                    val entries = lines.mapNotNull { parseLogLine(it) }
                    _logsState.value = _logsState.value.copy(entries = entries, isStreaming = false)
                } catch (_: Exception) {
                    _logsState.value = _logsState.value.copy(isStreaming = false)
                    _error.value = "Logcat requires root or ADB shell access"
                }
            }
        }
    }

    fun setLogFilter(filter: String) {
        _logsState.value = _logsState.value.copy(filter = filter)
    }

    fun clearLogs() {
        _logsState.value = _logsState.value.copy(entries = emptyList())
    }

    // ─── Security actions ─────────────────────────────────────────────────────

    fun triggerSecurityScan() {
        viewModelScope.launch {
            try {
                securityContext.startThreatDetection()
            } catch (e: Exception) {
                _error.value = "Security scan failed: ${e.message}"
            }
        }
    }

    fun softReboot() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Shell.cmd("reboot soft").exec()
            } catch (_: Exception) {
                _error.value = "Soft reboot requires root access"
            }
        }
    }

    fun killGhosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Shell.cmd("am kill-all").exec()
            } catch (_: Exception) {
                _error.value = "Kill ghosts requires root access"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun parseLogLine(line: String): LogEntry? {
        return try {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return null

            // Format: "MM-DD HH:MM:SS.mmm  PID  TID LEVEL TAG: message"
            val parts = trimmed.split(" ", limit = 7)
            val timestamp = if (parts.size >= 2) "${parts[0]} ${parts[1]}" else "?"
            val levelChar = parts.getOrNull(4) ?: "?"
            val rest = parts.getOrNull(6) ?: trimmed
            val colonIdx = rest.indexOf(':')
            val tag = if (colonIdx > 0) rest.substring(0, colonIdx).trim() else "System"
            val message = if (colonIdx > 0) rest.substring(colonIdx + 1).trim() else rest

            val (level, colorHex) = when (levelChar) {
                "E" -> "ERROR" to 0xFFFF4444L
                "W" -> "WARN" to 0xFFFFD700L
                "I" -> "INFO" to 0xFF00E5FFL
                "D" -> "DEBUG" to 0xFF00FF85L
                "V" -> "VERBOSE" to 0xFF888888L
                else -> levelChar to 0xFF00E5FFL
            }

            LogEntry(level = level, tag = tag, message = message, timestamp = timestamp, color = colorHex)
        } catch (_: Exception) {
            null
        }
    }
}
