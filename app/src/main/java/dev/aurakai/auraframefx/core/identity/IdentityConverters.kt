// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.identity

import androidx.room.TypeConverter

/**
 * 🔄 IDENTITY CONVERTERS
 * 
 * Room TypeConverters for identity enums
 */
class IdentityConverters {

    @TypeConverter
    fun fromPersonaType(value: SovereignIdentity.PersonaType): String = value.name

    @TypeConverter
    fun toPersonaType(value: String): SovereignIdentity.PersonaType = 
        SovereignIdentity.PersonaType.valueOf(value)

    @TypeConverter
    fun fromAuthProvider(value: SovereignIdentity.AuthProvider): String = value.name

    @TypeConverter
    fun toAuthProvider(value: String): SovereignIdentity.AuthProvider = 
        SovereignIdentity.AuthProvider.valueOf(value)

    @TypeConverter
    fun fromCredentialType(value: IdentityCredentials.CredentialType): String = value.name

    @TypeConverter
    fun toCredentialType(value: String): IdentityCredentials.CredentialType = 
        IdentityCredentials.CredentialType.valueOf(value)
}
