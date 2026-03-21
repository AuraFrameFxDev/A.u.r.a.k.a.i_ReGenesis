package com.example.app.ipc

import android.os.IBinder
import android.os.IInterface

interface IAuraDriveService : IInterface {
    fun getOracleDriveStatus(): String
    fun toggleLSPosedModule(): Boolean
    fun getDetailedInternalStatus(): String
    fun getInternalDiagnosticsLog(): List<String>

    companion object {
        object Stub {
            fun asInterface(service: IBinder?): IAuraDriveService? {
                return if (service != null) {
                    object : IAuraDriveService {
                        override fun asBinder(): IBinder = service
                        override fun getOracleDriveStatus(): String = "Operational"
                        override fun toggleLSPosedModule(): Boolean = true
                        override fun getDetailedInternalStatus(): String = "All systems green."
                        override fun getInternalDiagnosticsLog(): List<String> = listOf("Starting...", "Ready.")
                    }
                } else null
            }
        }
    }
}
