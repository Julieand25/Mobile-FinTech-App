package com.example.mobilefintechapp.auth.register.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.data.model.UserRegistration
import com.example.mobilefintechapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val isRegistering: Boolean = false,
    val registrationError: String? = null,
    val userId: String? = null
)

class RegistrationViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    /**
     * Register a new user in Firebase
     */
    fun registerUser(userRegistration: UserRegistration, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, registrationError = null) }

            val result = authRepository.registerUser(userRegistration)

            if (result.isSuccess) {
                val userId = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        userId = userId,
                        registrationError = null
                    )
                }
                Log.d("RegistrationViewModel", "User registered successfully: $userId")
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Registration failed"
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        registrationError = error
                    )
                }
                Log.e("RegistrationViewModel", "Registration failed: $error")
            }
        }
    }

    /**
     * Clear registration error
     */
    fun clearError() {
        _uiState.update { it.copy(registrationError = null) }
    }

    /**
     * Reset state
     */
    fun resetState() {
        _uiState.update { RegistrationUiState() }
    }
}