package dev.aurakai.auraframefx

import dev.aurakai.auraframefx.model.AiRequest
import dev.aurakai.auraframefx.security.SecurityContext
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Basic Trinity system integration tests
 */
class TrinitySystemTest {

    @Test
    fun testAiRequestModel() {
        val request = AiRequest(
            query = "Test query",
            type = "text",
            context = mapOf("test" to "context")
        )

        assertEquals("Test query", request.query)
        assertEquals("text", request.type)
        assertEquals("context", request.context["test"])
    }

    @Test
    fun testSecurityContextValidation() {
        val securityContext = SecurityContext()

        // Should not throw exception for valid content
        securityContext.validateContent("This is valid content")

        // Should not throw exception for valid image data
        val testImageData = ByteArray(100) { it.toByte() }
        securityContext.validateImageData(testImageData)
    }
}
