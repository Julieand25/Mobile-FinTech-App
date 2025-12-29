package com.example.mobilefintechapp.profile.bank

data class Bank(
    val id: Int,
    val name: String,
    val fullName: String,
    val logoResource: String // Resource name as string (e.g., "maybank_logo")
)

// Available banks list
object BankRepository {
    val availableBanks = listOf(
        Bank(1, "Maybank", "Malayan Banking Berhad", "maybank_logo"),
        Bank(2, "RHB Bank", "RHB Bank Berhad", "rhb_logo"),
        Bank(3, "BSN", "Bank Simpanan Nasional", "bsn_logo"),
        Bank(4, "Bank Rakyat", "Bank Rakyat Malaysia Berhad", "bankrakyat_logo"),
        Bank(5, "CIMB Bank", "CIMB Bank Berhad", "cimb_logo"),
        Bank(6, "Public Bank", "Public Bank Berhad", "publicbank_logo"),
        Bank(7, "Hong Leong Bank", "Hong Leong Bank Berhad", "hongleongbank_logo"),
        Bank(8, "AmBank", "AmBank (M) Berhad", "ambank_logo"),
        Bank(9, "Affin Bank", "Affin Bank Berhad", "affinbank_logo"),
        Bank(10, "Alliance Bank", "Alliance Bank Malaysia Berhad", "alliance_logo")
    )
}

data class LinkedBankAccount(
    val bank: Bank,
    val accountMask: String, // Last 4 digits
    val isActive: Boolean = true
)

// Firestore model (for saving to Firebase)
data class LinkedBankFirestore(
    val bankId: Int = 0,
    val bankName: String = "",
    val bankFullName: String = "",
    val accountMask: String = "",
    val isActive: Boolean = true,
    val linkedAt: Long = System.currentTimeMillis()
) {
    // Convert to LinkedBankAccount for UI
    fun toLinkedBankAccount(): LinkedBankAccount {
        val bank = BankRepository.availableBanks.find { it.id == bankId }
            ?: Bank(bankId, bankName, bankFullName, "")
        return LinkedBankAccount(
            bank = bank,
            accountMask = accountMask,
            isActive = isActive
        )
    }
}

// Extension to convert LinkedBankAccount to Firestore model
fun LinkedBankAccount.toFirestore(): LinkedBankFirestore {
    return LinkedBankFirestore(
        bankId = bank.id,
        bankName = bank.name,
        bankFullName = bank.fullName,
        accountMask = accountMask,
        isActive = isActive,
        linkedAt = System.currentTimeMillis()
    )
}