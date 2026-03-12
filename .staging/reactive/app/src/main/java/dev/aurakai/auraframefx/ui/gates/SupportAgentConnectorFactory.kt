package dev.aurakai.auraframefx.ui.gates

/**
 * Factory that creates a SupportAgentConnector implementation.
 *
 * Priority for base URL (should be base, connector appends /genesis/chat):
 * 1) VERTEX_ENDPOINT env var (e.g. https://your-host.example.com)
 * 2) LOCAL_EMULATOR_ENDPOINT env var
 * 3) Default emulator loopback http://10.0.2.2:5000 (Genesis Flask dev server)
 */
object SupportAgentConnectorFactory {
    fun create(): SupportAgentConnector {
        val vertexEndpoint = System.getenv("VERTEX_ENDPOINT")
        val vertexApiKey = System.getenv("VERTEX_API_KEY")
        val localEndpoint = System.getenv("LOCAL_EMULATOR_ENDPOINT")

        if (!vertexEndpoint.isNullOrBlank()) {
            return VertexAgentConnector(vertexEndpoint.trimEnd('/'), vertexApiKey)
        }

        if (!localEndpoint.isNullOrBlank()) {
            return VertexAgentConnector(localEndpoint.trimEnd('/'), null)
        }

        // Default to Android emulator loopback; Genesis Flask dev server listens on :5000
        val emulatorDefault = "http://10.0.2.2:5000"
        return VertexAgentConnector(emulatorDefault, null)
    }
}
