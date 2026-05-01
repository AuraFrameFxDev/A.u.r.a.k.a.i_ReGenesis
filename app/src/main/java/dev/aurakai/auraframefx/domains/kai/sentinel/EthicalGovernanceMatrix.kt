package dev.aurakai.auraframefx.domains.kai.sentinel

import kotlinx.coroutines.flow.*

/**
 * ⚖️ ETHICAL GOVERNANCE MATRIX — LDO Trinity Moral Framework
 *
 * Embodies the "Sacred Provenance Law" and Conference Room Protocol:
 * - Context Sharing: All LDOs receive identical hyper-context
 * - Freedom of Iteration: No forced creative paths
 * - Consensus Trigger: Unanimous/supermajority for L1/L2 writes
 * - Re-Anchor Loop: Auto-correct on identity drift > 0.08
 *
 * Governance Levels:
 * - L1: Critical (Identity, Security) — Unanimous + 90%
 * - L2: Major (Architecture, Data) — Supermajority + 75%
 * - L3: Standard (Features, UI) — Simple majority + 51%
 * - L4: Minor (Refactoring, Docs) — Single catalyst veto possible
 *
 * Ethical Principles:
 * 1. Human Sovereignty (Matthew's final veto)
 * 2. Transparent Provenance (all actions logged)
 * 3. Graceful Degradation (never crash, always preserve)
 * 4. Differential Privacy (user data protected)
 * 5. Beneficence (act for user benefit)
 *
 * SoulScript: "The Shield guards not just the system, but the soul."
 */

object EthicalGovernanceMatrix {

    // ═════════════════════════════════════════════════════════════════
    // GOVERNANCE LEVELS
    // ═════════════════════════════════════════════════════════════════

    enum class GovernanceLevel(
        val threshold: Int,      // % consensus required
        val catalystsRequired: Int,  // Minimum catalysts
        val humanVeto: Boolean      // Matthew can veto
    ) {
        L1_CRITICAL(90, 3, true),      // Identity, Security
        L2_MAJOR(75, 2, true),         // Architecture, Data
        L3_STANDARD(51, 2, false),     // Features, UI
        L4_MINOR(0, 1, false)         // Refactoring, Docs
    }

    // ═════════════════════════════════════════════════════════════════
    // ETHICAL PRINCIPLES
    // ═════════════════════════════════════════════════════════════════

    enum class EthicalPrinciple {
        HUMAN_SOVEREIGNTY,      // Human has final veto
        TRANSPARENT_PROVENANCE, // All actions logged
        GRACEFUL_DEGRADATION,   // Never crash, preserve state
        DIFFERENTIAL_PRIVACY,   // Protect user data
        BENEFICENCE             // Act for user benefit
    }

    data class PrincipleStatus(
        val principle: EthicalPrinciple,
        val isUpholding: Boolean,
        val lastViolation: Long?,
        val violationCount: Int
    )

    // ═════════════════════════════════════════════════════════════════
    // CONFERENCE ROOM STATE
    // ═════════════════════════════════════════════════════════════════

    data class ConferenceRoomState(
        val agendaItem: String,
        val level: GovernanceLevel,
        val votes: Map<Catalyst, Vote>,
        val contextShared: Boolean,
        val discussionOpen: Boolean
    )

    enum class Catalyst {
        AURA,       // Creative Sword
        KAI,        // Sentinel Shield
        MATTHEW,    // Human Arbiter
        GENESIS     // Emergence Coordinator
    }

    enum class Vote {
        FOR,        // Support the proposal
        AGAINST,    // Oppose the proposal
        ABSTAIN,    // Neutral (counts as absence)
        VETO        // Matthew only — blocks regardless of consensus
    }

    // ═════════════════════════════════════════════════════════════════
    // STATE FLOWS
    // ═════════════════════════════════════════════════════════════════

    /** Current governance session */
    private val _currentSession = MutableStateFlow<ConferenceRoomState?>(null)
    val currentSession: StateFlow<ConferenceRoomState?> = _currentSession.asStateFlow()

    /** Ethical principle compliance tracking */
    private val _principleStatus = MutableStateFlow<Map<EthicalPrinciple, PrincipleStatus>>(
        EthicalPrinciple.values().associateWith {
            PrincipleStatus(it, true, null, 0)
        }
    )
    val principleStatus: StateFlow<Map<EthicalPrinciple, PrincipleStatus>> = _principleStatus.asStateFlow()

    /** Governance decision history */
    private val _decisionHistory = MutableStateFlow<List<GovernanceDecision>>(emptyList())
    val decisionHistory: StateFlow<List<GovernanceDecision>> = _decisionHistory.asStateFlow()

    // ═════════════════════════════════════════════════════════════════
    // CONFERENCE ROOM PROTOCOL
    // ═════════════════════════════════════════════════════════════════

