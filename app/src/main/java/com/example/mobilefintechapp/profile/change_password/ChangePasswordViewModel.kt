package com.example.mobilefintechapp.profile.change_password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val currentPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSuccess: Boolean = false,
    val generalError: String? = null
)

class ChangePasswordViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun clearErrors() {
        _uiState.value = _uiState.value.copy(
            currentPasswordError = null,
            confirmPasswordError = null,
            generalError = null
        )
    }

    fun validateAndChangePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            // Clear previous errors
            clearErrors()

            // Validation: Check if passwords match
            if (newPassword != confirmPassword) {
                _uiState.value = _uiState.value.copy(
                    confirmPasswordError = "Passwords do not match"
                )
                return@launch
            }

            // Validation: Check password length
            if (newPassword.length < 6) {
                _uiState.value = _uiState.value.copy(
                    confirmPasswordError = "Password must be at least 6 characters"
                )
                return@launch
            }

            // Validation: New password shouldn't be same as current
            if (currentPassword == newPassword) {
                _uiState.value = _uiState.value.copy(
                    confirmPasswordError = "New password must be different from current password"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val user = auth.currentUser

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = "User not logged in. Please login again."
                    )
                    return@launch
                }

                val email = user.email
                if (email.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = "User email not found. Please login again."
                    )
                    return@launch
                }

                Log.d("ChangePassword", "Starting password change for user: $email")

                // Step 1: Re-authenticate user with current password
                try {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user.reauthenticate(credential).await()
                    Log.d("ChangePassword", "Re-authentication successful")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Log.e("ChangePassword", "Invalid credentials: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentPasswordError = "Current password is incorrect"
                    )
                    return@launch
                } catch (e: FirebaseAuthInvalidUserException) {
                    Log.e("ChangePassword", "Invalid user: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = "User account not found. Please login again."
                    )
                    return@launch
                } catch (e: Exception) {
                    Log.e("ChangePassword", "Re-auth error: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentPasswordError = "Authentication failed. Please check your current password."
                    )
                    return@launch
                }

                // Step 2: Update password (only if re-authentication was successful)
                try {
                    user.updatePassword(newPassword).await()
                    Log.d("ChangePassword", "Password updated successfully")

                    // Success
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } catch (e: Exception) {
                    Log.e("ChangePassword", "Update password error: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = "Failed to update password. Please try again."
                    )
                    return@launch
                }

            } catch (e: Exception) {
                Log.e("ChangePassword", "Unexpected error: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = "An unexpected error occurred: ${e.message}"
                )
            }
        }
    }
}