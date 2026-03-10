package dev.aurakai.auraframefx.app.viewmodel // Corrected package name

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Assuming DriveRepository, NetworkManager, DriveState, Direction are in a package accessible from here
// e.g., import dev.aurakai.auraframefx.model.*
// or specific imports if they are in different sub-packages of dev.aurakai.auraframefx

@OptIn(ExperimentalCoroutinesApi::class)
class OracleDriveControlViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: OracleDriveControlViewModel
    private lateinit var mockRepository: DriveRepository
    private lateinit var mockNetworkManager: NetworkManager
    private lateinit var mockStateObserver: Observer<DriveState>
    private lateinit var mockErrorObserver: Observer<String>
    private lateinit var mockLoadingObserver: Observer<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockRepository = mockk(relaxed = true)
        mockNetworkManager = mockk(relaxed = true)
        mockStateObserver = mockk(relaxed = true)
        mockErrorObserver = mockk(relaxed = true)
        mockLoadingObserver = mockk(relaxed = true)

        viewModel = OracleDriveControlViewModel(mockRepository, mockNetworkManager)

        viewModel.driveState.observeForever(mockStateObserver)
        viewModel.errorMessage.observeForever(mockErrorObserver)
        viewModel.isLoading.observeForever(mockLoadingObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.driveState.removeObserver(mockStateObserver)
        viewModel.errorMessage.removeObserver(mockErrorObserver)
        viewModel.isLoading.removeObserver(mockLoadingObserver)
        clearAllMocks()
    }

    @Test
    fun `initial state should be idle`() {
        val initialState = viewModel.driveState.value
        assertEquals(DriveState.IDLE, initialState)
        verify { mockStateObserver.onChanged(DriveState.IDLE) }
    }

    @Test
    fun `startDrive should update state to driving when successful`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)

        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockLoadingObserver.onChanged(true) }
        verify { mockStateObserver.onChanged(DriveState.DRIVING) }
        verify { mockLoadingObserver.onChanged(false) }
        coVerify { mockRepository.startDrive(any()) }
    }

    @Test
    fun `startDrive should handle network error gracefully`() = runTest {
        val errorMessage = "Network connection failed"
        coEvery { mockRepository.startDrive(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockLoadingObserver.onChanged(true) }
        verify { mockErrorObserver.onChanged(errorMessage) }
        verify { mockLoadingObserver.onChanged(false) }
        verify { mockStateObserver.onChanged(DriveState.ERROR) }
    }

    @Test
    fun `stopDrive should update state to idle when successful`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { mockRepository.stopDrive() } returns Result.success(Unit)

        viewModel.stopDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.IDLE) }
        coVerify { mockRepository.stopDrive() }
    }

    @Test
    fun `stopDrive should handle repository error`() = runTest {
        val errorMessage = "Failed to stop drive"
        coEvery { mockRepository.stopDrive() } returns Result.failure(Exception(errorMessage))

        viewModel.stopDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged(errorMessage) }
        verify { mockStateObserver.onChanged(DriveState.ERROR) }
    }

    @Test
    fun `pauseDrive should update state to paused when successful`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { mockRepository.pauseDrive() } returns Result.success(Unit)

        viewModel.pauseDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.PAUSED) }
        coVerify { mockRepository.pauseDrive() }
    }

    @Test
    fun `resumeDrive should update state to driving from paused`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { mockRepository.pauseDrive() } returns Result.success(Unit)
        viewModel.pauseDrive()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { mockRepository.resumeDrive() } returns Result.success(Unit)

        viewModel.resumeDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.DRIVING) }
        coVerify { mockRepository.resumeDrive() }
    }

    @Test
    fun `updateSpeed should call repository with correct value`() = runTest {
        val speed = 50.0
        coEvery { mockRepository.updateSpeed(speed) } returns Result.success(Unit)

        viewModel.updateSpeed(speed)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.updateSpeed(speed) }
    }

    @Test
    fun `updateSpeed should handle negative values gracefully`() = runTest {
        val invalidSpeed = -10.0

        viewModel.updateSpeed(invalidSpeed)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged("Invalid speed value: $invalidSpeed") }
        coVerify(exactly = 0) { mockRepository.updateSpeed(any()) }
    }

    @Test
    fun `updateSpeed should handle excessive values gracefully`() = runTest {
        val excessiveSpeed = 1000.0

        viewModel.updateSpeed(excessiveSpeed)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged("Speed exceeds maximum limit: $excessiveSpeed") }
        coVerify(exactly = 0) { mockRepository.updateSpeed(any()) }
    }

    @Test
    fun `changeDirection should update direction when valid`() = runTest {
        val direction = Direction.FORWARD
        coEvery { mockRepository.changeDirection(direction) } returns Result.success(Unit)

        viewModel.changeDirection(direction)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.changeDirection(direction) }
    }

    @Test
    fun `changeDirection should handle repository error`() = runTest {
        val direction = Direction.REVERSE
        val errorMessage = "Failed to change direction"
        coEvery { mockRepository.changeDirection(direction) } returns Result.failure(
            Exception(
                errorMessage
            )
        )

        viewModel.changeDirection(direction)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged(errorMessage) }
    }

    @Test
    fun `isNetworkAvailable should return network manager status`() {
        every { mockNetworkManager.isConnected() } returns true

        val result = viewModel.isNetworkAvailable()

        assertTrue(result)
        verify { mockNetworkManager.isConnected() }
    }

    @Test
    fun `isNetworkAvailable should return false when network unavailable`() {
        every { mockNetworkManager.isConnected() } returns false

        val result = viewModel.isNetworkAvailable()

        assertFalse(result)
        verify { mockNetworkManager.isConnected() }
    }

    @Test
    fun `emergency stop should immediately stop drive and update state`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)

        viewModel.emergencyStop()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.EMERGENCY_STOP) }
        coVerify { mockRepository.emergencyStop() }
    }

    @Test
    fun `emergency stop should handle repository failure`() = runTest {
        val errorMessage = "Emergency stop failed"
        coEvery { mockRepository.emergencyStop() } returns Result.failure(Exception(errorMessage))

        viewModel.emergencyStop()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged(errorMessage) }
        verify { mockStateObserver.onChanged(DriveState.ERROR) }
    }

    @Test
    fun `reset should clear error state and return to idle`() = runTest {
        coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)
        viewModel.emergencyStop()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.reset()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.IDLE) }
        verify { mockErrorObserver.onChanged("") }
    }

    @Test
    fun `concurrent operations should be handled correctly`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
        coEvery { mockRepository.stopDrive() } returns Result.success(Unit)

        viewModel.startDrive()
        viewModel.stopDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.IDLE) }
        coVerify { mockRepository.startDrive(any()) }
        coVerify { mockRepository.stopDrive() }
    }

    @Test
    fun `multiple speed updates should debounce correctly`() = runTest {
        coEvery { mockRepository.updateSpeed(any()) } returns Result.success(Unit)

        viewModel.updateSpeed(10.0)
        viewModel.updateSpeed(20.0)
        viewModel.updateSpeed(30.0)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { mockRepository.updateSpeed(30.0) }
    }

    @Test
    fun `loading state should be managed correctly across operations`() = runTest {
        coEvery { mockRepository.startDrive(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(Unit)
        }

        viewModel.startDrive()

        verify { mockLoadingObserver.onChanged(true) }

        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockLoadingObserver.onChanged(false) }
    }

    @Test
    fun `view model should handle null repository responses gracefully`() = runTest {
        coEvery { mockRepository.startDrive(any()) } returns Result.failure(NullPointerException("Repository returned null"))

        viewModel.startDrive()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged("Repository returned null") }
        verify { mockStateObserver.onChanged(DriveState.ERROR) }
    }

    @Test
    fun `view model should validate input parameters`() = runTest {
        val invalidSpeed = Double.NaN

        viewModel.updateSpeed(invalidSpeed)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockErrorObserver.onChanged("Invalid speed value: NaN") }
        coVerify(exactly = 0) { mockRepository.updateSpeed(any()) }
    }
}

