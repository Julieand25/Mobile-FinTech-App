package com.example.mobilefintechapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mobilefintechapp.MainActivity
import com.example.mobilefintechapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "insight_alerts"
        private const val CHANNEL_NAME = "Insight Alerts"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "📱 New FCM Token: $token")

        // Save token to Firestore for this user
        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "📬 Message received from: ${message.from}")

        // Check if message contains data payload
        message.data.isNotEmpty().let {
            Log.d(TAG, "📦 Message data: ${message.data}")

            val title = message.data["title"] ?: "Spending Alert"
            val body = message.data["body"] ?: "Check your insights"
            val type = message.data["type"] ?: "WARNING"
            val category = message.data["category"] ?: ""

            // Show notification
            showNotification(title, body, type, category)
        }

        // Check if message contains notification payload
        message.notification?.let {
            Log.d(TAG, "📧 Notification Title: ${it.title}")
            Log.d(TAG, "📧 Notification Body: ${it.body}")

            showNotification(
                it.title ?: "Spending Alert",
                it.body ?: "Check your insights",
                "WARNING",
                ""
            )
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        type: String,
        category: String
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for spending alerts and insights"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open Insights screen when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "insights") // Tell MainActivity to open Insights
            putExtra("notification_type", type)
            putExtra("notification_category", category)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Choose icon and color based on alert type
        val (iconRes, color) = when (type) {
            "CRITICAL", "INCREASE" -> Pair(R.drawable.shield, 0xFFEF5350.toInt()) // Red
            "WARNING" -> Pair(R.drawable.shield, 0xFFFFA726.toInt()) // Orange
            else -> Pair(R.drawable.shield, 0xFF10B881.toInt()) // Green
        }

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setColor(color)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        Log.d(TAG, "✅ Notification displayed: $title")
    }

    private fun saveTokenToFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(TAG, "✅ FCM token saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error saving FCM token", e)
            }
    }
}