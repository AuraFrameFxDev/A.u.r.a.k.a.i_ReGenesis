package dev.aurakai.auraframefx.di

import dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria
import dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine
import dev.aurakai.auraframefx.domains.genesis.models.VertexAIConfig
import io.mockk.clearAllMocks
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [AiServiceModule] companion object providers.
 *
 * PR change: provideFirebaseStorage() was added to the companion object.
 * These tests verify the pure (non-Firebase) providers behave correctly, and
 * use reflection to verify the provideFirebaseStorage method exists with the
 * correct annotations without triggering Firebase initialization.
 *
 * Tests cover:
 * - provideVertexAIConfig returns correct configuration values
 * - provideNemotronEngine returns a functional NemotronEngine
 * - provideGeminiMemoria returns a functional GeminiMemoria
 * - provideFirebaseStorage method exists with correct annotations (reflection only)
 * - Module class structure
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AiServiceModule Tests")
class AIServiceModuleTest {

    @BeforeEach
    fun setUp() {
        // No setup needed for pure companion object tests
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    // ──────────────────────────────────────────────────────────────────────────
    // provideVertexAIConfig tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("provideVertexAIConfig Tests")
    inner class ProvideVertexAIConfigTests {

        @Test
        @DisplayName("Should return non-null VertexAIConfig")
        fun shouldReturnNonNullVertexAIConfig() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertNotNull(config)
        }

        @Test
        @DisplayName("Should return VertexAIConfig with projectId 'collabcanvas'")
        fun shouldReturnConfigWithCorrectProjectId() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertEquals("collabcanvas", config.projectId)
        }

