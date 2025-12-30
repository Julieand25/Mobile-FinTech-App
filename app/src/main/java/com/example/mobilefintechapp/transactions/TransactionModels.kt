package com.example.mobilefintechapp.transactions

import com.google.firebase.Timestamp

// Updated Transaction data class
data class Transaction(
    val id: String = "",
    val merchantName: String = "",
    val merchantCategory: String = "",
    val mcc: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = 0L,
    val bankId: Int = 0,
    val bankName: String = "",
    val accountMask: String = "",
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
    val transactionType: String = "DEBIT" // DEBIT or CREDIT
) {
    // Format date for display
    fun getFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

enum class TransactionStatus {
    HALAL, UNKNOWN, HARAM
}

enum class TimeFilter {
    TODAY, THIS_WEEK, THIS_MONTH
}

enum class CategoryFilter {
    ALL, HALAL, UNKNOWN, HARAM
}

// Firestore model
data class TransactionFirestore(
    val merchantName: String = "",
    val merchantCategory: String = "",
    val mcc: String = "",
    val amount: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now(),
    val bankId: Int = 0,
    val bankName: String = "",
    val accountMask: String = "",
    val status: String = "UNKNOWN",
    val transactionType: String = "DEBIT"
) {
    fun toTransaction(id: String): Transaction {
        return Transaction(
            id = id,
            merchantName = merchantName,
            merchantCategory = merchantCategory,
            mcc = mcc,
            amount = amount,
            timestamp = timestamp.toDate().time,
            bankId = bankId,
            bankName = bankName,
            accountMask = accountMask,
            status = TransactionStatus.valueOf(status),
            transactionType = transactionType
        )
    }
}

fun Transaction.toFirestore(): TransactionFirestore {
    return TransactionFirestore(
        merchantName = merchantName,
        merchantCategory = merchantCategory,
        mcc = mcc,
        amount = amount,
        timestamp = Timestamp(java.util.Date(timestamp)),
        bankId = bankId,
        bankName = bankName,
        accountMask = accountMask,
        status = status.name,
        transactionType = transactionType
    )
}