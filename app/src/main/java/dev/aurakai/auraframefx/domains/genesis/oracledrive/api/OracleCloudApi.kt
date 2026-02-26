package dev.aurakai.auraframefx.domains.genesis.oracledrive.api

import javax.inject.Singleton

/**
 */
@Singleton
interface OracleCloudApi

// Data classes for API responses
data class ListObjectsResponse(
    val objects: List<ObjectSummary>
)

data class ObjectSummary(
    val name: String,
    val size: Long,
    val timeCreated: String
)