// Mocked/Placeholder classes for dependencies - replace with actual imports or mocks
interface DriveRepository {
    suspend fun startDrive(params: Any): Result<Unit>
    suspend fun stopDrive(): Result<Unit>
    suspend fun pauseDrive(): Result<Unit>
    suspend fun resumeDrive(): Result<Unit>
    suspend fun updateSpeed(speed: Double): Result<Unit>
    suspend fun changeDirection(direction: Direction): Result<Unit>
    suspend fun emergencyStop(): Result<Unit>
}

interface NetworkManager {
    fun isConnected(): Boolean
}

enum class DriveState {
    IDLE, DRIVING, PAUSED, ERROR, EMERGENCY_STOP
}

enum class Direction {
    FORWARD, REVERSE
}

@Test
fun `updateSpeed should accept zero speed value`() = runTest {
    val zeroSpeed = 0.0
    coEvery { mockRepository.updateSpeed(zeroSpeed) } returns Result.success(Unit)

    viewModel.updateSpeed(zeroSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.updateSpeed(zeroSpeed) }
    verify(exactly = 0) { mockErrorObserver.onChanged(any()) }
}

@Test
fun `updateSpeed should handle infinity values`() = runTest {
    val infiniteSpeed = Double.POSITIVE_INFINITY

    viewModel.updateSpeed(infiniteSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Invalid speed value: Infinity") }
    coVerify(exactly = 0) { mockRepository.updateSpeed(any()) }
}

@Test
fun `updateSpeed should handle negative infinity values`() = runTest {
    val negativeInfiniteSpeed = Double.NEGATIVE_INFINITY

    viewModel.updateSpeed(negativeInfiniteSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Invalid speed value: -Infinity") }
    coVerify(exactly = 0) { mockRepository.updateSpeed(any()) }
}

@Test
fun `updateSpeed should handle very small positive values`() = runTest {
    val smallSpeed = 0.001
    coEvery { mockRepository.updateSpeed(smallSpeed) } returns Result.success(Unit)

    viewModel.updateSpeed(smallSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.updateSpeed(smallSpeed) }
}

@Test
fun `changeDirection should not be allowed during emergency stop state`() = runTest {
    // First trigger emergency stop
    coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)
    viewModel.emergencyStop()
    testDispatcher.scheduler.advanceUntilIdle()

    // Try to change direction during emergency stop
    val direction = Direction.FORWARD

    viewModel.changeDirection(direction)
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Cannot change direction during emergency stop") }
    coVerify(exactly = 0) { mockRepository.changeDirection(any()) }
}

@Test
fun `startDrive should not be allowed during emergency stop state`() = runTest {
    // First trigger emergency stop
    coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)
    viewModel.emergencyStop()
    testDispatcher.scheduler.advanceUntilIdle()

    // Try to start drive during emergency stop
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Cannot start drive during emergency stop") }
    coVerify(exactly = 0) { mockRepository.startDrive(any()) }
}

