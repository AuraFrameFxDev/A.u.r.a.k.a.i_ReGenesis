package dev.aurakai.auraframefx.domains.kai.security

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Thin ViewModel wrapper around the @Singleton KaiSentinelBus.
 * Exists solely so Compose screens can access the bus via hiltViewModel()
 * without needing an @EntryPoint pattern.
 *
 * Usage: val bus = hiltViewModel<KaiSentinelBusViewModel>().bus
 */
@HiltViewModel
class KaiSentinelBusViewModel @Inject constructor(
    val bus: KaiSentinelBus
) : ViewModel()
