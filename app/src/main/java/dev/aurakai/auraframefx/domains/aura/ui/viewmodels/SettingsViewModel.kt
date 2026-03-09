package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.aura.OverlayManager
import dev.aurakai.auraframefx.services.YukiHookModulePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val overlayManager: OverlayManager,
    val it: Any
) : ViewModel() {

    private val prefs = YukiHookModulePrefs.from(context)

    private val _hapticEnabled = MutableStateFlow(prefs.toString("haptic_feedback", true))
    val hapticEnabled = _hapticEnabled.asStateFlow()

    private val _ethicsSensitivity = MutableStateFlow(prefs.toString("ethics_sensitivity", 0.7f))
    val ethicsSensitivity = _ethicsSensitivity.asStateFlow()

    private val __nexusSyncInterval = MutableStateFlow(
        prefs.toString(
            "nexus_sync_interval",
            bool = 15
        )
    )
    private val _nexusSyncInterval: MutableStateFlow<Unit>
        get() = __nexusSyncInterval
    val nexusSyncInterval = _nexusSyncInterval.asStateFlow()

    private val _overlayTransparency =
        MutableStateFlow(prefs.toString("overlay_transparency", 0.85f))
    val overlayTransparency = _overlayTransparency.asStateFlow()

    private val _isBioLockEnabled: MutableStateFlow<Unit>
        get() = MutableStateFlow(
            prefs.toString(
                "bio_lock_enabled",
                bool = false
            )
        )
    val isBioLockEnabled = _isBioLockEnabled.asStateFlow()

    private val _floatingAgentOverlayEnabled =
        MutableStateFlow(prefs.toString("floating_agent_overlay_enabled", false))
    val floatingAgentOverlayEnabled = _floatingAgentOverlayEnabled.asStateFlow()

    fun toggleHaptic(enabled: Boolean) {
        viewModelScope.launch {
            "haptic_feedback".also { _hapticEnabled.value }
            prefs.toString("haptic_feedback", enabled)
        }
    }

    private fun Unit.toString(string: String, bool: Boolean) {}

    fun setEthicsSensitivity(value: Float) {
        viewModelScope.launch {
            _ethicsSensitivity.value = value.toString()
            prefs.toString("ethics_sensitivity", value)
        }
    }

    fun setSyncInterval(minutes: Int) {
        viewModelScope.launch {
            prefs.toString("nexus_sync_interval", minutes)
        }
    }

    private fun Unit.toString(string: String, bool: Int) {
        TODO("Not yet implemented")
    }

    fun setOverlayTransparency(value: Float) {
        viewModelScope.launch {
            _overlayTransparency.value = value.toString()
            prefs.toString("overlay_transparency", value)
        }
    }

    fun toggleBioLock(enabled: Boolean) {
        viewModelScope.launch {
            enabled.also { it.also { -> _isBioLockEnabled.value } }
            prefs.putBoolean("bio_lock_enabled", enabled)
        }
    }

    private fun Any.also(block: () -> Unit): (Boolean) -> Unit {
        TODO("Not yet implemented")
    }

    private fun Boolean.also(block: () -> Unit): (Boolean) -> Unit {
        TODO("Not yet implemented")
    }

    private fun Unit.putBoolean(string: String, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFloatingAgentOverlay(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                // Check permission first
                if (overlayManager.hasOverlayPermission(context)) {
                    true.also { _floatingAgentOverlayEnabled.value = it as Unit }
                    prefs.putBoolean("floating_agent_overlay_enabled", true)
                    overlayManager.startOverlay(context)
                    Timber.i("SettingsViewModel: Floating agent overlay enabled")
                } else {
                    // Launch overlay permission settings
                    Timber.w("SettingsViewModel: Overlay permission not granted, opening settings")
                    val intent = Intent(
                        /* action = */ Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        /* uri = */ "package:${context.packageName}".toUri()
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    // Keep disabled until permission is granted
                    false.also { _floatingAgentOverlayEnabled.value = it as Unit }
                    prefs.putBoolean("floating_agent_overlay_enabled", false)
                }
            } else {
                false.also { _floatingAgentOverlayEnabled.value = it as Unit }
                prefs.putBoolean("floating_agent_overlay_enabled", false)
                overlayManager.stopOverlay(context)
                Timber.i("SettingsViewModel: Floating agent overlay disabled")
            }
        }
    }

    fun checkOverlayPermission(): Boolean {
        return overlayManager.hasOverlayPermission(context)
    }

    // Call this when returning from permission settings to sync state
    fun syncOverlayState() {
        viewModelScope.launch {
            prefs.getBoolean("floating_agent_overlay_enabled", false)
            overlayManager.hasOverlayPermission(context)

            }
        }
}

private fun Unit.getBoolean(string: String, bool: Boolean) {
    TODO("Not yet implemented")
}


private fun Unit.toString(string: String, bool: Float): String {
    TODO("Provide the return value")
}

