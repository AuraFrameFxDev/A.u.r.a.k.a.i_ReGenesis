package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class IconState {
    object Idle : IconState()
    object Loading : IconState()
    data class Success(val icons: List<String>, val collections: Map<String, IconCollection>) :
        IconState()

    data class Error(val message: String) : IconState()
}

@HiltViewModel
class IconPickerViewModel @Inject constructor(
    val iconifyService: IconifyService,
    private val iconCacheManager: IconCacheManager
) : ViewModel() {

    private val _iconState = MutableStateFlow<IconState>(IconState.Idle)
    val iconState: StateFlow<IconState> = _iconState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            IconState.Idle
        )

    private val _selectedIcon = MutableStateFlow<String?>(null)
    val selectedIcon: StateFlow<String?> = _selectedIcon.asStateFlow()

    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            _iconState.value = IconState.Loading
            try {
                iconifyService.getCollections()
                    .onSuccess { collections ->
                        Timber.d("IconPickerViewModel: Loaded ${collections.size} collections")
                        _iconState.value = IconState.Success(emptyList(), collections)
                    }
                    .onFailure { error ->
                        Timber.e(error, "IconPickerViewModel: Failed to load collections")
                        _iconState.value = IconState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: Exception) {
                Timber.e(e, "IconPickerViewModel: Exception loading collections")
                _iconState.value = IconState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun searchIcons(query: String) {
        if (query.isBlank()) {
            loadCollections()
            return
        }

        viewModelScope.launch {
            _iconState.value = IconState.Loading
            try {
                iconifyService.searchIcons(query, limit = 100)
                    .onSuccess { result ->
                        val currentState = _iconState.value as? IconState.Success
                        _iconState.value = IconState.Success(
                            result.icons,
                            currentState?.collections ?: emptyMap()
                        )
                    }
                    .onFailure { error ->
                        _iconState.value = IconState.Error(error.message ?: "Search failed")
                    }
            } catch (e: Exception) {
                _iconState.value = IconState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun selectIcon(iconId: String) {
        _selectedIcon.value = iconId
    }

    fun clearCache() {
        viewModelScope.launch {
            iconCacheManager.clearCache()
            Timber.d("IconPickerViewModel: Icon cache cleared")
        }
    }

    fun getCacheStats(): Flow<IconCacheManager.CacheStats> = flow {
        emit(iconCacheManager.getCacheStats())
    }
}
