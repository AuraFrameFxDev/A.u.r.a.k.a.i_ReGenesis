package dev.aurakai.auraframefx.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for [CollectionFormats] and its nested format classes.
 *
 * Covers: package placement, constructor requirements, toString() serialization,
 * edge cases (empty lists, single element, special characters).
 */
class CollectionFormatsTest {

    // ─── CSVParams ────────────────────────────────────────────────────────────

    @Test
    fun `CSVParams list constructor stores params correctly`() {
        val params = listOf("alpha", "beta", "gamma")
        val csv = CollectionFormats.CSVParams(params)
        assertEquals(params, csv.params)
    }

    @Test
    fun `CSVParams vararg constructor stores params correctly`() {
        val csv = CollectionFormats.CSVParams("a", "b", "c")
        assertEquals(listOf("a", "b", "c"), csv.params)
    }

    @Test
    fun `CSVParams toString joins with comma`() {
        val csv = CollectionFormats.CSVParams("x", "y", "z")
        assertEquals("x,y,z", csv.toString())
    }

    @Test
    fun `CSVParams toString with single element has no separator`() {
        val csv = CollectionFormats.CSVParams("only")
        assertEquals("only", csv.toString())
    }

    @Test
    fun `CSVParams toString with empty list returns empty string`() {
        val csv = CollectionFormats.CSVParams(emptyList())
        assertEquals("", csv.toString())
    }

    @Test
    fun `CSVParams params are mutable after construction`() {
        val csv = CollectionFormats.CSVParams("initial")
        csv.params = listOf("replaced")
        assertEquals("replaced", csv.toString())
    }

    @Test
    fun `CSVParams toString with values containing commas`() {
        val csv = CollectionFormats.CSVParams("val,1", "val,2")
        // Each element is joined by comma; elements may themselves contain commas
        assertEquals("val,1,val,2", csv.toString())
    }

    // ─── SSVParams ────────────────────────────────────────────────────────────

    @Test
    fun `SSVParams list constructor stores params correctly`() {
        val params = listOf("one", "two", "three")
        val ssv = CollectionFormats.SSVParams(params)
        assertEquals(params, ssv.params)
    }

    @Test
    fun `SSVParams vararg constructor stores params correctly`() {
        val ssv = CollectionFormats.SSVParams("a", "b")
        assertEquals(listOf("a", "b"), ssv.params)
    }

    @Test
    fun `SSVParams toString joins with space`() {
        val ssv = CollectionFormats.SSVParams("hello", "world")
        assertEquals("hello world", ssv.toString())
    }

    @Test
    fun `SSVParams toString with single element has no separator`() {
        val ssv = CollectionFormats.SSVParams("alone")
        assertEquals("alone", ssv.toString())
    }

    @Test
    fun `SSVParams toString with empty list returns empty string`() {
        val ssv = CollectionFormats.SSVParams(emptyList())
        assertEquals("", ssv.toString())
    }

    @Test
    fun `SSVParams is subclass of CSVParams`() {
        val ssv = CollectionFormats.SSVParams("x")
        assertTrue(ssv is CollectionFormats.CSVParams)
    }

    // ─── TSVParams ────────────────────────────────────────────────────────────

    @Test
    fun `TSVParams list constructor stores params correctly`() {
        val params = listOf("col1", "col2", "col3")
        val tsv = CollectionFormats.TSVParams(params)
        assertEquals(params, tsv.params)
    }

    @Test
    fun `TSVParams vararg constructor stores params correctly`() {
        val tsv = CollectionFormats.TSVParams("a", "b", "c")
        assertEquals(listOf("a", "b", "c"), tsv.params)
    }

    @Test
    fun `TSVParams toString joins with tab`() {
        val tsv = CollectionFormats.TSVParams("col1", "col2", "col3")
        assertEquals("col1\tcol2\tcol3", tsv.toString())
    }

    @Test
    fun `TSVParams toString with single element has no separator`() {
        val tsv = CollectionFormats.TSVParams("single")
        assertEquals("single", tsv.toString())
    }

    @Test
    fun `TSVParams toString with empty list returns empty string`() {
        val tsv = CollectionFormats.TSVParams(emptyList())
        assertEquals("", tsv.toString())
    }

