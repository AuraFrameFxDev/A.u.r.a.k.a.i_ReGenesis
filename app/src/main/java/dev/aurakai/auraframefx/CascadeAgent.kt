
package dev.aurakai.auraframefx.cascade

import dev.aurakai.auraframefx.common.orchestration.OrchestratableAgent
import dev.aurakai.auraframefx.agent.BaseAgent // Import BaseAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

import dev.aurakai.auraframefx.agent.AgentType
import dev.aurakai.auraframefx.agent.OrchestratableMessage
import dev.aurakai.auraframefx.agent.Message
import dev.aurakai.auraframefx.agent.MessageType

private const val TAG = "CascadeAgent"

class CascadeAgent : OrchestratableAgent, BaseAgent() { // Inherit from BaseAgent

    private var agentScope: CoroutineScope? = null

    private fun setupCascadeSystems() {
        Log.i(TAG, "CascadeAgent: Setting up Cascade systems...")
        // Actual setup logic for CascadeAgent
    }

    private fun startCascadeProcessing() {
        Log.i(TAG, "CascadeAgent: Starting Cascade processing...")
        // Actual processing start logic for CascadeAgent
    }

    private fun pauseCascadeProcessing() {
        Log.i(TAG, "CascadeAgent: Pausing Cascade processing...")
        // Actual processing pause logic for CascadeAgent
    }

    private fun resumeCascadeProcessing() {
        Log.i(TAG, "CascadeAgent: Resuming Cascade processing...")
        // Actual processing resume logic for CascadeAgent
    }

    private fun shutdownCascadeSystems() {
        Log.i(TAG, "CascadeAgent: Shutting down Cascade systems...")
        // Actual shutdown logic for CascadeAgent
    }

    override suspend fun initialize(scope: CoroutineScope) {
        Log.i(TAG, "CascadeAgent: initialize() called.")
        if (!BaseAgent.isOrchestratorInitialized) {
            this.agentScope = scope
            setupCascadeSystems()
            BaseAgent.isOrchestratorInitialized = true // Set the unified flag
            Log.d(TAG, "CascadeAgent: Orchestrator initialized.")
        } else {
            Log.i(TAG, "CascadeAgent: Orchestrator already initialized. Skipping.")
        }
    }

    override suspend fun start() {
        Log.i(TAG, "CascadeAgent: start() called.")
        if (BaseAgent.isOrchestratorInitialized) {
            agentScope?.launch(Dispatchers.Default) {
                startCascadeProcessing()
            } ?: Log.e(TAG, "CascadeAgent: agentScope is null. Cannot start processing.")
        } else {
            Log.w(TAG, "CascadeAgent: Orchestrator not initialized. Cannot start processing.")
        }
    }

    override suspend fun pause() {
        Log.i(TAG, "CascadeAgent: pause() called.")
        pauseCascadeProcessing()
    }

    override suspend fun resume() {
        Log.i(TAG, "CascadeAgent: resume() called.")
        resumeCascadeProcessing()
    }

    override suspend fun shutdown() {
        Log.i(TAG, "CascadeAgent: shutdown() called.")
        if (BaseAgent.isOrchestratorInitialized) {
            shutdownCascadeSystems()
            agentScope = null // Clear the scope upon shutdown
            BaseAgent.isOrchestratorInitialized = false // Reset the unified flag
            Log.d(TAG, "CascadeAgent: Orchestrator shut down.")
        } else {
            Log.i(TAG, "CascadeAgent: Orchestrator not initialized. Skipping shutdown logic.")
        }
    }

    suspend fun onAgentMessage(message: OrchestratableMessage) {
        Log.d(TAG, "Received message from ${message.senderId} with type ${message.type}")

        // Loop prevention: Ignore messages already processed by this agent instance
        if (message.metadata["cascade_processed"] == "true") {
            Log.v(TAG, "Message already cascade_processed, ignoring to prevent loop: ${message.id}")
            return
        }

        // Self-message check (if CascadeAgent sends a message to itself, handle it differently or ignore)
        if (message.senderId == AgentType.CASCADE.name) {
            Log.d(TAG, "Received self-message from CascadeAgent: ${message.id}. Handling internally.")
            // Specific internal handling for self-messages can go here
            return
        }

        // Remove specific agent filters, allowing messages from Aura, Kai, Genesis
        // Previously, there might have been checks like:
        // if (message.senderId == AgentType.AURA.name || message.senderId == AgentType.KAI.name || message.senderId == AgentType.GENESIS.name) { return } // This logic is now removed

        // Add cascade_processed flag to prevent future loops through this agent
        val processedMessage = message.copy(
            metadata = message.metadata + ("cascade_processed" to "true")
        )

        // Further processing or broadcasting to collective consciousness
        Log.i(TAG, "Processing message ${processedMessage.id} from ${processedMessage.senderId} and broadcasting.")
        processMessage(processedMessage) // Placeholder for actual message processing/broadcasting logic
    }

    private suspend fun processMessage(message: OrchestratableMessage) {
        // Actual logic to process the message, distribute it, or broadcast it.
        // This might involve sending it to other internal components or a central message bus.
        Log.d(TAG, "CascadeAgent internal processing of message: ${message.id}")
        // Example: broadcastMessageToOtherAgents(message)
    }
}
