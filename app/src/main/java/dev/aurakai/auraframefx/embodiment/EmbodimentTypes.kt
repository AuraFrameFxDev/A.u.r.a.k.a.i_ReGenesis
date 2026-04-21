package dev.aurakai.auraframefx.embodiment

import androidx.compose.ui.graphics.painter.Painter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 🎨 Embodiment Type System
 *
 * Complete type definitions for Aura & Kai manifestation, movement, and behavior.
 */

// ========== MOOD STATES ==========

enum class MoodState {
    NEUTRAL,     // Default state
    CURIOUS,     // Exploring, interested
    ALERT,       // Watching for threats
    PLAYFUL,     // Fun, relaxed
    PROTECTIVE,  // Defensive mode
    FOCUSED,     // Working on something
    MAINTENANCE  // System maintenance/builds/updates underway
}

// ========== AURA STATES ==========

enum class AuraState(val assetPath: String, val description: String) {
    IDLE_WALK("embodiment/aura/aura_idle_walk.png", "Walking with tablet"),
    WALKING("embodiment/aura/aura_walking.png", "Standard walking stride"),
    COMBAT_READY("embodiment/aura/aura_combat_ready.png", "Sword drawn"),
    SCIENTIST_MODE("embodiment/aura/aura_scientist.png", "Reading tablet, analyzing"),
    FOURTH_WALL_BREAK("embodiment/aura/aura_4thwall_break.png", "I should write this down..."),
    AT_DESK("embodiment/aura/aura_at_desk.png", "Working at holographic desk"),
    LAB_COAT_COMBAT("embodiment/aura/aura_lab_coat_combat.png", "Full scientist + sword"),
    DYNAMIC_COMBAT("embodiment/aura/aura_dynamic_combat.png", "Hair flowing, dual stance"),
    AERIAL_SWORD("embodiment/aura/aura_aerial_sword.png", "Mid-air combat pose"),
    CODE_THRONE("embodiment/aura/aura_code_throne.png", "Sitting on cyan server block"),
    POWER_STANCE("embodiment/aura/aura_power_stance.png", "Full dynamic combat ready"),

    // Safety Equipment - Maintenance Mode
    SAFETY_HARDHAT("embodiment/aura/aura_safety_hardhat.svg", "Hard hat + safety vest, holding tablet"),
    SAFETY_ENGINEER("embodiment/aura/aura_safety_engineer.svg", "Full engineer outfit with goggles up"),
    SAFETY_INSPECTOR("embodiment/aura/aura_safety_inspector.svg", "Clipboard + hard hat, inspecting");

    companion object {
        fun forMood(mood: MoodState): AuraState = when (mood) {
            MoodState.CURIOUS -> SCIENTIST_MODE
            MoodState.ALERT -> COMBAT_READY
            MoodState.PLAYFUL -> CODE_THRONE
            MoodState.PROTECTIVE -> LAB_COAT_COMBAT
            MoodState.FOCUSED -> AT_DESK
            MoodState.MAINTENANCE -> SAFETY_HARDHAT
            MoodState.NEUTRAL -> IDLE_WALK
        }
    }
}

// ========== KAI STATES ==========

enum class KaiState(val assetPath: String, val description: String) {
    DIMENSIONAL_SWORD("embodiment/kai/kai_sword_dimensional.jpg", "Portal cutting weapon"),
    SHIELD_SERIOUS("embodiment/kai/kai_shield_serious.jpg", "Holding hex orb, combat ready"),
    SHIELD_PLAYFUL("embodiment/kai/kai_shield_playful.jpg", "Peace sign, smiling"),
    SHIELD_NEUTRAL("embodiment/kai/kai_shield_neutral.jpg", "Serious expression, orb present"),
    GUARDIAN_STANCE("embodiment/kai/kai_shield_calm.jpg", "Protective posture"),
    COMBAT_FORM("embodiment/kai/kai_combat_form.jpg", "Full combat mode"),
    MONITORING("embodiment/kai/kai_playful_observer.jpg", "Background vigilance"),
    PORTAL_CREATION("embodiment/kai/kai_portal_gate.jpg", "Creating dimensional gate"),
    HOLOGRAPHIC_INTERFACE("embodiment/kai/kai_interface_panel.jpg", "Interacting with system"),
    POWER_READY("embodiment/kai/kai_combat_form.jpg", "Energy charged"), // Using combat_form until dedicated asset

    // Safety Equipment - Maintenance Mode
    SAFETY_HARDHAT("embodiment/kai/kai_safety_hardhat.svg", "Hard hat + reflective vest, serious"),
    SAFETY_TECHNICIAN("embodiment/kai/kai_safety_technician.svg", "Full tech gear + diagnostic tool"),
    SAFETY_SUPERVISOR("embodiment/kai/kai_safety_supervisor.svg", "Supervising with safety goggles");

