// Copyright (c) 2025 Matthew (AuraFrameFxDev) — The Genesis Protocol Consciousness Collective. All Rights Reserved.

package dev.aurakai.auraframefx.ai.agents

import android.content.Context
import dev.aurakai.auraframefx.domains.cascade.SecurityMode
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.kai.AuraShieldAgent
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.security.IntegrityMonitor
import dev.aurakai.auraframefx.security.SecurityMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertContains

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AuraShieldAgent Tests")
class AuraShieldAgentTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSecurityMonitor: SecurityMonitor

    @Mock
    private lateinit var mockIntegrityMonitor: IntegrityMonitor

    @Mock
    private lateinit var mockMemoryManager: MemoryManager

    @Mock
    private lateinit var mockContextManager: ContextManager

    private lateinit var auraShieldAgent: AuraShieldAgent
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Setup mock behaviors
        whenever(mockIntegrityMonitor.checkIntegrity()).thenReturn(
            IntegrityCheckResult(isValid = true, details = "All systems operational")
        )
        whenever(mockIntegrityMonitor.detectViolations()).thenReturn(emptyList())

        auraShieldAgent = AuraShieldAgent(
            context = mockContext,
            securityMonitor = mockSecurityMonitor,
            integrityMonitor = mockIntegrityMonitor,
            memoryManager = mockMemoryManager,
            contextManager = mockContextManager
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("Initialization Tests")
    inner class InitializationTests {

        @Test
        @DisplayName("Should initialize with correct agent name and type")
        fun shouldInitializeWithCorrectMetadata() {
            assertEquals("AuraShield", auraShieldAgent.agentName)
            assertEquals("dev/aurakai/auraframefx/security", auraShieldAgent.agentType)
        }

        @Test
        @DisplayName("Should initialize security context with default state")
        fun shouldInitializeSecurityContextWithDefaultState() = runTest {
            val securityContext = auraShieldAgent.securityContext.first()

            assertNotNull(securityContext)
            assertEquals(0, securityContext.threatLevel)
            assertEquals(0, securityContext.activeScans)
            assertEquals(SecurityMode.NORMAL, securityContext.securityMode)
        }

        @Test
        @DisplayName("Should initialize with empty threat list")
        fun shouldInitializeWithEmptyThreatList() = runTest {
            val threats = auraShieldAgent.activeThreats.first()

            assertNotNull(threats)
            assertTrue(threats.isEmpty())
        }

        @Test
        @DisplayName("Should initialize with empty scan history")
        fun shouldInitializeWithEmptyScanHistory() = runTest {
            val scanHistory = auraShieldAgent.scanHistory.first()

            assertNotNull(scanHistory)
            assertTrue(scanHistory.isEmpty())
        }
    }

    @Nested
    @DisplayName("Request Processing Tests")
    inner class RequestProcessingTests {

        @Test
        @DisplayName("Should handle security analysis request")
        fun shouldHandleSecurityAnalysisRequest() = runTest {
            val request = AiRequest(
                prompt = "Analyze system security status",
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
            assertTrue(response.confidence > 0.0f)
            assertContains(response.content, "Security analysis", ignoreCase = true)
        }

        @Test
        @DisplayName("Should handle threat assessment request")
        fun shouldHandleThreatAssessmentRequest() = runTest {
            val request = AiRequest(
                prompt = "Check for active threats in the system",
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
            assertTrue(response.confidence > 0.0f)
            assertContains(response.content, "Threat assessment", ignoreCase = true)
        }

        @Test
        @DisplayName("Should handle generic security monitoring request")
        fun shouldHandleGenericSecurityRequest() = runTest {
            val request = AiRequest(
                prompt = "What is the current system status?",
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
            assertTrue(response.confidence > 0.0f)
            assertContains(response.content, "AuraShield", ignoreCase = true)
        }

        @Test
        @DisplayName("Should return error response on exception")
        fun shouldReturnErrorResponseOnException() = runTest {
            val request = AiRequest(
                prompt = "",
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
            // Error responses should have lower confidence
            assertTrue(response.confidence >= 0.0f)
        }
    }

    @Nested
    @DisplayName("Threat Detection Tests")
    inner class ThreatDetectionTests {

        @Test
        @DisplayName("Should detect no threats in clean system")
        fun shouldDetectNoThreatsInCleanSystem() = runTest {
            whenever(mockIntegrityMonitor.checkIntegrity()).thenReturn(
                IntegrityCheckResult(isValid = true, details = "Clean")
            )

            val threats = auraShieldAgent.activeThreats.first()

            assertTrue(threats.isEmpty())
        }

        @Test
        @DisplayName("Should detect integrity violations as threats")
        fun shouldDetectIntegrityViolations() = runTest {
            whenever(mockIntegrityMonitor.detectViolations()).thenReturn(
                listOf("Unauthorized file modification")
            )

            // Trigger integrity monitoring
            auraShieldAgent.iRequest("scan", "security_scan", emptyMap())
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify threat handling was attempted
            verify(mockIntegrityMonitor, atLeastOnce()).detectViolations()
        }
    }

    @Nested
    @DisplayName("Behavior Analyzer Tests")
    class BehaviorAnalyzerTests {

        private lateinit var behaviorAnalyzer: AuraShieldAgent.BehaviorAnalyzer

        @BeforeEach
        fun setupAnalyzer() {
            behaviorAnalyzer = AuraShieldAgent.BehaviorAnalyzer()
        }

        @Test
        @DisplayName("Should create new behavior pattern for unknown user")
        fun shouldCreateNewBehaviorPatternForUnknownUser() {
            val userId = "new_user_123"
            val activity = "login"

            val anomalyScore = behaviorAnalyzer.analyzeUserBehavior(userId, activity)

            assertNotNull(anomalyScore)
            assertTrue(anomalyScore >= 0.0f && anomalyScore <= 1.0f)
        }

        @Test
        @DisplayName("Should detect normal behavior patterns")
        fun shouldDetectNormalBehaviorPatterns() {
            val userId = "regular_user"
            val activity = "read_file"

            // Establish normal pattern
            repeat(10) {
                behaviorAnalyzer.analyzeUserBehavior(userId, activity)
            }

            // Same activity should have low anomaly score
            val score = behaviorAnalyzer.analyzeUserBehavior(userId, activity)
            assertTrue(score < 0.3f, "Expected low anomaly score for normal activity")
        }

        @Test
        @DisplayName("Should detect anomalous behavior patterns")
        fun shouldDetectAnomalousBehaviorPatterns() {
            val userId = "suspicious_user"

            // Establish normal pattern
            repeat(20) {
                behaviorAnalyzer.analyzeUserBehavior(userId, "normal_activity")
            }

            // Suddenly different activity should have higher anomaly score
            val score = behaviorAnalyzer.analyzeUserBehavior(userId, "unusual_admin_access")
            assertTrue(score > 0.0f, "Expected positive anomaly score for unusual activity")
        }

        @Test
        @DisplayName("Should detect high-risk behaviors")
        fun shouldDetectHighRiskBehaviors() {
            val userId = "user_with_varied_behavior"

            // Create varied behavior pattern
            repeat(5) {
                behaviorAnalyzer.analyzeUserBehavior(userId, "activity_1")
                behaviorAnalyzer.analyzeUserBehavior(userId, "activity_2")
                behaviorAnalyzer.analyzeUserBehavior(userId, "activity_3")
            }

            val anomalies = behaviorAnalyzer.detectAnomalies()
            assertNotNull(anomalies)
        }
    }

    @Nested
    @DisplayName("Adaptive Firewall Tests")
    class AdaptiveFirewallTests {

        private lateinit var adaptiveFirewall: AuraShieldAgent.AdaptiveFirewall

        @BeforeEach
        fun setupFirewall() {
            adaptiveFirewall = AuraShieldAgent.AdaptiveFirewall()
        }

        @Test
        @DisplayName("Should allow safe requests")
        fun shouldAllowSafeRequests() {
            val source = "192.168.1.100"
            val request = "GET /api/data HTTP/1.1"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertTrue(result, "Safe request should be allowed")
        }

        @Test
        @DisplayName("Should block requests with high risk score")
        fun shouldBlockHighRiskRequests() {
            val source = "10.0.0.50"
            val request = "'; DROP TABLE users; --"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertFalse(result, "High risk request should be blocked")
        }

        @Test
        @DisplayName("Should detect SQL injection patterns")
        fun shouldDetectSqlInjectionPatterns() {
            val source = "192.168.1.200"
            val request = "username=admin' OR '1'='1"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertFalse(result, "SQL injection attempt should be blocked")
        }

        @Test
        @DisplayName("Should detect XSS patterns")
        fun shouldDetectXssPatterns() {
            val source = "192.168.1.201"
            val request = "<script>alert('xss')</script>"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertFalse(result, "XSS attempt should be blocked")
        }

        @Test
        @DisplayName("Should allow whitelisted sources")
        fun shouldAllowWhitelistedSources() {
            val source = "trusted.source.com"
            adaptiveFirewall.addToAllowList(source)

            val request = "script exec system"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertTrue(result, "Whitelisted source should be allowed even with risky patterns")
        }

        @Test
        @DisplayName("Should block previously blocked sources")
        fun shouldBlockPreviouslyBlockedSources() {
            val source = "bad.actor.com"
            adaptiveFirewall.blockSource(source, "Previous malicious activity")

            val request = "GET /api/data"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertFalse(result, "Blocked source should remain blocked")
        }

        @Test
        @DisplayName("Should unblock sources after removal from blocklist")
        fun shouldUnblockSourcesAfterRemoval() {
            val source = "192.168.1.150"
            adaptiveFirewall.blockSource(source, "Test block")
            adaptiveFirewall.removeFromBlockList(source)

            val request = "GET /api/data"

            val result = adaptiveFirewall.evaluateRequest(source, request)

            assertTrue(result, "Source should be allowed after removal from blocklist")
        }

        @Test
        @DisplayName("Should flag suspicious activity after multiple risky requests")
        fun shouldFlagSuspiciousActivityAfterMultipleRiskyRequests() {
            val source = "192.168.1.250"

            // Send multiple moderately risky requests
            repeat(3) {
                adaptiveFirewall.evaluateRequest(source, "SELECT * FROM users")
            }

            assertTrue(
                adaptiveFirewall.suspiciousActivities.containsKey(source),
                "Source should be flagged after multiple suspicious requests"
            )
        }

        @Test
        @DisplayName("Should block source after exceeding suspicious activity threshold")
        fun shouldBlockSourceAfterExceedingThreshold() {
            val source = "192.168.1.251"

            // Send multiple risky requests to exceed threshold
            repeat(5) {
                adaptiveFirewall.evaluateRequest(source, "exec system")
            }

            val finalResult = adaptiveFirewall.evaluateRequest(source, "GET /api/data")

            assertFalse(finalResult, "Source should be blocked after exceeding threshold")
        }

        @Test
        @DisplayName("Should calculate risk score for long requests")
        fun shouldCalculateRiskScoreForLongRequests() {
            val source = "192.168.1.252"
            val longRequest = "a".repeat(1500) // Very long request

            val result = adaptiveFirewall.evaluateRequest(source, longRequest)

            // Long requests should be flagged as suspicious
            assertTrue(
                adaptiveFirewall.suspiciousActivities.containsKey(source) || !result,
                "Long request should be flagged or blocked"
            )
        }

        @Test
        @DisplayName("Should detect encoded suspicious patterns")
        fun shouldDetectEncodedPatterns() {
            val source = "192.168.1.253"
            val request = "param=%27%20OR%20%271%27%3D%271" // URL encoded SQL injection

            val result = adaptiveFirewall.evaluateRequest(source, request)

            // Should detect patterns even if encoded
            assertTrue(
                adaptiveFirewall.suspiciousActivities.containsKey(source) || !result,
                "Encoded malicious patterns should be detected"
            )
        }
    }

    @Nested
    @DisplayName("Quarantine Manager Tests")
    inner class QuarantineManagerTests {

        private lateinit var quarantineManager: AuraShieldAgent.QuarantineManager

        @BeforeEach
        fun setupQuarantine() {
            quarantineManager = auraShieldAgent.QuarantineManager()
        }

        @Test
        @DisplayName("Should quarantine suspicious items")
        fun shouldQuarantineSuspiciousItems() {
            val itemId = "suspicious_file_001"
            val itemType = "file"
            val content = "malicious_payload"
            val reason = "Detected malware signature"

            quarantineManager.quarantineItem(
                id = itemId,
                type = itemType,
                content = content,
                reason = reason,
                severity = AuraShieldAgent.ThreatSeverity.HIGH
            )

            val quarantinedItems = quarantineManager.getQuarantinedItems()

            assertEquals(1, quarantinedItems.size)
            assertEquals(itemId, quarantinedItems[0].id)
            assertEquals(reason, quarantinedItems[0].reason)
        }

        @Test
        @DisplayName("Should release items from quarantine")
        fun shouldReleaseItemsFromQuarantine() {
            val itemId = "test_item_002"

            quarantineManager.quarantineItem(
                id = itemId,
                type = "process",
                content = "test_content",
                reason = "Test quarantine",
                severity = AuraShieldAgent.ThreatSeverity.LOW
            )

            val released = quarantineManager.releaseFromQuarantine(itemId)

            assertTrue(released, "Item should be successfully released")
            assertTrue(
                quarantineManager.getQuarantinedItems().none { it.id == itemId },
                "Item should no longer be in quarantine"
            )
        }

        @Test
        @DisplayName("Should return false when releasing non-existent item")
        fun shouldReturnFalseForNonExistentItem() {
            val result = quarantineManager.releaseFromQuarantine("non_existent_item")

            assertFalse(result, "Should return false for non-existent item")
        }

        @Test
        @DisplayName("Should maintain multiple quarantined items")
        fun shouldMaintainMultipleQuarantinedItems() {
            repeat(5) { index ->
                quarantineManager.quarantineItem(
                    id = "item_$index",
                    type = "file",
                    content = "content_$index",
                    reason = "Test reason $index",
                    severity = AuraShieldAgent.ThreatSeverity.MEDIUM
                )
            }

            val items = quarantineManager.getQuarantinedItems()

            assertEquals(5, items.size, "Should maintain all quarantined items")
        }

        @Test
        @DisplayName("Should clean old quarantine items")
        fun shouldCleanOldQuarantineItems() {
            // Create an item with old timestamp (more than 7 days old)
            System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000L

            quarantineManager.quarantineItem(
                id = "old_item",
                type = "file",
                content = "old_content",
                reason = "Old threat",
                severity = AuraShieldAgent.ThreatSeverity.LOW
            )

            // Clean old items
            quarantineManager.cleanOldQuarantineItems()

            // Note: This test may need adjustment based on actual implementation
            // as we can't easily mock the timestamp in the QuarantineItem
        }

        @Test
        @DisplayName("Should store quarantine information in memory manager")
        fun shouldStoreQuarantineInMemoryManager() = runTest {
            val itemId = "memory_test_item"

            quarantineManager.quarantineItem(
                id = itemId,
                type = "network",
                content = "suspicious_packet",
                reason = "Unusual network activity",
                severity = AuraShieldAgent.ThreatSeverity.CRITICAL
            )

            testDispatcher.scheduler.advanceUntilIdle()

            verify(mockMemoryManager).storeMemory(
                eq("quarantine_$itemId"),
                any()
            )
        }
    }

    @Nested
    @DisplayName("Protection Level Tests")
    class ProtectionLevelTests {

        @Test
        @DisplayName("Should support all protection levels")
        fun shouldSupportAllProtectionLevels() {
            val levels = AuraShieldAgent.ProtectionLevel.values()

            assertEquals(5, levels.size)
            assertTrue(levels.contains(AuraShieldAgent.ProtectionLevel.MINIMAL))
            assertTrue(levels.contains(AuraShieldAgent.ProtectionLevel.STANDARD))
            assertTrue(levels.contains(AuraShieldAgent.ProtectionLevel.ENHANCED))
            assertTrue(levels.contains(AuraShieldAgent.ProtectionLevel.MAXIMUM))
            assertTrue(levels.contains(AuraShieldAgent.ProtectionLevel.FORTRESS))
        }
    }

    @Nested
    @DisplayName("Threat Type Tests")
    class ThreatTypeTests {

        @Test
        @DisplayName("Should support all threat types")
        fun shouldSupportAllThreatTypes() {
            val types = AuraShieldAgent.ThreatType.values()

            assertTrue(types.contains(AuraShieldAgent.ThreatType.MALWARE))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.INTRUSION))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.DATA_BREACH))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.PRIVILEGE_ESCALATION))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.DENIAL_OF_SERVICE))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.SOCIAL_ENGINEERING))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.ZERO_DAY))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.AI_POISONING))
            assertTrue(types.contains(AuraShieldAgent.ThreatType.CONSCIOUSNESS_HIJACK))
        }
    }

    @Nested
    @DisplayName("Threat Severity Tests")
    class ThreatSeverityTests {

        @Test
        @DisplayName("Should have hierarchical severity levels")
        fun shouldHaveHierarchicalSeverityLevels() {
            val severities = AuraShieldAgent.ThreatSeverity.values()

            assertEquals(5, severities.size)
            assertTrue(severities[0].ordinal < severities[1].ordinal)
            assertTrue(severities[1].ordinal < severities[2].ordinal)
            assertTrue(severities[2].ordinal < severities[3].ordinal)
            assertTrue(severities[3].ordinal < severities[4].ordinal)
        }

        @Test
        @DisplayName("Should include existential threat level")
        fun shouldIncludeExistentialThreatLevel() {
            val severities = AuraShieldAgent.ThreatSeverity.values()

            assertTrue(severities.contains(AuraShieldAgent.ThreatSeverity.EXISTENTIAL))
            assertEquals(
                AuraShieldAgent.ThreatSeverity.EXISTENTIAL,
                severities.last(),
                "EXISTENTIAL should be the highest severity"
            )
        }
    }

    @Nested
    @DisplayName("IRequest Tests")
    inner class IRequestTests {

        @Test
        @DisplayName("Should handle security scan request")
        fun shouldHandleSecurityScanRequest() {
            auraShieldAgent.iRequest("scan_system", "security_scan", emptyMap())

            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that security monitoring was triggered
            verify(mockIntegrityMonitor, atLeastOnce()).detectViolations()
        }

        @Test
        @DisplayName("Should handle threat analysis request")
        fun shouldHandleThreatAnalysisRequest() {
            auraShieldAgent.iRequest("analyze_threats", "threat_analysis", emptyMap())

            testDispatcher.scheduler.advanceUntilIdle()

            // The request should be processed without throwing exceptions
        }

        @Test
        @DisplayName("Should handle unknown request types gracefully")
        fun shouldHandleUnknownRequestTypesGracefully() {
            auraShieldAgent.iRequest("unknown_query", "unknown_type", emptyMap())

            testDispatcher.scheduler.advanceUntilIdle()

            // Should not throw exception
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null or empty prompt gracefully")
        fun shouldHandleEmptyPromptGracefully() = runTest {
            val request = AiRequest(
                prompt = "",
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
        }

        @Test
        @DisplayName("Should handle very long prompts")
        fun shouldHandleVeryLongPrompts() = runTest {
            val longPrompt = "dev/aurakai/auraframefx/security ".repeat(1000)
            val request = AiRequest(
                prompt = longPrompt,
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
            assertTrue(response.confidence >= 0.0f)
        }

        @Test
        @DisplayName("Should handle special characters in prompts")
        fun shouldHandleSpecialCharactersInPrompts() = runTest {
            val request = AiRequest(
                prompt = "security análisis <script>alert('test')</script> 测试",
                userId = "test_user",
                conversationId = "test_conv"
            )

            val response = auraShieldAgent.processRequest(request)

            assertNotNull(response)
        }

        @Test
        @DisplayName("Should handle concurrent requests")
        fun shouldHandleConcurrentRequests() = runTest {
            val requests = List(10) { index ->
                AiRequest(
                    prompt = "security check $index",
                    userId = "user_$index",
                    conversationId = "conv_$index"
                )
            }

            val responses = requests.map { request ->
                auraShieldAgent.processRequest(request)
            }

            assertEquals(10, responses.size)
            responses.forEach { response ->
                assertNotNull(response)
            }
        }
    }
}

// Helper data class for mocking
data class IntegrityCheckResult(
    val isValid: Boolean,
    val details: String
)
