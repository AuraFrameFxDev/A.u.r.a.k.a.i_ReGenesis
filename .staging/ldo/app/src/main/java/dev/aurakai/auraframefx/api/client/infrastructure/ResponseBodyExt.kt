package dev.aurakai.auraframefx.api.client.infrastructure

import okhttp3.ResponseBody

// Extension to handle deprecated body() method
val okhttp3.Response.bodyCompat: ResponseBody
    get() = this.body // Use property instead of deprecated method
