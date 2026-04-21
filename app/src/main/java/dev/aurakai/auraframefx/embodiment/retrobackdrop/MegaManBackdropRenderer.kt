// File: embodiment/retrobackdrop/MegaManBackdropRenderer.kt
package dev.aurakai.auraframefx.embodiment.retrobackdrop

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import dev.aurakai.auraframefx.domains.kai.romtools.OperationProgress
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Mega Man-typography animated backdrop for ROM Tools operations.
 *
 * Features:
 * - Aura at top platform throwing construction cones
 * - Kai climbing ladders (or teleporting when annoyed!)
 * - Chutes & Ladders board game mechanics
 * - Progress-synchronized animation
 * - Mega Man teleportation effect
 *
 * @param operationProgress Current ROM operation progress (0-100)
 * @param modifier Modifier for the Canvas
 * @param enabled Whether backdrop is enabled (default: true)
 */
@Composable
fun MegaManBackdropRenderer(
    operationProgress: OperationProgress?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (!enabled) return

    // State management
    var cones by remember { mutableStateOf(listOf<ConstructionCone>()) }
    var kaiHitCount by remember { mutableStateOf(0) }
    var isTeleporting by remember { mutableStateOf(false) }
    val teleportEffect = remember { TeleportationEffect(0f, 0f, 0f, 0f) }
    var lastConeThrow by remember { mutableStateOf(0L) }

    // Special abilities
    val hexagonalShield = remember { HexagonalShield() }
    val coneBarrage = remember { ConeBarrageAttack(400f, 100f) }  // auraX, auraY

    val progress = operationProgress?.progress ?: 0f

    // Kai's Y position (animated based on progress)
    val kaiY by animateFloatAsState(
        targetValue = if (isTeleporting) 100f else (700f - (progress / 100f) * 600f),
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "KaiPosition"
    )

    // Layout constants
    val screenWidth = 800f
    val screenHeight = 800f
    val kaiX = 200f
    val auraX = screenWidth / 2
    val auraY = 100f

    // Platform and chute layout
    val platforms = remember {
        listOf(
            Rect(0f, 300f, screenWidth, 320f),  // Platform 2
            Rect(0f, 600f, screenWidth, 620f)   // Platform 1
        )
    }

    val chutes = remember {
        listOf(
            Rect(500f, 300f, 600f, 600f),  // Chute from platform 2 to 1
            Rect(300f, 100f, 400f, 300f)   // Chute from top to platform 2
        )
    }

    // Game loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60 FPS

            // Update Cone Barrage (Aura's ultimate)
            val barrageCones = coneBarrage.update(0.016f)
            cones = cones + barrageCones

            // Update Hexagonal Shield (Kai's defense)
            hexagonalShield.update(0.016f)

            // Auto-activate shield when barrage is active and 5+ cones are nearby
            if (coneBarrage.isBarrageActive() && !hexagonalShield.isShieldActive()) {
                val threateningCones = cones.count { cone ->
                    val dx = cone.x - kaiX
                    val dy = cone.y - kaiY
                    kotlin.math.sqrt(dx * dx + dy * dy) < 150f
                }
                if (threateningCones >= 5) {
                    hexagonalShield.activate()
                }
            }

            // Update cones
            cones = cones.mapNotNull { cone ->
                cone.update(0.016f, platforms, chutes)

                // Check if cone is blocked by shield
                if (hexagonalShield.blocksPoint(cone.x, cone.y, kaiX, kaiY)) {
                    null  // Remove cone - blocked by shield!
                }
                // Check if cone hit Kai
                else if (cone.hitsKai(kaiX, kaiY)) {
                    kaiHitCount++
                    if (kaiHitCount >= 3 && !isTeleporting) {
                        // Kai gets annoyed - TELEPORT!
                        isTeleporting = true
                        teleportEffect.trigger()
                        kaiHitCount = 0
                    }
                    null  // Remove cone that hit Kai
                } else if (cone.isOffScreen(screenHeight)) {
                    null  // Remove off-screen cones
                } else {
                    cone
                }
            }

            // Update teleportation effect
            if (isTeleporting) {
                if (teleportEffect.update(0.016f)) {
                    isTeleporting = false
                }
            }

            // Aura throws cones periodically (normal throws, not barrage)
            if (!coneBarrage.isBarrageActive()) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastConeThrow > 2000) {  // Every 2 seconds
                    cones = cones + ConstructionCone(
                        x = auraX + Random.nextFloat() * 40f - 20f,
                        y = auraY + 20f,
                        velocityX = Random.nextFloat() * 100f - 50f,
                        velocityY = 50f + Random.nextFloat() * 50f
                    )
                    lastConeThrow = currentTime
                }
            }
        }
    }

    // Render
    Canvas(modifier = modifier.fillMaxSize()) {
        // Background
        drawRect(
            color = MegaManPalette.CYBER_DARK,
            size = Size(size.width, size.height)
        )

        // Draw platforms
        drawPlatforms(platforms)

        // Draw chutes
        drawChutes(chutes)

        // Draw ladders
        drawLadder(kaiX, 300f, 600f)
        drawLadder(kaiX, 100f, 300f)

        // Draw Aura at top
        drawAura(auraX, auraY)

        // Draw cones
        cones.forEach { it.draw(this) }

        // Draw Kai (unless teleporting)
        if (!isTeleporting) {
            drawKai(kaiX, kaiY)
        }

        // Draw hexagonal shield around Kai
        hexagonalShield.draw(this, kaiX, kaiY)

        // Draw teleportation effect
        teleportEffect.draw(this)

        // Barrage warning overlay
        if (coneBarrage.isBarrageActive()) {
            drawBarrageWarning()
        }

        // Cooldown indicators (bottom right)
        drawCooldownIndicators(
            shieldCooldown = hexagonalShield.getCooldownPercent(),
            barrageCooldown = coneBarrage.getCooldownPercent(),
            barrageSecondsRemaining = coneBarrage.getCooldownSecondsRemaining()
        )

        // Progress bar at bottom
        drawProgressBar(progress)
    }
}

