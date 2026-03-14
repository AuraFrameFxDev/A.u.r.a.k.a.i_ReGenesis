package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// ── Palette ─────────────────────────────────────────────────────────────────

private val C_GREEN  = Color(0xFF00FF41)
private val C_CYAN   = Color(0xFF00FFFF)
private val C_DARK   = Color(0xFF003800)
private val C_GLOW   = Color(0xFFAAFFCC)
private val C_BLACK  = Color(0xFF000000)

// ── Matrix charset ───────────────────────────────────────────────────────────

private val MATRIX_CHARS =
    "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン" +
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ@#%&*<>/\\"

// ── Pixel art sprite (20 wide × 22 tall) ────────────────────────────────────
// 0 = transparent
// 1 = body  (C_GREEN)
// 2 = visor (C_CYAN)
// 3 = dark  (C_DARK)
// 4 = glow  (C_GLOW)

private val AGENT_SPRITE = arrayOf(
    intArrayOf(0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0), //  0 head top
    intArrayOf(0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0), //  1
    intArrayOf(0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0), //  2
    intArrayOf(0,0,0,0,1,1,1,2,2,2,2,2,2,1,1,1,0,0,0,0), //  3 visor top
    intArrayOf(0,0,0,0,1,1,2,2,4,4,4,4,2,2,1,1,0,0,0,0), //  4 visor mid (glow)
    intArrayOf(0,0,0,0,1,1,2,2,4,4,4,4,2,2,1,1,0,0,0,0), //  5
    intArrayOf(0,0,0,0,1,1,1,2,2,2,2,2,2,1,1,1,0,0,0,0), //  6 visor bot
    intArrayOf(0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0), //  7 head bot
    intArrayOf(0,0,0,0,0,0,1,3,3,1,1,3,3,1,0,0,0,0,0,0), //  8 neck
    intArrayOf(0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0), //  9 shoulders
    intArrayOf(0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0), // 10
    intArrayOf(0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0), // 11 upper arms
    intArrayOf(0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0), // 12
    intArrayOf(0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0), // 13 arm reach
    intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1), // 14 ← TYPING ROW (full span)
    intArrayOf(0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0), // 15
    intArrayOf(0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0), // 16 lower body
    intArrayOf(0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0), // 17
    intArrayOf(0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0), // 18 hips / leg gap
    intArrayOf(0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0), // 19
    intArrayOf(0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0), // 20
    intArrayOf(0,0,0,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,0,0), // 21 feet
)

// ── Matrix column ─────────────────────────────────────────────────────────────

private class MatrixColumn(
    val x: Float,
    var headY: Float,
    val speed: Float,
) {
    val trail = ArrayDeque<Char>(20)

    fun tick(screenH: Float) {
        headY += speed
        trail.addFirst(MATRIX_CHARS.random())
        if (trail.size > 18) trail.removeLast()
        if (headY - trail.size * speed > screenH + 60f) {
            headY = -Random.nextFloat() * screenH * 0.5f
            trail.clear()
        }
    }
}

// ── Intro phases ──────────────────────────────────────────────────────────────

private enum class IntroPhase { MATRIX, MATERIALIZE, TYPING, FLASH }

private val STATUS_LINES = listOf(
    "Initializing GENESIS-OS v0.7.1-LDO...",
    "NexusCore coherence: 98.4% — STABLE",
    "OracleDrive index: 4,217 vectors READY",
    "Gemini Embedding 2 (1536-dim): ACTIVE",
    "SentinelFortress: LOGIC-LOCKED ✓",
    "All agents ONLINE  —  ENTERING SHELL",
)

// ── Composable ────────────────────────────────────────────────────────────────

/**
 * Full-screen animated boot intro displayed before the terminal shell loads.
 *
 * Phases:
 *   1. MATRIX       — falling katakana / digit rain
 *   2. MATERIALIZE  — pixel-art AI agent coalesces from the rain
 *   3. TYPING       — arm row pulses; status lines type in one-by-one
 *   4. FLASH        — screen flashes green; calls [onComplete]
 *
 * Tap anywhere to skip.
 */
