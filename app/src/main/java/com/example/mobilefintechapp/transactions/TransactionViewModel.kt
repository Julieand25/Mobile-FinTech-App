package com.example.mobilefintechapp.transactions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel : ViewModel() {
    private val repository = TransactionRepository()

    companion object {
        private const val TAG = "TransactionViewModel"
    }

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            repository.getTransactions().collect { transactions ->
                _allTransactions.value = transactions
                Log.d(TAG, "📊 Transactions updated: ${transactions.size} transactions")
            }
        }
    }

    /**
     * Filter transactions by time
     */
    fun getFilteredTransactions(
        timeFilter: TimeFilter,
        categoryFilter: CategoryFilter
    ): List<Transaction> {
        val now = Calendar.getInstance()
        val filteredByTime = _allTransactions.value.filter { transaction ->
            val transactionDate = Calendar.getInstance().apply {
                timeInMillis = transaction.timestamp
            }

            when (timeFilter) {
                TimeFilter.TODAY -> {
                    transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            transactionDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                }
                TimeFilter.THIS_WEEK -> {
                    transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            transactionDate.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
                }
                TimeFilter.THIS_MONTH -> {
                    transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            transactionDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                }
            }
        }

        return filteredByTime.filter { transaction ->
            when (categoryFilter) {
                CategoryFilter.ALL -> true
                CategoryFilter.HALAL -> transaction.status == TransactionStatus.HALAL
                CategoryFilter.UNKNOWN -> transaction.status == TransactionStatus.UNKNOWN
                CategoryFilter.HARAM -> transaction.status == TransactionStatus.HARAM
            }
        }
    }

    /**
     * Calculate total amount for filtered transactions
     */
    fun calculateTotalAmount(transactions: List<Transaction>): Double {
        return transactions.sumOf { it.amount }
    }

    /**
     * Get transaction counts by status
     */
    fun getTransactionCounts(transactions: List<Transaction>): Triple<Int, Int, Int> {
        val halalCount = transactions.count { it.status == TransactionStatus.HALAL }
        val unknownCount = transactions.count { it.status == TransactionStatus.UNKNOWN }
        val haramCount = transactions.count { it.status == TransactionStatus.HARAM }
        return Triple(halalCount, unknownCount, haramCount)
    }

    fun clearError() {
        _errorMessage.value = null
    }
}