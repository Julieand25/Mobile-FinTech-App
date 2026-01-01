package com.example.mobilefintechapp.profile.bank

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.profile.bank.Bank
import com.example.mobilefintechapp.profile.bank.LinkedBankAccount
import com.example.mobilefintechapp.profile.bank.BankAccountRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BankAccountViewModel : ViewModel() {
    private val repository = BankAccountRepository()

    companion object {
        private const val TAG = "BankAccountViewModel"
    }

    // State flows
    private val _linkedBankAccounts = MutableStateFlow<List<LinkedBankAccount>>(emptyList())
    val linkedBankAccounts: StateFlow<List<LinkedBankAccount>> = _linkedBankAccounts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        // Start listening to real-time updates
        observeLinkedBankAccounts()
    }

    /**
     * Observe linked bank accounts in real-time
     */
    private fun observeLinkedBankAccounts() {
        viewModelScope.launch {
            repository.getLinkedBankAccounts().collect { banks ->
                _linkedBankAccounts.value = banks
                Log.d(TAG, "📊 Bank accounts updated: ${banks.size} banks")
            }
        }
    }

    /**
     * Add a new bank account
     */
    fun addBankAccount(bank: Bank) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Generate random account mask (last 4 digits)
                val accountMask = (1000..9999).random().toString()

                val newAccount = LinkedBankAccount(
                    bank = bank,
                    accountMask = accountMask,
                    isActive = true
                )

                val result = repository.addBankAccount(newAccount)

                result.fold(
                    onSuccess = { docId ->
                        Log.d(TAG, "✅ Bank added successfully: $docId")
                        _successMessage.value = "${bank.name} linked successfully"
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Failed to add bank", exception)
                        _errorMessage.value = "Failed to link bank: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error adding bank", e)
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Remove a bank account
     */
    // In BankAccountViewModel.kt
    fun removeBankAccount(bankAccount: LinkedBankAccount) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = repository.removeBankAccount(bankAccount)

                result.fold(
                    onSuccess = {
                        Log.d(TAG, "✅ Bank removed successfully")
                        _successMessage.value = "${bankAccount.bank.name} removed successfully"

                        // ✅ WAIT FOR DELETE TO COMPLETE
                        delay(5000) // Wait 5 seconds before allowing new actions
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Failed to remove bank", exception)
                        _errorMessage.value = "Failed to remove bank: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error removing bank", e)
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh bank accounts (manual fetch)
     */
    fun refreshBankAccounts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = repository.fetchLinkedBankAccounts()

                result.fold(
                    onSuccess = { banks ->
                        _linkedBankAccounts.value = banks
                        Log.d(TAG, "✅ Banks refreshed: ${banks.size} banks")
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Failed to refresh banks", exception)
                        _errorMessage.value = "Failed to load banks: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error refreshing banks", e)
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear messages
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}

