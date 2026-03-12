package dev.aurakai.auraframefx.ai.clients

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for Web Search capabilities
 */
interface WebSearchClient {
    suspend fun search(query: String): List<SearchResult>
}

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String
)

/**
 * Default implementation of WebSearchClient
 * Currently a mock, but ready for API integration (e.g., Google Search API, Bing API)
 */
@Singleton
class DefaultWebSearchClient @Inject constructor() : WebSearchClient {
    override suspend fun search(query: String): List<SearchResult> {
        // Mock results for now
        return listOf(
            SearchResult(
                title = "Result for $query",
                url = "https://example.com/search?q=$query",
                snippet = "This is a simulated search result for the query: $query. In a real implementation, this would come from a search engine API."
            ),
            SearchResult(
                title = "Advanced $query Techniques",
                url = "https://example.com/advanced/$query",
                snippet = "Learn advanced techniques and best practices for $query. Comprehensive guide and documentation."
            ),
            SearchResult(
                title = "$query - Wikipedia",
                url = "https://en.wikipedia.org/wiki/$query",
                snippet = "Encyclopedic entry for $query, covering history, usage, and related concepts."
            )
        )
    }
}
