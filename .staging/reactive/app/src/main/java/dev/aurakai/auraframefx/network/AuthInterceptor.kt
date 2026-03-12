package dev.aurakai.auraframefx.network

import dev.aurakai.auraframefx.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intercepts network requests to add authentication tokens and handle token refresh.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip authentication for login/refresh endpoints
        if (isAuthRequest(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        // Add token to the request
        val token = tokenManager.accessToken
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        var request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Execute the request
        val response = chain.proceed(request)

        // If unauthorized, try to refresh the token and retry the request
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            response.close()

            val newToken = runBlocking {
                try {
                    tokenManager.refreshToken?.let { refreshToken ->
                        val refreshResponse = authApi.refreshToken(
                            RefreshTokenRequest(refreshToken = refreshToken)
                        )

                        if (refreshResponse.isSuccessful) {
                            val newAccessToken = refreshResponse.body()?.accessToken
                            val newRefreshToken = refreshResponse.body()?.refreshToken
                            val expiresIn = refreshResponse.body()?.expiresIn ?: 3600L

                            if (!newAccessToken.isNullOrBlank() && !newRefreshToken.isNullOrBlank()) {
                                tokenManager.updateTokens(
                                    accessToken = newAccessToken,
                                    refreshToken = newRefreshToken,
                                    expiresInSeconds = expiresIn
                                )
                                return@runBlocking newAccessToken
                            }
                        } else {
                            // If refresh fails, clear tokens and redirect to login
                            tokenManager.clearTokens()
                            // TODO: Notify UI about session expiration
                        }
                    }
                    null
                } catch (e: Exception) {
                    Timber.e(e, "Failed to refresh token")
                    null
                }
            }

            // Retry the original request with the new token if refresh was successful
            newToken?.let {
                request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
                return chain.proceed(request)
            } ?: return response // Return the original 401 if refresh failed
        }

        return response
    }

    private fun isAuthRequest(request: Request): Boolean {
        val path = request.url.encodedPath
        return path.endsWith("/auth/login") ||
                path.endsWith("/auth/refresh") ||
                path.endsWith("/auth/register")
    }

    private fun Response.createErrorResponse(code: Int, message: String): Response {
        val json = JSONObject().apply {
            put("success", false)
            put("message", message)
            put("code", code)
        }.toString()

        return newBuilder()
            .code(code)
            .message(message)
            .body(json.toResponseBody())
            .build()
    }
}

/**
 * Data class for refresh token request.
 */
data class RefreshTokenRequest(
    val refreshToken: String,
)

/**
 * Data class for token response.
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600,
)
