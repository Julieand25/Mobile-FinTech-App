package com.example.mobilefintechapp.profile.change_email

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VerifyPasswordViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isPasswordVerified = MutableStateFlow(false)
    val isPasswordVerified: StateFlow<Boolean> = _isPasswordVerified.asStateFlow()

    fun verifyPassword(password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val user = auth.currentUser
                if (user == null || user.email == null) {
                    _errorMessage.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }

                // Re-authenticate user with their email and password
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()

                // If we reach here, password is correct
                _isPasswordVerified.value = true
                Log.d("VerifyPasswordViewModel", "Password verified successfully")

            } catch (e: Exception) {
                Log.e("VerifyPasswordViewModel", "Password verification failed", e)
                _errorMessage.value = "Incorrect password. Please try again."
                _isPasswordVerified.value = false
            }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetVerification() {
        _isPasswordVerified.value = false
        _errorMessage.value = null
    }
}