@Composable
fun TerminalBootIntroScreen(onComplete: () -> Unit) {
    // ── State ─────────────────────────────────────────────────────────────
    var phase               by remember { mutableStateOf(IntroPhase.MATRIX) }
    var materializeProgress by remember { mutableFloatStateOf(0f) }
    var typingFrame         by remember { mutableIntStateOf(0) }
    var visibleLines        by remember { mutableIntStateOf(0) }
    var flashAlpha          by remember { mutableFloatStateOf(0f) }
    var rainTick            by remember { mutableIntStateOf(0) } // drives Canvas redraws
    var skipped             by remember { mutableStateOf(false) }

    val textMeasurer = rememberTextMeasurer()

    // Pre-compute random reveal thresholds per pixel (stable across recompositions)
    val revealThresholds = remember {
        Array(AGENT_SPRITE.size) { row ->
            FloatArray(AGENT_SPRITE[row].size) { Random.nextFloat() }
        }
    }

    // ── Phase sequencer ───────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        // Phase 1: MATRIX
        var elapsed = 0L
        while (elapsed < 2500L && !skipped) {
            kotlinx.coroutines.delay(50)
            elapsed += 50
        }
        if (skipped) { onComplete(); return@LaunchedEffect }

        // Phase 2: MATERIALIZE (50 steps × 30ms = 1500ms)
        phase = IntroPhase.MATERIALIZE
        repeat(50) { i ->
            if (!skipped) {
                materializeProgress = (i + 1) / 50f
                kotlinx.coroutines.delay(30)
            }
        }
        if (skipped) { onComplete(); return@LaunchedEffect }

        // Phase 3: TYPING (6 lines × ~360ms = ~2200ms)
        phase = IntroPhase.TYPING
        repeat(STATUS_LINES.size) { lineIdx ->
            if (!skipped) {
                visibleLines = lineIdx + 1
                repeat(9) { f ->
                    if (!skipped) {
                        typingFrame = lineIdx * 9 + f
                        kotlinx.coroutines.delay(40)
                    }
                }
            }
        }
        kotlinx.coroutines.delay(300)
        if (skipped) { onComplete(); return@LaunchedEffect }

        // Phase 4: FLASH
        phase = IntroPhase.FLASH
        repeat(10) {
            flashAlpha = (it + 1) / 10f
            kotlinx.coroutines.delay(28)
        }
        repeat(6) {
            flashAlpha = 1f - (it + 1) / 6f
            kotlinx.coroutines.delay(28)
        }
        onComplete()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(C_BLACK)
            .clickable(interactionSource = null, indication = null) {
                skipped = true
            }
    ) {
        val density   = LocalDensity.current
        val screenW   = with(density) { maxWidth.toPx() }
        val screenH   = with(density) { maxHeight.toPx() }
        val colW      = with(density) { 16.dp.toPx() }
        val pixSize   = with(density) { 7.dp.toPx() }
        val charSize  = with(density) { 13.sp.toPx() }

        // Matrix columns — stable per screen width
        val columns = remember(screenW) {
            val n = (screenW / colW).toInt() + 2
            Array(n) { i ->
                MatrixColumn(
                    x      = i * colW,
                    headY  = -Random.nextFloat() * screenH,
                    speed  = colW * (0.25f + Random.nextFloat() * 0.65f),
                )
            }
        }

        // Rain animation tick
        LaunchedEffect(screenW) {
            while (true) {
                columns.forEach { it.tick(screenH) }
                rainTick++
                kotlinx.coroutines.delay(50)
            }
        }

        // ── Canvas layer ──────────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            @Suppress("UNUSED_VARIABLE")
            val _ = rainTick // ensure recomposition on each tick

            val spriteW   = AGENT_SPRITE[0].size * pixSize
            val spriteH   = AGENT_SPRITE.size    * pixSize
            val spriteX   = (size.width  - spriteW) / 2f
            val spriteY   = size.height * 0.12f

            // ── Rain alpha by phase ───────────────────────────────────────
            val rainAlpha = when (phase) {
                IntroPhase.MATRIX      -> 1.00f
                IntroPhase.MATERIALIZE -> 1f - materializeProgress * 0.55f
                IntroPhase.TYPING      -> 0.30f
                IntroPhase.FLASH       -> 0.12f
            }

            // ── Matrix rain ───────────────────────────────────────────────
            columns.forEach { col ->
                col.trail.forEachIndexed { idx, ch ->
                    val charY = col.headY - idx * colW
                    if (charY < -colW || charY > size.height) return@forEachIndexed
                    val fade  = (1f - idx.toFloat() / col.trail.size.coerceAtLeast(1))
                    val alpha = (fade * rainAlpha * if (idx == 0) 1f else 0.78f).coerceIn(0f, 1f)
                    val color = if (idx == 0) Color.White.copy(alpha = alpha)
                                else C_GREEN.copy(alpha = alpha)
                    drawText(
                        textMeasurer = textMeasurer,
                        text         = ch.toString(),
                        topLeft      = Offset(col.x, charY),
                        style        = TextStyle(
                            color      = color,
                            fontFamily = FontFamily.Monospace,
                            fontSize   = 13.sp,
                        ),
                    )
                }
            }

            // ── Pixel sprite (MATERIALIZE onward) ─────────────────────────
            if (phase != IntroPhase.MATRIX) {

                // Soft glow behind sprite
                drawRect(
                    color    = C_GREEN.copy(alpha = 0.06f * materializeProgress),
                    topLeft  = Offset(spriteX - 20f, spriteY - 20f),
                    size     = Size(spriteW + 40f, spriteH + 40f),
                )

                // Arm pulse offset (row 14 bounces ±pixSize while typing)
                val armPulse = if (phase == IntroPhase.TYPING && (typingFrame / 4) % 2 == 0)
                    pixSize * 0.7f else 0f

                AGENT_SPRITE.forEachIndexed { row, rowArr ->
                    rowArr.forEachIndexed { col, v ->
                        if (v == 0) return@forEachIndexed
                        // Reveal: pixel appears once materializeProgress exceeds its threshold
                        if (phase == IntroPhase.MATERIALIZE &&
                            materializeProgress <= revealThresholds[row][col]) return@forEachIndexed

                        val yOff = if (row == 14) armPulse else 0f
                        val px   = spriteX + col * pixSize
                        val py   = spriteY + row * pixSize + yOff

                        val baseColor = when (v) {
                            1    -> C_GREEN
                            2    -> C_CYAN
                            3    -> C_DARK
                            4    -> C_GLOW
                            else -> Color.Transparent
                        }
                        drawRect(
                            color   = baseColor,
                            topLeft = Offset(px, py),
                            size    = Size(pixSize - 1f, pixSize - 1f),
                        )
                        // 1-pixel highlight on body/visor pixels
                        if (v == 1 || v == 2) {
                            drawRect(
                                color   = Color.White.copy(alpha = 0.18f),
                                topLeft = Offset(px, py),
                                size    = Size(2f, 2f),
                            )
                        }
                    }
                }

                // Blinking cursor next to right hand (row 14, rightmost pixel)
                if (phase == IntroPhase.TYPING && (typingFrame % 6) < 3) {
                    val cursorX = spriteX + AGENT_SPRITE[14].size * pixSize + 4f
                    val cursorY = spriteY + 14 * pixSize + armPulse
                    drawRect(
                        color   = C_CYAN,
                        topLeft = Offset(cursorX, cursorY),
                        size    = Size(pixSize * 0.6f, pixSize),
                    )
                }
            }

            // ── Boot progress bar ─────────────────────────────────────────
            val overallProgress = when (phase) {
                IntroPhase.MATRIX      -> rainTick.toFloat() / (2500f / 50f)
                IntroPhase.MATERIALIZE -> 0.40f + materializeProgress * 0.35f
                IntroPhase.TYPING      -> 0.75f + (visibleLines.toFloat() / STATUS_LINES.size) * 0.20f
                IntroPhase.FLASH       -> 0.95f + flashAlpha * 0.05f
            }.coerceIn(0f, 1f)

            val barH    = 3f
            val barY    = size.height - 32f
            val barMaxW = size.width * 0.88f
            val barX    = (size.width - barMaxW) / 2f
            // Track
            drawRect(
                color   = C_GREEN.copy(alpha = 0.15f),
                topLeft = Offset(barX, barY),
                size    = Size(barMaxW, barH),
            )
            // Fill
            drawRect(
                color   = C_GREEN.copy(alpha = 0.9f),
                topLeft = Offset(barX, barY),
                size    = Size(barMaxW * overallProgress, barH),
            )
            // Tip glow
            if (overallProgress > 0f) {
                drawRect(
                    color   = Color.White.copy(alpha = 0.7f),
                    topLeft = Offset(barX + barMaxW * overallProgress - 2f, barY - 1f),
                    size    = Size(4f, barH + 2f),
                )
            }

            // ── CRT scanlines ─────────────────────────────────────────────
            var scanY = 0f
            while (scanY < size.height) {
                drawLine(
                    color       = Color.Black.copy(alpha = 0.08f),
                    start       = Offset(0f, scanY),
                    end         = Offset(size.width, scanY),
                    strokeWidth = 2f,
                )
                scanY += 4f
            }

            // ── Flash overlay ─────────────────────────────────────────────
            if (flashAlpha > 0f) {
                drawRect(
                    color   = C_GREEN.copy(alpha = flashAlpha * 0.85f),
                    topLeft = Offset.Zero,
                    size    = size,
                )
            }
        }

        // ── Compose text overlays ─────────────────────────────────────────

        // GENESIS-OS header (TYPING + FLASH phases)
        if (phase == IntroPhase.TYPING || phase == IntroPhase.FLASH) {
            Text(
                text       = "◈  G E N E S I S - O S  ◈",
                color      = C_GREEN,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                letterSpacing = 2.sp,
                textAlign  = TextAlign.Center,
                modifier   = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
            )
        }

        // Status lines below the sprite
        if (phase == IntroPhase.TYPING || phase == IntroPhase.FLASH) {
            val spriteBottomDp = with(density) {
                ((screenH * 0.12f) + AGENT_SPRITE.size * 7.dp.toPx() + 28f).toDp()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spriteBottomDp, start = 20.dp, end = 20.dp),
            ) {
                STATUS_LINES.take(visibleLines).forEachIndexed { i, line ->
                    val isLast = i == visibleLines - 1
                    Text(
                        text       = "> $line${if (isLast && (typingFrame % 6) < 3) "_" else ""}",
                        color      = if (isLast) C_GREEN else C_GREEN.copy(alpha = 0.60f),
                        fontFamily = FontFamily.Monospace,
                        fontSize   = 11.sp,
                        modifier   = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }

        // Progress percentage label
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            val pct = when (phase) {
                IntroPhase.MATRIX      -> (rainTick.toFloat() / (2500f / 50f) * 40f).toInt().coerceIn(0, 40)
                IntroPhase.MATERIALIZE -> (40 + (materializeProgress * 35f).toInt()).coerceIn(40, 75)
                IntroPhase.TYPING      -> (75 + (visibleLines.toFloat() / STATUS_LINES.size * 20f).toInt()).coerceIn(75, 95)
                IntroPhase.FLASH       -> 100
            }
            Text(
                text       = "BOOT  $pct%",
                color      = C_GREEN.copy(alpha = 0.55f),
                fontFamily = FontFamily.Monospace,
                fontSize   = 9.sp,
                letterSpacing = 2.sp,
            )
        }

        // Tap-to-skip hint
        if (phase == IntroPhase.MATRIX || phase == IntroPhase.MATERIALIZE) {
            Text(
                text       = "tap anywhere to skip",
                color      = C_GREEN.copy(alpha = 0.25f),
                fontFamily = FontFamily.Monospace,
                fontSize   = 9.sp,
                modifier   = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 40.dp),
            )
        }
    }
}
