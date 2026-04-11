
package dev.aurakai.auraframefx.system

import android.content.Context
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import rikka.shizuku.Shizuku

object ShizukuManager {

    private const val TAG = "ShizukuManager"
    const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001

    /**
     * Checks if Shizuku is available by pinging its binder.
     * @return True if Shizuku is running and responsive, false otherwise.
     */
    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
            Log.d(TAG, "Shizuku.pingBinder() returned true: Shizuku service is available.")
            true
        } catch (e: Throwable) {
            Log.e(
                TAG,
                "Shizuku.pingBinder() failed: Shizuku service not available or error occurred. " + e.message
            )
            false
        }
    }

    /**
     * Requests Shizuku permission from the user.
     * The result is delivered via the callback.
     */
    fun requestShizukuPermission(context: Context, callback: (granted: Boolean) -> Unit) {
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Shizuku permission already granted.")
            callback(true)
            return
        }

        Log.i(TAG, "Requesting Shizuku permission.")
        val listener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                val granted = grantResult == PackageManager.PERMISSION_GRANTED
                Log.i(TAG, "Shizuku permission request result: granted=$granted")
                callback(granted)
            }
        }
        Shizuku.addRequestPermissionResultListener(listener)
        Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
    }

    /**
     * Adds a listener to be notified when the Shizuku binder dies.
     */
    fun addShizukuBinderDeathListener(binder: IBinder, listener: IBinder.DeathRecipient) {
        try {
            binder.linkToDeath(listener, 0)
            Log.d(TAG, "Shizuku binder death listener added.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add Shizuku binder death listener: " + e.message)
        }
    }

    /**
     * Removes a listener from the Shizuku binder death notifications.
     */
    fun removeShizukuBinderDeathListener(binder: IBinder, listener: IBinder.DeathRecipient) {
        try {
            binder.unlinkToDeath(listener, 0)
            Log.d(TAG, "Shizuku binder death listener removed.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove Shizuku binder death listener: " + e.message)
        }
    }

    /**
     * Retrieves the Shizuku service binder.
     * @return The IBinder for the Shizuku service, or null if not available.
     */
    fun getShizukuServiceBinder(): IBinder? {
        return try {
            val binder = Shizuku.getBinder()
            if (binder != null) {
                Log.d(TAG, "Shizuku service binder obtained successfully.")
            } else {
                Log.w(TAG, "Shizuku.getBinder() returned null.")
            }
            binder
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting Shizuku service binder: " + e.message)
            null
        }
    }

    // Example of a death recipient
    private val shizukuDeathRecipient = IBinder.DeathRecipient {
        Log.w(TAG, "Shizuku binder died. Re-establishing connection or cleaning up.")
        // Handle binder death: e.g., re-request permission, re-bind to service
    }

    // You might want to call initialize() or similar methods from your application's onCreate
    // to set up Shizuku. Example:
    fun initializeShizukuIntegration(context: Context) {
        if (isShizukuAvailable()) {
            requestShizukuPermission(context) { granted ->
                if (granted) {
                    Log.i(TAG, "Shizuku permission granted. Can now proceed with operations.")
                    getShizukuServiceBinder()?.let { binder ->
                        addShizukuBinderDeathListener(binder, shizukuDeathRecipient)
                    }
                } else {
                    Log.w(TAG, "Shizuku permission denied.")
                }
            }
        } else {
            Log.e(
                TAG,
                "Shizuku is not available. Please ensure Shizuku app is installed and running."
            )
        }
    }
}
