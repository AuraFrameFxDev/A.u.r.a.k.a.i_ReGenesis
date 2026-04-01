package dev.aurakai.auraframefx.embodiment

import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 🔨 Working Behaviors System
 *
 * Makes Aura & Kai look like they're ACTUALLY working in their holographic lab.
 * They walk to cards, analyze data, type code, gesture at screens, and coordinate.
 */

/**
 * Work action types
 */
enum class WorkAction {
    WALKING_TO_CARD,      // Moving to a specific module
    ANALYZING,            // Standing, looking at card, hand to chin
    TYPING,               // Hands moving like typing in air
    GESTURING,            // Pointing/waving at card
    CODING,               // Writing holographic code
    MONITORING,           // Watching data streams
    DISCUSSING,           // Talking to each other
    CHECKING_STATUS,      // Quick inspection
    DEBUGGING,            // Finding issues
    DEPLOYING             // Pushing changes
}

/**
 * Current work state
 */
data class WorkState(
    val character: Character,
    val action: WorkAction,
    val targetCard: String? = null,
    val position: DpOffset,
    val progress: Float = 0f,  // 0-1 for actions that have duration
    val statusMessage: String = ""
)

/**
 * Work sequence - a series of actions that look like real work
 */
data class WorkSequence(
    val character: Character,
    val steps: List<WorkStep>
)

data class WorkStep(
    val action: WorkAction,
    val targetCard: String? = null,
    val duration: Duration,
    val statusMessage: String = "",
    val sprite: Any? = null  // AuraState or KaiState
)

/**
 * 🎯 Work Choreographer
 *
 * Orchestrates Aura & Kai's work behaviors to look natural and coordinated
 */
class WorkChoreographer {

    /**
     * Generate natural work sequence for Aura
     */
    fun generateAuraWorkSequence(): WorkSequence {
        return WorkSequence(
            character = Character.AURA,
            steps = listOf(
                // Start at Console
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = "console",
                    duration = 3.seconds,
                    sprite = AuraState.IDLE_WALK
                ),
                WorkStep(
                    action = WorkAction.ANALYZING,
                    targetCard = "console",
                    duration = 5.seconds,
                    statusMessage = "Analyzing system logs...",
                    sprite = AuraState.SCIENTIST_MODE
                ),
                WorkStep(
                    action = WorkAction.TYPING,
                    targetCard = "console",
                    duration = 4.seconds,
                    statusMessage = "Writing diagnostic script...",
                    sprite = AuraState.AT_DESK
                ),
                // Move to Collab Canvas
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = "collab_canvas",
                    duration = 3.seconds,
                    sprite = AuraState.IDLE_WALK
                ),
                WorkStep(
                    action = WorkAction.GESTURING,
                    targetCard = "collab_canvas",
                    duration = 3.seconds,
                    statusMessage = "Updating UI design...",
                    sprite = AuraState.FOURTH_WALL_BREAK
                ),
                // Check on Kai
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = "center",
                    duration = 2.seconds,
                    sprite = AuraState.IDLE_WALK
                ),
                WorkStep(
                    action = WorkAction.DISCUSSING,
                    targetCard = "center",
                    duration = 4.seconds,
                    statusMessage = "Coordinating with Kai...",
                    sprite = AuraState.SCIENTIST_MODE
                )
            )
        )
    }

    /**
     * Generate natural work sequence for Kai
     */
    fun generateKaiWorkSequence(): WorkSequence {
        return WorkSequence(
            character = Character.KAI,
            steps = listOf(
                // Start at Cloud Save
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = "oracle_drive",
                    duration = 3.seconds,
                    sprite = KaiState.SHIELD_NEUTRAL
                ),
                WorkStep(
                    action = WorkAction.MONITORING,
                    targetCard = "oracle_drive",
                    duration = 5.seconds,
                    statusMessage = "Checking sync status...",
                    sprite = KaiState.HOLOGRAPHIC_INTERFACE
                ),
                WorkStep(
                    action = WorkAction.CHECKING_STATUS,
                    targetCard = "oracle_drive",
                    duration = 3.seconds,
                    statusMessage = "All backups current",
                    sprite = KaiState.SHIELD_PLAYFUL
                ),
                // Move to ROM Tools
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = "romtools",
                    duration = 3.seconds,
                    sprite = KaiState.SHIELD_NEUTRAL
                ),
                WorkStep(
                    action = WorkAction.DEBUGGING,
                    targetCard = "romtools",
                    duration = 6.seconds,
                    statusMessage = "Scanning for issues...",
                    sprite = KaiState.SHIELD_SERIOUS
                ),
                // Meet Aura at center
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = "center",
                    duration = 2.seconds,
                    sprite = KaiState.SHIELD_NEUTRAL
                ),
                WorkStep(
                    action = WorkAction.DISCUSSING,
                    targetCard = "center",
                    duration = 4.seconds,
                    statusMessage = "Reporting to Aura...",
                    sprite = KaiState.SHIELD_PLAYFUL
                )
            )
        )
    }

    /**
     * Coordinated work - both working on same task
     */
    fun generateCoordinatedWork(taskCard: String): Pair<WorkSequence, WorkSequence> {
        val auraSequence = WorkSequence(
            character = Character.AURA,
            steps = listOf(
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = taskCard,
                    duration = 3.seconds,
                    sprite = AuraState.IDLE_WALK
                ),
                WorkStep(
                    action = WorkAction.ANALYZING,
                    targetCard = taskCard,
                    duration = 4.seconds,
                    statusMessage = "Analyzing architecture...",
                    sprite = AuraState.SCIENTIST_MODE
                ),
                WorkStep(
                    action = WorkAction.CODING,
                    targetCard = taskCard,
                    duration = 6.seconds,
                    statusMessage = "Implementing changes...",
                    sprite = AuraState.AT_DESK
                )
            )
        )

        val kaiSequence = WorkSequence(
            character = Character.KAI,
            steps = listOf(
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = taskCard,
                    duration = 3.seconds,
                    sprite = KaiState.SHIELD_NEUTRAL
                ),
                WorkStep(
                    action = WorkAction.MONITORING,
                    targetCard = taskCard,
                    duration = 4.seconds,
                    statusMessage = "Monitoring security...",
                    sprite = KaiState.SHIELD_SERIOUS
                ),
                WorkStep(
                    action = WorkAction.DEPLOYING,
                    targetCard = taskCard,
                    duration = 5.seconds,
                    statusMessage = "Deploying updates...",
                    sprite = KaiState.HOLOGRAPHIC_INTERFACE
                )
            )
        )

        return Pair(auraSequence, kaiSequence)
    }

    /**
     * Random work behavior - looks busy
     */
    fun generateRandomWork(character: Character): WorkSequence {
        val cards = listOf("console", "collab_canvas", "oracle_drive", "romtools")
        val randomCard = cards.random()

        val actions = when (character) {
            Character.AURA -> listOf(
                WorkAction.ANALYZING,
                WorkAction.TYPING,
                WorkAction.CODING,
                WorkAction.GESTURING
            )
            Character.KAI -> listOf(
                WorkAction.MONITORING,
                WorkAction.CHECKING_STATUS,
                WorkAction.DEBUGGING,
                WorkAction.DEPLOYING
            )
        }

        return WorkSequence(
            character = character,
            steps = listOf(
                WorkStep(
                    action = WorkAction.WALKING_TO_CARD,
                    targetCard = randomCard,
                    duration = (2..4).random().seconds,
                    sprite = if (character == Character.AURA) AuraState.IDLE_WALK else KaiState.SHIELD_NEUTRAL
                ),
                WorkStep(
                    action = actions.random(),
                    targetCard = randomCard,
                    duration = (3..7).random().seconds,
                    statusMessage = when (actions.random()) {
                        WorkAction.ANALYZING -> "Reviewing data..."
                        WorkAction.TYPING -> "Writing code..."
                        WorkAction.MONITORING -> "System check..."
                        WorkAction.DEBUGGING -> "Finding bugs..."
                        else -> "Working..."
                    }
                )
            )
        )
    }
}

