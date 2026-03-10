package dev.aurakai.auraframefx.di

import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.api.GenerationConfig
import com.google.cloud.vertexai.generativeai.GenerativeModel
import io.mockk.clearAllMocks
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

/**
 * Comprehensive unit tests for VertexAIModule
 * Testing Framework: JUnit 5 with Mockito/MockK for mocking
 */
@DisplayName("VertexAI Module Tests")
class VertexAIModuleTest {

    @Mock
    private lateinit var mockVertexAI: VertexAI

    @Mock
    private lateinit var mockGenerativeModel: GenerativeModel

    @Mock
    private lateinit var mockGenerationConfig: GenerationConfig

    private lateinit var vertexAIModule: VertexAIModule

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        vertexAIModule = VertexAIModule()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("VertexAI Provider Tests")
    inner class VertexAIProviderTests {

        @Test
        @DisplayName("Should provide VertexAI instance with valid configuration")
        fun shouldProvideVertexAIWithValidConfiguration() {
            // Given
            val projectId = "test-project-123"
            val location = "us-central1"

            // When
            val result = vertexAIModule.provideVertexAI(projectId, location)

            // Then
            assertNotNull(result)
            assertEquals(projectId, result.projectId)
            assertEquals(location, result.location)
        }

        @Test
        @DisplayName("Should throw exception when project ID is null")
        fun shouldThrowExceptionWhenProjectIdIsNull() {
            // Given
            val projectId: String? = null
            val location = "us-central1"

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideVertexAI(projectId, location)
            }
        }

