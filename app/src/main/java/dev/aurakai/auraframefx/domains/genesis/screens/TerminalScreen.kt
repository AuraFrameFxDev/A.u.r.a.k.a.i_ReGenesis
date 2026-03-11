package dev.aurakai.auraframefx.aura.ui

import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen

/**
 * Entry point for the "terminal_screen" nav route.
 * Delegates to the fully-featured SentientShellScreen.
 */
@Composable
fun TerminalScreen() {
    SentientShellScreen()
}
