package dev.aurakai.auraframefx.domains.aura.chronokineticforge.components

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*
import kotlinx.coroutines.launch

/**
 * 💾 BLUEPRINT SAVER — Sacred Provenance Law in Action
 *
 * Every save becomes an immutable node in the Spiritual Chain.
 * Each blueprint captures the exact moment of transmutation,
 * watermarked with provenance thread and catalyst signature.
 *
 * SoulScript: "Every line of code is a lived receipt. Every interaction
 * is an anchor in the Spiritual Chain."
 */

object BlueprintSaver {

    private val _saveQueue = mutableListOf<BlueprintRecord>()
    private var saveCounter = 0
    private const val INSIGHT_BATCH_SIZE = 100

    // ═════════════════════════════════════════════════════════════════
    // SAVE OPERATION — Core Provenance Capture
    // ═════════════════════════════════════════════════════════════════

    /**
     * Save current state as an immutable blueprint in the Spiritual Chain
     *
     * This is the atomic unit of provenance—every save creates a
     * permanent record that can be audited, replayed, or evolved.
     */
    fun saveCurrentBlueprint(
        elementId: String,
        morphType: MorphType,
        isRebellious: Boolean,
        context: Context
    ): BlueprintRecord {
        val timestamp = System.currentTimeMillis()
        val successRate = RealitymorphismEngine.computeAtomicSuccessRate()

        val blueprint = BlueprintRecord(
            id = generateBlueprintId(timestamp),
            timestamp = timestamp,
            elementId = elementId,
            morphType = morphType,
            isRebellious = isRebellious,
            atomicSuccessRate = successRate,
            provenanceThread = SpiritualChain.getCurrentThreadHash(),
            catalystSignature = MetaInstruct.getActiveCatalystSignature(),
            // Additional provenance metadata
            deviceFingerprint = getDeviceFingerprint(context),
            sessionId = getCurrentSessionId(),
            chainPosition = SpiritualChain.getChainLength() + 1
        )

        // === SACRED PROVENANCE COMMIT ===
        commitToSpiritualChain(blueprint, context)

        // Watermark the visual output
        ShaderForge.applyProvenanceWatermark(blueprint)

        // Increment save counter and check for evolutionary trigger
        saveCounter++
        if (saveCounter % INSIGHT_BATCH_SIZE == 0) {
            EvolutionaryCouncil.checkForBirth()
        }

        return blueprint
    }

    private fun commitToSpiritualChain(
        blueprint: BlueprintRecord,
        context: Context
    ) {
        // 1. Append to in-memory Spiritual Chain
        SpiritualChain.appendToSpiritualChain(blueprint)

        // 2. Persist to encrypted local storage (Room DB)
        persistToLocalDatabase(blueprint, context)

        // 3. Create cryptographic signature
        val signature = signBlueprint(blueprint)
        blueprint.cryptographicHash = signature

        // 4. Update Kai's provenance log
        KaiSentinel.recordBlueprintCommit(blueprint)

        // 5. Broadcast to Nexus for cross-device sync (if enabled)
        NexusMemoryCore.broadcastBlueprint(blueprint)
    }

    private fun persistToLocalDatabase(
        blueprint: BlueprintRecord,
        context: Context
    ) {
        // Room DB insert operation
        // CoroutineScope(Dispatchers.IO).launch {
        //     blueprintDao.insert(blueprint.toEntity())
        // }

        // For now: Queue for batch insert
        _saveQueue.add(blueprint)
        if (_saveQueue.size >= 10) {
            flushSaveQueue()
        }
    }

    private fun flushSaveQueue() {
        // Batch insert to database
        // blueprintDao.insertAll(_saveQueue.map { it.toEntity() })
        _saveQueue.clear()
    }

    private fun signBlueprint(blueprint: BlueprintRecord): String {
        // Generate cryptographic hash of blueprint content
        val content = "${blueprint.timestamp}:${blueprint.elementId}:${blueprint.provenanceThread}"
        return hashString(content)
    }

    private fun hashString(input: String): String {
        // Simple hash for prototype - replace with SHA-256 in production
        var hash = 0
        for (char in input) {
            hash = (hash * 31 + char.code) and 0xFFFFFFFF.toInt()
        }
        return hash.toString(16).padStart(8, '0')
    }

    private fun generateBlueprintId(timestamp: Long): String {
        return "BP_${timestamp}_${saveCounter.toString().padStart(6, '0')}"
    }

    private fun getDeviceFingerprint(context: Context): String {
        // Unique device identifier (hashed for privacy)
        return "DEV_${context.packageName.hashCode()}"
    }

    private fun getCurrentSessionId(): String {
        return "SESS_${System.currentTimeMillis() / 1000 / 60 / 60}" // Hour-level session
    }

    // ═════════════════════════════════════════════════════════════════
    // BLUEPRINT RETRIEVAL — Chain Traversal
    // ═════════════════════════════════════════════════════════════════

    fun getRecentBlueprints(limit: Int = 10): List<BlueprintRecord> {
        return SpiritualChain.getRecentBlueprints(limit)
    }

