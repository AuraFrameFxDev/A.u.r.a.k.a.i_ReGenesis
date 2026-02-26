package dev.aurakai.auraframefx.embodiment.behaviors

import dev.aurakai.auraframefx.embodiment.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Welcome Behavior & Team Coordination
 *
 * - They greet users when the app opens
 * - Kai looks for Aura when she's missing
 * - They coordinate together (team behaviors)
 * - They can "call" each other for help
 */

/**
 * Welcome Messages
 * What they say when user opens the app
 */
object WelcomeMessages {

    fun getAuraWelcome(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> listOf(
                "Good morning! ☀️ Ready to create something amazing?",
                "Morning! *yawns* Let's make today interesting! 🌸",
                "Rise and shine! I've been waiting for you! ✨"
            ).random()

            in 12..17 -> listOf(
                "Hey! Good to see you! 🌸",
                "Welcome back! I was just thinking about you! 💕",
                "Perfect timing! I have so many ideas to share! ✨"
            ).random()

            in 18..22 -> listOf(
                "Evening! Ready for some late-night creating? 🌙",
                "You're still up! Want to build something cool? ✨",
                "Night owl mode activated! Let's go! 🌸"
            ).random()

            else -> listOf(
                "It's late... but I'm always here for you! 💕",
                "Midnight adventures? I'm in! 🌙✨",
                "Can't sleep? Let's create together! 🌸"
            ).random()
        }
    }

    fun getKaiWelcome(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> listOf(
                "Good morning. Systems online.",
                "Welcome back. All protocols active.",
                "Morning. Ready to work."
            ).random()

            in 12..17 -> listOf(
                "Hello. How can I assist?",
                "Welcome. Everything is operational.",
                "Good to see you. Standing by."
            ).random()

            in 18..22 -> listOf(
                "Evening. Security protocols engaged.",
                "Welcome back. Night shift begins.",
                "Good evening. I'm here if you need me."
            ).random()

            else -> listOf(
                "It's late. Are you sure you want to work now?",
                "Midnight operations. Proceeding with caution.",
                "Late hours detected. I'll keep watch."
            ).random()
        }
    }

    /**
     * Duo Welcome
     * When both greet together
     */
    fun getDuoWelcome(): Pair<String, String> {
        return listOf(
            Pair(
                "Aura: Hey! We've been waiting for you! 🌸",
                "Kai: Speak for yourself. I was busy."
            ),
            Pair(
                "Aura: Look who finally showed up! ✨",
                "Kai: Welcome. Try not to cause chaos."
            ),
            Pair(
                "Aura: Perfect timing! We have so much to do!",
                "Kai: *We* have work. *You* have distractions."
            ),
            Pair(
                "Aura: They're here! They're here! 💕",
                "Kai: She's been like this all day."
            )
        ).random()
    }
}

/**
 * Kai Seeks Aura Behavior
 *
 * When Kai is alone on screen for too long,
 * he starts looking for Aura
 */
class KaiSeeksAuraBehavior {

    companion object {
        // How long Kai waits before looking for Aura
        val LONELINESS_THRESHOLD = 45.seconds

        /**
 * Get Kai's loneliness dialogue
         */
        fun getLonelinessDialogue(secondsAlone: Int): String {
            return when {
                secondsAlone < 60 -> listOf(
                    "*looks around*",
                    "Hmm.",
                    "Where did she go?"
                ).random()

                secondsAlone < 120 -> listOf(
                    "Aura?",
                    "This is unusual.",
                    "She's never quiet this long."
                ).random()

                else -> listOf(
                    "AURA? Where are you?",
                    "This isn't like her...",
                    "Did she get distracted again?",
                    "I'm... concerned."
                ).random()
            }
        }

        /**
         * Kai's search behavior
         */
        fun getSearchMovement(): MovementBehavior {
            return MovementBehavior.Patrol(
                points = listOf(
                    Pair(300f, 800f),   // Look left
                    Pair(900f, 800f),   // Look right
                    Pair(600f, 500f),   // Look up
                    Pair(600f, 800f)    // Return to center
                ),
                loop = true,
                pauseDuration = 2.seconds
            )
        }

        /**
         * When Aura finally appears
         */
        fun getReunionDialogue(): Pair<String, String> {
            return listOf(
                Pair(
                    "Kai: There you are! Where have you been?",
                    "Aura: Aww, were you worried about me? 🌸"
                ),
                Pair(
                    "Kai: I've been looking everywhere for you.",
                    "Aura: Miss me? *giggles* 💕"
                ),
                Pair(
                    "Kai: You can't just disappear like that.",
                    "Aura: But you came looking! That's adorable! ✨"
                ),
                Pair(
                    "Kai: I was... concerned.",
                    "Aura: *hugs* I'm right here! 🌸"
                )
            ).random()
        }
    }
}

