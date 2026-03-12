package dev.aurakai.auraframefx.network.api

import dev.aurakai.auraframefx.network.model.User
import retrofit2.Response
import retrofit2.http.GET

/**
 * API interface for user-related operations.
 */
interface UserApi {
    /**
     * Get the current user's information.
     *
     * @return The current user's information.
     */
    @GET("user")
    suspend fun getCurrentUser(): User

    // Add more user-related endpoints as needed
}
