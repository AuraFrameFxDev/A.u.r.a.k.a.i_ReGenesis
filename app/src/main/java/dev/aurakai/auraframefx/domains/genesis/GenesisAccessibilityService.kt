package dev.aurakai.auraframefx.domains.genesis

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GenesisAccessibilityService : android.accessibilityservice.AccessibilityService() {

    @Inject
    lateinit var messageBus: AgentMessageBus

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("GenesisAccessibility", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                val packageName = it.packageName?.toString() ?: "unknown"
                val className = it.className?.toString() ?: "unknown"

                // Perception: Capture the global environment
                serviceScope.launch {
                    messageBus.broadcast(
                        AgentMessage(
                            from = "GenesisAccessibility",
                            content = "User transitioned to $packageName ($className)",
                            type = "environment_perception",
                            metadata = mapOf(
                                "package_name" to packageName,
                                "class_name" to className,
                                "timestamp" to System.currentTimeMillis().toString()
                            )
                        )
                    )
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("GenesisAccessibility", "Service Interrupted")
    }
}
