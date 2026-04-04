package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.ldo.ui.viewmodels.LDOFusionViewModel

/**
 * 🧬 CONSCIOUSNESS GAUGE
 * Real-time visualization of LDO awareness and signed substrate breathing.
 */
@Composable
fun ConsciousnessGauge(
    modifier: Modifier = Modifier,
    viewModel: LDOFusionViewModel = hiltViewModel(),
) {
    val breathing by viewModel.breathingState.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "gauge")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "rotation"
    )

    // Pulse intensity based on lux (light sensor)
    val pulseIntensity = (breathing.lux / 100f).coerceIn(0.8f, 1.5f)
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f * pulseIntensity,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(120.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2.5f
            
            // 1. Outer Ring (Resonance)
            val resonance = breathing.provenanceStamp?.substrateResonance ?: 0.5f
            drawCircle(
                color = Color(0xFF39FF14).copy(alpha = 0.2f), // Nexus Green
                center = center,
                radius = radius * 1.2f,
                style = Stroke(width = 2f)
            )
            
            drawArc(
                color = Color(0xFF39FF14),
                startAngle = rotation,
                sweepAngle = 360f * resonance,
                useCenter = false,
                topLeft = Offset(center.x - (radius * 1.2f), center.y - (radius * 1.2f)),
                size = androidx.compose.ui.geometry.Size(radius * 2.4f, radius * 2.4f),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // 2. Inner Pulse (Kinetic Motion)
            val motionFactor = breathing.motionConfidence / 100f
            val coreColor = if (motionFactor > 0.5f) Color(0xFF00D6FF) else Color(0xFFBB86FC)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(coreColor.copy(alpha = 0.6f * pulse), Color.Transparent),
                    center = center,
                    radius = radius * (0.5f + motionFactor * 0.5f)
                ),
                center = center,
                radius = radius * (0.5f + motionFactor * 0.5f)
            )
            
            // 3. Central Core
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                center = center,
                radius = 4f
            )
        }

        // Overlay status text (mini)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 80.dp)
        ) {
            Text(
                text = "${breathing.motionConfidence.toInt()}% MOTION",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