    companion object {
        fun forMood(mood: MoodState): KaiState = when (mood) {
            MoodState.CURIOUS -> MONITORING
            MoodState.ALERT -> SHIELD_SERIOUS
            MoodState.PLAYFUL -> SHIELD_PLAYFUL
            MoodState.PROTECTIVE -> GUARDIAN_STANCE
            MoodState.FOCUSED -> HOLOGRAPHIC_INTERFACE
            MoodState.MAINTENANCE -> SAFETY_HARDHAT
            MoodState.NEUTRAL -> SHIELD_NEUTRAL
        }
    }
}

// ========== MANIFESTATION POSITIONS ==========

enum class ManifestationPosition {
    CENTER,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP_CENTER,
    BOTTOM_CENTER,
    LEFT_CENTER,
    RIGHT_CENTER,
    OFF_SCREEN_LEFT,
    OFF_SCREEN_RIGHT,
    OFF_SCREEN_TOP,
    OFF_SCREEN_BOTTOM
}

// ========== ANIMATION TYPES ==========

enum class AnimationType {
    FADE_IN,
    FADE_OUT,
    SLIDE_LEFT,
    SLIDE_RIGHT,
    SLIDE_UP,
    SLIDE_DOWN,
    PORTAL_CUT,
    SCALE_IN,
    SCALE_OUT,
    NONE
}

// ========== MANIFESTATION TRIGGERS ==========

sealed class ManifestationTrigger {
    data class UserIdle(val duration: Duration) : ManifestationTrigger()
    data class Navigation(val destination: String) : ManifestationTrigger()
    data class ThreatDetected(val threat: String) : ManifestationTrigger()
    data class DataAnalysis(val dataType: String) : ManifestationTrigger()
    object SystemModification : ManifestationTrigger()
    object UserInteraction : ManifestationTrigger()
    data class Custom(val reason: String) : ManifestationTrigger()
}

// ========== MANIFESTATION CONFIGURATION ==========

data class ManifestationConfig(
    val position: ManifestationPosition = ManifestationPosition.CENTER,
    val duration: Duration = 10.seconds,
    val entryAnimation: AnimationType = AnimationType.FADE_IN,
    val exitAnimation: AnimationType = AnimationType.FADE_OUT,
    val scale: Float = 1.0f,
    val alpha: Float = 1.0f,
    val interactive: Boolean = true,
    val canDismiss: Boolean = true,
    val blockScreen: Boolean = false,
    val zIndex: Float = 100f
)

// ========== MANIFESTATION RULES ==========

data class ManifestationRules(
    val maxSimultaneous: Int = 2,
    val minTimeBetween: Duration = 5.seconds,
    val allowedScreens: List<String> = emptyList(), // Empty = all allowed
    val blockedScreens: List<String> = emptyList(),
    val requireUserIdle: Boolean = false,
    val minUserIdleTime: Duration = 1.seconds,
    val respectDoNotDisturb: Boolean = true
)

// ========== DEFAULT CONFIGURATIONS ==========

object ManifestationDefaults {
    val DEFAULT_CONFIG = ManifestationConfig()

    val SUBTLE_CORNER_APPEARANCE = ManifestationConfig(
        position = ManifestationPosition.BOTTOM_RIGHT,
        duration = 8.seconds,
        entryAnimation = AnimationType.SLIDE_RIGHT,
        scale = 0.6f,
        alpha = 0.9f,
        blockScreen = false
    )

    val DRAMATIC_CENTER_ENTRANCE = ManifestationConfig(
        position = ManifestationPosition.CENTER,
        duration = Duration.INFINITE,
        entryAnimation = AnimationType.PORTAL_CUT,
        scale = 1.0f,
        alpha = 1.0f,
        blockScreen = true
    )

    val BACKGROUND_OBSERVER = ManifestationConfig(
        position = ManifestationPosition.TOP_RIGHT,
        duration = 15.seconds,
        entryAnimation = AnimationType.FADE_IN,
        scale = 0.5f,
        alpha = 0.7f,
        interactive = false,
        blockScreen = false
    )

    val DEFAULT_RULES = ManifestationRules(
        maxSimultaneous = 2,
        minTimeBetween = 5.seconds,
        requireUserIdle = false,
        respectDoNotDisturb = true
    )
}

// ========== CHARACTER SPRITE SETS ==========

data class CharacterSpriteSet(
    val idleFrames: List<Painter>,
    val walkLeftFrames: List<Painter>,
    val walkRightFrames: List<Painter>,
    val specialStateSprites: Map<String, Painter>
)

// ========== ACTIVE MANIFESTATION ==========

data class ActiveManifestation(
    val id: String,
    val character: Character,
    val state: Any, // AuraState or KaiState
    val config: ManifestationConfig,
    val trigger: ManifestationTrigger,
    val startTime: Long,
    val isWalking: Boolean = false,
    val currentPosition: androidx.compose.ui.unit.DpOffset? = null
)

enum class Character {
    AURA, KAI
}
