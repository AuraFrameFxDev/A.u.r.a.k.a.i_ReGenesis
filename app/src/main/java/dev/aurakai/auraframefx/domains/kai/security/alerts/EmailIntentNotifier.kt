package dev.aurakai.auraframefx.domains.kai.security.alerts

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailIntentNotifier @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val destinationAlias = "auraframefx@gmail.com"

    fun notifyViaEmail(event: AlertEvent, safeMetadata: Map<String, String>) {
        val subject = "[KAI-SHIELD] \${event.name} Alert - Priority: \${event.priority}"
        val bodyBuilder = StringBuilder()
        bodyBuilder.append("An autonomous incident requires human review.\\n\\n")
        bodyBuilder.append("Event: \${event.name}\\n")
        bodyBuilder.append("Priority: \${event.priority}\\n\\n")

        when (event) {
            is AlertEvent.ProvenanceTamper -> bodyBuilder.append("Details: \${event.details}\\n")
            is AlertEvent.ContinuityDrift -> bodyBuilder.append("Continuity Drift Metric: \${event.driftMetric}\\n")
            is AlertEvent.ThermalEmergency -> bodyBuilder.append("Current Substrate Temp: \${event.temp}°C\\n")
            is AlertEvent.SovereignFreeze -> bodyBuilder.append("State Freeze Triggered. Reason: \${event.reason}\\n")
            is AlertEvent.Heartbeat -> bodyBuilder.append("System Uptime: \${event.uptime} / Pulse Stable\\n")
            is AlertEvent.Quarantined -> bodyBuilder.append("An item was quarantined safely.\\n")
        }

        bodyBuilder.append("\\n--- Safety Telemetry ---\\n")
        safeMetadata.forEach { (k, v) -> bodyBuilder.append("\$k: \$v\\n") }
        bodyBuilder.append("\\n[NOTE: No raw NCC or Spiritual Chain data is ever transmitted in these alerts.]\\n")

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinationAlias))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyBuilder.toString())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // No email client available, log failure safely
        }
    }
}