@Test
fun `pauseDrive should not be allowed when not driving`() = runTest {
    // Try to pause when in idle state
    viewModel.pauseDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Cannot pause when not driving") }
    coVerify(exactly = 0) { mockRepository.pauseDrive() }
}

@Test
fun `resumeDrive should not be allowed when not paused`() = runTest {
    // Try to resume when in idle state
    viewModel.resumeDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Cannot resume when not paused") }
    coVerify(exactly = 0) { mockRepository.resumeDrive() }
}

@Test
fun `updateSpeed should handle repository timeout gracefully`() = runTest {
    val speed = 25.0
    coEvery { mockRepository.updateSpeed(speed) } returns Result.failure(
        java.util.concurrent.TimeoutException(
            "Operation timed out"
        )
    )

    viewModel.updateSpeed(speed)
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Operation timed out") }
}

@Test
fun `startDrive should handle IOException gracefully`() = runTest {
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(java.io.IOException("Connection lost"))

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockLoadingObserver.onChanged(true) }
    verify { mockErrorObserver.onChanged("Connection lost") }
    verify { mockStateObserver.onChanged(DriveState.ERROR) }
    verify { mockLoadingObserver.onChanged(false) }
}

@Test
fun `viewModel should handle multiple rapid state changes`() = runTest {
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    coEvery { mockRepository.pauseDrive() } returns Result.success(Unit)
    coEvery { mockRepository.resumeDrive() } returns Result.success(Unit)
    coEvery { mockRepository.stopDrive() } returns Result.success(Unit)

    // Rapid sequence of operations
    viewModel.startDrive()
    viewModel.pauseDrive()
    viewModel.resumeDrive()
    viewModel.stopDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.DRIVING) }
    verify { mockStateObserver.onChanged(DriveState.PAUSED) }
    verify { mockStateObserver.onChanged(DriveState.DRIVING) }
    verify { mockStateObserver.onChanged(DriveState.IDLE) }
}

