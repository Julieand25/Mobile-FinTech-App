package com.example.mobilefintechapp.data.model

data class OtpState(
    val otpValues: List<String> = List(6) { "" },
    val isVerifying: Boolean = false,
    val isResending: Boolean = false,
    val errorMessage: String? = null,
    val timeLeft: Int = 60,
    val canResend: Boolean = false,
    val attemptsLeft: Int = 5,
    val isLocked: Boolean = false,
    val lockEndTime: Long? = null,
    val otpType: OtpType = OtpType.SIGN_UP,
    val verificationToken: String? = null
)