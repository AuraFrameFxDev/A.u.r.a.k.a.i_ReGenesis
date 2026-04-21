package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.RootShellService
import javax.inject.Inject

@HiltViewModel
class RootToolsViewModel @Inject constructor(
    val rootShellService: RootShellService
) : ViewModel()
