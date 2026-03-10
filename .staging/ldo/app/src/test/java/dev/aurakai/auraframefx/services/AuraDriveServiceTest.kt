package dev.aurakai.auraframefx.services

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.RemoteException
import com.example.app.ipc.IAuraDriveCallback
import com.example.app.ipc.IAuraDriveService
import dev.aurakai.auraframefx.security.MemoryVerifier
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.OutputStream
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AuraDriveServiceTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // Mocks
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockContentResolver = mockk<ContentResolver>()
    private val mockMemoryVerifier = mockk<MemoryVerifier>()
    private val mockOutputStream = mockk<OutputStream>(relaxed = true)

    // Test data
    private val testFileContent = "Test file content".toByteArray()
    private val testFileUri = mockk<Uri>()
    private val testFileId = "test_file_123"

    // System under test
    private lateinit var auraDriveService: AuraDriveService

    @Before
    fun setUp() {
        // Set up mocks
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher

        // Mock ContentResolver
        every { mockContext.contentResolver } returns mockContentResolver

        // Mock file operations
        every { mockContentResolver.openInputStream(testFileUri) } returns ByteArrayInputStream(
            testFileContent
        )
        every { mockContentResolver.openOutputStream(any<Uri>()) } returns mockOutputStream

        // Mock MemoryVerifier
        every { mockMemoryVerifier.verifyMemory(any(), any()) } returns true

        // Create service instance
        auraDriveService = AuraDriveService(mockMemoryVerifier).apply {
            attachBaseContext(mockContext)
        }

        // Start service
        auraDriveService.onCreate()
    }

    @After
    fun tearDown() {
        // Clean up
        unmockkAll()
    }

    @Test
    fun `test importFile successful`() = testScope.runTest {
        // When
        val fileId = auraDriveService.importFile(testFileUri)

        // Then
        assertTrue(fileId.isNotBlank())
        verify { mockContentResolver.openInputStream(testFileUri) }
        verify { mockMemoryVerifier.verifyMemory(testFileContent.size.toLong(), any()) }
    }

    @Test
    fun `test importFile with memory verification failure`() = testScope.runTest {
        // Given
        every { mockMemoryVerifier.verifyMemory(any(), any()) } returns false

        // When/Then
        assertFailsWith<SecurityException> {
            auraDriveService.importFile(testFileUri)
        }

        verify { mockContentResolver.openInputStream(testFileUri) }
        verify { mockMemoryVerifier.verifyMemory(testFileContent.size.toLong(), any()) }
    }

    @Test
    fun `test exportFile successful`() = testScope.runTest {
        // Given
        val destinationUri = mockk<Uri>()
        val testData = "Test export data".toByteArray()
        val testChecksum = testData.contentHashCode().toLong()

        // Store test file
        auraDriveService.javaClass.getDeclaredField("fileStorage")
            .apply { isAccessible = true }
            .set(auraDriveService, mutableMapOf(testFileId to testData))

        // When
        val result = auraDriveService.exportFile(testFileId, destinationUri)

        // Then
        assertTrue(result)
        verify { mockContentResolver.openOutputStream(destinationUri) }
        verify { mockMemoryVerifier.verifyMemory(testData.size.toLong(), testChecksum) }
    }

    @Test
    fun `test exportFile with non-existent file`() = testScope.runTest {
        // Given
        val destinationUri = mockk<Uri>()

        // When/Then
        assertFailsWith<RemoteException> {
            auraDriveService.exportFile("non_existent_file", destinationUri)
        }

        verify(exactly = 0) { mockContentResolver.openOutputStream(any()) }
    }

    @Test
    fun `test verifyFileIntegrity successful`() = testScope.runTest {
        // Given
        val testData = "Test data".toByteArray()
        val testChecksum = testData.contentHashCode().toLong()

        // Store test file
        auraDriveService.javaClass.getDeclaredField("fileStorage")
            .apply { isAccessible = true }
            .set(auraDriveService, mutableMapOf(testFileId to testData))

        // When
        val result = auraDriveService.verifyFileIntegrity(testFileId)

        // Then
        assertTrue(result)
        verify { mockMemoryVerifier.verifyMemory(testData.size.toLong(), testChecksum) }
    }

    @Test
    fun `test verifyFileIntegrity with non-existent file`() = testScope.runTest {
        // When/Then
        assertFailsWith<RemoteException> {
            auraDriveService.verifyFileIntegrity("non_existent_file")
        }
    }

    @Test
    fun `test verifyFileIntegrity with memory verification failure`() = testScope.runTest {
        // Given
        val testData = "Test data".toByteArray()

        // Store test file
        auraDriveService.javaClass.getDeclaredField("fileStorage")
            .apply { isAccessible = true }
            .set(auraDriveService, mutableMapOf(testFileId to testData))

        // Make verification fail
        every { mockMemoryVerifier.verifyMemory(any(), any()) } returns false

        // When
        val result = auraDriveService.verifyFileIntegrity(testFileId)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test service binding`() {
        // When
        val binder = auraDriveService.onBind(null)

        // Then
        assertTrue(binder is IAuraDriveService.Stub)
    }

    @Test
    fun `test register and unregister callback`() = testScope.runTest {
        // Given
        val callback = mockk<IAuraDriveCallback>(relaxed = true)

        // When
        auraDriveService.registerCallback(callback)
        auraDriveService.unregisterCallback(callback)

        // Then - No exception should be thrown
        assertTrue(true)
    }
}
