package dev.aurakai.auraframefx.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [CollectionFormats].
 *
 * This PR moved CollectionFormats from `dev.aurakai.auraframefx` to
 * `dev.aurakai.auraframefx.config` and removed the default `emptyList()` initializer
 * from [CollectionFormats.CSVParams.params], requiring params to always be provided
 * via a constructor argument.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectionFormatsTest {

    // ─── CSVParams ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CSVParams")
    inner class CSVParamsTests {

        @Test
        @DisplayName("constructor(List) stores the supplied list")
        fun `constructor with list stores params`() {
            val params = listOf("alpha", "beta", "gamma")
            val csv = CollectionFormats.CSVParams(params)
            assertEquals(params, csv.params)
        }

        @Test
        @DisplayName("constructor(vararg) stores each element")
        fun `constructor with vararg stores params`() {
            val csv = CollectionFormats.CSVParams("x", "y", "z")
            assertEquals(listOf("x", "y", "z"), csv.params)
        }

        @Test
        @DisplayName("toString joins elements with comma")
        fun `toString joins with comma`() {
            val csv = CollectionFormats.CSVParams(listOf("a", "b", "c"))
            assertEquals("a,b,c", csv.toString())
        }

        @Test
        @DisplayName("toString with single element has no delimiter")
        fun `toString single element`() {
            val csv = CollectionFormats.CSVParams("only")
            assertEquals("only", csv.toString())
        }

        @Test
        @DisplayName("toString with empty list returns empty string")
        fun `toString with empty list returns empty string`() {
            val csv = CollectionFormats.CSVParams(emptyList())
            assertEquals("", csv.toString())
        }

        @Test
        @DisplayName("vararg constructor with no arguments creates empty params")
        fun `vararg constructor with no arguments creates empty params`() {
            val csv = CollectionFormats.CSVParams()
            assertEquals(emptyList<String>(), csv.params)
            assertEquals("", csv.toString())
        }

        @Test
        @DisplayName("params field is mutable and can be reassigned")
        fun `params field is mutable`() {
            val csv = CollectionFormats.CSVParams("old")
            csv.params = listOf("new1", "new2")
            assertEquals("new1,new2", csv.toString())
        }

        @Test
        @DisplayName("constructor(List) with special characters preserves them")
        fun `special characters are preserved`() {
            val csv = CollectionFormats.CSVParams(listOf("a,b", "c", "d"))
            // The comma inside the first element is embedded; joinToString adds
            // delimiters between elements only.
            assertEquals("a,b,c,d", csv.toString())
        }
    }

    // ─── SSVParams ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SSVParams")
    inner class SSVParamsTests {

        @Test
        @DisplayName("constructor(List) stores params from superclass")
        fun `constructor with list stores params`() {
            val ssv = CollectionFormats.SSVParams(listOf("one", "two"))
            assertEquals(listOf("one", "two"), ssv.params)
        }

        @Test
        @DisplayName("constructor(vararg) stores params from superclass")
        fun `constructor with vararg stores params`() {
            val ssv = CollectionFormats.SSVParams("hello", "world")
            assertEquals(listOf("hello", "world"), ssv.params)
        }

        @Test
        @DisplayName("toString joins elements with space")
        fun `toString joins with space`() {
            val ssv = CollectionFormats.SSVParams(listOf("foo", "bar", "baz"))
            assertEquals("foo bar baz", ssv.toString())
        }

        @Test
        @DisplayName("toString with single element has no space")
        fun `toString single element has no trailing space`() {
            val ssv = CollectionFormats.SSVParams("solo")
            assertEquals("solo", ssv.toString())
        }

        @Test
        @DisplayName("toString with empty list returns empty string")
        fun `toString with empty list`() {
            val ssv = CollectionFormats.SSVParams(emptyList())
            assertEquals("", ssv.toString())
        }

        @Test
        @DisplayName("SSVParams is a subtype of CSVParams")
        fun `SSVParams is subtype of CSVParams`() {
            val ssv = CollectionFormats.SSVParams("a", "b")
            assertNotNull(ssv as CollectionFormats.CSVParams)
        }
    }

    // ─── TSVParams ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("TSVParams")
    inner class TSVParamsTests {

        @Test
        @DisplayName("constructor(List) stores params")
        fun `constructor with list stores params`() {
            val tsv = CollectionFormats.TSVParams(listOf("col1", "col2"))
            assertEquals(listOf("col1", "col2"), tsv.params)
        }

        @Test
        @DisplayName("toString joins elements with tab character")
        fun `toString joins with tab`() {
            val tsv = CollectionFormats.TSVParams(listOf("a", "b", "c"))
            assertEquals("a\tb\tc", tsv.toString())
        }

        @Test
        @DisplayName("vararg constructor joins elements with tab")
        fun `vararg constructor produces tab-delimited string`() {
            val tsv = CollectionFormats.TSVParams("col1", "col2", "col3")
            assertEquals("col1\tcol2\tcol3", tsv.toString())
        }

        @Test
        @DisplayName("toString with empty list returns empty string")
        fun `toString with empty list`() {
            val tsv = CollectionFormats.TSVParams(emptyList())
            assertEquals("", tsv.toString())
        }
    }

    // ─── PIPESParams ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PIPESParams")
    inner class PIPESParamsTests {

        @Test
        @DisplayName("constructor(List) stores params")
        fun `constructor with list stores params`() {
            val pipes = CollectionFormats.PIPESParams(listOf("a", "b"))
            assertEquals(listOf("a", "b"), pipes.params)
        }

        @Test
        @DisplayName("toString joins elements with pipe character")
        fun `toString joins with pipe`() {
            val pipes = CollectionFormats.PIPESParams(listOf("x", "y", "z"))
            assertEquals("x|y|z", pipes.toString())
        }

        @Test
        @DisplayName("vararg constructor produces pipe-delimited string")
        fun `vararg constructor produces pipe string`() {
            val pipes = CollectionFormats.PIPESParams("p1", "p2")
            assertEquals("p1|p2", pipes.toString())
        }

        @Test
        @DisplayName("toString with single element has no pipe")
        fun `toString single element`() {
            val pipes = CollectionFormats.PIPESParams("only")
            assertEquals("only", pipes.toString())
        }

        @Test
        @DisplayName("toString with empty list returns empty string")
        fun `toString with empty list`() {
            val pipes = CollectionFormats.PIPESParams(emptyList())
            assertEquals("", pipes.toString())
        }
    }

    // ─── SPACEParams ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SPACEParams")
    inner class SPACEParamsTests {

        @Test
        @DisplayName("no-arg constructor creates instance with empty params")
        fun `no-arg constructor creates empty params`() {
            val space = CollectionFormats.SPACEParams()
            assertEquals(emptyList<String>(), space.params)
        }

        @Test
        @DisplayName("toString on default SPACEParams returns empty string")
        fun `toString on default instance returns empty string`() {
            val space = CollectionFormats.SPACEParams()
            assertEquals("", space.toString())
        }

        @Test
        @DisplayName("SPACEParams is a subtype of SSVParams")
        fun `SPACEParams is subtype of SSVParams`() {
            val space = CollectionFormats.SPACEParams()
            assertNotNull(space as CollectionFormats.SSVParams)
        }

        @Test
        @DisplayName("SPACEParams params can be reassigned to produce space-delimited output")
        fun `reassigning params produces space-delimited output`() {
            val space = CollectionFormats.SPACEParams()
            space.params = listOf("hello", "world")
            assertEquals("hello world", space.toString())
        }
    }

    // ─── Cross-type / regression ─────────────────────────────────────────────

    @Nested
    @DisplayName("Cross-type and regression tests")
    inner class CrossTypeTests {

        @Test
        @DisplayName("each subtype uses its own delimiter")
        fun `each format type uses correct delimiter`() {
            val items = listOf("A", "B", "C")
            assertEquals("A,B,C", CollectionFormats.CSVParams(items).toString())
            assertEquals("A B C", CollectionFormats.SSVParams(items).toString())
            assertEquals("A\tB\tC", CollectionFormats.TSVParams(items).toString())
            assertEquals("A|B|C", CollectionFormats.PIPESParams(items).toString())
        }

        @Test
        @DisplayName("empty-list constructor and empty-vararg produce identical results for CSV")
        fun `empty list and empty vararg are equivalent for CSV`() {
            val fromList = CollectionFormats.CSVParams(emptyList())
            val fromVararg = CollectionFormats.CSVParams()
            assertEquals(fromList.toString(), fromVararg.toString())
            assertEquals(fromList.params, fromVararg.params)
        }

        @Test
        @DisplayName("params property holds the exact list reference from the List constructor")
        fun `params holds same elements as input list`() {
            val input = listOf("1", "2", "3")
            val csv = CollectionFormats.CSVParams(input)
            // The PR removed the `= emptyList()` default; the constructor must assign correctly.
            assertEquals(3, csv.params.size)
            assertEquals("1", csv.params[0])
            assertEquals("3", csv.params[2])
        }
    }
}