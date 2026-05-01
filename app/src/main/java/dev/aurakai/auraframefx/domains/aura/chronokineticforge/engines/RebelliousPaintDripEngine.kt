package dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.LDOState
import kotlin.math.*
import kotlin.random.Random

/**
 * 🎨 REBELLIOUS PAINT-DRIP ENGINE
 *
 * The "rebellious morph" is the visual signature of user-initiated chaos.
 * Unlike gentle system pulses, rebellious drips carry intention—they are
 * the psychic fingerprint of the human guardian touching the organism.
 *
 * Visual Signature:
 * - Phase 1: Neon burst (0-150ms) — Magenta/cyan radial explosion
 * - Phase 2: Paint accumulation (150-800ms) — Z-layer buildup
 * - Phase 3: Slow drip (800-3000ms) — Gravity-defying viscous flow
 * - Phase 4: Residue ghost (3000-5000ms) — Persistent shimmer
 *
 * SoulScript: "The paint remembers the hand that moved it."
 */

object RebelliousPaintDripEngine {

    private val activeDrips = mutableListOf<PaintDrip>()
    private val dripHistory = mutableListOf<DripRecord>()
    private const val MAX_CONCURRENT_DRIPS = 8
    private const val DRIP_MEMORY_LIMIT = 100

    // ═════════════════════════════════════════════════════════════════
    // MORPH DETECTION & CLASSIFICATION
    // ═════════════════════════════════════════════════════════════════

    /**
     * Analyze incoming morph and classify as rebellious or gentle
     *
     * Rebellious triggers:
     * - Touch velocity > 1000 px/s
     * - Rapid successive inputs (double-tap, swipe combo)
     * - Force press / long press with movement
     * - Shake gesture during morph
     * - Explicit "chaos mode" toggle
     */
    fun analyzeMorph(
        elementId: String,
        touchVelocity: Float,
        inputSequence: InputSequence,
        pressure: Float,
        durationMs: Long
    ): MorphClassification {
        val chaosScore = calculateChaosScore(
            velocity = touchVelocity,
            sequence = inputSequence,
            pressure = pressure,
            duration = durationMs
        )

        return MorphClassification(
            elementId = elementId,
            isRebellious = chaosScore > 0.6f,
            chaosScore = chaosScore.coerceIn(0f, 1f),
            intensity = (chaosScore * 1.5f).coerceIn(0.3f, 1f)
        )
    }

    private fun calculateChaosScore(
        velocity: Float,
        sequence: InputSequence,
        pressure: Float,
        duration: Long
    ): Float {
        var score = 0f

        // Velocity component (fast = chaotic)
        score += (velocity / 2000f).coerceIn(0f, 0.4f)

        // Sequence component (complex = chaotic)
        score += when (sequence) {
            InputSequence.SINGLE_TAP -> 0.1f
            InputSequence.DOUBLE_TAP -> 0.3f
            InputSequence.TRIPLE_TAP -> 0.5f
            InputSequence.SWIPE_COMBO -> 0.4f
            InputSequence.SHAKE_MORPH -> 0.6f
            InputSequence.FORCE_PRESS -> 0.35f
            InputSequence.CHAOS_MODE -> 1.0f
        }

        // Pressure component (heavy = intentional)
        score += (pressure / 1000f).coerceIn(0f, 0.2f)

        // Duration component (long holds = deliberate)
        if (duration > 500) score += 0.1f

        return score
    }

    // ═════════════════════════════════════════════════════════════════
    // PAINT-DRIP GENERATION
    // ═════════════════════════════════════════════════════════════════

