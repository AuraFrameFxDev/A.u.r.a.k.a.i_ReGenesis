package dev.aurakai.auraframefx.utils

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("JsonUtils Tests")
class JsonUtilsTest {

    @Mock
    private lateinit var mockInputStream: InputStream

    @Mock
    private lateinit var mockReader: Reader

    @Mock
    private lateinit var mockWriter: Writer

    private val sampleValidJson = """
        {
            "name": "John Doe",
            "age": 30,
            "active": true,
            "scores": [95, 87, 92],
            "address": {
                "street": "123 Main St",
                "city": "Anytown",
                "zipCode": "12345"
            },
            "metadata": null
        }
    """.trimIndent()

    private val sampleInvalidJson = """
        {
            "name": "John Doe",
            "age": 30,
            "active": true,
            "scores": [95, 87, 92,
            "address": {
                "street": "123 Main St",
                "city": "Anytown"
        }
    """.trimIndent()

    private val sampleEmptyJson = "{}"
    private val sampleArrayJson = """["item1", "item2", "item3"]"""
    private val sampleNestedJson = """
        {
            "level1": {
                "level2": {
                    "level3": {
                        "value": "deep"
                    }
                }
            }
        }
    """.trimIndent()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @AfterEach
    fun tearDown() {
        // Clean up any resources if needed
    }

