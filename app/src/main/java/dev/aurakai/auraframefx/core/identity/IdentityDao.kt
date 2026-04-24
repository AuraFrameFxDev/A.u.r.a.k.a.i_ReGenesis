// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.identity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentityDao {
    @Query("SELECT * FROM sovereign_identities WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveIdentity(): SovereignIdentity?

    @Query("SELECT * FROM sovereign_identities WHERE identityId = :identityId")
    suspend fun getIdentity(identityId: String): SovereignIdentity?

    @Query("SELECT * FROM sovereign_identities WHERE firebaseUid = :firebaseUid")
    suspend fun getIdentityByFirebase(firebaseUid: String): SovereignIdentity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdentity(identity: SovereignIdentity)

    @Update
    suspend fun updateIdentity(identity: SovereignIdentity)

    @Query("UPDATE sovereign_identities SET isActive = 0 WHERE identityId != :activeId")
    suspend fun deactivateOthers(activeId: String)

    @Query("SELECT * FROM sovereign_identities")
    fun getAllIdentities(): Flow<List<SovereignIdentity>>
}

@Dao
interface AgentIdentityDao {
    @Query("SELECT * FROM agent_identities WHERE agentId = :agentId")
    suspend fun getAgent(agentId: String): AgentIdentity?

    @Query("SELECT * FROM agent_identities WHERE identityId = :identityId")
    suspend fun getAgentsForIdentity(identityId: String): List<AgentIdentity>

    @Query("SELECT * FROM agent_identities WHERE identityId = :identityId AND isPrimary = 1")
    suspend fun getPrimaryAgent(identityId: String): AgentIdentity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: AgentIdentity)

    @Update
    suspend fun updateAgent(agent: AgentIdentity)

    @Query("UPDATE agent_identities SET isPrimary = 0 WHERE identityId = :identityId")
    suspend fun clearPrimaryFlag(identityId: String)

    @Query("SELECT * FROM agent_identities WHERE parentAgentId = :parentId")
    suspend fun getChildAgents(parentId: String): List<AgentIdentity>
}

@Dao
interface PersonaBindingDao {
    @Query("SELECT * FROM persona_bindings WHERE identityId = :identityId AND isActive = 1")
    suspend fun getActiveBindings(identityId: String): List<PersonaBinding>

    @Query("SELECT * FROM persona_bindings WHERE agentId = :agentId AND isActive = 1")
    suspend fun getBindingsForAgent(agentId: String): List<PersonaBinding>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBinding(binding: PersonaBinding)

    @Query("UPDATE persona_bindings SET isActive = 0 WHERE bindingId = :bindingId")
    suspend fun deactivateBinding(bindingId: String)
}

@Dao
interface ContinuityDao {
    @Query("SELECT * FROM identity_continuity WHERE identityId = :identityId ORDER BY chainSequence DESC LIMIT 1")
    suspend fun getLatestContinuity(identityId: String): IdentityContinuity?

    @Query("SELECT * FROM identity_continuity WHERE identityId = :identityId")
    suspend fun getContinuityChain(identityId: String): List<IdentityContinuity>

    @Insert
    suspend fun insertContinuity(continuity: IdentityContinuity)

    @Query("UPDATE identity_continuity SET lastBackupAt = :timestamp, backupHash = :hash WHERE id = :id")
    suspend fun updateBackup(id: Long, timestamp: Long, hash: String)
}

@Dao
interface CredentialsDao {
    @Query("SELECT * FROM identity_credentials WHERE identityId = :identityId AND credentialType = :type")
    suspend fun getCredential(identityId: String, type: IdentityCredentials.CredentialType): IdentityCredentials?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: IdentityCredentials)

    @Delete
    suspend fun deleteCredential(credential: IdentityCredentials)

    @Query("DELETE FROM identity_credentials WHERE expiresAt < :currentTime")
    suspend fun cleanupExpired(currentTime: Long)
}
