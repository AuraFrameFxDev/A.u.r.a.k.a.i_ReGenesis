package dev.aurakai.auraframefx.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dev.aurakai.auraframefx.ui.theme.service.ThemeService

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeService: ThemeService,
) : ViewModel() {

    private val _theme = MutableStateFlow(Theme.DARK)
    val theme: StateFlow<Theme> = _theme

    private val _color = MutableStateFlow(Color.BLUE)
    val color: StateFlow<Color> = _color

    fun processThemeCommand(command: String) {
        viewModelScope.launch {
            when (val themeCommand = themeService.parseThemeCommand(command)) {
                is ThemeCommand.SetTheme -> _theme.value = themeCommand.theme
                is ThemeCommand.SetColor -> _color.value = themeCommand.color
                ThemeCommand.Unknown -> { /* Do nothing */
                }
            }
        }
    }
}
