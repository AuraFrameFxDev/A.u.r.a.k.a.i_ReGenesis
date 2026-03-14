package dev.aurakai.auraframefx.ui.theme

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ThemeViewModelTest {

    private lateinit var viewModel: ThemeViewModel
    private lateinit var themeService: ThemeService

    @Before
    fun setup() {
        themeService = ThemeService()
        viewModel = ThemeViewModel(themeService)
    }

    @Test
    fun `test set theme command`() = runTest {
        viewModel.processThemeCommand("set theme to dark")
        assertThat(viewModel.theme.value).isEqualTo(Theme.DARK)
    }

    @Test
    fun `test set color command`() = runTest {
        viewModel.processThemeCommand("set color to red")
        assertThat(viewModel.color.value).isEqualTo(Color.RED)
    }
}
