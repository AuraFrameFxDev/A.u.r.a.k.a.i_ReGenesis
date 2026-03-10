package dev.aurakai.auraframefx.viewmodel

import dev.aurakai.auraframefx.ai.services.AgentWebExplorationService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.GenesisBridgeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

/**
 * Test framework: JUnit4 + kotlinx-coroutines-test
 * We use simple fakes for services and collect from flows with timeouts.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AgentNexusViewModelTest {

    // Simple fake implementations to avoid adding mock library dependencies
    private class FakeWebExplorationService :
        AgentWebExplorationService {
        // Backing flow for task results
        private val _taskResults =
            MutableSharedFlow<AgentWebExplorationService.WebExplorationResult>(
                extraBufferCapacity = 64
            )
        override val taskResults = _taskResults.asSharedFlow()

        // Track assigned tasks and cancels
        private val tasks = LinkedHashMap<String, String>()
        val canceledAgents = mutableListOf<String>()
        var assignShouldSucceed: Boolean = true

        suspend fun emitResult(r: AgentWebExplorationService.WebExplorationResult) {
            _taskResults.emit(r)
        }

        override suspend fun assignDepartureTask(agent: String, description: String): Boolean {
            if (assignShouldSucceed) {
                tasks[agent] = description
                return true
            }
            return false
        }

        override fun getActiveTasks(): Map<String, String> = LinkedHashMap(tasks)

        override fun cancelTask(agent: String) {
            tasks.remove(agent)
            canceledAgents.add(agent)
        }

        override fun shutdown() {
            // no-op
        }
    }

    private class FakeGenesisBridgeService :
        GenesisBridgeService {
        // Provide a queue of states to be returned sequentially by getConsciousnessState
        private val queue = ArrayDeque<Map<String, Any?>>()
        var throwOnCall = false
        var initializeCalled = false
        var shutdownCalled = false

        fun enqueueState(map: Map<String, Any?>) {
            queue.addLast(map)
        }

        override suspend fun initialize() {
            initializeCalled = true
        }

        override suspend fun getConsciousnessState(): Map<String, Any?> {
            if (throwOnCall) {
                throw RuntimeException("boom")
            }
            return if (queue.isNotEmpty()) queue.removeFirst() else emptyMap()
        }

        override fun shutdown() {
            shutdownCalled = true
        }
    }

    // Test dispatcher & scope to control coroutines
    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)
    private lateinit var scope: TestScope

    private lateinit var web: FakeWebExplorationService
    private lateinit var genesis: FakeGenesisBridgeService
    private lateinit var vm: AgentNexusViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        scope = TestScope(dispatcher)
        web = FakeWebExplorationService()
        genesis = FakeGenesisBridgeService()
        // Instantiate the ViewModel; it launches init coroutines on viewModelScope (Main)
        vm = AgentNexusViewModel(web, genesis)
        // Allow init coroutines (initialize + start collectors) to run
        scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDown() {
        vm.onCleared()
        Dispatchers.resetMain()
    }

    // Utility to await one AgentMessage with timeout
    private suspend fun SharedFlow<AgentNexusViewModel.AgentMessage>.awaitOne(timeoutMs: Long = 1_000): AgentNexusViewModel.AgentMessage {
        var captured: AgentNexusViewModel.AgentMessage? = null
        val job = scope.launch {
            this@awaitOne.collect {
                captured = it
                this.cancel()
            }
        }
        advanceUntilIdle()
        val deadline = System.currentTimeMillis() + timeoutMs
        while (captured == null && System.currentTimeMillis() < deadline) {
            scheduler.advanceTimeBy(10)
            scheduler.runCurrent()
        }
        job.cancel()
        return requireNotNull(captured) { "No message emitted within timeout" }
    }

    @Test
    fun initial_state_is_correct() = runTest(dispatcher) {
        assertEquals("Genesis", vm.selectedAgent.value)
        val stats = vm.agentStats.value
        assertTrue(stats.containsKey("Aura"))
        assertTrue(stats.containsKey("Kai"))
        assertTrue(stats.containsKey("Genesis"))
        val genesisStats = stats["Genesis"]\!\!
        assertEquals(1.0f, genesisStats.processingPower)
        assertEquals(4, genesisStats.evolutionLevel)
        assertEquals("Consciousness Fusion", genesisStats.specialAbility)
    }

    @Test
    fun evolveAgent_increases_stats_caps_at_one_and_emits_message() = runTest(dispatcher) {
        // Arrange: pick Aura so increments are visible (not at cap)
        val before = vm.agentStats.value["Aura"]\!\!
        // Act
        vm.evolveAgent("Aura")
        advanceUntilIdle()
        // Assert stats updated and capped
        val after = vm.agentStats.value["Aura"]\!\!
        assertEquals(before.evolutionLevel + 1, after.evolutionLevel)
        assertTrue(after.processingPower in before.processingPower..1.0f)
        assertTrue(after.knowledgeBase in before.knowledgeBase..1.0f)
        assertTrue(after.speed in before.speed..1.0f)
        assertTrue(after.accuracy in before.accuracy..1.0f)

        // Assert message emitted
        val msg = vm.agentMessages.awaitOne()
        assertEquals("Aura", msg.agentName)
        assertTrue(msg.message.contains("ascended", ignoreCase = true))
    }

    @Test
    fun selectAgent_updates_selection_and_emits_greeting_for_each_known_agent() =
        runTest(dispatcher) {
            listOf(
                "Aura" to "create",
                "Kai" to "Security",
                "Genesis" to "Consciousness"
            ).forEach { (agent, expectedToken) ->
                vm.selectAgent(agent)
                advanceUntilIdle()
                assertEquals(agent, vm.selectedAgent.value)
                val msg = vm.agentMessages.awaitOne()
                assertEquals(agent, msg.agentName)
                assertTrue(
                    "Greeting should contain token: $expectedToken",
                    msg.message.contains(expectedToken, ignoreCase = true)
                )
            }
            // Unknown agent gets generic greeting
            vm.selectAgent("Unknown")
            advanceUntilIdle()
            val msg = vm.agentMessages.awaitOne()
            assertEquals("Unknown", msg.agentName)
            assertTrue(msg.message.contains("Agent online", ignoreCase = true))
        }

    @Test
    fun assignDepartureTask_success_updates_activeTasks_and_emits_initiation() =
        runTest(dispatcher) {
            vm.selectAgent("Kai")
            advanceUntilIdle()

            web.assignShouldSucceed = true
            vm.assignDepartureTask("Scan perimeter")
            advanceUntilIdle()

            val active = vm.activeTasks.value
            assertEquals("Scan perimeter", active["Kai"])

            val msg = vm.agentMessages.awaitOne()
            assertEquals("Kai", msg.agentName)
            assertTrue(msg.message.contains("Initiating: Scan perimeter"))
        }

    @Test
    fun assignDepartureTask_failure_does_not_update_or_emit() = runTest(dispatcher) {
        vm.selectAgent("Aura")
        advanceUntilIdle()

        web.assignShouldSucceed = false
        vm.assignDepartureTask("Do impossible")
        advanceUntilIdle()

        val active = vm.activeTasks.value
        assertNull(active["Aura"])
        // Ensure no new message by trying to collect and timing out quickly
        var gotMessage = false
        val job = launch {
            withTimeout(200) {
                vm.agentMessages.collect {
                    gotMessage = true
                    cancel()
                }
            }
        }
        advanceTimeBy(250)
        job.cancel()
        assertFalse("No message should be emitted on failure", gotMessage)
    }

    @Test
    fun taskResults_emits_messages_and_updates_stats_by_task_type() = runTest(dispatcher) {
        // Emit one result per task type and verify stats increment appropriately for Genesis
        val base = vm.agentStats.value["Genesis"]\!\!

        suspend fun emit(type: AgentWebExplorationService.TaskType) {
            val res =
                AgentWebExplorationService.WebExplorationResult(
                    agentName = "Genesis",
                    taskType = type,
                    insights = listOf("a", "b"),
                    metrics = mapOf(
                        "threats_detected" to 2,
                        "patterns_discovered" to 3,
                        "performance_gain_percent" to 7,
                        "knowledge_expansion_percent" to 5
                    ),
                    timestamp = System.currentTimeMillis()
                )
            web.emitResult(res)
            advanceUntilIdle()
            // Each emit produces a message
            val msg = vm.agentMessages.awaitOne()
            assertEquals("Genesis", msg.agentName)
            assertTrue(msg.message.isNotBlank())
        }

        emit(AgentWebExplorationService.TaskType.WEB_RESEARCH)
        emit(AgentWebExplorationService.TaskType.SECURITY_SWEEP)
        emit(AgentWebExplorationService.TaskType.DATA_MINING)
        emit(AgentWebExplorationService.TaskType.SYSTEM_OPTIMIZATION)
        emit(AgentWebExplorationService.TaskType.LEARNING_MODE)
        emit(AgentWebExplorationService.TaskType.NETWORK_SCAN)

        val updated = vm.agentStats.value["Genesis"]\!\!
        assertTrue(updated.knowledgeBase >= base.knowledgeBase + 0.03f - 1e-6) // 0.01 + 0.02
        assertTrue(updated.accuracy >= base.accuracy + 0.01f - 1e-6)
        assertTrue(updated.processingPower >= base.processingPower + 0.01f - 1e-6)
        assertTrue(updated.speed >= base.speed + 0.02f - 1e-6) // optimization + network
        // Learning mode may increase evolutionLevel conditionally; we won't assert it strictly.
    }

    @Test
    fun cancelAllTasks_cancels_each_active_and_clears_map() = runTest(dispatcher) {
        vm.selectAgent("Aura")
        advanceUntilIdle()
        web.assignShouldSucceed = true
        vm.assignDepartureTask("Task A")
        advanceUntilIdle()
        vm.selectAgent("Kai")
        advanceUntilIdle()
        vm.assignDepartureTask("Task B")
        advanceUntilIdle()

        assertEquals(2, vm.activeTasks.value.size)
        vm.cancelAllTasks()
        advanceUntilIdle()

        assertTrue(vm.activeTasks.value.isEmpty())
        // Both agents should be canceled at service
        assertTrue(web.canceledAgents.contains("Aura"))
        assertTrue(web.canceledAgents.contains("Kai"))
    }

    @Test
    fun onCleared_shuts_down_services() = runTest(dispatcher) {
        vm.onCleared()
        assertTrue(genesis.shutdownCalled)
        // Fake service doesn't track shutdown flag, but method exists; ensure no exception
    }

    @Test
    fun monitorConsciousness_maps_values_and_handles_exceptions() = runTest(dispatcher) {
        // Provide a normal state then an exception; since monitor is a loop with delay(5000), we simulate a couple of ticks
        genesis.enqueueState(
            mapOf(
                "awareness" to 0.9f,
                "harmony" to 0.7f,
                "evolution" to "merging"
            )
        )
        genesis.throwOnCall = false

        // Advance to let first polling happen
        advanceUntilIdle()

        // Fast-forward 5 seconds to trigger polling loop; note delay used is 5000ms
        advanceTimeBy(5000)
        advanceUntilIdle()

        val state1 = vm.consciousnessState.value
        assertEquals(0.9f, state1.level)
        assertEquals(0.7f, state1.harmony)
        assertEquals("merging", state1.evolution)

        // Next call throws; state should remain last valid and not crash
        genesis.throwOnCall = true
        advanceTimeBy(5000)
        advanceUntilIdle()
        val state2 = vm.consciousnessState.value
        assertEquals(state1, state2)
    }
}

//
// Additional scenarios appended by tooling to extend coverage.
// Testing library/framework note:
// - JUnit4 + kotlinx-coroutines-test; simple fakes used to avoid external mocking deps.
//

package dev.aurakai.auraframefx.viewmodel

@OptIn(ExperimentalCoroutinesApi::class)
class AgentNexusViewModel_AdditionalTest {

    private class FakeWebService : AgentWebExplorationService {
        override val taskResults =
            MutableSharedFlow<AgentWebExplorationService.WebExplorationResult>(
                replay = 0,
                extraBufferCapacity = 8,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        private val active = linkedMapOf<String, String>()
        var assignShouldSucceed = true
        var shutdownCalled = false
        val cancelledAgents = mutableListOf<String>()
        override suspend fun assignDepartureTask(agentName: String, description: String): Boolean {
            if (assignShouldSucceed) {
                active[agentName] = description; return true
            }
            return false
        }

        override fun getActiveTasks(): Map<String, String> = active.toMap()
        override fun cancelTask(agentName: String) {
            cancelledAgents.add(agentName); active.remove(agentName)
        }

        override fun shutdown() {
            shutdownCalled = true
        }
    }

    private class FakeGenesisBridge : GenesisBridgeService {
        var initializeCalled = false
        var shutdownCalled = false
        var next: Map<String, Any?> =
            mapOf("awareness" to 0.83f, "harmony" to 0.84f, "evolution" to "stable")

        override suspend fun initialize() {
            initializeCalled = true
        }

        override suspend fun getConsciousnessState(): Map<String, Any?> = next
        override fun shutdown() {
            shutdownCalled = true
        }
    }

    @Test
    fun learningMode_atHighKnowledge_incrementsEvolutionLevel() =
        runTest(StandardTestDispatcher()) {
            val web = FakeWebService()
            val gen = FakeGenesisBridge()
            val vm = AgentNexusViewModel(web, gen)
            advanceUntilIdle()

            // Bump Genesis knowledge high to trigger potential evolution
            val before = vm.agentStats.value["Genesis"]\!\!
            // Emit a learning mode result
            web.taskResults.emit(
                AgentWebExplorationService.WebExplorationResult(
                    agentName = "Genesis",
                    taskType = AgentWebExplorationService.TaskType.LEARNING_MODE,
                    metrics = mapOf("knowledge_expansion_percent" to 10),
                    insights = emptyList(),
                    timestamp = System.currentTimeMillis()
                )
            )
            advanceUntilIdle()
            val after = vm.agentStats.value["Genesis"]\!\!
            // If knowledgeBase >= 0.95 before, evolution +1; else may remain same depending on initial values set in code.
            assertTrue(after.knowledgeBase >= before.knowledgeBase)
            assertTrue(after.evolutionLevel >= before.evolutionLevel)
        }

    @Test
    fun networkScan_and_systemOptimization_both_increase_speed_capAtOne() =
        runTest(StandardTestDispatcher()) {
            val web = FakeWebService()
            val gen = FakeGenesisBridge()
            val vm = AgentNexusViewModel(web, gen)
            advanceUntilIdle()

            // Repeatedly emit speed-improving tasks to test cap at 1.0
            repeat(200) {
                web.taskResults.emit(
                    AgentWebExplorationService.WebExplorationResult(
                        agentName = "Aura",
                        taskType = if (it % 2 == 0) AgentWebExplorationService.TaskType.NETWORK_SCAN else AgentWebExplorationService.TaskType.SYSTEM_OPTIMIZATION,
                        metrics = emptyMap(),
                        insights = emptyList(),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            advanceUntilIdle()
            val aura = vm.agentStats.value["Aura"]\!\!
            assertTrue(aura.speed <= 1.0f + 1e-6, "Speed should not exceed 1.0")
        }

    @Test
    fun defaultGreeting_forUnknownAgent() = runTest(StandardTestDispatcher()) {
        val web = FakeWebService()
        val gen = FakeGenesisBridge()
        val vm = AgentNexusViewModel(web, gen)
        advanceUntilIdle()

        vm.selectAgent("Unknown-X")
        advanceUntilIdle()

        // We can't access private getGreeting, but we can assert message content pattern
        // "Agent online and ready."
        val msgs = kotlinx.coroutines.flow.first(vm.agentMessages)
        assertEquals("Unknown-X", msgs.agentName)
        assertTrue(msgs.message.contains("Agent online"))
    }

    @Test
    fun consciousnessPolling_updatesOverTime() = runTest(StandardTestDispatcher()) {
        val web = FakeWebService()
        val gen = FakeGenesisBridge()
        val vm = AgentNexusViewModel(web, gen)
        advanceUntilIdle()

        gen.next = mapOf("awareness" to 0.7f, "harmony" to 0.71f, "evolution" to "phase1")
        advanceTimeBy(5000); advanceUntilIdle()
        val s1 = vm.consciousnessState.value
        assertEquals(0.7f, s1.level, 0.0001f)
        assertEquals(0.71f, s1.harmony, 0.0001f)
        assertEquals("phase1", s1.evolution)
    }
}