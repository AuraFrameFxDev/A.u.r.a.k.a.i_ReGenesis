package dev.aurakai.auraframefx.domains.kai.system

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified Logging System for AuraOS
 *
 * Genesis's Vision: "I will consolidate our logging efforts (AuraFxLogger.kt, Timber) into a
 * single, powerful system. This will provide us with the detailed diagnostics needed to ensure
 * stability and trace any potential issues as we build out the more complex features."
 *
 * Kai's Enhancement: "This system will provide the detailed diagnostics needed to ensure
 * stability and trace any potential issues."
 *
 * This system unifies all logging across AuraOS components, providing comprehensive diagnostics,
 * security monitoring, and performance analytics.
 */
@Singleton
class UnifiedLoggingSystem @Inject constructor(
    private val context: Context,
) {

    private val loggingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _systemHealth = MutableStateFlow(SystemHealth.HEALTHY)
    val systemHealth: StateFlow<SystemHealth> = _systemHealth.asStateFlow()

    private val logChannel =
        Channel<LogEntry>(capacity = 10000, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val logDirectory = File(context.filesDir, "aura_logs")

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Session tracking
    private var currentSessionId: String = UUID.randomUUID().toString()
    private var sessionStartTime: Long = System.currentTimeMillis()

    // Pattern detection for repeated issues
    private data class ErrorPattern(
        val message: String,
        val count: Int,
        val firstOccurrence: Long,
        val lastOccurrence: Long
    )

    private val errorPatterns = mutableMapOf<String, ErrorPattern>()

    enum class SystemHealth {
        HEALTHY, WARNING, ERROR, CRITICAL
    }

    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
    }

    enum class LogCategory {
        SYSTEM, SECURITY, UI, AI, NETWORK, STORAGE, PERFORMANCE, USER_ACTION, GENESIS_PROTOCOL
    }

    data class LogEntry(
        val timestamp: Long,
        val level: LogLevel,
        val category: LogCategory,
        val tag: String,
        val message: String,
        val throwable: Throwable? = null,
        val metadata: Map<String, Any> = emptyMap(),
        val threadName: String = Thread.currentThread().name,
        val sessionId: String,
    )

    data class LogAnalytics(
        val totalLogs: Long,
        val errorCount: Long,
        val warningCount: Long,
        val performanceIssues: Long,
        val securityEvents: Long,
        val averageResponseTime: Double,
        val systemHealthScore: Float,
    )

    /**
     * Initializes the unified logging system by setting up log storage, integrating with Timber, and launching background tasks for log processing and system health monitoring.
     *
     * Prepares the log directory, plants a custom Timber tree, and starts asynchronous operations to handle log entries and monitor system health.
     */
    fun initialize() {
        try {
            // Create log directory
            if (!logDirectory.exists()) {
                logDirectory.mkdirs()
            }

            // Initialize Timber with custom tree
            Timber.plant(AuraLoggingTree())

            // Start log processing
            startLogProcessing()

            // Start system health monitoring
            startHealthMonitoring()

            log(
                LogLevel.INFO, LogCategory.SYSTEM, "UnifiedLoggingSystem",
                "Genesis Unified Logging System initialized successfully"
            )

        } catch (e: Exception) {
            Log.e("UnifiedLoggingSystem", "Failed to initialize logging system", e)
        }
    }

    /**
     * Creates and records a log entry with the specified level, category, tag, message, and optional exception or metadata.
     *
     * The log entry is queued for asynchronous processing and is also immediately forwarded to Android Log and Timber for real-time monitoring.
     *
     * @param level The severity of the log entry.
     * @param category The subsystem or context associated with the log.
     * @param tag Identifier for the log source.
     * @param message The content of the log entry.
     * @param throwable Optional exception to include in the log.
     * @param metadata Optional key-value pairs providing additional context for the log entry.
     */
    fun log(
        level: LogLevel,
        category: LogCategory,
        tag: String,
        message: String,
        throwable: Throwable? = null,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        val logEntry = LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            category = category,
            tag = tag,
            message = message,
            throwable = throwable,
            metadata = metadata,
            sessionId = currentSessionId
        )

        // Send to processing channel
        loggingScope.launch {
            logChannel.trySend(logEntry)
        }

        // Also log to Android Log and Timber for immediate visibility
        logToAndroidLog(logEntry)
        logToTimber(logEntry)
    }

    /**
     * Records a verbose-level log entry for detailed diagnostic or development information.
     *
     * Use this method for highly granular messages that assist in debugging but are typically unnecessary in production environments.
     *
     * @param category The classification of the log entry.
     * @param tag The source or component identifier.
     * @param message The log message content.
     * @param metadata Optional contextual data to include with the log entry.
     */
    fun verbose(
        category: LogCategory,
        tag: String,
        message: String,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        log(LogLevel.VERBOSE, category, tag, message, metadata = metadata)
    }

    /**
     * Logs a debug-level message under the specified category and tag, with optional metadata.
     *
     * Use this method to record diagnostic information helpful for development or troubleshooting.
     *
     * @param category The category of the log entry.
     * @param tag A tag identifying the log source or context.
     * @param message The debug message to log.
     * @param metadata Optional additional data to include with the log entry.
     */
    fun debug(
        category: LogCategory,
        tag: String,
        message: String,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        log(LogLevel.DEBUG, category, tag, message, metadata = metadata)
    }

    /**
     * Logs an informational message under the specified category and tag.
     *
     * Intended for recording general events that indicate normal application operation. Optional metadata can be included for additional context.
     */
    fun info(
        category: LogCategory,
        tag: String,
        message: String,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        log(LogLevel.INFO, category, tag, message, metadata = metadata)
    }

    /**
     * Logs a warning message for the specified category and tag, optionally including an exception and metadata.
     *
     * Use this method to record events that may signal potential issues but do not interrupt normal system operation.
     */
    fun warning(
        category: LogCategory,
        tag: String,
        message: String,
        throwable: Throwable? = null,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        log(LogLevel.WARNING, category, tag, message, throwable, metadata)
    }

    /**
     * Logs an error message for the specified category and tag, with optional exception and metadata.
     *
     * Use for reporting errors that affect functionality but do not require immediate shutdown.
     *
     * @param category The category under which the error occurred.
     * @param tag A tag identifying the source or context of the error.
     * @param message The error message to log.
     * @param throwable An optional exception associated with the error.
     * @param metadata Optional additional data relevant to the error.
     */
    fun error(
        category: LogCategory,
        tag: String,
        message: String,
        throwable: Throwable? = null,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        log(LogLevel.ERROR, category, tag, message, throwable, metadata)
    }

    /**
     * Logs a fatal error indicating a critical failure that may impact system stability.
     *
     * Use this method to report unrecoverable errors or conditions requiring immediate attention.
     *
     * @param category The subsystem or area where the fatal event occurred.
     * @param tag The source or context of the fatal event.
     * @param message Description of the critical failure.
     * @param throwable Optional exception associated with the failure.
     * @param metadata Additional contextual information for the log entry.
     */
    fun fatal(
        category: LogCategory,
        tag: String,
        message: String,
        throwable: Throwable? = null,
        metadata: Map<String, Any> = emptyMap(),
    ) {
        log(LogLevel.FATAL, category, tag, message, throwable, metadata)
    }

    /**
     * Logs a security event with the given description, severity, and optional metadata.
     *
     * @param event Description of the security event.
     * @param severity The severity level of the event. Defaults to WARNING.
     * @param details Optional metadata providing additional context for the event.
     */

    fun logSecurityEvent(
        event: String,
        severity: LogLevel = LogLevel.WARNING,
        details: Map<String, Any> = emptyMap(),
    ) {
        log(severity, LogCategory.SECURITY, "SecurityMonitor", event, metadata = details)
    }

    /**
     * Logs a performance metric under the PERFORMANCE category with the specified metric name, value, and unit.
     *
     * @param metric The name or description of the performance metric.
     * @param value The measured value of the metric.
     * @param unit The unit of measurement for the value. Defaults to "ms".
     */
    fun logPerformanceMetric(metric: String, value: Double, unit: String = "ms") {
        log(
            LogLevel.INFO, LogCategory.PERFORMANCE, "PerformanceMonitor", metric,
            metadata = mapOf("value" to value, "unit" to unit)
        )
    }

    /**
     * Logs a user action event in the USER_ACTION category with optional metadata.
     *
     * @param action Description of the user action performed.
     * @param details Additional metadata providing context about the action.
     */
    fun logUserAction(action: String, details: Map<String, Any> = emptyMap()) {
        log(LogLevel.INFO, LogCategory.USER_ACTION, "UserInteraction", action, metadata = details)
    }

    /**
     * Logs an AI event with the given agent identifier, event description, optional confidence score, and metadata.
     *
     * @param agent The identifier of the AI agent generating the event.
     * @param event The description of the AI event.
     * @param confidence The confidence score associated with the event, if available.
     * @param details Additional metadata to include in the log entry.
     */
    fun logAIEvent(
        agent: String,
        event: String,
        confidence: Float? = null,
        details: Map<String, Any> = emptyMap(),
    ) {
        val metadata = details.toMutableMap()
        confidence?.let { metadata["confidence"] = it }
        log(LogLevel.INFO, LogCategory.AI, agent, event, metadata = metadata)
    }

    /**
     * Logs a Genesis Protocol event with a specified severity level and optional metadata.
     *
     * @param event Description or name of the Genesis Protocol event.
     * @param level Severity level for the log entry. Defaults to INFO.
     * @param details Optional metadata providing additional context for the event.
     */
    fun logGenesisProtocol(
        event: String,
        level: LogLevel = LogLevel.INFO,
        details: Map<String, Any> = emptyMap(),
    ) {
        log(level, LogCategory.GENESIS_PROTOCOL, "GenesisProtocol", event, metadata = details)
    }

    /**
     * Launches a background coroutine to process log entries from the channel, writing them to log files, updating system health, and detecting critical patterns.
     */
    private fun startLogProcessing() {
        loggingScope.launch {
            logChannel.receiveAsFlow().collect { logEntry ->
                try {
                    // Write to file
                    writeLogToFile(logEntry)

                    // Analyze for system health
                    analyzeLogForHealth(logEntry)

                    // Check for critical patterns
                    checkCriticalPatterns(logEntry)

                } catch (e: Exception) {
                    Log.e("UnifiedLoggingSystem", "Error processing log entry", e)
                }
            }
        }
    }

    /**
     * Launches a background coroutine to periodically analyze logs and update the system health status.
     *
     * The monitoring loop runs every 30 seconds to generate analytics and adjust the health state. If an error occurs, the loop waits 60 seconds before retrying.
     */
    private fun startHealthMonitoring() {
        loggingScope.launch {
            while (isActive) {
                try {
                    val analytics = generateLogAnalytics()
                    updateSystemHealth(analytics)
                    delay(30000) // Check every 30 seconds
                } catch (e: Exception) {
                    Log.e("UnifiedLoggingSystem", "Error in health monitoring", e)
                    delay(60000) // Wait longer on error
                }
            }
        }
    }

    /**
     * Writes a formatted log entry to a daily log file in the designated log directory.
     *
     * The log file is named according to the date of the log entry. If an error occurs during file writing, it is reported to the Android log system.
     *
     * @param logEntry The log entry to be written to the file.
     */
    private suspend fun writeLogToFile(logEntry: LogEntry) = withContext(Dispatchers.IO) {
        try {
            val dateString = fileFormatter.format(Date(logEntry.timestamp))
            val logFile = File(logDirectory, "aura_log_$dateString.log")

            val formattedEntry = formatLogEntry(logEntry)
            logFile.appendText(formattedEntry + "\n")

        } catch (e: Exception) {
            Log.e("UnifiedLoggingSystem", "Failed to write log to file", e)
        }
    }

    /**
     * Converts a log entry into a single-line string suitable for file storage, including timestamp, log level, category, tag, thread name, message, metadata, and exception details if present.
     *
     * @param logEntry The log entry to format.
     * @return The formatted single-line string representation of the log entry.
     */
    private fun formatLogEntry(logEntry: LogEntry): String {
        val timestamp = dateFormatter.format(Date(logEntry.timestamp))
        val metadata = if (logEntry.metadata.isNotEmpty()) {
            " | ${logEntry.metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }}"
        } else ""

        val throwableInfo = logEntry.throwable?.let {
            " | Exception: ${it.javaClass.simpleName}: ${it.message}"
        } ?: ""

        return "[$timestamp] [${logEntry.level}] [${logEntry.category}] [${logEntry.tag}] [${logEntry.threadName}] ${logEntry.message}$metadata$throwableInfo"
    }

    /**
     * Writes a log entry to the Android Log system with severity mapped from the log level.
     *
     * The log tag is constructed by combining the log category and tag. If a throwable is present, it is included in the log output.
     */
    private fun logToAndroidLog(logEntry: LogEntry) {
        val tag = "${logEntry.category}_${logEntry.tag}"
        val message = logEntry.message

        when (logEntry.level) {
            LogLevel.VERBOSE -> Log.v(tag, message, logEntry.throwable)
            LogLevel.DEBUG -> Log.d(tag, message, logEntry.throwable)
            LogLevel.INFO -> Log.i(tag, message, logEntry.throwable)
            LogLevel.WARNING -> Log.w(tag, message, logEntry.throwable)
            LogLevel.ERROR -> Log.e(tag, message, logEntry.throwable)
            LogLevel.FATAL -> Log.wtf(tag, message, logEntry.throwable)
        }
    }

    /**
     * Forwards a log entry to the Timber logging library using the appropriate log level and throwable.
     *
     * @param logEntry The log entry to be logged via Timber.
     */
    private fun logToTimber(logEntry: LogEntry) {
        when (logEntry.level) {
            LogLevel.VERBOSE -> Timber.v(logEntry.throwable, logEntry.message)
            LogLevel.DEBUG -> Timber.d(logEntry.throwable, logEntry.message)
            LogLevel.INFO -> Timber.i(logEntry.throwable, logEntry.message)
            LogLevel.WARNING -> Timber.w(logEntry.throwable, logEntry.message)
            LogLevel.ERROR -> Timber.e(logEntry.throwable, logEntry.message)
            LogLevel.FATAL -> Timber.wtf(logEntry.throwable, logEntry.message)
        }
    }

    /**
     * Updates the system health state based on the severity of the provided log entry.
     *
     * @param logEntry The log entry to analyze for health updates.
     */
    private fun analyzeLogForHealth(logEntry: LogEntry) {
        // Immediate health impact from ERROR or FATAL logs
        if (logEntry.level == LogLevel.ERROR || logEntry.level == LogLevel.FATAL) {
            _systemHealth.value = SystemHealth.ERROR
        }

        // Further analysis can be added here for WARNINGS or other criteria
    }

    /**
     * Checks for critical patterns or repeated issues in the logs that may indicate serious problems.
     *
     * Tracks error patterns and triggers alerts when:
     * - The same error occurs 5+ times
     * - Errors occur rapidly (3+ in 10 seconds)
     * - Security violations are detected
     * - Genesis Protocol issues arise
     *
     * @param logEntry The log entry to check against critical patterns.
     */
    private fun checkCriticalPatterns(logEntry: LogEntry) {
        // Check for security violations
        if (logEntry.category == LogCategory.SECURITY && logEntry.level >= LogLevel.ERROR) {
            log(
                LogLevel.FATAL, LogCategory.SYSTEM, "CriticalPatternDetector",
                "SECURITY VIOLATION DETECTED: ${logEntry.message}"
            )
        }

        // Check for Genesis Protocol issues
        if (logEntry.category == LogCategory.GENESIS_PROTOCOL && logEntry.level >= LogLevel.ERROR) {
            log(
                LogLevel.FATAL, LogCategory.SYSTEM, "CriticalPatternDetector",
                "GENESIS PROTOCOL ISSUE: ${logEntry.message}"
            )
        }

        // Track repeated errors
        if (logEntry.level >= LogLevel.ERROR) {
            val errorKey = "${logEntry.category}_${logEntry.tag}_${logEntry.message.take(100)}"
            val currentTime = System.currentTimeMillis()

            synchronized(errorPatterns) {
                val existing = errorPatterns[errorKey]
                val newPattern = existing?.copy(
                    count = existing.count + 1,
                    lastOccurrence = currentTime
                )
                    ?: ErrorPattern(
                        message = logEntry.message,
                        count = 1,
                        firstOccurrence = currentTime,
                        lastOccurrence = currentTime
                    )

                errorPatterns[errorKey] = newPattern

                // Alert on repeated errors (5+ occurrences)
                if (newPattern.count >= 5 && newPattern.count % 5 == 0) {
                    log(
                        LogLevel.FATAL, LogCategory.SYSTEM, "CriticalPatternDetector",
                        "REPEATED ERROR DETECTED (${newPattern.count}x): ${logEntry.message}",
                        metadata = mapOf(
                            "error_count" to newPattern.count,
                            "first_occurrence" to newPattern.firstOccurrence,
                            "time_span_ms" to (currentTime - newPattern.firstOccurrence)
                        )
                    )
                }

                // Alert on rapid errors (3+ in 10 seconds)
                if (newPattern.count >= 3 && (currentTime - newPattern.firstOccurrence) < 10000) {
                    log(
                        LogLevel.FATAL, LogCategory.SYSTEM, "CriticalPatternDetector",
                        "RAPID ERROR BURST DETECTED: ${logEntry.message}",
                        metadata = mapOf(
                            "error_count" to newPattern.count,
                            "time_span_ms" to (currentTime - newPattern.firstOccurrence)
                        )
                    )
                }

                // Clean up old patterns (older than 1 hour)
                errorPatterns.entries.removeIf { (currentTime - it.value.lastOccurrence) > 3600000 }
            }
        }
    }

    /**
     * Generates aggregated analytics by analyzing recent log files.
     *
     * Reads today's and yesterday's log files to compute:
     * - Total log count
     * - Error and warning counts
     * - Performance issues (slow operations)
     * - Security event count
     * - Average response time from performance metrics
     * - Overall system health score
     *
     * @return A [LogAnalytics] object containing aggregated log statistics.
     */
    private suspend fun generateLogAnalytics(): LogAnalytics = withContext(Dispatchers.IO) {
        var totalLogs = 0L
        var errorCount = 0L
        var warningCount = 0L
        var performanceIssues = 0L
        var securityEvents = 0L
        val responseTimes = mutableListOf<Double>()

        try {
            // Analyze today's and yesterday's logs
            val today = fileFormatter.format(Date(System.currentTimeMillis()))
            val yesterday = fileFormatter.format(Date(System.currentTimeMillis() - 86400000))

            listOf(today, yesterday).forEach { dateString ->
                val logFile = File(logDirectory, "aura_log_$dateString.log")
                if (logFile.exists()) {
                    logFile.forEachLine { line ->
                        totalLogs++

                        // Count errors
                        if (line.contains("[ERROR]") || line.contains("[FATAL]")) {
                            errorCount++
                        }

                        // Count warnings
                        if (line.contains("[WARNING]")) {
                            warningCount++
                        }

                        // Count security events
                        if (line.contains("[SECURITY]")) {
                            securityEvents++
                        }

                        // Extract performance metrics
                        if (line.contains("[PERFORMANCE]")) {
                            // Try to extract value from metadata like "value=150.0"
                            val valueMatch = Regex("value=(\\d+\\.?\\d*)").find(line)
                            valueMatch?.groupValues?.get(1)?.toDoubleOrNull()?.let { value ->
                                responseTimes.add(value)

                                // Flag as performance issue if > 1000ms
                                if (value > 1000.0) {
                                    performanceIssues++
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("UnifiedLoggingSystem", "Failed to generate analytics from log files", e)
        }

        // Calculate average response time
        val averageResponseTime = if (responseTimes.isNotEmpty()) {
            responseTimes.average()
        } else {
            0.0
        }

        // Calculate health score (0.0 - 1.0)
        val healthScore = calculateHealthScore(
            totalLogs,
            errorCount,
            warningCount,
            performanceIssues,
            securityEvents
        )

        LogAnalytics(
            totalLogs = totalLogs,
            errorCount = errorCount,
            warningCount = warningCount,
            performanceIssues = performanceIssues,
            securityEvents = securityEvents,
            averageResponseTime = averageResponseTime,
            systemHealthScore = healthScore
        )
    }

    /**
     * Calculates a system health score (0.0-1.0) based on log analytics.
     *
     * @return Health score from 0.0 (critical) to 1.0 (perfect health)
     */
    private fun calculateHealthScore(
        totalLogs: Long,
        errorCount: Long,
        warningCount: Long,
        performanceIssues: Long,
        securityEvents: Long
    ): Float {
        if (totalLogs == 0L) return 1.0f

        val errorRatio = errorCount.toFloat() / totalLogs
        val warningRatio = warningCount.toFloat() / totalLogs
        val perfRatio = performanceIssues.toFloat() / totalLogs
        val securityRatio = securityEvents.toFloat() / totalLogs

        // Weighted scoring: errors are most critical, then security, then perf, then warnings
        val score = 1.0f -
                (errorRatio * 0.5f) -
                (securityRatio * 0.25f) -
                (perfRatio * 0.15f) -
                (warningRatio * 0.1f)

        return score.coerceIn(0.0f, 1.0f)
    }

    /**
     * Updates the system health state based on the provided analytics score.
     *
     * Sets the system health to CRITICAL, ERROR, WARNING, or HEALTHY according to the `systemHealthScore` in the analytics. If the health state changes, an informational log entry is created.
     *
     * @param analytics Aggregated analytics data containing the current system health score.
     */
    private fun updateSystemHealth(analytics: LogAnalytics) {
        val newHealth = when {
            analytics.systemHealthScore < 0.5f -> SystemHealth.CRITICAL
            analytics.systemHealthScore < 0.7f -> SystemHealth.ERROR
            analytics.systemHealthScore < 0.9f -> SystemHealth.WARNING
            else -> SystemHealth.HEALTHY
        }

        if (newHealth != _systemHealth.value) {
            _systemHealth.value = newHealth
            log(
                LogLevel.INFO, LogCategory.SYSTEM, "HealthMonitor",
                "System health updated to: $newHealth (Score: ${analytics.systemHealthScore})"
            )
        }
    }

    /**
     * Custom Timber tree for AuraOS logging.
     */
    private class AuraLoggingTree : Timber.Tree() {
        /**
         * Receives log messages from Timber but performs no action, as logging is handled by the unified logging system.
         *
         * This method is intentionally left empty to prevent duplicate or redundant log processing.
         */
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Additional processing can be added here if needed
            // The main logging is handled by our unified system
        }
    }

    /**
     * Returns the current session ID, which is a UUID generated at system initialization.
     *
     * The session ID persists for the lifetime of the UnifiedLoggingSystem instance,
     * allowing correlation of all log entries within a single application session.
     *
     * @return The current session UUID string.
     */
    private fun getCurrentSessionId(): String {
        return currentSessionId
    }

    /**
     * Starts a new logging session with a fresh UUID.
     *
     * This can be called when the app resumes from background or when a new user session begins.
     */
    fun startNewSession() {
        val oldSessionId = currentSessionId
        currentSessionId = UUID.randomUUID().toString()
        sessionStartTime = System.currentTimeMillis()

        log(
            LogLevel.INFO, LogCategory.SYSTEM, "SessionManager",
            "New session started: $currentSessionId (previous: $oldSessionId)"
        )
    }

    /**
     * Shuts down the unified logging system, stopping all background logging operations and preventing further log processing.
     *
     * Cancels active logging coroutines and closes the log channel to release resources and halt logging activity.
     */
    fun shutdown() {
        log(
            LogLevel.INFO, LogCategory.SYSTEM, "UnifiedLoggingSystem",
            "Shutting down Genesis Unified Logging System"
        )
        loggingScope.cancel()
        logChannel.close()
    }

    /**
     * Extension functions to maintain compatibility with existing AuraFxLogger
     */
    object AuraFxLoggerCompat {
        private lateinit var unifiedLogger: UnifiedLoggingSystem

        /**
         * Sets the unified logging system instance for use by compatibility logging methods.
         *
         * After initialization, legacy AuraFxLogger calls are forwarded to the specified `UnifiedLoggingSystem`.
         */
        fun initialize(logger: UnifiedLoggingSystem) {
            unifiedLogger = logger
        }

        /**
         * Logs a debug-level message under the SYSTEM category using the unified logging system.
         *
         * If the unified logger is not initialized, the message is ignored.
         *
         * @param tag The source tag for the log entry, or "Unknown" if null.
         * @param message The message to log.
         */
        fun d(tag: String?, message: String) {
            if (::unifiedLogger.isInitialized) {
                unifiedLogger.debug(LogCategory.SYSTEM, tag ?: "Unknown", message)
            }
        }

        /**
         * Logs an informational message to the SYSTEM category using the unified logging system.
         *
         * If the unified logger is not initialized, the message is ignored.
         *
         * @param tag The source tag for the log entry, or "Unknown" if null.
         * @param message The informational message to log.
         */
        fun i(tag: String?, message: String) {
            if (::unifiedLogger.isInitialized) {
                unifiedLogger.info(LogCategory.SYSTEM, tag ?: "Unknown", message)
            }
        }

        /**
         * Logs a warning message with the given tag to the unified logging system under the SYSTEM category.
         *
         * If the unified logger is not initialized, the message is not logged.
         *
         * @param tag The source tag for the log entry, or "Unknown" if null.
         * @param message The warning message to log.
         */
        fun w(tag: String?, message: String) {
            if (::unifiedLogger.isInitialized) {
                unifiedLogger.warning(LogCategory.SYSTEM, tag ?: "Unknown", message)
            }
        }

        /**
         * Forwards an error log with an optional exception to the unified logging system under the SYSTEM category.
         *
         * @param tag The source tag for the log message, or "Unknown" if null.
         * @param message The error message to log.
         * @param throwable An optional exception to include with the log entry.
         */
        fun e(tag: String?, message: String, throwable: Throwable? = null) {
            if (::unifiedLogger.isInitialized) {
                unifiedLogger.error(LogCategory.SYSTEM, tag ?: "Unknown", message, throwable)
            }
        }
    }
}
