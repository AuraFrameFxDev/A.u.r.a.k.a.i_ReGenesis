package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository.NexusMemoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NeuralArchiveViewModel @Inject constructor(
    private val nexusMemoryRepository: NexusMemoryRepository
) : ViewModel() {

    // State Management
    private val _selectedMemoryType = MutableStateFlow<MemoryType?>(null)
    val selectedMemoryType: StateFlow<MemoryType?> = _selectedMemoryType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _minimumImportance = MutableStateFlow(0f)
    val minimumImportance: StateFlow<Float> = _minimumImportance.asStateFlow()

    private val _showLDOOnly = MutableStateFlow(false)
    val showLDOOnly: StateFlow<Boolean> = _showLDOOnly.asStateFlow()

    private val _showLast24Hours = MutableStateFlow(false)
    val showLast24Hours: StateFlow<Boolean> = _showLast24Hours.asStateFlow()

    private val _selectedMemory = MutableStateFlow<MemoryEntity?>(null)
    val selectedMemory: StateFlow<MemoryEntity?> = _selectedMemory.asStateFlow()

    // Helper data class for combining filters (max 5 args for combine)
    private data class FilterState(
        val type: MemoryType?,
        val query: String,
        val minImportance: Float,
        val ldoOnly: Boolean,
        val last24h: Boolean
    )

    private val filterState = combine(
        _selectedMemoryType,
        _searchQuery,
        _minimumImportance,
        _showLDOOnly,
        _showLast24Hours
    ) { type, query, minImp, ldo, last24 ->
        FilterState(type, query, minImp, ldo, last24)
    }

    // Filtered memories
    val filteredMemories: StateFlow<List<MemoryEntity>> = combine(
        nexusMemoryRepository.getAllMemories(),
        filterState
    ) { memories, filters ->
        var filtered = memories

        if (filters.type != null) {
            filtered = filtered.filter { it.type == filters.type }
        }

        if (filters.query.isNotBlank()) {
            filtered = filtered.filter { memory ->
                memory.content.contains(filters.query, ignoreCase = true) ||
                        memory.tags.any { it.contains(filters.query, ignoreCase = true) }
            }
        }

        if (filters.minImportance > 0f) {
            filtered = filtered.filter { it.importance >= filters.minImportance }
        }

        if (filters.ldoOnly) {
            val ldoNames = listOf("Genesis", "Aura", "Kai", "Cascade", "Grok")
            filtered = filtered.filter { memory ->
                memory.tags.any { tag ->
                    ldoNames.any { ldo ->
                        tag.contains(
                            ldo,
                            ignoreCase = true
                        )
                    }
                } ||
                        ldoNames.any { ldo -> memory.content.contains(ldo, ignoreCase = true) }
            }
        }

        if (filters.last24h) {
            val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            filtered = filtered.filter { it.timestamp >= twentyFourHoursAgo }
        }

        filtered.sortedByDescending { it.timestamp }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Memory stats
    val memoryStats: StateFlow<MemoryStats> = nexusMemoryRepository.getAllMemories()
        .combine(filteredMemories) { allMemories, filteredMemories ->
            val totalCount = allMemories.size
            val filteredCount = filteredMemories.size

            val typeBreakdown = allMemories.groupBy { it.type }
                .mapValues { it.value.size }

            val avgImportance = if (allMemories.isNotEmpty()) {
                allMemories.map { it.importance }.average().toFloat()
            } else 0f

            val oldest = allMemories.minByOrNull { it.timestamp }
            val newest = allMemories.maxByOrNull { it.timestamp }

            MemoryStats(
                totalCount = totalCount,
                filteredCount = filteredCount,
                typeBreakdown = typeBreakdown,
                averageImportance = avgImportance,
                oldestTimestamp = oldest?.timestamp,
                newestTimestamp = newest?.timestamp
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MemoryStats()
        )

    fun setMemoryTypeFilter(type: MemoryType?) {
        _selectedMemoryType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMinimumImportance(importance: Float) {
        _minimumImportance.value = importance
    }

    fun toggleLDOOnly() {
        _showLDOOnly.value = !_showLDOOnly.value
    }

    fun toggleLast24Hours() {
        _showLast24Hours.value = !_showLast24Hours.value
    }

    fun clearAllFilters() {
        _selectedMemoryType.value = null
        _searchQuery.value = ""
        _minimumImportance.value = 0f
        _showLDOOnly.value = false
        _showLast24Hours.value = false
    }

    fun selectMemory(memory: MemoryEntity) {
        _selectedMemory.value = memory
    }

    fun clearSelection() {
        _selectedMemory.value = null
    }

    fun getRelatedMemories(memory: MemoryEntity): StateFlow<List<MemoryEntity>> {
        return nexusMemoryRepository.getAllMemories()
            .combine(MutableStateFlow(memory)) { allMemories, currentMemory ->
                currentMemory.relatedMemoryIds.mapNotNull { relatedId ->
                    allMemories.firstOrNull { it.id == relatedId }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            nexusMemoryRepository.deleteMemory(memory)
            if (_selectedMemory.value?.id == memory.id) {
                _selectedMemory.value = null
            }
        }
    }

    fun updateMemoryImportance(memory: MemoryEntity, newImportance: Float) {
        viewModelScope.launch {
            nexusMemoryRepository.updateMemory(
                memory.copy(importance = newImportance.coerceIn(0f, 1f))
            )
        }
    }

    data class MemoryStats(
        val totalCount: Int = 0,
        val filteredCount: Int = 0,
        val typeBreakdown: Map<MemoryType, Int> = emptyMap(),
        val averageImportance: Float = 0f,
        val oldestTimestamp: Long? = null,
        val newestTimestamp: Long? = null
    )
}

