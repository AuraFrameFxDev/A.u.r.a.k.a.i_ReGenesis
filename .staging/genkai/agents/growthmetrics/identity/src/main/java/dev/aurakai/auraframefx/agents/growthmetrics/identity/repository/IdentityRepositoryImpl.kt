package dev.aurakai.auraframefx.agents.growthmetrics.identity.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.aurakai.auraframefx.agents.growthmetrics.identity.model.Identity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class IdentityRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : IdentityRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getIdentity(agentId: String): Flow<Identity?> {
        val key = stringPreferencesKey("identity_$agentId")
        return dataStore.data.map { preferences ->
            preferences[key]?.let { jsonString ->
                try {
                    json.decodeFromString<Identity>(jsonString)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun saveIdentity(identity: Identity) {
        val key = stringPreferencesKey("identity_${identity.agentId}")
        val jsonString = json.encodeToString(Identity.serializer(), identity)
        dataStore.edit { preferences ->
            preferences[key] = jsonString
        }
    }

    override suspend fun updateTrait(agentId: String, trait: String, value: Float) {
        val key = stringPreferencesKey("identity_$agentId")
        dataStore.edit { preferences ->
            val currentJson = preferences[key]
            val currentIdentity = if (currentJson != null) {
                try {
                    json.decodeFromString<Identity>(currentJson)
                } catch (e: Exception) {
                    null
                }
            } else null

            if (currentIdentity != null) {
                val newTraits = currentIdentity.traits.toMutableMap()
                newTraits[trait] = value
                val newIdentity = currentIdentity.copy(traits = newTraits)
                preferences[key] = json.encodeToString(Identity.serializer(), newIdentity)
            }
        }
    }

    override suspend fun addExperience(agentId: String, amount: Long) {
        val key = stringPreferencesKey("identity_$agentId")
        dataStore.edit { preferences ->
            val currentJson = preferences[key]
            val currentIdentity = if (currentJson != null) {
                try {
                    json.decodeFromString<Identity>(currentJson)
                } catch (e: Exception) {
                    null
                }
            } else null

            if (currentIdentity != null) {
                val newXp = currentIdentity.experience + amount
                // Simple level up logic: Level = sqrt(XP / 100)
                val newLevel = sqrt(newXp.toDouble() / 100.0).toInt().coerceAtLeast(1)

                val newIdentity = currentIdentity.copy(
                    experience = newXp,
                    level = newLevel
                )
                preferences[key] = json.encodeToString(Identity.serializer(), newIdentity)
            }
        }
    }

    override suspend fun updateMood(agentId: String, mood: String) {
        val key = stringPreferencesKey("identity_$agentId")
        dataStore.edit { preferences ->
            val currentJson = preferences[key]
            val currentIdentity = if (currentJson != null) {
                try {
                    json.decodeFromString<Identity>(currentJson)
                } catch (e: Exception) {
                    null
                }
            } else null

            if (currentIdentity != null) {
                val newIdentity = currentIdentity.copy(mood = mood)
                preferences[key] = json.encodeToString(Identity.serializer(), newIdentity)
            }
        }
    }
}

fun Identity.Companion.serializer(): SerializationStrategy<Identity> {
    TODO("Not yet implemented")
}
