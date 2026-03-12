package dev.aurakai.auraframefx.domains.aura

import kotlinx.serialization.Serializable

@Serializable
enum class ConnectorStatus {
    OFFLINE, CONNECTING, ACTIVE, ERROR
}

@Serializable
data class MCPConnector(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: String, // Icon name or resource identifier
    val category: String, // "Filesystem", "Design", "Cloud", "Dev"
    val status: ConnectorStatus = ConnectorStatus.OFFLINE,
    val stats: String = "No data synchronized"
)
