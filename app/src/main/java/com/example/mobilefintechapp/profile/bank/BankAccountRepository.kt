package com.example.mobilefintechapp.profile.bank

import android.util.Log
import com.example.mobilefintechapp.profile.bank.LinkedBankAccount
import com.example.mobilefintechapp.profile.bank.LinkedBankFirestore
import com.example.mobilefintechapp.profile.bank.toFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BankAccountRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "BankAccountRepository"
        private const val USERS_COLLECTION = "users"
        private const val LINKED_BANKS_COLLECTION = "linked_banks"
    }

    /**
     * Get current user ID
     */
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Get reference to user's linked banks collection
     */
    private fun getUserBanksCollection() = getCurrentUserId()?.let { userId ->
        db.collection(USERS_COLLECTION)
            .document(userId)
            .collection(LINKED_BANKS_COLLECTION)
    }

    /**
     * Add a new bank account
     */
    suspend fun addBankAccount(bankAccount: LinkedBankAccount): Result<String> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            val banksCollection = getUserBanksCollection()
                ?: return Result.failure(Exception("Failed to access user banks"))

            // Convert to Firestore model
            val bankFirestore = bankAccount.toFirestore()

            // Add to Firestore
            val docRef = banksCollection.add(bankFirestore).await()

            Log.d(TAG, "✅ Bank account added successfully: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding bank account", e)
            Result.failure(e)
        }
    }

    /**
     * Remove a bank account
     */
    suspend fun removeBankAccount(bankAccount: LinkedBankAccount): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            val banksCollection = getUserBanksCollection()
                ?: return Result.failure(Exception("Failed to access user banks"))

            // Query to find the bank document
            val querySnapshot = banksCollection
                .whereEqualTo("bankId", bankAccount.bank.id)
                .whereEqualTo("accountMask", bankAccount.accountMask)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                return Result.failure(Exception("Bank account not found"))
            }

            // Delete the document
            val docId = querySnapshot.documents.first().id
            banksCollection.document(docId).delete().await()

            Log.d(TAG, "✅ Bank account removed successfully: $docId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error removing bank account", e)
            Result.failure(e)
        }
    }

    /**
     * Get all linked bank accounts (Real-time updates)
     */
    fun getLinkedBankAccounts(): Flow<List<LinkedBankAccount>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e(TAG, "❌ User not logged in")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val banksCollection = getUserBanksCollection()
        if (banksCollection == null) {
            Log.e(TAG, "❌ Failed to access user banks")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // Listen to real-time updates
        val listener = banksCollection
            .orderBy("linkedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "❌ Error listening to bank accounts", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val banks = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(LinkedBankFirestore::class.java)
                                ?.toLinkedBankAccount()
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error parsing bank document: ${doc.id}", e)
                            null
                        }
                    }
                    Log.d(TAG, "📊 Loaded ${banks.size} bank accounts")
                    trySend(banks)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get linked bank accounts (one-time fetch)
     */
    suspend fun fetchLinkedBankAccounts(): Result<List<LinkedBankAccount>> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            val banksCollection = getUserBanksCollection()
                ?: return Result.failure(Exception("Failed to access user banks"))

            val snapshot = banksCollection
                .orderBy("linkedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val banks = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(LinkedBankFirestore::class.java)
                        ?.toLinkedBankAccount()
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error parsing bank document: ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "✅ Fetched ${banks.size} bank accounts")
            Result.success(banks)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching bank accounts", e)
            Result.failure(e)
        }
    }
}