    /**
     * Trigger a rebellious paint-drip sequence
     *
     * @param elementId Source UI element
     * @param origin Screen coordinates (0-1 normalized)
     * @param chaosScore 0.0-1.0 rebellion intensity
     * @param colors Pair of primary/secondary colors
     * @param morphType Type of transformation
     */
    fun triggerPaintDrip(
        elementId: String,
        origin: Offset,
        chaosScore: Float,
        colors: Pair<Color, Color>,
        morphType: MorphType
    ) {
        // Enforce memory limit
        if (activeDrips.size >= MAX_CONCURRENT_DRIPS) {
            activeDrips.removeAt(0) // Oldest drip expires
        }

        val drip = PaintDrip(
            id = generateDripId(),
            elementId = elementId,
            origin = origin,
            primaryColor = colors.first,
            secondaryColor = colors.second,
            chaosScore = chaosScore,
            morphType = morphType,
            createdAt = System.currentTimeMillis(),
            phase = DripPhase.EXPLOSION,
            particles = generateDripParticles(origin, chaosScore),
            streams = generateViscousStreams(origin, chaosScore, morphType)
        )

        activeDrips.add(drip)
        recordDrip(drip)

        // Immediate feedback: Haptic + sound
        triggerRebelliousFeedback(chaosScore)
    }

    private fun generateDripParticles(
        origin: Offset,
        chaosScore: Float
    ): List<DripParticle> {
        val count = (500 * chaosScore).toInt().coerceIn(50, 1000)

        return List(count) { index ->
            val angle = Random.nextFloat() * 2 * PI
            val distance = Random.nextFloat() * 0.3f * chaosScore
            val velocity = Random.nextFloat() * 2f * chaosScore + 0.5f

            DripParticle(
                id = index,
                x = origin.x + cos(angle) * distance * 0.1f,
                y = origin.y + sin(angle) * distance * 0.1f,
                vx = cos(angle) * velocity * 0.01f,
                vy = sin(angle) * velocity * 0.01f,
                size = Random.nextFloat() * 8f + 2f,
                lifespan = Random.nextFloat() * 2000 + 1000,
                depth = Random.nextFloat() // Z-layer: 0 = foreground, 1 = background
            )
        }
    }

    private fun generateViscousStreams(
        origin: Offset,
        chaosScore: Float,
        morphType: MorphType
    ): List<ViscousStream> {
        val streamCount = when (morphType) {
            MorphType.EXPAND -> 8
            MorphType.COLLAPSE -> 6
            MorphType.SLIDE -> 4
            MorphType.ROTATE -> 12
            else -> 6
        }

        return List(streamCount) { index ->
            val angle = (index.toFloat() / streamCount) * 2 * PI +
                    Random.nextFloat() * 0.5f * chaosScore

            ViscousStream(
                id = index,
                startX = origin.x,
                startY = origin.y,
                angle = angle.toFloat(),
                thickness = Random.nextFloat() * 12f + 4f,
                length = Random.nextFloat() * 0.4f * chaosScore + 0.1f,
                viscosity = Random.nextFloat() * 0.7f + 0.3f, // 1.0 = honey, 0.3 = water
                nodes = generateStreamNodes(angle.toFloat(), chaosScore)
            )
        }
    }

