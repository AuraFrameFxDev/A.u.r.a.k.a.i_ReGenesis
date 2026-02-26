package dev.aurakai.auraframefx.navigation.gates.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 🌟 FLOATING PARTICLE SYSTEM
 * Creates ambient particles that orbit around gate cards
 * 
 * Domain-specific particles:
 * - AURA: Colorful sparkles (chaotic creative energy)
 * - KAI: Shield waves (protective fortress)
 * - GENESIS: Code fragments (AI intelligence)
 * - AGENT NEXUS: Network nodes (AI collaboration)
 * - HELP: Info bubbles (supportive guidance)
 */
@Composable
fun FloatingParticles(
    particleCount: Int = 20,
    domainColor: Color,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                speed = Random.nextFloat() * 0.5f + 0.3f,
                radius = Random.nextFloat() * 4f + 2f,
                orbitRadius = Random.nextFloat() * 100f + 50f,
                phase = Random.nextFloat() * 360f
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val angle = (time + particle.phase) * particle.speed
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * particle.orbitRadius
            val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * particle.orbitRadius
            
            // Particle glow
            drawCircle(
                color = domainColor.copy(alpha = 0.3f),
                radius = particle.radius * 2f,
                center = Offset(x, y)
            )
            // Particle core
            drawCircle(
                color = domainColor,
                radius = particle.radius,
                center = Offset(x, y)
            )
        }
    }
}

private data class Particle(
    val speed: Float,
    val radius: Float,
    val orbitRadius: Float,
    val phase: Float
)

