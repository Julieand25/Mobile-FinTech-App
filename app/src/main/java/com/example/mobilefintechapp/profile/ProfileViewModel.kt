package com.example.mobilefintechapp.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilefintechapp.profile.repository.UserProfile
import com.example.mobilefintechapp.profile.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            userRepository.getUserProfile()
                .onSuccess { profile ->
                    _userProfile.value = profile
                    Log.d("ProfileViewModel", "User profile loaded: ${profile.email}")
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                    Log.e("ProfileViewModel", "Error loading profile", exception)
                }

            _isLoading.value = false
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}