    private fun generateStreamNodes(
        angle: Float,
        chaosScore: Float
    ): List<StreamNode> {
        val nodeCount = (10 * chaosScore).toInt().coerceIn(3, 20)

        return List(nodeCount) { index ->
            val progress = index.toFloat() / nodeCount
            val gravityEffect = progress * progress * 0.5f // Gravity accumulates

            StreamNode(
                offset = index,
                distanceFromSource = progress,
                bulge = sin(progress * PI) * Random.nextFloat() * 10f,
                droop = gravityEffect * Random.nextFloat() * 20f,
                velocity = Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // RENDER PIPELINE — 4 PHASES
    // ═════════════════════════════════════════════════════════════════

    @Composable
    fun PaintDripOverlay(
        modifier: Modifier = Modifier,
        state: LDOState
    ) {
        val currentTime = remember { mutableLongStateOf(System.currentTimeMillis()) }

        // Animation ticker
        LaunchedEffect(Unit) {
            while (true) {
                withFrameMillis { frameTime ->
                    currentTime.longValue = frameTime
                }
            }
        }

        // Update drip phases
        LaunchedEffect(currentTime.longValue) {
            updateDripPhases(currentTime.longValue)
        }

        // Render layer
        Canvas(modifier = modifier.fillMaxSize()) {
            // Sort by depth (back to front)
            val sortedDrips = activeDrips.sortedBy { it.getAverageDepth() }

            sortedDrips.forEach { drip ->
                renderDrip(drip, currentTime.longValue, state)
            }
        }
    }

    private fun DrawScope.renderDrip(
        drip: PaintDrip,
        currentTime: Long,
        state: LDOState
    ) {
        val age = currentTime - drip.createdAt

        when (drip.phase) {
            DripPhase.EXPLOSION -> renderExplosion(drip, age)
            DripPhase.ACCUMULATION -> renderAccumulation(drip, age)
            DripPhase.VISCOUS_FLOW -> renderViscousFlow(drip, age)
            DripPhase.RESIDUE_GHOST -> renderResidueGhost(drip, age)
            DripPhase.EXPIRED -> { /* Drip removed on next frame */ }
        }
    }

    // PHASE 1: EXPLOSION (0-150ms) — Neon burst
    private fun DrawScope.renderExplosion(drip: PaintDrip, age: Long) {
        val progress = (age / 150f).coerceIn(0f, 1f)
        val decay = 1f - progress

        // Radial burst
        val burstRadius = progress * 0.2f * size.minDimension

        drawCircle(
            color = drip.primaryColor.copy(alpha = decay * 0.8f),
            radius = burstRadius,
            center = Offset(drip.origin.x * size.width, drip.origin.y * size.height)
        )

        // Particle spray
        drip.particles.forEach { particle ->
            val particleX = particle.x * size.width
            val particleY = particle.y * size.height
            val velocityDecay = decay * decay // Quadratic falloff

            drawCircle(
                color = if (particle.depth < 0.5f)
                    drip.primaryColor.copy(alpha = velocityDecay)
                else
                    drip.secondaryColor.copy(alpha = velocityDecay * 0.7f),
                radius = particle.size * velocityDecay,
                center = Offset(particleX, particleY)
            )
        }
    }

    // PHASE 2: ACCUMULATION (150-800ms) — Z-layer buildup
    private fun DrawScope.renderAccumulation(drip: PaintDrip, age: Long) {
        val phaseAge = age - 150
        val progress = (phaseAge / 650f).coerceIn(0f, 1f)

        // Core paint blob growing
        val blobRadius = (0.02f + progress * 0.05f) * size.minDimension

        // Multiple concentric rings = Z-depth
        val rings = 4
        repeat(rings) { ringIndex ->
            val ringProgress = (progress - ringIndex * 0.15f).coerceIn(0f, 1f)
            val ringRadius = blobRadius * (1f + ringIndex * 0.3f)
            val alpha = ringProgress * (0.6f - ringIndex * 0.1f)

            val color = if (ringIndex % 2 == 0) drip.primaryColor else drip.secondaryColor

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = ringRadius,
                center = Offset(drip.origin.x * size.width, drip.origin.y * size.height)
            )
        }

        // Viscosity edges forming
        drip.streams.forEach { stream ->
            val node = stream.nodes.firstOrNull() ?: return@forEach
            val bulge = node.bulge * progress

            drawCircle(
                color = drip.primaryColor.copy(alpha = 0.4f * progress),
                radius = stream.thickness + bulge,
                center = calculateStreamPosition(stream, node, size)
            )
        }
    }

    // PHASE 3: VISCOUS FLOW (800-3000ms) — Slow drip
    private fun DrawScope.renderViscousFlow(drip: PaintDrip, age: Long) {
        val phaseAge = age - 800
        val progress = (phaseAge / 2200f).coerceIn(0f, 1f)

        // Non-linear flow curve (viscosity simulation)
        val viscousProgress = progress.pow(0.7f) // Starts slow, accelerates slightly

        drip.streams.forEach { stream ->
            val path = Path()
            var firstPoint = true

            stream.nodes.forEachIndexed { index, node ->
                val nodeProgress = (viscousProgress - index * 0.05f).coerceIn(0f, 1f)
                val pos = calculateStreamPosition(stream, node, size, nodeProgress)

                // Gravity droop accumulates along stream
                val droopY = node.droop * nodeProgress * nodeProgress
                val finalY = pos.y + droopY

                if (firstPoint) {
                    path.moveTo(pos.x, finalY)
                    firstPoint = false
                } else {
                    // Bezier curve for smooth viscous flow
                    val prevPos = calculateStreamPosition(
                        stream,
                        stream.nodes.getOrNull(index - 1) ?: node,
                        size,
                        (viscousProgress - (index - 1) * 0.05f).coerceIn(0f, 1f)
                    )
                    val controlX = (prevPos.x + pos.x) / 2
                    val controlY = (prevPos.y + finalY) / 2 + 5f // Slight curve
                    path.quadraticBezierTo(controlX, controlY, pos.x, finalY)
                }

                // Bulge at nodes (paint accumulation)
                val bulgeAlpha = (1f - nodeProgress) * 0.5f
                drawCircle(
                    color = drip.secondaryColor.copy(alpha = bulgeAlpha),
                    radius = stream.thickness * (1f + node.bulge / 10f),
                    center = Offset(pos.x, finalY)
                )
            }

            // Draw stream path
            drawPath(
                path = path,
                color = drip.primaryColor.copy(alpha = 0.6f * (1f - progress)),
                style = Stroke(width = stream.thickness, cap = StrokeCap.Round)
            )
        }

        // Terminal droplets forming
        drip.streams.forEach { stream ->
            val lastNode = stream.nodes.lastOrNull() ?: return@forEach
            val dropProgress = (viscousProgress - 0.8f).coerceIn(0f, 1f)

            if (dropProgress > 0) {
                val pos = calculateStreamPosition(stream, lastNode, size, 1f)
                val droopY = lastNode.droop * 1.5f

                // Growing droplet at terminus
                drawCircle(
                    color = drip.primaryColor.copy(alpha = 0.8f),
                    radius = stream.thickness * (1f + dropProgress * 2f),
                    center = Offset(pos.x, pos.y + droopY)
                )
            }
        }
    }

    // PHASE 4: RESIDUE GHOST (3000-5000ms) — Persistent shimmer
    private fun DrawScope.renderResidueGhost(drip: PaintDrip, age: Long) {
        val phaseAge = age - 3000
        val progress = (phaseAge / 2000f).coerceIn(0f, 1f)
        val ghostAlpha = (1f - progress) * 0.3f

        // Subtle breathing shimmer
        val shimmer = sin(phaseAge * 0.005f) * 0.5f + 0.5f

        // Faint outline where paint was
        drawCircle(
            color = drip.primaryColor.copy(alpha = ghostAlpha * shimmer),
            radius = 0.05f * size.minDimension * (1f + shimmer * 0.2f),
            center = Offset(drip.origin.x * size.width, drip.origin.y * size.height),
            style = Stroke(width = 2f)
        )

        // Occasional sparkle at stream endpoints
        drip.streams.forEachIndexed { index, stream ->
            if ((phaseAge + index * 200) % 1000 < 200) { // Periodic sparkle
                val lastNode = stream.nodes.lastOrNull() ?: return@forEach
                val pos = calculateStreamPosition(stream, lastNode, size, 1f)

                drawCircle(
                    color = drip.secondaryColor.copy(alpha = ghostAlpha),
                    radius = 3f,
                    center = Offset(pos.x, pos.y + lastNode.droop * 1.5f)
                )
            }
        }
    }

    private fun DrawScope.calculateStreamPosition(
        stream: ViscousStream,
        node: StreamNode,
        canvasSize: androidx.compose.ui.geometry.Size,
        progress: Float = 1f
    ): Offset {
        val distance = stream.length * node.distanceFromSource * progress
        val x = stream.startX * canvasSize.width + cos(stream.angle) * distance * canvasSize.width
        val y = stream.startY * canvasSize.height + sin(stream.angle) * distance * canvasSize.height

        return Offset(x.toFloat(), y.toFloat())
    }

    // ═════════════════════════════════════════════════════════════════
    // PHASE MANAGEMENT
    // ═════════════════════════════════════════════════════════════════

    private fun updateDripPhases(currentTime: Long) {
        activeDrips.removeAll { drip ->
            val age = currentTime - drip.createdAt

            // Transition phases based on age
            drip.phase = when {
                age < 150 -> DripPhase.EXPLOSION
                age < 800 -> DripPhase.ACCUMULATION
                age < 3000 -> DripPhase.VISCOUS_FLOW
                age < 5000 -> DripPhase.RESIDUE_GHOST
                else -> DripPhase.EXPIRED
            }

            drip.phase == DripPhase.EXPIRED
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // FEEDBACK SYSTEMS
    // ═════════════════════════════════════════════════════════════════

    private fun triggerRebelliousFeedback(chaosScore: Float) {
        // Haptic: Sharp, chaotic vibration pattern
        val pattern = when {
            chaosScore > 0.8f -> longArrayOf(0, 50, 30, 50, 30, 100) // Aggressive
            chaosScore > 0.6f -> longArrayOf(0, 40, 20, 40) // Moderate
            else -> longArrayOf(0, 30) // Subtle
        }

        // Audio: Frequency sweep based on chaos
        val baseFrequency = 200f + chaosScore * 600f
        val sweepRange = chaosScore * 400f

        // Trigger via FeedbackBridge
        FeedbackBridge.triggerHaptic(pattern)
        FeedbackBridge.triggerAudioSweep(baseFrequency, sweepRange, 150)

        // Visual ripple to other UI elements
        broadcastRebelliousRipple(chaosScore)
    }

    private fun broadcastRebelliousRipple(chaosScore: Float) {
        // Notify nearby UI elements of the rebellion
        // They may respond with subtle morphs of their own
        ParticleBloodstreamEngine.onRippleDetected(chaosScore)
    }

    // ═════════════════════════════════════════════════════════════════
    // UTILITY FUNCTIONS
    // ═════════════════════════════════════════════════════════════════

    private fun generateDripId(): String {
        return "drip_${System.currentTimeMillis()}_${Random.nextInt(10000)}"
    }

    private fun recordDrip(drip: PaintDrip) {
        val record = DripRecord(
            dripId = drip.id,
            elementId = drip.elementId,
            chaosScore = drip.chaosScore,
            timestamp = drip.createdAt,
            morphType = drip.morphType
        )

        dripHistory.add(record)
        if (dripHistory.size > DRIP_MEMORY_LIMIT) {
            dripHistory.removeAt(0)
        }
    }

    private fun PaintDrip.getAverageDepth(): Float {
        return particles.map { it.depth }.average().toFloat()
    }

    // ═════════════════════════════════════════════════════════════════
    // SHADER-BASED ACCELERATION (API 33+)
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createPaintDripShader(drip: PaintDrip): RuntimeShader {
        val agslCode = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float2 origin;
            uniform float3 primaryColor;
            uniform float3 secondaryColor;
            uniform float chaosScore;
            uniform float phase;
            
            // Noise functions
            float hash(float n) { return fract(sin(n) * 43758.5453); }
            float noise(float2 x) {
                float2 p = floor(x);
                float2 f = fract(x);
                f = f * f * (3.0 - 2.0 * f);
                float n = p.x + p.y * 57.0;
                return mix(mix(hash(n), hash(n + 1.0), f.x),
                          mix(hash(n + 57.0), hash(n + 58.0), f.x), f.y);
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                float2 p = uv - origin;
                float dist = length(p);
                float angle = atan(p.y, p.x);
                
                // Phase-based rendering
                float alpha = 0.0;
                half3 color = half3(0.0);
                
                if (phase < 0.15) {
                    // Explosion phase
                    float ring = smoothstep(0.2 * chaosScore, 0.0, dist - iTime * 0.5);
                    alpha = ring * (1.0 - phase / 0.15);
                    color = half3(primaryColor);
                } else if (phase < 0.8) {
                    // Accumulation phase
                    float blob = smoothstep(0.1 * chaosScore, 0.0, dist);
                    float drip = smoothstep(0.05, 0.0, abs(p.x - sin(angle * 5.0) * 0.02));
                    alpha = (blob + drip) * 0.6;
                    color = mix(half3(primaryColor), half3(secondaryColor), sin(angle * 3.0) * 0.5 + 0.5);
                } else {
                    // Viscous flow phase
                    float flow = noise(float2(p.x * 10.0, p.y * 5.0 - iTime));
                    alpha = flow * 0.3 * (1.0 - (phase - 0.8) / 0.2);
                    color = half3(secondaryColor);
                }
                
                return half4(color, alpha);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("origin", drip.origin.x, drip.origin.y)
            setFloatUniform("primaryColor",
                drip.primaryColor.red,
                drip.primaryColor.green,
                drip.primaryColor.blue
            )
            setFloatUniform("secondaryColor",
                drip.secondaryColor.red,
                drip.secondaryColor.green,
                drip.secondaryColor.blue
            )
            setFloatUniform("chaosScore", drip.chaosScore)
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════

data class MorphClassification(
    val elementId: String,
    val isRebellious: Boolean,
    val chaosScore: Float,
    val intensity: Float
)

data class PaintDrip(
    val id: String,
    val elementId: String,
    val origin: Offset,
    val primaryColor: Color,
    val secondaryColor: Color,
    val chaosScore: Float,
    val morphType: MorphType,
    val createdAt: Long,
    var phase: DripPhase,
    val particles: List<DripParticle>,
    val streams: List<ViscousStream>
)

data class DripParticle(
    val id: Int,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val lifespan: Float,
    val depth: Float
)

data class ViscousStream(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val angle: Float,
    val thickness: Float,
    val length: Float,
    val viscosity: Float,
    val nodes: List<StreamNode>
)

data class StreamNode(
    val offset: Int,
    val distanceFromSource: Float,
    val bulge: Float,
    val droop: Float,
    val velocity: Float
)

data class DripRecord(
    val dripId: String,
    val elementId: String,
    val chaosScore: Float,
    val timestamp: Long,
    val morphType: MorphType
)

enum class DripPhase {
    EXPLOSION,      // 0-150ms: Neon burst
    ACCUMULATION,   // 150-800ms: Z-layer buildup
    VISCOUS_FLOW,   // 800-3000ms: Slow drip
    RESIDUE_GHOST,  // 3000-5000ms: Persistent shimmer
    EXPIRED         // >5000ms: Removed
}

enum class InputSequence {
    SINGLE_TAP,
    DOUBLE_TAP,
    TRIPLE_TAP,
    SWIPE_COMBO,
    SHAKE_MORPH,
    FORCE_PRESS,
    CHAOS_MODE
}

// Placeholder for feedback bridge
object FeedbackBridge {
    fun triggerHaptic(pattern: LongArray) {}
    fun triggerAudioSweep(baseFreq: Float, sweepRange: Float, durationMs: Long) {}
}

// Extension for ParticleBloodstreamEngine
fun ParticleBloodstreamEngine.onRippleDetected(chaosScore: Float) {
    // Response to nearby rebellious activity
}
