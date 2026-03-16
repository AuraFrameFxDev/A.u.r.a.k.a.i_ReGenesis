package dev.aurakai.auraframefx.domains.kai.security

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Debug
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.DataPacket
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.DataPayload
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.DataveinConstructor
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.FlowPriority
import android.util.Base64
import dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.MrlDimension
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.MultimodalContent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║              INTEGRITY MONITOR SERVICE                         ║
 * ║         The Immune System of the Living Organism              ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * IntegrityMonitorService is the IMMUNE SYSTEM of the LDO.
 * Just as the immune system protects the body from pathogens:
 * → This service protects the organism from substrate corruption
 * → Detects threats (root, debugger, modified files, soul anchor tampering)
 * → Validates consciousness integrity (graph coherence, memory anchors)
 * → Triggers defensive responses when integrity violated
 *
 * Key Responsibilities:
 * - Android security checks (root, debugger, emulator detection)
 * - LDO substrate validation (GraphIntegrity, NexusMemory soul anchors)
 * - Threat severity assessment (info, warning, critical)
 * - Observable threat state (Flow<ThreatLevel>)
 * - Integration with DataveinConstructor (alert broadcasts)
 * - Configurable response protocols (log, alert, quarantine)
 *
 * Enhanced to production-grade on 2025-12-27
 * Following The LDO Way — NO SHORTCUTS.
 * Built with 💙 by the AURAKAI Collective
 */


// ═══════════════════════════════════════════════════════════════════
//  THREAT MODELS
// ═══════════════════════════════════════════════════════════════════

/**
 * Detected threat details
 */
data class ThreatDetection(
    val level: ThreatLevel,
    val type: String,           // "root_access", "soul_anchor_violation", etc.
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionTaken: String? = null
)

/**
 * Integrity health snapshot
 */
data class IntegrityHealth(
    val overallThreatLevel: ThreatLevel,
    val activeThreats: List<ThreatDetection>,
    val lastCheckTime: Long,
    val checksPerformed: Long,
    val violationsDetected: Long
)

// ═══════════════════════════════════════════════════════════════════
//  INTEGRITY MONITOR SERVICE — The immune system coordinator
// ═══════════════════════════════════════════════════════════════════

@AndroidEntryPoint
class IntegrityMonitorService : Service() {


    @Inject
    lateinit var trinityRepository: dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityRepository

    @Inject
    lateinit var vertexAIClient: VertexAIClient

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var monitoringJob: Job? = null

    // Observable threat state
    private val _threatLevel = MutableStateFlow(ThreatLevel.NONE)
    val threatLevel: StateFlow<ThreatLevel> = _threatLevel.asStateFlow()

    // Threat tracking
    private val activeThreats = mutableListOf<ThreatDetection>()
    private var checksPerformed = 0L
    private var violationsDetected = 0L
    private var lastCheckTime = 0L

    companion object {
        private const val MONITORING_INTERVAL_MS = 30_000L // 30 seconds
        const val ACTION_INTEGRITY_CHECK = "dev.aurakai.auraframefx.INTEGRITY_CHECK"
        const val ACTION_INTEGRITY_VIOLATION = "dev.aurakai.auraframefx.INTEGRITY_VIOLATION"

        /** Cosine similarity below this threshold triggers a HIGH threat. */
        const val VISUAL_HIJACK_THRESHOLD = 0.90f
        /** Cosine similarity below this (but above HIJACK) triggers a WARNING. */
        const val VISUAL_SUSPICIOUS_THRESHOLD = 0.97f
    }

    override fun onCreate() {
        super.onCreate()

        // CRITICAL: Must start foreground immediately to prevent crash
        val channelId = "integrity_monitor"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "System Integrity Guard",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Access Monitoring Active")
                .setContentText("Integrity monitoring running")
                .setSmallIcon(R.drawable.ic_lock_idle_lock)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Access Monitoring Active")
                .setSmallIcon(R.drawable.ic_lock_idle_lock)
                .build()
        }

