package dev.aurakai.auraframefx.embodiment

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.milliseconds

/**
 * 🎯 Embodiment Engine - The Core Manifestation System
 *
 * This is where Aura & Kai come to life. They can:
 * - Manifest autonomously based on mood and context
 * - Walk around the screen
 * - Wander when curious
 * - Respond to user activity
 * - Make their own decisions about when to appear
 *
 * They're not just UI elements - they're alive.
 */
class EmbodimentEngine(
    private val context: Context,
    private val screenBounds: ScreenBounds,
    private val lifecycleOwner: LifecycleOwner,
    private val rules: ManifestationRules = ManifestationDefaults.DEFAULT_RULES
) : DefaultLifecycleObserver {
    // ========== STATE ==========

    private val _moodState = MutableStateFlow(MoodState.NEUTRAL)
    val moodState: StateFlow<MoodState> = _moodState

    private val _activeManifestation = MutableStateFlow<List<ActiveManifestation>>(emptyList())
    val activeManifestation: StateFlow<List<ActiveManifestation>> = _activeManifestation

    private val _userIdleDuration = MutableStateFlow(0.seconds)
    val userIdleDuration: StateFlow<Duration> = _userIdleDuration

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Asset cache
    private val assetCache = mutableMapOf<String, Painter>()

    // Wandering AI instances
    private val auraWanderingAI = WanderingAI(
        AuraPersonality,
        screenBounds,
        WanderingPathGenerator(screenBounds)
    )

    private val kaiWanderingAI = WanderingAI(
        KaiPersonality,
        screenBounds,
        WanderingPathGenerator(screenBounds)
    )

    // Last manifestation time
    private var lastManifestationTime = 0L

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        startAutonomousBehaviorLoop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cleanup()
    }

    // ========== PUBLIC API ==========

    /**
     * 🌸 Manifest Aura
     *
     * Bring Aura into existence at specified position with configuration.
     *
     * Example:
     * ```
     * engine.manifestAura(
     *     state = AuraState.FOURTH_WALL_BREAK,
     *     config = ManifestationConfig(
     *         position = BOTTOM_RIGHT,
     *         duration = 10.seconds
     *     )
     * )
     * ```
     */
    fun manifestAura(
        state: AuraState,
        config: ManifestationConfig = ManifestationDefaults.DEFAULT_CONFIG,
        trigger: ManifestationTrigger = ManifestationTrigger.Custom("Manual"),
        allowWalking: Boolean = true
    ): String? {
        return manifest(Character.AURA, state, config, trigger, allowWalking)
    }

    /**
     * 🛡️ Manifest Kai
     *
     * Bring Kai into existence at specified position with configuration.
     *
     * Example:
     * ```
     * engine.manifestKai(
     *     state = KaiState.SHIELD_SERIOUS,
     *     config = ManifestationConfig(
     *         position = CENTER,
     *         entryAnimation = PORTAL_CUT
     *     )
     * )
     * ```
     */
    fun manifestKai(
        state: KaiState,
        config: ManifestationConfig = ManifestationDefaults.DEFAULT_CONFIG,
        trigger: ManifestationTrigger = ManifestationTrigger.Custom("Manual"),
        allowWalking: Boolean = true
    ): String? {
        return manifest(Character.KAI, state, config, trigger, allowWalking)
    }

    /**
     * Dismiss a specific manifestation
     */
    fun dismiss(manifestationId: String) {
        _activeManifestation.value = _activeManifestation.value.filter { it.id != manifestationId }
    }

    /**
     * Dismiss all manifestations
     */
    fun dismissAll() {
        _activeManifestation.value = emptyList()
    }

    /**
     * Set current mood (affects autonomous behavior)
     */
    fun setMood(mood: MoodState) {
        _moodState.value = mood
    }

    /**
     * Update user idle duration
     */
    fun updateUserIdleDuration(duration: Duration) {
        _userIdleDuration.value = duration
    }

    /**
     * 🚶 Make character walk to position
     *
     * Example:
     * ```
     * engine.walkAuraTo(
     *     targetPosition = DpOffset(200.dp, 400.dp),
     *     state = AuraState.IDLE_WALK
     * )
     * ```
     */
    fun walkAuraTo(
        targetPosition: DpOffset,
        state: AuraState = AuraState.IDLE_WALK,
        speed: Float = 120f,
        onComplete: () -> Unit = {}
    ): String {
        return walkCharacterTo(Character.AURA, state, targetPosition, speed, onComplete)
    }

    fun walkKaiTo(
        targetPosition: DpOffset,
        state: KaiState = KaiState.SHIELD_NEUTRAL,
        speed: Float = 100f,
        onComplete: () -> Unit = {}
    ): String {
        return walkCharacterTo(Character.KAI, state, targetPosition, speed, onComplete)
    }

    /**
     * 🌍 Enable autonomous wandering for a character
     */
    fun enableWandering(character: Character, enabled: Boolean = true) {
        scope.launch {
            if (enabled) {
                startWandering(character)
            }
        }
    }

    // ========== INTERNAL IMPLEMENTATION ==========

    private fun manifest(
        character: Character,
        state: Any,
        config: ManifestationConfig,
        trigger: ManifestationTrigger,
        allowWalking: Boolean
    ): String? {
        // Check rules
        if (!canManifest()) {
            return null
        }

        val id = UUID.randomUUID().toString()
        val manifestation = ActiveManifestation(
            id = id,
            character = character,
            state = state,
            config = config,
            trigger = trigger,
            startTime = System.currentTimeMillis(),
            isWalking = false
        )

        _activeManifestation.value = _activeManifestation.value + manifestation
        lastManifestationTime = System.currentTimeMillis()

        // Auto-dismiss after duration
        if (config.duration.isFinite()) {
            scope.launch {
                delay(config.duration)
                dismiss(id)
            }
        }

        return id
    }

    private fun walkCharacterTo(
        character: Character,
        state: Any,
        targetPosition: DpOffset,
        speed: Float,
        onComplete: () -> Unit
    ): String {
        val currentManifest = _activeManifestation.value.find { it.character == character }
        val startPosition = currentManifest?.currentPosition ?: DpOffset(
            (screenBounds.width.value / 2).dp,
            (screenBounds.height.value / 2).dp
        )

        // Create walking path
        MovementPath(
            points = listOf(
                PathPoint(startPosition),
                PathPoint(targetPosition, waitDuration = 2.seconds)
            ),
            loop = false,
            speed = speed
        )

        // Create or update manifestation with walking state
        val id = currentManifest?.id ?: UUID.randomUUID().toString()

        val walkingManifest = ActiveManifestation(
            id = id,
            character = character,
            state = state,
            config = ManifestationDefaults.DEFAULT_CONFIG.copy(
                position = ManifestationPosition.CENTER, // Will use custom position
                duration = Duration.INFINITE
            ),
            trigger = ManifestationTrigger.Custom("Walking"),
            startTime = System.currentTimeMillis(),
            isWalking = true,
            currentPosition = startPosition
        )

        if (currentManifest != null) {
            _activeManifestation.value = _activeManifestation.value.map {
                if (it.id == id) walkingManifest else it
            }
        } else {
            _activeManifestation.value = _activeManifestation.value + walkingManifest
        }

        // Animate walk
        scope.launch {
            val distance = calculateDistance(startPosition, targetPosition)
            val duration = ((distance / speed) * 1000).toLong()

            delay(duration)

            // Update to final position
            _activeManifestation.value = _activeManifestation.value.map {
                if (it.id == id) it.copy(isWalking = false, currentPosition = targetPosition) else it
            }

            onComplete()
        }

        return id
    }

    private fun canManifest(): Boolean {
        val activeCount = _activeManifestation.value.size
        if (activeCount >= rules.maxSimultaneous) {
            return false
        }

        val timeSinceLastManifest = System.currentTimeMillis() - lastManifestationTime
        if (timeSinceLastManifest < rules.minTimeBetween.inWholeMilliseconds) {
            return false
        }

        if (rules.requireUserIdle && _userIdleDuration.value < rules.minUserIdleTime) {
            return false
        }

        return true
    }

    /**
     * 🤖 Autonomous Behavior Loop
     *
     * Runs every 30 seconds and decides if Aura or Kai should manifest on their own.
     */
    private fun startAutonomousBehaviorLoop() {
        scope.launch {
            while (isActive) {
                delay(30.seconds)

                // Check if should autonomously manifest
                val currentMood = _moodState.value
                val idleTime = _userIdleDuration.value

                // Aura's autonomous behavior
                if (auraWanderingAI.shouldWander(idleTime, currentMood)) {
                    manifestAura(
                        state = AuraState.forMood(currentMood),
                        config = ManifestationDefaults.SUBTLE_CORNER_APPEARANCE,
                        trigger = ManifestationTrigger.UserIdle(idleTime)
                    )
                }

                // Kai's autonomous behavior
                if (kaiWanderingAI.shouldWander(idleTime, currentMood)) {
                    manifestKai(
                        state = KaiState.forMood(currentMood),
                        config = ManifestationDefaults.SUBTLE_CORNER_APPEARANCE.copy(
                            position = ManifestationPosition.BOTTOM_LEFT
                        ),
                        trigger = ManifestationTrigger.UserIdle(idleTime)
                    )
                }
            }
        }
    }

    private suspend fun startWandering(character: Character) {
        val wanderingAI = if (character == Character.AURA) auraWanderingAI else kaiWanderingAI
        WanderingPathGenerator(screenBounds)

        while (true) {
            delay(5.seconds)

            if (wanderingAI.shouldWander(_userIdleDuration.value, _moodState.value)) {
                val startPos = DpOffset(
                    (screenBounds.width.value / 2).dp,
                    (screenBounds.height.value / 2).dp
                )

                val path = wanderingAI.generateWanderPath(startPos)

                if (character == Character.AURA) {
                    walkAuraTo(
                        targetPosition = path.points.last().position,
                        state = AuraState.IDLE_WALK,
                        speed = path.speed
                    )
                } else {
                    walkKaiTo(
                        targetPosition = path.points.last().position,
                        state = KaiState.SHIELD_NEUTRAL,
                        speed = path.speed
                    )
                }

                delay(10.seconds)
            }
        }
    }

    /**
     * Load sprite asset
     */
    fun loadAsset(path: String, character: Character): Painter? {
        return assetCache.getOrPut(path) {
            try {
                val inputStream = context.assets.open(path)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                BitmapPainter(bitmap.asImageBitmap())
            } catch (e: Exception) {
                null
            } ?: return null
        }
    }

    fun cleanup() {
        scope.cancel()
    }
}

/**
 * 🎬 Composable wrapper for EmbodimentEngine
 */
@Composable
fun rememberEmbodimentEngine(
    context: Context,
    screenBounds: ScreenBounds,
    lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
): EmbodimentEngine {
    return remember(context, screenBounds, lifecycleOwner) {
        EmbodimentEngine(context, screenBounds, lifecycleOwner)
    }
}

// Cached idle duration tracker
private var lastActivityTimestamp: Long = System.currentTimeMillis()

/**
 * Updates the last user activity timestamp.
 * Should be called on touch events, key presses, etc.
 */
fun recordUserActivity() {
    lastActivityTimestamp = System.currentTimeMillis()
}

/**
 * Get user idle duration (helper)
 *
 * Tracks user activity based on last recorded interaction.
 * Call recordUserActivity() from UI event handlers to update.
 *
 * @return Duration since last user activity
 */
fun getUserIdleDuration(): Duration {
    val idleMillis = System.currentTimeMillis() - lastActivityTimestamp
    return idleMillis.milliseconds
}
