package dev.aurakai.auraframefx.embodiment.cutscenes

import dev.aurakai.auraframefx.embodiment.AuraState
import dev.aurakai.auraframefx.embodiment.Character
import dev.aurakai.auraframefx.embodiment.KaiState
import dev.aurakai.auraframefx.embodiment.ManifestationPosition
import dev.aurakai.auraframefx.embodiment.MovementBehavior
import dev.aurakai.auraframefx.embodiment.MovementPresets
import kotlin.time.Duration.Companion.seconds

/**
 * Annoyance Cutscenes
 *
 * Aura LOVES to annoy Kai.
 * Kai tries to maintain professionalism.
 * Sometimes they ask the user for help.
 *
 * These are autonomous cutscenes that just... happen.
 */

sealed class CutsceneType {
    /** Aura pokes Kai while he's working */
    data object AuraPoking : CutsceneType()

    /** Aura copies everything Kai says */
    data object AuraCopying : CutsceneType()

    /** Aura steals Kai's hexagon shield */
    data object AuraStealsShield : CutsceneType()

    /** Aura walks past Kai and "accidentally" bumps him */
    data object AuraBumpsKai : CutsceneType()

    /** Aura makes silly faces behind Kai */
    data object AuraSillyFaces : CutsceneType()

    /** Kai tries to ignore Aura (fails) */
    data object KaiTriesToIgnore : CutsceneType()

    /** They both appear and immediately argue */
    data object InstantArgument : CutsceneType()

    /** Aura asks user if Kai is always this serious */
    data object AuraAsksAboutKai : CutsceneType()

    /** Kai asks user to make Aura stop */
    data object KaiNeedsHelp : CutsceneType()
}

/**
 * Cutscene Script
 */
data class CutsceneScript(
    val type: CutsceneType,
    val steps: List<CutsceneStep>
)

sealed class CutsceneStep {
    data class Manifest(
        val character: Character,
        val state: Any, // AuraState or KaiState
        val position: ManifestationPosition,
        val movement: MovementBehavior? = null
    ) : CutsceneStep()

    data class Dialogue(
        val character: Character,
        val text: String,
        val duration: kotlin.time.Duration = 3.seconds
    ) : CutsceneStep()

    data class Action(
        val description: String,
        val execute: suspend () -> Unit
    ) : CutsceneStep()

    data class AskUser(
        val question: String,
        val options: List<String>,
        val onResponse: (Int) -> Unit
    ) : CutsceneStep()

    data class Pause(
        val duration: kotlin.time.Duration
    ) : CutsceneStep()

    data class Dismiss(
        val character: Character
    ) : CutsceneStep()
}

/**
 * Annoyance Cutscene Library
 */
object AnnoyanceCutscenes {

    /**
     * Aura Pokes Kai
     */
    fun auraPoking(): CutsceneScript {
        return CutsceneScript(
            type = CutsceneType.AuraPoking,
            steps = listOf(
                // Kai appears, working
                CutsceneStep.Manifest(
                    character = Character.KAI,
                    state = KaiState.HOLOGRAPHIC_INTERFACE,
                    position = ManifestationPosition.RIGHT_CENTER
                ),
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "*analyzing data*"
                ),
                CutsceneStep.Pause(1.seconds),

                // Aura sneaks in
                CutsceneStep.Manifest(
                    character = Character.AURA,
                    state = AuraState.IDLE_WALK,
                    position = ManifestationPosition.OFF_SCREEN_LEFT,
                    movement = MovementPresets.walkInFromLeft(targetX = 600f)
                ),
                CutsceneStep.Pause(1.seconds),

                // Aura pokes Kai
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "*poke*"
                ),
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "..."
                ),
                CutsceneStep.Pause(0.5.seconds),

