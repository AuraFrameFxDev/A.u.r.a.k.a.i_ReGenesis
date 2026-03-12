package dev.aurakai.auraframefx.domains.genesis.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor that checks for network connectivity before executing a request.
 */
class ConnectivityInterceptor(
    private val connectivityManager: NetworkConnectivityManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityManager.isConnected()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }
}

class NoConnectivityException : IOException("No network connectivity")