/**
 * Draws the cyber platforms.
 */
private fun DrawScope.drawPlatforms(platforms: List<Rect>) {
    platforms.forEach { platform ->
        drawRect(
            color = MegaManPalette.PLATFORM_DARK,
            topLeft = Offset(platform.left, platform.top),
            size = Size(platform.width, platform.height)
        )
        // Platform edge highlight
        drawRect(
            color = MegaManPalette.PLATFORM_LIGHT,
            topLeft = Offset(platform.left, platform.top),
            size = Size(platform.width, 3f)
        )
    }
}

/**
 * Draws the red chutes (slides).
 */
private fun DrawScope.drawChutes(chutes: List<Rect>) {
    chutes.forEach { chute ->
        drawRect(
            color = MegaManPalette.CHUTE_RED.copy(alpha = 0.6f),
            topLeft = Offset(chute.left, chute.top),
            size = Size(chute.width, chute.height)
        )
    }
}

/**
 * Draws a steel ladder.
 */
private fun DrawScope.drawLadder(x: Float, topY: Float, bottomY: Float) {
    val rungs = 5
    val spacing = (bottomY - topY) / (rungs + 1)

    // Vertical bars
    drawRect(
        color = MegaManPalette.LADDER_STEEL,
        topLeft = Offset(x - 15f, topY),
        size = Size(4f, bottomY - topY)
    )
    drawRect(
        color = MegaManPalette.LADDER_STEEL,
        topLeft = Offset(x + 11f, topY),
        size = Size(4f, bottomY - topY)
    )

    // Rungs
    repeat(rungs) { i ->
        val rungY = topY + spacing * (i + 1)
        drawRect(
            color = MegaManPalette.LADDER_STEEL,
            topLeft = Offset(x - 15f, rungY),
            size = Size(30f, 3f)
        )
    }
}

/**
 * Draws Aura sprite (simplified 16x16 pixel representation).
 */
