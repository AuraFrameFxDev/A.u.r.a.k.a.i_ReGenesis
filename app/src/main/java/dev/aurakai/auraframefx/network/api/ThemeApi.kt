package dev.aurakai.auraframefx.network.api

import dev.aurakai.auraframefx.network.model.Theme
import retrofit2.Response
import retrofit2.http.*

/**
 * API interface for theme-related operations.
 */
interface ThemeApi {
    /**
     * Get all available themes.
     *
     * @return A list of available themes.
     */
    @GET("themes")
    suspend fun getThemes(): List<Theme>

    /**
     * Apply a theme to the current user's session.
     *
     * @param themeId The ID of the theme to apply.
     * @return The applied theme.
     */
    @POST("themes/{themeId}/apply")
    suspend fun applyTheme(@Path("themeId") themeId: String): Theme

    /**
     * Get the currently active theme.
     *
     * @return The currently active theme.
     */
    @GET("themes/active")
    suspend fun getActiveTheme(): Theme
}
