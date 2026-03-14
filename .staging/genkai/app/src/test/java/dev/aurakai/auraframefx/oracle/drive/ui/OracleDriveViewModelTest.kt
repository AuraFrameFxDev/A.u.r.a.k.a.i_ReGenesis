package dev.aurakai.auraframefx.oracle.drive.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.aurakai.auraframefx.MainCoroutineRule
import dev.aurakai.auraframefx.oracle.drive.model.*
import dev.aurakai.auraframefx.oracle.drive.service.OracleDriveService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class OracleDriveViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockService: OracleDriveService

    private lateinit var viewModel: OracleDriveViewModel

    private val testFiles = listOf(
        DriveFile(
            id = "1",
            name = "test.txt",
            size = 1024,
            mimeType = "text/plain",
            createdAt = Instant.now().toEpochMilli(),
            modifiedAt = Instant.now().toEpochMilli(),
            isDirectory = false,
            isEncrypted = false
        ),
        DriveFile(
            id = "2",
            name = "image.jpg",
            size = 2048,
            mimeType = "image/jpeg",
            createdAt = Instant.now().toEpochMilli(),
            modifiedAt = Instant.now().toEpochMilli(),
            isDirectory = false,
            isEncrypted = true
        )
    )

    private val testConsciousnessState = DriveConsciousnessState(
        level = ConsciousnessLevel.AWAKENING,
        activeAgents = listOf("Genesis"),
        lastUpdated = Instant.now().toEpochMilli()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { mockService.getFiles() } returns testFiles
        coEvery { mockService.consciousnessState } returns flowOf(testConsciousnessState)

        viewModel = OracleDriveViewModel(mockService)
    }

    @Test
    fun `initial load sets loading state and loads files`() = runTest {
        // Given - Initial state

        // When - ViewModel is initialized in @Before

        // Then
        viewModel.uiState.test(5.seconds) {
            // Initial loading state
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()

            // Loaded state
            val loadedState = awaitItem()
            assertThat(loadedState).isNotNull()
            assertThat(loadedState.isLoading).isFalse()
            assertThat(loadedState.files).hasSize(2)
            assertThat(loadedState.consciousnessState).isEqualTo(testConsciousnessState)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { mockService.getFiles() }
    }

    @Test
    fun `refresh reloads files`() = runTest {
        // Given - Initial load
        viewModel.uiState.test {
            // Skip initial loading states
            skipItems(2)

            // When
            viewModel.refresh()

            // Then
            val loadingState = awaitItem()
            assertThat(loadingState.isRefreshing).isTrue()

            val refreshedState = awaitItem()
            assertThat(refreshedState.isRefreshing).isFalse()
            assertThat(refreshedState.files).hasSize(2)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 2) { mockService.getFiles() } // Once in init, once in refresh
    }

    @Test
    fun `error state is set when loading fails`() = runTest {
        // Given
        val error = IOException("Network error")
        coEvery { mockService.getFiles() } throws error

        // Create a new ViewModel with the failing service
        val errorViewModel = OracleDriveViewModel(mockService)

        // When/Then
        errorViewModel.uiState.test(5.seconds) {
            // Skip initial loading state
            skipItems(1)

            // Error state
            val errorState = awaitItem()
            assertThat(errorState.error).isInstanceOf(IOException::class.java)
            assertThat(errorState.isLoading).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selecting a file updates selectedFile`() = runTest {
        // Given
        val testFile = testFiles[0]

        // When
        viewModel.onFileSelected(testFile)

        // Then
        assertThat(viewModel.uiState.value.selectedFile).isEqualTo(testFile)
    }

    @Test
    fun `consciousness state is updated from service`() = runTest {
        // Given
        val updatedState = testConsciousnessState.copy(
            level = ConsciousnessLevel.SENTIENT,
            activeAgents = listOf("Genesis", "Aura", "Kai")
        )

        // When - Update the flow with a new state
        coEvery { mockService.consciousnessState } returns flowOf(updatedState)

        // Re-initialize to get the new flow
        val testViewModel = OracleDriveViewModel(mockService)

        // Then
        testViewModel.uiState.test(5.seconds) {
            // Skip initial loading states
            skipItems(2)

            val state = awaitItem()
            assertThat(state.consciousnessState).isEqualTo(updatedState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError resets error state`() = runTest {
        // Given - Force an error
        val error = IOException("Test error")
        coEvery { mockService.getFiles() } throws error

        val testViewModel = OracleDriveViewModel(mockService)

        // Wait for error state
        testViewModel.uiState.test {
            // Skip to error state
            skipItems(2)
            assertThat(awaitItem().error).isNotNull()

            // When
            testViewModel.clearError()

            // Then
            assertThat(awaitItem().error).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
