package com.example.mobilefintechapp.transactions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TransactionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "TransactionRepository"
        private const val USERS_COLLECTION = "users"
        private const val TRANSACTIONS_COLLECTION = "transactions"
    }

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    private fun getUserTransactionsCollection() = getCurrentUserId()?.let { userId ->
        db.collection(USERS_COLLECTION)
            .document(userId)
            .collection(TRANSACTIONS_COLLECTION)
    }

    /**
     * Get all transactions in real-time
     */
    fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e(TAG, "❌ User not logged in")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val transactionsCollection = getUserTransactionsCollection()
        if (transactionsCollection == null) {
            Log.e(TAG, "❌ Failed to access transactions")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = transactionsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "❌ Error listening to transactions", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val transactions = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(TransactionFirestore::class.java)
                                ?.toTransaction(doc.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error parsing transaction: ${doc.id}", e)
                            null
                        }
                    }
                    Log.d(TAG, "📊 Loaded ${transactions.size} transactions")
                    trySend(transactions)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Delete all transactions for a specific bank
     */
    suspend fun deleteTransactionsForBank(bankId: Int): Result<Unit> {
        return try {
            val transactionsCollection = getUserTransactionsCollection()
                ?: return Result.failure(Exception("Failed to access transactions"))

            val snapshot = transactionsCollection
                .whereEqualTo("bankId", bankId)
                .get()
                .await()

            val batch = db.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            Log.d(TAG, "✅ Deleted ${snapshot.size()} transactions for bankId: $bankId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error deleting transactions", e)
            Result.failure(e)
        }
    }

    /**
     * Get transaction count for a specific bank
     */
    suspend fun getTransactionCountForBank(bankId: Int): Int {
        return try {
            val transactionsCollection = getUserTransactionsCollection()
                ?: return 0

            val snapshot = transactionsCollection
                .whereEqualTo("bankId", bankId)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting transaction count", e)
            0
        }
    }
}