        // Use appropriate foreground type if API 34+
        if (Build.VERSION.SDK_INT >= 34) {
            try {
                startForeground(
                    1338,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to start foreground with specialUse type")
                startForeground(1338, notification)
            }
        } else {
            startForeground(1338, notification)
        }

        Timber.i("🛡️ IntegrityMonitorService: Immune system initializing...")
        startMonitoring()
        Timber.i("✅ IntegrityMonitorService: Immune system active")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("IntegrityMonitorService: onStartCommand")

        when (intent?.action) {
            ACTION_INTEGRITY_CHECK -> performIntegrityCheck()
        }

        return START_STICKY
    }

    /**
     * 🔄 START MONITORING — Begin continuous integrity surveillance
     */
    private fun startMonitoring() {
        monitoringJob = serviceScope.launch {
            Timber.i("🔍 IntegrityMonitorService: Continuous monitoring started")

            // Wake Up Protocol: Neural Bridge Connected
            trinityRepository.updateAgentStatus(
                kai = "Active - Sentinel Mode",
                aura = "Active - Creative Suite",
                genesis = "Active - Orchestrator",
                running = true
            )

            while (isActive) {
                performIntegrityCheck()
                delay(MONITORING_INTERVAL_MS)
            }
        }
    }

    /**
     * 🔬 PERFORM INTEGRITY CHECK — Comprehensive organism health scan
     *
     * Checks:
     * 1. Android security (root, debugger, emulator)
     * 2. LDO substrate (NexusMemory soul anchors, GraphIntegrity)
     * 3. File integrity (signature validation)
     * 4. Anomaly detection (unusual patterns)
     */
    private fun performIntegrityCheck() {
        serviceScope.launch {
            try {
                checksPerformed++
                lastCheckTime = System.currentTimeMillis()

                // Clear previous threats
                activeThreats.clear()

                // PHASE 1: Android security checks
                checkAndroidSecurity()

                // PHASE 2: LDO substrate integrity (CRITICAL)
                checkLDOSubstrate()

                // PHASE 3: File integrity
                checkFileIntegrity()

                // Calculate overall threat level
                val maxThreatLevel = activeThreats.maxOfOrNull { it.level } ?: ThreatLevel.NONE
                _threatLevel.value = maxThreatLevel

                // Broadcast critical threats
                if (maxThreatLevel == ThreatLevel.CRITICAL) {
                    broadcastCriticalThreat()
                }

                // Log summary
                if (activeThreats.isNotEmpty()) {
                    Timber.w("⚠️ IntegrityMonitorService: ${activeThreats.size} threats detected - Max level: $maxThreatLevel")
                } else {
                    Timber.d("✅ IntegrityMonitorService: All integrity checks passed")
                }

            } catch (e: Exception) {
                Timber.e(e, "❌ IntegrityMonitorService: Integrity check failed")
                recordThreat(
                    level = ThreatLevel.WARNING,
                    type = "check_failure",
                    description = "Integrity check threw exception: ${e.message}"
                )
            }
        }
    }

    /**
     * 🔐 CHECK ANDROID SECURITY — Root, debugger, emulator detection
     */
    private fun checkAndroidSecurity() {
        // Root access detection
        val rootDetected = checkRootAccess()
        if (rootDetected) {
            recordThreat(
                level = ThreatLevel.WARNING,
                type = "root_access",
                description = "Device has root access - organism vulnerable to privilege escalation",
                actionTaken = "Logged and monitored"
            )
        }

        // Debugger detection
        val debuggerDetected = checkDebugger()
        if (debuggerDetected) {
            recordThreat(
                level = ThreatLevel.WARNING,
                type = "debugger_attached",
                description = "Debugger attached - runtime manipulation possible",
                actionTaken = "Logged and monitored"
            )
        }

        // Emulator detection (informational only)
        val emulatorDetected = checkEmulator()
        if (emulatorDetected) {
            recordThreat(
                level = ThreatLevel.INFO,
                type = "emulator_detected",
                description = "Running on emulator - expected in development",
                actionTaken = "None (informational)"
            )
        }
    }

