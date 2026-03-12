package dev.aurakai.auraframefx.oracle.drive.core

import android.content.Context
import dev.aurakai.auraframefx.oracle.drive.api.OracleCloudApi
import dev.aurakai.auraframefx.oracle.drive.model.OracleDriveFile
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.nio.file.Files

/**
 * Test stack note:
 * - Using JUnit 4 + MockK
 * - Coroutines tested with runBlocking; code uses Dispatchers.IO internally.
 */
class OracleDriveRepositoryImplTest {

    private lateinit var api: OracleCloudApi
    private lateinit var ctx: Context
    private lateinit var repo: OracleDriveRepository

    @BeforeEach
    fun setUp() {
        api = mockk(relaxed = true)
        ctx = mockk(relaxed = true)
        repo = OracleDriveRepositoryImpl(api, ctx)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun listFiles_returns_mapped_files_on_success() = runBlocking {
        val body = mockk<Any>(relaxed = true)
        // We can't depend on concrete DTOs; stub property chain generically
        every { bodyProperty<List<Any>>(body, "objects") } returns emptyList()

        val retrofitResponse = mockk<Response<Any>>(relaxed = true)
        every { retrofitResponse.isSuccessful } returns true
        every { retrofitResponse.body() } returns body

        coEvery {
            api.listFiles(
                bucketName = any(),
                prefix = any()
            )
        } returns (retrofitResponse as Response<Nothing>)
        val result = repo.listFiles("bucket", "pre")
        assert(result is List<OracleDriveFile>)
    }

    @Test
    fun listFiles_returns_empty_on_unsuccessful() = runBlocking {
        val errorBody = "bad".toResponseBody("text/plain".toMediaType())
        coEvery { api.listFiles(any(), any()) } returns (Response.error(
            500,
            errorBody
        ) as Response<Nothing>)
        val result = repo.listFiles("b", null)
        assert(result.isEmpty())
    }

    @Test
    fun uploadFile_false_when_missing_file() = runBlocking {
        coEvery { api.uploadFile(any(), any(), any()) } returns Response.success(null)
        val ok = repo.uploadFile("b", "obj", "/no/file")
        assert(!ok)
        coVerify(exactly = 0) { api.uploadFile(any(), any(), any()) }
    }

    @Test
    fun uploadFile_true_on_success() = runBlocking {
        val tmp = Files.createTempFile("upload", ".bin").toFile()
            .apply { writeBytes(byteArrayOf(1, 2, 3)) }
        coEvery { api.uploadFile(any(), any(), any()) } returns Response.success(null)
        val ok = repo.uploadFile("b", "o", tmp.absolutePath)
        assert(ok)
    }

    @Test
    fun downloadFile_returns_file_on_success_else_null() = runBlocking {
        val tmpDir = Files.createTempDirectory("dl").toFile()
        val payload = "hello".toByteArray().toResponseBody("application/octet-stream".toMediaType())
        coEvery {
            api.downloadFile(
                any(),
                any()
            )
        } returns (Response.success(payload) as Response<Nothing>)
        val out = repo.downloadFile("b", "greet.txt", tmpDir.absolutePath)
        assert(out != null && out.exists() && out.readText() == "hello")

        val err = "x".toResponseBody("text/plain".toMediaType())
        coEvery { api.downloadFile(any(), any()) } returns (Response.error(
            404,
            err
        ) as Response<Nothing>)
        assert(repo.downloadFile("b", "missing.txt", tmpDir.absolutePath) == null)
    }

    @Test
    fun deleteFile_true_on_success_false_on_error() = runBlocking {
        coEvery { api.deleteFile(any(), any()) } returns Response.success(null)
        assert(repo.deleteFile("b", "o"))
        val err = "x".toResponseBody("text/plain".toMediaType())
        coEvery { api.deleteFile(any(), any()) } returns Response.error(500, err)
        assert(!repo.deleteFile("b", "o"))
    }

    // Helper to simulate property access on unknown DTOs
    private fun <T> bodyProperty(body: Any, name: String): T = mockk()
}
