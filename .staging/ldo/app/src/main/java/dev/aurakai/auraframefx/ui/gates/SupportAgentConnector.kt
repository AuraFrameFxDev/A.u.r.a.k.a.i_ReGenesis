package dev.aurakai.auraframefx.ui.gates

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * SupportAgentConnector
 */
interface SupportAgentConnector {
    val incomingMessages: SharedFlow<SupportMessage>
    fun sendMessage(text: String)
    fun setOnAgentName(name: String)
    fun shutdown()
}

/**
 * HTTP-backed connector that posts JSON to the Genesis API (/genesis/chat).
 * Expects the server to return JSON with a 'response' field containing the text reply.
 */
class VertexAgentConnector(
    private val baseUrl: String,
    private val apiKey: String?
) : SupportAgentConnector {
    override val incomingMessages = MutableSharedFlow<SupportMessage>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var agentName: String = "GenesisAgent"
    private var closed = false

    override fun sendMessage(text: String) {
        if (closed) return
        coroutineScope.launch {
            try {
                val url = URL("$baseUrl/genesis/chat")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 5000
                conn.readTimeout = 20000
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                apiKey?.let { conn.setRequestProperty("Authorization", "Bearer $it") }

                val payload = JSONObject()
                payload.put("message", text)
                payload.put("user_id", "android_user_${UUID.randomUUID()}")

                OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                val reader = if (responseCode in 200..299) {
                    BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8))
                } else {
                    BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream, Charsets.UTF_8))
                }

                val respText = StringBuilder()
                reader.useLines { lines -> lines.forEach { respText.append(it).append('\n') } }
                val respStr = respText.toString().trim()

                // Parse JSON response and extract 'response' text
                val reply = try {
                    val json = JSONObject(respStr)
                    when {
                        json.has("response") -> json.getString("response")
                        json.has("message") -> json.getString("message")
                        else -> respStr
                    }
                } catch (e: Exception) {
                    respStr
                }

                incomingMessages.emit(SupportMessage(reply, agentName, false, "Now"))

            } catch (t: Throwable) {
                Log.w("VertexAgentConnector", "Failed to send message to $baseUrl", t)
                incomingMessages.emit(
                    SupportMessage(
                        "Failed to reach live agent: ${t.message}",
                        agentName,
                        false,
                        "Now"
                    )
                )
            }
        }
    }

    override fun setOnAgentName(name: String) {
        agentName = name
    }

    override fun shutdown() {
        closed = true
    }
}
