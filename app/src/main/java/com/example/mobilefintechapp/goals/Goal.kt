package com.example.mobilefintechapp.goals

data class Goal(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val lastContribution: String = "",
    val progress: Int = 0,
    val targetDate: String = "",
    val createdDate: String = "",
    val history: List<Transaction> = emptyList()
)

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "add" or "subtract"
    val note: String = "",
    val date: String = "",
    val timestamp: Long = 0L
)