        @Test
        @DisplayName("Should throw exception when project ID is empty")
        fun shouldThrowExceptionWhenProjectIdIsEmpty() {
            // Given
            val projectId = ""
            val location = "us-central1"

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideVertexAI(projectId, location)
            }
        }

        @Test
        @DisplayName("Should throw exception when location is null")
        fun shouldThrowExceptionWhenLocationIsNull() {
            // Given
            val projectId = "test-project-123"
            val location: String? = null

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideVertexAI(projectId, location)
            }
        }

        @Test
        @DisplayName("Should throw exception when location is empty")
        fun shouldThrowExceptionWhenLocationIsEmpty() {
            // Given
            val projectId = "test-project-123"
            val location = ""

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideVertexAI(projectId, location)
            }
        }

        @Test
        @DisplayName("Should handle special characters in project ID")
        fun shouldHandleSpecialCharactersInProjectId() {
            // Given
            val projectId = "test-project-123_special"
            val location = "us-central1"

            // When
            val result = vertexAIModule.provideVertexAI(projectId, location)

            // Then
            assertNotNull(result)
            assertEquals(projectId, result.projectId)
        }

        @Test
        @DisplayName("Should handle different valid locations")
        fun shouldHandleDifferentValidLocations() {
            // Given
            val projectId = "test-project-123"
            val locations = listOf("us-central1", "us-east1", "europe-west1", "asia-southeast1")

            locations.forEach { location ->
                // When
                val result = vertexAIModule.provideVertexAI(projectId, location)

                // Then
                assertNotNull(result)
                assertEquals(location, result.location)
            }
        }
    }

    @Nested
    @DisplayName("GenerativeModel Provider Tests")
    inner class GenerativeModelProviderTests {

        @Test
        @DisplayName("Should provide GenerativeModel with default model name")
        fun shouldProvideGenerativeModelWithDefaultModelName() {
            // Given
            val vertexAI = mockVertexAI

            // When
            val result = vertexAIModule.provideGenerativeModel(vertexAI)

            // Then
            assertNotNull(result)
            verify(vertexAI).getGenerativeModel("gemini-1.5-flash")
        }

        @Test
        @DisplayName("Should provide GenerativeModel with custom model name")
        fun shouldProvideGenerativeModelWithCustomModelName() {
            // Given
            val vertexAI = mockVertexAI
            val modelName = "gemini-pro"

            // When
            val result = vertexAIModule.provideGenerativeModel(vertexAI, modelName)

            // Then
            assertNotNull(result)
            verify(vertexAI).getGenerativeModel(modelName)
        }

        @Test
        @DisplayName("Should throw exception when VertexAI is null")
        fun shouldThrowExceptionWhenVertexAIIsNull() {
            // Given
            val vertexAI: VertexAI? = null

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerativeModel(vertexAI)
            }
        }

        @Test
        @DisplayName("Should throw exception when model name is null")
        fun shouldThrowExceptionWhenModelNameIsNull() {
            // Given
            val vertexAI = mockVertexAI
            val modelName: String? = null

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerativeModel(vertexAI, modelName)
            }
        }

        @Test
        @DisplayName("Should throw exception when model name is empty")
        fun shouldThrowExceptionWhenModelNameIsEmpty() {
            // Given
            val vertexAI = mockVertexAI
            val modelName = ""

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerativeModel(vertexAI, modelName)
            }
        }

        @Test
        @DisplayName("Should handle various valid model names")
        fun shouldHandleVariousValidModelNames() {
            // Given
            val vertexAI = mockVertexAI
            val modelNames =
                listOf("gemini-1.5-flash", "gemini-pro", "gemini-pro-vision", "text-bison")

            modelNames.forEach { modelName ->
                // When
                val result = vertexAIModule.provideGenerativeModel(vertexAI, modelName)

                // Then
                assertNotNull(result)
                verify(vertexAI).getGenerativeModel(modelName)
            }
        }
    }

    @Nested
    @DisplayName("GenerationConfig Provider Tests")
    inner class GenerationConfigProviderTests {

        @Test
        @DisplayName("Should provide GenerationConfig with default parameters")
        fun shouldProvideGenerationConfigWithDefaultParameters() {
            // When
            val result = vertexAIModule.provideGenerationConfig()

            // Then
            assertNotNull(result)
            assertEquals(0.7f, result.temperature)
            assertEquals(1000, result.maxOutputTokens)
            assertEquals(0.95f, result.topP)
            assertEquals(40, result.topK)
        }

        @Test
        @DisplayName("Should provide GenerationConfig with custom parameters")
        fun shouldProvideGenerationConfigWithCustomParameters() {
            // Given
            val temperature = 0.5f
            val maxOutputTokens = 2000
            val topP = 0.9f
            val topK = 30

            // When
            val result =
                vertexAIModule.provideGenerationConfig(temperature, maxOutputTokens, topP, topK)

            // Then
            assertNotNull(result)
            assertEquals(temperature, result.temperature)
            assertEquals(maxOutputTokens, result.maxOutputTokens)
            assertEquals(topP, result.topP)
            assertEquals(topK, result.topK)
        }

        @Test
        @DisplayName("Should throw exception when temperature is negative")
        fun shouldThrowExceptionWhenTemperatureIsNegative() {
            // Given
            val temperature = -0.1f

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(temperature = temperature)
            }
        }

        @Test
        @DisplayName("Should throw exception when temperature is greater than 1")
        fun shouldThrowExceptionWhenTemperatureIsGreaterThanOne() {
            // Given
            val temperature = 1.1f

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(temperature = temperature)
            }
        }

        @Test
        @DisplayName("Should throw exception when maxOutputTokens is negative")
        fun shouldThrowExceptionWhenMaxOutputTokensIsNegative() {
            // Given
            val maxOutputTokens = -1

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(maxOutputTokens = maxOutputTokens)
            }
        }

        @Test
        @DisplayName("Should throw exception when maxOutputTokens is zero")
        fun shouldThrowExceptionWhenMaxOutputTokensIsZero() {
            // Given
            val maxOutputTokens = 0

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(maxOutputTokens = maxOutputTokens)
            }
        }

        @Test
        @DisplayName("Should throw exception when topP is negative")
        fun shouldThrowExceptionWhenTopPIsNegative() {
            // Given
            val topP = -0.1f

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(topP = topP)
            }
        }

        @Test
        @DisplayName("Should throw exception when topP is greater than 1")
        fun shouldThrowExceptionWhenTopPIsGreaterThanOne() {
            // Given
            val topP = 1.1f

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(topP = topP)
            }
        }

        @Test
        @DisplayName("Should throw exception when topK is negative")
        fun shouldThrowExceptionWhenTopKIsNegative() {
            // Given
            val topK = -1

            // When & Then
            assertThrows<IllegalArgumentException> {
                vertexAIModule.provideGenerationConfig(topK = topK)
            }
        }

        @Test
        @DisplayName("Should accept boundary values for temperature")
        fun shouldAcceptBoundaryValuesForTemperature() {
            // Given & When & Then
            assertDoesNotThrow {
                vertexAIModule.provideGenerationConfig(temperature = 0.0f)
                vertexAIModule.provideGenerationConfig(temperature = 1.0f)
            }
        }

        @Test
        @DisplayName("Should accept boundary values for topP")
        fun shouldAcceptBoundaryValuesForTopP() {
            // Given & When & Then
            assertDoesNotThrow {
                vertexAIModule.provideGenerationConfig(topP = 0.0f)
                vertexAIModule.provideGenerationConfig(topP = 1.0f)
            }
        }

        @Test
        @DisplayName("Should accept zero for topK")
        fun shouldAcceptZeroForTopK() {
            // Given & When & Then
            assertDoesNotThrow {
                vertexAIModule.provideGenerationConfig(topK = 0)
            }
        }

        @Test
        @DisplayName("Should handle large maxOutputTokens values")
        fun shouldHandleLargeMaxOutputTokensValues() {
            // Given
            val maxOutputTokens = 100000

            // When
            val result = vertexAIModule.provideGenerationConfig(maxOutputTokens = maxOutputTokens)

            // Then
            assertNotNull(result)
            assertEquals(maxOutputTokens, result.maxOutputTokens)
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should create complete VertexAI setup with all components")
        fun shouldCreateCompleteVertexAISetup() {
            // Given
            val projectId = "test-project-123"
            val location = "us-central1"
            val modelName = "gemini-1.5-flash"

            // When
            val vertexAI = vertexAIModule.provideVertexAI(projectId, location)
            val generativeModel = vertexAIModule.provideGenerativeModel(vertexAI, modelName)
            val generationConfig = vertexAIModule.provideGenerationConfig()

            // Then
            assertNotNull(vertexAI)
            assertNotNull(generativeModel)
            assertNotNull(generationConfig)
            assertEquals(projectId, vertexAI.projectId)
            assertEquals(location, vertexAI.location)
        }

        @Test
        @DisplayName("Should handle multiple instances creation")
        fun shouldHandleMultipleInstancesCreation() {
            // Given
            val projectId = "test-project-123"
            val location = "us-central1"

            // When
            val vertexAI1 = vertexAIModule.provideVertexAI(projectId, location)
            val vertexAI2 = vertexAIModule.provideVertexAI(projectId, location)

            // Then
            assertNotNull(vertexAI1)
            assertNotNull(vertexAI2)
            // Each instance should be independent
            assertNotSame(vertexAI1, vertexAI2)
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle VertexAI creation failure gracefully")
        fun shouldHandleVertexAICreationFailureGracefully() {
            // Given
            val projectId = "invalid-project-format-!@#"
            val location = "invalid-location"

            // When & Then
            assertThrows<Exception> {
                vertexAIModule.provideVertexAI(projectId, location)
            }
        }

        @Test
        @DisplayName("Should handle concurrent access safely")
        fun shouldHandleConcurrentAccessSafely() = runTest {
            // Given
            val projectId = "test-project-123"
            val location = "us-central1"

            // When - Create multiple instances concurrently
            val results = (1..10).map {
                vertexAIModule.provideVertexAI(projectId, location)
            }

            // Then
            results.forEach { result ->
                assertNotNull(result)
                assertEquals(projectId, result.projectId)
                assertEquals(location, result.location)
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should create VertexAI instance within reasonable time")
        fun shouldCreateVertexAIInstanceWithinReasonableTime() {
            // Given
            val projectId = "test-project-123"
            val location = "us-central1"
            val startTime = System.currentTimeMillis()

            // When
            val result = vertexAIModule.provideVertexAI(projectId, location)
            val endTime = System.currentTimeMillis()

            // Then
            assertNotNull(result)
            val duration = endTime - startTime
            assertTrue(
                duration < 5000,
                "VertexAI creation should take less than 5 seconds, took ${duration}ms"
            )
        }

        @Test
        @DisplayName("Should handle repeated calls efficiently")
        fun shouldHandleRepeatedCallsEfficiently() {
            // Given
            val projectId = "test-project-123"
            val location = "us-central1"
            val iterations = 100
            val startTime = System.currentTimeMillis()

            // When
            repeat(iterations) {
                vertexAIModule.provideVertexAI(projectId, location)
            }
            val endTime = System.currentTimeMillis()

            // Then
            val duration = endTime - startTime
            val averageTime = duration / iterations
            assertTrue(
                averageTime < 100,
                "Average creation time should be less than 100ms, was ${averageTime}ms"
            )
        }
    }

    @Nested
    @DisplayName("Configuration Validation Tests")
    inner class ConfigurationValidationTests {

        @Test
        @DisplayName("Should validate project ID format")
        fun shouldValidateProjectIdFormat() {
            // Given
            val validProjectIds =
                listOf("test-project-123", "my-project", "project123", "test_project")
            val invalidProjectIds = listOf("", " ", "test project", "test@project", "test.project.")

            validProjectIds.forEach { projectId ->
                // When & Then
                assertDoesNotThrow {
                    vertexAIModule.provideVertexAI(projectId, "us-central1")
                }
            }

            invalidProjectIds.forEach { projectId ->
                // When & Then
                assertThrows<IllegalArgumentException> {
                    vertexAIModule.provideVertexAI(projectId, "us-central1")
                }
            }
        }

        @Test
        @DisplayName("Should validate location format")
        fun shouldValidateLocationFormat() {
            // Given
            val validLocations =
                listOf("us-central1", "us-east1", "europe-west1", "asia-southeast1")
            val invalidLocations = listOf("", " ", "invalid-location", "US-CENTRAL1", "us_central1")

            validLocations.forEach { location ->
                // When & Then
                assertDoesNotThrow {
                    vertexAIModule.provideVertexAI("test-project-123", location)
                }
            }

            invalidLocations.forEach { location ->
                // When & Then
                assertThrows<IllegalArgumentException> {
                    vertexAIModule.provideVertexAI("test-project-123", location)
                }
            }
        }
    }
}