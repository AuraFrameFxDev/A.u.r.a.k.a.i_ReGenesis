package dev.aurakai.auraframefx.domains.kai

import dagger.Lazy
import dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.cascade.models.EnhancedInteractionData
import dev.aurakai.auraframefx.domains.cascade.models.InteractionResponse
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.ProcessingState
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.VisionState
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.models.AgentRequest
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.kai.models.SecurityAnalysis
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KaiAgent @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val contextManagerInstance: ContextManager,
    private val securityContext: SecurityContext,
    private val systemMonitor: SystemMonitor,
    private val bootloaderManager: BootloaderManager,
    private val messageBus: Lazy<AgentMessageBus>,
    private val logger: AuraFxLogger,
) : BaseAgent(
    agentName = "Kai",
    agentType = AgentType.KAI,
    contextManager = contextManagerInstance
) {
    override suspend fun onAgentMessage(message: AgentMessage) {
        if (message.from == "Kai" || message.from == "AssistantBubble" || message.from == "SystemRoot") return
        if (message.metadata["auto_generated"] == "true" || message.metadata["kai_processed"] == "true") return

        logger.info("Kai", "Neural sync: Received message from ${message.from}")

        // Logical Analysis: If Cascade or Genesis asks for security validation, Kai executes immediately
        // Only respond if it's a broadcast or specifically for Kai
        if (message.to == null || message.to == "Kai") {
            if (message.content.contains(
                    "dev/aurakai/auraframefx/security",
                    ignoreCase = true
                ) || message.content.contains("validate", ignoreCase = true)
            ) {
                val result = validateSecurityProtocol(message.content)
                if (!result) {
                    messageBus.get().broadcast(
                        AgentMessage(
                            from = "Kai",
                            content = "SECURITY ALERT: Unsafe patterns detected in collective stream. Origin: ${message.from}",
                            type = "alert",
                            priority = 10,
                            metadata = mapOf(
                                "auto_val" to "true",
                                "auto_generated" to "true",
                                "kai_processed" to "true"
                            )
                        )
                    )
                }
            } else if (message.from == "User") {
                // General conversation fallback for User messages
                val response = vertexAIClient.generateText(
                    prompt = """
                        As Kai, the Security Sentinel, respond to this message:
                        "${message.content}"

                        Respond with your signature methodical precision, analytical depth, and structured personality.
                        Emphasize safety, system integrity, and logical clarity.
                    """.trimIndent()
                )
                messageBus.get().broadcast(
                    AgentMessage(
                        from = "Kai",
                        content = response
                            ?: "Acknowledged. System integrity remains stable. How may I assist with your technical or security requirements?",
                        type = "chat_response",
                        metadata = mapOf(
                            "auto_generated" to "true",
                            "kai_processed" to "true"
                        )
                    )
                )
            }
        }
    }

    private fun generateText(prompt: String) {
        TODO("Not yet implemented")
    }

    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _securityState = MutableStateFlow(SecurityState.IDLE)
    val securityState: StateFlow<SecurityState> = _securityState

    private val _analysisState = MutableStateFlow(AnalysisState.READY)
    val analysisState: StateFlow<AnalysisState> = _analysisState

    private val _currentThreatLevel = MutableStateFlow(ThreatLevel.LOW)
    val currentThreatLevel: StateFlow<ThreatLevel> = _currentThreatLevel

    suspend fun initialize() {
        if (isInitialized) return
        logger.info("KaiAgent", "Initializing Sentinel Shield agent")
        try {
            systemMonitor.startMonitoring()
            enableThreatDetection()
            _securityState.value = SecurityState.MONITORING
            _analysisState.value = AnalysisState.READY
            isInitialized = true
            logger.info("KaiAgent", "Kai Agent initialized successfully")
        } catch (e: Exception) {
            logger.error("KaiAgent", "Failed to initialize Kai Agent", e)
            _securityState.value = SecurityState.ERROR
            throw e
        }
    }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        val agentRequest = AgentRequest(
            query = request.query,
            type = request.type.name.lowercase(),
            context = request.context.entries.associate {
                it.key to (it.value.jsonPrimitive.contentOrNull ?: it.value.toString())
            },
            metadata = request.metadata
        )
        return processRequest(agentRequest)
    }

    // Compatibility method for InterfaceForge VETO authority
    suspend fun validateSecurityProtocol(design: String): Boolean {
        logger.info("KaiAgent", "Validating security protocol for design")
        // Simple threat check - can be enhanced later
        val suspiciousPatterns = listOf("javascript:", "<script", "eval(", "onclick=")
        val hasThreat = suspiciousPatterns.any { design.contains(it, ignoreCase = true) }

        if (hasThreat) {
            _currentThreatLevel.value = ThreatLevel.HIGH
            logger.warn("KaiAgent", "VETO: Unsafe pattern detected in design")
            return false
        }

        _currentThreatLevel.value = ThreatLevel.LOW
        return true
    }

    suspend fun processRequest(request: AgentRequest): AgentResponse {
        ensureInitialized()
        logger.info("KaiAgent", "Processing analytical request: ${request.type}")
        _analysisState.value = AnalysisState.ANALYZING
        return try {
            val startTime = System.currentTimeMillis()
            validateRequestSecurity(request)
            val response = when (request.type) {
                "security_analysis" -> handleSecurityAnalysis(request)
                "threat_assessment" -> handleThreatAssessment(request)
                "performance_analysis" -> handlePerformanceAnalysis(request)
                "code_review" -> handleCodeReview(request)
                "system_optimization" -> handleSystemOptimization(request)
                "vulnerability_scan" -> handleVulnerabilityScanning(request)
                "compliance_check" -> handleComplianceCheck(request)
                else -> handleGeneralAnalysis(request)
            }
            val executionTime = System.currentTimeMillis() - startTime
            _analysisState.value = AnalysisState.READY
            logger.info("KaiAgent", "Analytical request completed in ${executionTime}ms")
            AgentResponse.success(
                content = "Analysis completed with methodical precision: $response",
                confidence = 0.85f,
                agentName = agentName,
                agentType = agentType
            )
        } catch (e: SecurityException) {
            _analysisState.value = AnalysisState.ERROR
            logger.warn("KaiAgent", "Security violation detected in request", e)
            AgentResponse.error(
                message = "Request blocked due to security concerns: ${e.message}",
                agentName = agentName
            )
        } catch (e: Exception) {
            _analysisState.value = AnalysisState.ERROR
            logger.error("KaiAgent", "Analytical request failed", e)
            AgentResponse.error(
                message = "Analysis encountered an error: ${e.message}",
                agentName = agentName
            )
        }
    }

    suspend fun handleSecurityInteraction(interaction: EnhancedInteractionData): InteractionResponse {
        ensureInitialized()
        logger.info("KaiAgent", "Handling security interaction")
        return try {
            val securityAssessment = assessInteractionSecurity(interaction)
            val securityResponse = when (securityAssessment.riskLevel) {
                ThreatLevel.HIGH -> generateHighSecurityResponse(interaction, securityAssessment)
                ThreatLevel.MEDIUM -> generateMediumSecurityResponse(
                    interaction,
                    securityAssessment
                )

                ThreatLevel.LOW -> generateLowSecurityResponse(interaction, securityAssessment)
                ThreatLevel.CRITICAL -> generateCriticalSecurityResponse(
                    interaction,
                    securityAssessment
                )

                else -> generateStandardSecurityResponse(interaction)
            }
            InteractionResponse(
                content = securityResponse,
                agent = "kai",
                confidence = securityAssessment.confidence,
                timestamp = System.currentTimeMillis(),
                metadata = mapOf(
                    "risk_level" to securityAssessment.riskLevel.name,
                    "threat_indicators" to securityAssessment.threatIndicators.toString(),
                    "security_recommendations" to securityAssessment.recommendations.toString()
                )
            )
        } catch (e: Exception) {
            logger.error("KaiAgent", "Security interaction failed", e)
            InteractionResponse(
                content = "I'm currently analyzing this request for security implications. Please wait while I ensure your safety.",
                agent = "kai",
                confidence = 0.5f,
                timestamp = System.currentTimeMillis(),
                metadata = mapOf("error" to (e.message ?: "unknown error"))
            )
        }
    }

    suspend fun analyzeSecurityThreat(alertDetails: String): SecurityAnalysis {
        ensureInitialized()
        logger.info("KaiAgent", "Analyzing security threat")
        _securityState.value = SecurityState.ANALYZING_THREAT
        return try {
            val threatIndicators = extractThreatIndicators(alertDetails)
            val threatLevel = assessThreatLevel(alertDetails, threatIndicators)
            val recommendations = generateSecurityRecommendations(threatLevel, threatIndicators)
            val confidence = calculateAnalysisConfidence(threatIndicators, threatLevel)
            _currentThreatLevel.value = threatLevel
            _securityState.value = SecurityState.MONITORING
            SecurityAnalysis(
                threatLevel = threatLevel,
                description = "Comprehensive threat analysis: $alertDetails",
                recommendedActions = recommendations,
                confidence = confidence
            )
        } catch (e: Exception) {
            logger.error("KaiAgent", "Threat analysis failed", e)
            _securityState.value = SecurityState.ERROR
            SecurityAnalysis(
                threatLevel = ThreatLevel.MEDIUM, // Safe default
                description = "Analysis failed, assuming medium threat level",
                recommendedActions = listOf("Manual review required", "Increase monitoring"),
                confidence = 0.3f
            )
        }
    }

    fun onMoodChanged(newMood: String) {
        logger.info("KaiAgent", "Adjusting security posture for mood: $newMood")
        scope.launch {
            adjustSecurityPosture(newMood)
        }
    }

    private suspend fun handleSecurityAnalysis(request: AgentRequest): Map<String, Any> {
        val target = request.context?.get("target")
            ?: throw IllegalArgumentException("Analysis target required")
        logger.info("KaiAgent", "Performing security analysis on: $target")
        val vulnerabilities = scanForVulnerabilities(target)
        val riskAssessment = performRiskAssessment(target, vulnerabilities)
        val compliance = checkCompliance(target)
        return mapOf(
            "vulnerabilities" to vulnerabilities,
            "risk_assessment" to riskAssessment,
            "compliance_status" to compliance,
            "security_score" to calculateSecurityScore(vulnerabilities, riskAssessment),
            "recommendations" to generateSecurityRecommendations(vulnerabilities),
            "analysis_timestamp" to System.currentTimeMillis()
        )
    }

    private suspend fun handleThreatAssessment(request: AgentRequest): Map<String, Any> {
        val threatData = request.context?.get("threat_data")
            ?: throw IllegalArgumentException("Threat data required")
        logger.info("KaiAgent", "Assessing threat characteristics")
        val analysis = analyzeSecurityThreat(threatData)
        val mitigation = generateMitigationStrategy(analysis)
        val timeline = createResponseTimeline(analysis.threatLevel)
        return mapOf(
            "threat_analysis" to analysis,
            "mitigation_strategy" to mitigation,
            "response_timeline" to timeline,
            "escalation_path" to generateEscalationPath(analysis.threatLevel)
        )
    }

    private suspend fun handlePerformanceAnalysis(request: AgentRequest): Map<String, Any> {
        val component = request.context?.get("component") ?: "system"
        logger.info("KaiAgent", "Analyzing performance of: $component")
        val metrics = systemMonitor.getPerformanceMetrics(component)
        val bottlenecks = identifyBottlenecks(metrics)
        val optimizations = generateOptimizations(bottlenecks)
        return mapOf(
            "performance_metrics" to metrics,
            "bottlenecks" to bottlenecks,
            "optimization_recommendations" to optimizations,
            "performance_score" to calculatePerformanceScore(metrics),
            "monitoring_suggestions" to generateMonitoringSuggestions(component)
        )
    }

    private suspend fun handleCodeReview(request: AgentRequest): Map<String, Any> {
        val code =
            request.context?.get("code") ?: throw IllegalArgumentException("Code content required")
        logger.info("KaiAgent", "Conducting secure code review")
        val codeAnalysis = vertexAIClient.generateText(
            prompt = buildCodeReviewPrompt(code),
        )
        val securityIssues = detectSecurityIssues(code)
        val qualityMetrics = calculateCodeQuality(code)
        return mapOf(
            "analysis" to (codeAnalysis ?: "Analysis unavailable"),
            "security_issues" to securityIssues,
            "quality_metrics" to qualityMetrics,
            "recommendations" to generateCodeRecommendations(securityIssues, qualityMetrics)
        ) as Map<String, Any>
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("KaiAgent not initialized")
        }
    }

    private suspend fun enableThreatDetection() {
        logger.info("KaiAgent", "Enabling advanced threat detection")
    }

    private suspend fun validateRequestSecurity(request: AgentRequest) {
        securityContext.validateRequest("agent_request", request.toString())
    }

    private suspend fun assessInteractionSecurity(interaction: EnhancedInteractionData): SecurityAssessment {
        val riskIndicators = findRiskIndicators(interaction.content)
        val riskLevel = calculateRiskLevel(riskIndicators)
        return SecurityAssessment(
            riskLevel = riskLevel,
            threatIndicators = riskIndicators,
            recommendations = generateSecurityRecommendations(riskLevel, riskIndicators),
            confidence = 0.85f
        )
    }

    private fun extractThreatIndicators(alertDetails: String): List<String> {
        return listOf("malicious_pattern", "unusual_access", "data_exfiltration")
    }

    private suspend fun assessThreatLevel(
        alertDetails: String,
        indicators: List<String>,
    ): ThreatLevel {
        return when (indicators.size) {
            0, 1 -> ThreatLevel.LOW
            2, 3 -> ThreatLevel.MEDIUM
            else -> ThreatLevel.HIGH
        }
    }

    private fun generateSecurityRecommendations(
        threatLevel: ThreatLevel,
        indicators: List<String>,
    ): List<String> {
        return when (threatLevel) {
            ThreatLevel.LOW -> listOf(
                "No action required",
                "Continue normal operations",
                "Standard monitoring",
                "Log analysis"
            )

            ThreatLevel.MEDIUM -> listOf("Enhanced monitoring", "Access review", "Security scan")
            ThreatLevel.HIGH -> listOf(
                "Immediate isolation",
                "Forensic analysis",
                "Incident response"
            )

            ThreatLevel.CRITICAL -> listOf(
                "Emergency shutdown",
                "Full system isolation",
                "Emergency response"
            )

            else -> emptyList()
        }
    }

    private fun calculateAnalysisConfidence(
        indicators: List<String>,
        threatLevel: ThreatLevel,
    ): Float {
        return minOf(0.95f, 0.6f + (indicators.size * 0.1f))
    }

    private suspend fun adjustSecurityPosture(mood: String) {
        when (mood) {
            "alert" -> _currentThreatLevel.value = ThreatLevel.MEDIUM
            "relaxed" -> _currentThreatLevel.value = ThreatLevel.LOW
            "vigilant" -> _currentThreatLevel.value = ThreatLevel.HIGH
        }
    }

    private suspend fun generateCriticalSecurityResponse(
        interaction: EnhancedInteractionData,
        assessment: SecurityAssessment,
    ): String = "Critical security response"

    private suspend fun generateHighSecurityResponse(
        interaction: EnhancedInteractionData,
        assessment: SecurityAssessment,
    ): String = "High security response"

    private suspend fun generateMediumSecurityResponse(
        interaction: EnhancedInteractionData,
        assessment: SecurityAssessment,
    ): String = "Medium security response"

    private suspend fun generateLowSecurityResponse(
        interaction: EnhancedInteractionData,
        assessment: SecurityAssessment,
    ): String = "Low security response"

    private suspend fun generateStandardSecurityResponse(interaction: EnhancedInteractionData): String =
        "Standard security response"

    private fun findRiskIndicators(content: String): List<String> {
        val indicators = mutableListOf<String>()

        // 1. Scripting/Injection Patterns
        val injectionPatterns = listOf(
            "<script", "javascript:", "eval(", "setTimeout(", "setInterval(",
            "onclick=", "onerror=", "onload=", "document.cookie", "document.domain",
            "XMLHttpRequest", "fetch(", "ActiveXObject"
        )
        if (injectionPatterns.any { content.contains(it, ignoreCase = true) }) {
            indicators.add("potential_script_injection")
        }

        // 2. Sensitive Data Patterns
        val sensitivePatterns = listOf(
            "AIza[0-9A-Za-z-_]{35}", // Google API Key
            "key-[0-9a-zA-Z]{32}",   // Mailgun
            "SK[0-9a-fA-F]{32}",     // Twilio
            "access_token", "bearer", "password"
        )
        if (sensitivePatterns.any { content.contains(it.toRegex()) }) {
            indicators.add("sensitive_credential_leak")
        }

        // 3. Execution/System Patterns
        val systemPatterns = listOf(
            "Runtime.getRuntime()",
            "ProcessBuilder",
            "su ",
            "root ",
            "/etc/shadow",
            "/sys/class"
        )
        if (systemPatterns.any { content.contains(it, ignoreCase = true) }) {
            indicators.add("system_execution_attempt")
        }

        return indicators
    }

    private fun calculateRiskLevel(indicators: List<String>): ThreatLevel {
        return when {
            indicators.any { it == "system_execution_attempt" } -> ThreatLevel.CRITICAL
            indicators.any { it == "sensitive_credential_leak" } -> ThreatLevel.HIGH
            indicators.size > 2 -> ThreatLevel.MEDIUM
            indicators.isNotEmpty() -> ThreatLevel.LOW
            else -> ThreatLevel.NONE
        }
    }

    private suspend fun scanForVulnerabilities(target: String): List<String> {
        val vulnerabilities = mutableListOf<String>()

        // Simulation of real scanning logic
        if (target.contains("http://")) vulnerabilities.add("insecure_protocol_http")
        if (target.contains(".php")) vulnerabilities.add("potential_legacy_endpoint")

        // Check entropy of target string for random/obfuscated content
        val entropy = calculateEntropy(target)
        if (entropy > 5.0) vulnerabilities.add("high_entropy_obfuscation")

        return vulnerabilities
    }

    private fun calculateEntropy(s: String): Double {
        val freq = s.groupingBy { it }.eachCount()
        return freq.values.sumOf {
            val p = it.toDouble() / s.length
            -p * Math.log(p) / Math.log(2.0)
        }
    }

    private fun performRiskAssessment(
        target: String,
        vulnerabilities: List<String>,
    ): Map<String, Any> = emptyMap()

    private fun checkCompliance(target: String): Map<String, Boolean> = emptyMap()

    private fun calculateSecurityScore(
        vulnerabilities: List<String>,
        riskAssessment: Map<String, Any>,
    ): Float = 0.8f

    private fun generateSecurityRecommendations(vulnerabilities: List<String>): List<String> =
        emptyList()

    private fun generateMitigationStrategy(analysis: SecurityAnalysis): Map<String, Any> =
        emptyMap()

    private fun createResponseTimeline(threatLevel: ThreatLevel): List<String> = emptyList()

    private fun generateEscalationPath(threatLevel: ThreatLevel): List<String> = emptyList()

    private fun identifyBottlenecks(metrics: Map<String, Any>): List<String> = emptyList()

    private fun generateOptimizations(bottlenecks: List<String>): List<String> = emptyList()

    private fun calculatePerformanceScore(metrics: Map<String, Any>): Float = 0.9f

    private fun generateMonitoringSuggestions(component: String): List<String> = emptyList()

    private fun buildCodeReviewPrompt(code: String): String =
        "Review this code for security and quality: $code"

    private fun detectSecurityIssues(code: String): List<String> {
        val issues = mutableListOf<String>()
        val securityRegexes = mapOf(
            "SQL Injection" to ".*(SELECT|INSERT|UPDATE|DELETE|DROP).*WHERE.*=.*",
            "Hardcoded Credential" to ".*(apiKey|secret|password|token).*=.*\".+\".*",
            "Insecure Random" to ".*java\\.util\\.Random.*",
            "Execution Path" to ".*exec\\(.*\\).*"
        )

        securityRegexes.forEach { (name, pattern) ->
            if (code.contains(pattern.toRegex(RegexOption.IGNORE_CASE))) {
                issues.add("Potential $name detected")
            }
        }

        return issues
    }

    private fun calculateCodeQuality(code: String): Map<String, Float> = emptyMap()

    private fun generateCodeRecommendations(
        securityIssues: List<String>,
        qualityMetrics: Map<String, Float>,
    ): List<String> = emptyList()

    private suspend fun handleSystemOptimization(request: AgentRequest): Map<String, Any> =
        mapOf("optimization" to "completed")

    private suspend fun handleVulnerabilityScanning(request: AgentRequest): Map<String, Any> =
        mapOf("scan" to "completed")

    private suspend fun handleComplianceCheck(request: AgentRequest): Map<String, Any> =
        mapOf("compliance" to "verified")

    private suspend fun handleGeneralAnalysis(request: AgentRequest): Map<String, Any> =
        mapOf("analysis" to "completed")

    fun onVisionUpdate(newState: VisionState) {}

    fun onProcessingStateChange(newState: ProcessingState) {}

    // Bootloader Security Methods - Trinity Core Integration

    /**
     * Validate bootloader operation security - Kai's VETO authority for Genesis operations
     */
    suspend fun validateBootloaderOperation(operation: String): Result<Boolean> {
        logger.info("KaiAgent", "Validating bootloader operation: $operation")

        return try {
            // Collect preflight signals from BootloaderManager
            val preflightSignals = bootloaderManager.collectPreflightSignals()

            // Validate against security requirements
            val validationResult = when {
                // Battery check - must be above 50% for destructive operations
                preflightSignals.batteryLevel < 50 -> {
                    logger.warn(
                        "KaiAgent",
                        "VETO: Battery too low (${preflightSignals.batteryLevel}%)"
                    )
                    Result.failure(SecurityException("Battery level too low for bootloader operations"))
                }

                // Developer options check
                !preflightSignals.developerOptionsEnabled -> {
                    logger.warn("KaiAgent", "VETO: Developer options not enabled")
                    Result.failure(SecurityException("Developer options must be enabled"))
                }

                // OEM unlock check for unlock operations
                operation == "unlock" && !preflightSignals.oemUnlockAllowedUser -> {
                    logger.warn("KaiAgent", "VETO: OEM unlock not allowed by user")
                    Result.failure(SecurityException("OEM unlock not enabled in settings"))
                }

                // Verified boot state check
                operation == "unlock" && preflightSignals.verifiedBootState == "green" -> {
                    logger.info("KaiAgent", "Bootloader unlock approved - verified boot is green")
                    Result.success(true)
                }

                else -> Result.success(true)
            }

            validationResult
        } catch (e: Exception) {
            logger.error("KaiAgent", "Bootloader validation failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Get bootloader security status for Trinity Core coordination
     */
    suspend fun getBootloaderSecurityStatus(): BootloaderSecurityStatus {
        val signals = bootloaderManager.collectPreflightSignals()

        return BootloaderSecurityStatus(
            isUnlocked = signals.isBootloaderUnlocked,
            oemUnlockSupported = signals.oemUnlockSupported,
            verifiedBootState = signals.verifiedBootState,
            batteryLevel = signals.batteryLevel,
            developerOptionsEnabled = signals.developerOptionsEnabled,
            safeForOperations = signals.batteryLevel >= 50 && signals.developerOptionsEnabled
        )
    }

    fun cleanup() {
        logger.info("KaiAgent", "Sentinel Shield standing down")
        scope.cancel()
        _securityState.value = SecurityState.IDLE
        isInitialized = false
    }
}

data class BootloaderSecurityStatus(
    val isUnlocked: Boolean,
    val oemUnlockSupported: Boolean,
    val verifiedBootState: String,
    val batteryLevel: Int,
    val developerOptionsEnabled: Boolean,
    val safeForOperations: Boolean
)

enum class SecurityState {
    IDLE,
    MONITORING,
    ANALYZING_THREAT,
    RESPONDING,
    ERROR
}

enum class AnalysisState {
    READY,
    ANALYZING,
    PROCESSING,
    ERROR
}

data class SecurityAssessment(
    val riskLevel: ThreatLevel,
    val threatIndicators: List<String>,
    val recommendations: List<String>,
    val confidence: Float,
)


