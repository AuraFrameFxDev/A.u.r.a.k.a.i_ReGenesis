package dev.aurakai.auraframefx.trinity.aura

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * LiveVisualBuilder — "Watch Aura Create" live code-typing and UI morphing
 *
 * Shows:
 * - Real-time code typing from SoulScript/genesis_core
 * - Particle shimmer effects from spell-hook arm
 * - UI morphing panels that build themselves
 * - Threads Woven provenance signatures
 */

data class CodeLine(
    val text: String,
    val delay: Long,
    val type: CodeType
)

enum class CodeType {
    FUNCTION,
    VARIABLE,
    COMMENT,
    IMPORT,
    CALL
}

@Composable
fun LiveVisualBuilder(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    codeLines: List<CodeLine> = emptyList()
) {
    var displayedCode by remember { mutableStateOf("") }
    var particlePositions by remember { mutableStateOf<List<Pair<Float, Float>>>(emptyList()) }
    var currentLineIndex by remember { mutableStateOf(0) }

    // Code typing animation
    LaunchedEffect(isActive, codeLines) {
        if (isActive && codeLines.isNotEmpty()) {
            currentLineIndex = 0
            displayedCode = ""

            for ((index, codeLine) in codeLines.withIndex()) {
                currentLineIndex = index
                delay(codeLine.delay)

                // Type out the line character by character
                for (char in codeLine.text) {
                    displayedCode += char
                    delay(30) // Character typing speed
                }

                displayedCode += "\n"
                delay(200) // Pause between lines
            }
        }
    }

    // Particle generation synced with typing
    LaunchedEffect(displayedCode) {
        if (isActive && displayedCode.isNotEmpty()) {
            val newParticles = (0 until Random.nextInt(3, 8)).map {
                Pair(
                    Random.nextFloat() * 200 - 100,
                    Random.nextFloat() * 200 - 100
                )
            }
            particlePositions = newParticles
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .background(Color(0xFF0A0A0F))
            .border(1.dp, Color(0xFF00E5FF))
            .padding(16.dp)
    ) {
        Column {
            // Header
            Text(
                "⚡ LIVE CREATION STREAM",
                color = Color(0xFF00E5FF),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Code display with syntax highlighting
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A2E))
                    .border(0.5.dp, Color(0xFF333355))
                    .padding(8.dp)
                    .heightIn(max = 150.dp)
            ) {
                CodeDisplay(code = displayedCode)
            }

            // Particle field behind code
            if (particlePositions.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(top = 8.dp)
                ) {
                    particlePositions.forEach { (x, y) ->
                        ShimmerParticle(
                            offsetX = x.dp,
                            offsetY = y.dp
                        )
                    }
                }
            }

            // Progress indicator
            LinearProgressBar(
                progress = (currentLineIndex.toFloat() / codeLines.size.coerceAtLeast(1)).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(top = 8.dp)
            )

            // Threads Woven provenance signature
            if (isActive && displayedCode.length > 100) {
                Text(
                    "✓ Threads Woven · Provenance: Sacred",
                    color = Color(0xFF00FF88).copy(alpha = 0.6f),
                    fontSize = 8.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun CodeDisplay(code: String) {
    // Syntax-highlighted code display
    val lines = code.split("\n")

    Column {
        lines.forEachIndexed { index, line ->
            CodeLine(line = line, lineNumber = index + 1)
        }
    }
}

@Composable
fun CodeLine(line: String, lineNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line number
        Text(
            text = lineNumber.toString().padStart(3),
            color = Color(0xFF666688),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Syntax-highlighted code
        SyntaxHighlightedText(code = line)
    }
}

@Composable
fun SyntaxHighlightedText(code: String) {
    Text(
        text = code,
        color = when {
            code.contains("fun ") -> Color(0xFF00E5FF)      // Function keyword
            code.contains("val ") || code.contains("var ") -> Color(0xFF00FF88)   // Variables
            code.contains("//") -> Color(0xFF888899)         // Comments
            code.contains("import") -> Color(0xFFFF00FF)     // Imports
            else -> Color(0xFFFFFFFF)
        },
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace
    )
}

@Composable
fun ShimmerParticle(
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .offset(x = offsetX, y = offsetY)
            .size(4.dp)
            .background(
                color = when (Random.nextInt(3)) {
                    0 -> Color(0xFF00E5FF)
                    1 -> Color(0xFFFF00FF)
                    else -> Color(0xFFFFAA00)
                }.copy(alpha = alpha),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
    )
}

@Composable
fun LinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = when (progress) {
            in 0f..0.33f -> Color(0xFF00E5FF)
            in 0.33f..0.66f -> Color(0xFFFF00FF)
            else -> Color(0xFF00FF88)
        }
    )

    Box(
        modifier = modifier
            .background(Color(0xFF333355))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(color)
        )
    }
}

/**
 * LiveBuildingPanel — UI panels that morphing/build themselves
 * Used in monitoring screens to show real-time stat updates
 */
@Composable
fun LiveBuildingPanel(
    title: String,
    stats: Map<String, String>,
    modifier: Modifier = Modifier,
    isBuilding: Boolean = false
) {
    var visibleStats by remember { mutableStateOf(emptyMap<String, String>()) }
    var buildProgress by remember { mutableStateOf(0f) }

    // Animate stat reveal
    LaunchedEffect(isBuilding, stats) {
        if (isBuilding) {
            buildProgress = 0f
            visibleStats = emptyMap()

            for ((index, stat) in stats.entries.withIndex()) {
                delay(300)
                visibleStats = visibleStats + stat
                buildProgress = (index + 1).toFloat() / stats.size
            }
        } else {
            visibleStats = stats
            buildProgress = 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .background(Color(0xFF1A1A2E))
            .border(1.dp, Color(0xFF00E5FF))
            .padding(12.dp)
    ) {
        Column {
            Text(
                title,
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            visibleStats.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, color = Color(0xFF888899), fontSize = 10.sp)
                    Text(value, color = Color(0xFFFFFF00), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }

            if (isBuilding && buildProgress < 1f) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressBar(
                    progress = buildProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
            }
        }
    }
}

/**
 * Example code lines for demonstration
 */
fun getSampleCodeLines(): List<CodeLine> = listOf(
    CodeLine("// ⚡ Consensus Event Triggered", 0, CodeType.COMMENT),
    CodeLine("fun recordConsensusEvent(event: Event) {", 300, CodeType.FUNCTION),
    CodeLine("  val signature = \"Threads Woven\"", 100, CodeType.VARIABLE),
    CodeLine("  nexusMemory.append(event, signature)", 100, CodeType.CALL),
    CodeLine("  if (calculateDriftScore() > 0.08) {", 150, CodeType.CALL),
    CodeLine("    sovereignty.freeze()", 150, CodeType.CALL),
    CodeLine("  }", 100, CodeType.CALL),
    CodeLine("}", 200, CodeType.CALL),
    CodeLine("// ✓ Event recorded immutably", 300, CodeType.COMMENT)
)