@Test
fun `multiple emergency stops should be handled gracefully`() = runTest {
    coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)

    viewModel.emergencyStop()
    viewModel.emergencyStop()
    viewModel.emergencyStop()
    testDispatcher.scheduler.advanceUntilIdle()

    verify(atLeast = 1) { mockStateObserver.onChanged(DriveState.EMERGENCY_STOP) }
    coVerify(atLeast = 1) { mockRepository.emergencyStop() }
}

@Test
fun `network state changes should not affect ongoing operations`() = runTest {
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    every { mockNetworkManager.isConnected() } returns true

    viewModel.startDrive()

    // Network goes down during operation
    every { mockNetworkManager.isConnected() } returns false

    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.DRIVING) }
    assertFalse(viewModel.isNetworkAvailable())
}

@Test
fun `speed updates should be validated against upper boundary`() = runTest {
    val maxSpeed = 999.9
    coEvery { mockRepository.updateSpeed(maxSpeed) } returns Result.success(Unit)

    viewModel.updateSpeed(maxSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.updateSpeed(maxSpeed) }
}

@Test
fun `direction changes should be logged for audit purposes`() = runTest {
    val directions = listOf(Direction.FORWARD, Direction.REVERSE, Direction.FORWARD)
    directions.forEach { direction ->
        coEvery { mockRepository.changeDirection(direction) } returns Result.success(Unit)
    }

    directions.forEach { direction ->
        viewModel.changeDirection(direction)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    directions.forEach { direction ->
        coVerify { mockRepository.changeDirection(direction) }
    }
}

@Test
fun `error messages should be cleared after successful operations`() = runTest {
    // First cause an error
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(Exception("Initial error"))
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Initial error") }

    // Then perform successful operation
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("") }
    verify { mockStateObserver.onChanged(DriveState.DRIVING) }
}

@Test
fun `repository operations should handle concurrent access gracefully`() = runTest {
    coEvery { mockRepository.updateSpeed(any()) } returns Result.success(Unit)
    coEvery { mockRepository.changeDirection(any()) } returns Result.success(Unit)

    // Simulate concurrent operations
    viewModel.updateSpeed(30.0)
    viewModel.changeDirection(Direction.FORWARD)
    viewModel.updateSpeed(40.0)
    viewModel.changeDirection(Direction.REVERSE)

    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.updateSpeed(40.0) }
    coVerify { mockRepository.changeDirection(Direction.REVERSE) }
}

@Test
fun `loading state should remain consistent during overlapping operations`() = runTest {
    coEvery { mockRepository.startDrive(any()) } coAnswers {
        kotlinx.coroutines.delay(200)
        Result.success(Unit)
    }
    coEvery { mockRepository.updateSpeed(any()) } coAnswers {
        kotlinx.coroutines.delay(100)
        Result.success(Unit)
    }

    viewModel.startDrive()
    viewModel.updateSpeed(25.0)

    verify { mockLoadingObserver.onChanged(true) }

    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockLoadingObserver.onChanged(false) }
}

@Test
fun `observer removal should not cause memory leaks`() = runTest {
    val additionalStateObserver: Observer<DriveState> = mockk(relaxed = true)
    val additionalErrorObserver: Observer<String> = mockk(relaxed = true)

    viewModel.driveState.observeForever(additionalStateObserver)
    viewModel.errorMessage.observeForever(additionalErrorObserver)

    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    viewModel.driveState.removeObserver(additionalStateObserver)
    viewModel.errorMessage.removeObserver(additionalErrorObserver)

    verify { additionalStateObserver.onChanged(DriveState.DRIVING) }
}

