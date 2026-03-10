package dev.aurakai.auraframefx.embodiment

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 🔧 Maintenance Mode Detector
 *
 * Monitors system operations and automatically triggers MAINTENANCE mood
 * when Aura & Kai should wear safety equipment.
 *
 * Detects:
 * - Gradle builds
 * - System updates
 * - Cache clearing
 * - Database migrations
 * - Long-running file operations
 * - Heavy processing tasks
 *
 * When maintenance is detected:
 * - Sets mood to MoodState.MAINTENANCE
 * - Aura appears in SAFETY_HARDHAT gear
 * - Kai appears in SAFETY_HARDHAT gear
 * - Both hold their respective diagnostic tools
 *
 * Safety First! ⚠️
 */
@Singleton
class MaintenanceModeDetector @Inject constructor(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _isMaintenanceMode = MutableStateFlow(false)
    val isMaintenanceMode: StateFlow<Boolean> = _isMaintenanceMode

    private val _maintenanceReason = MutableStateFlow<String?>(null)
    val maintenanceReason: StateFlow<String?> = _maintenanceReason

    // Active maintenance operations
    private val activeOperations = mutableSetOf<String>()
    private val operationLock = Any()

    init {
        startMonitoring()
    }

    /**
     * Start a maintenance operation
     *
     * Example:
     * ```
     * detector.startMaintenance("database_migration", "Migrating to v5")
     * // ... perform operation
     * detector.endMaintenance("database_migration")
     * ```
     */
    fun startMaintenance(operationId: String, reason: String) {
        synchronized(operationLock) {
            activeOperations.add(operationId)
            _maintenanceReason.value = reason
            _isMaintenanceMode.value = true
            Timber.d("⚠️ Maintenance started: $operationId - $reason")
        }
    }

    /**
     * End a maintenance operation
     */
    fun endMaintenance(operationId: String) {
        synchronized(operationLock) {
            activeOperations.remove(operationId)

            if (activeOperations.isEmpty()) {
                _isMaintenanceMode.value = false
                _maintenanceReason.value = null
                Timber.d("✅ All maintenance complete")
            } else {
                // Update reason to next active operation
                _maintenanceReason.value = "Multiple operations in progress"
            }
        }
    }

    /**
     * Check if specific operation is active
     */
    fun isOperationActive(operationId: String): Boolean {
        return synchronized(operationLock) {
            activeOperations.contains(operationId)
        }
    }

    /**
     * Start maintenance with automatic timeout
     */
    fun startMaintenanceWithTimeout(
        operationId: String,
        reason: String,
        timeout: Duration = 300.seconds // 5 minutes default
    ) {
        startMaintenance(operationId, reason)

        scope.launch {
            delay(timeout)
            if (isOperationActive(operationId)) {
                Timber.w("⏰ Maintenance operation timed out: $operationId")
                endMaintenance(operationId)
            }
        }
    }

    /**
     * Execute operation with automatic maintenance mode
     */
    suspend fun <T> withMaintenance(
        operationId: String,
        reason: String,
        block: suspend () -> T
    ): T {
        startMaintenance(operationId, reason)
        return try {
            block()
        } finally {
            endMaintenance(operationId)
        }
    }

    /**
     * Monitor system for automatic maintenance detection
     */
    private fun startMonitoring() {
        // Monitor for gradle builds (if running in development)
        scope.launch {
            monitorGradleBuilds()
        }

        // Monitor for heavy file operations
        scope.launch {
            monitorFileSystemLoad()
        }

        // Monitor for database operations
        scope.launch {
            monitorDatabaseOperations()
        }
    }

    private suspend fun monitorGradleBuilds() {
        // Check for .gradle lock files or build processes
        while (scope.isActive) {
            try {
                val buildLockFile = context.filesDir.parentFile?.resolve(".gradle/build.lock")
                if (buildLockFile?.exists() == true) {
                    if (!_isMaintenanceMode.value) {
                        startMaintenanceWithTimeout(
                            "gradle_build",
                            "Building project...",
                            timeout = 600.seconds
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error monitoring gradle builds")
            }

            delay(10.seconds)
        }
    }

    private suspend fun monitorFileSystemLoad() {
        while (scope.isActive) {
            try {
                // Monitor file system I/O (simplified)
                val filesDir = context.filesDir
                val fileCount = filesDir.listFiles()?.size ?: 0

                // If there's heavy file activity, might indicate maintenance
                // This is a placeholder - real implementation would monitor I/O operations
            } catch (e: Exception) {
                Timber.e(e, "Error monitoring file system")
            }

            delay(30.seconds)
        }
    }

    private suspend fun monitorDatabaseOperations() {
        // Monitor for active database migrations or heavy queries
        while (scope.isActive) {
            try {
                // Placeholder for database operation monitoring
                // Real implementation would hook into Room or SQLite
            } catch (e: Exception) {
                Timber.e(e, "Error monitoring database")
            }

            delay(30.seconds)
        }
    }

    /**
     * Get current maintenance status
     */
    fun getMaintenanceStatus(): MaintenanceStatus {
        return MaintenanceStatus(
            isActive = _isMaintenanceMode.value,
            reason = _maintenanceReason.value,
            activeOperations = synchronized(operationLock) {
                activeOperations.toList()
            },
            operationCount = synchronized(operationLock) {
                activeOperations.size
            }
        )
    }

    fun cleanup() {
        scope.cancel()
    }
}

/**
 * Maintenance status data
 */
data class MaintenanceStatus(
    val isActive: Boolean,
    val reason: String?,
    val activeOperations: List<String>,
    val operationCount: Int
) {
    val statusMessage: String
        get() = when {
            !isActive -> "All systems operational"
            operationCount == 1 -> reason ?: "Maintenance in progress"
            else -> "$operationCount operations in progress"
        }
}

/**
 * Common maintenance operations
 */
object MaintenanceOperations {
    const val CACHE_CLEAR = "cache_clear"
    const val DATABASE_MIGRATION = "database_migration"
    const val FILE_SYNC = "file_sync"
    const val BUILD_PROJECT = "build_project"
    const val SYSTEM_UPDATE = "system_update"
    const val ICON_CACHE_REBUILD = "icon_cache_rebuild"
    const val ASSET_IMPORT = "asset_import"
    const val CLEANUP = "cleanup"
}

/**
 * Extension function for EmbodimentEngine to connect with maintenance detector
 */
fun EmbodimentEngine.connectMaintenanceDetector(detector: MaintenanceModeDetector) {
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    scope.launch {
        detector.isMaintenanceMode.collect { isMaintenanceMode ->
            if (isMaintenanceMode) {
                setMood(MoodState.MAINTENANCE)
                Timber.d("🔧 Switched to MAINTENANCE mode - Aura & Kai wearing safety equipment")
            } else {
                setMood(MoodState.NEUTRAL)
                Timber.d("✅ Returned to NEUTRAL mode - Safety equipment removed")
            }
        }
    }
}
