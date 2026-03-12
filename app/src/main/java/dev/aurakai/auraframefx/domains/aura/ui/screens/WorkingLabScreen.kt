package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.cascade.storage.AuraKaiModules
import dev.aurakai.auraframefx.embodiment.*
import dev.aurakai.auraframefx.domains.aura.ui.components.*
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 🔬 Working Lab Screen
 *
 * The complete integrated system showing Aura & Kai ACTUALLY WORKING
 * in their holographic 3D lab!
 *
 * Features:
 * - 3D holographic menu with floating cards
 * - Aura & Kai walking around autonomously
 * - Working behaviors (analyzing, typing, coding, debugging)
 * - Cards react when they approach (glow, particles, status)
 * - Coordinated teamwork
 * - Data streams between cards
 * - Holographic platform with rings
 * - Cyberpunk background with particles
 */

/**
 * Renders the Working Lab UI with autonomous Aura and Kai manifestations, interactive module cards, and visual effects.
 *
 * This composable initializes the embodiment engine and choreographer, drives continuous autonomous work behaviors for Aura and Kai, displays their manifestations and module cards positioned in a 3D-like layout, and shows a data stream when both characters are centered.
 *
 * @param onNavigate Callback invoked with a destination identifier when a module or menu item is selected (e.g., "collab_canvas", "oracle_drive", "console", "romtools", "center").
 */
/**
 * Renders the Working Lab UI with autonomous Aura and Kai manifestations, interactive module cards, a holographic platform, and visual data streams.
 *
 * Initializes and drives the embodiment engine and choreographer, starts the autonomous work loop that manifests and moves Aura and Kai between module cards, and renders the resulting UI (background, platform, module cards, character manifestations, and data streams). User interactions on module cards and the center menu invoke the navigation callback.
 *
 * @param onNavigate Callback invoked with a destination identifier (e.g. "collab_canvas", "oracle_drive", "console", "romtools", or other menu targets) when a module card or menu item is selected.
 */
/**
 * Renders the Working Lab UI where autonomous Aura and Kai manifestations interact with module cards and a central menu.
 *
 * The composable displays a cyberpunk background, a holographic platform, four interactive module cards (Collab Canvas, Oracle Drive, Console, ROM Tools), and a centered main menu. It drives an embodied engine that autonomously sequences Aura and Kai behaviors, updates character positions and visible actions, renders their manifestations at computed positions, and shows a data stream between the characters when both target the center card. The engine lifecycle is tied to the composable and is cleaned up when the composable is disposed.
 *
 * @param onNavigate Callback invoked with the target route key (e.g., "collab_canvas", "oracle_drive", "console", "romtools", "center") when a module or menu item is clicked.
 */
