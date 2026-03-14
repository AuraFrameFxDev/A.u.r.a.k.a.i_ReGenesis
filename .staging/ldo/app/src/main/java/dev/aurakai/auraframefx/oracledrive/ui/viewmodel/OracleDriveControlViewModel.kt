package dev.aurakai.auraframefx.oracledrive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OracleDriveControlViewModel @Inject constructor() : ViewModel() {

    private val _moduleStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val moduleStatus: StateFlow<Map<String, Boolean>> = _moduleStatus.asStateFlow()

    fun toggleModule(moduleName: String) {
        viewModelScope.launch {
            val currentStatus = _moduleStatus.value.toMutableMap()
            currentStatus[moduleName] = !(currentStatus[moduleName] ?: false)
            _moduleStatus.value = currentStatus
        }
    }
}