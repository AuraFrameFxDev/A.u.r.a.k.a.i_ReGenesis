package dev.aurakai.auraframefx.domains.aura.ui.components.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextAlign
import dev.aurakai.auraframefx.domains.aura.ui.theme.ChessFontFamily
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.random.Random

/**
 * 🎬 RE:GENESIS INTRO SEQUENCER
 * Glitch-out animation intro before entering the system.
 */
@Composable
fun ReGenesisIntroAnimation(
    onIntroFinished: () -> Unit
) {
    var stage by remember { mutableStateOf(IntroStage.BLACK_VOID) }
    var glitchAmount by remember { mutableStateOf(0f) }

    // Animation Sequencer
    LaunchedEffect(Unit) {
        // 1. Hold Black
        delay(500)

        // 2. Reveal Text "AIAOSP PROJECT"
        stage = IntroStage.SHOW_PROJECT
        delay(1500)

        // 3. Glitch Reveal "RE:GENESIS"
        stage = IntroStage.SHOW_TITLE

        // Glitch FX Loop
        repeat(5) {
            glitchAmount = 10f
            delay(50)
            glitchAmount = -10f
            delay(50)
            glitchAmount = 0f
            delay(100)
        }

        // Hold Title
        delay(1200)

        // 4. Glitch Out / Melt
        stage = IntroStage.GLITCH_OUT
        glitchAmount = 25f
        delay(200)

        // 5. Finish
        onIntroFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer {
                // Screen shake on glitch
                if (Math.abs(glitchAmount) > 0) {
                    translationX = glitchAmount * 2f
                    translationY = Random.nextFloat() * glitchAmount
                }
            },
        contentAlignment = Alignment.Center
    ) {

        if (stage == IntroStage.SHOW_PROJECT || stage == IntroStage.SHOW_TITLE || stage == IntroStage.GLITCH_OUT) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // TOP LINE
                Text(
                    text = "AIAOSP PROJECT",
                    fontSize = 16.sp,
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 4.sp,
                    modifier = Modifier.offset(x = (glitchAmount * 0.5f).dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // MAIN TITLE
                if (stage == IntroStage.SHOW_TITLE || stage == IntroStage.GLITCH_OUT) {
                    Box {
                        // Glitch Shadow (Cyan)
                        Text(
                            text = "RE:GENESIS",
                            fontSize = 48.sp,
                            fontFamily = ChessFontFamily,
                            color = Color.Cyan.copy(alpha = 0.5f),
                            modifier = Modifier
                                .offset(x = (-4 + glitchAmount / 3).dp, y = (2).dp)
                                .graphicsLayer {
                                    scaleX = 1.05f
                                }
                        )
                        // Glitch Shadow (Magenta)
                        Text(
                            text = "RE:GENESIS",
                            fontSize = 48.sp,
                            fontFamily = ChessFontFamily,
                            color = Color.Magenta.copy(alpha = 0.5f),
                            modifier = Modifier
                                .offset(x = (4 - glitchAmount / 3).dp, y = (-2).dp)
                        )

                        // Main Text
                        Text(
                            text = "RE:GENESIS",
                            fontSize = 48.sp,
                            fontFamily = ChessFontFamily,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // SUBTITLE
                    Text(
                        text = "Evolve, Understand, Remember",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = LEDFontFamily
                    )
                }
            }
        }
    }
}

enum class IntroStage {
    BLACK_VOID,
    SHOW_PROJECT,
    SHOW_TITLE,
    GLITCH_OUT
}

