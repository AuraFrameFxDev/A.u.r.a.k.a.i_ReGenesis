package dev.aurakai.auraframefx.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors the connectivity state and reachability of the cloud backend.
 * Uses low-level socket checks for accurate reachability determination.
 */
@Singleton
class CloudStatusMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val TAG = "CloudStatusMonitor"
    private val _isCloudReachable = MutableStateFlow(true) // Assume reachable initially
    val isCloudReachable: StateFlow<Boolean> = _isCloudReachable.asStateFlow()

    init {
        Log.d(TAG, "CloudStatusMonitor initialized.")
    }

    /**
     * Checks if the device has a validated internet connection via ConnectivityManager.
     */
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Performs a low-level reachability check by attempting to connect to a known DNS server.
     */
    suspend fun checkActualInternetReachability(): Boolean = withContext(Dispatchers.IO) {
        if (!isNetworkConnected()) {
            _isCloudReachable.update { false }
            Log.d(TAG, "No network connection. Cloud is determined unreachable.")
            return@withContext false
        }
        try {
            val host = "8.8.8.8"
            val port = 53
            val timeout = 1500
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeout)
                _isCloudReachable.update { true }
                Log.d(TAG, "Actual internet host $host:$port is reachable. Cloud is reachable.")
                return@withContext true
            }
        } catch (e: IOException) {
            _isCloudReachable.update { false }
            Log.w(
                TAG,
                "Actual internet host is unreachable. Cloud determined unreachable: ${e.message}"
            )
            return@withContext false
        } catch (e: Exception) {
            _isCloudReachable.update { false }
            Log.e(TAG, "Unexpected error during internet reachability check: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * Starts periodic monitoring of cloud status at the specified interval.
     */
    suspend fun startMonitoring(intervalMillis: Long = 30000) {
        Log.d(TAG, "Starting periodic cloud status monitoring every $intervalMillis ms.")
        checkActualInternetReachability()
        while (true) {
            delay(intervalMillis)
            checkActualInternetReachability()
        }
    }
}
