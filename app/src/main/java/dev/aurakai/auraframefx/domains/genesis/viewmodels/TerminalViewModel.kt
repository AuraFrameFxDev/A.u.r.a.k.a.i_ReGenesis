package dev.aurakai.auraframefx.domains.genesis.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.cascade.CascadeAIService
import dev.aurakai.auraframefx.domains.cascade.grok.AuraDifyBridge
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    val pythonManager: dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager,
    val cascadeService: CascadeAIService,
    val casberrySwarm: CasberryParticleSwarm,
    val auraDifyBridge: AuraDifyBridge
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
