package com.example.mobilefintechapp.data

import android.content.Context
import android.util.Log
import com.example.mobilefintechapp.transactions.Transaction
import com.example.mobilefintechapp.transactions.TransactionStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class BankRepository(context: Context) {

    private val apiClient = FinverseApiClient()
    private val tokenStorage = SecureTokenStorage(context)
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Check if user has linked bank account
    fun hasLinkedAccount(): Boolean {
        return tokenStorage.hasValidToken()
    }

    // Get linked bank info
    fun getLinkedBankInfo(): Pair<String?, String?> {
        return Pair(tokenStorage.getBankName(), tokenStorage.getAccountMask())
    }

    // Exchange auth code for token and save
    suspend fun linkBankAccount(authCode: String): Result<Unit> {
        return try {
            val tokenResult = apiClient.exchangeCodeForToken(authCode)

            if (tokenResult.isSuccess) {
                val tokenResponse = tokenResult.getOrNull()!!
                tokenStorage.saveAccessToken(tokenResponse.accessToken)
                tokenResponse.refreshToken?.let {
                    tokenStorage.saveRefreshToken(it)
                }

                // Fetch and save accounts
                fetchAndSaveAccounts()

                // Fetch and save transactions
                syncTransactions()

                Result.success(Unit)
            } else {
                Result.failure(tokenResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Log.e("BankRepo", "Failed to link account", e)
            Result.failure(e)
        }
    }

    // Fetch accounts from Finverse
    private suspend fun fetchAndSaveAccounts(): Result<List<FinverseAccount>> {
        val accessToken = tokenStorage.getAccessToken() ?: return Result.failure(Exception("No token"))

        return try {
            val accountsResult = apiClient.fetchAccounts(accessToken)

            if (accountsResult.isSuccess) {
                val accounts = accountsResult.getOrNull()!!

                if (accounts.isNotEmpty()) {
                    val firstAccount = accounts[0]
                    tokenStorage.saveAccountId(firstAccount.id)
                    tokenStorage.saveBankName(firstAccount.name)
                    tokenStorage.saveAccountMask(firstAccount.mask ?: "****")
                }

                Result.success(accounts)
            } else {
                Result.failure(accountsResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Log.e("BankRepo", "Failed to fetch accounts", e)
            Result.failure(e)
        }
    }

    // Sync transactions from Finverse to Firebase
    suspend fun syncTransactions(): Result<Int> {
        val accessToken = tokenStorage.getAccessToken() ?: return Result.failure(Exception("No token"))

        return try {
            val transactionsResult = apiClient.fetchTransactions(accessToken)

            if (transactionsResult.isSuccess) {
                val finverseTransactions = transactionsResult.getOrNull()!!

                // Convert to your app's transaction format with Halal/Haram classification
                val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No user"))
                var syncedCount = 0

                finverseTransactions.forEach { fTx ->
                    // Check if transaction already exists
                    val existingDocs = firestore.collection("transactions")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("finverseId", fTx.id)
                        .get()
                        .await()

                    if (existingDocs.isEmpty) {
                        // Transaction doesn't exist, add it
                        val status = classifyTransaction(fTx)
                        val category = fTx.category?.firstOrNull() ?: "Other"

                        firestore.collection("transactions")
                            .add(
                                mapOf(
                                    "userId" to userId,
                                    "merchantCode" to "FV_${fTx.id.take(8)}",
                                    "merchantName" to (fTx.merchantName ?: fTx.name),
                                    "category" to category,
                                    "amount" to Math.abs(fTx.amount),
                                    "timestamp" to parseDate(fTx.date),
                                    "status" to status.name,
                                    "finverseId" to fTx.id,
                                    "date" to formatDateForDisplay(fTx.date)
                                )
                            ).await()

                        syncedCount++
                    }
                }

                Log.d("BankRepo", "Synced $syncedCount new transactions")
                Result.success(syncedCount)
            } else {
                Result.failure(transactionsResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Log.e("BankRepo", "Failed to sync transactions", e)
            Result.failure(e)
        }
    }

    // Classify transaction as Halal/Haram based on category and merchant name
    private fun classifyTransaction(fTx: FinverseTransaction): TransactionStatus {
        val merchantName = (fTx.merchantName ?: fTx.name).lowercase()
        val categories = fTx.category?.map { it.lowercase() } ?: emptyList()

        // Check for Haram indicators
        when {
            categories.any { it.contains("alcohol") || it.contains("bar") || it.contains("liquor") } -> {
                return TransactionStatus.HARAM
            }
            merchantName.contains("bar") || merchantName.contains("liquor") ||
                    merchantName.contains("pub") || merchantName.contains("wine") -> {
                return TransactionStatus.HARAM
            }
            merchantName.contains("mcdonald") -> {
                return TransactionStatus.HARAM
            }
        }

        // Check for Halal indicators
        when {
            categories.any { it.contains("grocery") || it.contains("supermarket") } -> {
                return TransactionStatus.HALAL
            }
            merchantName.contains("mydin") || merchantName.contains("tesco") ||
                    merchantName.contains("7-eleven") || merchantName.contains("shell") ||
                    merchantName.contains("petronas") -> {
                return TransactionStatus.HALAL
            }
        }

        // Default to unknown if we can't determine
        return TransactionStatus.UNKNOWN
    }

    private fun parseDate(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun formatDateForDisplay(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    // Unlink bank account
    fun unlinkAccount() {
        tokenStorage.clearTokens()
    }
}