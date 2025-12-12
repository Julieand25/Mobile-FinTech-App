// viewmodel/OtpViewModel.kt
package com.example.mobilefintechapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.data.model.OtpState
import com.example.mobilefintechapp.data.model.OtpType
import com.example.mobilefintechapp.data.model.OtpVerificationResult
import com.example.mobilefintechapp.data.repository.OtpRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtpViewModel(
    private val otpRepository: OtpRepository = OtpRepository()
) : ViewModel() {

    companion object {
        private const val TAG = "OtpViewModel"
    }

    private val _uiState = MutableStateFlow(OtpState())
    val uiState: StateFlow<OtpState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    /**
     * Initialize OTP by sending it to the user's email
     */
    fun initializeOtp(email: String, otpType: OtpType) {
        Log.d(TAG, "==========================================")
        Log.d(TAG, "initializeOtp() called")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "OTP Type: $otpType")
        Log.d(TAG, "==========================================")

        viewModelScope.launch {
            _uiState.update { it.copy(otpType = otpType, isResending = true) }

            Log.d(TAG, "Checking if email is locked...")

            // Check if email is locked
            val (isLocked, lockEndTime) = otpRepository.isEmailLocked(email)
            if (isLocked) {
                Log.e(TAG, "Email is locked until: $lockEndTime")
                _uiState.update {
                    it.copy(
                        isLocked = true,
                        lockEndTime = lockEndTime,
                        isResending = false,
                        errorMessage = "Too many attempts. Try again in 8 hours."
                    )
                }
                return@launch
            }

            Log.d(TAG, "Email not locked, proceeding to send OTP...")

            // Send OTP
            val result = otpRepository.sendOtp(email, otpType)

            if (result.isSuccess) {
                Log.d(TAG, "✅ OTP sent successfully!")
                _uiState.update {
                    it.copy(
                        isResending = false,
                        errorMessage = null,
                        canResend = false
                    )
                }
                startCountdown()
            } else {
                val exception = result.exceptionOrNull()
                Log.e(TAG, "❌ Failed to send OTP: ${exception?.message}", exception)
                _uiState.update {
                    it.copy(
                        isResending = false,
                        errorMessage = "Failed to send OTP: ${exception?.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    /**
     * Update OTP input values
     */
    fun updateOtpValues(newValues: List<String>) {
        _uiState.update { it.copy(otpValues = newValues, errorMessage = null) }
    }

    /**
     * Verify the entered OTP
     */
    fun verifyOtp(email: String, otpType: OtpType, onSuccess: () -> Unit) {
        Log.d(TAG, "verifyOtp() called for email: $email")

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, errorMessage = null) }

            val enteredOtp = _uiState.value.otpValues.joinToString("")
            Log.d(TAG, "Entered OTP: $enteredOtp")

            if (enteredOtp.length != 6) {
                Log.e(TAG, "Invalid OTP length: ${enteredOtp.length}")
                _uiState.update {
                    it.copy(
                        isVerifying = false,
                        errorMessage = "Please enter all 6 digits"
                    )
                }
                return@launch
            }

            val result = otpRepository.verifyOtp(email, enteredOtp, otpType)

            when (result) {
                is OtpVerificationResult.Success -> {
                    Log.d(TAG, "✅ OTP verified successfully!")
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            errorMessage = null
                        )
                    }

                    // Delete OTP after successful verification
                    otpRepository.deleteOtp(email)
                    onSuccess()
                }

                is OtpVerificationResult.InvalidOtp -> {
                    val attemptsLeft = otpRepository.getRemainingAttempts(email)
                    Log.e(TAG, "❌ Invalid OTP. Attempts left: $attemptsLeft")
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            errorMessage = "Invalid code. $attemptsLeft attempts remaining.",
                            attemptsLeft = attemptsLeft,
                            otpValues = List(6) { "" } // Clear input
                        )
                    }
                }

                is OtpVerificationResult.Expired -> {
                    Log.e(TAG, "❌ OTP expired")
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            errorMessage = "Code expired. Please request a new one.",
                            canResend = true,
                            otpValues = List(6) { "" }
                        )
                    }
                    countdownJob?.cancel()
                }

                is OtpVerificationResult.TooManyAttempts -> {
                    Log.e(TAG, "❌ Too many attempts")
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            errorMessage = "Too many attempts. Account locked for 8 hours.",
                            isLocked = true,
                            otpValues = List(6) { "" }
                        )
                    }
                    countdownJob?.cancel()
                }

                is OtpVerificationResult.Error -> {
                    Log.e(TAG, "❌ Verification error: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            errorMessage = result.message,
                            otpValues = List(6) { "" }
                        )
                    }
                }
            }
        }
    }

    /**
     * Resend OTP with email parameter
     */
    fun resendOtpWithEmail(email: String) {
        Log.d(TAG, "==========================================")
        Log.d(TAG, "resendOtpWithEmail() called")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "==========================================")

        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, errorMessage = null) }

            val otpType = _uiState.value.otpType
            Log.d(TAG, "OTP Type: $otpType")

            val result = otpRepository.sendOtp(email, otpType)

            if (result.isSuccess) {
                Log.d(TAG, "✅ OTP resent successfully!")
                _uiState.update {
                    it.copy(
                        isResending = false,
                        errorMessage = null,
                        canResend = false,
                        timeLeft = 60,
                        otpValues = List(6) { "" } // Clear previous input
                    )
                }
                startCountdown()
            } else {
                val exception = result.exceptionOrNull()
                Log.e(TAG, "❌ Failed to resend OTP: ${exception?.message}", exception)
                _uiState.update {
                    it.copy(
                        isResending = false,
                        errorMessage = "Failed to resend OTP: ${exception?.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    /**
     * Start countdown timer for resend button
     */
    private fun startCountdown() {
        Log.d(TAG, "Starting countdown timer...")
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            repeat(60) { second ->
                _uiState.update { it.copy(timeLeft = 60 - second) }
                delay(1000)
            }
            _uiState.update { it.copy(canResend = true, timeLeft = 0) }
            Log.d(TAG, "Countdown completed, can resend now")
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        Log.d(TAG, "ViewModel cleared")
    }
}