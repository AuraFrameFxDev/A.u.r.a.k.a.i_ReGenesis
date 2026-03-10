package dev.aurakai.auraframefx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.navigation.AppNavGraph
import dev.aurakai.auraframefx.ui.components.BottomNavigationBar
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.ui.theme.ThemeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuraFrameFXTheme {
                val themeViewModel: ThemeViewModel = hiltViewModel()
                MainScreen(themeViewModel = themeViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Perform any cleanup here if needed
    }
}

// New: a preview-friendly content composable that accepts a lambda for theme commands
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreenContent(
    processThemeCommand: (String) -> Unit
) {
    val navController = rememberNavController()

    var showDigitalEffects by remember { mutableStateOf(true) }
    var command by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row {
                TextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Enter theme command") }
                )
                Button(onClick = { processThemeCommand(command) }) {
                    Text("Apply")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .let { base ->
                        if (showDigitalEffects) {
                            base.digitalPixelEffect()
                        } else {
                            base
                        }
                    }
            ) {
                AppNavGraph(navController = navController)
            }
        }
    }
}

// Keep original API used by Activity: delegate to the content with the real ViewModel
@Composable
internal fun MainScreen(
    themeViewModel: ThemeViewModel
) {
    MainScreenContent(processThemeCommand = { themeViewModel.processThemeCommand(it) })
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AuraFrameFXTheme {
        // For preview, use a no-op lambda for the theme command handler
        MainScreenContent(processThemeCommand = { /* no-op in preview */ })
    }
}

// Extension function placeholder - this should be implemented elsewhere
fun Modifier.digitalPixelEffect(): Modifier = this
