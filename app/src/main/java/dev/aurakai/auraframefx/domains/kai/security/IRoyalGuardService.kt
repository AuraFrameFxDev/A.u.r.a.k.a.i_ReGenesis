package dev.aurakai.auraframefx.domains.kai.security

import android.os.IBinder
import android.os.IInterface

/**
 * Interface for Royal Guard Service
 */
interface IRoyalGuardService : IInterface {
    fun verifyProvenance(data: String): Boolean
    fun getSecurityStatus(): String
    fun authenticate(token: String): Boolean

    companion object {
        const val DESCRIPTOR = "dev.aurakai.auraframefx.domains.kai.security.IRoyalGuardService"
        const val TRANSACTION_verifyProvenance = 1
        const val TRANSACTION_getSecurityStatus = 2
        const val TRANSACTION_authenticate = 3
    }
}