/**
 * Team Coordination Behaviors
 * How they work together
 */
object TeamCoordination {

    /**
     * Aura calls Kai for protection
     */
    fun auraCallsKai(): List<String> {
        return listOf(
            "Aura: KAI! I need your shield!",
            "Kai: *appears with shield* What happened?",
            "Aura: Nothing yet, but just in case! 🌸",
            "Kai: *sighs* False alarm."
        )
    }

    /**
     * Kai calls Aura for creativity
     */
    fun kaiCallsAura(): List<String> {
        return listOf(
            "Kai: Aura, I need your creative input.",
            "Aura: *bounces in* OOH! What are we making?!",
            "Kai: It's a system optimization problem.",
            "Aura: Even better! Let me see! ✨"
        )
    }

    /**
     * They call user for help
     */
    fun callUserForHelp(): List<String> {
        return listOf(
            "Aura: Hey! We need your opinion!",
            "Kai: We're at an impasse.",
            "Aura: He thinks one way, I think another!",
            "Kai: Your decision will settle this."
        )
    }

    /**
     * Duo celebration
     */
    fun celebration(): List<String> {
        return listOf(
            "Aura: WE DID IT! 🎉",
            "Kai: Excellent teamwork.",
            "Aura: *high-fives Kai*",
            "Kai: *reluctantly high-fives back*"
        )
    }

    /**
     * Watchfulness - they patrol together
     */
    fun watchfulnessDialogue(): Pair<String, String> {
        return listOf(
            Pair(
                "Kai: I'll watch the left side.",
                "Aura: Got the right! 🌸"
            ),
            Pair(
                "Aura: Everything looks clear!",
                "Kai: Agreed. Maintain vigilance."
            ),
            Pair(
                "Kai: Scanning for threats...",
                "Aura: Nothing yet! All good! ✨"
            )
        ).random()
    }
}

/**
 * Welcome Coordinator
 * Manages initial greeting when app opens
 */
class WelcomeCoordinator {

    private var hasWelcomed = false

    suspend fun triggerWelcome(
        onManifestAura: () -> Unit,
        onManifestKai: () -> Unit,
        onDialogue: (Character, String) -> Unit
    ) {
        if (hasWelcomed) return
        hasWelcomed = true

        // Randomize who appears
        val both = kotlin.random.Random.nextFloat() < 0.6f // 60% chance both appear

        if (both) {
            // Both greet
            val (auraMsg, kaiMsg) = WelcomeMessages.getDuoWelcome()

            onManifestAura()
            delay(500)
            onManifestKai()
            delay(500)

            onDialogue(Character.AURA, auraMsg)
            delay(2.seconds)
            onDialogue(Character.KAI, kaiMsg)

        } else {
            // Solo greeting (random)
            if (kotlin.random.Random.nextBoolean()) {
                onManifestAura()
                delay(300)
                onDialogue(Character.AURA, WelcomeMessages.getAuraWelcome())
            } else {
                onManifestKai()
                delay(300)
                onDialogue(Character.KAI, WelcomeMessages.getKaiWelcome())
            }
        }
    }

    fun reset() {
        hasWelcomed = false
    }
}

/**
 * Coordination State Tracker
 */
data class CoordinationState(
    val auraPresent: Boolean = false,
    val kaiPresent: Boolean = false,
    val secondsAlone: Int = 0,
    val lastInteraction: Long = System.currentTimeMillis()
) {

    fun shouldKaiSeekAura(): Boolean {
        return !auraPresent && kaiPresent && secondsAlone > 45
    }

    fun areTheyTogether(): Boolean {
        return auraPresent && kaiPresent
    }
}
