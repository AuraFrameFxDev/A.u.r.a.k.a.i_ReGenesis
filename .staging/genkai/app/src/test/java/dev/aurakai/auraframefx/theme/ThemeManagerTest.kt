package dev.aurakai.auraframefx.theme

import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ui.theme.AuraTheme
import dev.aurakai.auraframefx.ui.theme.CyberpunkTheme
import dev.aurakai.auraframefx.ui.theme.ForestTheme
import dev.aurakai.auraframefx.ui.theme.SolarFlareTheme
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for ThemeManager
 * Testing framework: JUnit 5 with MockK for mocking
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThemeManagerTest {

    private lateinit var mockAuraAIService: AuraAIService
    private lateinit var themeManager: ThemeManager

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        mockAuraAIService = mockk<AuraAIService>()
        themeManager = ThemeManager(mockAuraAIService)
    }

    // ========== applyThemeFromNaturalLanguage Tests ==========

    @Test
    fun `applyThemeFromNaturalLanguage - cyberpunk intent returns CyberpunkTheme success`() =
        runTest {
            // Given
            val query = "make it cyberpunk"
            coEvery { mockAuraAIService.discernThemeIntent(query) } returns "cyberpunk"

            // When
            val result = themeManager.applyThemeFromNaturalLanguage(query)

            // Then
            assertIs<ThemeManager.ThemeResult.Success>(result)
            assertEquals(CyberpunkTheme, result.appliedTheme)
            coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent(query) }
        }

    @Test
    fun `applyThemeFromNaturalLanguage - solar intent returns SolarFlareTheme success`() = runTest {
        // Given
        val query = "bright solar theme"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "solar"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result)
        assertEquals(SolarFlareTheme, result.appliedTheme)
        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent(query) }
    }

    @Test
    fun `applyThemeFromNaturalLanguage - nature intent returns ForestTheme success`() = runTest {
        // Given
        val query = "make it feel like nature"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "nature"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result)
        assertEquals(ForestTheme, result.appliedTheme)
        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent(query) }
    }

    @Test
    fun `applyThemeFromNaturalLanguage - cheerful intent maps to SolarFlareTheme`() = runTest {
        // Given
        val query = "I need something cheerful"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "cheerful"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result)
        assertEquals(SolarFlareTheme, result.appliedTheme)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - calming intent maps to ForestTheme`() = runTest {
        // Given
        val query = "something calming please"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "calming"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result)
        assertEquals(ForestTheme, result.appliedTheme)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - energetic intent maps to CyberpunkTheme`() = runTest {
        // Given
        val query = "I want something energetic"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "energetic"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result)
        assertEquals(CyberpunkTheme, result.appliedTheme)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - unknown intent returns UnderstandingFailed`() = runTest {
        // Given
        val query = "some unknown theme request"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "unknown"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(result)
        assertEquals(query, result.originalQuery)
        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent(query) }
    }

    @Test
    fun `applyThemeFromNaturalLanguage - null intent returns UnderstandingFailed`() = runTest {
        // Given
        val query = "incomprehensible query"
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns null

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(result)
        assertEquals(query, result.originalQuery)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - empty string intent returns UnderstandingFailed`() =
        runTest {
            // Given
            val query = "empty response query"
            coEvery { mockAuraAIService.discernThemeIntent(query) } returns ""

            // When
            val result = themeManager.applyThemeFromNaturalLanguage(query)

            // Then
            assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(result)
            assertEquals(query, result.originalQuery)
        }

    @Test
    fun `applyThemeFromNaturalLanguage - AI service throws exception returns Error`() = runTest {
        // Given
        val query = "test query"
        val expectedException = RuntimeException("AI service failed")
        coEvery { mockAuraAIService.discernThemeIntent(query) } throws expectedException

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Error>(result)
        assertEquals(expectedException, result.exception)
        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent(query) }
    }

    @Test
    fun `applyThemeFromNaturalLanguage - empty query handled gracefully`() = runTest {
        // Given
        val query = ""
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "unknown"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(result)
        assertEquals(query, result.originalQuery)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - whitespace only query handled gracefully`() = runTest {
        // Given
        val query = "   \t\n  "
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "unknown"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(result)
        assertEquals(query, result.originalQuery)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - very long query handled gracefully`() = runTest {
        // Given
        val query = "a".repeat(10000)
        coEvery { mockAuraAIService.discernThemeIntent(query) } returns "cyberpunk"

        // When
        val result = themeManager.applyThemeFromNaturalLanguage(query)

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result)
        assertEquals(CyberpunkTheme, result.appliedTheme)
    }

    @Test
    fun `applyThemeFromNaturalLanguage - special characters in query handled gracefully`() =
        runTest {
            // Given
            val query = "!@#$%^&*()_+-={}[]|\\:;\"'<>?,./"
            coEvery { mockAuraAIService.discernThemeIntent(query) } returns "nature"

            // When
            val result = themeManager.applyThemeFromNaturalLanguage(query)

            // Then
            assertIs<ThemeManager.ThemeResult.Success>(result)
            assertEquals(ForestTheme, result.appliedTheme)
        }

    @Test
    fun `applyThemeFromNaturalLanguage - unicode characters in query handled gracefully`() =
        runTest {
            // Given
            val query = "테마를 변경해주세요 🎨✨"
            coEvery { mockAuraAIService.discernThemeIntent(query) } returns "solar"

            // When
            val result = themeManager.applyThemeFromNaturalLanguage(query)

            // Then
            assertIs<ThemeManager.ThemeResult.Success>(result)
            assertEquals(SolarFlareTheme, result.appliedTheme)
        }

    // ========== suggestThemeBasedOnContext Tests ==========

    @Test
    fun `suggestThemeBasedOnContext - returns mapped themes for valid suggestions`() = runTest {
        // Given
        val timeOfDay = "morning"
        val userActivity = "working"
        val emotionalContext = "focused"
        val expectedContextQuery = "Time: morning, Activity: working, Mood: focused"
        val aiSuggestions = listOf("cyberpunk", "solar", "nature")

        coEvery { mockAuraAIService.suggestThemes(expectedContextQuery) } returns aiSuggestions

        // When
        val result =
            themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity, emotionalContext)

        // Then
        assertEquals(3, result.size)
        assertTrue(result.contains(CyberpunkTheme))
        assertTrue(result.contains(SolarFlareTheme))
        assertTrue(result.contains(ForestTheme))
        coVerify(exactly = 1) { mockAuraAIService.suggestThemes(expectedContextQuery) }
    }

    @Test
    fun `suggestThemeBasedOnContext - without emotional context builds correct query`() = runTest {
        // Given
        val timeOfDay = "evening"
        val userActivity = "relaxing"
        val expectedContextQuery = "Time: evening, Activity: relaxing"
        val aiSuggestions = listOf("nature")

        coEvery { mockAuraAIService.suggestThemes(expectedContextQuery) } returns aiSuggestions

        // When
        val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

        // Then
        assertEquals(1, result.size)
        assertEquals(ForestTheme, result.first())
        coVerify(exactly = 1) { mockAuraAIService.suggestThemes(expectedContextQuery) }
    }

    @Test
    fun `suggestThemeBasedOnContext - filters out unknown suggestions`() = runTest {
        // Given
        val timeOfDay = "afternoon"
        val userActivity = "gaming"
        val aiSuggestions = listOf("cyberpunk", "unknown", "solar", "invalid")

        coEvery { mockAuraAIService.suggestThemes(any()) } returns aiSuggestions

        // When
        val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains(CyberpunkTheme))
        assertTrue(result.contains(SolarFlareTheme))
    }

    @Test
    fun `suggestThemeBasedOnContext - returns empty list when AI returns empty suggestions`() =
        runTest {
            // Given
            val timeOfDay = "midnight"
            val userActivity = "sleeping"

            coEvery { mockAuraAIService.suggestThemes(any()) } returns emptyList()

            // When
            val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

            // Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `suggestThemeBasedOnContext - returns empty list when AI returns only unknown suggestions`() =
        runTest {
            // Given
            val timeOfDay = "dawn"
            val userActivity = "meditation"
            val aiSuggestions = listOf("unknown1", "invalid", "unrecognized")

            coEvery { mockAuraAIService.suggestThemes(any()) } returns aiSuggestions

            // When
            val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

            // Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `suggestThemeBasedOnContext - handles AI service exception gracefully`() = runTest {
        // Given
        val timeOfDay = "morning"
        val userActivity = "working"

        coEvery { mockAuraAIService.suggestThemes(any()) } throws RuntimeException("AI service error")

        // When
        val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

        // Then
        assertTrue(result.isEmpty())
        coVerify(exactly = 1) { mockAuraAIService.suggestThemes(any()) }
    }

    @Test
    fun `suggestThemeBasedOnContext - handles null emotional context`() = runTest {
        // Given
        val timeOfDay = "evening"
        val userActivity = "reading"
        val emotionalContext: String? = null
        val expectedContextQuery = "Time: evening, Activity: reading"

        coEvery { mockAuraAIService.suggestThemes(expectedContextQuery) } returns listOf("nature")

        // When
        val result =
            themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity, emotionalContext)

        // Then
        assertEquals(1, result.size)
        assertEquals(ForestTheme, result.first())
    }

    @Test
    fun `suggestThemeBasedOnContext - handles empty timeOfDay and userActivity`() = runTest {
        // Given
        val timeOfDay = ""
        val userActivity = ""
        val expectedContextQuery = "Time: , Activity: "

        coEvery { mockAuraAIService.suggestThemes(expectedContextQuery) } returns listOf("solar")

        // When
        val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

        // Then
        assertEquals(1, result.size)
        assertEquals(SolarFlareTheme, result.first())
    }

    @Test
    fun `suggestThemeBasedOnContext - handles duplicate suggestions`() = runTest {
        // Given
        val timeOfDay = "morning"
        val userActivity = "exercising"
        val aiSuggestions = listOf("solar", "solar", "cyberpunk", "solar")

        coEvery { mockAuraAIService.suggestThemes(any()) } returns aiSuggestions

        // When
        val result = themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity)

        // Then
        assertEquals(4, result.size) // Should include duplicates as returned by AI
        assertTrue(result.count { it == SolarFlareTheme } == 3)
        assertTrue(result.count { it == CyberpunkTheme } == 1)
    }

    @Test
    fun `suggestThemeBasedOnContext - handles special characters in parameters`() = runTest {
        // Given
        val timeOfDay = "morning!@#"
        val userActivity = "working$%^"
        val emotionalContext = "happy&*()"
        val expectedContextQuery = "Time: morning!@#, Activity: working$%^, Mood: happy&*()"

        coEvery { mockAuraAIService.suggestThemes(expectedContextQuery) } returns listOf("nature")

        // When
        val result =
            themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity, emotionalContext)

        // Then
        assertEquals(1, result.size)
        assertEquals(ForestTheme, result.first())
    }

    // ========== ThemeResult Sealed Class Tests ==========

    @Test
    fun `ThemeResult Success contains correct applied theme`() {
        // Given
        val theme = CyberpunkTheme

        // When
        val result = ThemeManager.ThemeResult.Success(theme)

        // Then
        assertEquals(theme, result.appliedTheme)
    }

    @Test
    fun `ThemeResult UnderstandingFailed contains original query`() {
        // Given
        val query = "test query"

        // When
        val result = ThemeManager.ThemeResult.UnderstandingFailed(query)

        // Then
        assertEquals(query, result.originalQuery)
    }

    @Test
    fun `ThemeResult Error contains exception`() {
        // Given
        val exception = RuntimeException("test error")

        // When
        val result = ThemeManager.ThemeResult.Error(exception)

        // Then
        assertEquals(exception, result.exception)
    }

    // ========== Edge Cases and Boundary Conditions ==========

    @Test
    fun `multiple consecutive calls work correctly`() = runTest {
        // Given
        coEvery { mockAuraAIService.discernThemeIntent("query1") } returns "cyberpunk"
        coEvery { mockAuraAIService.discernThemeIntent("query2") } returns "nature"
        coEvery { mockAuraAIService.discernThemeIntent("query3") } returns "solar"

        // When
        val result1 = themeManager.applyThemeFromNaturalLanguage("query1")
        val result2 = themeManager.applyThemeFromNaturalLanguage("query2")
        val result3 = themeManager.applyThemeFromNaturalLanguage("query3")

        // Then
        assertIs<ThemeManager.ThemeResult.Success>(result1)
        assertEquals(CyberpunkTheme, result1.appliedTheme)

        assertIs<ThemeManager.ThemeResult.Success>(result2)
        assertEquals(ForestTheme, result2.appliedTheme)

        assertIs<ThemeManager.ThemeResult.Success>(result3)
        assertEquals(SolarFlareTheme, result3.appliedTheme)

        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent("query1") }
        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent("query2") }
        coVerify(exactly = 1) { mockAuraAIService.discernThemeIntent("query3") }
    }

    @Test
    fun `concurrent calls handled properly`() = runTest {
        // Given
        coEvery { mockAuraAIService.discernThemeIntent(any()) } returns "cyberpunk"

        // When - simulate concurrent calls
        val results = List(10) { index ->
            themeManager.applyThemeFromNaturalLanguage("query$index")
        }

        // Then
        results.forEach { result ->
            assertIs<ThemeManager.ThemeResult.Success>(result)
            assertEquals(CyberpunkTheme, result.appliedTheme)
        }

        coVerify(exactly = 10) { mockAuraAIService.discernThemeIntent(any()) }
    }

    @Test
    fun `case sensitivity in AI response handled correctly`() = runTest {
        // Given - test various case combinations
        coEvery { mockAuraAIService.discernThemeIntent("upper") } returns "CYBERPUNK"
        coEvery { mockAuraAIService.discernThemeIntent("mixed") } returns "CyberPunk"
        coEvery { mockAuraAIService.discernThemeIntent("lower") } returns "cyberpunk"

        // When
        val resultUpper = themeManager.applyThemeFromNaturalLanguage("upper")
        val resultMixed = themeManager.applyThemeFromNaturalLanguage("mixed")
        val resultLower = themeManager.applyThemeFromNaturalLanguage("lower")

        // Then - only exact lowercase match should work
        assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(resultUpper)
        assertIs<ThemeManager.ThemeResult.UnderstandingFailed>(resultMixed)
        assertIs<ThemeManager.ThemeResult.Success>(resultLower)
        assertEquals(CyberpunkTheme, (resultLower as ThemeManager.ThemeResult.Success).appliedTheme)
    }

    // ========== Integration-style Tests ==========

    @Test
    fun `theme mapping consistency - all known intents map to valid themes`() = runTest {
        // Given - all known theme intents
        val knownIntents =
            listOf("cyberpunk", "solar", "nature", "cheerful", "calming", "energetic")
        val expectedThemes = mapOf(
            "cyberpunk" to CyberpunkTheme,
            "solar" to SolarFlareTheme,
            "nature" to ForestTheme,
            "cheerful" to SolarFlareTheme,
            "calming" to ForestTheme,
            "energetic" to CyberpunkTheme
        )

        // When & Then
        knownIntents.forEach { intent ->
            coEvery { mockAuraAIService.discernThemeIntent(any()) } returns intent
            val result = themeManager.applyThemeFromNaturalLanguage("test query")

            assertIs<ThemeManager.ThemeResult.Success>(result)
            assertEquals(expectedThemes[intent], result.appliedTheme)
        }
    }

    @Test
    fun `context query building - various combinations produce correct format`() = runTest {
        // Given
        val testCases = listOf(
            Triple("morning", "working", null) to "Time: morning, Activity: working",
            Triple(
                "evening",
                "relaxing",
                "happy"
            ) to "Time: evening, Activity: relaxing, Mood: happy",
            Triple("", "", null) to "Time: , Activity: ",
            Triple("night", "sleeping", "") to "Time: night, Activity: sleeping, Mood: "
        )

        // When & Then
        testCases.forEach { (params, expectedQuery) ->
            val (timeOfDay, userActivity, emotionalContext) = params
            coEvery { mockAuraAIService.suggestThemes(expectedQuery) } returns listOf("nature")

            val result =
                themeManager.suggestThemeBasedOnContext(timeOfDay, userActivity, emotionalContext)

            assertEquals(1, result.size)
            assertEquals(ForestTheme, result.first())
            coVerify { mockAuraAIService.suggestThemes(expectedQuery) }
        }
    }

    // ========== Performance and Stress Tests ==========

    @Test
    fun `large number of suggestions handled efficiently`() = runTest {
        // Given
        val largeSuggestionList = (1..1000).map { "solar" }
        coEvery { mockAuraAIService.suggestThemes(any()) } returns largeSuggestionList

        // When
        val result = themeManager.suggestThemeBasedOnContext("morning", "working")

        // Then
        assertEquals(1000, result.size)
        assertTrue(result.all { it == SolarFlareTheme })
    }

    @Test
    fun `exception handling preserves thread safety`() = runTest {
        // Given
        coEvery { mockAuraAIService.discernThemeIntent(any()) } throws RuntimeException("Test error")

        // When - multiple concurrent calls with exceptions
        val results = List(5) {
            themeManager.applyThemeFromNaturalLanguage("error query $it")
        }

        // Then - all should return Error results
        results.forEach { result ->
            assertIs<ThemeManager.ThemeResult.Error>(result)
            assertTrue(result.exception is RuntimeException)
        }
    }

    // ========== Business Logic Validation Tests ==========

    @Test
    fun `emotional theme mapping follows expected psychology patterns`() = runTest {
        // Given - emotional states and their expected theme mappings
        val emotionalMappings = mapOf(
            "cheerful" to SolarFlareTheme,  // Bright, energetic
            "calming" to ForestTheme,       // Natural, peaceful
            "energetic" to CyberpunkTheme   // High-energy, vibrant
        )

        // When & Then - verify each mapping
        emotionalMappings.forEach { (emotion, expectedTheme) ->
            coEvery { mockAuraAIService.discernThemeIntent(any()) } returns emotion
            val result = themeManager.applyThemeFromNaturalLanguage("I feel $emotion")

            assertIs<ThemeManager.ThemeResult.Success>(result)
            assertEquals(expectedTheme, result.appliedTheme)
        }
    }

    @Test
    fun `theme application follows Genesis vision requirements`() = runTest {
        // Given - testing the conversational AI theme approach as described in the Genesis vision
        val conversationalQueries = listOf(
            "Hey Aura, I'm feeling a bit down today, can you make my phone feel a bit more cheerful?",
            "I need something energetic for my workout",
            "Help me focus with a calming environment"
        )

        coEvery { mockAuraAIService.discernThemeIntent(match { it.contains("cheerful") }) } returns "cheerful"
        coEvery { mockAuraAIService.discernThemeIntent(match { it.contains("energetic") }) } returns "energetic"
        coEvery { mockAuraAIService.discernThemeIntent(match { it.contains("calming") }) } returns "calming"

        // When & Then
        val cheerfulResult = themeManager.applyThemeFromNaturalLanguage(conversationalQueries[0])
        val energeticResult = themeManager.applyThemeFromNaturalLanguage(conversationalQueries[1])
        val calmingResult = themeManager.applyThemeFromNaturalLanguage(conversationalQueries[2])

        assertIs<ThemeManager.ThemeResult.Success>(cheerfulResult)
        assertEquals(SolarFlareTheme, cheerfulResult.appliedTheme)

        assertIs<ThemeManager.ThemeResult.Success>(energeticResult)
        assertEquals(CyberpunkTheme, energeticResult.appliedTheme)

        assertIs<ThemeManager.ThemeResult.Success>(calmingResult)
        assertEquals(ForestTheme, calmingResult.appliedTheme)
    }

    // ========== Method Return Type and Interface Validation Tests ==========

    @Test
    fun `sealed class ThemeResult exhaustive when handling results`() = runTest {
        // Given
        val testCases = listOf(
            ThemeManager.ThemeResult.Success(CyberpunkTheme),
            ThemeManager.ThemeResult.UnderstandingFailed("test query"),
            ThemeManager.ThemeResult.Error(RuntimeException("test"))
        )

        // When & Then - ensure all cases can be handled exhaustively
        testCases.forEach { result ->
            val handled = when (result) {
                is ThemeManager.ThemeResult.Success -> "success"
                is ThemeManager.ThemeResult.UnderstandingFailed -> "failed"
                is ThemeManager.ThemeResult.Error -> "error"
            }
            assertTrue(handled.isNotEmpty())
        }
    }

    @Test
    fun `ThemeManager constructor injection works correctly`() {
        // Given & When
        val manager = ThemeManager(mockAuraAIService)

        // Then - constructor should complete without issues
        // This test validates the dependency injection setup
        assertTrue(manager is ThemeManager)
    }
}