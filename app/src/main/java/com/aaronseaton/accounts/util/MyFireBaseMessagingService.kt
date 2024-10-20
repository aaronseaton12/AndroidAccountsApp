package com.aaronseaton.accounts.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.aaronseaton.accounts.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        remoteMessage.data.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            sendNotification(
                it["title"],
                it["body"],
                it["route"],
                applicationContext
            )
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(
                it.title,
                it.body,
                remoteMessage.data["route"],
                applicationContext
            )
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.

        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     * @param messageTitle FCM message title received
     *
     */
    private fun sendNotification(
        messageTitle: String?,
        messageBody: String?,
        route: String?,
        applicationContext: Context
    ) {
        val values = object {
            val NOTIFICATION_ID = 0
            val REQUEST_CODE = 0
            val FLAGS = 0
        }

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        val intent: Intent = Intent(Intent.ACTION_VIEW, route?.toUri())
        val pending: PendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(values.REQUEST_CODE, PendingIntent.FLAG_IMMUTABLE)
        }
//    val largeIcon = BitmapFactory.decodeResource(
//        applicationContext.resources,
//        R.drawable.receipt_icon_two
//    )
        val bigTextStyle = NotificationCompat.BigTextStyle()
        val smallIcon = R.drawable.receipt_icon_two
        // Build the notification
        val channelId = applicationContext.getString(R.string.default_notification_channel_id)
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(messageTitle ?: "TITLE BLANK")
            .setContentText(messageBody ?: "BODY BLANK")
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setStyle(bigTextStyle)
            //.setLargeIcon(largeIcon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(route, values.NOTIFICATION_ID, builder.build())
    }

    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}