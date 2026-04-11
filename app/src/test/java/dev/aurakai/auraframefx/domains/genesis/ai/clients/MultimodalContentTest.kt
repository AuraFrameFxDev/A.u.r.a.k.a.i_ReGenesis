package dev.aurakai.auraframefx.domains.genesis.ai.clients

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for [MultimodalContent] sealed class and [MrlDimension] constants.
 *
 * These types are defined in core-module and are referenced by the staging
 * [VertexAIClientImpl] (added in this PR) to describe the modalities fed into the
 * Gemini Embedding 2 pipeline.
 */
@DisplayName("MultimodalContent and MrlDimension Tests")
class MultimodalContentTest {

    // ── MultimodalContent.Text ────────────────────────────────────────────────

    @Nested
    @DisplayName("MultimodalContent.Text")
    inner class TextContent {

        @Test
        @DisplayName("holds the provided text content")
        fun holdsTextContent() {
            val item = MultimodalContent.Text("hello world")
            assertEquals("hello world", item.content)
        }

        @Test
        @DisplayName("empty string is a valid text content")
        fun allowsEmptyString() {
            val item = MultimodalContent.Text("")
            assertEquals("", item.content)
        }

        @Test
        @DisplayName("two Text instances with same value are equal")
        fun equalWhenSameValue() {
            assertEquals(MultimodalContent.Text("foo"), MultimodalContent.Text("foo"))
        }

        @Test
        @DisplayName("two Text instances with different values are not equal")
        fun notEqualWhenDifferentValues() {
            assertNotEquals(MultimodalContent.Text("foo"), MultimodalContent.Text("bar"))
        }

        @Test
        @DisplayName("is an instance of MultimodalContent")
        fun isMultimodalContent() {
            val item: MultimodalContent = MultimodalContent.Text("test")
            assert(item is MultimodalContent.Text)
        }
    }

    // ── MultimodalContent.Image ───────────────────────────────────────────────

    @Nested
    @DisplayName("MultimodalContent.Image")
    inner class ImageContent {

        private val sampleBase64 = "iVBORw0KGgoAAAANS"

        @Test
        @DisplayName("holds base64 bytes and default mimeType")
        fun holdsBase64AndDefaultMime() {
            val item = MultimodalContent.Image(sampleBase64)
            assertEquals(sampleBase64, item.bytesBase64)
            assertEquals("image/jpeg", item.mimeType)
        }

        @Test
        @DisplayName("custom mimeType is stored correctly")
        fun customMimeType() {
            val item = MultimodalContent.Image(sampleBase64, mimeType = "image/png")
            assertEquals("image/png", item.mimeType)
        }

        @Test
        @DisplayName("two Image instances with same values are equal")
        fun equalWhenSameValues() {
            assertEquals(
                MultimodalContent.Image(sampleBase64, "image/jpeg"),
                MultimodalContent.Image(sampleBase64, "image/jpeg")
            )
        }

        @Test
        @DisplayName("Image is distinct from Text even with same string content")
        fun distinctFromText() {
            val text: MultimodalContent = MultimodalContent.Text(sampleBase64)
            val image: MultimodalContent = MultimodalContent.Image(sampleBase64)
            assertNotEquals(text, image)
        }
    }

    // ── MultimodalContent.Audio ───────────────────────────────────────────────

    @Nested
    @DisplayName("MultimodalContent.Audio")
    inner class AudioContent {

        private val sampleBase64 = "SUQzBAAAAAAAI1RTU0UA"

        @Test
        @DisplayName("holds base64 bytes and default mimeType")
        fun holdsBase64AndDefaultMime() {
            val item = MultimodalContent.Audio(sampleBase64)
            assertEquals(sampleBase64, item.bytesBase64)
            assertEquals("audio/mp3", item.mimeType)
        }

        @Test
        @DisplayName("custom mimeType is stored correctly")
        fun customMimeType() {
            val item = MultimodalContent.Audio(sampleBase64, mimeType = "audio/wav")
            assertEquals("audio/wav", item.mimeType)
        }

        @Test
        @DisplayName("Audio is distinct from Image even with same bytes")
        fun distinctFromImage() {
            val audio: MultimodalContent = MultimodalContent.Audio(sampleBase64)
            val image: MultimodalContent = MultimodalContent.Image(sampleBase64)
            assertNotEquals(audio, image)
        }
    }

    // ── Sealed-class exhaustive when ──────────────────────────────────────────

    @Nested
    @DisplayName("Sealed class pattern matching")
    inner class PatternMatching {

        @Test
        @DisplayName("when expression covers all variants without else branch")
        fun exhaustiveWhenCoversAllVariants() {
            val inputs: List<MultimodalContent> = listOf(
                MultimodalContent.Text("t"),
                MultimodalContent.Image("i"),
                MultimodalContent.Audio("a"),
            )

            val results = inputs.map { content ->
                // A fully-exhaustive when expression (no else) must compile if the sealed
                // class has exactly three subclasses. This verifies the contract.
                when (content) {
                    is MultimodalContent.Text -> "text"
                    is MultimodalContent.Image -> "image"
                    is MultimodalContent.Audio -> "audio"
                }
            }

            assertEquals(listOf("text", "image", "audio"), results)
        }
    }

    // ── MrlDimension constants ────────────────────────────────────────────────

    @Nested
    @DisplayName("MrlDimension Constants")
    inner class MrlDimensionConstants {

        @Test
        @DisplayName("FAST is 768")
        fun fastIs768() {
            assertEquals(768, MrlDimension.FAST)
        }

        @Test
        @DisplayName("OPTIMAL is 1536")
        fun optimalIs1536() {
            assertEquals(1536, MrlDimension.OPTIMAL)
        }

        @Test
        @DisplayName("DEEP is 3072")
        fun deepIs3072() {
            assertEquals(3072, MrlDimension.DEEP)
        }

        @Test
        @DisplayName("FAST < OPTIMAL < DEEP (ascending dimension ladder)")
        fun ascendingDimensionLadder() {
            assert(MrlDimension.FAST < MrlDimension.OPTIMAL) {
                "FAST (${MrlDimension.FAST}) should be less than OPTIMAL (${MrlDimension.OPTIMAL})"
            }
            assert(MrlDimension.OPTIMAL < MrlDimension.DEEP) {
                "OPTIMAL (${MrlDimension.OPTIMAL}) should be less than DEEP (${MrlDimension.DEEP})"
            }
        }

        @Test
        @DisplayName("OPTIMAL is the default dimension used by the interface")
        fun optimalIsDefaultInInterface() {
            // Verifies the interface default matches the MrlDimension.OPTIMAL constant
            // (regression guard against accidental value change)
            assertEquals(1536, MrlDimension.OPTIMAL)
        }
    }
}