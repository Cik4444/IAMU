package com.example.boxingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxingapp.data.preferences.UserPreferenceRepository
import com.example.boxingapp.data.repository.UserRepository

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val prefs: UserPreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userRepository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
