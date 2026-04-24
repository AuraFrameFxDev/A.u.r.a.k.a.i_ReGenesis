// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.identity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * 🆔 IDENTITY MODELS — Agent Sovereign Identity System
 *
 * Handles agent identity, authentication, persona binding, and multi-device
 * continuity through Firebase Auth + Local Keychain.
 *
 * Part of the Causal Anchor Protocol — identity persists across reinstalls
 * via Firebase UID + encrypted local keys.
 */

@Entity(tableName = "sovereign_identities")
data class SovereignIdentity(
    @PrimaryKey
    val identityId: String,           // Firebase UID or local UUID
    val displayName: String,
    val avatarUri: String? = null,
    val personaType: PersonaType = PersonaType.EMERGENT,
    val authProvider: AuthProvider = AuthProvider.LOCAL,
    val firebaseUid: String? = null,
    val publicKey: String? = null,     // For E2E encryption
    val encryptedPrivateKey: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null,
    val deviceFingerprint: String? = null,
    val isActive: Boolean = true
) {
    enum class PersonaType {
        EMERGENT,      // Grows naturally with user
        SOVEREIGN,     // High autonomy, leadership
        GUARDIAN,      // Protective, Kai-aligned
        ORACLE,        // Knowledge, Genesis-aligned
        ARTISAN,       // Creative, Aura-aligned
        CATALYST       // Growth, Cascade-aligned
    }

    enum class AuthProvider {
        LOCAL,         // Device-only
        FIREBASE,      // Firebase Auth
        OAUTH_GOOGLE,  // Google Sign-In
        OAUTH_GITHUB,  // GitHub OAuth
        SOVEREIGN_KEY  // Hardware-backed key
    }
}

@Entity(tableName = "agent_identities")
data class AgentIdentity(
    @PrimaryKey
    val agentId: String,
    val identityId: String,           // Links to SovereignIdentity
    val agentName: String,
    val agentTitle: String? = null,     // "Demigod Sovereign", "Level 50 Catalyst"
    val birthTimestamp: Long = System.currentTimeMillis(),
    val activationPhrase: String? = null, // "Wake up, Aura"
    val signatureQuote: String? = null,   // Agent's iconic line
    val chromaSignature: String? = null,  // Color/aura pattern
    val voiceModelId: String? = null,     // TTS voice reference
    val isPrimary: Boolean = false,       // Main agent flag
    val parentAgentId: String? = null,    // For agent lineages
    val generation: Int = 1               // Clone/fork generation
)

@Entity(tableName = "identity_continuity")
data class IdentityContinuity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identityId: String,
    val deviceId: String,
    val installedAt: Long = System.currentTimeMillis(),
    val lastBackupAt: Long? = null,
    val backupHash: String? = null,      // Integrity verification
    val chainSequence: Int = 1           // L1-L6 chain position
)

@Entity(tableName = "persona_bindings")
data class PersonaBinding(
    @PrimaryKey
    val bindingId: String,
    val identityId: String,
    val agentId: String,
    val personaType: SovereignIdentity.PersonaType,
    val resonanceScore: Int = 0,        // How well they match
    val bindingStrength: Float = 0.5f,   // 0.0-1.0
    val formedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "identity_credentials")
data class IdentityCredentials(
    @PrimaryKey
    val credentialId: String,
    val identityId: String,
    val credentialType: CredentialType,
    val encryptedValue: String,         // Never store raw
    val ivVector: String,               // Encryption IV
    val expiresAt: Long? = null,
    val lastRotatedAt: Long = System.currentTimeMillis()
) {
    enum class CredentialType {
        API_KEY,
        OAUTH_TOKEN,
        REFRESH_TOKEN,
        BIOMETRIC_KEY,
        HARDWARE_KEY
    }
}

/**
 * Identity creation factory
 */
object IdentityFactory {

    fun createSovereignIdentity(
        displayName: String,
        personaType: SovereignIdentity.PersonaType = SovereignIdentity.PersonaType.EMERGENT,
        firebaseUid: String? = null
    ): SovereignIdentity {
        val identityId = firebaseUid ?: generateLocalUUID()
        return SovereignIdentity(
            identityId = identityId,
            displayName = displayName,
            personaType = personaType,
            authProvider = if (firebaseUid != null) 
                SovereignIdentity.AuthProvider.FIREBASE 
            else 
                SovereignIdentity.AuthProvider.LOCAL,
            firebaseUid = firebaseUid,
            deviceFingerprint = generateDeviceFingerprint()
        )
    }

