package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChromaCoreViewModel @Inject constructor(
    private val vertexAIClient: VertexAIClient
) : ViewModel() {

    private val _suggestedColors = MutableStateFlow<List<Color>>(emptyList())
    val suggestedColors: StateFlow<List<Color>> = _suggestedColors

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    fun generatePalette(themeDescription: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val prompt = """
                    Generate a HEX color palette (5 colors) for an Android theme with the following description: $themeDescription.
                    Return ONLY the HEX codes separated by commas. Example: #FF0000, #00FF00, #0000FF, #FFFF00, #00FFFF
                """.trimIndent()
                
                val response = vertexAIClient.generateText(prompt)
                if (response != null) {
                    val colors = response.split(",")
                        .mapNotNull { it.trim().toColor() }
                    _suggestedColors.value = colors
                }
            } catch (e: Exception) {
                // Log or handle error
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun String.toColor(): Color? {
        return try {
            Color(android.graphics.Color.parseColor(this))
        } catch (e: Exception) {
            null
        }
    }
}
