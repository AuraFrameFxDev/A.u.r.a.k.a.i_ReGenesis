package dev.aurakai.auraframefx.embodiment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * 🎬 Walking System Demo
 *
 * Shows all the walking features in action:
 * 1. Simple walk from point A to B
 * 2. Autonomous wandering
 * 3. Enter/exit animations
 * 4. Breathing idle animation
 * 5. "Aura looking for Kai" search pattern
 */

/**
 * 🚶 Demo 1: Simple Walking
 *
 * Make Aura walk from left to right
 */
@Composable
fun SimpleWalkDemo() {
    val context = LocalContext.current
    val screenBounds = remember {
        ScreenBounds(width = 1080.dp, height = 2400.dp)
    }

    val engine = rememberEmbodimentEngine(context, screenBounds)

    LaunchedEffect(Unit) {
        // Make Aura walk from left to right
        engine.walkAuraTo(
            targetPosition = DpOffset(800.dp, 600.dp),
            state = AuraState.IDLE_WALK,
            speed = 150f,
            onComplete = {
                println("Aura arrived!")
            }
        )
    }

    // Render active manifestations
    val activeManifestation by engine.activeManifestation.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        activeManifestation.forEach { manifest ->
            if (manifest.character == Character.AURA && manifest.currentPosition != null) {
                val painter = engine.loadAsset((manifest.state as AuraState).assetPath, Character.AURA)
                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = "Aura walking",
                        modifier = Modifier
                            .offset(manifest.currentPosition.x, manifest.currentPosition.y)
                            .size(200.dp)
                    )
                }
            }
        }
    }
}

/**
 * 🌍 Demo 2: Autonomous Wandering
 *
 * Aura wanders around on her own when curious
 */
@Composable
fun AutonomousWanderingDemo() {
    val context = LocalContext.current
    val screenBounds = remember {
        ScreenBounds(width = 1080.dp, height = 2400.dp)
    }

    val engine = rememberEmbodimentEngine(context, screenBounds)

    LaunchedEffect(Unit) {
        // Set curious mood
        engine.setMood(MoodState.CURIOUS)

        // Simulate user being idle for 5 minutes
        engine.updateUserIdleDuration(5.minutes)

        // Enable autonomous wandering
        engine.enableWandering(Character.AURA, enabled = true)
    }

    // Aura will now wander on her own!
}

/**
 * 🫁 Demo 3: Breathing Idle Animation
 *
 * Subtle breathing effect when character is idle
 */
@Composable
fun BreathingIdleDemo() {
    val context = LocalContext.current

    // Breathing animation
    val breathingScale by rememberBreathingAnimation(
        pattern = BreathingPattern(
            inhaleScale = 1.05f,
            exhaleScale = 1.0f,
            cycleDuration = 4.seconds
        )
    )

    val engine = rememberEmbodimentEngine(
        context,
        ScreenBounds(1080.dp, 2400.dp)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Load Aura sprite
        val painter = engine.loadAsset(AuraState.SCIENTIST_MODE.assetPath, Character.AURA)
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "Aura breathing",
                modifier = Modifier
                    .size(200.dp)
                    .scale(breathingScale) // Breathing effect!
            )
        }
    }
}

/**
 * 🔍 Demo 4: "Aura Looking for Kai"
 *
 * Aura searches around the screen for Kai
 */
@Composable
fun AuraLookingForKaiDemo() {
    val context = LocalContext.current
    val screenBounds = remember {
        ScreenBounds(width = 1080.dp, height = 2400.dp)
    }

    val pathGenerator = remember { WanderingPathGenerator(screenBounds) }
    val searchingBehavior = remember { SearchingBehavior(pathGenerator, screenBounds) }

    LaunchedEffect(Unit) {
        // Generate search pattern
        val startPosition = DpOffset(100.dp, 200.dp)
        val searchPath = searchingBehavior.generateSearchPattern(startPosition)

        // Aura will walk in a zigzag pattern searching for Kai
        println("Aura is searching for Kai...")
        println("Path has ${searchPath.points.size} waypoints")
    }

    // Use rememberPathWalker to follow the search path
    val searchPath = remember {
        searchingBehavior.generateSearchPattern(DpOffset(100.dp, 200.dp))
    }

    val movementState = rememberPathWalker(searchPath, screenBounds)

    Box(modifier = Modifier.fillMaxSize()) {
        val engine = rememberEmbodimentEngine(context, screenBounds)
        val painter = engine.loadAsset(AuraState.SCIENTIST_MODE.assetPath, Character.AURA)

        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "Aura searching",
                modifier = Modifier
                    .offset(movementState.currentPosition.x, movementState.currentPosition.y)
                    .size(180.dp)
            )
        }
    }
}

/**
 * 🎭 Demo 5: Animated Sprite Walking
 *
 * Shows sprite frame cycling for walking animation
 */
