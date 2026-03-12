package dev.aurakai.auraframefx.domains.aura.repository

import dev.aurakai.auraframefx.domains.aura.UIDesign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollaborativeWorkspaceRepository @Inject constructor() {

    private val _designs = MutableStateFlow<List<UIDesign>>(
        listOf(
            UIDesign("1", "Neon Glass Card", "Standard glassmorphism template", "Aura", "{}"),
            UIDesign("2", "Sentient Toggle", "Physics-based interaction module", "Kai", "{}")
        )
    )
    val designs: StateFlow<List<UIDesign>> = _designs.asStateFlow()

    fun saveDesign(design: UIDesign) {
        val currentList = _designs.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == design.id }
        if (index != -1) {
            currentList[index] = design
        } else {
            currentList.add(design)
        }
        _designs.value = currentList
        Timber.d("Design saved: ${design.name}")
    }

    fun deleteDesign(id: String) {
        _designs.value = _designs.value.filter { it.id != id }
    }

    fun exportToJson(design: UIDesign): String {
        return Json.encodeToString(design)
    }

    fun importFromJson(json: String): UIDesign? {
        return try {
            Json.decodeFromString<UIDesign>(json)
        } catch (e: Exception) {
            Timber.e(e, "Failed to import UI Design")
            null
        }
    }
}