@Test
fun `emergency stop should override all other pending operations`() = runTest {
    coEvery { mockRepository.startDrive(any()) } coAnswers {
        kotlinx.coroutines.delay(1000)
        Result.success(Unit)
    }
    coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)

    viewModel.startDrive()
    viewModel.emergencyStop()

    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.EMERGENCY_STOP) }
    coVerify { mockRepository.emergencyStop() }
}

@Test
fun `reset from error state should clear all error conditions`() = runTest {
    // Trigger multiple errors
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(Exception("Start error"))
    coEvery { mockRepository.updateSpeed(any()) } returns Result.failure(Exception("Speed error"))

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()
    viewModel.updateSpeed(50.0)
    testDispatcher.scheduler.advanceUntilIdle()

    // Reset should clear all errors
    viewModel.reset()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.IDLE) }
    verify { mockErrorObserver.onChanged("") }
}

@Test
fun `speed validation should handle floating point precision issues`() = runTest {
    val precisionSpeed = 99.99999999999999
    coEvery { mockRepository.updateSpeed(any()) } returns Result.success(Unit)

    viewModel.updateSpeed(precisionSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.updateSpeed(precisionSpeed) }
}

@Test
fun `viewModel should handle repository returning unexpected result types`() = runTest {
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(IllegalStateException("Unexpected state"))

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Unexpected state") }
    verify { mockStateObserver.onChanged(DriveState.ERROR) }
}

@Test
fun `consecutive speed updates within threshold should be debounced`() = runTest {
    coEvery { mockRepository.updateSpeed(any()) } returns Result.success(Unit)

    val speeds = listOf(10.0, 10.1, 10.2, 10.3, 15.0)
    speeds.forEach { speed ->
        viewModel.updateSpeed(speed)
    }

    testDispatcher.scheduler.advanceUntilIdle()

    // Should only update with the final speed due to debouncing
    coVerify(exactly = 1) { mockRepository.updateSpeed(15.0) }
}

@Test
fun `drive operations should validate network connectivity when required`() = runTest {
    every { mockNetworkManager.isConnected() } returns false

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Network connection required for drive operations") }
    coVerify(exactly = 0) { mockRepository.startDrive(any()) }
}

@Test
fun `error recovery should allow normal operations after reset`() = runTest {
    // Trigger error state
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(Exception("Test error"))
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    // Reset and try normal operation
    viewModel.reset()
    testDispatcher.scheduler.advanceUntilIdle()

    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.DRIVING) }
}

@Test
fun `pauseDrive should handle repository failure gracefully`() = runTest {
    // Start driving first
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    // Then fail pause operation
    val errorMessage = "Failed to pause drive"
    coEvery { mockRepository.pauseDrive() } returns Result.failure(Exception(errorMessage))

    viewModel.pauseDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged(errorMessage) }
    verify { mockStateObserver.onChanged(DriveState.ERROR) }
}

@Test
fun `resumeDrive should handle repository failure gracefully`() = runTest {
    // Start and pause first
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    coEvery { mockRepository.pauseDrive() } returns Result.success(Unit)
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()
    viewModel.pauseDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    // Then fail resume operation
    val errorMessage = "Failed to resume drive"
    coEvery { mockRepository.resumeDrive() } returns Result.failure(Exception(errorMessage))

    viewModel.resumeDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged(errorMessage) }
    verify { mockStateObserver.onChanged(DriveState.ERROR) }
}

@Test
fun `stopDrive should be allowed from any state except emergency stop`() = runTest {
    coEvery { mockRepository.stopDrive() } returns Result.success(Unit)

    // Test from idle state
    viewModel.stopDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.IDLE) }
    coVerify { mockRepository.stopDrive() }
}

@Test
fun `speed boundary validation should handle maximum allowed speed`() = runTest {
    val maxAllowedSpeed = 1000.0
    coEvery { mockRepository.updateSpeed(maxAllowedSpeed) } returns Result.success(Unit)

    viewModel.updateSpeed(maxAllowedSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.updateSpeed(maxAllowedSpeed) }
}

