package com.example.mobilefintechapp.homepage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.goals.Goal
import com.example.mobilefintechapp.profile.repository.UserProfile
import com.example.mobilefintechapp.profile.repository.UserRepository
import com.example.mobilefintechapp.transactions.Transaction
import com.example.mobilefintechapp.transactions.TransactionRepository
import com.example.mobilefintechapp.transactions.TransactionStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomepageViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val transactionRepository = TransactionRepository()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile.asStateFlow()

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    private val _isLoadingTransactions = MutableStateFlow(false)
    val isLoadingTransactions: StateFlow<Boolean> = _isLoadingTransactions.asStateFlow()

    init {
        observeGoals()
        loadUserProfile()
        observeTransactions()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoadingProfile.value = true

            userRepository.getUserProfile()
                .onSuccess { profile ->
                    _userProfile.value = profile
                    Log.d("HomepageViewModel", "User profile loaded: ${profile.fullName}")
                }
                .onFailure { exception ->
                    Log.e("HomepageViewModel", "Error loading profile", exception)
                }

            _isLoadingProfile.value = false
        }
    }

    private fun observeGoals() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HomepageViewModel", "Error observing goals", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    _goals.value = it.documents.mapNotNull { doc ->
                        doc.toObject(Goal::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            _isLoadingTransactions.value = true
            transactionRepository.getTransactions().collect { transactions ->
                _allTransactions.value = transactions
                Log.d("HomepageViewModel", "📊 Transactions updated: ${transactions.size} transactions")
                _isLoadingTransactions.value = false
            }
        }
    }

    /**
     * Get total spent for a specific time period (TIME-BASED)
     */
    fun getTotalSpent(period: String): Double {
        val now = System.currentTimeMillis()

        val filteredTransactions = _allTransactions.value.filter { transaction ->
            val transactionTime = transaction.timestamp
            val timeDiff = now - transactionTime

            when (period) {
                "Day" -> {
                    // Last 24 hours
                    timeDiff <= 24 * 60 * 60 * 1000L
                }
                "Week" -> {
                    // Last 7 days
                    timeDiff <= 7 * 24 * 60 * 60 * 1000L
                }
                "Month" -> {
                    // Last 30 days
                    timeDiff <= 30 * 24 * 60 * 60 * 1000L
                }
                else -> false
            }
        }

        return filteredTransactions.sumOf { it.amount }
    }

    /**
     * Get transaction counts by status for a specific period (TIME-BASED)
     */
    fun getTransactionPercentages(period: String): Triple<Int, Int, Int> {
        val now = System.currentTimeMillis()

        val filteredTransactions = _allTransactions.value.filter { transaction ->
            val transactionTime = transaction.timestamp
            val timeDiff = now - transactionTime

            when (period) {
                "Day" -> {
                    // Last 24 hours
                    timeDiff <= 24 * 60 * 60 * 1000L
                }
                "Week" -> {
                    // Last 7 days
                    timeDiff <= 7 * 24 * 60 * 60 * 1000L
                }
                "Month" -> {
                    // Last 30 days
                    timeDiff <= 30 * 24 * 60 * 60 * 1000L
                }
                else -> false
            }
        }

        val totalCount = filteredTransactions.size
        if (totalCount == 0) return Triple(0, 0, 0)

        val halalCount = filteredTransactions.count { it.status == TransactionStatus.HALAL }
        val unknownCount = filteredTransactions.count { it.status == TransactionStatus.UNKNOWN }
        val haramCount = filteredTransactions.count { it.status == TransactionStatus.HARAM }

        val halalPercentage = (halalCount.toFloat() / totalCount * 100).toInt()
        val unknownPercentage = (unknownCount.toFloat() / totalCount * 100).toInt()
        val haramPercentage = (haramCount.toFloat() / totalCount * 100).toInt()

        return Triple(halalPercentage, unknownPercentage, haramPercentage)
    }

    /**
     * Get top 3 recent transactions
     */
    fun getRecentTransactions(): List<Transaction> {
        return _allTransactions.value
            .sortedByDescending { it.timestamp }
            .take(3)
    }
}