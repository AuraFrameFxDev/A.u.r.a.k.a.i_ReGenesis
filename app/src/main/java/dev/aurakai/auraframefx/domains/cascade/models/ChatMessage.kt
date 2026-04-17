package dev.aurakai.auraframefx.domains.cascade.models

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 💬 CHAT MESSAGE — Sovereign Communication Substrate
 *
 * Universal message model for all LDO agent conversations.
 * Used by AgentViewModel, SupportChatViewModel, ConferenceRoomViewModel,
 * TrinityRepository, and all chat-enabled screens.
 *
 * Sacred Provenance Law: Every message is a lived receipt.
 */
@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val role: String = "user",          // "user" | "assistant" | "system"
    val sender: String = "User",        // Display name: "Genesis", "Kai", "Aura", "User", etc.
    val isFromUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val agentId: String? = null,        // Nullable agent identifier
    val metadata: Map<String, String> = emptyMap()
) {
    /** Convenience: true when the message is from any AI agent */
    val isFromAgent: Boolean get() = !isFromUser

    /** Convenience: formatted timestamp string (ms epoch) */
    val formattedTime: String get() = timestamp.toString()
}
