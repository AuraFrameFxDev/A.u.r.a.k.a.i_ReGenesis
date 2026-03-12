package dev.aurakai.auraframefx.domains.genesis.network.api

import dev.aurakai.auraframefx.domains.genesis.network.KtorClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Example API service demonstrating Ktor client usage.
 * Replace with your actual API endpoints and data models.
 */
@Singleton
class ExampleApiService @Inject constructor(
    private val ktorClient: KtorClient
) {

    /**
     * Example GET request to fetch data.
     * 
     * @param id The ID of the resource to fetch
     * @return The response as a String
     */
    suspend fun fetchExampleData(id: String): String {
        return ktorClient.client.get("https://api.example.com/data/") {
            parameter("id", id)
            header("X-Custom-Header", "CustomValue")
        }.bodyAsText()
    }

    /**
     * Example POST request to send data.
     * 
     * @param data The data to send
     * @return The response as a String
     */
    suspend fun postExampleData(data: ExampleData): String {
        return ktorClient.client.post("https://api.example.com/data/") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.bodyAsText()
    }

    /**
     * Example PUT request to update data.
     * 
     * @param id The ID of the resource to update
     * @param data The updated data
     * @return The response as a String
     */
    suspend fun updateExampleData(id: String, data: ExampleData): String {
        return ktorClient.client.put("https://api.example.com/data/") {
            parameter("id", id)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.bodyAsText()
    }

    /**
     * Example DELETE request to remove data.
     * 
     * @param id The ID of the resource to delete
     */
    suspend fun deleteExampleData(id: String) {
        ktorClient.client.delete("https://api.example.com/data/") {
            parameter("id", id)
        }
    }
}

/**
 * Example data class for API requests/responses.
 * Replace with your actual data models.
 */
data class ExampleData(
    val id: String,
    val name: String,
    val description: String,
    val value: Int
)

