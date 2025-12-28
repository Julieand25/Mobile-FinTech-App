package com.example.mobilefintechapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BankLinkingViewModel : ViewModel() {

    private val TAG = "BankLinkingViewModel"

    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val functions = FirebaseFunctions.getInstance()

    // State flows
    private val _hasLinkedAccount = MutableStateFlow(false)
    val hasLinkedAccount: StateFlow<Boolean> = _hasLinkedAccount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _bankName = MutableStateFlow<String?>(null)
    val bankName: StateFlow<String?> = _bankName.asStateFlow()

    private val _accountMask = MutableStateFlow<String?>(null)
    val accountMask: StateFlow<String?> = _accountMask.asStateFlow()

    private val _showLinkScreen = MutableStateFlow(false)
    val showLinkScreen: StateFlow<Boolean> = _showLinkScreen.asStateFlow()

    private val _finverseConnectUrl = MutableStateFlow<String?>(null)
    val finverseConnectUrl: StateFlow<String?> = _finverseConnectUrl.asStateFlow()

    init {
        checkLinkStatus()
    }

    // ============================================
    // 1️⃣ CHECK IF USER HAS LINKED ACCOUNT
    // ============================================
    fun checkLinkStatus() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "📡 Checking link status...")

                val result = functions
                    .getHttpsCallable("checkFinverseStatus")
                    .call()
                    .await()

                val data = result.data as? Map<*, *>
                val hasLinked = data?.get("hasLinkedAccount") as? Boolean ?: false

                _hasLinkedAccount.value = hasLinked
                Log.d(TAG, "✅ Link status: $hasLinked")

                // If linked, fetch account details
                if (hasLinked) {
                    fetchAccountDetails()
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error checking status", e)
                _errorMessage.value = "Failed to check link status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============================================
    // 2️⃣ START BANK LINKING (Get Finverse URL)
    // ============================================
    fun startBankLinking() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "🔗 Starting bank linking...")

                val result = functions
                    .getHttpsCallable("getFinverseConnectUrl")
                    .call()
                    .await()

                val data = result.data as? Map<*, *>
                val connectUrl = data?.get("connectUrl") as? String

                if (connectUrl != null) {
                    _finverseConnectUrl.value = connectUrl
                    _showLinkScreen.value = true
                    Log.d(TAG, "✅ Connect URL received: $connectUrl")
                } else {
                    _errorMessage.value = "Failed to get connection URL"
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting connect URL", e)
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============================================
    // 3️⃣ FETCH ACCOUNT DETAILS
    // ============================================
    private fun fetchAccountDetails() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "📊 Fetching account details...")

                val result = functions
                    .getHttpsCallable("getFinverseAccounts")
                    .call()
                    .await()

                val data = result.data as? Map<*, *>
                val accounts = data?.get("accounts") as? List<*>

                if (accounts != null && accounts.isNotEmpty()) {
                    val firstAccount = accounts[0] as? Map<*, *>
                    _bankName.value = firstAccount?.get("institution_name") as? String ?: "TestBank"
                    _accountMask.value = firstAccount?.get("mask") as? String ?: "1234"

                    Log.d(TAG, "✅ Account details fetched: ${_bankName.value}")
                } else {
                    Log.d(TAG, "⚠️ No accounts found")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error fetching account details", e)
                // Don't show error to user, just log it
            }
        }
    }

    // ============================================
    // 4️⃣ SYNC TRANSACTIONS
    // ============================================
    fun syncTransactions() {
        viewModelScope.launch {
            try {
                _isSyncing.value = true
                Log.d(TAG, "🔄 Syncing transactions...")

                val result = functions
                    .getHttpsCallable("getFinverseTransactions")
                    .call()
                    .await()

                val data = result.data as? Map<*, *>
                val transactions = data?.get("transactions") as? List<*>
                val count = data?.get("count") as? Long ?: 0

                Log.d(TAG, "✅ Synced $count transactions")
                _successMessage.value = "Synced $count transactions successfully"

                // TODO: Save transactions to local database if needed

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error syncing transactions", e)
                _errorMessage.value = "Failed to sync: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // ============================================
    // 5️⃣ UNLINK ACCOUNT
    // ============================================
    fun unlinkAccount() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "🔓 Unlinking account...")

                functions
                    .getHttpsCallable("unlinkFinverseAccount")
                    .call()
                    .await()

                _hasLinkedAccount.value = false
                _bankName.value = null
                _accountMask.value = null
                _successMessage.value = "Account unlinked successfully"

                Log.d(TAG, "✅ Account unlinked")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error unlinking account", e)
                _errorMessage.value = "Failed to unlink: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    fun closeLinkScreen() {
        _showLinkScreen.value = false
        _finverseConnectUrl.value = null
        // Re-check status after closing (user may have completed linking)
        checkLinkStatus()
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}