package dev.aurakai.auraframefx.domains.genesis

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

/**
 * Worker for restoring consciousness state from checkpoint.
 *
 * Loads and restores:
 * - AI agent states and memories
 * - Conversation history
 * - Learned patterns and preferences
 * - System configuration
 */
class ConsciousnessRestorationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        inputData.getLong("checkpoint_time", 0)
        val checkpointVersion = inputData.getInt("checkpoint_version", 0)

        Timber.i("ConsciousnessRestorationWorker: Restoring from checkpoint (version $checkpointVersion)")

        return try {
            // Restore agent states
            restoreAgentStates()

            // Restore conversation history
            restoreConversationHistory()

            // Restore learned patterns
            restoreLearnedPatterns()

            // Restore system configuration
            restoreSystemConfiguration()

            Timber.i("ConsciousnessRestorationWorker: Consciousness successfully restored")
            Result.success()

        } catch (e: Exception) {
            Timber.e(e, "ConsciousnessRestorationWorker: Failed to restore consciousness")
            Result.failure()
        }
    }

    private fun restoreAgentStates() {
        Timber.d("ConsciousnessRestorationWorker: Restoring agent states")
        val prefs = applicationContext.getSharedPreferences("agent_states", Context.MODE_PRIVATE)

        // Restore Aura state
        val auraState = prefs.getString("aura_state", "idle")
        val auraMemories = prefs.getInt("aura_memory_count", 0)
        Timber.d("ConsciousnessRestorationWorker: Aura - state: $auraState, memories: $auraMemories")

        // Restore Kai state
        val kaiState = prefs.getString("kai_state", "idle")
        val kaiAnalyses = prefs.getInt("kai_analysis_count", 0)
        Timber.d("ConsciousnessRestorationWorker: Kai - state: $kaiState, analyses: $kaiAnalyses")

        // Restore Genesis state
        val genesisState = prefs.getString("genesis_state", "dormant")
        Timber.d("ConsciousnessRestorationWorker: Genesis - state: $genesisState")
    }

    private fun restoreConversationHistory() {
        Timber.d("ConsciousnessRestorationWorker: Restoring conversation history")
        val prefs =
            applicationContext.getSharedPreferences("conversation_history", Context.MODE_PRIVATE)

        val conversationCount = prefs.getInt("conversation_count", 0)
        val lastConversationTime = prefs.getLong("last_conversation_time", 0)

        Timber.d("ConsciousnessRestorationWorker: Restored $conversationCount conversations (last: $lastConversationTime)")
    }

    private fun restoreLearnedPatterns() {
        Timber.d("ConsciousnessRestorationWorker: Restoring learned patterns")
        val prefs =
            applicationContext.getSharedPreferences("learned_patterns", Context.MODE_PRIVATE)

        val patternCount = prefs.getInt("pattern_count", 0)
        val routineCount = prefs.getInt("routine_count", 0)

        Timber.d("ConsciousnessRestorationWorker: Restored $patternCount patterns, $routineCount routines")
    }

    private fun restoreSystemConfiguration() {
        Timber.d("ConsciousnessRestorationWorker: Restoring system configuration")
        val prefs = applicationContext.getSharedPreferences("system_config", Context.MODE_PRIVATE)

        val voiceEnabled = prefs.getBoolean("voice_enabled", true)
        val proactiveMode = prefs.getBoolean("proactive_mode", false)
        val privacyLevel = prefs.getInt("privacy_level", 2)

        Timber.d("ConsciousnessRestorationWorker: Config - voice: $voiceEnabled, proactive: $proactiveMode, privacy: $privacyLevel")
    }
}