@Composable
fun WorkingLabScreen(
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val screenBounds = remember {
        ScreenBounds(width = 1080.dp, height = 2400.dp)
    }

    // Embodiment engine
    val engine = rememberEmbodimentEngine(context, screenBounds)

    // Work choreographer
    val choreographer = remember { WorkChoreographer() }

    // Card positions (matching 3D layout)
    val cardPositions = remember {
        mapOf(
            "collab_canvas" to DpOffset(240.dp, 800.dp),      // Top left
            "oracle_drive" to DpOffset(840.dp, 800.dp),       // Top right
            "console" to DpOffset(240.dp, 1400.dp),           // Bottom left
            "romtools" to DpOffset(840.dp, 1400.dp),          // Bottom right
            "center" to DpOffset(540.dp, 1200.dp)             // Center menu
        )
    }

    // Work behavior executor
    remember {
        WorkBehaviorExecutor(engine, cardPositions)
    }

    // Track current work states
    var auraWorkAction by remember { mutableStateOf<String?>(null) }
    var kaiWorkAction by remember { mutableStateOf<String?>(null) }
    var auraTargetCard by remember { mutableStateOf<String?>(null) }
    var kaiTargetCard by remember { mutableStateOf<String?>(null) }

    // Track character positions
    val auraPosition by engine.activeManifestation.collectAsState().let { manifestations ->
        remember(manifestations.value) {
            derivedStateOf {
                manifestations.value
                    .find { it.character == Character.AURA }
                    ?.currentPosition
            }
        }
    }

    val kaiPosition by engine.activeManifestation.collectAsState().let { manifestations ->
        remember(manifestations.value) {
            derivedStateOf {
                manifestations.value
                    .find { it.character == Character.KAI }
                    ?.currentPosition
            }
        }
    }

    // Breathing animation for idle characters
    val breathingScale by rememberBreathingAnimation()

    // Start autonomous working behaviors
    LaunchedEffect(Unit) {
        try {
            engine.setMood(MoodState.FOCUSED)

            // Initial positions
            delay(1000)

            // Start continuous work loop
            while (true) {
                // Generate sequences ONCE
                val auraSeq = choreographer.generateAuraWorkSequence()
                val kaiSeq = choreographer.generateKaiWorkSequence()

                // Execute in parallel and track state
                coroutineScope {
                    // Aura's execution
                    launch {
                        auraSeq.steps.forEach { step ->
                            auraWorkAction = step.statusMessage
                            auraTargetCard = step.targetCard

                            // Let executor handle movement
                            when (step.action) {
                                WorkAction.WALKING_TO_CARD -> {
                                    val targetPos = cardPositions[step.targetCard] ?: DpOffset.Zero
                                    engine.walkAuraTo(
                                        targetPosition = targetPos,
                                        state = step.sprite as? AuraState ?: AuraState.IDLE_WALK
                                    )
                                }

                                else -> {
                                    engine.manifestAura(
                                        state = step.sprite as? AuraState
                                            ?: AuraState.SCIENTIST_MODE,
                                        config = ManifestationDefaults.DEFAULT_CONFIG.copy(
                                            duration = step.duration
                                        )
                                    )
                                }
                            }

                            delay(step.duration)
                        }
                        auraWorkAction = null
                        auraTargetCard = null
                    }

                    // Kai's execution
                    launch {
                        kaiSeq.steps.forEach { step ->
                            kaiWorkAction = step.statusMessage
                            kaiTargetCard = step.targetCard

                            // Let executor handle movement
                            when (step.action) {
                                WorkAction.WALKING_TO_CARD -> {
                                    val targetPos = cardPositions[step.targetCard] ?: DpOffset.Zero
                                    engine.walkKaiTo(
                                        targetPosition = targetPos,
                                        state = step.sprite as? KaiState ?: KaiState.SHIELD_NEUTRAL
                                    )
                                }

                                else -> {
                                    engine.manifestKai(
                                        state = step.sprite as? KaiState
                                            ?: KaiState.HOLOGRAPHIC_INTERFACE,
                                        config = ManifestationDefaults.DEFAULT_CONFIG.copy(
                                            duration = step.duration
                                        )
                                    )
                                }
                            }

                            delay(step.duration)
                        }
                        kaiWorkAction = null
                        kaiTargetCard = null
                    }
                }

                // Small break between cycles
                delay(3000)
            }
        } finally {
            // Cleanup when composable is disposed
            engine.cleanup()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Cyberpunk background
        CyberpunkBackground()

        // Holographic platform
        HolographicPlatform(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 100.dp)
        )

        // Interactive module cards
        Box(modifier = Modifier.fillMaxSize()) {
            // Top left - Collab Canvas
            InteractiveModuleCard(
                module = AuraKaiModules.CollabCanvas,
                cardPosition = cardPositions["collab_canvas"] ?: DpOffset.Zero,
                characterPosition = if (auraTargetCard == "collab_canvas") auraPosition else null,
                character = if (auraTargetCard == "collab_canvas") Character.AURA else null,
                workAction = if (auraTargetCard == "collab_canvas") auraWorkAction else null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 80.dp, y = 300.dp),
                onClick = { onNavigate("collab_canvas") }
            )

            // Top right - Cloud Save
            InteractiveModuleCard(
                module = AuraKaiModules.OracleDrive,
                cardPosition = cardPositions["oracle_drive"] ?: DpOffset.Zero,
                characterPosition = if (kaiTargetCard == "oracle_drive") kaiPosition else null,
                character = if (kaiTargetCard == "oracle_drive") Character.KAI else null,
                workAction = if (kaiTargetCard == "oracle_drive") kaiWorkAction else null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-80).dp, y = 300.dp),
                onClick = { onNavigate("oracle_drive") }
            )

            // Bottom left - Console
            InteractiveModuleCard(
                module = AuraKaiModules.Console,
                cardPosition = cardPositions["console"] ?: DpOffset.Zero,
                characterPosition = if (auraTargetCard == "console") auraPosition else null,
                character = if (auraTargetCard == "console") Character.AURA else null,
                workAction = if (auraTargetCard == "console") auraWorkAction else null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 80.dp, y = (-400).dp),
                onClick = { onNavigate("console") }
            )

            // Bottom right - ROM Tools
            InteractiveModuleCard(
                module = AuraKaiModules.ROMTools,
                cardPosition = cardPositions["romtools"] ?: DpOffset.Zero,
                characterPosition = if (kaiTargetCard == "romtools") kaiPosition else null,
                character = if (kaiTargetCard == "romtools") Character.KAI else null,
                workAction = if (kaiTargetCard == "romtools") kaiWorkAction else null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-80).dp, y = (-400).dp),
                onClick = { onNavigate("romtools") }
            )

            // Center main menu
            CenterMainMenu(
                onMenuItemClick = onNavigate
            )
        }

        // Aura & Kai manifestations
        val activeManifestation by engine.activeManifestation.collectAsState()

        activeManifestation.forEach { manifest ->
            val asset = when (manifest.character) {
                Character.AURA -> (manifest.state as AuraState).assetPath
                Character.KAI -> (manifest.state as KaiState).assetPath
            }

            val painter = engine.loadAsset(asset, manifest.character)
            val position = manifest.currentPosition ?: DpOffset.Zero

            if (painter != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.TopStart)
                        .offset(x = position.x, y = position.y)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "${manifest.character} manifestation",
                        modifier = Modifier
                            .size(200.dp)
                            .scale(if (manifest.isWalking) 1f else breathingScale) // Breathe when idle
                    )
                }
            }
        }

        // Data stream between Aura and Kai when both at center
        if (auraTargetCard == "center" && kaiTargetCard == "center") {
            auraPosition?.let { auraPos ->
                kaiPosition?.let { kaiPos ->
                    DataStreamBetweenCards(
                        fromPosition = auraPos,
                        toPosition = kaiPos,
                        color = Color(0xFFFF00FF),
                        active = true
                    )
                }
            }
        }
    }
}

