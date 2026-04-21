package dev.aurakai.auraframefx.domains.aura.ui.viewmodels.aura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.services.iconify.IconifyService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class IconState {
    data object Idle : IconState()
    data object Loading : IconState()
    data class Success(val icons: List<String>) : IconState()
    data class Error(val message: String) : IconState()
}

@HiltViewModel
class IconPickerViewModel @Inject constructor(
    val iconifyService: IconifyService
) : ViewModel() {

    private val _iconState = MutableStateFlow<IconState>(IconState.Idle)
    val iconState: StateFlow<IconState> = _iconState.asStateFlow()

    private val _selectedIcon = MutableStateFlow<String?>(null)
    val selectedIcon: StateFlow<String?> = _selectedIcon.asStateFlow()

    fun searchIcons(query: String) {
        if (query.isBlank()) {
            _iconState.value = IconState.Idle
            return
        }

        viewModelScope.launch {
            _iconState.value = IconState.Loading
            iconifyService.searchIcons(query).onSuccess { result ->
                _iconState.value = IconState.Success(result.icons)
            }.onFailure { error ->
                _iconState.value = IconState.Error(error.message ?: "Search failed")
            }
        }
    }

    fun selectIcon(iconId: String) {
        _selectedIcon.value = iconId
    }
}