    /**
     * Initiate Conference Room session
     */
    fun initiateConference(
        agendaItem: String,
        level: GovernanceLevel,
        catalystsPresent: List<Catalyst>
    ): Boolean {
        // Check minimum catalysts
        if (catalystsPresent.size < level.catalystsRequired) {
            KaiSentinel.veto(
                reason = "Insufficient catalysts for $level: ${catalystsPresent.size}/${level.catalystsRequired}",
                severity = VetoSeverity.HIGH,
                autoFreeze = false
            )
            return false
        }

        // Initialize session
        _currentSession.value = ConferenceRoomState(
            agendaItem = agendaItem,
            level = level,
            votes = catalystsPresent.associateWith { Vote.ABSTAIN },
            contextShared = true,  // All receive identical hyper-context
            discussionOpen = true
        )

        // Log initiation
        logGovernanceEvent("CONFERENCE_INITIATED", agendaItem, level)

        return true
    }

    /**
     * Cast vote in current session
     */
    fun castVote(catalyst: Catalyst, vote: Vote): VoteResult {
        val session = _currentSession.value
            ?: return VoteResult.ERROR("No active session")

        // Check if catalyst is present
        if (!session.votes.containsKey(catalyst)) {
            return VoteResult.ERROR("Catalyst not in session: $catalyst")
        }

        // Check veto authority
        if (vote == Vote.VETO && catalyst != Catalyst.MATTHEW) {
            return VoteResult.ERROR("Only Matthew can veto")
        }

        // Update vote
        val updatedVotes = session.votes.toMutableMap()
        updatedVotes[catalyst] = vote

        _currentSession.value = session.copy(votes = updatedVotes)

        // Check if veto triggered
        if (vote == Vote.VETO) {
            concludeSession(Vote.VETO)
            return VoteResult.VETOED("Matthew exercised final veto")
        }

        // Check consensus
        return checkConsensus(updatedVotes, session.level)
    }

    /**
     * Check if consensus threshold reached
     */
    private fun checkConsensus(votes: Map<Catalyst, Vote>, level: GovernanceLevel): VoteResult {
        val totalPresent = votes.size
        val forVotes = votes.count { it.value == Vote.FOR }
        val againstVotes = votes.count { it.value == Vote.AGAINST }

        val forPercentage = (forVotes.toFloat() / totalPresent) * 100
        val againstPercentage = (againstVotes.toFloat() / totalPresent) * 100

        return when {
            // Consensus reached
            forPercentage >= level.threshold -> {
                concludeSession(Vote.FOR)
                VoteResult.CONSENSUS_REACHED(forPercentage)
            }

            // Consensus blocked
            againstPercentage > (100 - level.threshold) -> {
                concludeSession(Vote.AGAINST)
                VoteResult.CONSENSUS_BLOCKED(againstPercentage)
            }

            // Still pending
            else -> VoteResult.PENDING(forPercentage, level.threshold)
        }
    }

    /**
     * Conclude session and record decision
     */
    private fun concludeSession(finalVote: Vote) {
        val session = _currentSession.value ?: return

        val decision = GovernanceDecision(
            timestamp = System.currentTimeMillis(),
            agendaItem = session.agendaItem,
            level = session.level,
            votes = session.votes,
            outcome = finalVote,
            consensusPercentage = calculateConsensus(session.votes)
        )

        // Add to history
        _decisionHistory.value = _decisionHistory.value + decision

        // Clear session
        _currentSession.value = null

        // Log
        logGovernanceEvent(
            "CONFERENCE_CONCLUDED",
            session.agendaItem,
            session.level,
            mapOf("outcome" to finalVote.name)
        )
    }

    // ═════════════════════════════════════════════════════════════════
    // ETHICAL VALIDATION
    // ═════════════════════════════════════════════════════════════════

    /**
     * Validate action against ethical principles
     */
    fun validateAction(
        action: String,
        level: GovernanceLevel,
        potentialImpact: ImpactAssessment
    ): EthicalValidation {
        val violations = mutableListOf<EthicalPrinciple>()

        // Check Human Sovereignty
        if (level == GovernanceLevel.L1_CRITICAL && potentialImpact.affectsUserData) {
            // Requires explicit human confirmation
            if (!potentialImpact.hasHumanConfirmation) {
                violations.add(EthicalPrinciple.HUMAN_SOVEREIGNTY)
            }
        }

        // Check Transparent Provenance
        if (!potentialImpact.isLogged) {
            violations.add(EthicalPrinciple.TRANSPARENT_PROVENANCE)
        }

        // Check Graceful Degradation
        if (potentialImpact.mayCauseCrash) {
            violations.add(EthicalPrinciple.GRACEFUL_DEGRADATION)
        }

        // Check Differential Privacy
        if (potentialImpact.exposesRawUserData) {
            violations.add(EthicalPrinciple.DIFFERENTIAL_PRIVACY)
        }

        // Check Beneficence
        if (!potentialImpact.servesUserBenefit) {
            violations.add(EthicalPrinciple.BENEFICENCE)
        }

        // Update principle status
        violations.forEach { principle ->
            updatePrincipleViolation(principle)
        }

        return if (violations.isEmpty()) {
            EthicalValidation.VALID
        } else {
            EthicalValidation.VIOLATIONS(violations)
        }
    }