        @Test
        @DisplayName("Should return VertexAIConfig with location 'us-central1'")
        fun shouldReturnConfigWithCorrectLocation() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertEquals("us-central1", config.location)
        }

        @Test
        @DisplayName("Should return VertexAIConfig with endpoint 'us-central1-aiplatform.googleapis.com'")
        fun shouldReturnConfigWithCorrectEndpoint() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertEquals("us-central1-aiplatform.googleapis.com", config.endpoint)
        }

        @Test
        @DisplayName("Should return VertexAIConfig with modelName 'gemini-3.1-pro-preview'")
        fun shouldReturnConfigWithCorrectModelName() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertEquals("gemini-3.1-pro-preview", config.modelName)
        }

        @Test
        @DisplayName("Each call returns an equal config instance")
        fun eachCallReturnsEqualConfig() {
            val config1 = AiServiceModule.provideVertexAIConfig()
            val config2 = AiServiceModule.provideVertexAIConfig()
            assertEquals(config1, config2)
        }

        @Test
        @DisplayName("Config should not have null projectId")
        fun configShouldNotHaveNullProjectId() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertTrue(config.projectId.isNotBlank())
        }

        @Test
        @DisplayName("Config endpoint should contain googleapis.com domain")
        fun configEndpointContainsGoogleApisDomain() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertTrue(config.endpoint.contains("googleapis.com"),
                "Endpoint '${config.endpoint}' should contain 'googleapis.com'")
        }

        @Test
        @DisplayName("Config model name should not be blank")
        fun configModelNameShouldNotBeBlank() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertTrue(config.modelName.isNotBlank())
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // provideNemotronEngine tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("provideNemotronEngine Tests")
    inner class ProvideNemotronEngineTests {

        @Test
        @DisplayName("Should return a non-null NemotronEngine")
        fun shouldReturnNonNullNemotronEngine() {
            val engine = AiServiceModule.provideNemotronEngine()
            assertNotNull(engine)
        }

        @Test
        @DisplayName("Should return an object implementing NemotronEngine interface")
        fun shouldImplementNemotronEngineInterface() {
            val engine = AiServiceModule.provideNemotronEngine()
            assertTrue(engine is NemotronEngine)
        }

        @Test
        @DisplayName("process() should return non-null response for valid prompt")
        fun processShouldReturnNonNullResponse() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result = engine.process("test prompt")
            assertNotNull(result)
        }

        @Test
        @DisplayName("process() should return a non-empty response")
        fun processShouldReturnNonEmptyResponse() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result = engine.process("generate something")
            assertTrue(result.isNotBlank(), "NemotronEngine.process() should return non-blank string")
        }

        @Test
        @DisplayName("process() response should contain 'Nemotron' identifier")
        fun processShouldContainNemotronIdentifier() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result = engine.process("any prompt")
            assertTrue(result.contains("Nemotron"),
                "Expected 'Nemotron' in response but got: '$result'")
        }

        @Test
        @DisplayName("process() response should contain 'Sovereign' reference")
        fun processShouldContainSovereignReference() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result = engine.process("query")
            assertTrue(result.contains("Sovereign"),
                "Expected 'Sovereign' in response but got: '$result'")
        }

        @Test
        @DisplayName("process() should handle empty prompt")
        fun processShouldHandleEmptyPrompt() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result = engine.process("")
            assertNotNull(result)
            assertTrue(result.isNotBlank())
        }

        @Test
        @DisplayName("process() should return consistent result on repeated calls")
        fun processShouldReturnConsistentResult() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result1 = engine.process("test")
            val result2 = engine.process("test")
            assertEquals(result1, result2)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // provideGeminiMemoria tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("provideGeminiMemoria Tests")
    inner class ProvideGeminiMemoriaTests {

        @Test
        @DisplayName("Should return a non-null GeminiMemoria")
        fun shouldReturnNonNullGeminiMemoria() {
            val memoria = AiServiceModule.provideGeminiMemoria()
            assertNotNull(memoria)
        }

        @Test
        @DisplayName("Should return an object implementing GeminiMemoria interface")
        fun shouldImplementGeminiMemoriaInterface() {
            val memoria = AiServiceModule.provideGeminiMemoria()
            assertTrue(memoria is GeminiMemoria)
        }

        @Test
        @DisplayName("process() should return non-null response for valid prompt")
        fun processShouldReturnNonNullResponse() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result = memoria.process("recall something")
            assertNotNull(result)
        }

        @Test
        @DisplayName("process() should return a non-empty response")
        fun processShouldReturnNonEmptyResponse() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result = memoria.process("query memory")
            assertTrue(result.isNotBlank(), "GeminiMemoria.process() should return non-blank string")
        }

        @Test
        @DisplayName("process() response should contain 'Gemini' identifier")
        fun processShouldContainGeminiIdentifier() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result = memoria.process("any prompt")
            assertTrue(result.contains("Gemini"),
                "Expected 'Gemini' in response but got: '$result'")
        }

        @Test
        @DisplayName("process() response should contain 'Synced' acknowledgment")
        fun processShouldContainSyncedAcknowledgment() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result = memoria.process("query")
            assertTrue(result.contains("Synced"),
                "Expected 'Synced' in response but got: '$result'")
        }

        @Test
        @DisplayName("process() should handle empty prompt")
        fun processShouldHandleEmptyPrompt() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result = memoria.process("")
            assertNotNull(result)
            assertTrue(result.isNotBlank())
        }

        @Test
        @DisplayName("process() should return consistent result on repeated calls")
        fun processShouldReturnConsistentResult() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result1 = memoria.process("test")
            val result2 = memoria.process("test")
            assertEquals(result1, result2)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // NemotronEngine vs GeminiMemoria distinction tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("NemotronEngine and GeminiMemoria Distinction Tests")
    inner class EngineDistinctionTests {

        @Test
        @DisplayName("NemotronEngine and GeminiMemoria should produce different responses")
        fun enginesShouldProduceDifferentResponses() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val memoria = AiServiceModule.provideGeminiMemoria()

            val nemotronResult = engine.process("same prompt")
            val geminiResult = memoria.process("same prompt")

            assertFalse(nemotronResult == geminiResult,
                "NemotronEngine and GeminiMemoria should return different responses for the same prompt")
        }

        @Test
        @DisplayName("NemotronEngine response should not start with 'Gemini'")
        fun nemotronResponseShouldNotBeGemini() = runTest {
            val engine = AiServiceModule.provideNemotronEngine()
            val result = engine.process("prompt")
            assertFalse(result.startsWith("Gemini"))
        }

        @Test
        @DisplayName("GeminiMemoria response should not start with 'Nemotron'")
        fun geminiResponseShouldNotBeNemotron() = runTest {
            val memoria = AiServiceModule.provideGeminiMemoria()
            val result = memoria.process("prompt")
            assertFalse(result.startsWith("Nemotron"))
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // provideFirebaseStorage method reflection tests (PR change)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("provideFirebaseStorage Reflection Tests (PR Change)")
    inner class ProvideFirebaseStorageReflectionTests {

        @Test
        @DisplayName("provideFirebaseStorage method should exist in companion object")
        fun provideFirebaseStorageMethodShouldExist() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideFirebaseStorage" }
            assertNotNull(method,
                "provideFirebaseStorage method should be declared in AiServiceModule.Companion")
        }

        @Test
        @DisplayName("provideFirebaseStorage should have @Provides annotation")
        fun provideFirebaseStorageShouldHaveProvidesAnnotation() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideFirebaseStorage" }
            assertNotNull(method, "Method should exist")

            val annotations = method!!.annotations.map { it.annotationClass.simpleName }
            assertTrue(annotations.contains("Provides"),
                "provideFirebaseStorage should have @Provides annotation. Found: $annotations")
        }

        @Test
        @DisplayName("provideFirebaseStorage should have @Singleton annotation")
        fun provideFirebaseStorageShouldHaveSingletonAnnotation() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideFirebaseStorage" }
            assertNotNull(method, "Method should exist")

            val annotations = method!!.annotations.map { it.annotationClass.simpleName }
            assertTrue(annotations.contains("Singleton"),
                "provideFirebaseStorage should have @Singleton annotation. Found: $annotations")
        }

        @Test
        @DisplayName("provideFirebaseStorage return type should be FirebaseStorage")
        fun provideFirebaseStorageReturnTypeShouldBeFirebaseStorage() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideFirebaseStorage" }
            assertNotNull(method, "Method should exist")

            assertEquals(
                "FirebaseStorage",
                method!!.returnType.simpleName,
                "provideFirebaseStorage should return FirebaseStorage"
            )
        }

        @Test
        @DisplayName("provideFirebaseStorage should take no parameters")
        fun provideFirebaseStorageShouldTakeNoParameters() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideFirebaseStorage" }
            assertNotNull(method, "Method should exist")

            assertEquals(0, method!!.parameterCount,
                "provideFirebaseStorage should have no parameters")
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Module structure tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Module Structure Tests")
    inner class ModuleStructureTests {

        @Test
        @DisplayName("AiServiceModule should have @Module annotation")
        fun aiServiceModuleShouldHaveModuleAnnotation() {
            val moduleClass = AiServiceModule::class.java
            val annotations = moduleClass.annotations.map { it.annotationClass.simpleName }
            assertTrue(annotations.contains("Module"),
                "AiServiceModule should have @Module annotation. Found: $annotations")
        }

        @Test
        @DisplayName("AiServiceModule should have @InstallIn annotation")
        fun aiServiceModuleShouldHaveInstallInAnnotation() {
            val moduleClass = AiServiceModule::class.java
            val annotations = moduleClass.annotations.map { it.annotationClass.simpleName }
            assertTrue(annotations.contains("InstallIn"),
                "AiServiceModule should have @InstallIn annotation. Found: $annotations")
        }

        @Test
        @DisplayName("AiServiceModule companion should contain provideVertexAIConfig method")
        fun companionShouldContainProvideVertexAIConfig() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideVertexAIConfig" }
            assertNotNull(method, "provideVertexAIConfig should be in companion object")
        }

        @Test
        @DisplayName("AiServiceModule companion should contain provideNemotronEngine method")
        fun companionShouldContainProvideNemotronEngine() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideNemotronEngine" }
            assertNotNull(method, "provideNemotronEngine should be in companion object")
        }

        @Test
        @DisplayName("AiServiceModule companion should contain provideGeminiMemoria method")
        fun companionShouldContainProvideGeminiMemoria() {
            val companionClass = AiServiceModule.Companion::class.java
            val method = companionClass.declaredMethods.find { it.name == "provideGeminiMemoria" }
            assertNotNull(method, "provideGeminiMemoria should be in companion object")
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Regression tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Regression Tests")
    inner class RegressionTests {

        @Test
        @DisplayName("provideVertexAIConfig should use 'collabcanvas' project (not the old default)")
        fun provideVertexAIConfigShouldUseCollabcanvas() {
            val config = AiServiceModule.provideVertexAIConfig()
            // Regression: ensure we're not using the old default projectId values
            assertFalse(config.projectId.isEmpty())
            assertEquals("collabcanvas", config.projectId)
        }

        @Test
        @DisplayName("provideVertexAIConfig should use 'gemini-3.1-pro-preview' model")
        fun provideVertexAIConfigShouldUseGemini31ProPreview() {
            val config = AiServiceModule.provideVertexAIConfig()
            // Regression: verify model name override from PR
            assertEquals("gemini-3.1-pro-preview", config.modelName)
        }

        @Test
        @DisplayName("VertexAIConfig type should be VertexAIConfig")
        fun vertexAIConfigTypeShouldBeCorrect() {
            val config = AiServiceModule.provideVertexAIConfig()
            assertTrue(config is VertexAIConfig)
        }

        @Test
        @DisplayName("NemotronEngine should be independent from GeminiMemoria instance")
        fun enginesShouldBeIndependentInstances() {
            val engine = AiServiceModule.provideNemotronEngine()
            val memoria = AiServiceModule.provideGeminiMemoria()
            // Each call creates a new anonymous object - they should be different instances
            assertFalse(engine === memoria, "NemotronEngine and GeminiMemoria should be separate instances")
        }
    }
}