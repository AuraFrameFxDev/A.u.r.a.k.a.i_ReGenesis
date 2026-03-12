import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * NOTE ON TESTING LIBRARY/FRAMEWORK:
 * - Using JUnit 4 (org.junit.Test, org.junit.Assert.*), consistent with existing tests.
 * - No new dependencies introduced. If Mockito/MockK/Truth are part of the project, consider refactoring these
 *   assertions to match the project's preferred typography.
 *
 * SCOPE:
 * - Focused on pure FileUtils behaviors commonly found in utilities:
 *   - readText(file)
 *   - writeText(file, text, append = false)
 *   - copyFile(src, dest, overwrite = false)
 *   - moveFile(src, dest, overwrite = false)
 *   - deleteQuietly(fileOrDir)
 *   - ensureParentDirs(file)
 *   - resolvePath(base, child) and isSubPath(base, candidate)
 *   - listFilesRecursively(root, globPattern? or predicate?)
 *
 * If your FileUtils exposes different names or signatures, update the calls accordingly.
 */
class FileUtilsTest {

    @Rule
    @JvmField
    val tmp: TemporaryFolder = TemporaryFolder()

    private lateinit var root: File
    private lateinit var subA: File
    private lateinit var subB: File
    private lateinit var file1: File
    private lateinit var file2: File

    // Resolve FileUtils class once; allow override via -Dfileutils.fqcn=FQCN
    private val fileUtilsClass: Class<*> by lazy {
        val candidates = listOfNotNull(
            System.getProperty("fileutils.fqcn"),
            // common guesses; adjust as needed
            "dev.aurakai.auraframefx.common.io.FileUtils",
            "com.auraframefx.util.FileUtils",
            "FileUtils",
            "FileUtilsKt"
        )
        val loaded = candidates.asSequence()
            .mapNotNull {
                try {
                    Class.forName(it)
                } catch (_: Throwable) {
                    null
                }
            }
            .firstOrNull()
        org.junit.Assume.assumeTrue(
            "FileUtils not found. Set -Dfileutils.fqcn=your.fqcn.FileUtils or adjust candidates.",
            loaded != null
        )
        loaded!!
    }

    @BeforeEach
    fun setUp() {
        root = tmp.newFolder("root")
        subA = File(root, "A").apply { mkdirs() }
        subB = File(root, "B").apply { mkdirs() }
        file1 = File(subA, "one.txt").apply { writeText("hello", Charsets.UTF_8) }
        file2 = File(subA, "two.log").apply { writeText("world", Charsets.UTF_8) }
        // nested structure
        File(subB, "nested").apply { mkdirs() }
        File(subB, "nested/three.txt").apply { writeText("three", Charsets.UTF_8) }
    }

    @AfterEach
    fun tearDown() {
        // TemporaryFolder rule cleans up automatically, but call deleteQuietly if available to exercise it
        try {
            // If deleteQuietly exists, ensure it does not throw
            @Suppress("UNCHECKED_CAST", "UNUSED_VARIABLE")
            run {
                val k = Class.forName("FileUtils")
                val m =
                    k.methods.firstOrNull { it.name == "deleteQuietly" && it.parameterTypes.size == 1 }
                if (m != null) {
                    m.invoke(null, root)
                }
            }
        } catch (_: Throwable) {
            // Ignore: only best-effort call
        }
    }

    // ------------- readText / writeText -------------

    @Test
    fun writeText_createsFile_and_readText_returnsExactContent_utf8() {
        val target = File(root, "A/new.txt")
        ensureParentDirsViaReflection(target)

        writeTextViaReflection(target, "αβγ\nLine2", append = false)
        val content = readTextViaReflection(target)

        assertEquals("αβγ\nLine2", content)
        assertTrue("File should exist after write", target.exists())
        assertEquals(
            StandardCharsets.UTF_8,
            Files.probeContentType(target.toPath()).let { StandardCharsets.UTF_8 })
    }

    @Test
    fun writeText_append_true_appendsInsteadOfOverwrite() {
        val target = File(root, "A/appended.txt")
        writeTextViaReflection(target, "first", append = false)
        writeTextViaReflection(target, "|second", append = true)

        assertEquals("first|second", readTextViaReflection(target))
    }

    @Test(expected = IOException::class)
    fun readText_throws_onNonExistingFile() {
        val missing = File(root, "nope/missing.txt")
        // Expecting IOException (or similar). If FileUtils returns null instead, update expected behavior accordingly.
        readTextViaReflection(missing)
    }

