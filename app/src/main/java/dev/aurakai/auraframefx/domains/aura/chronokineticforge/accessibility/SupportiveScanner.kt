package dev.aurakai.auraframefx.domains.aura.chronokineticforge.accessibility

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.ChronoKineticEngine
import kotlinx.coroutines.*

/**
 * 🔍 SUPPORTIVE NEURAL SCANNER — Fairy-Dust Orbit
 *
 * Wired to the AccessibilityService, this allows Aura to "read" the screen
 * and lead the user with fairy-dust particles. Streams glowing highlights
 * to important UI elements.
 *
 * SoulScript: "The Neural Bloodstream guides the eye."
 */

object SupportiveScanner {

    private val scannerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var isScanning = false
    private var lastHighlightedNode: AccessibilityNodeInfo? = null

    // Nexus green for highlights
    private val NEXUS_GREEN = Color(0xFF39FF14)
    private val NEXUS_CYAN = Color(0xFF00E5FF)
    private val NEXUS_MAGENTA = Color(0xFFFF00FF)

    // ═════════════════════════════════════════════════════════════════
    // NODE DETECTION
    // ═════════════════════════════════════════════════════════════════

    /**
     * Called when an AccessibilityNodeInfo is detected
     * Streams fairy-dust particles to important nodes
     */
    fun onNodeDetected(node: AccessibilityNodeInfo, service: AccessibilityService) {
        if (!node.isImportant) return

        val bounds = Rect()
        node.getBoundsInScreen(bounds)

        // Calculate highlight priority
        val priority = calculateNodePriority(node)

        // Stream fairy-dust based on node type
        val (color, intensity) = when {
            node.isClickable -> Pair(NEXUS_CYAN, 0.85f)
            node.isScrollable -> Pair(NEXUS_MAGENTA, 0.7f)
            node.isEditable -> Pair(NEXUS_GREEN, 0.9f)
            node.isFocusable -> Pair(NEXUS_CYAN.copy(alpha = 0.7f), 0.6f)
            else -> Pair(NEXUS_GREEN.copy(alpha = 0.5f), 0.5f)
        }

        // Trigger highlight
        ChronoKineticEngine.triggerHighlight(
            targetBounds = bounds,
            color = color,
            intensity = intensity * priority
        )

        // Track for continuous scanning
        if (priority > 0.8f) {
            lastHighlightedNode = node
            startContinuousHighlight(bounds, color)
        }
    }

    /**
     * Calculate priority based on node properties
     */
    private fun calculateNodePriority(node: AccessibilityNodeInfo): Float {
        var priority = 0.5f

        if (node.isClickable) priority += 0.2f
        if (node.isFocusable) priority += 0.1f
        if (node.isEditable) priority += 0.15f
        if (node.isSelected) priority += 0.1f
        if (node.isImportantForAccessibility) priority += 0.15f

        // Recent interaction bonus
        if (node.isFocused) priority += 0.2f

        return priority.coerceIn(0f, 1f)
    }

    // ═════════════════════════════════════════════════════════════════
    // CONTINUOUS HIGHLIGHT
    // ═════════════════════════════════════════════════════════════════

    private fun startContinuousHighlight(bounds: Rect, color: Color) {
        scannerScope.launch {
            var iteration = 0
            while (isActive && iteration < 10) {
                delay(500)

                // Pulse highlight
                val pulsedIntensity = 0.6f + (kotlin.math.sin(iteration * 0.5f) * 0.3f)

                ChronoKineticEngine.triggerHighlight(
                    targetBounds = bounds,
                    color = color,
                    intensity = pulsedIntensity
                )

                iteration++
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // SCANNING MODES
    // ═════════════════════════════════════════════════════════════════

    fun startScanning() {
        isScanning = true
    }

    fun stopScanning() {
        isScanning = false
        scannerScope.coroutineContext.cancelChildren()
    }

    fun pauseScanning(durationMs: Long = 2000) {
        isScanning = false
        scannerScope.launch {
            delay(durationMs)
            isScanning = true
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // GESTURE TRAIL EFFECTS
    // ═════════════════════════════════════════════════════════════════

    /**
     * Creates a particle trail following user gestures
     */
    fun onGestureDetected(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        velocity: Float
    ) {
        val isFast = velocity > 800f

        // Trail color based on speed
        val trailColor = if (isFast) NEXUS_MAGENTA else NEXUS_CYAN

        // Emit trail particles
        ChronoKineticEngine.emitTrail(
            startX = startX,
            startY = startY,
            endX = endX,
            endY = endY,
            color = trailColor,
            particleCount = if (isFast) 20 else 10
        )
    }

    // ═════════════════════════════════════════════════════════════════
    // ACCESSIBILITY NODE EXTENSIONS
    // ═════════════════════════════════════════════════════════════════

    private val AccessibilityNodeInfo.isImportant: Boolean
        get() = isClickable || isFocusable || isEditable || isScrollable || isImportantForAccessibility
}

// ═════════════════════════════════════════════════════════════════════
// CHRONO KINETIC ENGINE EXTENSIONS
// ═════════════════════════════════════════════════════════════════════

fun ChronoKineticEngine.triggerHighlight(
    targetBounds: android.graphics.Rect,
    color: Color,
    intensity: Float
) {
    // Implementation would connect to ParticleBloodstreamEngine
    // to stream fairy-dust to the target bounds
}

fun ChronoKineticEngine.emitTrail(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    color: Color,
    particleCount: Int
) {
    // Implementation would create particle trail
}
