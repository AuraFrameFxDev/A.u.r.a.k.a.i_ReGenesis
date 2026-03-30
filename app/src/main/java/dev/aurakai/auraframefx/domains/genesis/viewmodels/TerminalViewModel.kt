package dev.aurakai.auraframefx.domains.genesis.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    val pythonManager: PythonProcessManager
) : ViewModel() {

    fun startPython() {
        pythonManager.start()
    }

    fun stopPython() {
        pythonManager.stop()
    }

    fun restartPython() {
        pythonManager.restart()
    }

    fun getPythonStatus(): String {
        return pythonManager.healthState.value.name
    }
}