    @Nested
    @DisplayName("JSON Parsing Tests")
    inner class JsonParsingTests {

        @Test
        @DisplayName("Should parse valid JSON string successfully")
        fun testParseValidJsonString() {
            val result = JsonUtils.parseJsonString(sampleValidJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            val jsonObject = result.asJsonObject
            assertEquals("John Doe", jsonObject.get("name").asString)
            assertEquals(30, jsonObject.get("age").asInt)
            assertTrue(jsonObject.get("active").asBoolean)
            assertTrue(jsonObject.get("scores").isJsonArray)
            assertTrue(jsonObject.get("address").isJsonObject)
            assertTrue(jsonObject.get("metadata").isJsonNull)
        }

        @Test
        @DisplayName("Should parse empty JSON object successfully")
        fun testParseEmptyJsonObject() {
            val result = JsonUtils.parseJsonString(sampleEmptyJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            assertTrue(result.asJsonObject.entrySet().isEmpty())
        }

        @Test
        @DisplayName("Should parse JSON array successfully")
        fun testParseJsonArray() {
            val result = JsonUtils.parseJsonString(sampleArrayJson)

            assertNotNull(result)
            assertTrue(result.isJsonArray)
            val jsonArray = result.asJsonArray
            assertEquals(3, jsonArray.size())
            assertEquals("item1", jsonArray.get(0).asString)
            assertEquals("item2", jsonArray.get(1).asString)
            assertEquals("item3", jsonArray.get(2).asString)
        }

        @Test
        @DisplayName("Should parse deeply nested JSON successfully")
        fun testParseDeeplyNestedJson() {
            val result = JsonUtils.parseJsonString(sampleNestedJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            val deepValue = result.asJsonObject
                .get("level1").asJsonObject
                .get("level2").asJsonObject
                .get("level3").asJsonObject
                .get("value").asString
            assertEquals("deep", deepValue)
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["   ", "\n\t", ""])
        @DisplayName("Should handle null, empty, and whitespace-only JSON strings")
        fun testParseNullEmptyWhitespaceJson(input: String?) {
            assertThrows<IllegalArgumentException> {
                JsonUtils.parseJsonString(input)
            }
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON syntax")
        fun testParseInvalidJsonSyntax() {
            assertThrows<JsonSyntaxException> {
                JsonUtils.parseJsonString(sampleInvalidJson)
            }
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "{ \"key\": }",
                "{ \"key\": value }",
                "{ key: \"value\" }",
                "{ \"key\": \"value\", }",
                "[ 1, 2, 3, ]",
                "{ \"unclosed\": \"string"
            ]
        )
        @DisplayName("Should throw exception for various malformed JSON")
        fun testParseMalformedJson(malformedJson: String) {
            assertThrows<JsonSyntaxException> {
                JsonUtils.parseJsonString(malformedJson)
            }
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    inner class JsonSerializationTests {

        @Test
        @DisplayName("Should serialize object to JSON string successfully")
        fun testSerializeObjectToJson() {
            val testObject = TestDataClass("Test", 42, true, listOf(1, 2, 3))

            val jsonString = JsonUtils.toJsonString(testObject)

            assertNotNull(jsonString)
            assertTrue(jsonString.contains("\"name\":\"Test\""))
            assertTrue(jsonString.contains("\"value\":42"))
            assertTrue(jsonString.contains("\"active\":true"))
            assertTrue(jsonString.contains("\"numbers\":[1,2,3]"))
        }

        @Test
        @DisplayName("Should serialize null object to null JSON")
        fun testSerializeNullObject() {
            val jsonString = JsonUtils.toJsonString(null)

            assertEquals("null", jsonString)
        }

        @Test
        @DisplayName("Should serialize empty object successfully")
        fun testSerializeEmptyObject() {
            val emptyObject = EmptyTestClass()

            val jsonString = JsonUtils.toJsonString(emptyObject)

            assertNotNull(jsonString)
            assertEquals("{}", jsonString)
        }

        @Test
        @DisplayName("Should serialize collection to JSON array")
        fun testSerializeCollectionToJson() {
            val collection = listOf("item1", "item2", "item3")

            val jsonString = JsonUtils.toJsonString(collection)

            assertNotNull(jsonString)
            assertTrue(jsonString.contains("\"item1\""))
            assertTrue(jsonString.contains("\"item2\""))
            assertTrue(jsonString.contains("\"item3\""))
        }

        @Test
        @DisplayName("Should serialize map to JSON object")
        fun testSerializeMapToJson() {
            val map = mapOf(
                "key1" to "value1",
                "key2" to 42,
                "key3" to true
            )

            val jsonString = JsonUtils.toJsonString(map)

            assertNotNull(jsonString)
            assertTrue(jsonString.contains("\"key1\":\"value1\""))
            assertTrue(jsonString.contains("\"key2\":42"))
            assertTrue(jsonString.contains("\"key3\":true"))
        }
    }

    @Nested
    @DisplayName("JSON Deserialization Tests")
    inner class JsonDeserializationTests {

        @Test
        @DisplayName("Should deserialize JSON string to object successfully")
        fun testDeserializeJsonToObject() {
            val jsonString = """{"name":"Test","value":42,"active":true,"numbers":[1,2,3]}"""

            val result =
                JsonUtils.fromJsonString<TestDataClass>(jsonString, TestDataClass::class.java)

            assertNotNull(result)
            assertEquals("Test", result.name)
            assertEquals(42, result.value)
            assertTrue(result.active)
            assertEquals(listOf(1, 2, 3), result.numbers)
        }

        @Test
        @DisplayName("Should deserialize empty JSON object successfully")
        fun testDeserializeEmptyJsonObject() {
            val jsonString = "{}"

            val result =
                JsonUtils.fromJsonString<EmptyTestClass>(jsonString, EmptyTestClass::class.java)

            assertNotNull(result)
        }

        @Test
        @DisplayName("Should throw exception when deserializing invalid JSON")
        fun testDeserializeInvalidJson() {
            val invalidJson = "{ invalid json }"

            assertThrows<JsonSyntaxException> {
                JsonUtils.fromJsonString<TestDataClass>(invalidJson, TestDataClass::class.java)
            }
        }

        @Test
        @DisplayName("Should throw exception when deserializing JSON with wrong type")
        fun testDeserializeWrongType() {
            val jsonString = """{"name":"Test","value":"not_a_number","active":true}"""

            assertThrows<JsonSyntaxException> {
                JsonUtils.fromJsonString<TestDataClass>(jsonString, TestDataClass::class.java)
            }
        }
    }

    @Nested
    @DisplayName("Stream Processing Tests")
    inner class StreamProcessingTests {

        @Test
        @DisplayName("Should read JSON from InputStream successfully")
        fun testReadJsonFromInputStream() {
            val inputStream =
                ByteArrayInputStream(sampleValidJson.toByteArray(StandardCharsets.UTF_8))

            val result = JsonUtils.parseJsonFromStream(inputStream)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            assertEquals("John Doe", result.asJsonObject.get("name").asString)
        }

        @Test
        @DisplayName("Should read JSON from Reader successfully")
        fun testReadJsonFromReader() {
            val reader = StringReader(sampleValidJson)

            val result = JsonUtils.parseJsonFromReader(reader)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            assertEquals("John Doe", result.asJsonObject.get("name").asString)
        }

        @Test
        @DisplayName("Should write JSON to Writer successfully")
        fun testWriteJsonToWriter() {
            val writer = StringWriter()
            val testObject = TestDataClass("Test", 42, true, listOf(1, 2, 3))

            JsonUtils.writeJsonToWriter(testObject, writer)

            val result = writer.toString()
            assertNotNull(result)
            assertTrue(result.contains("\"name\":\"Test\""))
            assertTrue(result.contains("\"value\":42"))
        }

        @Test
        @DisplayName("Should handle IOException when reading from stream")
        fun testReadJsonFromStreamIOException() {
            whenever(mockInputStream.read()).thenThrow(IOException("Stream error"))

            assertThrows<IOException> {
                JsonUtils.parseJsonFromStream(mockInputStream)
            }
        }

        @Test
        @DisplayName("Should handle IOException when writing to writer")
        fun testWriteJsonToWriterIOException() {
            whenever(mockWriter.write(any<String>())).thenThrow(IOException("Write error"))
            val testObject = TestDataClass("Test", 42, true, listOf(1, 2, 3))

            assertThrows<IOException> {
                JsonUtils.writeJsonToWriter(testObject, mockWriter)
            }
        }
    }

    @Nested
    @DisplayName("JSON Validation Tests")
    inner class JsonValidationTests {

        @Test
        @DisplayName("Should validate correct JSON as valid")
        fun testValidJsonValidation() {
            assertTrue(JsonUtils.isValidJson(sampleValidJson))
        }

        @Test
        @DisplayName("Should validate empty JSON object as valid")
        fun testEmptyJsonValidation() {
            assertTrue(JsonUtils.isValidJson(sampleEmptyJson))
        }

        @Test
        @DisplayName("Should validate JSON array as valid")
        fun testJsonArrayValidation() {
            assertTrue(JsonUtils.isValidJson(sampleArrayJson))
        }

        @Test
        @DisplayName("Should validate deeply nested JSON as valid")
        fun testDeeplyNestedJsonValidation() {
            assertTrue(JsonUtils.isValidJson(sampleNestedJson))
        }

        @Test
        @DisplayName("Should identify invalid JSON as invalid")
        fun testInvalidJsonValidation() {
            assertFalse(JsonUtils.isValidJson(sampleInvalidJson))
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = ["   ", "\n\t"])
        @DisplayName("Should handle null, empty, and whitespace-only strings in validation")
        fun testNullEmptyWhitespaceValidation(input: String?) {
            assertFalse(JsonUtils.isValidJson(input))
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "{ \"key\": }",
                "{ \"key\": value }",
                "{ key: \"value\" }",
                "{ \"key\": \"value\", }",
                "[ 1, 2, 3, ]",
                "{ \"unclosed\": \"string"
            ]
        )
        @DisplayName("Should identify various malformed JSON as invalid")
        fun testMalformedJsonValidation(malformedJson: String) {
            assertFalse(JsonUtils.isValidJson(malformedJson))
        }
    }

    @Nested
    @DisplayName("JSON Formatting Tests")
    inner class JsonFormattingTests {

        @Test
        @DisplayName("Should format JSON with proper indentation")
        fun testFormatJsonWithIndentation() {
            val compactJson = """{"name":"John","age":30,"active":true}"""

            val formatted = JsonUtils.formatJson(compactJson, 2)

            assertNotNull(formatted)
            assertTrue(formatted.contains("  \"name\": \"John\""))
            assertTrue(formatted.contains("  \"age\": 30"))
            assertTrue(formatted.contains("  \"active\": true"))
        }

        @Test
        @DisplayName("Should format JSON with custom indentation")
        fun testFormatJsonWithCustomIndentation() {
            val compactJson = """{"name":"John","age":30}"""

            val formatted = JsonUtils.formatJson(compactJson, 4)

            assertNotNull(formatted)
            assertTrue(formatted.contains("    \"name\": \"John\""))
            assertTrue(formatted.contains("    \"age\": 30"))
        }

        @Test
        @DisplayName("Should minify formatted JSON")
        fun testMinifyJson() {
            val formattedJson = """
                {
                    "name": "John",
                    "age": 30,
                    "active": true
                }
            """.trimIndent()

            val minified = JsonUtils.minifyJson(formattedJson)

            assertNotNull(minified)
            assertFalse(minified.contains("  "))
            assertFalse(minified.contains("\n"))
            assertTrue(minified.contains("\"name\":\"John\""))
            assertTrue(minified.contains("\"age\":30"))
            assertTrue(minified.contains("\"active\":true"))
        }

        @Test
        @DisplayName("Should handle empty JSON in formatting")
        fun testFormatEmptyJson() {
            val formatted = JsonUtils.formatJson("{}", 2)

            assertNotNull(formatted)
            assertEquals("{}", formatted.trim())
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON in formatting")
        fun testFormatInvalidJson() {
            assertThrows<JsonSyntaxException> {
                JsonUtils.formatJson("{ invalid json }", 2)
            }
        }
    }

    @Nested
    @DisplayName("JSON Path and Query Tests")
    inner class JsonPathTests {

        @Test
        @DisplayName("Should extract value using simple JSON path")
        fun testExtractValueSimplePath() {
            val result = JsonUtils.extractValue(sampleValidJson, "$.name")

            assertNotNull(result)
            assertEquals("John Doe", result)
        }

        @Test
        @DisplayName("Should extract value using nested JSON path")
        fun testExtractValueNestedPath() {
            val result = JsonUtils.extractValue(sampleValidJson, "$.address.city")

            assertNotNull(result)
            assertEquals("Anytown", result)
        }

        @Test
        @DisplayName("Should extract value using array index JSON path")
        fun testExtractValueArrayPath() {
            val result = JsonUtils.extractValue(sampleValidJson, "$.scores[0]")

            assertNotNull(result)
            assertEquals(95, result)
        }

        @Test
        @DisplayName("Should return null for non-existent path")
        fun testExtractValueNonExistentPath() {
            val result = JsonUtils.extractValue(sampleValidJson, "$.nonexistent")

            assertNull(result)
        }

        @Test
        @DisplayName("Should handle complex path expressions")
        fun testExtractValueComplexPath() {
            val result = JsonUtils.extractValue(sampleValidJson, "$.scores[*]")

            assertNotNull(result)
            assertTrue(result is List<*>)
            val scores = result as List<*>
            assertEquals(3, scores.size)
            assertTrue(scores.contains(95))
            assertTrue(scores.contains(87))
            assertTrue(scores.contains(92))
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON path")
        fun testExtractValueInvalidPath() {
            assertThrows<IllegalArgumentException> {
                JsonUtils.extractValue(sampleValidJson, "$.invalid.[path")
            }
        }
    }

    @Nested
    @DisplayName("JSON Transformation Tests")
    inner class JsonTransformationTests {

        @Test
        @DisplayName("Should merge two JSON objects successfully")
        fun testMergeJsonObjects() {
            val json1 = """{"name":"John","age":30}"""
            val json2 = """{"city":"New York","age":31}"""

            val merged = JsonUtils.mergeJsonObjects(json1, json2)

            assertNotNull(merged)
            assertTrue(merged.contains("\"name\":\"John\""))
            assertTrue(merged.contains("\"age\":31"))
            assertTrue(merged.contains("\"city\":\"New York\""))
        }

        @Test
        @DisplayName("Should filter JSON object by keys")
        fun testFilterJsonByKeys() {
            val keys = listOf("name", "age")

            val filtered = JsonUtils.filterJsonByKeys(sampleValidJson, keys)

            assertNotNull(filtered)
            assertTrue(filtered.contains("\"name\":\"John Doe\""))
            assertTrue(filtered.contains("\"age\":30"))
            assertFalse(filtered.contains("\"active\""))
            assertFalse(filtered.contains("\"scores\""))
        }

        @Test
        @DisplayName("Should exclude keys from JSON object")
        fun testExcludeKeysFromJson() {
            val keysToExclude = listOf("scores", "address")

            val filtered = JsonUtils.excludeKeysFromJson(sampleValidJson, keysToExclude)

            assertNotNull(filtered)
            assertTrue(filtered.contains("\"name\":\"John Doe\""))
            assertTrue(filtered.contains("\"age\":30"))
            assertTrue(filtered.contains("\"active\":true"))
            assertFalse(filtered.contains("\"scores\""))
            assertFalse(filtered.contains("\"address\""))
        }

        @Test
        @DisplayName("Should transform JSON values using custom function")
        fun testTransformJsonValues() {
            val transformer: (Any) -> Any = { value ->
                if (value is String) value.uppercase() else value
            }

            val transformed = JsonUtils.transformJsonValues(sampleValidJson, transformer)

            assertNotNull(transformed)
            assertTrue(transformed.contains("\"name\":\"JOHN DOE\""))
            assertTrue(transformed.contains("\"age\":30"))
            assertTrue(transformed.contains("\"active\":true"))
        }
    }

    @Nested
    @DisplayName("Performance and Edge Case Tests")
    inner class PerformanceEdgeCaseTests {

        @Test
        @DisplayName("Should handle large JSON objects efficiently")
        fun testLargeJsonHandling() {
            val largeJsonBuilder = StringBuilder()
            largeJsonBuilder.append("{")
            for (i in 1..1000) {
                largeJsonBuilder.append("\"key$i\":\"value$i\"")
                if (i < 1000) largeJsonBuilder.append(",")
            }
            largeJsonBuilder.append("}")

            val largeJson = largeJsonBuilder.toString()

            val startTime = System.currentTimeMillis()
            val result = JsonUtils.parseJsonString(largeJson)
            val endTime = System.currentTimeMillis()

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            assertTrue(endTime - startTime < 1000) // Should parse in less than 1 second
        }

        @Test
        @DisplayName("Should handle deeply nested JSON structures")
        fun testDeeplyNestedJsonHandling() {
            val deepJson = generateDeeplyNestedJson(50)

            val result = JsonUtils.parseJsonString(deepJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
        }

        @Test
        @DisplayName("Should handle JSON with special characters")
        fun testJsonWithSpecialCharacters() {
            val specialJson = """
                {
                    "unicode": "Hello 世界",
                    "emoji": "😀😁😂",
                    "escaped": "Line 1\nLine 2\tTabbed",
                    "quote": "He said \"Hello\"",
                    "backslash": "Path\\to\\file"
                }
            """.trimIndent()

            val result = JsonUtils.parseJsonString(specialJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            val jsonObject = result.asJsonObject
            assertTrue(jsonObject.get("unicode").asString.contains("世界"))
            assertTrue(jsonObject.get("emoji").asString.contains("😀"))
            assertTrue(jsonObject.get("escaped").asString.contains("\n"))
            assertTrue(jsonObject.get("quote").asString.contains("\"Hello\""))
            assertTrue(jsonObject.get("backslash").asString.contains("\\"))
        }

        @Test
        @DisplayName("Should handle concurrent JSON operations safely")
        fun testConcurrentJsonOperations() {
            val futures = (1..10).map { index ->
                CompletableFuture.supplyAsync {
                    val json = """{"id":$index,"name":"Item$index"}"""
                    JsonUtils.parseJsonString(json)
                }
            }

            val results = futures.map { it.get() }

            assertEquals(10, results.size)
            results.forEachIndexed { index, result ->
                assertNotNull(result)
                assertTrue(result.isJsonObject)
                assertEquals(index + 1, result.asJsonObject.get("id").asInt)
            }
        }

        @Test
        @DisplayName("Should handle JSON with null values correctly")
        fun testJsonWithNullValues() {
            val nullJson = """
                {
                    "name": null,
                    "value": 42,
                    "optional": null,
                    "array": [1, null, 3]
                }
            """.trimIndent()

            val result = JsonUtils.parseJsonString(nullJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            val jsonObject = result.asJsonObject
            assertTrue(jsonObject.get("name").isJsonNull)
            assertEquals(42, jsonObject.get("value").asInt)
            assertTrue(jsonObject.get("optional").isJsonNull)
            assertTrue(jsonObject.get("array").asJsonArray.get(1).isJsonNull)
        }

        @Test
        @DisplayName("Should handle empty arrays and objects correctly")
        fun testEmptyArraysAndObjects() {
            val emptyJson = """
                {
                    "emptyObject": {},
                    "emptyArray": [],
                    "nestedEmpty": {
                        "innerEmpty": {}
                    }
                }
            """.trimIndent()

            val result = JsonUtils.parseJsonString(emptyJson)

            assertNotNull(result)
            assertTrue(result.isJsonObject)
            val jsonObject = result.asJsonObject
            assertTrue(jsonObject.get("emptyObject").isJsonObject)
            assertTrue(jsonObject.get("emptyObject").asJsonObject.entrySet().isEmpty())
            assertTrue(jsonObject.get("emptyArray").isJsonArray)
            assertEquals(0, jsonObject.get("emptyArray").asJsonArray.size())
        }

        private fun generateDeeplyNestedJson(depth: Int): String {
            val builder = StringBuilder()
            repeat(depth) {
                builder.append("{\"level$it\":")
            }
            builder.append("\"deep_value\"")
            repeat(depth) {
                builder.append("}")
            }
            return builder.toString()
        }
    }

    @Nested
    @DisplayName("Type Safety and Generic Tests")
    inner class TypeSafetyTests {

        @Test
        @DisplayName("Should handle generic type deserialization safely")
        fun testGenericTypeDeserialization() {
            val listJson = """["item1", "item2", "item3"]"""

            val result = JsonUtils.fromJsonString<List<String>>(
                listJson,
                object : TypeToken<List<String>>() {}.type
            )

            assertNotNull(result)
            assertEquals(3, result.size)
            assertEquals("item1", result[0])
            assertEquals("item2", result[1])
            assertEquals("item3", result[2])
        }

        @Test
        @DisplayName("Should handle map type deserialization safely")
        fun testMapTypeDeserialization() {
            val mapJson = """{"key1":"value1","key2":"value2"}"""

            val result = JsonUtils.fromJsonString<Map<String, String>>(
                mapJson,
                object : TypeToken<Map<String, String>>() {}.type
            )

            assertNotNull(result)
            assertEquals(2, result.size)
            assertEquals("value1", result["key1"])
            assertEquals("value2", result["key2"])
        }

        @Test
        @DisplayName("Should handle nested generic type deserialization")
        fun testNestedGenericTypeDeserialization() {
            val nestedJson = """{"items":[{"id":1,"name":"Item1"},{"id":2,"name":"Item2"}]}"""

            val result = JsonUtils.fromJsonString<Map<String, List<Map<String, Any>>>>(
                nestedJson,
                object : TypeToken<Map<String, List<Map<String, Any>>>>() {}.type
            )

            assertNotNull(result)
            assertTrue(result.containsKey("items"))
            val items = result["items"] as List<Map<String, Any>>
            assertEquals(2, items.size)
            assertEquals(1.0, items[0]["id"]) // JSON numbers are parsed as doubles
            assertEquals("Item1", items[0]["name"])
        }
    }

    // Test data classes for serialization/deserialization tests
    data class TestDataClass(
        val name: String,
        val value: Int,
        val active: Boolean,
        val numbers: List<Int>,
    )

    class EmptyTestClass

    // Additional utility test classes
    data class ComplexTestClass(
        val id: String,
        val timestamp: Long,
        val metadata: Map<String, Any>,
        val tags: Set<String>,
        val nested: NestedTestClass?,
    )

    data class NestedTestClass(
        val description: String,
        val priority: Int,
    )
}