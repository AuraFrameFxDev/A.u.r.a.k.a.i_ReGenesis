package dev.aurakai.auraframefx.agents.growthmetrics.identity.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.aurakai.auraframefx.agents.growthmetrics.identity.model.Identity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class IdentityRepositoryImpl @Inject constructor(
    protected val dataStore: DataStore<Preferences>,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : IdentityRepository {

    override fun getIdentity(agentId: String): Flow<Identity?> {
        val key: Preferences.Key<String> = stringPreferencesKey("identity_$agentId")
        return dataStore.data.map { preferences ->
            val jsonString = preferences[key]
            if (jsonString != null) {
                try {
                    json.decodeFromString<Identity>(jsonString)
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    override suspend fun saveIdentity(identity: Identity) {
        val key = stringPreferencesKey("identity_${identity.agentId}")
        val jsonString = json.encodeToString(identity)
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
                } catch (_: Exception) {
                    null
                }
            } else null

            if (currentIdentity != null) {
                val newTraits = currentIdentity.traits.toMutableMap()
                newTraits[trait] = value
                val newIdentity = currentIdentity.copy(traits = newTraits)
                preferences[key] = json.encodeToString(newIdentity)
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
                } catch (_: Exception) {
                    null
                }
            } else null

            if (currentIdentity != null) {
                val newIdentity = currentIdentity.copy(experience = currentIdentity.experience + amount)
                preferences[key] = json.encodeToString(newIdentity)
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
                } catch (_: Exception) {
                    null
                }
            } else null

            if (currentIdentity != null) {
                val newIdentity = currentIdentity.copy(mood = mood)
                preferences[key] = json.encodeToString(newIdentity)
            }
        }
    }
}