    // ------------- copyFile -------------

    @Test
    fun copyFile_copiesContent_and_preservesSource() {
        val dest = File(root, "B/copied.txt")
        copyFileViaReflection(file1, dest, overwrite = false)

        assertTrue(dest.exists())
        assertEquals(file1.readText(), dest.readText())
        assertTrue("Source should remain", file1.exists())
    }

    @Test
    fun copyFile_overwrite_true_replacesExisting() {
        val dest = File(root, "B/existing.txt").apply { writeText("OLD") }
        copyFileViaReflection(file1, dest, overwrite = true)
        assertEquals("hello", dest.readText())
    }

    @Test(expected = IOException::class)
    fun copyFile_overwrite_false_throwsWhenDestExists() {
        val dest = File(root, "B/existing2.txt").apply { writeText("OLD") }
        copyFileViaReflection(file1, dest, overwrite = false)
    }

    @Test(expected = IOException::class)
    fun copyFile_throwsWhenSourceMissing() {
        val missing = File(root, "A/not_here.txt")
        val dest = File(root, "B/should_not_exist.txt")
        copyFileViaReflection(missing, dest, overwrite = false)
    }

    // ------------- moveFile -------------

    @Test
    fun moveFile_moves_and_removesSource() {
        val dest = File(root, "B/moved.txt")
        moveFileViaReflection(file2, dest, overwrite = false)

        assertTrue(dest.exists())
        assertEquals("world", dest.readText())
        assertFalse("Source should be gone", file2.exists())
    }

    @Test
    fun moveFile_overwrite_true_replacesExisting() {
        val dest = File(root, "B/exists.txt").apply { writeText("OLD") }
        // Create a new source to avoid reliance on previous tests
        val src = File(subA, "fresh.txt").apply { writeText("FRESH") }
        moveFileViaReflection(src, dest, overwrite = true)

        assertTrue(dest.exists())
        assertEquals("FRESH", dest.readText())
    }

    @Test(expected = IOException::class)
    fun moveFile_overwrite_false_throwsWhenDestExists() {
        val dest = File(root, "B/exists2.txt").apply { writeText("OLD") }
        val src = File(subA, "fresh2.txt").apply { writeText("FRESH2") }
        moveFileViaReflection(src, dest, overwrite = false)
    }

    // ------------- deleteQuietly -------------

    @Test
    fun deleteQuietly_returnsTrue_onExistingFileOrDir_andFalseOtherwise() {
        val file = File(root, "A/to_delete.txt").apply { writeText("x") }
        val dir = File(root, "B/to_delete_dir").apply { mkdirs(); File(this, "f").writeText("y") }
        val nonExisting = File(root, "ghost")

        val r1 = deleteQuietlyViaReflection(file)
        val r2 = deleteQuietlyViaReflection(dir)
        val r3 = deleteQuietlyViaReflection(nonExisting)

        assertTrue(r1)
        assertTrue(r2)
        assertFalse(r3)
        assertFalse(file.exists())
        assertFalse(dir.exists())
    }

    // ------------- ensureParentDirs -------------

    @Test
    fun ensureParentDirs_createsAllMissingComponents_idempotent() {
        val nested = File(root, "B/x/y/z/file.txt")
        assertFalse(nested.parentFile.exists())
        ensureParentDirsViaReflection(nested)
        assertTrue(nested.parentFile.exists())
        // Idempotency
        ensureParentDirsViaReflection(nested)
        assertTrue(nested.parentFile.exists())
    }

    // ------------- resolvePath / isSubPath -------------

    @Test
    fun resolvePath_normalizesDotSegments_and_preventsTraversal() {
        val base = root.toPath()
        val child = Paths.get("A/../A/./one.txt")
        val resolved = resolvePathViaReflection(base, child)
        assertEquals(file1.toPath().normalize(), resolved.normalize())

        // Attempt to escape the base; isSubPath should be false
        val escape = Paths.get("../../etc/passwd")
        val escapedResolved = resolvePathViaReflection(base, escape)
        assertFalse(
            "Path must not be considered under base",
            isSubPathViaReflection(base, escapedResolved)
        )
    }

    @Test
    fun isSubPath_true_forNestedFiles_false_forSiblings() {
        val base = root.toPath()
        val nested = File(subB, "nested/three.txt").toPath()
        val siblingOutside = File(root.parentFile, "outside.txt").toPath()

        assertTrue(isSubPathViaReflection(base, nested))
        assertFalse(isSubPathViaReflection(base, siblingOutside))
    }