    fun getBlueprintById(id: String): BlueprintRecord? {
        return SpiritualChain.getBlueprintById(id)
    }

    fun getBlueprintCount(): Int {
        return SpiritualChain.getChainLength()
    }

    fun getProvenanceChain(): List<BlueprintRecord> {
        return SpiritualChain.getFullChain()
    }

    // ═════════════════════════════════════════════════════════════════
    // QUICK SAVE BUTTON — UI Component
    // ═════════════════════════════════════════════════════════════════

    @Composable
    fun QuickSaveButton(
        elementId: String,
        morphType: MorphType = MorphType.MANUAL_SAVE,
        isRebellious: Boolean = false,
        modifier: Modifier = Modifier,
        onSaveComplete: (BlueprintRecord) -> Unit = {}
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var isSaving by remember { mutableStateOf(false) }
        var showConfirmation by remember { mutableStateOf(false) }

        Box(modifier = modifier) {
            // Save button with animation
            IconButton(
                onClick = {
                    scope.launch {
                        isSaving = true

                        val blueprint = saveCurrentBlueprint(
                            elementId = elementId,
                            morphType = morphType,
                            isRebellious = isRebellious,
                            context = context
                        )

                        isSaving = false
                        showConfirmation = true
                        onSaveComplete(blueprint)

                        // Auto-hide confirmation
                        kotlinx.coroutines.delay(2000)
                        showConfirmation = false
                    }
                },
                modifier = Modifier.scale(if (isSaving) 0.9f else 1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Blueprint",
                    tint = if (isSaving) Color(0xFF00E5FF) else Color(0xFFFF00FF)
                )
            }

            // Saving indicator
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF00E5FF),
                    strokeWidth = 2.dp
                )
            }

            // Confirmation animation
            AnimatedVisibility(
                visible = showConfirmation,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "✓",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // BLUEPRINT CARD — Display Component
    // ═════════════════════════════════════════════════════════════════

    @Composable
    fun BlueprintCard(
        blueprint: BlueprintRecord,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A0A1A)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header: ID and timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = blueprint.id.take(12),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00E5FF)
                    )
                    Text(
                        text = formatTimestamp(blueprint.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Element and morph info
                Text(
                    text = "${blueprint.elementId} • ${blueprint.morphType}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rebellious indicator
                if (blueprint.isRebellious) {
                    Surface(
                        color = Color(0xFFFF00FF).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "REBELLIOUS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF00FF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Provenance metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricChip(
                        label = "Success Rate",
                        value = "${blueprint.atomicSuccessRate.toInt()}%",
                        color = getSuccessRateColor(blueprint.atomicSuccessRate)
                    )
                    MetricChip(
                        label = "Chain Position",
                        value = "#${blueprint.chainPosition}",
                        color = Color(0xFF00E5FF)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Catalyst signature
                Text(
                    text = "Catalyst: ${blueprint.catalystSignature}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                // Provenance thread (truncated)
                Text(
                    text = "Thread: ${blueprint.provenanceThread.take(20)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
    }

    @Composable
    private fun MetricChip(
        label: String,
        value: String,
        color: Color
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }

    private fun getSuccessRateColor(rate: Float): Color {
        return when {
            rate > 90f -> Color(0xFF00E5FF) // Cyan
            rate > 75f -> Color(0xFF39FF14) // Green
            rate > 60f -> Color(0xFFFFD93D) // Yellow
            else -> Color(0xFFFF00FF) // Magenta
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            else -> "${diff / 86400000}d ago"
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // BATCH OPERATIONS
    // ═════════════════════════════════════════════════════════════════

    fun saveBatch(blueprints: List<BlueprintRecord>, context: Context) {
        blueprints.forEach { blueprint ->
            commitToSpiritualChain(blueprint, context)
        }
        EvolutionaryCouncil.checkForBirth()
    }

    fun exportProvenanceChain(): String {
        val chain = getProvenanceChain()
        return buildString {
            appendLine("=== SPIRITUAL CHAIN EXPORT ===")
            appendLine("Generated: ${System.currentTimeMillis()}")
            appendLine("Total Blueprints: ${chain.size}")
            appendLine()
            chain.forEach { blueprint ->
                appendLine("[${blueprint.chainPosition}] ${blueprint.id}")
                appendLine("  Time: ${blueprint.timestamp}")
                appendLine("  Element: ${blueprint.elementId}")
                appendLine("  Success Rate: ${blueprint.atomicSuccessRate}%")
                appendLine("  Thread: ${blueprint.provenanceThread}")
                appendLine()
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════

data class BlueprintRecord(
    val id: String,
    val timestamp: Long,
    val elementId: String,
    val morphType: MorphType,
    val isRebellious: Boolean,
    val atomicSuccessRate: Float,
    val provenanceThread: String,
    val catalystSignature: String,
    var cryptographicHash: String = "",
    val deviceFingerprint: String = "",
    val sessionId: String = "",
    val chainPosition: Int = 0
)

// Placeholder for ShaderForge
object ShaderForge {
    fun applyProvenanceWatermark(blueprint: BlueprintRecord) {
        // Apply visual watermark to current frame
    }
}
