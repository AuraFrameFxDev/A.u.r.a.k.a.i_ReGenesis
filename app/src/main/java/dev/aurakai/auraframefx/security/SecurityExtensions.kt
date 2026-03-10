package dev.aurakai.auraframefx.security

/**
 * Extended Security Context with Request Validation
 */
fun SecurityContext.validateRequestOrThrow(requestType: String, requestData: String) {
    if (!validateRequest(requestType, requestData)) {
        throw SecurityException("Access denied for request type: $requestType")
    }
    // Additional validation logic here
}