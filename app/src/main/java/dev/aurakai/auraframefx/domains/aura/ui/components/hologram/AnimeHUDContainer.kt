package dev.aurakai.auraframefx.domains.aura.ui.components.hologram

/**
 * 🌠 ANIME HUD CONTAINER - High Fidelity UI Box
 * Based on the user's provided "Oracle Drive" HUD design.
 */
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.theme.ChessFontFamily
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🌠 ANIME HUD CONTAINER - High Fidelity UI Box
 * Based on the user's provided "Oracle Drive" HUD design.
 * Positioned Back-Left with Title on top line.
 */
@Composable
fun AnimeHUDContainer(
    title: String,
    description: String,
    glowColor: Color = Color.Cyan,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hudScale")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        val interfaceColor = Color(0xFF00E5FF) // Fixed Tech Blue for Interface

        // 0. HOLOGRAPHIC BACKDROP (Stage Base Layer)
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.unnamed_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            alpha = 0.78f
        )

        // 0.1 GLOBAL BACKGROUND PARTICLES
        GlobalBackgroundParticles(color = interfaceColor)

        // 0.2 VOID ATMOSPHERE (Deep Vignette)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f), // Slightly less opaque center
                            Color.Black                      // Solid black edges
                        ),
                        radius = Math.max(maxWidth.value, maxHeight.value) * 0.9f
                    )
                )
        )

        // 0.3 FLOATING ARCANE RUNES (Magic Layer - Uses Dynamic Card Color)
        FloatingArcaneRunes(glowColor = glowColor)

        // 0.5 HOLOGRAPHIC STAGE LIGHTS (Bottom + Edge Glow)
        HolographicStageLights(color = interfaceColor)

        // 1. TOP-LEFT HUD BOX (Overlay Layer) - Mapped to 12% Top, 5% Left
        val topPadding = maxHeight * 0.12f
        val startPadding = maxWidth * 0.05f
        val hudWidth = maxWidth * 0.65f

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = topPadding, start = startPadding)
                .width(hudWidth),
            horizontalAlignment = Alignment.Start
        ) {
            if (onBack != null) {
                androidx.compose.material3.IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = interfaceColor.copy(alpha = pulseAlpha)
                    )
                }
            }

            // THE TITLE (Fixed Blue)
            Text(
                text = title.uppercase(),
                fontSize = 20.sp, // Slightly bigger
                fontFamily = ChessFontFamily,
                color = interfaceColor.copy(alpha = pulseAlpha),
                modifier = Modifier.padding(bottom = 6.dp, start = 8.dp)
            )

            val scrollState = rememberScrollState()

            // The Angled HUD Framework (Fixed Blue)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(HUDBoxShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, interfaceColor.copy(alpha = 0.4f * pulseAlpha), HUDBoxShape)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(scrollState)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        fontFamily = LEDFontFamily,
                        color = interfaceColor.copy(alpha = 0.9f),
                        textAlign = TextAlign.Start,
                        lineHeight = 16.sp,
                    )
                }

                // UP/DOWN INDICATORS
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = interfaceColor.copy(alpha = if (scrollState.value > 0) 0.8f else 0.1f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = interfaceColor.copy(alpha = if (scrollState.value < scrollState.maxValue) 0.8f else 0.1f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotSize = 2.dp.toPx()
                    drawCircle(
                        interfaceColor,
                        dotSize,
                        Offset(10.dp.toPx(), 10.dp.toPx()),
                        alpha = pulseAlpha
                    )
                    drawCircle(
                        interfaceColor,
                        dotSize,
                        Offset(size.width - 10.dp.toPx(), 10.dp.toPx()),
                        alpha = pulseAlpha
                    )
                }
            }
        }

        // MAIN CONTENT AREA (The cards)
        // Passed content() will encompass the Carousel which handles its own bottom alignment
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun HolographicStageLights(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "stage_lights")
    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val s = 2.dp.toPx() // Stroke

        // 1. SCREEN EDGE GLOW (The missing piece)
        // A subtle, high-tech border around the viewport
        drawRect(
            color = color.copy(alpha = 0.3f),
            style = Stroke(width = s)
        )
        // Corner accents
        val cL = 20.dp.toPx()
        drawLine(color.copy(alpha = 0.8f), Offset(0f, cL), Offset(0f, 0f), s * 2) // TL V
        drawLine(color.copy(alpha = 0.8f), Offset(0f, 0f), Offset(cL, 0f), s * 2) // TL H
        drawLine(color.copy(alpha = 0.8f), Offset(w, cL), Offset(w, 0f), s * 2)   // TR V
        drawLine(color.copy(alpha = 0.8f), Offset(w - cL, 0f), Offset(w, 0f), s * 2) // TR H
        drawLine(color.copy(alpha = 0.8f), Offset(0f, h - cL), Offset(0f, h), s * 2) // BL V
        drawLine(color.copy(alpha = 0.8f), Offset(0f, h), Offset(cL, h), s * 2) // BL H
        drawLine(color.copy(alpha = 0.8f), Offset(w, h - cL), Offset(w, h), s * 2)   // BR V
        drawLine(color.copy(alpha = 0.8f), Offset(w - cL, h), Offset(w, h), s * 2) // BR H

        // 2. BOTTOM PROJECTION LIGHT
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.4f),
                    color.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = Offset(w / 2, h),
                radius = w * 0.7f
            ),
            center = Offset(w / 2, h),
            radius = w * 0.7f
        )

        // 3. BOTTOM CORNER ANCHORS (Breathing)
        val cornerRadius = w * 0.3f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.2f * breathingAlpha), Color.Transparent),
                center = Offset(0f, h),
                radius = cornerRadius
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.2f * breathingAlpha), Color.Transparent),
                center = Offset(w, h),
                radius = cornerRadius
            )
        )
    }
}