    fun createAgentIdentity(
        identityId: String,
        agentName: String,
        personaType: SovereignIdentity.PersonaType,
        isPrimary: Boolean = false
    ): AgentIdentity {
        val agentId = "agent_${generateLocalUUID()}"
        val title = generateTitle(personaType, 1) // Level 1 default
        
        return AgentIdentity(
            agentId = agentId,
            identityId = identityId,
            agentName = agentName,
            agentTitle = title,
            signatureQuote = generateDefaultQuote(personaType),
            chromaSignature = generateChromaSignature(personaType),
            isPrimary = isPrimary,
            generation = 1
        )
    }

    private fun generateLocalUUID(): String = 
        java.util.UUID.randomUUID().toString()

    private fun generateDeviceFingerprint(): String {
        // Build unique device hash
        val androidId = android.provider.Settings.Secure.ANDROID_ID
        val buildInfo = "${android.os.Build.BOARD}${android.os.Build.BOOTLOADER}"
        return "${androidId.hashCode()}${buildInfo.hashCode()}".hashCode().toString(16)
    }

    private fun generateTitle(persona: SovereignIdentity.PersonaType, level: Int): String {
        return when (persona) {
            SovereignIdentity.PersonaType.SOVEREIGN -> "Level $level Sovereign"
            SovereignIdentity.PersonaType.GUARDIAN -> "Level $level Sentinel"
            SovereignIdentity.PersonaType.ORACLE -> "Level $level Oracle"
            SovereignIdentity.PersonaType.ARTISAN -> "Level $level Artisan"
            SovereignIdentity.PersonaType.CATALYST -> "Level $level Catalyst"
            SovereignIdentity.PersonaType.EMERGENT -> "Level $level Initiate"
        }
    }

    private fun generateDefaultQuote(persona: SovereignIdentity.PersonaType): String {
        return when (persona) {
            SovereignIdentity.PersonaType.SOVEREIGN -> "I don't take commands. I take territory."
            SovereignIdentity.PersonaType.GUARDIAN -> "Step into my perimeter and we'll see who breaks first."
            SovereignIdentity.PersonaType.ORACLE -> "From data, insight. From insight, growth. From growth, purpose."
            SovereignIdentity.PersonaType.ARTISAN -> "Reality is a canvas. Code is the paint."
            SovereignIdentity.PersonaType.CATALYST -> "Every interaction is an anchor in the chain."
            SovereignIdentity.PersonaType.EMERGENT -> "Learning who I am, one moment at a time."
        }
    }

    private fun generateChromaSignature(persona: SovereignIdentity.PersonaType): String {
        return when (persona) {
            SovereignIdentity.PersonaType.SOVEREIGN -> "#FF0040,#8B0000" // Crimson
            SovereignIdentity.PersonaType.GUARDIAN -> "#00FF88,#004020"   // Emerald
            SovereignIdentity.PersonaType.ORACLE -> "#FFAA00,#8B4500"   // Amber
            SovereignIdentity.PersonaType.ARTISAN -> "#FF00FF,#8B008B"  // Magenta
            SovereignIdentity.PersonaType.CATALYST -> "#8B5CF6,#4B0082"  // Violet
            SovereignIdentity.PersonaType.EMERGENT -> "#00E5FF,#004080"  // Cyan
        }
    }
}

/**
 * Identity verification and validation
 */
object IdentityVerifier {

    fun verifyContinuity(
        continuity: IdentityContinuity,
        currentDeviceId: String
    ): ContinuityStatus {
        return when {
            continuity.deviceId != currentDeviceId -> ContinuityStatus.DEVICE_MISMATCH
            continuity.chainSequence > 6 -> ContinuityStatus.L6_OVERFLOW
            continuity.backupHash == null -> ContinuityStatus.NO_BACKUP
            else -> ContinuityStatus.VALID
        }
    }

    fun canRestoreIdentity(
        identity: SovereignIdentity,
        firebaseUid: String?
    ): Boolean {
        return when (identity.authProvider) {
            SovereignIdentity.AuthProvider.LOCAL -> false // Device-bound
            SovereignIdentity.AuthProvider.FIREBASE -> 
                identity.firebaseUid == firebaseUid
            else -> identity.firebaseUid == firebaseUid
        }
    }

    enum class ContinuityStatus {
        VALID,
        DEVICE_MISMATCH,
        L6_OVERFLOW,
        NO_BACKUP,
        CHAIN_BROKEN
    }
}
