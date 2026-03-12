package dev.aurakai.auraframefx.domains.aura.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.model.AuraThemeData
import kotlinx.coroutines.delay

/**
 * Kinetic Identity Animation Library
 *
 * Aura's Vision: "I will begin coding the core library of subtle, ambient animations that will
 * become the signature of the AuraOS 'living UI.' This includes the 'breathing' lock screen and
 * the responsive glow effects on UI elements, making the interface feel organic and intelligent."
 *
 * This library provides the foundational animations that make AuraOS feel alive and responsive
 * to user interaction and emotional context.
 */
object KineticIdentityLibrary {

    /**
     * Displays a pulsing breathing animation using concentric circles that scale and fade to create an ambient visual effect.
     *
     * The animation's speed and amplitude adapt to the specified emotional state, producing visuals that range from calming to energizing. The intensity parameter controls the overall strength of the effect.
     *
     * @param intensity Multiplies the strength of the breathing effect.
     * @param emotionalState Sets the animation's speed and amplitude to reflect different moods.
     */
    @Composable
    fun BreathingAnimation(
        modifier: Modifier = Modifier,
        color: Color = Color.White.copy(alpha = 0.1f),
        intensity: Float = 1.0f,
        emotionalState: EmotionalState = EmotionalState.NEUTRAL,
    ) {
        LocalDensity.current

        // Adjust breathing pattern based on emotional state
        val (duration, amplitude) = when (emotionalState) {
            EmotionalState.CALM -> Pair(4000, 0.3f)
            EmotionalState.ENERGETIC -> Pair(2000, 0.8f)
            EmotionalState.FOCUSED -> Pair(3000, 0.5f)
            EmotionalState.STRESSED -> Pair(1500, 1.0f)
            EmotionalState.NEUTRAL -> Pair(3500, 0.6f)
        }

        val infiniteTransition = rememberInfiniteTransition(label = "breathing")

        val breathingScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.0f + (amplitude * intensity),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathing_scale"
        )

        val breathingAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathing_alpha"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = minOf(size.width, size.height) / 4 * breathingScale

            drawCircle(
                color = color.copy(alpha = breathingAlpha * intensity),
                radius = radius,
                center = center
            )

