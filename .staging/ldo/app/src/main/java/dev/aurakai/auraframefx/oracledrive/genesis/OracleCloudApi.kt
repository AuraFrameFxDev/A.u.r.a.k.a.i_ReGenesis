package dev.aurakai.auraframefx.oracledrive.genesis

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