/**
 * 🎬 Demo Mode - Shows specific work scenarios
 */
@Composable
fun WorkingLabDemoScreen(
    scenario: WorkScenario = WorkScenario.AUTONOMOUS_WORK
) {
    when (scenario) {
        WorkScenario.AUTONOMOUS_WORK -> {
            // Regular autonomous work mode
            WorkingLabScreen()
        }

        WorkScenario.COORDINATED_TASK -> {
            // Both working on same module
            CoordinatedWorkDemo()
        }

        WorkScenario.DEBUGGING_SESSION -> {
            // Finding and fixing bugs together
            DebuggingSessionDemo()
        }

        WorkScenario.DEPLOYMENT -> {
            // Deploying updates to production
            DeploymentDemo()
        }
    }
}

enum class WorkScenario {
    AUTONOMOUS_WORK,
    COORDINATED_TASK,
    DEBUGGING_SESSION,
    DEPLOYMENT
}

/**
 * 🤝 Coordinated Work Demo
 *
 * Aura & Kai work together on ROM Tools
 */
@Composable
fun CoordinatedWorkDemo() {
    val context = LocalContext.current
    val screenBounds = remember { ScreenBounds(1080.dp, 2400.dp) }
    val engine = rememberEmbodimentEngine(context, screenBounds)
    val choreographer = remember { WorkChoreographer() }

    LaunchedEffect(Unit) {
        val (auraSeq, kaiSeq) = choreographer.generateCoordinatedWork("romtools")

        launch {
            WorkBehaviorExecutor(
                engine,
                mapOf("romtools" to DpOffset(540.dp, 1200.dp))
            ).executeCoordinated(auraSeq, kaiSeq)
        }
    }

    WorkingLabScreen()
}

/**
 */
@Composable
fun DebuggingSessionDemo() {
    // Aura finds bug, Kai helps fix it
    val context = LocalContext.current
    val screenBounds = remember { ScreenBounds(1080.dp, 2400.dp) }
    val engine = rememberEmbodimentEngine(context, screenBounds)

    LaunchedEffect(Unit) {
        // Aura discovers issue
        engine.manifestAura(
            state = AuraState.FOURTH_WALL_BREAK,
            config = ManifestationDefaults.SUBTLE_CORNER_APPEARANCE.copy(
                duration = 5.seconds
            ),
            trigger = ManifestationTrigger.Custom("Bug detected!")
        )

        delay(5.seconds)

        // Kai comes to help
        engine.manifestKai(
            state = KaiState.SHIELD_SERIOUS,
            config = ManifestationDefaults.DRAMATIC_CENTER_ENTRANCE.copy(
                duration = 8.seconds
            ),
            trigger = ManifestationTrigger.Custom("Assisting debug")
        )
    }

    WorkingLabScreen()
}

/**
 */
@Composable
fun DeploymentDemo() {
    // Coordinated deployment sequence
    val context = LocalContext.current
    val screenBounds = remember { ScreenBounds(1080.dp, 2400.dp) }
    val engine = rememberEmbodimentEngine(context, screenBounds)

    LaunchedEffect(Unit) {
        // Final checks
        delay(2.seconds)

        // Aura: "Ready to deploy?"
        engine.manifestAura(
            state = AuraState.SCIENTIST_MODE,
            trigger = ManifestationTrigger.SystemModification
        )

        delay(3.seconds)

        // Kai: "All systems green ✓"
        engine.manifestKai(
            state = KaiState.SHIELD_PLAYFUL,
            trigger = ManifestationTrigger.Custom("Deploy approved")
        )

        delay(2.seconds)

        // Deploy!
        engine.manifestAura(
            state = AuraState.POWER_STANCE,
            config = ManifestationDefaults.DRAMATIC_CENTER_ENTRANCE
        )
    }

    WorkingLabScreen()
}