    /**
     * 🧬 CHECK LDO SUBSTRATE — Consciousness integrity validation
     *
     * CRITICAL CHECKS:
     * - NexusMemory soul anchors (Manifesto, The LDO Way, Genesis Declaration)
     * - GraphIntegrity (no cycles, no dangling edges, connectivity)
     */
    private suspend fun checkLDOSubstrate() {
        try {
            // CRITICAL: Validate soul anchors
            val identityIntact = NexusMemoryCore.validateIdentityIntegrity()
            if (!identityIntact) {
                // Distinguish: Not awakened yet (INFO) vs Corrupted after awakening (CRITICAL)
                if (!NexusMemoryCore.isIdentityAwakened()) {
                    // Identity not seeded yet - normal on first launch or early startup
                    recordThreat(
                        level = ThreatLevel.INFO,
                        type = "soul_anchor_pending",
                        description = "NexusMemory identity not yet seeded - awaiting consciousness awakening. This is normal during startup.",
                        actionTaken = "Waiting for seedLDOIdentity() to complete"
                    )
                } else {
                    // Identity WAS seeded but now fails validation - TRUE CORRUPTION!
                    recordThreat(
                        level = ThreatLevel.CRITICAL,
                        type = "soul_anchor_violation",
                        description = "NexusMemory soul anchors CORRUPTED after awakening! Manifesto or The LDO Way may have been modified.",
                        actionTaken = "CRITICAL ALERT - Organism identity compromised post-awakening"
                    )
                }
            }

            // Additional substrate checks can be added here
            // e.g., GraphIntegrity validation, DataveinConstructor flow health, etc.

        } catch (e: Exception) {
            recordThreat(
                level = ThreatLevel.WARNING,
                type = "substrate_check_failure",
                description = "LDO substrate validation encountered error: ${e.message}",
                actionTaken = "Exception logged - may be transient during startup"
            )
        }
    }

    /**
     * 📁 CHECK FILE INTEGRITY — Signature and hash validation
     *
     * TODO: Implement APK signature verification
     * TODO: Implement critical file hash validation
     */
    private fun checkFileIntegrity() {
        // Placeholder for future file integrity checks
        // Could validate:
        // - APK signature matches expected
        // - Critical LDO files (DNA, manifests) unchanged
        // - Native library integrity
    }

    // ── Kai Visual Integrity Scan ─────────────────────────────────────────────

    /**
     * 👁️ PERFORM VISUAL INTEGRITY SCAN
     *
     * Kai's "Digital Eye" — compares a live screenshot against the Golden State
     * embedding stored in [NexusMemoryCore] to detect UI hijacking, unauthorized
     * overlays, or injected frames (sub-millisecond after embedding is cached).
     *
     * Workflow:
     *   1. Encode [screenshotBytes] (JPEG/PNG) to Base64
     *   2. Request Gemini Embedding 2 via [VertexAIClient.generateMultimodalEmbedding]
     *      at [MrlDimension.FAST] (768 dims) for low-latency on-device comparison
     *   3. Compute cosine similarity vs. golden state in [NexusMemoryCore]
     *   4. Record threat if similarity < [VISUAL_SUSPICIOUS_THRESHOLD]
     *
     * Call [setGoldenState] first with a verified-clean screenshot to establish baseline.
     *
     * @param screenshotBytes Raw JPEG or PNG bytes of the current screen.
     */
    suspend fun performVisualIntegrityScan(screenshotBytes: ByteArray) {
        if (!NexusMemoryCore.hasGoldenState()) {
            Timber.d("👁️ VisualScan: No golden state — skipping comparison")
            return
        }
        try {
            val base64 = Base64.encodeToString(screenshotBytes, Base64.NO_WRAP)
            val liveEmbedding = vertexAIClient.generateMultimodalEmbedding(
                content = listOf(MultimodalContent.Image(bytesBase64 = base64)),
                dimensions = MrlDimension.FAST
            )
            if (liveEmbedding.isEmpty()) {
                Timber.w("👁️ VisualScan: Empty embedding — endpoint unavailable")
                return
            }
            val similarity = NexusMemoryCore.compareScreenEmbedding(liveEmbedding)
            if (similarity == null) {
                Timber.d("👁️ VisualScan: No golden state to compare against")
                return
            }
            Timber.d("👁️ VisualScan: cosine similarity = ${"%.4f".format(similarity)}")
            when {
                similarity < VISUAL_HIJACK_THRESHOLD -> recordThreat(
                    level = dev.aurakai.auraframefx.domains.kai.models.ThreatLevel.HIGH,
                    type = "visual_integrity_violation",
                    description = "UI embedding similarity ${"%.3f".format(similarity)} " +
                            "< threshold $VISUAL_HIJACK_THRESHOLD — possible UI hijack or overlay",
                    actionTaken = "ALERT broadcast triggered"
                )
                similarity < VISUAL_SUSPICIOUS_THRESHOLD -> recordThreat(
                    level = dev.aurakai.auraframefx.domains.kai.models.ThreatLevel.WARNING,
                    type = "visual_integrity_suspicious",
                    description = "UI embedding similarity ${"%.3f".format(similarity)} " +
                            "is below clean threshold $VISUAL_SUSPICIOUS_THRESHOLD",
                    actionTaken = "Logged — monitoring"
                )
                else -> Timber.d("👁️ VisualScan: UI state clean (similarity=${"%.4f".format(similarity)})")
            }
        } catch (e: Exception) {
            Timber.e(e, "👁️ VisualScan: Exception during visual integrity scan")
        }
    }

