package collabcanvas.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasElementSerializationTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Color::class.java, ColorTypeAdapter())
        .registerTypeAdapter(Path::class.java, PathTypeAdapter())
        .registerTypeAdapter(Offset::class.java, OffsetTypeAdapter())
        .create()

    @Test
    fun testCanvasElementSerialization() {
        val pathData = PathData(
            points = listOf(Offset(10f, 20f), Offset(30f, 40f)),
            isComplete = true
        )
        val element = CanvasElement(
            id = "test-id",
            type = ElementType.PATH,
            path = pathData,
            color = Color.Red,
            strokeWidth = 5f,
            zIndex = 1,
            isSelected = true,
            createdBy = "user-1",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val json = gson.toJson(element)
        val deserializedElement = gson.fromJson(json, CanvasElement::class.java)

        assertEquals(element.id, deserializedElement.id)
        assertEquals(element.type, deserializedElement.type)
        assertEquals(element.path.isComplete, deserializedElement.path.isComplete)
        assertEquals(element.path.points.size, deserializedElement.path.points.size)
        assertEquals(element.path.points[0], deserializedElement.path.points[0])
        assertEquals(element.path.points[1], deserializedElement.path.points[1])
        assertEquals(element.color, deserializedElement.color)
        assertEquals(element.strokeWidth, deserializedElement.strokeWidth, 0.01f)
        assertEquals(element.zIndex, deserializedElement.zIndex)
        assertEquals(element.isSelected, deserializedElement.isSelected)
        assertEquals(element.createdBy, deserializedElement.createdBy)
        assertEquals(element.createdAt, deserializedElement.createdAt)
        assertEquals(element.updatedAt, deserializedElement.updatedAt)
    }

    @Test
    fun testColorSerialization() {
        // Test standard colors and a high-bit color value to ensure no truncation
        val colors = listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Black,
            Color.White,
            Color(0x1234567890ABCDEFUL)
        )
        for (color in colors) {
            val json = gson.toJson(color)
            val deserializedColor = gson.fromJson(json, Color::class.java)
            assertEquals("Color mismatch for $color", color, deserializedColor)
        }
    }

    @Test
    fun testOffsetSerialization() {
        val offset = Offset(123.45f, 678.90f)
        val json = gson.toJson(offset)
        val deserializedOffset = gson.fromJson(json, Offset::class.java)
        assertEquals(offset, deserializedOffset)
    }
}