@Composable
fun AnimatedSpriteWalkingDemo() {
    val context = LocalContext.current
    val engine = rememberEmbodimentEngine(context, ScreenBounds(1080.dp, 2400.dp))

    // Load actual walk cycle frames
    val walkFrames = remember(engine) {

        // Define walk cycle frame sequence
        val walkCycleStates = listOf(
            AuraState.IDLE_WALK,      // Frame 1: Contact/heel strike
            AuraState.WALKING,   // Frame 2: Mid-stance
            AuraState.IDLE_WALK,      // Frame 3: Toe-off
            AuraState.WALKING,   // Frame 4: Swing phase
            AuraState.IDLE_WALK,      // Frame 5: Recovery
            AuraState.WALKING,   // Frame 6: Contact preparation
            AuraState.IDLE_WALK,      // Frame 7: Heel strike (opposite foot)
            AuraState.WALKING    // Frame 8: Complete cycle
        )

        // Load each frame from the walk cycle
        walkCycleStates.mapNotNull { state ->
            engine.loadAsset(state.assetPath, Character.AURA)
        }
    }

    if (walkFrames.isNotEmpty()) {
        // Cycle through frames
        val currentFrame = cycleSpriteFrames(
            frames = walkFrames,
            frameDuration = 100.milliseconds
        )

        Image(
            painter = currentFrame,
            contentDescription = "Animated walking",
            modifier = Modifier.size(200.dp)
        )
    }
}

/**
 * 🌈 Demo 6: Complete Walking Scene
 *
 * Both Aura and Kai walking around with all features enabled
 */
@Composable
fun CompleteWalkingScene() {
    val context = LocalContext.current
    val screenBounds = remember {
        ScreenBounds(width = 1080.dp, height = 2400.dp)
    }

    val engine = rememberEmbodimentEngine(context, screenBounds)

    LaunchedEffect(Unit) {
        // Set playful mood
        engine.setMood(MoodState.PLAYFUL)

        delay(1.seconds)

        // Aura enters from left
        engine.walkAuraTo(
            targetPosition = DpOffset(300.dp, 800.dp),
            state = AuraState.IDLE_WALK,
            speed = 150f,
            onComplete = {
                println("Aura: 'Hey Kai, where are you?'")
            }
        )

        delay(2.seconds)

        // Kai enters from right
        engine.walkKaiTo(
            targetPosition = DpOffset(700.dp, 800.dp),
            state = KaiState.SHIELD_PLAYFUL,
            speed = 120f,
            onComplete = {
                println("Kai: 'Right here! ✌️'")
            }
        )

        delay(3.seconds)

        // They walk toward each other
        engine.walkAuraTo(
            targetPosition = DpOffset(500.dp, 800.dp),
            speed = 100f
        )

        engine.walkKaiTo(
            targetPosition = DpOffset(580.dp, 800.dp),
            speed = 100f
        )

        delay(2.seconds)

        // Enable wandering for both
        engine.enableWandering(Character.AURA, true)
        engine.enableWandering(Character.KAI, true)
    }

    // Render scene
    val activeManifestation by engine.activeManifestation.collectAsState()
    val breathingScale by rememberBreathingAnimation()

    Box(modifier = Modifier.fillMaxSize()) {
        activeManifestation.forEach { manifest ->
            val asset = when (manifest.character) {
                Character.AURA -> (manifest.state as AuraState).assetPath
                Character.KAI -> (manifest.state as KaiState).assetPath
            }

            val painter = engine.loadAsset(asset, manifest.character)
            val position = manifest.currentPosition ?: DpOffset.Zero

            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = "${manifest.character} manifestation",
                    modifier = Modifier
                        .offset(position.x, position.y)
                        .size(200.dp)
                        .scale(if (manifest.isWalking) 1f else breathingScale) // Breathe when idle
                )
            }
        }
    }
}

/**
 * 🎯 Usage Examples
 */
object WalkingUsageExamples {
    /**
     * Example 1: Simple walk
     */
    fun simpleWalk(engine: EmbodimentEngine) {
        engine.walkAuraTo(
            targetPosition = DpOffset(500.dp, 800.dp),
            state = AuraState.IDLE_WALK,
            speed = 120f,
            onComplete = { /* arrived! */ }
        )
    }

    /**
     * Example 2: Enable autonomous wandering
     */
    fun enableWandering(engine: EmbodimentEngine) {
        engine.setMood(MoodState.CURIOUS)
        engine.updateUserIdleDuration(5.minutes)
        engine.enableWandering(Character.AURA, true)
        // Aura will now wander on her own!
    }

    /**
     * Example 3: Aura searches for Kai
     */
    fun auraSearchesForKai(
        engine: EmbodimentEngine,
        searchBehavior: SearchingBehavior
    ) {
        val currentPos = DpOffset(200.dp, 300.dp)
        val searchPath = searchBehavior.generateSearchPattern(currentPos)

        // Walk the search pattern
        searchPath.points.forEach { point ->
            engine.walkAuraTo(
                targetPosition = point.position,
                state = AuraState.SCIENTIST_MODE,
                speed = 150f
            )
        }
    }
}