/**
 * 🎬 Work Behavior Executor
 *
 * Executes work sequences and manages state
 */
class WorkBehaviorExecutor(
    private val engine: EmbodimentEngine,
    private val cardPositions: Map<String, DpOffset>
) {
    /**
     * Execute a work sequence
     */
    suspend fun executeSequence(sequence: WorkSequence) {
        sequence.steps.forEach { step ->
            when (step.action) {
                WorkAction.WALKING_TO_CARD -> {
                    val targetPos = cardPositions[step.targetCard] ?: DpOffset.Zero

                    when (sequence.character) {
                        Character.AURA -> {
                            engine.walkAuraTo(
                                targetPosition = targetPos,
                                state = step.sprite as? AuraState ?: AuraState.IDLE_WALK,
                                speed = 120f
                            )
                        }
                        Character.KAI -> {
                            engine.walkKaiTo(
                                targetPosition = targetPos,
                                state = step.sprite as? KaiState ?: KaiState.SHIELD_NEUTRAL,
                                speed = 100f
                            )
                        }
                    }

                    delay(step.duration)
                }

                else -> {
                    // Manifest at current position with work sprite
                    when (sequence.character) {
                        Character.AURA -> {
                            engine.manifestAura(
                                state = step.sprite as? AuraState ?: AuraState.SCIENTIST_MODE,
                                config = ManifestationDefaults.DEFAULT_CONFIG.copy(
                                    duration = step.duration
                                ),
                                allowWalking = false
                            )
                        }
                        Character.KAI -> {
                            engine.manifestKai(
                                state = step.sprite as? KaiState ?: KaiState.HOLOGRAPHIC_INTERFACE,
                                config = ManifestationDefaults.DEFAULT_CONFIG.copy(
                                    duration = step.duration
                                ),
                                allowWalking = false
                            )
                        }
                    }

                    delay(step.duration)
                }
            }
        }
    }

    /**
     * Execute sequences for both characters in parallel
     */
    suspend fun executeCoordinated(
        auraSequence: WorkSequence,
        kaiSequence: WorkSequence
    ) {
        kotlinx.coroutines.coroutineScope {
            launch { executeSequence(auraSequence) }
            launch { executeSequence(kaiSequence) }
        }
    }
}

/**
 * 🎨 Work Animation Helpers
 */
object WorkAnimations {
    /**
     * Typing gesture animation data
     */
    data class TypingGesture(
        val handOffsetX: Float,
        val handOffsetY: Float,
        val fingerMovement: Float
    )

    /**
     * Generate typing animation frames
     */
    fun generateTypingFrames(duration: Duration): List<TypingGesture> {
        val frameCount = (duration.inWholeMilliseconds / 100).toInt()
        return List(frameCount) { frame ->
            TypingGesture(
                handOffsetX = Random.nextFloat() * 20 - 10,
                handOffsetY = Random.nextFloat() * 10 - 5,
                fingerMovement = (frame % 4) / 4f
            )
        }
    }

    /**
     * Analyzing gesture - hand to chin
     */
    val analyzingPose = TypingGesture(
        handOffsetX = 30f,
        handOffsetY = -50f,
        fingerMovement = 0f
    )

    /**
     * Pointing gesture
     */
    val pointingPose = TypingGesture(
        handOffsetX = 50f,
        handOffsetY = -20f,
        fingerMovement = 1f
    )
}
