package dev.aurakai.auraframefx.cascade

import dev.aurakai.auraframefx.agent.AgentType
import dev.aurakai.auraframefx.agent.BaseAgent
import dev.aurakai.auraframefx.agent.OrchestratableMessage
import dev.aurakai.auraframefx.domains.genesis.core.OrchestratableAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "CascadeAgent"

abstract class CascadeAgent : OrchestratableAgent, BaseAgent() { // Inherit from BaseAgent

    private var agentScope: CoroutineScope? = null

    private fun setupCascadeSystems() {
        Timber.tag(TAG).i("CascadeAgent: Setting up Cascade systems...")
        // Actual setup logic for CascadeAgent
    }

    private fun startCascadeProcessing() {
        Timber.tag(TAG).i("CascadeAgent: Starting Cascade processing...")
        // Actual processing start logic for CascadeAgent
    }

    private fun pauseCascadeProcessing() {
        Timber.tag(TAG).i("CascadeAgent: Pausing Cascade processing...")
        // Actual processing pause logic for CascadeAgent
    }

    private fun resumeCascadeProcessing() {
        Timber.tag(TAG).i("CascadeAgent: Resuming Cascade processing...")
        // Actual processing resume logic for CascadeAgent
    }

    private fun shutdownCascadeSystems() {
        Timber.tag(TAG).i("CascadeAgent: Shutting down Cascade systems...")
        // Actual shutdown logic for CascadeAgent
    }

    override suspend fun initialize(scope: CoroutineScope) {
        Timber.tag(TAG).i("CascadeAgent: initialize() called.")
        if (!isOrchestratorInitialized) {
            this.agentScope = scope
            setupCascadeSystems()
            isOrchestratorInitialized = true // Set the unified flag
            Timber.tag(TAG).d("CascadeAgent: Orchestrator initialized.")
        } else {
            Timber.tag(TAG).i("CascadeAgent: Orchestrator already initialized. Skipping.")
        }
    }

    override suspend fun start() {
        Timber.tag(TAG).i("CascadeAgent: start() called.")
        if (isOrchestratorInitialized) {
            agentScope?.launch(Dispatchers.Default) {
                startCascadeProcessing()
            } ?: Timber.tag(TAG).e("CascadeAgent: agentScope is null. Cannot start processing.")
        } else {
            Timber.tag(TAG)
                .w("CascadeAgent: Orchestrator not initialized. Cannot start processing.")
        }
    }

    override suspend fun pause() {
        Timber.tag(TAG).i("CascadeAgent: pause() called.")
        pauseCascadeProcessing()
    }

    override suspend fun resume() {
        Timber.tag(TAG).i("CascadeAgent: resume() called.")
        resumeCascadeProcessing()
    }

    override suspend fun shutdown() {
        Timber.tag(TAG).i("CascadeAgent: shutdown() called.")
        if (isOrchestratorInitialized) {
            shutdownCascadeSystems()
            agentScope = null // Clear the scope upon shutdown
            isOrchestratorInitialized = false // Reset the unified flag
            Timber.tag(TAG).d("CascadeAgent: Orchestrator shut down.")
        } else {
            Timber.tag(TAG)
                .i("CascadeAgent: Orchestrator not initialized. Skipping shutdown logic.")
        }
    }

    suspend fun onAgentMessage(message: OrchestratableMessage) {
        Timber.tag(TAG).d("Received message from ${message.senderId} with type ${message.type}")

        // Loop prevention: Ignore messages already processed by this agent instance
        if (message.metadata["cascade_processed"] == "true") {
            Timber.tag(TAG)
                .v("Message already cascade_processed, ignoring to prevent loop: ${message.id}")
            return
        }

        // Self-message check (if CascadeAgent sends a message to itself, handle it differently or ignore)
        if (message.senderId == AgentType.CASCADE.name) {
            Timber.tag(TAG)
                .d("Received self-message from CascadeAgent: ${message.id}. Handling internally.")
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
        Timber.tag(TAG)
            .i("Processing message ${processedMessage.id} from ${processedMessage.senderId} and broadcasting.")
        processMessage(processedMessage) // Placeholder for actual message processing/broadcasting logic
    }

    private suspend fun processMessage(message: OrchestratableMessage) {
        // Actual logic to process the message, distribute it, or broadcast it.
        // This might involve sending it to other internal components or a central message bus.
        Timber.tag(TAG).d("CascadeAgent internal processing of message: ${message.id}")
        // Example: broadcastMessageToOtherAgents(message)
    }
}