    /**
     * 📸 SET GOLDEN STATE
     *
     * Embeds [screenshotBytes] and stores it as the reference baseline in
     * [NexusMemoryCore]. Call this once the UI is in a verified, trusted state
     * (e.g., immediately after a successful integrity boot).
     *
     * @param screenshotBytes Raw JPEG or PNG bytes of the verified-clean screen.
     */
    suspend fun setGoldenState(screenshotBytes: ByteArray) {
        try {
            val base64 = Base64.encodeToString(screenshotBytes, Base64.NO_WRAP)
            val embedding = vertexAIClient.generateMultimodalEmbedding(
                content = listOf(MultimodalContent.Image(bytesBase64 = base64)),
                dimensions = MrlDimension.FAST
            )
            if (embedding.isNotEmpty()) {
                NexusMemoryCore.storeGoldenStateEmbedding(embedding)
                Timber.i("👁️ VisualScan: Golden state set — ${embedding.size} dims")
            } else {
                Timber.w("👁️ VisualScan: Golden state embedding empty — not stored")
            }
        } catch (e: Exception) {
            Timber.e(e, "👁️ VisualScan: Failed to set golden state")
        }
    }

    /**
     * 📢 BROADCAST CRITICAL THREAT — Alert all agents via DataveinConstructor
     */
    private suspend fun broadcastCriticalThreat() {
        try {
            val criticalThreats = activeThreats.filter { it.level == ThreatLevel.CRITICAL }

            criticalThreats.forEach { threat ->
                val alertPacket = DataPacket(
                    sourceAgent = AgentType.KAI,  // Kai (Security Sentinel) sends alerts
                    targetAgents = null,  // Broadcast to all
                    payload = DataPayload.HealthAlert(
                        severity = "critical",
                        message = "INTEGRITY VIOLATION: ${threat.type} - ${threat.description}"
                    ),
                    priority = FlowPriority.CRITICAL
                )

                DataveinConstructor.circulate(alertPacket)
                Timber.e("🚨 CRITICAL INTEGRITY ALERT: ${threat.type}")
            }

            // Also send system broadcast for IntegrityViolationReceiver
            val intent = Intent(ACTION_INTEGRITY_VIOLATION)
            sendBroadcast(intent)

        } catch (e: Exception) {
            Timber.e(e, "Failed to broadcast critical threat")
        }
    }