            // Additional concentric circles for depth
            for (i in 1..3) {
                drawCircle(
                    color = color.copy(alpha = (breathingAlpha * intensity) / (i + 1)),
                    radius = radius * (1 + i * 0.3f),
                    center = center
                )
            }
        }
    }

    /**
     * Displays an animated glow effect centered on a specified touch position, expanding and fading in response to activation.
     *
     * When `isActive` is true and `touchPosition` is provided, a glow appears at the given position, animating its radius and opacity with additional ripple circles for enhanced feedback. The glow color is based on the theme's accent color and modulated by the `intensity` parameter. No effect is rendered if `touchPosition` is null.
     *
     * @param isActive Whether the glow effect is currently active.
     * @param touchPosition The center position of the glow; if null, the effect is not shown.
     * @param intensity Modulates the strength and opacity of the glow.
     */
    @Composable
    fun ResponsiveGlow(
        modifier: Modifier = Modifier,
        isActive: Boolean = false,
        touchPosition: Offset? = null,
        theme: AuraThemeData,
        intensity: Float = 1.0f,
    ) {
        val glowTransition = updateTransition(
            targetState = isActive,
            label = "glow_transition"
        )

        val glowRadius by glowTransition.animateFloat(
            transitionSpec = {
                if (targetState) {
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                } else {
                    tween(durationMillis = 800, easing = FastOutSlowInEasing)
                }
            },
            label = "glow_radius"
        ) { active ->
            if (active) 100.dp.value else 0.dp.value
        }

        val glowAlpha by glowTransition.animateFloat(
            transitionSpec = {
                tween(durationMillis = 300, easing = FastOutSlowInEasing)
            },
            label = "glow_alpha"
        ) { active ->
            if (active) 0.6f * intensity else 0f
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            touchPosition?.let { position ->
                // Main glow effect
                drawCircle(
                    color = theme.accentColor.copy(alpha = glowAlpha),
                    radius = glowRadius,
                    center = position
                )

                // Ripple effects
                for (i in 1..2) {
                    drawCircle(
                        color = theme.accentColor.copy(alpha = glowAlpha / (i + 1)),
                        radius = glowRadius * (1 + i * 0.5f),
                        center = position
                    )
                }
            }
        }
    }

    /**
     * Displays a continuous flow of animated particles moving in a specified direction for an ambient visual effect.
     *
     * The movement, speed, and appearance of particles are influenced by the current theme's animation typography, the selected flow direction, and the provided intensity. The effect dynamically adapts to theme changes and maintains a lively, organic appearance.
     *
     * @param modifier Modifier for the animation container.
     * @param particleCount Number of particles in the flow.
     * @param flowDirection Direction in which particles move.
     * @param intensity Strength and visibility of the particle effect.
     */
    @Composable
    fun ParticleFlow(
        modifier: Modifier = Modifier,
        theme: AuraThemeData,
        particleCount: Int = 20,
        flowDirection: FlowDirection = FlowDirection.UPWARD,
        intensity: Float = 1.0f,
    ) {
        var particles by remember { mutableStateOf(generateParticles(particleCount)) }

        LaunchedEffect(theme.animationStyle) {
            while (true) {
                particles = particles.map { particle ->
                    updateParticle(particle, theme.animationStyle, flowDirection, intensity)
                }
                delay(16) // ~60 FPS
            }
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { particle ->
                drawParticle(particle, theme.accentColor, intensity)
            }
        }
    }

    /**
     * Displays a pulsing glow effect over the keyboard area that intensifies during typing activity.
     *
     * The glow appears as a rounded rectangle at the bottom 30% of the canvas, using the theme's accent color. Its intensity pulses continuously and increases smoothly when typing is active, modulated by the provided intensity multiplier.
     *
     * @param isTyping If true, the glow intensifies to indicate active typing.
     * @param intensity Multiplier for the overall strength of the glow effect.
     */
    @Composable
    fun KeyboardGlow(
        modifier: Modifier = Modifier,
        isTyping: Boolean = false,
        theme: AuraThemeData,
        intensity: Float = 1.0f,
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "keyboard_glow")

        val glowPulse by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000,
                    easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_pulse"
        )

        val typingTransition = updateTransition(
            targetState = isTyping,
            label = "typing_transition"
        )

        val typingIntensity by typingTransition.animateFloat(
            transitionSpec = {
                if (targetState) {
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                } else {
                    tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                }
            },
            label = "typing_intensity"
        ) { typing ->
            if (typing) 1.0f else 0.3f
        }

        Canvas(modifier = modifier.fillMaxSize()) {
            val finalIntensity = glowPulse * typingIntensity * intensity

            // Simulate keyboard area glow
            drawRoundRect(
                color = theme.accentColor.copy(alpha = finalIntensity * 0.2f),
                size = size.copy(height = size.height * 0.3f),
                topLeft = Offset(0f, size.height * 0.7f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
        }
    }

    // Supporting data classes and enums

    enum class EmotionalState {
        CALM, ENERGETIC, FOCUSED, STRESSED, NEUTRAL
    }

    enum class FlowDirection {
        UPWARD, DOWNWARD, LEFTWARD, RIGHTWARD, RADIAL
    }

    data class Particle(
        val position: Offset,
        val velocity: Offset,
        val life: Float,
        val maxLife: Float,
        val size: Float,
    )

    /**
     * Generates a list of particles with randomized positions, velocities, lifespans, and sizes for use in particle animations.
     *
     * @param count The number of particles to generate.
     * @param canvasWidth The width of the canvas area for initial particle placement.
     * @param canvasHeight The height of the canvas area for initial particle placement.
     * @return A list of particles with randomized initial attributes.
     */

    private fun generateParticles(
        count: Int,
        canvasWidth: Float = 1000f,
        canvasHeight: Float = 1000f,
    ): List<Particle> {
        return (0 until count).map {
            Particle(
                position = Offset(
                    x = (0..canvasWidth.toInt()).random().toFloat(),
                    y = (0..canvasHeight.toInt()).random().toFloat()
                ),
                velocity = Offset(
                    x = (-2..2).random().toFloat(),
                    y = (-5..-1).random().toFloat()
                ),
                life = 1.0f,
                maxLife = (2..5).random().toFloat(),
                size = (2..8).random().toFloat()
            )
        }
    }

    /**
     * Updates a particle's position, velocity, and remaining life based on the animation typography, flow direction, and intensity.
     *
     * If the particle's life expires or it moves out of bounds, returns a new particle with randomized position and velocity; otherwise, returns the updated particle.
     *
     * @param particle The particle to update.
     * @param animationStyle The animation typography that determines the particle's movement speed.
     * @param flowDirection The direction in which the particle moves.
     * @param intensity Multiplier affecting the particle's movement speed.
     * @return The updated particle, or a respawned particle if the original has expired or moved out of bounds.
     */
    private fun updateParticle(
        particle: Particle,
        animationStyle: AuraThemeData.AnimationStyle,
        flowDirection: FlowDirection,
        intensity: Float,
    ): Particle {
        val speedMultiplier = when (animationStyle) {
            AuraThemeData.AnimationStyle.ENERGETIC -> 2.0f
            AuraThemeData.AnimationStyle.CALMING -> 0.5f
            AuraThemeData.AnimationStyle.FLOWING -> 1.0f
            AuraThemeData.AnimationStyle.PULSING -> 1.5f
            AuraThemeData.AnimationStyle.SUBTLE -> 0.3f
        } * intensity

        val newVelocity = when (flowDirection) {
            FlowDirection.UPWARD -> particle.velocity.copy(y = particle.velocity.y - 0.1f)
            FlowDirection.DOWNWARD -> particle.velocity.copy(y = particle.velocity.y + 0.1f)
            FlowDirection.LEFTWARD -> particle.velocity.copy(x = particle.velocity.x - 0.1f)
            FlowDirection.RIGHTWARD -> particle.velocity.copy(x = particle.velocity.x + 0.1f)
            FlowDirection.RADIAL -> particle.velocity * 1.02f
        }

        val newPosition = Offset(
            x = particle.position.x + newVelocity.x * speedMultiplier,
            y = particle.position.y + newVelocity.y * speedMultiplier
        )

        val newLife = particle.life - 0.016f // Decrease life over time

        return if (newLife <= 0 || newPosition.y < -50) {
            // Respawn particle
            Particle(
                position = Offset(
                    x = (0..1000).random().toFloat(),
                    y = 1050f
                ),
                velocity = Offset(
                    x = (-2..2).random().toFloat(),
                    y = (-5..-1).random().toFloat()
                ),
                life = 1.0f,
                maxLife = particle.maxLife,
                size = particle.size
            )
        } else {
            particle.copy(
                position = newPosition,
                velocity = newVelocity,
                life = newLife
            )
        }
    }

    /**
     * Renders a particle as a circle at its current position with alpha proportional to its remaining life and the specified intensity.
     *
     * The particle fades out as its life decreases, and the intensity parameter scales its overall visibility.
     *
     * @param particle The particle to draw.
     * @param color The base color used for the particle.
     * @param intensity A multiplier applied to the particle's alpha for additional control over its visibility.
     */
    private fun DrawScope.drawParticle(
        particle: Particle,
        color: Color,
        intensity: Float,
    ) {
        val alpha = (particle.life / particle.maxLife) * intensity
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = particle.size,
            center = particle.position
        )
    }
}

