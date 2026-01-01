package com.example.mobilefintechapp.insights

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.transactions.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InsightsViewModel : ViewModel() {
    private val transactionRepository = TransactionRepository()
    private val notificationRepository = NotificationRepository()  // ← NEW
    private val analysisEngine = InsightAnalysisEngine()

    private val _spendingAlerts = MutableStateFlow<List<SpendingAlert>>(emptyList())
    val spendingAlerts: StateFlow<List<SpendingAlert>> = _spendingAlerts.asStateFlow()

    private val _aiRecommendations = MutableStateFlow<List<AIRecommendation>>(emptyList())
    val aiRecommendations: StateFlow<List<AIRecommendation>> = _aiRecommendations.asStateFlow()

    // ✅ NEW: FCM Notifications from Firestore
    private val _fcmNotifications = MutableStateFlow<List<InsightNotification>>(emptyList())
    val fcmNotifications: StateFlow<List<InsightNotification>> = _fcmNotifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInsights()
        loadFCMNotifications()  // ← NEW
    }

    private fun loadInsights() {
        viewModelScope.launch {
            _isLoading.value = true

            transactionRepository.getTransactions().collect { transactions ->
                Log.d("InsightsViewModel", "📊 Analyzing ${transactions.size} transactions")

                // Generate AI insights locally from transactions
                val alerts = analysisEngine.generateSpendingAlerts(transactions)
                val recommendations = analysisEngine.generateRecommendations(transactions)

                _spendingAlerts.value = alerts
                _aiRecommendations.value = recommendations

                _isLoading.value = false

                Log.d("InsightsViewModel", "✅ Generated ${alerts.size} alerts and ${recommendations.size} recommendations")
            }
        }
    }

    // ✅ NEW: Load FCM notifications from Firestore
    private fun loadFCMNotifications() {
        viewModelScope.launch {
            notificationRepository.getInsightNotifications().collect { notifications ->
                _fcmNotifications.value = notifications
                Log.d("InsightsViewModel", "✅ Loaded ${notifications.size} FCM notifications from Firestore")
            }
        }
    }

    fun refreshInsights() {
        loadInsights()
        loadFCMNotifications()  // ← Also refresh FCM notifications
    }
}