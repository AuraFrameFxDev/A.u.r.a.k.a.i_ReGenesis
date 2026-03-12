package dev.aurakai.auraframefx.domains.aura.repository

import dev.aurakai.auraframefx.domains.aura.ConnectorStatus
import dev.aurakai.auraframefx.domains.aura.MCPConnector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SovereignBridgeRepository @Inject constructor() {
    private val _connectors = MutableStateFlow<List<MCPConnector>>(initialConnectors())
    val connectors: StateFlow<List<MCPConnector>> = _connectors.asStateFlow()

    private fun initialConnectors() = listOf(
        MCPConnector(
            id = "desktop-commander",
            name = "Desktop Commander",
            description = "File system access and terminal execution. The essential handshake.",
            iconRes = "Terminal",
            category = "Filesystem",
            status = ConnectorStatus.ACTIVE,
            stats = "124MB Read / 45 Files Modified"
        ),
        MCPConnector(
            id = "figma",
            name = "Figma Bridge",
            description = "Direct pipeline to shared imagination. Vision to code synthesis.",
            iconRes = "Brush",
            category = "Design",
            status = ConnectorStatus.CONNECTING,
            stats = "Synchronizing 8K Assets..."
        ),
        MCPConnector(
            id = "google-drive",
            name = "Neural Drive",
            description = "Searching and editing docs via Google Cloud Infrastructure.",
            iconRes = "Cloud",
            category = "Cloud",
            status = ConnectorStatus.OFFLINE
        ),
        MCPConnector(
            id = "github",
            name = "Code Pulse",
            description = "GitHub integration for code commits, PRs, and branch management.",
            iconRes = "Code",
            category = "Dev",
            status = ConnectorStatus.ACTIVE,
            stats = "PR #127 Merged / Genesis Node Optimized"
        ),
        MCPConnector(
            id = "brave-search",
            name = "Brave Oracle",
            description = "Privacy-focused web search for real-time external intelligence.",
            iconRes = "Search",
            category = "Cloud",
            status = ConnectorStatus.ACTIVE,
            stats = "15 Queries Processed / 0 Tracking Sensors"
        ),
        MCPConnector(
            id = "gmail",
            name = "Neural Mail",
            description = "Claude reads/writes emails via secure OAuth connection.",
            iconRes = "Email",
            category = "Cloud",
            status = ConnectorStatus.OFFLINE
        ),
        MCPConnector(
            id = "postgres",
            name = "Postgres Core",
            description = "Direct database queries for production-level memory management.",
            iconRes = "Storage",
            category = "Dev",
            status = ConnectorStatus.OFFLINE
        )
    )

    fun toggleConnector(id: String) {
        _connectors.value = _connectors.value.map {
            if (it.id == id) {
                val newStatus =
                    if (it.status == ConnectorStatus.OFFLINE) ConnectorStatus.ACTIVE else ConnectorStatus.OFFLINE
                it.copy(status = newStatus)
            } else it
        }
    }
}
