package dev.aurakai.auraframefx.agent

import android.util.Log

private const val TAG = "BaseAgent"

abstract class BaseAgent {

    companion object {
        @Volatile
        var isOrchestratorInitialized: Boolean = false
            set(value) {
                if (field != value) {
                    Log.d(TAG, "isOrchestratorInitialized changed from $field to $value")
                    field = value
                }
            }
    }

    // Other common agent functionalities can be added here
}
