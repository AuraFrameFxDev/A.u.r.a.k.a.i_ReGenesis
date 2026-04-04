package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

/**
 * Thrown when an agent attempts to access a capability gated by a Pandora tier
 * that is not currently unlocked.
 */
class PandoraAccessDeniedException(
    val requiredTier: UnlockTier,
    val currentTier: UnlockTier,
    message: String = "Access denied: Required tier $requiredTier but current is $currentTier"
) : SecurityException(message)

/**
 * Thrown when a provenance chain fails verification.
 */
class ProvenanceInvalidException(
    val reason: String,
    message: String = "Provenance verification failed: $reason"
) : SecurityException(message)