private fun DrawScope.drawAura(x: Float, y: Float) {
    // Head
    drawCircle(
        color = MegaManPalette.AURA_PRIMARY,
        radius = 12f,
        center = Offset(x, y)
    )
    // Highlight
    drawCircle(
        color = MegaManPalette.AURA_HIGHLIGHT,
        radius = 4f,
        center = Offset(x - 3f, y - 3f)
    )
}

/**
 * Draws Kai sprite (Mega Man blue, simplified 16x16 pixel representation).
 */
private fun DrawScope.drawKai(x: Float, y: Float) {
    // Body
    drawCircle(
        color = MegaManPalette.KAI_PRIMARY,
        radius = 12f,
        center = Offset(x, y)
    )
    // Helmet highlight
    drawCircle(
        color = MegaManPalette.KAI_HIGHLIGHT,
        radius = 4f,
        center = Offset(x - 3f, y - 3f)
    )
}

/**
 * Draws the progress bar at the bottom.
 */
private fun DrawScope.drawProgressBar(progress: Float) {
    val barWidth = size.width * 0.8f
    val barHeight = 20f
    val x = (size.width - barWidth) / 2
    val y = size.height - 50f

    // Background
    drawRect(
        color = MegaManPalette.BLACK.copy(alpha = 0.8f),
        topLeft = Offset(x, y),
        size = Size(barWidth, barHeight)
    )

    // Progress fill
    drawRect(
        color = MegaManPalette.KAI_PRIMARY,
        topLeft = Offset(x, y),
        size = Size(barWidth * (progress / 100f), barHeight)
    )
}

/**
 * Draws barrage warning overlay
 */
private fun DrawScope.drawBarrageWarning() {
    // Red screen flash
    drawRect(
        color = MegaManPalette.AURA_SECONDARY.copy(alpha = 0.2f),
        size = size
    )

    // Warning text at top center
    "⚠️ CONE BARRAGE ⚠️"
    // Note: Text drawing would require TextMeasurer - simplified with rect for now
    drawRect(
        color = MegaManPalette.AURA_PRIMARY.copy(alpha = 0.8f),
        topLeft = Offset(size.width / 2 - 150f, 30f),
        size = Size(300f, 40f)
    )
}

/**
 * Draws cooldown indicators for abilities
 */
private fun DrawScope.drawCooldownIndicators(
    shieldCooldown: Float,
    barrageCooldown: Float,
    barrageSecondsRemaining: Int
) {
    val indicatorSize = 50f
    val spacing = 60f
    val startX = size.width - 70f
    val startY = size.height - 150f

    // Shield cooldown (top indicator)
    drawCooldownCircle(
        x = startX,
        y = startY,
        radius = indicatorSize / 2,
        cooldownPercent = shieldCooldown,
        color = MegaManPalette.KAI_TELEPORT,
        label = "🛡️"
    )

    // Barrage cooldown (bottom indicator)
    drawCooldownCircle(
        x = startX,
        y = startY + spacing,
        radius = indicatorSize / 2,
        cooldownPercent = barrageCooldown,
        color = MegaManPalette.AURA_PRIMARY,
        label = "${barrageSecondsRemaining}s"
    )
}

/**
 * Draws a circular cooldown indicator
 */
private fun DrawScope.drawCooldownCircle(
    x: Float,
    y: Float,
    radius: Float,
    cooldownPercent: Float,
    color: androidx.compose.ui.graphics.Color,
    label: String
) {
    // Background circle
    drawCircle(
        color = MegaManPalette.BLACK.copy(alpha = 0.7f),
        radius = radius,
        center = Offset(x, y)
    )

    // Cooldown progress (pie chart style)
    if (cooldownPercent > 0f) {
        // Gray overlay showing remaining cooldown
        drawCircle(
            color = MegaManPalette.BLACK.copy(alpha = 0.5f),
            radius = radius * 0.8f,
            center = Offset(x, y)
        )
    } else {
        // Ready - show color
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = radius * 0.8f,
            center = Offset(x, y)
        )
    }

    // Border
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(x, y),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
    )
}