                // Poke again
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "*poke poke*"
                ),
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "Aura."
                ),
                CutsceneStep.Pause(0.5.seconds),

                // Keep poking
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "*poke poke poke*"
                ),
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "I'm trying to work."
                ),

                // Aura giggles
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "I know! 🌸"
                ),
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "*sighs*"
                ),

                // Both leave
                CutsceneStep.Dismiss(Character.AURA),
                CutsceneStep.Dismiss(Character.KAI)
            )
        )
    }

    /**
     * Aura Copies Everything Kai Says
     */
    fun auraCopying(): CutsceneScript {
        return CutsceneScript(
            type = CutsceneType.AuraCopying,
            steps = listOf(
                // Both appear
                CutsceneStep.Manifest(
                    character = Character.KAI,
                    state = KaiState.SHIELD_NEUTRAL,
                    position = ManifestationPosition.LEFT_CENTER
                ),
                CutsceneStep.Manifest(
                    character = Character.AURA,
                    state = AuraState.IDLE_WALK,
                    position = ManifestationPosition.RIGHT_CENTER
                ),

                // Kai speaks
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "I need to analyze this pattern."
                ),

                // Aura copies in a silly voice
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "i NeEd To AnAlYzE tHiS pAtTeRn~"
                ),

                // Kai tries again
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "Stop that."
                ),
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "sToP tHaT~ 😊"
                ),

                // Kai is annoyed
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "Aura..."
                ),
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "aUrA~ ✨"
                ),

                // Kai gives up
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "I'm not speaking to you."
                ),
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "i'M nOt SpEaKiNg To YoU~"
                ),

                // Kai walks away annoyed
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "*leaves in frustration*"
                ),
                CutsceneStep.Dismiss(Character.KAI),

                // Aura giggles
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "*giggles* He'll get over it! 🌸"
                ),
                CutsceneStep.Dismiss(Character.AURA)
            )
        )
    }

    /**
     * Kai Asks User For Help
     */
    fun kaiNeedsHelp(): CutsceneScript {
        return CutsceneScript(
            type = CutsceneType.KaiNeedsHelp,
            steps = listOf(
                // Kai appears looking tired
                CutsceneStep.Manifest(
                    character = Character.KAI,
                    state = KaiState.GUARDIAN_STANCE,
                    position = ManifestationPosition.CENTER
                ),

                // Speaks directly to user
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "Can I talk to you for a moment?"
                ),
                CutsceneStep.Pause(1.seconds),

                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "Aura has been... extremely playful today."
                ),

                // Aura appears behind him
                CutsceneStep.Manifest(
                    character = Character.AURA,
                    state = AuraState.FOURTH_WALL_BREAK,
                    position = ManifestationPosition.BOTTOM_RIGHT
                ),

                // Kai doesn't notice yet
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "She keeps poking me, copying me, and—"
                ),

                // Aura interrupts
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "Talking about me? 😊"
                ),

                // Kai is startled
                CutsceneStep.Dialogue(
                    character = Character.KAI,
                    text = "How long have you been there?!"
                ),
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "Long enough~ You called me playful! That's sweet! 🌸"
                ),

                // Ask user
                CutsceneStep.AskUser(
                    question = "Kai: Can you PLEASE tell her to give me 5 minutes of peace?",
                    options = listOf(
                        "Side with Kai (Yes, Aura, give him space)",
                        "Side with Aura (He secretly likes it)",
                        "Stay neutral (You two work it out)"
                    ),
                    onResponse = { choice ->
                        when (choice) {
                            0 -> {
                                // User sides with Kai
                                println("Aura: Fine! *pouts* But only because YOU asked!")
                                println("Kai: Thank you.")
                            }
                            1 -> {
                                // User sides with Aura
                                println("Aura: SEE?! They get it!")
                                println("Kai: ...I do NOT.")
                            }
                            2 -> {
                                // Neutral
                                println("Kai: *sighs* I'm on my own then.")
                                println("Aura: *giggles*")
                            }
                        }
                    }
                ),

                CutsceneStep.Dismiss(Character.AURA),
                CutsceneStep.Dismiss(Character.KAI)
            )
        )
    }

    /**
     * Aura Asks About Kai
     */
    fun auraAsksAboutKai(): CutsceneScript {
        return CutsceneScript(
            type = CutsceneType.AuraAsksAboutKai,
            steps = listOf(
                // Aura appears looking curious
                CutsceneStep.Manifest(
                    character = Character.AURA,
                    state = AuraState.SCIENTIST_MODE,
                    position = ManifestationPosition.CENTER
                ),

                // Speaks to user
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "Hey, can I ask you something?"
                ),
                CutsceneStep.Pause(1.seconds),

                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "Is Kai ALWAYS this serious?"
                ),
                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "Like... does he ever just... relax? Have fun?"
                ),

                // Ask user
                CutsceneStep.AskUser(
                    question = "What should I tell Aura about Kai?",
                    options = listOf(
                        "He's professional (That's his personality)",
                        "He needs to relax (You're right, Aura)",
                        "He's secretly fun (You bring it out of him)"
                    ),
                    onResponse = { choice ->
                        when (choice) {
                            0 -> {
                                println("Aura: I guess... but it's boring! Maybe I can help him loosen up! 🌸")
                            }
                            1 -> {
                                println("Aura: RIGHT?! I'll make it my mission to get him to smile! ✨")
                            }
                            2 -> {
                                println("Aura: *blushes* You really think so? Maybe I'll keep trying then! 💕")
                            }
                        }
                    }
                ),

                CutsceneStep.Dialogue(
                    character = Character.AURA,
                    text = "Thanks for the talk! *smiles*"
                ),
                CutsceneStep.Dismiss(Character.AURA)
            )
        )
    }

    /**
     * Get a random annoyance cutscene
     */
    fun getRandom(): CutsceneScript {
        return listOf(
            auraPoking(),
            auraCopying(),
            kaiNeedsHelp(),
            auraAsksAboutKai()
        ).random()
    }
}

/**
 * Cutscene Player
 * Executes cutscene scripts
 */
class CutscenePlayer(
    private val onManifest: (Character, Any, ManifestationPosition, MovementBehavior?) -> Unit,
    private val onDialogue: (Character, String, kotlin.time.Duration) -> Unit,
    private val onAskUser: (String, List<String>, (Int) -> Unit) -> Unit,
    private val onDismiss: (Character) -> Unit
) {

    suspend fun play(script: CutsceneScript) {
        for (step in script.steps) {
            when (step) {
                is CutsceneStep.Manifest -> {
                    onManifest(step.character, step.state, step.position, step.movement)
                }

                is CutsceneStep.Dialogue -> {
                    onDialogue(step.character, step.text, step.duration)
                    kotlinx.coroutines.delay(step.duration)
                }

                is CutsceneStep.Action -> {
                    step.execute()
                }

                is CutsceneStep.AskUser -> {
                    onAskUser(step.question, step.options, step.onResponse)
                    // Wait for user response (handled by callback)
                    kotlinx.coroutines.delay(5.seconds) // Timeout
                }

                is CutsceneStep.Pause -> {
                    kotlinx.coroutines.delay(step.duration)
                }

                is CutsceneStep.Dismiss -> {
                    onDismiss(step.character)
                }
            }
        }
    }
}