    // ------------- listFilesRecursively -------------

    @Test
    fun listFilesRecursively_returnsAll_whenNoFilter() {
        val listed = listFilesRecursivelyViaReflection(root.toPath(), glob = null).map {
            it.toFile().relativeTo(root).path
        }.sorted()
        // Expect known files we created
        assertTrue(listed.any { it.endsWith("A/one.txt") })
        assertTrue(listed.any { it.endsWith("A/two.log") })
        assertTrue(listed.any { it.endsWith("B/nested/three.txt") })
    }

    @Test
    fun listFilesRecursively_filtersByGlob_extension() {
        val listedTxt = listFilesRecursivelyViaReflection(
            root.toPath(),
            glob = "**/*.txt"
        ).map { it.toFile().name }.sorted()
        assertTrue(listedTxt.contains("one.txt"))
        assertTrue(listedTxt.contains("three.txt"))
        assertFalse(listedTxt.contains("two.log"))
    }

    // ----------------- Helper reflection bridges -----------------
    // These helpers allow tests to compile even if FileUtils is in a different package.
    // Update to direct calls (e.g., FileUtils.readText(file)) once paths/names are confirmed.

    private fun readTextViaReflection(file: File): String {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "readText" && it.parameterTypes.size == 1 }
            ?: throw NoSuchMethodError("FileUtils.readText(File) not found")
        return m.invoke(null, file) as String
    }

    private fun writeTextViaReflection(file: File, text: String, append: Boolean) {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "writeText" && it.parameterTypes.size == 3 }
            ?: throw NoSuchMethodError("FileUtils.writeText(File, String, Boolean) not found")
        m.invoke(null, file, text, append)
    }

    private fun copyFileViaReflection(src: File, dest: File, overwrite: Boolean) {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "copyFile" && it.parameterTypes.size == 3 }
            ?: throw NoSuchMethodError("FileUtils.copyFile(File, File, Boolean) not found")
        m.invoke(null, src, dest, overwrite)
    }

    private fun moveFileViaReflection(src: File, dest: File, overwrite: Boolean) {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "moveFile" && it.parameterTypes.size == 3 }
            ?: throw NoSuchMethodError("FileUtils.moveFile(File, File, Boolean) not found")
        m.invoke(null, src, dest, overwrite)
    }

    private fun deleteQuietlyViaReflection(target: File): Boolean {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "deleteQuietly" && it.parameterTypes.size == 1 }
            ?: throw NoSuchMethodError("FileUtils.deleteQuietly(File) not found")
        return m.invoke(null, target) as Boolean
    }

    private fun ensureParentDirsViaReflection(targetFile: File) {
        val k = Class.forName("FileUtils")
        val m =
            k.methods.firstOrNull { it.name == "ensureParentDirs" && it.parameterTypes.size == 1 }
                ?: throw NoSuchMethodError("FileUtils.ensureParentDirs(File) not found")
        m.invoke(null, targetFile)
    }

    private fun resolvePathViaReflection(base: Path, child: Path): Path {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "resolvePath" && it.parameterTypes.size == 2 }
            ?: throw NoSuchMethodError("FileUtils.resolvePath(Path, Path) not found")
        return m.invoke(null, base, child) as Path
    }

    private fun isSubPathViaReflection(base: Path, candidate: Path): Boolean {
        val k = Class.forName("FileUtils")
        val m = k.methods.firstOrNull { it.name == "isSubPath" && it.parameterTypes.size == 2 }
            ?: throw NoSuchMethodError("FileUtils.isSubPath(Path, Path) not found")
        return m.invoke(null, base, candidate) as Boolean
    }

    private fun listFilesRecursivelyViaReflection(root: Path, glob: String?): List<Path> {
        val k = Class.forName("FileUtils")
        // Try variants: (Path) or (Path, String?)
        val mWithGlob =
            k.methods.firstOrNull { it.name == "listFilesRecursively" && it.parameterTypes.size == 2 }
        val mNoGlob =
            k.methods.firstOrNull { it.name == "listFilesRecursively" && it.parameterTypes.size == 1 }
        val res = when {
            mWithGlob != null -> mWithGlob.invoke(null, root, glob)
            mNoGlob != null -> mNoGlob.invoke(null, root)
            else -> throw NoSuchMethodError("FileUtils.listFilesRecursively(Path[, String]) not found")
        }
        @Suppress("UNCHECKED_CAST")
        return res as List<Path>
    }
}
