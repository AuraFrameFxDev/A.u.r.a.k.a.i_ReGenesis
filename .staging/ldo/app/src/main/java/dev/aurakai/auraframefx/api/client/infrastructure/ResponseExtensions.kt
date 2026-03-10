package dev.aurakai.auraframefx.api.client.infrastructure

import okhttp3.Response

/**
 * OkHttp 5.x compatibility extensions
 * Replaces deprecated Response properties
 */

/** Check if response code is in informational range (100-199) */
val Response.isInformational: Boolean
    get() = code in 100..199

/** Check if response code is in client error range (400-499) */
val Response.isClientError: Boolean
    get() = code in 400..499

/** Check if response code is in server error range (500-599) */
val Response.isServerError: Boolean
    get() = code in 500..599
