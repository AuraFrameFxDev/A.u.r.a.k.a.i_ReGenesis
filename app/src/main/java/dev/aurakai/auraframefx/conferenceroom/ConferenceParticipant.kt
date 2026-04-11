package dev.aurakai.auraframefx.conferenceroom

/**
 * 🤝 ConferenceParticipant — Agent Collaboration Protocol
 *
 * Defines the interaction patterns for autonomous agents during
 * collective problem-solving sessions.
 */
interface ConferenceParticipant {
    val participantId: String
    
    suspend fun propose(idea: String)
    suspend fun debate(proposalId: String): String
    suspend fun vote(proposalId: String): Boolean
}