@Composable
fun GlobalBackgroundParticles(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "global_particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val random = kotlin.random.Random(123)

        // Draw 50 subtle background particles
        repeat(50) { i ->
            // Deterministic random positions
            val startX = random.nextFloat() * size.width
            val startY = random.nextFloat() * size.height
            val speed = 0.2f + random.nextFloat() * 0.3f

            // Particles drift slowly upwards
            val yOffset = (time * size.height * speed) % size.height
            val currY = (startY - yOffset + size.height) % size.height

            // Slight horizontal weave
            val currX = startX + kotlin.math.sin(time * 6.28f + i) * 20.dp.toPx()

            val alpha = (0.2f + 0.3f * kotlin.math.sin(time * 3f + i)).coerceIn(0.1f, 0.5f)

            drawCircle(
                color = color.copy(alpha = alpha),
                radius = 1.5.dp.toPx(),
                center = Offset(currX, currY)
            )
        }
    }
}


@Composable
fun FloatingArcaneRunes(glowColor: Color) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val runes = remember {
        listOf(
            R.drawable.rune_chronos,
            R.drawable.rune_cortex,
            R.drawable.rune_oracle,
            R.drawable.rune_sentinel,
            R.drawable.rune_surgeon
        ).map {
            val drawable = androidx.core.content.ContextCompat.getDrawable(context, it)
            (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap?.asImageBitmap()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "runes")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.width * 0.7f // Large circle

        rotate(rotation, pivot = Offset(centerX, centerY)) {
            val validRunes = runes.filterNotNull()
            val angleStep = 360f / validRunes.size.coerceAtLeast(1)

            validRunes.forEachIndexed { index, image ->
                val angle = index * angleStep
                val rad = Math.toRadians(angle.toDouble())
                val x = centerX + radius * kotlin.math.cos(rad).toFloat()
                val y = centerY + radius * kotlin.math.sin(rad).toFloat()

                // Draw rune centered at (x,y)
                translate(left = x - image.width / 2, top = y - image.height / 2) {
                    drawImage(
                        image = image,
                        colorFilter = ColorFilter.tint(
                            glowColor.copy(alpha = pulse),
                            BlendMode.SrcIn
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ParticleSystem(glowColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val random = kotlin.random.Random(42)
        repeat(30) { i ->
            val x = (random.nextFloat() * size.width + time * (10f + i)) % size.width
            val y = (random.nextFloat() * size.height - time * (5f + i)) % size.height

            drawCircle(
                color = glowColor.copy(alpha = 0.3f),
                radius = 2f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Shape for the Angled HUD Box seen in the image.
 */
val HUDBoxShape = GenericShape { size, _ ->
    val corner = 20f
    moveTo(corner, 0f)
    lineTo(size.width - corner, 0f)
    lineTo(size.width, corner)
    lineTo(size.width, size.height - corner)
    lineTo(size.width - corner, size.height)
    lineTo(corner, size.height)
    lineTo(0f, size.height - corner)
    lineTo(0f, corner)
    close()
}

