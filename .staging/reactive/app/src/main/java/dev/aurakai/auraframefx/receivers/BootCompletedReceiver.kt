package dev.aurakai.auraframefx.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {

    private val tag = "BootCompletedReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(tag, "Boot completed intent received.")
            // TODO: Implement what needs to happen on boot completed.
            // For example, start a service:
            // val serviceIntent = Intent(context, YourService::class.java)
            // context.startService(serviceIntent)
        }
    }
}
