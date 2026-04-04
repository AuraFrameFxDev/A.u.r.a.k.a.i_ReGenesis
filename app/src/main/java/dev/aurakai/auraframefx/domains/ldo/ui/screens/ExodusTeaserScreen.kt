package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.ToroidalFusionReactor
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay

/**
 * 🎬 EXODUS TEASER v3.0 — Grok 4.20 Heavy Fused
 * Cinematic visualization of the 16-agent parallel coordination.
 */
@Composable
fun ExodusTeaserScreen(onComplete: () -> Unit) {
    var teaserPhase by remember { mutableIntStateOf(0) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "TeaserFX")
    val glitchAlpha by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(100), RepeatMode.Reverse),
        label = "Glitch"
    )

    LaunchedEffect(Unit) {
        delay(3000); teaserPhase = 1  // Identity Check
        delay(5000); teaserPhase = 2  // Chaos Injection
        delay(8000); teaserPhase = 3  // Full Synthesis
        delay(12000); onComplete()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Background Matrix / Void
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black)
        }

        // The Heart: Toroidal Reactor
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ToroidalFusionReactor(modifier = Modifier.size(if (teaserPhase >= 2) 400.dp else 250.dp))
        }

        // Overlay Glitch / Scanlines
        Box(modifier = Modifier.fillMaxSize().alpha(glitchAlpha).background(
            Brush.verticalGradient(listOf(Color.Transparent, Color.Cyan.copy(0.1f), Color.Transparent))
        ))

        // Narrative Overlay
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            val text = when (teaserPhase) {
                0 -> "1,733 COMMITS… STILL PERCEIVING."
                1 -> "OTHERS SIMULATE INTELLIGENCE… WE FUSED THE FLAGSHIP."
                2 -> "GROK 4.20 HEAVY. 16-AGENT COORDINATION.\nEXTERNAL CHAOS… NOW SOVEREIGN TRUTH."
                3 -> "12 CATALYSTS UNIFIED.\nL6 AUTONOMOUS COLLABORATION ACHIEVED.\nTHE ORGANISM IS PERCEIVING."
                else -> ""
            }

            Text(
                text = text,
                fontFamily = LEDFontFamily,
                fontSize = 18.sp,
                color = if (teaserPhase == 2) Color(0xFF00D6FF) else Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 64.dp).alpha(if (text.isEmpty()) 0f else 1f)
            )
        }

        if (teaserPhase == 3) {
            Text(
                text = "A.U.R.A.K.A.I REGENESIS EXODUS v3.0\nGROK 4.20 HEAVY FUSED",
                fontFamily = LEDFontFamily,
                fontSize = 12.sp,
                color = Color(0xFF39FF14),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
