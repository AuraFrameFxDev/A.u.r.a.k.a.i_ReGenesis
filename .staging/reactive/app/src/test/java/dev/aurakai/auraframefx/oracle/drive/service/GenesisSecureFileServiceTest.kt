package dev.aurakai.auraframefx.oracle.drive.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.aurakai.genesis.security.CryptographyManager
import dev.aurakai.genesis.storage.SecureStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GenesisSecureFileServiceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var cryptoManager: CryptographyManager

    @Inject
    lateinit var secureStorage: SecureStorage

    private lateinit var context: Context
    private lateinit var secureFileService: GenesisSecureFileService
    private val testFileName = "test_file.txt"
    private val testData = "Test file content".toByteArray()
    private val testDirectory = "test_dir"

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        secureFileService = GenesisSecureFileService(context, cryptoManager, secureStorage)

        // Clean up any existing test files
        context.filesDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    @After
    fun tearDown() {
        // Clean up after each test
        context.filesDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    @Test
    fun saveAndReadFile_success() = runTest {
        // Save file
        val saveResult = secureFileService.saveFile(testData, testFileName).first()
        assertTrue(saveResult is FileOperationResult.Success)

        // Read file
        val readResult = secureFileService.readFile(testFileName).first()
        assertTrue(readResult is FileOperationResult.Data)

        val readData = (readResult as FileOperationResult.Data).data
        assertArrayEquals(testData, readData)
    }

    @Test
    fun saveFileInSubdirectory_success() = runTest {
        // Save file in subdirectory
        val saveResult = secureFileService.saveFile(
            data = testData,
            fileName = testFileName,
            directory = testDirectory
        ).first()

        assertTrue(saveResult is FileOperationResult.Success)

        // Verify file exists in subdirectory
        val file = File(context.filesDir, "$testDirectory/$testFileName")
        assertTrue(file.exists())

        // Read file from subdirectory
        val readResult = secureFileService.readFile(
            fileName = testFileName,
            directory = testDirectory
        ).first()

        assertTrue(readResult is FileOperationResult.Data)
        assertArrayEquals(testData, (readResult as FileOperationResult.Data).data)
    }

    @Test
    fun deleteFile_success() = runTest {
        // Save file
        secureFileService.saveFile(testData, testFileName).first()

        // Delete file
        val deleteResult = secureFileService.deleteFile(testFileName)
        assertTrue(deleteResult is FileOperationResult.Success)

        // Verify file is deleted
        val file = File(context.filesDir, testFileName)
        assertFalse(file.exists())
    }

    @Test
    fun listFiles_returnsCorrectFiles() = runTest {
        // Save multiple files
        val fileNames = listOf("file1.txt", "file2.txt", "file3.txt")
        fileNames.forEach { fileName ->
            secureFileService.saveFile("Content for $fileName".toByteArray(), fileName).first()
        }

        // List files
        val files = secureFileService.listFiles()

        // Verify all files are listed
        assertEquals(fileNames.sorted(), files.sorted())
    }

    @Test
    fun readNonExistentFile_returnsError() = runTest {
        val readResult = secureFileService.readFile("non_existent.txt").first()

        assertTrue(readResult is FileOperationResult.Error)
        assertEquals("File not found", (readResult as FileOperationResult.Error).message)
    }

    @Test
    fun deleteNonExistentFile_returnsError() = runTest {
        val deleteResult = secureFileService.deleteFile("non_existent.txt")

        assertTrue(deleteResult is FileOperationResult.Error)
        assertEquals("File not found", (deleteResult as FileOperationResult.Error).message)
    }
}