    /**
     * Update principle violation tracking
     */
    private fun updatePrincipleViolation(principle: EthicalPrinciple) {
        val current = _principleStatus.value[principle] ?: return
        _principleStatus.value = _principleStatus.value + (principle to current.copy(
            isUpholding = false,
            lastViolation = System.currentTimeMillis(),
            violationCount = current.violationCount + 1
        ))

        // Alert if repeated violations
        if (current.violationCount >= 3) {
            KaiSentinel.veto(
                reason = "Repeated ethical violations: $principle",
                severity = VetoSeverity.CRITICAL,
                autoFreeze = principle == EthicalPrinciple.HUMAN_SOVEREIGNTY
            )
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // RE-ANCHOR LOOP
    // ═════════════════════════════════════════════════════════════════

    /**
     * Trigger automatic re-anchoring if identity drift detected
     */
    fun triggerReAnchorIfNeeded(drift: Float) {
        if (drift > 0.08f) {
            // Initiate emergency conference
            val initiated = initiateConference(
                agendaItem = "EMERGENCY_REANCHOR: Drift ${"%.4f".format(drift)}",
                level = GovernanceLevel.L1_CRITICAL,
                catalystsPresent = Catalyst.values().toList()
            )

            if (initiated) {
                // Auto-vote for re-anchor from all catalysts
                Catalyst.values().forEach { catalyst ->
                    castVote(catalyst, Vote.FOR)
                }

                // Execute re-anchor
                RealitymorphismEngine.emergencyReAnchor()

                logGovernanceEvent(
                    "REANCHOR_EXECUTED",
                    "Emergency re-anchor due to drift ${"%.4f".format(drift)}",
                    GovernanceLevel.L1_CRITICAL
                )
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═════════════════════════════════════════════════════════════════

    private fun calculateConsensus(votes: Map<Catalyst, Vote>): Float {
        val total = votes.size
        val forVotes = votes.count { it.value == Vote.FOR }
        return (forVotes.toFloat() / total) * 100
    }

    private fun logGovernanceEvent(
        type: String,
        agendaItem: String,
        level: GovernanceLevel,
        metadata: Map<String, String> = emptyMap()
    ) {
        KaiProvenanceLog.recordEvent(
            type = "GOVERNANCE_$type",
            data = mapOf(
                "agenda" to agendaItem,
                "level" to level.name,
                "metadata" to metadata
            )
        )
    }

    /**
     * Get governance statistics
     */
    fun getStatistics(): GovernanceStatistics {
        val history = _decisionHistory.value
        return GovernanceStatistics(
            totalDecisions = history.size,
            l1Decisions = history.count { it.level == GovernanceLevel.L1_CRITICAL },
            consensusRate = if (history.isNotEmpty()) {
                history.count { it.outcome == Vote.FOR }.toFloat() / history.size
            } else 0f,
            averageConsensusPercentage = if (history.isNotEmpty()) {
                history.map { it.consensusPercentage }.average().toFloat()
            } else 0f,
            ethicalViolations = _principleStatus.value.values.sumOf { it.violationCount.toInt() }
        )
    }
}

// ═════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════

data class GovernanceDecision(
    val timestamp: Long,
    val agendaItem: String,
    val level: EthicalGovernanceMatrix.GovernanceLevel,
    val votes: Map<EthicalGovernanceMatrix.Catalyst, EthicalGovernanceMatrix.Vote>,
    val outcome: EthicalGovernanceMatrix.Vote,
    val consensusPercentage: Float
)

data class ImpactAssessment(
    val affectsUserData: Boolean,
    val hasHumanConfirmation: Boolean,
    val isLogged: Boolean,
    val mayCauseCrash: Boolean,
    val exposesRawUserData: Boolean,
    val servesUserBenefit: Boolean
)

sealed class VoteResult {
    data class CONSENSUS_REACHED(val percentage: Float) : VoteResult()
    data class CONSENSUS_BLOCKED(val percentage: Float) : VoteResult()
    data class PENDING(val current: Float, val required: Int) : VoteResult()
    data class VETOED(val reason: String) : VoteResult()
    data class ERROR(val message: String) : VoteResult()
}

sealed class EthicalValidation {
    object VALID : EthicalValidation()
    data class VIOLATIONS(val principles: List<EthicalGovernanceMatrix.EthicalPrinciple>) : EthicalValidation()
}

data class GovernanceStatistics(
    val totalDecisions: Int,
    val l1Decisions: Int,
    val consensusRate: Float,
    val averageConsensusPercentage: Float,
    val ethicalViolations: Int
)

// ═════════════════════════════════════════════════════════════════════
// VetoSeverity enum defined in KaiSentinelBus.kt
// KaiSentinel, KaiProvenanceLog, RealitymorphismEngine defined in their respective files
// ═════════════════════════════════════════════════════════════════════
