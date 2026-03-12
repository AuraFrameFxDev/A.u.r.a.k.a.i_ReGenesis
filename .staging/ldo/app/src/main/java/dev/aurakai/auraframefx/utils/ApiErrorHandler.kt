package dev.aurakai.auraframefx.utils

import android.content.Context
import dev.aurakai.auraframefx.R
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for handling API errors and providing user-friendly error messages.
 */
@Singleton
class ApiErrorHandler @Inject constructor(
    private val context: Context,
) {
    /**
     * Get a user-friendly error message from a throwable.
     *
     * @param throwable The throwable to handle.
     * @return A user-friendly error message.
     */
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> context.getString(R.string.error_bad_request)
                    401 -> context.getString(R.string.error_unauthorized)
                    403 -> context.getString(R.string.error_forbidden)
                    404 -> context.getString(R.string.error_not_found)
                    408 -> context.getString(R.string.error_timeout)
                    500 -> context.getString(R.string.error_server_error)
                    502 -> context.getString(R.string.error_bad_gateway)
                    503 -> context.getString(R.string.error_service_unavailable)
                    504 -> context.getString(R.string.error_gateway_timeout)
                    else -> context.getString(R.string.error_unknown_http, throwable.code())
                }
            }

            is ConnectException -> context.getString(R.string.error_connection_failed)
            is SocketTimeoutException -> context.getString(R.string.error_connection_timeout)
            is UnknownHostException -> context.getString(R.string.error_no_internet)
            is IOException -> context.getString(R.string.error_network_io)
            else -> throwable.message ?: context.getString(R.string.error_unknown)
        }
    }

    /**
     * Check if the error is a network error.
     *
     * @param throwable The throwable to check.
     * @return True if the error is a network error, false otherwise.
     */
    fun isNetworkError(throwable: Throwable): Boolean {
        return throwable is IOException ||
                throwable is SocketTimeoutException ||
                throwable is UnknownHostException
    }

    /**
     * Check if the error is an authentication error.
     *
     * @param throwable The throwable to check.
     * @return True if the error is an authentication error, false otherwise.
     */
    fun isAuthError(throwable: Throwable): Boolean {
        return (throwable is HttpException && throwable.code() == 401)
    }
}