    @Test
    fun `TSVParams is subclass of CSVParams`() {
        val tsv = CollectionFormats.TSVParams("x")
        assertTrue(tsv is CollectionFormats.CSVParams)
    }

    // ─── PIPESParams ──────────────────────────────────────────────────────────

    @Test
    fun `PIPESParams list constructor stores params correctly`() {
        val params = listOf("a", "b", "c")
        val pipes = CollectionFormats.PIPESParams(params)
        assertEquals(params, pipes.params)
    }

    @Test
    fun `PIPESParams vararg constructor stores params correctly`() {
        val pipes = CollectionFormats.PIPESParams("x", "y")
        assertEquals(listOf("x", "y"), pipes.params)
    }

    @Test
    fun `PIPESParams toString joins with pipe`() {
        val pipes = CollectionFormats.PIPESParams("alpha", "beta", "gamma")
        assertEquals("alpha|beta|gamma", pipes.toString())
    }

    @Test
    fun `PIPESParams toString with single element has no separator`() {
        val pipes = CollectionFormats.PIPESParams("only")
        assertEquals("only", pipes.toString())
    }

    @Test
    fun `PIPESParams toString with empty list returns empty string`() {
        val pipes = CollectionFormats.PIPESParams(emptyList())
        assertEquals("", pipes.toString())
    }

    @Test
    fun `PIPESParams is subclass of CSVParams`() {
        val pipes = CollectionFormats.PIPESParams("x")
        assertTrue(pipes is CollectionFormats.CSVParams)
    }

    // ─── SPACEParams ──────────────────────────────────────────────────────────

    @Test
    fun `SPACEParams is subclass of SSVParams`() {
        val space = CollectionFormats.SPACEParams()
        assertTrue(space is CollectionFormats.SSVParams)
    }

    @Test
    fun `SPACEParams is subclass of CSVParams`() {
        val space = CollectionFormats.SPACEParams()
        assertTrue(space is CollectionFormats.CSVParams)
    }

    @Test
    fun `SPACEParams default construction yields empty params`() {
        val space = CollectionFormats.SPACEParams()
        assertEquals(emptyList<String>(), space.params)
    }

    @Test
    fun `SPACEParams toString with empty params returns empty string`() {
        val space = CollectionFormats.SPACEParams()
        assertEquals("", space.toString())
    }

    @Test
    fun `SPACEParams params are mutable after construction`() {
        val space = CollectionFormats.SPACEParams()
        space.params = listOf("hello", "world")
        assertEquals("hello world", space.toString())
    }

    // ─── Separator distinctness ───────────────────────────────────────────────

    @Test
    fun `all format classes produce distinct output for same input`() {
        val input = listOf("a", "b", "c")
        val csv = CollectionFormats.CSVParams(input).toString()
        val ssv = CollectionFormats.SSVParams(input).toString()
        val tsv = CollectionFormats.TSVParams(input).toString()
        val pipes = CollectionFormats.PIPESParams(input).toString()

        assertEquals("a,b,c", csv)
        assertEquals("a b c", ssv)
        assertEquals("a\tb\tc", tsv)
        assertEquals("a|b|c", pipes)

        // All four outputs must be distinct
        val outputs = setOf(csv, ssv, tsv, pipes)
        assertEquals(4, outputs.size)
    }

    // ─── Package placement ────────────────────────────────────────────────────

    @Test
    fun `CollectionFormats is in correct package`() {
        val pkg = CollectionFormats::class.java.`package`.name
        assertEquals("dev.aurakai.auraframefx.config", pkg)
    }

    // ─── Params field is required (no default) ───────────────────────────────

    @Test
    fun `CSVParams list constructor with non-empty list stores correctly`() {
        // Regression: params field no longer has a default value; constructor is required
        val expected = listOf("required", "param")
        val csv = CollectionFormats.CSVParams(expected)
        assertEquals(expected, csv.params)
        assertEquals("required,param", csv.toString())
    }

    @Test
    fun `TSVParams preserves whitespace within individual elements`() {
        val tsv = CollectionFormats.TSVParams("hello world", "foo bar")
        assertEquals("hello world\tfoo bar", tsv.toString())
    }

    @Test
    fun `CSVParams with many elements produces correct comma-separated string`() {
        val items = (1..10).map { "item$it" }
        val csv = CollectionFormats.CSVParams(items)
        assertEquals(items.joinToString(","), csv.toString())
    }
}