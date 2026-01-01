// File: insights/NotificationRepository.kt
package com.example.mobilefintechapp.insights

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "NotificationRepository"
    }

    /**
     * Get insight notifications from Firestore (real-time)
     */
    fun getInsightNotifications(): Flow<List<InsightNotification>> = callbackFlow {
        val userId = auth.currentUser?.uid

        Log.d(TAG, "==========================================")
        Log.d(TAG, "📊 getInsightNotifications called")
        Log.d(TAG, "User ID: $userId")
        Log.d(TAG, "==========================================")

        if (userId == null) {
            Log.e(TAG, "❌ User not logged in")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val collectionPath = "users/$userId/insight_notifications"
        Log.d(TAG, "Listening to: $collectionPath")

        val listener = db.collection("users")
            .document(userId)
            .collection("insight_notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "❌ Error listening to notifications", error)
                    Log.e(TAG, "Error message: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "📊 Snapshot received")
                    Log.d(TAG, "Document count: ${snapshot.documents.size}")
                    Log.d(TAG, "Is empty: ${snapshot.isEmpty}")

                    snapshot.documents.forEach { doc ->
                        Log.d(TAG, "Document ID: ${doc.id}")
                        Log.d(TAG, "Document data: ${doc.data}")
                    }

                    val notifications = snapshot.documents.mapNotNull { doc ->
                        try {
                            val notification = InsightNotification(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                message = doc.getString("message") ?: "",
                                type = doc.getString("type") ?: "INFO",
                                category = doc.getString("category") ?: "",
                                amount = doc.getLong("amount")?.toInt() ?: 0,
                                timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L,
                                read = doc.getBoolean("read") ?: false,
                                bankId = doc.getLong("bankId")?.toInt() ?: 0
                            )
                            Log.d(TAG, "✅ Parsed notification: ${notification.title}")
                            notification
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error parsing notification: ${doc.id}", e)
                            null
                        }
                    }

                    Log.d(TAG, "✅ Loaded ${notifications.size} notifications from Firestore")
                    trySend(notifications)
                } else {
                    Log.d(TAG, "Snapshot is null")
                    trySend(emptyList())
                }
            }

        awaitClose {
            Log.d(TAG, "Listener removed")
            listener.remove()
        }
    }
}

// Data class for Firestore notifications
data class InsightNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: String,  // "CRITICAL", "WARNING", "INFO"
    val category: String,
    val amount: Int,
    val timestamp: Long,
    val read: Boolean,
    val bankId: Int
)