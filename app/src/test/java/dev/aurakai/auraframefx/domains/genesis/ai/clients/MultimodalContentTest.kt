package dev.aurakai.auraframefx.domains.genesis.ai.clients

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [MultimodalContent] sealed class and [MrlDimension] constants.
 *
 * These are pure structural / value tests — no mocking required.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("MultimodalContent and MrlDimension Tests")
class MultimodalContentTest {

    // ──────────────────────────────────────────────────────────────────────────
    // MultimodalContent.Text
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MultimodalContent.Text")
    inner class TextTests {

        @Test
        @DisplayName("should store content string")
        fun shouldStoreContentString() {
            val text = MultimodalContent.Text("Hello, Gemini!")
            assertEquals("Hello, Gemini!", text.content)
        }

        @Test
        @DisplayName("should be a subtype of MultimodalContent")
        fun shouldBeSubtypeOfMultimodalContent() {
            val text: MultimodalContent = MultimodalContent.Text("test")
            assertTrue(text is MultimodalContent.Text)
        }

        @Test
        @DisplayName("Text instances with identical content should be equal")
        fun identicalTextInstancesShouldBeEqual() {
            val a = MultimodalContent.Text("same")
            val b = MultimodalContent.Text("same")
            assertEquals(a, b)
            assertEquals(a.hashCode(), b.hashCode())
        }

        @Test
        @DisplayName("Text instances with different content should not be equal")
        fun differentTextInstancesShouldNotBeEqual() {
            val a = MultimodalContent.Text("hello")
            val b = MultimodalContent.Text("world")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("should accept empty string content")
        fun shouldAcceptEmptyStringContent() {
            val text = MultimodalContent.Text("")
            assertEquals("", text.content)
        }

        @Test
        @DisplayName("should accept Unicode content")
        fun shouldAcceptUnicodeContent() {
            val content = "日本語テスト 🤖"
            val text = MultimodalContent.Text(content)
            assertEquals(content, text.content)
        }

        @Test
        @DisplayName("should support copy()")
        fun shouldSupportCopy() {
            val original = MultimodalContent.Text("original")
            val copy = original.copy(content = "copy")
            assertEquals("copy", copy.content)
            assertNotEquals(original, copy)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MultimodalContent.Image
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MultimodalContent.Image")
    inner class ImageTests {

        @Test
        @DisplayName("should store base64 bytes and default mimeType")
        fun shouldStoreBase64AndDefaultMimeType() {
            val image = MultimodalContent.Image("base64data")
            assertEquals("base64data", image.bytesBase64)
            assertEquals("image/jpeg", image.mimeType)
        }

        @Test
        @DisplayName("should accept custom mimeType")
        fun shouldAcceptCustomMimeType() {
            val image = MultimodalContent.Image("data", mimeType = "image/png")
            assertEquals("image/png", image.mimeType)
        }

        @Test
        @DisplayName("should be a subtype of MultimodalContent")
        fun shouldBeSubtypeOfMultimodalContent() {
            val image: MultimodalContent = MultimodalContent.Image("data")
            assertTrue(image is MultimodalContent.Image)
        }

        @Test
        @DisplayName("Image instances with identical fields should be equal")
        fun identicalImagesShouldBeEqual() {
            val a = MultimodalContent.Image("data", "image/jpeg")
            val b = MultimodalContent.Image("data", "image/jpeg")
            assertEquals(a, b)
        }

        @Test
        @DisplayName("Images with different base64 should not be equal")
        fun differentBase64ImagesShouldNotBeEqual() {
            val a = MultimodalContent.Image("data1")
            val b = MultimodalContent.Image("data2")
            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("Images with different mimeType should not be equal")
        fun differentMimeTypesShouldNotBeEqual() {
            val a = MultimodalContent.Image("data", "image/jpeg")
            val b = MultimodalContent.Image("data", "image/png")
            assertNotEquals(a, b)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MultimodalContent.Audio
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MultimodalContent.Audio")
    inner class AudioTests {

        @Test
        @DisplayName("should store base64 bytes and default mimeType")
        fun shouldStoreBase64AndDefaultMimeType() {
            val audio = MultimodalContent.Audio("audiodata")
            assertEquals("audiodata", audio.bytesBase64)
            assertEquals("audio/mp3", audio.mimeType)
        }

        @Test
        @DisplayName("should accept custom mimeType")
        fun shouldAcceptCustomMimeType() {
            val audio = MultimodalContent.Audio("data", mimeType = "audio/ogg")
            assertEquals("audio/ogg", audio.mimeType)
        }

        @Test
        @DisplayName("should be a subtype of MultimodalContent")
        fun shouldBeSubtypeOfMultimodalContent() {
            val audio: MultimodalContent = MultimodalContent.Audio("data")
            assertTrue(audio is MultimodalContent.Audio)
        }

        @Test
        @DisplayName("Audio instances with identical fields should be equal")
        fun identicalAudiosShouldBeEqual() {
            val a = MultimodalContent.Audio("data", "audio/mp3")
            val b = MultimodalContent.Audio("data", "audio/mp3")
            assertEquals(a, b)
        }

        @Test
        @DisplayName("Audio instances with different base64 should not be equal")
        fun differentBase64AudioShouldNotBeEqual() {
            val a = MultimodalContent.Audio("data1")
            val b = MultimodalContent.Audio("data2")
            assertNotEquals(a, b)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Sealed class exhaustiveness
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Sealed class exhaustiveness")
    inner class SealedClassTests {

        @Test
        @DisplayName("when expression over all subtypes should compile and not fall through")
        fun whenExpressionShouldCoverAllSubtypes() {
            val contents: List<MultimodalContent> = listOf(
                MultimodalContent.Text("t"),
                MultimodalContent.Image("i"),
                MultimodalContent.Audio("a")
            )
            val tags = contents.map { content ->
                when (content) {
                    is MultimodalContent.Text -> "text"
                    is MultimodalContent.Image -> "image"
                    is MultimodalContent.Audio -> "audio"
                }
            }
            assertEquals(listOf("text", "image", "audio"), tags)
        }

        @Test
        @DisplayName("Text should not be assignable to Image or Audio")
        fun subtypesShouldBeDistinct() {
            val text: MultimodalContent = MultimodalContent.Text("t")
            assertFalse(text is MultimodalContent.Image)
            assertFalse(text is MultimodalContent.Audio)
        }

        @Test
        @DisplayName("Image should not be assignable to Text or Audio")
        fun imageShouldNotBeTextOrAudio() {
            val image: MultimodalContent = MultimodalContent.Image("i")
            assertFalse(image is MultimodalContent.Text)
            assertFalse(image is MultimodalContent.Audio)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MrlDimension constants
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MrlDimension constants")
    inner class MrlDimensionTests {

        @Test
        @DisplayName("FAST should equal 768")
        fun fastShouldEqual768() {
            assertEquals(768, MrlDimension.FAST)
        }

        @Test
        @DisplayName("OPTIMAL should equal 1536")
        fun optimalShouldEqual1536() {
            assertEquals(1536, MrlDimension.OPTIMAL)
        }

        @Test
        @DisplayName("DEEP should equal 3072")
        fun deepShouldEqual3072() {
            assertEquals(3072, MrlDimension.DEEP)
        }

        @Test
        @DisplayName("dimensions should be ordered FAST < OPTIMAL < DEEP")
        fun dimensionsShouldBeOrdered() {
            assertTrue(MrlDimension.FAST < MrlDimension.OPTIMAL)
            assertTrue(MrlDimension.OPTIMAL < MrlDimension.DEEP)
        }

        @Test
        @DisplayName("OPTIMAL should be exactly double FAST")
        fun optimalShouldBeDoubleFast() {
            assertEquals(MrlDimension.FAST * 2, MrlDimension.OPTIMAL)
        }

        @Test
        @DisplayName("DEEP should be exactly double OPTIMAL")
        fun deepShouldBeDoubleOptimal() {
            assertEquals(MrlDimension.OPTIMAL * 2, MrlDimension.DEEP)
        }

        @Test
        @DisplayName("all dimensions should be positive")
        fun allDimensionsShouldBePositive() {
            assertTrue(MrlDimension.FAST > 0)
            assertTrue(MrlDimension.OPTIMAL > 0)
            assertTrue(MrlDimension.DEEP > 0)
        }

        @Test
        @DisplayName("all dimensions should be multiples of 768")
        fun allDimensionsShouldBeMultiplesOf768() {
            assertEquals(0, MrlDimension.FAST % 768)
            assertEquals(0, MrlDimension.OPTIMAL % 768)
            assertEquals(0, MrlDimension.DEEP % 768)
        }
    }
}