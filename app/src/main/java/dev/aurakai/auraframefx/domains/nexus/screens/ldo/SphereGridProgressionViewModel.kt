package dev.aurakai.auraframefx.domains.nexus.screens.ldo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── v2.1 Sphere Grid Progression ViewModel ──────────────────────────────────
//
// Manages the 10-node progression grid state.
// When all 10 nodes are filled an ability is auto-generated, the grid resets,
// and the cycle counter increments. Generated abilities accumulate in the
// ability list that the UI renders as a dismissable dropdown.

data class SphereGridProgressionState(
    val nodes: List<ProgressionNode> = List(10) { ProgressionNode(index = it) },
    val filledCount: Int = 0,
    val cycle: GridCycle = GridCycle(),
    val isResetting: Boolean = false,
    val justGeneratedAbility: GeneratedAbility? = null,   // transient, cleared after display
    val generatedAbilities: List<GeneratedAbility> = emptyList(),
    val selectedAbilityId: String? = null,                // for detail panel
    val activePairingBonus: PairingBonus? = null,         // from latest task deployment pair
    val lastPairedAgents: Pair<String, String>? = null,
)

@HiltViewModel
class SphereGridProgressionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SphereGridProgressionState())
    val state: StateFlow<SphereGridProgressionState> = _state.asStateFlow()

    // ── Node filling ──────────────────────────────────────────────────────────

    /**
     * Fill the next empty node in sequence (call after a task completes).
     * Triggers a FILLING animation state, then transitions to FILLED.
     */
    fun fillNextNode() {
        val current = _state.value
        if (current.isResetting) return

        val nextEmptyIndex = current.nodes.indexOfFirst { it.state == NodeState.EMPTY }
        if (nextEmptyIndex == -1) return // all filled — should not happen (reset handles this)

        // Begin fill animation
        _state.update { s ->
            s.copy(
                nodes = s.nodes.mapIndexed { i, node ->
                    if (i == nextEmptyIndex) node.copy(state = NodeState.FILLING, fillProgress = 0f)
                    else node
                }
            )
        }

        viewModelScope.launch {
            // Animate fill progress 0→1
            for (step in 1..10) {
                delay(40L)
                _state.update { s ->
                    s.copy(
                        nodes = s.nodes.mapIndexed { i, node ->
                            if (i == nextEmptyIndex) node.copy(fillProgress = step / 10f)
                            else node
                        }
                    )
                }
            }
            // Confirm filled
            _state.update { s ->
                val updatedNodes = s.nodes.mapIndexed { i, node ->
                    if (i == nextEmptyIndex) node.copy(state = NodeState.FILLED, fillProgress = 1f)
                    else node
                }
                val newCount = updatedNodes.count { it.state == NodeState.FILLED }
                s.copy(nodes = updatedNodes, filledCount = newCount)
            }

            // Check if grid is complete
            if (_state.value.filledCount >= 10) {
                triggerGridReset()
            }
        }
    }

    /**
     * Directly fill a specific node by index (e.g. from manual tap or test).
     */
    fun fillNode(index: Int) {
        val current = _state.value
        if (current.isResetting) return
        val node = current.nodes.getOrNull(index) ?: return
        if (node.state != NodeState.EMPTY) return

        viewModelScope.launch {
            _state.update { s ->
                s.copy(
                    nodes = s.nodes.mapIndexed { i, n ->
                        if (i == index) n.copy(state = NodeState.FILLING, fillProgress = 0f) else n
                    }
                )
            }
            for (step in 1..10) {
                delay(40L)
                _state.update { s ->
                    s.copy(
                        nodes = s.nodes.mapIndexed { i, n ->
                            if (i == index) n.copy(fillProgress = step / 10f) else n
                        }
                    )
                }
            }
            _state.update { s ->
                val updatedNodes = s.nodes.mapIndexed { i, n ->
                    if (i == index) n.copy(state = NodeState.FILLED, fillProgress = 1f) else n
                }
                val newCount = updatedNodes.count { it.state == NodeState.FILLED }
                s.copy(nodes = updatedNodes, filledCount = newCount)
            }
            if (_state.value.filledCount >= 10) {
                triggerGridReset()
            }
        }
    }

    // ── Grid reset + ability generation ──────────────────────────────────────

    private suspend fun triggerGridReset() {
        val currentCycle = _state.value.cycle

        // Generate the ability before resetting
        val nodeSeed = System.currentTimeMillis().toInt()
        val newAbility = generateAbility(
            cycleNumber = currentCycle.cycleNumber,
            nodeSeed = nodeSeed,
        )

        // Mark all nodes as RESETTING
        _state.update { s ->
            s.copy(
                isResetting = true,
                justGeneratedAbility = newAbility,
            )
        }

        // Ripple-reset animation — stagger each node clearing
        for (i in 0 until 10) {
            delay(60L)
            _state.update { s ->
                s.copy(
                    nodes = s.nodes.mapIndexed { idx, node ->
                        if (idx == i) node.copy(state = NodeState.RESETTING, fillProgress = 0f) else node
                    }
                )
            }
        }

        delay(300L)

        // All nodes back to EMPTY, cycle increments, ability added to list
        _state.update { s ->
            s.copy(
                nodes = List(10) { ProgressionNode(index = it) },
                filledCount = 0,
                isResetting = false,
                cycle = GridCycle(
                    cycleNumber = currentCycle.cycleNumber + 1,
                    resetTimestamp = System.currentTimeMillis(),
                    abilitiesEarned = currentCycle.abilitiesEarned + 1,
                ),
                generatedAbilities = listOf(newAbility) + s.generatedAbilities,
            )
        }
    }

    // ── Ability list interactions ─────────────────────────────────────────────

    /** Select an ability to show its detail panel */
    fun selectAbility(abilityId: String?) {
        _state.update { it.copy(selectedAbilityId = abilityId) }
    }

    /** Acknowledge / dismiss the "new" badge on an ability */
    fun acknowledgeAbility(abilityId: String) {
        _state.update { s ->
            s.copy(
                justGeneratedAbility = if (s.justGeneratedAbility?.id == abilityId) null
                    else s.justGeneratedAbility,
                generatedAbilities = s.generatedAbilities.map { ability ->
                    if (ability.id == abilityId) ability.copy(isNew = false) else ability
                },
            )
        }
    }

    // ── Pairing bonus ─────────────────────────────────────────────────────────

    /**
     * Called when a task is dispatched with two specific agents paired.
     * Looks up the pairing bonus and stores it for UI display.
     */
    fun onAgentsPaired(agentA: String, agentB: String) {
        val bonus = getPairingBonus(agentA, agentB)
        _state.update { s ->
            s.copy(
                activePairingBonus = bonus,
                lastPairedAgents = agentA to agentB,
            )
        }

        // Auto-fill one node when a valid pairing bonus triggers
        if (bonus != null) {
            viewModelScope.launch {
                delay(200L)
                fillNextNode()
            }
        }
    }

    /** Clear the active pairing bonus display after the user dismisses it */
    fun clearPairingBonus() {
        _state.update { it.copy(activePairingBonus = null) }
    }

    // ── Demo / debug helpers ──────────────────────────────────────────────────

    /** Fill all 10 nodes at once (debug / preview use only) */
    fun debugFillAll() {
        viewModelScope.launch {
            repeat(10) {
                fillNextNode()
                delay(80L)
            }
        }
    }

    /** Reset grid without generating an ability (debug use) */
    fun debugReset() {
        _state.update { s ->
            s.copy(
                nodes = List(10) { ProgressionNode(index = it) },
                filledCount = 0,
                isResetting = false,
            )
        }
    }
}