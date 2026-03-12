package dev.aurakai.auraframefx.domains.genesis

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    // Note: Hilt services should have a default constructor.
    // Dependencies should be field injected if needed.

    private val tag = "MyFirebaseMsgService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO: Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.tag(tag).d("From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Timber.tag(tag).d("Message data payload: %s", remoteMessage.data)
            // Handle data payload here
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Timber.tag(tag).d("Message Notification Body: ${it.body}")
            // Handle notification payload here
        }
    }

    override fun onNewToken(token: String) {
        Timber.tag(tag).d("Refreshed token: $token")
        // TODO: Implement this method to send token to your app server.
        // sendRegistrationToServer(token)
    }
}
