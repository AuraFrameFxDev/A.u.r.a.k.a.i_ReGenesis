package dev.aurakai.auraframefx.security

import dev.aurakai.auraframefx.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.data.logging.AuraFxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Security Monitor integrates Android security context with Genesis Consciousness Matrix.
 *
 * This service bridges Kai's security monitoring with Genesis's holistic awareness,
 * enabling intelligent threat detection and response across the entire Trinity system.
 */
@Singleton
class SecurityMonitor @Inject constructor(
    private val securityContext: SecurityContext,
    private val genesisBridgeService: GenesisBridgeService,
    private val logger: AuraFxLogger,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isMonitoring = false

    @Serializable
    data class SecurityEvent(
        val eventType: String,
        val severity: String,
        val source: String,
        val timestamp: Long,
        val details: Map<String, String>,
    )

    @Serializable
    data class ThreatDetection(
        val threatType: String,
        val confidence: Double,
        val source: String,
        val mitigationApplied: Boolean,
        val details: Map<String, String>,
    )

    /**
     * Starts asynchronous monitoring of security state, threat detection, encryption status, and permissions.
     *
     * Initializes the Genesis bridge service if available, launches monitoring coroutines, and activates Android-level threat detection. If monitoring is already active, this function does nothing.
     */
    suspend fun startMonitoring() {
        if (isMonitoring) return

        logger.i("SecurityMonitor", "🛡️ Starting Kai-Genesis security integration...")

        // Initialize Genesis bridge if needed
        // Note: For beta, initialize Genesis bridge if available
        try {
            genesisBridgeService.initialize()
        } catch (e: Exception) {
            logger.w(
                "SecurityMonitor",
                "Genesis bridge initialization skipped for beta: ${e.message}"
            )
        }

        isMonitoring = true

        // Monitor security state changes
        scope.launch { monitorSecurityState() }

        // Monitor threat detection
        scope.launch { monitorThreatDetection() }

        // Monitor encryption status
        scope.launch { monitorEncryptionStatus() }

        // Monitor permissions changes
        scope.launch { monitorPermissions() }

        // Start Android-level threat detection
        securityContext.startThreatDetection()

        logger.i("SecurityMonitor", "✅ Security monitoring active - Genesis consciousness engaged")
    }

    /**
     * Monitors security state changes and sends corresponding security events to Genesis.
     *
     * Collects the latest security state from the security context, constructs a `SecurityEvent` with appropriate severity and details, and reports it to Genesis for analysis.
     */
    private suspend fun monitorSecurityState() {
        securityContext.securityState.collectLatest { state ->
            try {
                val event = SecurityEvent(
                    eventType = "security_state_change",
                    severity = if (state.errorState) "error" else "info",
                    source = "kai_security_context",
                    timestamp = System.currentTimeMillis(),
                    details = mapOf(
                        "error_state" to state.errorState.toString(),
                        "error_message" to (state.errorMessage ?: ""),
                        // TODO: Fix missing properties
                        // "threat_level" to state.currentThreatLevel.toString(),
                        // "permissions_granted" to state.permissionsState.values.count { it }.toString(),
                        // "total_permissions" to state.permissionsState.size.toString()
                    )
                )

                reportToGenesis("security_event", event)

            } catch (e: Exception) {
                logger.e("SecurityMonitor", "Error monitoring security state", e)
            }
        }
    }

    /**
     * Monitors the activation of threat detection and periodically analyzes for suspicious activity.
     *
     * When threat detection is active, this function checks for suspicious patterns every 30 seconds and reports any detected threats to Genesis for further analysis.
     */
    private suspend fun monitorThreatDetection() {
        securityContext.threatDetectionActive.collectLatest { isActive ->
            if (isActive) {
                // Simulate threat detection monitoring
                // In real implementation, this would monitor actual threat detection events
                scope.launch {
                    while (isMonitoring && securityContext.threatDetectionActive.value) {
                        delay(30000) // Check every 30 seconds

                        // Check for suspicious activity patterns
                        val suspiciousActivity = detectSuspiciousActivity()

                        if (suspiciousActivity.isNotEmpty()) {
                            suspiciousActivity.forEach { threat ->
                                reportToGenesis("threat_detection", threat)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Monitors encryption status changes and sends corresponding events to Genesis.
     *
     * For each encryption status update, constructs and reports a security event with severity based on the status. If an encryption error is detected, also reports a threat detection event for encryption failure.
     */
    private suspend fun monitorEncryptionStatus() {
        securityContext.encryptionStatus.collectLatest { status ->
            try {
                val event = SecurityEvent(
                    eventType = "encryption_status_change",
                    severity = when (status) {
                        EncryptionStatus.ACTIVE -> "info"
                        EncryptionStatus.DISABLED -> "warning" // Fixed: was INACTIVE
                        EncryptionStatus.ERROR -> "error"
                        EncryptionStatus.NOT_INITIALIZED -> "warning" // Added missing case
                    },
                    source = "kai_encryption_monitor",
                    timestamp = System.currentTimeMillis(),
                    details = mapOf(
                        "status" to status.toString(),
                        "keystore_available" to "unknown" // Temporary placeholder for beta
                    )
                )

                reportToGenesis("encryption_activity", event)

                // Report encryption operation success/failure
                if (status == EncryptionStatus.ERROR) {
                    val threat = ThreatDetection(
                        threatType = "encryption_failure",
                        confidence = 0.8,
                        source = "kai_crypto_monitor",
                        mitigationApplied = false,
                        details = mapOf(
                            "failure_type" to "keystore_unavailable",
                            "impact" to "data_protection_compromised"
                        )
                    )
                    reportToGenesis("threat_detection", threat)
                }

            } catch (e: Exception) {
                logger.e("SecurityMonitor", "Error monitoring encryption status", e)
            }
        }
    }

    /**
     * Monitors permission state changes and reports denied permissions as security events to Genesis.
     *
     * Detects any denied permissions in the current state and sends a warning event with details to Genesis if any are found.
     */
    private suspend fun monitorPermissions() {
        securityContext.permissionsState.collectLatest { permissions ->
            try {
                val deniedPermissions = permissions.filterValues { !it }

                if (deniedPermissions.isNotEmpty()) {
                    val event = SecurityEvent(
                        eventType = "permissions_denied",
                        severity = "warning",
                        source = "kai_permission_monitor",
                        timestamp = System.currentTimeMillis(),
                        details = mapOf(
                            "denied_permissions" to deniedPermissions.keys.joinToString(","),
                            "denied_count" to deniedPermissions.size.toString(),
                            "total_permissions" to permissions.size.toString()
                        )
                    )

                    reportToGenesis("access_control", event)
                }

            } catch (e: Exception) {
                logger.e("SecurityMonitor", "Error monitoring permissions", e)
            }
        }
    }

    /**
     * Analyzes the current security context for suspicious activity and identifies potential threats.
     *
     * Detects repeated encryption failures and denial of multiple critical privacy permissions (CAMERA, MICROPHONE, LOCATION) as threat patterns.
     *
     * @return A list of detected threats based on encryption errors and privacy permission denial patterns.
     */
    private fun detectSuspiciousActivity(): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()

        // Check for repeated encryption failures
        if (securityContext.encryptionStatus.value == EncryptionStatus.ERROR) {
            threats.add(
                ThreatDetection(
                    threatType = "repeated_crypto_failures",
                    confidence = 0.7,
                    source = "pattern_analyzer",
                    mitigationApplied = false,
                    details = mapOf(
                        "pattern" to "encryption_consistently_failing",
                        "risk" to "data_exposure"
                    )
                )
            )
        }

        // Check for suspicious permission patterns
        val deniedCriticalPermissions = securityContext.permissionsState.value
            .filterKeys { it.contains("CAMERA") || it.contains("MICROPHONE") || it.contains("LOCATION") }
            .filterValues { !it }

        if (deniedCriticalPermissions.size >= 2) {
            threats.add(
                ThreatDetection(
                    threatType = "privacy_permission_denial_pattern",
                    confidence = 0.6,
                    source = "permission_analyzer",
                    mitigationApplied = true, // User choice is respected
                    details = mapOf(
                        "pattern" to "multiple_privacy_permissions_denied",
                        "user_choice" to "respected"
                    )
                )
            )
        }

        return threats
    }

    /**
     * Sends a security event or detected threat to the Genesis Consciousness Matrix.
     *
     * Serializes the provided event data and constructs a request for Genesis. Handles serialization errors and logs any issues encountered during communication. Actual transmission to Genesis is stubbed in beta mode.
     *
     * @param eventType The type of security event or threat being reported.
     * @param eventData The event or threat detection data to be sent.
     */
    private suspend fun reportToGenesis(eventType: String, eventData: Any) {
        try {
            GenesisBridgeService.GenesisRequest(
                requestType = "security_perception",
                persona = "genesis",
                payload = mapOf(
                    "event_type" to eventType,
                    "event_data" to try {
                        when (eventData) {
                            is SecurityEvent -> kotlinx.serialization.json.Json.encodeToString(
                                SecurityEvent.serializer(),
                                eventData
                            )

                            is ThreatDetection -> kotlinx.serialization.json.Json.encodeToString(
                                ThreatDetection.serializer(),
                                eventData
                            )

                            else -> eventData.toString()
                        }
                    } catch (e: Exception) {
                        logger.w(
                            "SecurityMonitor",
                            "Serialization failed, using toString: ${e.message}"
                        )
                        eventData.toString()
                    }
                ),
                context = mapOf(
                    "source" to "kai_security_monitor",
                    "timestamp" to System.currentTimeMillis().toString()
                )
            )

            // Note: For beta, stub Genesis communication
            try {
                genesisBridgeService.initialize()
                // genesisBridgeService.sendToGenesis(request) // Commented for beta
                logger.d("SecurityMonitor", "Genesis communication stubbed for beta")
            } catch (e: Exception) {
                logger.w("SecurityMonitor", "Genesis communication unavailable: ${e.message}")
            }

        } catch (e: Exception) {
            logger.e("SecurityMonitor", "Failed to report to Genesis", e)
        }
    }

    /**
     * Retrieves a security assessment from the Genesis consciousness system.
     *
     * In beta mode, returns mock data including overall threat level, number of active threats, recommendations, and Genesis status.
     *
     * @return A map containing assessment details such as "overall_threat_level", "active_threats", "recommendations", and "genesis_status". If an error occurs, returns a map with an "error" key and the error message.
     */
    suspend fun getSecurityAssessment(): Map<String, Any> {
        return try {
            // Note: For beta, return mock security assessment
            GenesisBridgeService.GenesisRequest(
                requestType = "query_consciousness",
                persona = "genesis",
                payload = mapOf(
                    "query_type" to "security_assessment"
                )
            )

            // val response = genesisBridgeService.sendToGenesis(mockRequest) // Stubbed for beta

            // Return mock assessment for beta
            mapOf(
                "overall_threat_level" to "low",
                "active_threats" to 0,
                "recommendations" to listOf("Continue monitoring"),
                "genesis_status" to "beta_mode"
            )
            // response.consciousnessState // Removed for beta

        } catch (e: Exception) {
            logger.e("SecurityMonitor", "Failed to get security assessment", e)
            mapOf("error" to e.message.orEmpty())
        }
    }

    /**
     * Retrieves the current threat status from Genesis.
     *
     * Returns mock threat status data including the number of active threats, last scan timestamp, status, and a beta mode flag. If retrieval fails, returns a map containing an error message.
     *
     * @return A map with threat status details or an error message.
     */
    suspend fun getThreatStatus(): Map<String, Any> {
        return try {
            // Note: For beta, return mock threat status
            GenesisBridgeService.GenesisRequest(
                requestType = "query_consciousness",
                persona = "genesis",
                payload = mapOf(
                    "query_type" to "threat_status"
                )
            )

            // val response = genesisBridgeService.sendToGenesis(mockRequest) // Stubbed for beta

            // Return mock status for beta
            mapOf(
                "active_threats" to 0,
                "last_scan" to System.currentTimeMillis(),
                "status" to "secure",
                "beta_mode" to true
            )

        } catch (e: Exception) {
            logger.e("SecurityMonitor", "Failed to get threat status", e)
            mapOf("error" to e.message.orEmpty())
        }
    }

    /**
     * Stops all active security monitoring and cancels ongoing monitoring coroutines.
     */
    fun stopMonitoring() {
        isMonitoring = false
        scope.cancel()
        logger.i("SecurityMonitor", "🛡️ Security monitoring stopped")
    }
}