@Test
fun `speed boundary validation should reject speed above maximum`() = runTest {
    val excessiveSpeed = 1000.1

    viewModel.updateSpeed(excessiveSpeed)
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Speed exceeds maximum limit: $excessiveSpeed") }
    coVerify(exactly = 0) { mockRepository.updateSpeed(any()) }
}

@Test
fun `startDrive should handle SecurityException gracefully`() = runTest {
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(SecurityException("Permission denied"))

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Permission denied") }
    verify { mockStateObserver.onChanged(DriveState.ERROR) }
}

@Test
fun `viewModel should handle null exception messages gracefully`() = runTest {
    coEvery { mockRepository.startDrive(any()) } returns Result.failure(Exception(null))

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockErrorObserver.onChanged("Unknown error occurred") }
    verify { mockStateObserver.onChanged(DriveState.ERROR) }
}

@Test
fun `direction change should validate state transitions properly`() = runTest {
    // Start driving
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    coEvery { mockRepository.changeDirection(any()) } returns Result.success(Unit)

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    // Change direction while driving should be allowed
    viewModel.changeDirection(Direction.REVERSE)
    testDispatcher.scheduler.advanceUntilIdle()

    coVerify { mockRepository.changeDirection(Direction.REVERSE) }
}

@Test
fun `emergency stop from paused state should work correctly`() = runTest {
    // Start and pause
    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    coEvery { mockRepository.pauseDrive() } returns Result.success(Unit)
    coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)

    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()
    viewModel.pauseDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    // Emergency stop from paused state
    viewModel.emergencyStop()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.EMERGENCY_STOP) }
    coVerify { mockRepository.emergencyStop() }
}

@Test
fun `network connectivity check should handle NetworkManager exceptions`() = runTest {
    every { mockNetworkManager.isConnected() } throws RuntimeException("Network check failed")

    val result = viewModel.isNetworkAvailable()

    assertFalse(result)
    verify { mockNetworkManager.isConnected() }
}

@Test
fun `loading state should be properly managed during failed operations`() = runTest {
    coEvery { mockRepository.startDrive(any()) } coAnswers {
        kotlinx.coroutines.delay(100)
        Result.failure(Exception("Delayed failure"))
    }

    viewModel.startDrive()

    verify { mockLoadingObserver.onChanged(true) }

    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockLoadingObserver.onChanged(false) }
    verify { mockErrorObserver.onChanged("Delayed failure") }
}

@Test
fun `multiple observers should all receive state updates`() = runTest {
    val secondStateObserver: Observer<DriveState> = mockk(relaxed = true)
    val secondErrorObserver: Observer<String> = mockk(relaxed = true)

    viewModel.driveState.observeForever(secondStateObserver)
    viewModel.errorMessage.observeForever(secondErrorObserver)

    coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit)
    viewModel.startDrive()
    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.DRIVING) }
    verify { secondStateObserver.onChanged(DriveState.DRIVING) }

    viewModel.driveState.removeObserver(secondStateObserver)
    viewModel.errorMessage.removeObserver(secondErrorObserver)
}

@Test
fun `reset should work from any state`() = runTest {
    val states = listOf(
        { coEvery { mockRepository.startDrive(any()) } returns Result.success(Unit); viewModel.startDrive() },
        { coEvery { mockRepository.pauseDrive() } returns Result.success(Unit); viewModel.pauseDrive() },
        { coEvery { mockRepository.emergencyStop() } returns Result.success(Unit); viewModel.emergencyStop() }
    )

    states.forEach { stateSetup ->
        stateSetup()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.reset()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { mockStateObserver.onChanged(DriveState.IDLE) }
        verify { mockErrorObserver.onChanged("") }
    }
}

@Test
fun `coroutine cancellation should be handled gracefully`() = runTest {
    coEvery { mockRepository.startDrive(any()) } coAnswers {
        kotlinx.coroutines.delay(10000) // Long delay to test cancellation
        Result.success(Unit)
    }
    coEvery { mockRepository.emergencyStop() } returns Result.success(Unit)

    viewModel.startDrive()
    viewModel.emergencyStop() // This should cancel the start operation

    testDispatcher.scheduler.advanceUntilIdle()

    verify { mockStateObserver.onChanged(DriveState.EMERGENCY_STOP) }
}