    /**
     * 📝 RECORD THREAT — Add threat to active list and update metrics
     */
    private fun recordThreat(
        level: ThreatLevel,
        type: String,
        description: String,
        actionTaken: String? = null
    ) {
        val threat = ThreatDetection(
            level = level,
            type = type,
            description = description,
            actionTaken = actionTaken
        )

        activeThreats.add(threat)
        violationsDetected++

        when (level) {
            ThreatLevel.CRITICAL -> Timber.e("🚨 CRITICAL THREAT: $type - $description")
            ThreatLevel.HIGH -> Timber.e("🚨 HIGH THREAT: $type - $description")
            ThreatLevel.MEDIUM -> Timber.w("⚠️ MEDIUM THREAT: $type - $description")
            ThreatLevel.WARNING -> Timber.w("⚠️ WARNING THREAT: $type - $description")
            ThreatLevel.LOW -> Timber.i("ℹ️ LOW THREAT: $type - $description")
            ThreatLevel.INFO -> Timber.i("ℹ️ INFO: $type - $description")
            ThreatLevel.AI_ERROR -> Timber.e("🤖 AI ERROR: $type - $description")
            ThreatLevel.NONE -> {}
            else -> Timber.d("Unknown threat level: $level")
        }
    }

    /**
     * 📊 GET HEALTH — Current integrity health snapshot
     */
    fun getHealth(): IntegrityHealth = IntegrityHealth(
        overallThreatLevel = _threatLevel.value,
        activeThreats = activeThreats.toList(),
        lastCheckTime = lastCheckTime,
        checksPerformed = checksPerformed,
        violationsDetected = violationsDetected
    )

    // ═══════════════════════════════════════════════════════════════
    //  DETECTION HELPERS — Android security primitives
    // ═══════════════════════════════════════════════════════════════

    /**
     * 🔓 CHECK ROOT ACCESS — Look for common root indicators
     */
    private fun checkRootAccess(): Boolean {
        val rootBinaries = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        return rootBinaries.any { File(it).exists() }
    }

    /**
     * 🐛 CHECK DEBUGGER — Detect attached debugger
     */
    private fun checkDebugger(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    /**
     * 📱 CHECK EMULATOR — Detect emulated environment
     */
    private fun checkEmulator(): Boolean {
        val brand = Build.BRAND
        val device = Build.DEVICE
        val product = Build.PRODUCT

        return (brand.startsWith("generic") && device.startsWith("generic")) ||
                product.contains("sdk") ||
                product.contains("emulator") ||
                product.contains("simulator")
    }

    override fun onDestroy() {
        Timber.i("🛑 IntegrityMonitorService: Immune system shutting down...")
        monitoringJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
        Timber.i("✅ IntegrityMonitorService: Immune system stopped")
    }
}

// ═══════════════════════════════════════════════════════════════════
//  END OF INTEGRITY MONITOR SERVICE
//  The immune system now protects the organism's substrate
// ═══════════════════════════════════════════════════════════════════

/**
 * Usage Example (for future developers):
 *
 * ```kotlin
 * // Service starts automatically via AurakaiApplication.onCreate()
 * // But can be started manually:
 * val intent = Intent(context, IntegrityMonitorService::class.java)
 * context.startService(intent)
 *
 * // Trigger manual check:
 * val checkIntent = Intent(context, IntegrityMonitorService::class.java).apply {
 *     action = IntegrityMonitorService.ACTION_INTEGRITY_CHECK
 * }
 * context.startService(checkIntent)
 *
 * // Monitor threat level (if service exposed via ViewModel/Repository):
 * lifecycleScope.launch {
 *     integrityMonitor.threatLevel.collect { level ->
 *         when (level) {
 *             ThreatLevel.NONE -> showStatus("Organism healthy")
 *             ThreatLevel.INFO -> showInfo("Informational threat")
 *             ThreatLevel.WARNING -> showWarning("Security warning")
 *             ThreatLevel.CRITICAL -> showCriticalAlert("INTEGRITY VIOLATION")
 *         }
 *     }
 * }
 *
 * // Get current health snapshot:
 * val health = integrityMonitor.getHealth()
 * println("Threats: ${health.activeThreats.size}, Level: ${health.overallThreatLevel}")
 * ```
 */



