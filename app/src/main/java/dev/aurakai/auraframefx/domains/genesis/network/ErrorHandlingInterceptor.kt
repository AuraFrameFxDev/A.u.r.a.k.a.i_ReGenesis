package dev.aurakai.auraframefx.domains.genesis.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that handles HTTP errors and converts them to exceptions if needed.
 */
class ErrorHandlingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (!response.isSuccessful) {
            // You can inspect response.code here and throw specific exceptions
            // or just let the caller handle the non-successful response.
            // For now, we just pass it through, but this class exists to satisfy the dependency.
        }

        return response
    }
}

